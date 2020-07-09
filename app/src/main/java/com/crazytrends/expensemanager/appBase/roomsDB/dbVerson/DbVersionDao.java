package com.crazytrends.expensemanager.appBase.roomsDB.dbVerson;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DbVersionDao {
    @Delete
    int delete(DbVersionRowModel dbVersionRowModel);

    @Query("DELETE FROM categoryList")
    void deleteAllCategory();

    @Query("DELETE FROM modeList")
    void deleteAllMode();

    @Query("DELETE FROM transactionList")
    void deleteAllTransaction();

    @Query("DELETE FROM typeList")
    void deleteAllType();

    @Query("Select * FROM dbVersionList ")
    List<DbVersionRowModel> getAll();

    @Query("Select * FROM dbVersionList where versionNumber=:versionNumber")
    DbVersionRowModel getDetail(int versionNumber);

    @Insert
    long insert(DbVersionRowModel dbVersionRowModel);

    @Update
    int update(DbVersionRowModel dbVersionRowModel);
}
