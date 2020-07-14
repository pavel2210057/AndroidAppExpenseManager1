package com.crazytrends.expensemanager.backupRestore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;


import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.baseClass.BaseActivityRecyclerBinding;
import com.crazytrends.expensemanager.appBase.models.toolbar.ToolbarModel;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.BackgroundAsync;
import com.crazytrends.expensemanager.appBase.utils.Constants;
import com.crazytrends.expensemanager.appBase.utils.OnAsyncBackground;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import com.crazytrends.expensemanager.appBase.utils.TwoButtonDialogListener;
import com.crazytrends.expensemanager.databinding.ActivityRestoreListBinding;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog.Builder;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;
import pub.devrel.easypermissions.EasyPermissions.RationaleCallbacks;

public class RestoreListAct extends BaseActivityRecyclerBinding implements PermissionCallbacks, RationaleCallbacks {
    private BackupRestore backupRestore;
    private ActivityRestoreListBinding binding;

    public boolean isDesc;

    public boolean isResultOK;

    public RestoreListModel model;
    private BackupRestoreProgress progressDialog;
    private ToolbarModel toolbarModel;


    public void callApi() {
    }


    public void initMethods() {
    }

    public void onRationaleAccepted(int i) {
    }

    public void onRationaleDenied(int i) {
    }


    public void setBinding() {
        this.binding = (ActivityRestoreListBinding) DataBindingUtil.setContentView(this, R.layout.act_restore_list);
        this.model = new RestoreListModel();
        this.model.setArrayList(new ArrayList());
        this.model.setNoDataIcon(R.drawable.no_data);
        this.model.setNoDataText(getString(R.string.noDataTitleBackup));
        this.model.setNoDataDetail(getString(R.string.noDataDescBackup));
        this.binding.setRestoreListModel(this.model);
        this.backupRestore = new BackupRestore(this);
        this.progressDialog = new BackupRestoreProgress(this);
    }


    public void setToolbar() {
        this.toolbarModel = new ToolbarModel();
        this.toolbarModel.setTitle(getString(R.string.local_backups));
        this.toolbarModel.setAdd(true);
        this.binding.includedToolbar.setToolbarModel(this.toolbarModel);
    }


