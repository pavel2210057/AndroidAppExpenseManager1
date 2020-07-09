package com.crazytrends.expensemanager.appBase.roomsDB.mode;



import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ModeDao {
    @Delete
    int delete(ModeRowModel modeRowModel);

    @Query("DELETE FROM modeList")
    void deleteAll();

    @Query("Select * FROM modeList")
    List<ModeRowModel> getAll();

    @Query("Select * FROM modeList where id=:str")
    ModeRowModel getDetail(String str);

    @Insert
    long insert(ModeRowModel modeRowModel);

    @Update
    int update(ModeRowModel modeRowModel);
}
