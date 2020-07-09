package com.crazytrends.expensemanager.appBase.models.transaction;



import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.github.mikephil.charting.utils.Utils;
import com.crazytrends.expensemanager.appBase.MyApp;
import com.crazytrends.expensemanager.appBase.appPref.AppPref;
import com.crazytrends.expensemanager.appBase.roomsDB.transation.TransactionRowModel;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import java.util.ArrayList;

public class TransactionListModel extends BaseObservable {
    private ArrayList<TransactionRowModel> arrayList;
    private double balance = Utils.DOUBLE_EPSILON;
    private double expense = Utils.DOUBLE_EPSILON;
    private TransactionFilterModel filterModel;
    private double income = Utils.DOUBLE_EPSILON;
    private boolean isShowSummary = false;
    private String noDataDetail;
    private int noDataIcon;
    private String noDataText;

    @Bindable
    public ArrayList<TransactionRowModel> getArrayList() {
        return this.arrayList;
    }

    public void setArrayList(ArrayList<TransactionRowModel> arrayList2) {
        this.arrayList = arrayList2;
        notifyChange();
    }

    public int getNoDataIcon() {
        return this.noDataIcon;
    }

    public void setNoDataIcon(int i) {
        this.noDataIcon = i;
    }

    public String getNoDataText() {
        return this.noDataText;
    }

    public void setNoDataText(String str) {
        this.noDataText = str;
    }

    public String getNoDataDetail() {
        return this.noDataDetail;
    }

    public void setNoDataDetail(String str) {
        this.noDataDetail = str;
    }

    @Bindable
    public boolean isShowSummary() {
        return this.isShowSummary;
    }

    public void setShowSummary(boolean z) {
        this.isShowSummary = z;
        notifyChange();
    }

    @Bindable
    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double d) {
        this.balance = d;
        notifyChange();
    }

    @Bindable
    public double getIncome() {
        return this.income;
    }

    public void setIncome(double d) {
        this.income = d;
        notifyChange();
    }

    @Bindable
    public double getExpense() {
        return this.expense;
    }

    public void setExpense(double d) {
        this.expense = d;
        notifyChange();
    }

    public TransactionFilterModel getFilterModel() {
        return this.filterModel;
    }

    public void setFilterModel(TransactionFilterModel transactionFilterModel) {
        this.filterModel = transactionFilterModel;
    }

    public String getBalanceLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append(AppPref.getCurrencySymbol(MyApp.getInstance()));
        sb.append(AppConstants.getFormattedPrice(this.balance));
        return sb.toString();
    }

    public String getIncomeLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append(AppPref.getCurrencySymbol(MyApp.getInstance()));
        sb.append(AppConstants.getFormattedPrice(this.income));
        return sb.toString();
    }

    public String getExpenseLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append(AppPref.getCurrencySymbol(MyApp.getInstance()));
        sb.append(AppConstants.getFormattedPrice(this.expense));
        return sb.toString();
    }

    public boolean isListData() {
        return getArrayList() != null && getArrayList().size() > 0;
    }
}
