package com.crazytrends.expensemanager.backupRestore;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import androidx.sqlite.db.SupportSQLiteDatabase;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.roomsDB.AppDataBase;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.Constants;
import java.io.File;
import java.util.ArrayList;

public class LocalBackupRestore {

    public Activity activity;

    public LocalBackupRestore(Activity activity2) {
        this.activity = activity2;
    }

    public void localBackUpAndRestore(BackupRestoreProgress backupRestoreProgress, boolean z, String str, boolean z2) {
        backupRestoreProgress.showDialog();
        AppConstants.deleteTempFile(this.activity);
        if (z) {
            startLocalBackUp(backupRestoreProgress);
        } else {
            startLocalRestore(backupRestoreProgress, str, z2);
        }
    }

    private void startLocalBackUp(final BackupRestoreProgress backupRestoreProgress) {
        BackupRestoreProgress backupRestoreProgress2 = backupRestoreProgress;
        ZipUnZipAsyncTask zipUnZipAsyncTask = new ZipUnZipAsyncTask(backupRestoreProgress2, this.activity, true, getAllFilesForBackup(AppConstants.getRootPath(this.activity)), "", AppConstants.getLocalZipFilePath(), new GetCompleteResponse() {
            public void getList(ArrayList<RestoreRowModel> arrayList) {
            }

            public void getResponse(boolean z) {
                backupRestoreProgress.dismissDialog();
                if (z) {
                    AppConstants.toastShort(LocalBackupRestore.this.activity, LocalBackupRestore.this.activity.getString(R.string.export_successfully));
                } else {
                    AppConstants.toastShort(LocalBackupRestore.this.activity, LocalBackupRestore.this.activity.getString(R.string.failed_to_export));
                }
            }
        });
        zipUnZipAsyncTask.execute(new Object[0]);
    }

    private ArrayList<File> getAllFilesForBackup(String str) {
        ArrayList<File> arrayList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("/databases");
        File[] listFiles = new File(sb.toString()).listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for (File add : listFiles) {
                arrayList.add(add);
            }
        }
        return arrayList;
    }

    private void startLocalRestore(final BackupRestoreProgress backupRestoreProgress, String str, final boolean z) {
        ZipUnZipAsyncTask zipUnZipAsyncTask = new ZipUnZipAsyncTask(backupRestoreProgress, this.activity, false, null, str, "", new GetCompleteResponse() {
            public void getList(ArrayList<RestoreRowModel> arrayList) {
            }

            public void getResponse(boolean z) {
                try {
                    LocalBackupRestore.this.localRestore(z);
                } catch (Exception e) {
                    backupRestoreProgress.dismissDialog();
                    e.printStackTrace();
                }
                backupRestoreProgress.dismissDialog();
                if (z) {
                    AppConstants.toastShort(LocalBackupRestore.this.activity, LocalBackupRestore.this.activity.getString(R.string.import_successfully));
                } else {
                    AppConstants.toastShort(LocalBackupRestore.this.activity, LocalBackupRestore.this.activity.getString(R.string.failed_to_import));
                }
            }
        });
        zipUnZipAsyncTask.execute(new Object[0]);
    }


    public void localRestore(boolean z) {
        SupportSQLiteDatabase writableDatabase = AppDataBase.getAppDatabase(this.activity).getOpenHelper().getWritableDatabase();
        if (!z) {
            deleteAllTableData(writableDatabase);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(AppConstants.getTempFileDir(this.activity));
        sb.append(File.separator);
        sb.append(Constants.APP_DB_NAME);
        writableDatabase.execSQL(String.format("ATTACH DATABASE '%s' AS encrypted;", new Object[]{sb.toString()}));
        try {
            Cursor query = writableDatabase.query("SELECT MAX(versionNumber) FROM encrypted.dbVersionList");
            if (query != null) {
                query.moveToFirst();
                query.getInt(0);
            }
            query.close();
        } catch (Exception e) {
            e.printStackTrace();
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
}
