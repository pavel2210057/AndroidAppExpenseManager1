package com.crazytrends.expensemanager.backupRestore;

import android.annotation.SuppressLint;
import android.content.Intent;


import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.baseClass.BaseActivityRecyclerBinding;
import com.crazytrends.expensemanager.appBase.models.toolbar.ToolbarModel;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import com.crazytrends.expensemanager.appBase.utils.TwoButtonDialogListener;
import com.crazytrends.expensemanager.databinding.ActivityRestoreListBinding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RestoreDriveListActivity extends BaseActivityRecyclerBinding {
    private BackupRestore backupRestore;

    public ActivityRestoreListBinding binding;

    public boolean isDesc;

    public boolean isResultOK;

    public RestoreListModel model;
    private BackupRestoreProgress progressDialog;
    private ToolbarModel toolbarModel;


    public void callApi() {
    }


    public void setBinding() {
        binding = (ActivityRestoreListBinding) DataBindingUtil.setContentView(this, R.layout.activity_restore_list);
        model = new RestoreListModel();
        model.setArrayList(new ArrayList());
        model.setNoDataIcon(R.drawable.no_data);
        model.setNoDataText(getString(R.string.noDataTitleBackup));
        model.setNoDataDetail(getString(R.string.noDataDescBackup));
        binding.setRestoreListModel(model);
        backupRestore = new BackupRestore(this);
        progressDialog = new BackupRestoreProgress(this);
    }


    public void setToolbar() {
        toolbarModel = new ToolbarModel();
        toolbarModel.setTitle(getString(R.string.drive_backups));
        binding.includedToolbar.setToolbarModel(toolbarModel);
    }


    public void setOnClicks() {
        binding.includedToolbar.imgBack.setOnClickListener(this);
        binding.includedToolbar.imgAdd.setOnClickListener(this);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imgAdd) {
            isDesc = !isDesc;
            shortList();
        } else if (id == R.id.imgBack) {
            onBackPressed();
        }
    }


    public void fillData() {
        getDriveBackupList();
    }

    private void getDriveBackupList() {
        backupRestore.driveBackupList(progressDialog, new OnBackupRestore() {
            public void onSuccess(boolean z) {
            }

            public void getList(ArrayList<RestoreRowModel> arrayList) {
                model.getArrayList().addAll(arrayList);
                notifyAdapter();
            }
        });
    }

    private void shortList() {
        binding.includedToolbar.imgAdd.setImageResource(isDesc ? R.drawable.sort_down : R.drawable.sort_up);
        Collections.sort(model.getArrayList(), new Comparator<RestoreRowModel>() {
            @SuppressLint({"NewApi"})
            public int compare(RestoreRowModel restoreRowModel, RestoreRowModel restoreRowModel2) {
                if (isDesc) {
                    return restoreRowModel.getDateModified().compareTo(restoreRowModel2.getDateModified());
                }
                return restoreRowModel2.getDateModified().compareTo(restoreRowModel.getDateModified());
            }
        });
        notifyAdapter();
    }


    public void setRecycler() {
        binding.recycler.setLayoutManager(new LinearLayoutManager(context));
        binding.recycler.setAdapter(new RestoreAdapter(context, model.getArrayList(), new RecyclerItemClick() {
            public void onClick(int i, int i2) {
                if (i2 == 2) {
                    deleteItem(i);
                } else {
                    restoreItem(i);
                }
            }
        }));
    }


    public void notifyAdapter() {
        setViewVisibility();
        if (binding.recycler.getAdapter() != null) {
            binding.recycler.getAdapter().notifyDataSetChanged();
        }
    }

    private void setViewVisibility() {
        int i = 8;
        binding.linData.setVisibility(model.isListData() ? View.VISIBLE : View.GONE);
        LinearLayout linearLayout = binding.linNoData;
        if (!model.isListData()) {
            i = 0;
        }
        linearLayout.setVisibility(i);
    }


    public void initMethods() {
        binding.txtPath.setVisibility(View.GONE);
    }

    public void deleteItem(final int i) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.delete_msg));
        sb.append("<br /> <b>This Backup</b> <br />");
        AppConstants.showTwoButtonDialog(context, getString(R.string.app_name), sb.toString(), true, true, getString(R.string.delete), getString(R.string.cancel), new TwoButtonDialogListener() {
            public void onCancel() {
            }

            public void onOk() {
                deleteFile(i);
            }
        });
    }


    public void deleteFile(final int i) {
        backupRestore.deleteFromDrive(progressDialog, ((RestoreRowModel) model.getArrayList().get(i)).getPath(), new OnBackupRestore() {
            public void getList(ArrayList<RestoreRowModel> arrayList) {
            }

            public void onSuccess(boolean z) {
                if (z) {
                    model.getArrayList().remove(i);
                    binding.recycler.getAdapter().notifyItemRemoved(i);
                    AppConstants.toastShort(context, "File delete");
                    notifyAdapter();
                    return;
                }
                AppConstants.toastShort(context, "Unable to delete");
            }
        });
    }

    public void restoreItem(final int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("<b>");
        sb.append(getString(R.string.restore_msg));
        sb.append("</b>");
        AppConstants.showRestoreDialog(context, getString(R.string.app_name), sb.toString(), true, true, getString(R.string.restore), getString(R.string.cancel), new TwoButtonDialogListener() {
            public void onOk() {
                isResultOK = true;
                backupData(((RestoreRowModel) model.getArrayList().get(i)).getPath(), false);
            }

            public void onCancel() {
                isResultOK = true;
                backupData(((RestoreRowModel) model.getArrayList().get(i)).getPath(), true);
            }
        });
    }


    public void backupData(String str, boolean z) {
        backupRestore.backupRestore(progressDialog, false, false, str, z, new OnBackupRestore() {
            public void getList(ArrayList<RestoreRowModel> arrayList) {
            }

            public void onSuccess(boolean z) {
                if (z) {
                    AppConstants.toastShort(context, context.getString(R.string.import_successfully));
                } else {
                    AppConstants.toastShort(context, context.getString(R.string.failed_to_import));
                }
            }
        });
    }


    public void onActivityResult(int i, int i2, @Nullable Intent intent) {
        Log.e("lfgjgfj",";fgh;llgj");
        super.onActivityResult(i, i2, intent);
        Log.e("lfgjgfj",";fgh;llgj");
        if (i2 == -1 && i == 1005) {
            handleSignIn(intent);
            Log.e("lfgjgfj",";fgh;llgj");
        }
    }

    private void handleSignIn(Intent intent) {
        backupRestore.handleSignInResult(intent, true, true, null, progressDialog, new OnBackupRestore() {
            public void onSuccess(boolean z) {
            }

            public void getList(ArrayList<RestoreRowModel> arrayList) {
                model.getArrayList().addAll(arrayList);
                notifyAdapter();
            }
        });
    }

    public void onBackPressed() {
        if (isResultOK) {
            setResult(-1);
        }
        finish();
    }
}