    public void setOnClicks() {
        this.binding.includedToolbar.imgBack.setOnClickListener(this);
        this.binding.includedToolbar.imgAdd.setOnClickListener(this);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imgAdd) {
            this.isDesc = !this.isDesc;
            shortList();
        } else if (id == R.id.imgBack) {
            onBackPressed();
        }
    }


    public void fillData() {
        checkPermAndFill();
    }

    private void checkPermAndFill() {
        if (isHasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE")) {
            fillList();
            return;
        }
        requestPermissions(this, getString(R.string.rationale_save), Constants.REQUEST_PERM_FILE, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE");
    }

    private void fillList() {
        new BackgroundAsync(this, true, "", new OnAsyncBackground() {
            public void onPreExecute() {
            }

            public void doInBackground() {
                try {
                    File[] listFiles = new File(AppConstants.getLocalFileDir()).listFiles();
                    if (listFiles != null) {
                        for (int i = 0; i < listFiles.length; i++) {
                            if (FilenameUtils.getExtension(listFiles[i].getName()).equalsIgnoreCase("zip")) {
                                RestoreRowModel restoreRowModel = new RestoreRowModel();
                                restoreRowModel.setTitle(listFiles[i].getName());
                                restoreRowModel.setPath(listFiles[i].getAbsolutePath());
                                restoreRowModel.setDateModified(AppConstants.getFormattedDate(listFiles[i].lastModified(), (DateFormat) Constants.FILE_DATE_FORMAT));
                                long length = listFiles[i].length() / 1024;
                                StringBuilder sb = new StringBuilder();
                                sb.append(length);
                                sb.append("KB");
                                restoreRowModel.setSize(sb.toString());
                                RestoreListAct.this.model.getArrayList().add(restoreRowModel);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onPostExecute() {
                RestoreListAct.this.shortList();
            }
        }).execute(new Object[0]);
    }


    public void shortList() {
        this.binding.includedToolbar.imgAdd.setImageResource(this.isDesc ? R.drawable.sort_down : R.drawable.sort_up);
        Collections.sort(this.model.getArrayList(), new Comparator<RestoreRowModel>() {
            @SuppressLint({"NewApi"})
            public int compare(RestoreRowModel restoreRowModel, RestoreRowModel restoreRowModel2) {
                if (RestoreListAct.this.isDesc) {
                    return restoreRowModel.getDateModified().compareTo(restoreRowModel2.getDateModified());
                }
                return restoreRowModel2.getDateModified().compareTo(restoreRowModel.getDateModified());
            }
        });
        notifyAdapter();
    }


    public void setRecycler() {
        this.binding.recycler.setLayoutManager(new LinearLayoutManager(this.context));
        this.binding.recycler.setAdapter(new RestoreAdapter(this.context, this.model.getArrayList(), new RecyclerItemClick() {
            public void onClick(int i, int i2) {
                if (i2 == 2) {
                    RestoreListAct.this.deleteItem(i);
                } else {
                    RestoreListAct.this.restoreItem(i);
                }
            }
        }));
    }

    private void notifyAdapter() {
        setViewVisibility();
        if (this.binding.recycler.getAdapter() != null) {
            this.binding.recycler.getAdapter().notifyDataSetChanged();
        }
    }

    @SuppressLint("WrongConstant")
    private void setViewVisibility() {
        int i = 8;
        this.binding.linData.setVisibility(this.model.isListData() ? 0 : 8);
        LinearLayout linearLayout = this.binding.linNoData;
        if (!this.model.isListData()) {
            i = 0;
        }
        linearLayout.setVisibility(i);
    }

    public void deleteItem(final int i) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.delete_msg));
        sb.append("<br /> <b>This Backup</b> <br />");
        AppConstants.showTwoButtonDialog(this.context, getString(R.string.app_name), sb.toString(), true, true, getString(R.string.delete), getString(R.string.cancel), new TwoButtonDialogListener() {
            public void onCancel() {
            }

            public void onOk() {
                RestoreListAct.this.deleteFile(i);
            }
        });
    }


    public void deleteFile(int i) {
        File file = new File(((RestoreRowModel) this.model.getArrayList().get(i)).getPath());
        try {
            if (!file.exists()) {
                return;
            }
            if (file.delete()) {
                this.model.getArrayList().remove(i);
                this.binding.recycler.getAdapter().notifyItemRemoved(i);
                AppConstants.toastShort(this.context, "File delete");
                notifyAdapter();
                return;
            }
            AppConstants.toastShort(this.context, "Unable to delete");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restoreItem(final int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("<b>");
        sb.append(getString(R.string.restore_msg));
        sb.append("</b>");
        AppConstants.showRestoreDialog(this.context, getString(R.string.app_name), sb.toString(), true, true, getString(R.string.restore), getString(R.string.cancel), new TwoButtonDialogListener() {
            public void onOk() {
                RestoreListAct.this.isResultOK = true;
                RestoreListAct.this.backupData(((RestoreRowModel) RestoreListAct.this.model.getArrayList().get(i)).getPath(), false);
            }

            public void onCancel() {
                RestoreListAct.this.isResultOK = true;
                RestoreListAct.this.backupData(((RestoreRowModel) RestoreListAct.this.model.getArrayList().get(i)).getPath(), true);
            }
        });
    }


    public void backupData(String str, boolean z) {
        this.backupRestore.backupRestore(this.progressDialog, true, false, str, z, new OnBackupRestore() {
            public void getList(ArrayList<RestoreRowModel> arrayList) {
            }

            public void onSuccess(boolean z) {
                if (z) {
                    AppConstants.toastShort(RestoreListAct.this.context, RestoreListAct.this.context.getString(R.string.import_successfully));
                } else {
                    AppConstants.toastShort(RestoreListAct.this.context, RestoreListAct.this.context.getString(R.string.failed_to_import));
                }
            }
        });
    }

    private boolean isHasPermissions(Context context, String... strArr) {
        return EasyPermissions.hasPermissions(context, strArr);
    }

    private void requestPermissions(Context context, String str, int i, String... strArr) {
        EasyPermissions.requestPermissions((Activity) context, str, i, strArr);
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        EasyPermissions.onRequestPermissionsResult(i, strArr, iArr, this);
    }

    public void onPermissionsGranted(int i, @NonNull List<String> list) {
        if (i == 1053) {
            fillList();
        }
    }

    public void onPermissionsDenied(int i, @NonNull List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied((Activity) this, list)) {
            new Builder((Activity) this).build().show();
        }
    }

    public void onBackPressed() {
        if (this.isResultOK) {
            setResult(-1);
        }
        finish();
    }
}
