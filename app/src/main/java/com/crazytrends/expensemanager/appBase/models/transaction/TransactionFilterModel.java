package com.crazytrends.expensemanager.appBase.models.transaction;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.crazytrends.expensemanager.appBase.MyApp;
import com.crazytrends.expensemanager.appBase.appPref.AppPref;
import com.crazytrends.expensemanager.appBase.roomsDB.category.CategoryRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.mode.ModeRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.transation.TransactionRowModel;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.Constants;
import java.util.ArrayList;

public class TransactionFilterModel extends BaseObservable implements Parcelable {
    public static final Creator<TransactionFilterModel> CREATOR = new Creator<TransactionFilterModel>() {
        public TransactionFilterModel createFromParcel(Parcel parcel) {
            return new TransactionFilterModel(parcel);
        }

        public TransactionFilterModel[] newArray(int i) {
            return new TransactionFilterModel[i];
        }
    };
    private ArrayList<CategoryRowModel> categoryArrayList;
    private ArrayList<String> categoryList;
    private int filterType = Constants.CAT_TYPE_All;
    private long fromDateInMillis;
    private boolean isDateFilter;
    private boolean isMainFilter;
    private ArrayList<ModeRowModel> modeArrayList;
    private ArrayList<String> modeList;
    private int sortType = Constants.SORT_TYPE_ALL;
    private String statisticsByType = Constants.STATISTICS_BY_TYPE;
    private long toDateInMillis;

    public int describeContents() {
        return 0;
    }

    public TransactionFilterModel() {
    }

    @Bindable
    public int getSortType() {
        return this.sortType;
    }

    public void setSortType(int i) {
        this.sortType = i;
        notifyChange();
    }

    @Bindable
    public int getFilterType() {
        return this.filterType;
    }

    public void setFilterType(int i) {
        this.filterType = i;
        notifyChange();
    }

    @Bindable
    public boolean isDateFilter() {
        return this.isDateFilter;
    }

    public void setDateFilter(boolean z) {
        this.isDateFilter = z;
        notifyChange();
    }

    @Bindable
    public long getFromDateInMillis() {
        return this.fromDateInMillis;
    }

    public void setFromDateInMillis(long j) {
        this.fromDateInMillis = j;
        notifyChange();
    }

    @Bindable
    public long getToDateInMillis() {
        return this.toDateInMillis;
    }

    public void setToDateInMillis(long j) {
        this.toDateInMillis = j;
        notifyChange();
    }

    @Bindable
    public ArrayList<CategoryRowModel> getCategoryArrayList() {
        return this.categoryArrayList;
    }

    public void setCategoryArrayList(ArrayList<CategoryRowModel> arrayList) {
        this.categoryArrayList = arrayList;
        notifyChange();
    }

    @Bindable
    public ArrayList<String> getCategoryList() {
        return this.categoryList;
    }

    public void setCategoryList(ArrayList<String> arrayList) {
        this.categoryList = arrayList;
        notifyChange();
    }

    @Bindable
    public ArrayList<ModeRowModel> getModeArrayList() {
        return this.modeArrayList;
    }

    public void setModeArrayList(ArrayList<ModeRowModel> arrayList) {
        this.modeArrayList = arrayList;
        notifyChange();
    }

    @Bindable
    public ArrayList<String> getModeList() {
        return this.modeList;
    }

    public void setModeList(ArrayList<String> arrayList) {
        this.modeList = arrayList;
        notifyChange();
    }

    @Bindable
    public String getStatisticsByType() {
        return this.statisticsByType;
    }

    public void setStatisticsByType(String str) {
        this.statisticsByType = str;
        notifyChange();
    }

    @Bindable
    public boolean isMainFilter() {
        return this.isMainFilter;
    }

    public void setMainFilter(boolean z) {
        this.isMainFilter = z;
        notifyChange();
    }

    public String getFromDateFormatted() {
        return AppConstants.getFormattedDate(getFromDateInMillis(), AppPref.getDateFormat(MyApp.getInstance()));
    }

    public String getToDateFormatted() {
        return AppConstants.getFormattedDate(getToDateInMillis(), AppPref.getDateFormat(MyApp.getInstance()));
    }

    public boolean isContains(TransactionRowModel transactionRowModel) {
        return (getSortType() == Constants.SORT_TYPE_ALL || (transactionRowModel.getDateOnly() >= getFromDateInMillis() && transactionRowModel.getDateOnly() <= getToDateInMillis())) && (getFilterType() == Constants.CAT_TYPE_All || (getFilterType() != Constants.CAT_TYPE_INCOME ? transactionRowModel.getType() == Constants.CAT_TYPE_EXPENSE : transactionRowModel.getType() == Constants.CAT_TYPE_INCOME)) && ((getCategoryList().size() <= 0 || getCategoryList().contains(transactionRowModel.getCategoryId())) && (getModeList().size() <= 0 || getModeList().contains(transactionRowModel.getModeId())));
    }

    public String getQueryFilter() {
        StringBuilder sb = new StringBuilder();
        sb.append(getQueryPre());
        sb.append(getQueryWhereClause());
        sb.append(getQueryEnd());
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append("getQueryFilter: ");
        sb3.append(sb2);
        Log.i("DB", sb3.toString());
        return sb2;
    }

