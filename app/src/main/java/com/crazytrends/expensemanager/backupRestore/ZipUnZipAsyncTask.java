package com.crazytrends.expensemanager.backupRestore;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FilenameUtils;

public class ZipUnZipAsyncTask extends AsyncTask<Object, Object, Boolean> {
    Context context;
    BackupRestoreProgress dialog;
    ArrayList<File> fileListForZip;
    String fileToRestore;
    GetCompleteResponse getCompleteResponse;
    boolean isZip;
    String pass = stringSystemFile();
    String tempZipFilePath;
    WeakReference<Activity> weakReference;

    public static native String stringSystemFile();

    static {
        System.loadLibrary("native-lib");
    }

    public ZipUnZipAsyncTask(BackupRestoreProgress backupRestoreProgress, Activity activity, boolean z, ArrayList<File> arrayList, String str, String str2, GetCompleteResponse getCompleteResponse2) {
        this.weakReference = new WeakReference<>(activity);
        this.context = activity;
        this.isZip = z;
        this.dialog = backupRestoreProgress;
        this.tempZipFilePath = str2;
        this.fileToRestore = str;
        this.fileListForZip = arrayList;
        this.getCompleteResponse = getCompleteResponse2;
    }


    public void onPreExecute() {
        super.onPreExecute();
        this.dialog.showDialog();
    }


    public Boolean doInBackground(Object... objArr) {
        if (this.isZip) {
            return Boolean.valueOf(encryptedZip(this.fileListForZip, this.tempZipFilePath));
        }
        return Boolean.valueOf(decryptedZip(this.fileToRestore));
    }


    public void onPostExecute(Boolean bool) {
        super.onPostExecute(bool);
        this.getCompleteResponse.getResponse(bool.booleanValue());
    }

    public boolean encryptedZip(ArrayList<File> arrayList, String str) {
        try {
            ZipFile zipFile = new ZipFile(str);
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setCompressionMethod(8);
            zipParameters.setCompressionLevel(5);
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(99);
            zipParameters.setAesKeyStrength(3);
            zipParameters.setPassword(this.pass);
            zipFile.addFiles(arrayList, zipParameters);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean decryptedZip(String str) {
        String rootPath = AppConstants.getRootPath(this.context);
        try {
            ZipFile zipFile = new ZipFile(str);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(this.pass);
            }
            List fileHeaders = zipFile.getFileHeaders();
            String str2 = rootPath;
            boolean z = false;
            for (int i = 0; i < fileHeaders.size(); i++) {
                FileHeader fileHeader = (FileHeader) fileHeaders.get(i);
                if (FilenameUtils.getExtension(fileHeader.getFileName()).equalsIgnoreCase("db")) {
                    String tempFileDir = AppConstants.getTempFileDir(this.context);
                    str2 = tempFileDir;
                    z = dirChecker(tempFileDir);
                }
                if (z) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(str2);
                    sb.append(File.separator);
                    sb.append(fileHeader.getFileName());
                    new FileOutputStream(sb.toString());
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str2);
                    sb2.append(File.separator);
                    zipFile.extractFile(fileHeader, sb2.toString());
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private boolean dirChecker(String str) {
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.exists();
    }
}
