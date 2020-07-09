package com.crazytrends.expensemanager.appBase.roomsDB.demo;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DemoDao {
    @Delete
    int delete(DemoRowModel demoRowModel);

    @Query("DELETE FROM demoList")
    void deleteAll();

    @Query("Select * FROM demoList")
    List<DemoRowModel> getAll();

    @Insert
    long insert(DemoRowModel demoRowModel);

    @Update
    int update(DemoRowModel demoRowModel);
}