    public String getQueryWhereClause() {
        String str = "";
        String str2 = "";
        if (getFilterType() != Constants.CAT_TYPE_All) {
            StringBuilder sb = new StringBuilder();
            sb.append(" and t.type in ( ");
            sb.append(getFilterType());
            sb.append(" )");
            String sb2 = sb.toString();
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            sb3.append(sb2);
            str = sb3.toString();
        }
        if (getCategoryList().size() > 0) {
            String str3 = " and t.categoryId in (";
            for (int i = 0; i < getCategoryList().size(); i++) {
                if (i == 0) {
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append(str3);
                    sb4.append((String) getCategoryList().get(i));
                    str3 = sb4.toString();
                } else {
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append(str3);
                    sb5.append(",");
                    sb5.append((String) getCategoryList().get(i));
                    str3 = sb5.toString();
                }
            }
            StringBuilder sb6 = new StringBuilder();
            sb6.append(str3);
            sb6.append(")");
            String sb7 = sb6.toString();
            StringBuilder sb8 = new StringBuilder();
            sb8.append(str);
            sb8.append(sb7);
            str = sb8.toString();
        }
        if (getModeList().size() > 0) {
            String str4 = " and t.modeId in (";
            for (int i2 = 0; i2 < getModeList().size(); i2++) {
                if (i2 == 0) {
                    StringBuilder sb9 = new StringBuilder();
                    sb9.append(str4);
                    sb9.append((String) getModeList().get(i2));
                    str4 = sb9.toString();
                } else {
                    StringBuilder sb10 = new StringBuilder();
                    sb10.append(str4);
                    sb10.append(",");
                    sb10.append((String) getModeList().get(i2));
                    str4 = sb10.toString();
                }
            }
            StringBuilder sb11 = new StringBuilder();
            sb11.append(str4);
            sb11.append(")");
            String sb12 = sb11.toString();
            StringBuilder sb13 = new StringBuilder();
            sb13.append(str);
            sb13.append(sb12);
            str = sb13.toString();
        }
        if (getSortType() == Constants.SORT_TYPE_ALL) {
            return str;
        }
        if (!isMainFilter()) {
            StringBuilder sb14 = new StringBuilder();
            sb14.append(" and date(dateTimeInMillis/1000,'unixepoch','localtime')>= date(");
            sb14.append(getFromDateInMillis());
            sb14.append("/1000,'unixepoch','localtime') and date(dateTimeInMillis/1000,'unixepoch','localtime')<= date(");
            sb14.append(getToDateInMillis());
            sb14.append("/1000,'unixepoch','localtime')");
            str2 = sb14.toString();
        }
        StringBuilder sb15 = new StringBuilder();
        sb15.append(str);
        sb15.append(str2);
        return sb15.toString();
    }

    private String getQueryPre() {
        if (!getStatisticsByType().equalsIgnoreCase(Constants.STATISTICS_BY_TYPE)) {
            return getStatisticsByType().equalsIgnoreCase(Constants.STATISTICS_BY_CATEGORY) ? "select categoryId type, categoryList.name cat_name, ifnull(sum(t.amount),0) total from transactionList t left join categoryList on categoryList.id = t.categoryId where 1 = 1" : "select modeList.id type, modeList.name cat_name, ifnull(sum(t.amount),0) total from transactionList t left join modeList on modeList.id = t.modeId where 1=1";
        }
        if (!isMainFilter()) {
            return "select mt.id, case when (mt.id = 1) THEN 'Income' ELSE 'Expense' END cat_name,ifnull(sum(t.amount),0.00) total from typeList mt LEFT JOIN transactionList t  on t.type = mt.id left join categoryList on categoryList.id = t.categoryId where 1 = 1";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("select mt.id, case when (mt.id = 1) THEN 'Income' ELSE 'Expense' END cat_name,ifnull(sum(t.amount),0.00) total from typeList mt LEFT JOIN transactionList t  on t.type = mt.id and date(dateTimeInMillis/1000,'unixepoch','localtime')>= date(");
        sb.append(getFromDateInMillis());
        sb.append("/1000,'unixepoch','localtime') and date(dateTimeInMillis/1000,'unixepoch','localtime')<= date(");
        sb.append(getToDateInMillis());
        sb.append("/1000,'unixepoch','localtime')");
        return sb.toString();
    }

    private String getQueryEnd() {
        if (getStatisticsByType().equalsIgnoreCase(Constants.STATISTICS_BY_TYPE)) {
            return " group by mt.id";
        }
        return getStatisticsByType().equalsIgnoreCase(Constants.STATISTICS_BY_CATEGORY) ? " group by categoryId" : " group by modeList.id";
    }

    public boolean isFilter() {
        return getQueryWhereClause().length() > 0;
    }

    protected TransactionFilterModel(Parcel parcel) {
        this.sortType = parcel.readInt();
        this.filterType = parcel.readInt();
        boolean z = false;
        this.isDateFilter = parcel.readByte() != 0;
        this.fromDateInMillis = parcel.readLong();
        this.toDateInMillis = parcel.readLong();
        this.categoryArrayList = parcel.createTypedArrayList(CategoryRowModel.CREATOR);
        this.categoryList = parcel.createStringArrayList();
        this.modeArrayList = parcel.createTypedArrayList(ModeRowModel.CREATOR);
        this.modeList = parcel.createStringArrayList();
        this.statisticsByType = parcel.readString();
        if (parcel.readByte() != 0) {
            z = true;
        }
        this.isMainFilter = z;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.sortType);
        parcel.writeInt(this.filterType);
        parcel.writeByte(this.isDateFilter ? (byte) 1 : 0);
        parcel.writeLong(this.fromDateInMillis);
        parcel.writeLong(this.toDateInMillis);
        parcel.writeTypedList(this.categoryArrayList);
        parcel.writeStringList(this.categoryList);
        parcel.writeTypedList(this.modeArrayList);
        parcel.writeStringList(this.modeList);
        parcel.writeString(this.statisticsByType);
        parcel.writeByte(this.isMainFilter ? (byte) 1 : 0);
    }
}
