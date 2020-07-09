package com.crazytrends.expensemanager.appBase.view;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.databinding.DataBindingUtil;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.baseClass.BaseActivityBinding;
import com.crazytrends.expensemanager.appBase.models.toolbar.ToolbarModel;
import com.crazytrends.expensemanager.appBase.roomsDB.AppDataBase;
import com.crazytrends.expensemanager.appBase.roomsDB.demo.DemoRowModel;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.TwoButtonDialogListener;
import com.crazytrends.expensemanager.databinding.ActivityDemoAddEditBinding;

public class AddEditDemoActivity extends BaseActivityBinding {
    public static String EXTRA_ID = "id";
    public static String EXTRA_IS_DELETED = "isDeleted";
    public static String EXTRA_IS_EDIT = "isEdit";
    public static String EXTRA_MODEL = "model";
    public static String EXTRA_POSITION = "position";
    private ActivityDemoAddEditBinding binding;

    public AppDataBase db;
    private boolean isEdit = false;

    public DemoRowModel model;
    public ToolbarModel toolbarModel;


    public void initMethods() {
    }


    public void setBinding() {
        this.binding = (ActivityDemoAddEditBinding) DataBindingUtil.setContentView(this, R.layout.activity_demo_add_edit);
        this.db = AppDataBase.getAppDatabase(this);
        setModelDetail();
        this.binding.setDemoRowModel(this.model);
    }

    private void setModelDetail() {
        boolean z = false;
        if (getIntent() != null && getIntent().getBooleanExtra(EXTRA_IS_EDIT, false)) {
            z = true;
        }
        this.isEdit = z;
        try {
            this.model = (DemoRowModel) getIntent().getParcelableExtra(EXTRA_MODEL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setToolbar() {
        this.toolbarModel = new ToolbarModel();
        this.toolbarModel.setTitle(this.isEdit ? "Edit Demo" : "Add Demo");
        this.binding.includedToolbar.setToolbarModel(this.toolbarModel);
        setSupportActionBar(this.binding.includedToolbar.toolbar);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);
        menu.findItem(R.id.delete).setVisible(this.isEdit);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.delete) {
            return super.onOptionsItemSelected(menuItem);
        }
        deleteItem();
        return true;
    }

    public void deleteItem() {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.delete_msg));
        sb.append("<br /> <b>");
        sb.append(this.model.getNote());
        sb.append("</b>");
        AppConstants.showTwoButtonDialog(this.context, getString(R.string.app_name), sb.toString(), true, true, getString(R.string.delete), getString(R.string.cancel), new TwoButtonDialogListener() {
            public void onCancel() {
            }

            public void onOk() {
                try {
                    AddEditDemoActivity.this.db.demoDao().delete(AddEditDemoActivity.this.model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AddEditDemoActivity.this.openItemList(true);
            }
        });
    }


    public void setOnClicks() {
        this.binding.includedToolbar.imgBack.setOnClickListener(this);
        this.binding.btnSave.setOnClickListener(this);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnSave) {
            addUpdate();
        } else if (id == R.id.imgBack) {
            onBackPressed();
        }
    }

    private void addUpdate() {
        if (isValid()) {
            try {
                if (this.isEdit) {
                    this.db.demoDao().update(this.model);
                } else {
                    this.db.demoDao().insert(this.model);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            openItemList(false);
        }
    }

    private boolean isValid() {
        Context context = this.context;
        EditText editText = this.binding.etNote;
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.please_enter));
        sb.append(" ");
        sb.append(getString(R.string.note));
        if (AppConstants.isNotEmpty(context, editText, sb.toString())) {
            Context context2 = this.context;
            EditText editText2 = this.binding.etNote;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(getString(R.string.please_enter));
            sb2.append(" ");
            sb2.append(getString(R.string.note));
            if (AppConstants.isNotEmpty(context2, editText2, sb2.toString())) {
                return true;
            }
        }
        return false;
    }


    public void openItemList(boolean z) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_IS_DELETED, z);
        intent.putExtra(EXTRA_IS_EDIT, getIntent().getBooleanExtra(EXTRA_IS_EDIT, false));
        intent.putExtra(EXTRA_POSITION, getIntent().getIntExtra(EXTRA_POSITION, 0));
        intent.putExtra(EXTRA_MODEL, this.model);
        setResult(-1, intent);
        finish();
    }
}
