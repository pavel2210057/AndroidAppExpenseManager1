package com.crazytrends.expensemanager.appBase.view;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;

import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.MyApp;
import com.crazytrends.expensemanager.appBase.adapter.CategoryFilterAdapter;
import com.crazytrends.expensemanager.appBase.adapter.ModeFilterAdapter;
import com.crazytrends.expensemanager.appBase.appPref.AppPref;
import com.crazytrends.expensemanager.appBase.baseClass.BaseActivityBinding;
import com.crazytrends.expensemanager.appBase.models.toolbar.ToolbarModel;
import com.crazytrends.expensemanager.appBase.models.transaction.TransactionFilterModel;
import com.crazytrends.expensemanager.appBase.roomsDB.AppDataBase;
import com.crazytrends.expensemanager.appBase.roomsDB.category.CategoryRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.mode.ModeRowModel;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.Constants;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import com.crazytrends.expensemanager.appBase.utils.TwoButtonDialogListener;
import com.crazytrends.expensemanager.databinding.ActivityTransactionFilterAddEditBinding;
import java.util.Calendar;

public class AddEditTransactionFilterAct extends BaseActivityBinding {
    public static String EXTRA_MODEL = "model";
    private ActivityTransactionFilterAddEditBinding binding;

    public Calendar calendar;
    private AppDataBase db;

    public TransactionFilterModel model;
    public ToolbarModel toolbarModel;


    public void setBinding() {
        this.binding = (ActivityTransactionFilterAddEditBinding) DataBindingUtil.setContentView(this, R.layout.act_transaction_filter_add_edit);
        this.db = AppDataBase.getAppDatabase(this);
        this.calendar = Calendar.getInstance();
        this.calendar.set(11, 0);
        this.calendar.set(12, 0);
        this.calendar.set(13, 0);
        this.calendar.set(14, 0);
        setModelDetail();
        this.binding.setTransactionFilterModel(this.model);
    }

