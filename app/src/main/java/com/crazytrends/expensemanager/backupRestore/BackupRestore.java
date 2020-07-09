package com.crazytrends.expensemanager.backupRestore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.roomsDB.AppDataBase;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.Constants;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class BackupRestore {
    String METADATA_FILE_PARENT = "appDataFolder";
    String METADATA_FILE_TYPE = "application/zip";
    private Activity activity;

    public Drive driveService;
    File file;
    FileList fileList = null;
    File fileMetadata;
    java.io.File filePath;
    boolean isSuccessCreate = true;
    boolean isSuccessDelete = true;
    FileContent mediaContent;
    OutputStream outputStream = null;

    public BackupRestore(Activity activity2) {
        this.activity = activity2;
    }

    public void backupRestore(BackupRestoreProgress backupRestoreProgress, boolean z, boolean z2, String str, boolean z3, OnBackupRestore onBackupRestore) {
        if (z) {
            localBackUpAndRestore(backupRestoreProgress, z2, str, z3, onBackupRestore);
        } else {
            driveBackupRestore(backupRestoreProgress, z2, str, z3, onBackupRestore);
        }
    }

    private void localBackUpAndRestore(BackupRestoreProgress backupRestoreProgress, boolean z, String str, boolean z2, OnBackupRestore onBackupRestore) {
        backupRestoreProgress.showDialog();
        AppConstants.deleteTempFile(this.activity);
        if (z) {
            startLocalBackUp(backupRestoreProgress, onBackupRestore);
        } else {
            startLocalRestore(backupRestoreProgress, str, z2, onBackupRestore);
        }
    }

    private void startLocalBackUp(BackupRestoreProgress backupRestoreProgress, OnBackupRestore onBackupRestore) {
        BackupRestoreProgress backupRestoreProgress2 = backupRestoreProgress;
        ZipUnZipAsync zipUnZipAsync = new ZipUnZipAsync(backupRestoreProgress2, this.activity, true, getAllFilesForBackup(AppConstants.getRootPath(this.activity)), "", AppConstants.getLocalZipFilePath(), onBackupRestore);
        zipUnZipAsync.execute(new Object[0]);
    }

    private ArrayList<java.io.File> getAllFilesForBackup(String str) {
        ArrayList<java.io.File> arrayList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("/databases");
        java.io.File[] listFiles = new java.io.File(sb.toString()).listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for (java.io.File add : listFiles) {
                arrayList.add(add);
            }
        }
        return arrayList;
    }


    public void startLocalRestore(BackupRestoreProgress backupRestoreProgress, String str, final boolean z, final OnBackupRestore onBackupRestore) {
        ZipUnZipAsync zipUnZipAsync = new ZipUnZipAsync(backupRestoreProgress, this.activity, false, null, str, "", new OnBackupRestore() {
            public void onSuccess(boolean z) {
                onBackupRestore.onSuccess(z);
                localRestore(z);
            }

            public void getList(ArrayList<RestoreRowModel> arrayList) {
                onBackupRestore.getList(arrayList);
            }
        });
        zipUnZipAsync.execute(new Object[0]);
    }


    public void localRestore(boolean z) {
        SupportSQLiteDatabase writableDatabase = AppDataBase.getAppDatabase(this.activity).getOpenHelper().getWritableDatabase();
        if (!z) {
            deleteAllTableData(writableDatabase);
        }
        String str = "ATTACH DATABASE '%s' AS encrypted;";
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(AppConstants.getTempFileDir(this.activity));
            sb.append(java.io.File.separator);
            sb.append(Constants.APP_DB_NAME);
            writableDatabase.execSQL(String.format(str, new Object[]{sb.toString()}));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            Cursor query = writableDatabase.query("SELECT MAX(versionNumber) FROM encrypted.dbVersionList");
            if (query != null && query.moveToFirst()) {
                query.getInt(0);
            }
            query.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        replaceAllTableData(z, writableDatabase);
        writableDatabase.execSQL("DETACH DATABASE encrypted");
    }

    private void deleteAllTableData(SupportSQLiteDatabase supportSQLiteDatabase) {
        supportSQLiteDatabase.execSQL("DELETE FROM categoryList");
        supportSQLiteDatabase.execSQL("DELETE FROM modeList");
        supportSQLiteDatabase.execSQL("DELETE FROM transactionList");
        supportSQLiteDatabase.execSQL("DELETE FROM typeList");
    }

    private void replaceAllTableData(boolean z, SupportSQLiteDatabase supportSQLiteDatabase) {
        replaceAll(supportSQLiteDatabase, z, "categoryList");
        replaceAll(supportSQLiteDatabase, z, "modeList");
        replaceAll(supportSQLiteDatabase, z, "transactionList");
        replaceAll(supportSQLiteDatabase, z, "typeList");
    }

    private void replaceAll(SupportSQLiteDatabase supportSQLiteDatabase, boolean z, String str) {
        if (z) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("insert into ");
                sb.append(str);
                sb.append(" select b.* from encrypted.");
                sb.append(str);
                sb.append(" b  left join ");
                sb.append(str);
                sb.append(" c on c.id=b.id  where c.id is null ");
                supportSQLiteDatabase.execSQL(sb.toString());
            } catch (SQLException e) {
                e.printStackTrace();
                StringBuilder sb2 = new StringBuilder();
                sb2.append("replaceAll: ");
                sb2.append(e.toString());
                Log.e("replaceAll", sb2.toString());
            }
        } else {
            try {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("insert into ");
                sb3.append(str);
                sb3.append(" select * from encrypted.");
                sb3.append(str);
                supportSQLiteDatabase.execSQL(sb3.toString());
            } catch (SQLException e2) {
                e2.printStackTrace();
                StringBuilder sb4 = new StringBuilder();
                sb4.append("replaceAll: ");
                sb4.append(e2.toString());
                Log.e("replaceAll", sb4.toString());
            }
        }
    }

    private void driveBackupRestore(BackupRestoreProgress backupRestoreProgress, boolean z, String str, boolean z2, OnBackupRestore onBackupRestore) {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this.activity);
        if (lastSignedInAccount == null) {
            Toast.makeText(this.activity, "Backup in Google drive might take 24 hours to get proceed for initial users.", Toast.LENGTH_LONG).show();
            signIn();
            return;
        }
        setCredentials(lastSignedInAccount);
        startDriveOperation(z, str, z2, backupRestoreProgress, onBackupRestore);
    }

    public void driveBackupList(BackupRestoreProgress backupRestoreProgress, OnBackupRestore onBackupRestore) {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this.activity);
        if (lastSignedInAccount == null) {
            signIn();
            return;
        }
        setCredentials(lastSignedInAccount);
        listFilesFromAppFolder(backupRestoreProgress, onBackupRestore);
    }

    private void listFilesFromAppFolder(final BackupRestoreProgress backupRestoreProgress, final OnBackupRestore onBackupRestore) {
        new AsyncTask<Void, Void, Void>() {

            public void onPreExecute() {
                backupRestoreProgress.setMessage("Fetching backups...");
                backupRestoreProgress.showDialog();
                super.onPreExecute();
            }


            public Void doInBackground(Void... voidArr) {
                try {
                    BackupRestore backupRestore = BackupRestore.this;
                    List list = driveService.files().list();
                    StringBuilder sb = new StringBuilder();
                    sb.append("mimeType ='");
                    sb.append(METADATA_FILE_TYPE);
                    sb.append("'");
                    backupRestore.fileList = (FileList) list.setQ(sb.toString()).setSpaces(METADATA_FILE_PARENT).setFields("files(id, name,size,createdTime,modifiedTime)").execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }


            public void onPostExecute(Void voidR) {
                backupRestoreProgress.dismissDialog();
                onBackupRestore.onSuccess(true);
                onBackupRestore.getList(getBackupList());
                super.onPostExecute(voidR);
            }
        }.execute(new Void[0]);
    }


    public ArrayList<RestoreRowModel> getBackupList() {
        ArrayList<RestoreRowModel> arrayList = new ArrayList<>();
        for (int i = 0; i < this.fileList.getFiles().size(); i++) {
            File file2 = (File) this.fileList.getFiles().get(i);
            long longValue = file2.getSize().longValue() / 1024;
            String name = file2.getName();
            String id = file2.getId();
            String formattedDate = AppConstants.getFormattedDate(file2.getModifiedTime().getValue(), (DateFormat) Constants.FILE_DATE_FORMAT);
            StringBuilder sb = new StringBuilder();
            sb.append(longValue);
            sb.append("KB");
            arrayList.add(new RestoreRowModel(name, id, formattedDate, sb.toString()));
        }
        return arrayList;
    }

    private void signIn() {
        GoogleSignInClient googleSignInClient = buildGoogleSignInClient();
        Intent intent = googleSignInClient.getSignInIntent();
        this.activity.startActivityForResult(intent , Constants.REQUEST_CODE_SIGN_IN);
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        return GoogleSignIn.getClient(this.activity,
                new Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(
                                new Scope("https://www.googleapis.com/auth/drive.appdata"),
                                new Scope[0]
                        )
                        .requestEmail()
                        .requestProfile()
                        .build()
        );
    }


    public void startDriveOperation(boolean z, String str, boolean z2, BackupRestoreProgress backupRestoreProgress, OnBackupRestore onBackupRestore) {
        AppConstants.deleteTempFile(this.activity);
        if (z) {
            startDriveBackup(str, backupRestoreProgress, onBackupRestore);
            return;
        }
        downloadRestore(backupRestoreProgress, str, AppConstants.getRemoteZipFilePath(this.activity), z2, onBackupRestore);
    }

    public void signOut(final ProgressDialog progressDialog) {
        buildGoogleSignInClient().signOut().addOnCompleteListener(this.activity, (OnCompleteListener) new OnCompleteListener<Void>() {
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
            }
        });
    }

    public void handleSignInResult(Intent intent, boolean z, boolean z2, String str, BackupRestoreProgress backupRestoreProgress, OnBackupRestore onBackupRestore) {
        try {
            Task signedInAccountFromIntent = GoogleSignIn.getSignedInAccountFromIntent(intent);
            final boolean z3 = z2;
            final boolean z4 = z;
            final String str2 = str;
            final BackupRestoreProgress backupRestoreProgress2 = backupRestoreProgress;
            final OnBackupRestore onBackupRestore2 = onBackupRestore;
            OnSuccessListener<GoogleSignInAccount> r0 = new OnSuccessListener<GoogleSignInAccount>() {
                public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Signed in as ");
                    sb.append(googleSignInAccount.getEmail());
                    Log.d("handleSignInResult", sb.toString());
                    setCredentials(googleSignInAccount);
                    if (!z3) {
                        startDriveOperation(z4, str2, false, backupRestoreProgress2, onBackupRestore2);
                    } else {
                        driveBackupList(backupRestoreProgress2, onBackupRestore2);
                    }
                }
            };
            signedInAccountFromIntent.addOnSuccessListener(r0).addOnFailureListener(new OnFailureListener() {
                public void onFailure(@NonNull Exception exc) {
                    Log.e("handleSignInResult", exc.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setCredentials(GoogleSignInAccount googleSignInAccount) {
        GoogleAccountCredential usingOAuth2 = GoogleAccountCredential.usingOAuth2(this.activity, Collections.singleton("https://www.googleapis.com/auth/drive.appdata"));
        usingOAuth2.setSelectedAccount(googleSignInAccount.getAccount());
        this.driveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                usingOAuth2)
                .setApplicationName(activity.getString(R.string.app_name))
                .build();
    }

    private void startDriveBackup(String str, final BackupRestoreProgress backupRestoreProgress, final OnBackupRestore onBackupRestore) {
        backupRestoreProgress.showDialog();
        if (str == null) {
            final String remoteZipFilePath = AppConstants.getRemoteZipFilePath(this.activity);
            ZipUnZipAsync zipUnZipAsync = new ZipUnZipAsync(backupRestoreProgress, this.activity, true, getAllFilesForBackup(AppConstants.getRootPath(this.activity)), "", remoteZipFilePath, new OnBackupRestore() {
                public void getList(ArrayList<RestoreRowModel> arrayList) {
                }

                public void onSuccess(boolean z) {
                    createFileInAppFolder(backupRestoreProgress, remoteZipFilePath, driveService, onBackupRestore);
                }
            });
            zipUnZipAsync.execute(new Object[0]);
            return;
        }
        createFileInAppFolder(backupRestoreProgress, str, this.driveService, onBackupRestore);
    }


    public void createFileInAppFolder(BackupRestoreProgress backupRestoreProgress, String str, Drive drive, OnBackupRestore onBackupRestore) {
        final BackupRestoreProgress backupRestoreProgress2 = backupRestoreProgress;
        final String str2 = str;
        final Drive drive2 = drive;
        final OnBackupRestore onBackupRestore2 = onBackupRestore;
        AsyncTask<Void, Void, Void> r0 = new AsyncTask<Void, Void, Void>() {

            public void onPreExecute() {
                backupRestoreProgress2.setMessage("Uploading to drive...");
                backupRestoreProgress2.showDialog();
                fileMetadata = new File();
                fileMetadata.setName(AppConstants.getBackupName());
                fileMetadata.setParents(Collections.singletonList(METADATA_FILE_PARENT));
                filePath = new java.io.File(str2);
                mediaContent = new FileContent(METADATA_FILE_TYPE, filePath);
                super.onPreExecute();
            }


            public Void doInBackground(Void... voidArr) {
                try {
                    file = (File) drive2.files().create(fileMetadata, mediaContent).setFields("id").execute();
                } catch (IOException e) {
                    isSuccessCreate = false;
                    e.printStackTrace();
                }
                return null;
            }


            public void onPostExecute(Void voidR) {
                backupRestoreProgress2.dismissDialog();
                onBackupRestore2.onSuccess(isSuccessCreate);
                super.onPostExecute(voidR);
            }
        };
        r0.execute(new Void[0]);
    }

    private void downloadRestore(BackupRestoreProgress backupRestoreProgress, String str, String str2, boolean z, OnBackupRestore onBackupRestore) {
        final BackupRestoreProgress backupRestoreProgress2 = backupRestoreProgress;
        final String str3 = str2;
        final String str4 = str;
        final boolean z2 = z;
        final OnBackupRestore onBackupRestore2 = onBackupRestore;
        AsyncTask<Void, Void, Void> r0 = new AsyncTask<Void, Void, Void>() {

            public void onPreExecute() {
                backupRestoreProgress2.showDialog();
                super.onPreExecute();
            }


            public Void doInBackground(Void... voidArr) {
                try {
                    outputStream = new FileOutputStream(str3);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    driveService.files().get(str4).executeMediaAndDownloadTo(outputStream);
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                return null;
            }


            public void onPostExecute(Void voidR) {
                backupRestoreProgress2.dismissDialog();
                if (new java.io.File(str3).exists()) {
                    startLocalRestore(backupRestoreProgress2, str3, z2, onBackupRestore2);
                }
                super.onPostExecute(voidR);
            }
        };
        r0.execute(new Void[0]);
    }

    public void deleteFromDrive(final BackupRestoreProgress backupRestoreProgress, final String str, final OnBackupRestore onBackupRestore) {
        new AsyncTask<Void, Void, Void>() {

            public void onPreExecute() {
                backupRestoreProgress.setMessage("Deleting from Drive...");
                backupRestoreProgress.showDialog();
                super.onPreExecute();
            }


            public Void doInBackground(Void... voidArr) {
                try {
                    driveService.files().delete(str).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    isSuccessDelete = false;
                }
                return null;
            }


            public void onPostExecute(Void voidR) {
                backupRestoreProgress.dismissDialog();
                onBackupRestore.onSuccess(isSuccessDelete);
                super.onPostExecute(voidR);
            }
        }.execute(new Void[0]);
    }
}
