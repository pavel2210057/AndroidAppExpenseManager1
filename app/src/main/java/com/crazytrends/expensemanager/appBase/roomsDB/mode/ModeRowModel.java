package com.crazytrends.expensemanager.appBase.roomsDB.mode;

import android.os.Parcel;
import android.os.Parcelable;


import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "modeList")
public class ModeRowModel extends BaseObservable implements Parcelable {
    public static final Creator<ModeRowModel> CREATOR = new Creator<ModeRowModel>() {
        public ModeRowModel createFromParcel(Parcel parcel) {
            return new ModeRowModel(parcel);
        }

        public ModeRowModel[] newArray(int i) {
            return new ModeRowModel[i];
        }
    };
    @PrimaryKey
    @NonNull
    private String id;
    private boolean isDefault;
    @Ignore
    private boolean isSelected;
    private String name = "";

    public int describeContents() {
        return 0;
    }

    public ModeRowModel() {
    }

    @Ignore
    public ModeRowModel(@NonNull String str, String str2, boolean z) {
        this.id = str;
        this.name = str2;
        this.isDefault = z;
    }

    @NonNull
    public String getId() {
        return this.id;
    }

    public void setId(@NonNull String str) {
        this.id = str;
    }

    @Bindable
    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
        notifyChange();
    }

    public boolean isDefault() {
        return this.isDefault;
    }

    public void setDefault(boolean z) {
        this.isDefault = z;
    }

    @Bindable
    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean z) {
        this.isSelected = z;
        notifyChange();
    }

    protected ModeRowModel(Parcel parcel) {
        this.id = parcel.readString();
        this.name = parcel.readString();
        boolean z = false;
        this.isDefault = parcel.readByte() != 0;
        if (parcel.readByte() != 0) {
            z = true;
        }
        this.isSelected = z;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.name);
        parcel.writeByte(this.isDefault ? (byte) 1 : 0);
        parcel.writeByte(this.isSelected ? (byte) 1 : 0);
    }
}
