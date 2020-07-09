package com.crazytrends.expensemanager.appBase.roomsDB.category;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {
    @Delete
    int delete(CategoryRowModel categoryRowModel);

    @Query("DELETE FROM categoryList")
    void deleteAll();

    @Query("Select * FROM categoryList ORDER BY name COLLATE NOCASE")
    List<CategoryRowModel> getAll();

    @Query("Select * FROM categoryList where id=:id")
    CategoryRowModel getDetail(String id);

    @Insert
    long insert(CategoryRowModel categoryRowModel);

    @Update
    int update(CategoryRowModel categoryRowModel);
}
