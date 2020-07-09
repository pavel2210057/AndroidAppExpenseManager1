package com.crazytrends.expensemanager.appBase.roomsDB.transation;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.github.mikephil.charting.utils.Utils;
import com.crazytrends.expensemanager.appBase.MyApp;
import com.crazytrends.expensemanager.appBase.appPref.AppPref;
import com.crazytrends.expensemanager.appBase.roomsDB.category.CategoryRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.mode.ModeRowModel;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.Constants;
import java.text.DateFormat;
import java.util.Calendar;

@Entity(tableName = "transactionList")
public class TransactionRowModel extends BaseObservable implements Parcelable {
    public static final Creator<TransactionRowModel> CREATOR = new Creator<TransactionRowModel>() {
        public TransactionRowModel createFromParcel(Parcel parcel) {
            return new TransactionRowModel(parcel);
        }

        public TransactionRowModel[] newArray(int i) {
            return new TransactionRowModel[i];
        }
    };
    private double amount = Utils.DOUBLE_EPSILON;
    private String categoryId;
    @Ignore
    private CategoryRowModel categoryRowModel;
    private long dateTimeInMillis;
    @PrimaryKey
    @NonNull
    private String id;
    private String modeId;
    @Ignore
    private ModeRowModel modeRowModel;
    private String note = "";
    private int type = Constants.CAT_TYPE_INCOME;
    @Ignore
    private int viewType;

    public int describeContents() {
        return 0;
    }

    public TransactionRowModel() {
    }

    @NonNull
    public String getId() {
        return this.id;
    }

    public void setId(@NonNull String str) {
        this.id = str;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double d) {
        this.amount = d;
    }

    @Bindable
    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String str) {
        this.categoryId = str;
        notifyChange();
    }

    @Bindable
    public long getDateTimeInMillis() {
        return this.dateTimeInMillis;
    }

    public void setDateTimeInMillis(long j) {
        this.dateTimeInMillis = j;
        notifyChange();
    }

    @Bindable
    public String getNote() {
        return this.note;
    }

    public void setNote(String str) {
        this.note = str;
        notifyChange();
    }

    @Bindable
    public String getModeId() {
        return this.modeId;
    }

    public void setModeId(String str) {
        this.modeId = str;
        notifyChange();
    }

    @Bindable
    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
        notifyChange();
    }

    @Bindable
    public CategoryRowModel getCategoryRowModel() {
        return this.categoryRowModel;
    }

    public void setCategoryRowModel(CategoryRowModel categoryRowModel2) {
        this.categoryRowModel = categoryRowModel2;
        notifyChange();
    }

    @Bindable
    public ModeRowModel getModeRowModel() {
        return this.modeRowModel;
    }

    public void setModeRowModel(ModeRowModel modeRowModel2) {
        this.modeRowModel = modeRowModel2;
        notifyChange();
    }

    public int getViewType() {
        return this.viewType;
    }

    public void setViewType(int i) {
        this.viewType = i;
    }

    public String getDateFormatted() {
        return AppConstants.getFormattedDate(getDateTimeInMillis(), AppPref.getDateFormat(MyApp.getInstance()));
    }

    public String getTimeFormatted() {
        return AppConstants.getFormattedDate(getDateTimeInMillis(), AppPref.getTimeFormat(MyApp.getInstance()));
    }

    public String getHeaderLabel() {
        return AppConstants.getFormattedDate(getDateTimeInMillis(), (DateFormat) Constants.SIMPLE_DATE_FORMAT_HEADER);
    }

    public long getMonthOnly() {
        return AppConstants.getMonthOnly(getDateTimeInMillis());
    }

    public long getDateOnly() {
        return AppConstants.getDateOnly(getDateTimeInMillis());
    }

    public String getAmountWithCurrency() {
        StringBuilder sb = new StringBuilder();
        sb.append(AppPref.getCurrencySymbol(MyApp.getInstance()));
        sb.append(AppConstants.getFormattedPrice(this.amount));
        return sb.toString();
    }

    public String getDateLabel() {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(getDateTimeInMillis());
        StringBuilder sb = new StringBuilder();
        sb.append(instance.get(5));
        sb.append("");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        return ((TransactionRowModel) obj).getDateTimeInMillis() == getDateTimeInMillis();
    }

    protected TransactionRowModel(Parcel parcel) {
        this.id = parcel.readString();
        this.amount = parcel.readDouble();
        this.categoryId = parcel.readString();
        this.dateTimeInMillis = parcel.readLong();
        this.note = parcel.readString();
        this.modeId = parcel.readString();
        this.type = parcel.readInt();
        this.categoryRowModel = (CategoryRowModel) parcel.readParcelable(CategoryRowModel.class.getClassLoader());
        this.modeRowModel = (ModeRowModel) parcel.readParcelable(ModeRowModel.class.getClassLoader());
        this.viewType = parcel.readInt();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeDouble(this.amount);
        parcel.writeString(this.categoryId);
        parcel.writeLong(this.dateTimeInMillis);
        parcel.writeString(this.note);
        parcel.writeString(this.modeId);
        parcel.writeInt(this.type);
        parcel.writeParcelable(this.categoryRowModel, i);
        parcel.writeParcelable(this.modeRowModel, i);
        parcel.writeInt(this.viewType);
    }

    public int setFont() {
        if(getType() == 1){
            return Color.parseColor("#ff49a142");
        }else {
            return Color.parseColor("#ffff4a4a");
        }
    }
}
