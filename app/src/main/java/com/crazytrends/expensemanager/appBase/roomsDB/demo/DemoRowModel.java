package com.crazytrends.expensemanager.appBase.roomsDB.demo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "demoList")
public class DemoRowModel extends BaseObservable implements Parcelable {
    public static final Creator<DemoRowModel> CREATOR = new Creator<DemoRowModel>() {
        public DemoRowModel createFromParcel(Parcel parcel) {
            return new DemoRowModel(parcel);
        }

        public DemoRowModel[] newArray(int i) {
            return new DemoRowModel[i];
        }
    };
    @PrimaryKey
    @NonNull
    private String id;
    private String note;

    public int describeContents() {
        return 0;
    }

    public DemoRowModel() {
    }

    @NonNull
    public String getId() {
        return this.id;
    }

    public void setId(@NonNull String str) {
        this.id = str;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String str) {
        this.note = str;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.note);
    }

    protected DemoRowModel(Parcel parcel) {
        this.id = parcel.readString();
        this.note = parcel.readString();
    }
}