    private void setModelDetail() {
        try {
            this.model = (TransactionFilterModel) getIntent().getParcelableExtra(EXTRA_MODEL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setToolbar() {
        this.toolbarModel = new ToolbarModel();
        this.toolbarModel.setTitle(getString(R.string.filterTransactions));
        this.toolbarModel.setDelete(true);
        this.binding.includedToolbar.imgDelete.setImageResource(R.drawable.reset);
        this.toolbarModel.setAdd(true);
        this.binding.includedToolbar.imgAdd.setImageResource(R.drawable.save);
        this.binding.includedToolbar.setToolbarModel(this.toolbarModel);
    }


    public void setOnClicks() {
        this.binding.includedToolbar.imgBack.setOnClickListener(this);
        this.binding.includedToolbar.imgDelete.setOnClickListener(this);
        this.binding.includedToolbar.imgAdd.setOnClickListener(this);
        this.binding.radioSortAll.setOnClickListener(this);
        this.binding.radioSortToday.setOnClickListener(this);
        this.binding.radioSortYesterday.setOnClickListener(this);
        this.binding.radioSortLastWeek.setOnClickListener(this);
        this.binding.radioSortThisMonth.setOnClickListener(this);
        this.binding.radioSortLastMonth.setOnClickListener(this);
        this.binding.radioDateFilter.setOnClickListener(this);
        this.binding.radioTypeAll.setOnClickListener(this);
        this.binding.radioTypeIncome.setOnClickListener(this);
        this.binding.radioTypeExpense.setOnClickListener(this);
        this.binding.linFromDate.setOnClickListener(this);
        this.binding.linToDate.setOnClickListener(this);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imgAdd) {
            applyFilter();
        } else if (id == R.id.linFromDate) {
            showDatePickerDialog(true);
        } else if (id == R.id.linToDate) {
            showDatePickerDialog(false);
        } else if (id != R.id.radioDateFilter) {
            switch (id) {
                case R.id.imgBack :
                    onBackPressed();
                    return;
                case R.id.imgDelete :
                    resetFilter();
                    return;
                default:
                    switch (id) {
                        case R.id.radioSortAll :
                            setFilterDates(Constants.SORT_TYPE_ALL, false);
                            return;
                        case R.id.radioSortLastMonth :
                            setFilterDates(Constants.SORT_TYPE_LAST_MONTH, false);
                            return;
                        case R.id.radioSortLastWeek :
                            setFilterDates(Constants.SORT_TYPE_LAST_WEEK, false);
                            return;
                        case R.id.radioSortThisMonth :
                            setFilterDates(Constants.SORT_TYPE_THIS_MONTH, false);
                            return;
                        case R.id.radioSortToday :
                            setFilterDates(Constants.SORT_TYPE_TODAY, false);
                            return;
                        case R.id.radioSortYesterday :
                            setFilterDates(Constants.SORT_TYPE_YESTERDAY, false);
                            return;
                        default:
                            switch (id) {
                                case R.id.radioTypeAll :
                                    this.model.setFilterType(Constants.CAT_TYPE_All);
                                    return;
                                case R.id.radioTypeExpense :
                                    this.model.setFilterType(Constants.CAT_TYPE_EXPENSE);
                                    return;
                                case R.id.radioTypeIncome :
                                    this.model.setFilterType(Constants.CAT_TYPE_INCOME);
                                    return;
                                default:
                                    return;
                            }
                    }
            }
        } else {
            this.model.setDateFilter(!this.model.isDateFilter());
            if (this.model.isDateFilter()) {
                setFilterDates(Constants.SORT_TYPE_BETWEEN_DATES, true);
            } else {
                setFilterDates(Constants.SORT_TYPE_ALL, false);
            }
        }
    }

    public void resetFilter() {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.reset_msg));
        sb.append("<br /> <b>Your filter</b>");
        AppConstants.showTwoButtonDialog(this.context, getString(R.string.app_name), sb.toString(), true, true, getString(R.string.reset), getString(R.string.cancel), new TwoButtonDialogListener() {
            public void onCancel() {
            }

            public void onOk() {
                resetFilterDetails();
            }
        });
    }


    public void resetFilterDetails() {
        setFilterDates(Constants.SORT_TYPE_ALL, false);
        this.model.setFilterType(Constants.CAT_TYPE_All);
        deselectAllCategory();
        deselectAllModes();
        applyFilter();
    }

