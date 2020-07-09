package com.crazytrends.expensemanager.appBase.models;



import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.crazytrends.expensemanager.appBase.models.transaction.TransactionFilterModel;
import com.crazytrends.expensemanager.appBase.roomsDB.statistics.StatisticRowModel;
import java.util.ArrayList;

public class StatisticsListModel extends BaseObservable {
    private ArrayList<StatisticRowModel> arrayList;
    private TransactionFilterModel filterModel;
    private String noDataDetail;
    private String noDataDetailTrip;
    private int noDataIcon;
    private int noDataIconTrip;
    private String noDataText;
    private String noDataTextTrip;

    @Bindable
    public ArrayList<StatisticRowModel> getArrayList() {
        return this.arrayList;
    }

    public void setArrayList(ArrayList<StatisticRowModel> arrayList2) {
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

    public int getNoDataIconTrip() {
        return this.noDataIconTrip;
    }

    public void setNoDataIconTrip(int i) {
        this.noDataIconTrip = i;
    }

    public String getNoDataTextTrip() {
        return this.noDataTextTrip;
    }

    public void setNoDataTextTrip(String str) {
        this.noDataTextTrip = str;
    }

    public String getNoDataDetailTrip() {
        return this.noDataDetailTrip;
    }

    public void setNoDataDetailTrip(String str) {
        this.noDataDetailTrip = str;
    }

    @Bindable
    public TransactionFilterModel getFilterModel() {
        return this.filterModel;
    }

    public void setFilterModel(TransactionFilterModel transactionFilterModel) {
        this.filterModel = transactionFilterModel;
        notifyChange();
    }

    public boolean isListData() {
        return getArrayList() != null && getArrayList().size() > 0;
    }
}
