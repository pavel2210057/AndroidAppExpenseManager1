package com.crazytrends.expensemanager.appBase.roomsDB.types;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.crazytrends.expensemanager.appBase.roomsDB.demo.DemoRowModel;
import java.util.List;

@Dao
public interface TypeDao {
    @Delete
    int delete(TypeRowModel typeRowModel);

    @Query("DELETE FROM typeList")
    void deleteAll();

    @Query("Select * FROM typeList")
    List<DemoRowModel> getAll();

    @Insert
    long insert(TypeRowModel typeRowModel);

    @Update
    int update(TypeRowModel typeRowModel);
}