    public void setFilterDates(int i, boolean z) {
        this.model.setSortType(i);
        if (!z) {
            this.model.setDateFilter(false);
        }
        Calendar instance = Calendar.getInstance();
        instance.set(11, 0);
        instance.set(12, 0);
        instance.set(13, 0);
        instance.set(14, 0);
        Calendar instance2 = Calendar.getInstance();
        instance2.setTimeInMillis(instance.getTimeInMillis());
        if (i == Constants.SORT_TYPE_YESTERDAY) {
            instance2.add(5, -1);
            instance.add(5, -1);
        } else if (i == Constants.SORT_TYPE_LAST_WEEK) {
            Calendar instance3 = Calendar.getInstance();
            instance3.setTimeInMillis(AppConstants.getWeekFirstDate(instance2.getTimeInMillis(), 1).getTimeInMillis());
            instance3.add(5, -7);
            instance2.setTimeInMillis(instance3.getTimeInMillis());
            instance.setTimeInMillis(AppConstants.getWeekLastDate(instance3.getTimeInMillis()).getTimeInMillis());
        } else if (i == Constants.SORT_TYPE_THIS_MONTH) {
            instance2.set(5, 1);
            Calendar instance4 = Calendar.getInstance();
            instance4.setTimeInMillis(instance2.getTimeInMillis());
            instance4.add(2, 1);
            instance4.add(5, -1);
            instance.setTimeInMillis(instance4.getTimeInMillis());
        } else if (i == Constants.SORT_TYPE_LAST_MONTH) {
            instance2.add(2, -1);
            instance2.set(5, 1);
            Calendar instance5 = Calendar.getInstance();
            instance5.setTimeInMillis(instance2.getTimeInMillis());
            instance5.add(2, 1);
            instance5.add(5, -1);
            instance.setTimeInMillis(instance5.getTimeInMillis());
        } else if (i == Constants.SORT_TYPE_BETWEEN_DATES || i == Constants.SORT_TYPE_ALL) {
            instance2.add(5, -2);
        }
        this.model.setFromDateInMillis(instance2.getTimeInMillis());
        this.model.setToDateInMillis(instance.getTimeInMillis());
        StringBuilder sb = new StringBuilder();
        sb.append("model.getFromDateInMillis() : ");
        sb.append(AppConstants.getFormattedDate(this.model.getFromDateInMillis(), AppPref.getDateFormat(MyApp.getInstance())));
        Log.i("setFilterDates", sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("model.getToDateInMillis() : ");
        sb2.append(AppConstants.getFormattedDate(this.model.getToDateInMillis(), AppPref.getDateFormat(MyApp.getInstance())));
        Log.i("setFilterDates", sb2.toString());
    }

    private void deselectAllCategory() {
        for (int i = 0; i < this.model.getCategoryArrayList().size(); i++) {
            ((CategoryRowModel) this.model.getCategoryArrayList().get(i)).setSelected(false);
        }
        this.model.getCategoryList().clear();
    }

    private void deselectAllModes() {
        for (int i = 0; i < this.model.getModeArrayList().size(); i++) {
            ((ModeRowModel) this.model.getModeArrayList().get(i)).setSelected(false);
        }
        this.model.getModeList().clear();
    }

    private void applyFilter() {
        openItemList();
    }

    private void openItemList() {
        Intent intent = new Intent();
        intent.putExtra(AddEditTransactionAct.EXTRA_MODEL, this.model);
        setResult(-1, intent);
        finish();
    }


    public void initMethods() {
        setRecyclerCategory();
        setRecyclerModes();
    }


    public void setRecyclerCategory() {
        this.binding.recyclerCategories.setLayoutManager(new GridLayoutManager(this.context, 2));
        this.binding.recyclerCategories.setAdapter(new CategoryFilterAdapter(this.context, this.model.getCategoryArrayList(), this.model.getCategoryList(), new RecyclerItemClick() {
            public void onClick(int i, int i2) {
            }
        }));
    }


    public void setRecyclerModes() {
        this.binding.recyclerModes.setLayoutManager(new GridLayoutManager(this.context, 2));
        this.binding.recyclerModes.setAdapter(new ModeFilterAdapter(this.context, this.model.getModeArrayList(), this.model.getModeList(), new RecyclerItemClick() {
            public void onClick(int i, int i2) {
            }
        }));
    }

    private void showDatePickerDialog(final boolean z) {
        if (z) {
            this.calendar.setTimeInMillis(this.model.getFromDateInMillis());
        } else {
            this.calendar.setTimeInMillis(this.model.getToDateInMillis());
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(this.context, R.style.AppThemeDialogActionBar, new OnDateSetListener() {
            public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                calendar.set(1, i);
                calendar.set(2, i2);
                calendar.set(5, i3);
                if (z) {
                    model.setFromDateInMillis(calendar.getTimeInMillis());
                    if (model.getFromDateInMillis() > model.getToDateInMillis()) {
                        Calendar instance = Calendar.getInstance();
                        instance.setTimeInMillis(calendar.getTimeInMillis());
                        instance.add(5, 2);
                        model.setToDateInMillis(instance.getTimeInMillis());
                        return;
                    }
                    return;
                }
                model.setToDateInMillis(calendar.getTimeInMillis());
            }
        }, this.calendar.get(1), this.calendar.get(2), this.calendar.get(5));
        if (!z) {
            datePickerDialog.getDatePicker().setMinDate(this.model.getFromDateInMillis());
        }
        try {
            datePickerDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
