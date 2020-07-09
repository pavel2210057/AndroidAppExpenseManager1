package com.crazytrends.expensemanager.appBase.roomsDB.types;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "typeList")
public class TypeRowModel {
    @PrimaryKey
    @NonNull
    private int id;
    private String name = "";

    public TypeRowModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
