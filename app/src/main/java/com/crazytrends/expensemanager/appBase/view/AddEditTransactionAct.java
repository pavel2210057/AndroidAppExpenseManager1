package com.crazytrends.expensemanager.appBase.view;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.tabs.TabLayout;
import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.adapter.CategoryAdapter;
import com.crazytrends.expensemanager.appBase.adapter.ModeAdapter;
import com.crazytrends.expensemanager.appBase.baseClass.BaseActivityBinding;
import com.crazytrends.expensemanager.appBase.models.toolbar.ToolbarModel;
import com.crazytrends.expensemanager.appBase.roomsDB.AppDataBase;
import com.crazytrends.expensemanager.appBase.roomsDB.category.CategoryRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.mode.ModeRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.transation.TransactionRowModel;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.Constants;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import com.crazytrends.expensemanager.appBase.utils.TwoButtonDialogListener;
import com.crazytrends.expensemanager.databinding.ActivityTransactionAddEditBinding;
import com.crazytrends.expensemanager.databinding.AlertDialogRecyclerListBinding;

import java.util.ArrayList;
import java.util.Calendar;

public class AddEditTransactionAct extends BaseActivityBinding implements OnClickListener {
    public static String EXTRA_ID = "id";
    public static String EXTRA_IS_DELETED = "isDeleted";
    public static String EXTRA_IS_EDIT = "isEdit";
    public static String EXTRA_MODEL = "model";
    public static String EXTRA_POSITION = "position";
    private ActivityTransactionAddEditBinding binding;

    public Calendar calendar;

    public ArrayList<CategoryRowModel> categoryList;

    public AppDataBase db;

    public Dialog dialogCategoryList;
    private AlertDialogRecyclerListBinding dialogCategoryListBinding;

    public Dialog dialogModeList;
    private AlertDialogRecyclerListBinding dialogModeListBinding;
    private boolean isEdit = false;

    public ArrayList<ModeRowModel> modeList;

    public TransactionRowModel model;

    public int selectedCategoryPos = 0;

    public int selectedModePos = 0;
    public ToolbarModel toolbarModel;

    public void setBinding() {
        binding = (ActivityTransactionAddEditBinding) DataBindingUtil.setContentView(this, R.layout.act_transaction_add_edit);
        db = AppDataBase.getAppDatabase(this);
        setModelDetail();
        binding.setTransactionRowModel(model);


        binding.talSet.addTab(binding.talSet.newTab().setText(getResources().getString(R.string.income)));
        binding.talSet.addTab(binding.talSet.newTab().setText(getResources().getString(R.string.expense)));
        binding.talSet.setTabGravity(TabLayout.GRAVITY_FILL);


        binding.talSet.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    model.setType(Constants.CAT_TYPE_INCOME);
                } else {
                    model.setType(Constants.CAT_TYPE_EXPENSE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setModelDetail() {
        isEdit = getIntent() != null && getIntent().getBooleanExtra(EXTRA_IS_EDIT, false);
        try {
            model = (TransactionRowModel) getIntent().getParcelableExtra(EXTRA_MODEL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        calendar = Calendar.getInstance();
        if (model.getDateTimeInMillis() > 0) {
            calendar.setTimeInMillis(model.getDateTimeInMillis());
        } else {
            model.setDateTimeInMillis(calendar.getTimeInMillis());
        }
        calendar.set(13, 0);
        calendar.set(14, 0);
    }


    public void setToolbar() {
        toolbarModel = new ToolbarModel();
        ToolbarModel toolbarModel2 = toolbarModel;
        StringBuilder sb = new StringBuilder();
        sb.append(isEdit ? "Edit" : "Add");
        sb.append(" ");
        sb.append(model.getType() == Constants.CAT_TYPE_INCOME ? "Income" : "Expense");
        toolbarModel2.setTitle(sb.toString());
        toolbarModel.setAdd(true);
        binding.includedToolbar.imgAdd.setImageResource(R.drawable.save);
        toolbarModel.setDelete(isEdit);
        binding.includedToolbar.setToolbarModel(toolbarModel);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);
        menu.findItem(R.id.delete).setVisible(isEdit);
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
        sb.append("<br /> <b>This Transaction</b>");
        AppConstants.showTwoButtonDialog(context, getString(R.string.app_name), sb.toString(), true, true, getString(R.string.delete), getString(R.string.cancel), new TwoButtonDialogListener() {
            public void onCancel() {
            }

            public void onOk() {
                try {
                    db.transactionDao().delete(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                openItemList(true);
            }
        });
    }


    public void setOnClicks() {
        binding.includedToolbar.imgBack.setOnClickListener(this);
        binding.includedToolbar.imgAdd.setOnClickListener(this);
        binding.includedToolbar.imgDelete.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);
        binding.cardViewCategory.setOnClickListener(this);
        binding.cardViewMode.setOnClickListener(this);
        binding.txtDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();

            }
        });
        binding.txtTime.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave :
            case R.id.imgAdd :
                    addUpdate();
                return;
            case R.id.cardViewCategory :
                showDialogCategoryList();
                return;
            case R.id.cardViewMode :
                showDialogModeList();
                return;
            case R.id.imgBack :
                onBackPressed();
                return;
            case R.id.imgDelete :
                deleteItem();
                return;
            case R.id.txtDate :
                return;
            case R.id.txtTime :
                showTimePickerDialog();
                return;
            default:
                return;
        }
    }


