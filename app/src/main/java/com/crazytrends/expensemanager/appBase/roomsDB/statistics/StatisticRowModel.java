package com.crazytrends.expensemanager.appBase.roomsDB.statistics;


import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;

import com.github.mikephil.charting.utils.Utils;
import com.crazytrends.expensemanager.appBase.MyApp;
import com.crazytrends.expensemanager.appBase.appPref.AppPref;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;

public class StatisticRowModel extends BaseObservable {
    @ColumnInfo(name = "total")
    private double catTotal = Utils.DOUBLE_EPSILON;
    @ColumnInfo(name = "type")
    private String categoryId = "";
    private String imgType = "";
    @ColumnInfo(name = "cat_name")
    private String name = "";

    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String str) {
        this.categoryId = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getImgType() {
        return this.imgType;
    }

    public void setImgType(String str) {
        this.imgType = str;
    }

    public double getCatTotal() {
        return this.catTotal;
    }

    public void setCatTotal(double d) {
        this.catTotal = d;
    }

    public String getCatTotalLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append(AppPref.getCurrencySymbol(MyApp.getInstance()));
        sb.append(AppConstants.getFormattedPrice(getCatTotal()));
        return sb.toString();
    }
}
