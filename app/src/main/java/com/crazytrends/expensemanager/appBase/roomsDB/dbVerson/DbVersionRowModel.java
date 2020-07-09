package com.crazytrends.expensemanager.appBase.roomsDB.dbVerson;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "dbVersionList")
public class DbVersionRowModel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String versionDesc = "";
    private int versionNumber;

    public DbVersionRowModel(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersionNumber() {
        return this.versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getVersionDesc() {
        return this.versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }
}