    public void initMethods() {
        setRadio();
        setEditTextValue();
        setEditTextChange();
        categoryDialogSetup();
        modeDialogSetup();
    }

    private void setRadio() {
        if (model.getType() == Constants.CAT_TYPE_INCOME) {
            binding.talSet.getTabAt(0).select();
        } else {
            binding.talSet.getTabAt(1).select();
        }

    }

    private void setEditTextValue() {
        if (isEdit) {
            try {
                binding.etAmount.setText(AppConstants.getFormattedPriceEdit(model.getAmount()));
            } catch (NumberFormatException e) {
                binding.etAmount.setText(0);
                e.printStackTrace();
            }
        }
    }

    private void setEditTextChange() {
        binding.etAmount.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                try {
                    model.setAmount(Double.valueOf(charSequence.toString().trim()).doubleValue());
                } catch (NumberFormatException e) {
                    model.setAmount(Utils.DOUBLE_EPSILON);
                    e.printStackTrace();
                }
            }
        });
    }

    private void addUpdate() {
        if (isValid()) {
            try {
                if (isEdit) {
                    db.transactionDao().update(model);
                } else {
                    db.transactionDao().insert(model);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            openItemList(false);
        }
    }

    private boolean isValid() {
        if (model.getAmount() > Utils.DOUBLE_EPSILON) {
            return true;
        }
        EditText editText = binding.etAmount;
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.please_enter));
        sb.append(" ");
        sb.append(getString(R.string.amount));
        AppConstants.requestFocusAndError(context, editText, sb.toString());
        return false;
    }


    public void openItemList(boolean z) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_IS_DELETED, z);
        intent.putExtra(EXTRA_IS_EDIT, getIntent().getBooleanExtra(EXTRA_IS_EDIT, false));
        intent.putExtra(EXTRA_POSITION, getIntent().getIntExtra(EXTRA_POSITION, 0));
        intent.putExtra(EXTRA_MODEL, model);
        setResult(-1, intent);
        finish();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.AppThemeDialogActionBar, new OnDateSetListener() {
            public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                calendar.set(1, i);
                calendar.set(2, i2);
                calendar.set(5, i3);
                model.setDateTimeInMillis(calendar.getTimeInMillis());
            }
        }, calendar.get(1), calendar.get(2), calendar.get(5));
        try {
            datePickerDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.AppThemeDialogActionBar, new OnTimeSetListener() {
            @SuppressLint({"NewApi"})
            public void onTimeSet(TimePicker timePicker, int i, int i2) {
                calendar.set(11, i);
                calendar.set(12, i2);
                model.setDateTimeInMillis(calendar.getTimeInMillis());
            }
        }, calendar.get(11), calendar.get(12), false);
        timePickerDialog.show();
    }

    private void categoryDialogSetup() {
        fillCategoryList();
        setCategoryListDialog();
    }

    private void fillCategoryList() {
        categoryList = new ArrayList<>();
        try {
            categoryList.addAll(db.categoryDao().getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
        selectedCategoryPos = getSelectedPosById();
        ((CategoryRowModel) categoryList.get(selectedCategoryPos)).setSelected(true);
        if (model.getCategoryRowModel() == null) {
            model.setCategoryId(((CategoryRowModel) categoryList.get(selectedCategoryPos)).getId());
            model.setCategoryRowModel((CategoryRowModel) categoryList.get(selectedCategoryPos));
        }
    }

    private int getSelectedPosById() {
        for (int i = 0; i < categoryList.size(); i++) {
            if (((CategoryRowModel) categoryList.get(i)).getId().equalsIgnoreCase(model.getCategoryId())) {
                return i;
            }
        }
        return 0;
    }

    public void setCategoryListDialog() {
        dialogCategoryListBinding = (AlertDialogRecyclerListBinding) DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.alert_dialog_recycler_list_act, null, false);
        dialogCategoryList = new Dialog(context);
        dialogCategoryList.setContentView(dialogCategoryListBinding.getRoot());
        dialogCategoryList.setCancelable(false);
        dialogCategoryList.getWindow().setBackgroundDrawableResource(17170445);
        dialogCategoryList.getWindow().setLayout(-1, -2);
        dialogCategoryListBinding.txtTitle.setText(R.string.select_category);
        dialogCategoryListBinding.btnOk.setText(R.string.set);
        dialogCategoryListBinding.recycler.setLayoutManager(new LinearLayoutManager(context));
        dialogCategoryListBinding.recycler.setAdapter(new CategoryAdapter(context, false, categoryList, new RecyclerItemClick() {
            public void onClick(int i, int i2) {
                selectedCategoryPos = i;
            }
        }));
        dialogCategoryListBinding.imgAdd.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    dialogCategoryList.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialogCategoryListBinding.btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    dialogCategoryList.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialogCategoryListBinding.btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                model.setCategoryId(((CategoryRowModel) categoryList.get(selectedCategoryPos)).getId());
                model.setCategoryRowModel((CategoryRowModel) categoryList.get(selectedCategoryPos));
                try {
                    dialogCategoryList.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showDialogCategoryList() {
        try {
            dialogCategoryListBinding.recycler.scrollToPosition(selectedCategoryPos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (dialogCategoryList != null && !dialogCategoryList.isShowing()) {
                dialogCategoryList.show();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void selectionAllCategory(boolean z) {
        for (int i = 0; i < categoryList.size(); i++) {
            ((CategoryRowModel) categoryList.get(i)).setSelected(z);
        }
    }

    private void modeDialogSetup() {
        fillModeList();
        setModeListDialog();
    }

    private void fillModeList() {
        modeList = new ArrayList<>();
        try {
            modeList.addAll(db.modeDao().getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
        selectedModePos = getSelectedPosModeById();
        ((ModeRowModel) modeList.get(selectedModePos)).setSelected(true);
        if (model.getModeRowModel() == null) {
            model.setModeId(((ModeRowModel) modeList.get(selectedModePos)).getId());
            model.setModeRowModel((ModeRowModel) modeList.get(selectedModePos));
        }
    }

    private int getSelectedPosModeById() {
        for (int i = 0; i < modeList.size(); i++) {
            if (((ModeRowModel) modeList.get(i)).getId().equalsIgnoreCase(model.getModeId())) {
                return i;
            }
        }
        return 0;
    }

    public void setModeListDialog() {
        dialogModeListBinding = (AlertDialogRecyclerListBinding) DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.alert_dialog_recycler_list_act, null, false);
        dialogModeList = new Dialog(context);
        dialogModeList.setContentView(dialogModeListBinding.getRoot());
        dialogModeList.setCancelable(false);
        dialogModeList.getWindow().setBackgroundDrawableResource(17170445);
        dialogModeList.getWindow().setLayout(-1, -2);
        dialogModeListBinding.txtTitle.setText(R.string.selectPaymentMode);
        dialogModeListBinding.btnOk.setText(R.string.set);
        dialogModeListBinding.recycler.setLayoutManager(new LinearLayoutManager(context));
        dialogModeListBinding.recycler.setAdapter(new ModeAdapter(context, false, modeList, new RecyclerItemClick() {
            public void onClick(int i, int i2) {
                selectedModePos = i;
            }
        }));
        dialogModeListBinding.imgAdd.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    dialogModeList.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialogModeListBinding.btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    dialogModeList.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialogModeListBinding.btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                model.setModeId(((ModeRowModel) modeList.get(selectedModePos)).getId());
                model.setModeRowModel((ModeRowModel) modeList.get(selectedModePos));
                try {
                    dialogModeList.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showDialogModeList() {
        try {
            dialogModeListBinding.recycler.scrollToPosition(selectedModePos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (dialogModeList != null && !dialogModeList.isShowing()) {
                dialogModeList.show();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void selectionAllMode(boolean z) {
        for (int i = 0; i < modeList.size(); i++) {
            ((ModeRowModel) modeList.get(i)).setSelected(z);
        }
    }
}
