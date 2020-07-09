package com.crazytrends.expensemanager.backupRestore;

import android.app.Activity;
import android.app.Dialog;
import android.widget.TextView;
import com.crazytrends.expensemanager.R;
import java.lang.ref.WeakReference;

public class BackupRestoreProgress {
    Activity activity;
    boolean isShowing = false;
    private Dialog progressDialog = null;
    TextView textView;
    WeakReference<Activity> weakReference;

    public BackupRestoreProgress(Activity activity2) {
        this.progressDialog = new Dialog(activity2);
        this.activity = activity2;
        this.weakReference = new WeakReference<>(activity2);
        this.progressDialog.setContentView(R.layout.progress_dialog);
        this.textView = (TextView) this.progressDialog.findViewById(R.id.message);
        this.progressDialog.setCancelable(false);
    }

    public void showDialog() {
        if (this.progressDialog != null && !this.isShowing) {
            this.progressDialog.show();
            this.isShowing = true;
        }
    }

    public void dismissDialog() {
        if (this.progressDialog != null && this.progressDialog.isShowing() && this.weakReference.get() != null) {
            this.progressDialog.dismiss();
            this.isShowing = false;
        }
    }

    public void setMessage(String str) {
        this.textView.setText(str);
    }

    public boolean isShowing() {
        return this.isShowing;
    }
}
