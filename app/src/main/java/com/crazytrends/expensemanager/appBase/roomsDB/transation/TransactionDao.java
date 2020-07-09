package com.crazytrends.expensemanager.appBase.roomsDB.transation;



import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TransactionDao {
    @Delete
    int delete(TransactionRowModel transactionRowModel);

    @Query("DELETE FROM transactionList")
    void deleteAll();

    @Query("DELETE FROM transactionList WHERE categoryId=:categoryId")
    void deleteAllCategory(String categoryId);

    @Query("DELETE FROM transactionList WHERE modeId=:modeId")
    void deleteAllMode(String modeId);

    @Query("Select * FROM transactionList ORDER BY dateTimeInMillis desc ")
    List<TransactionRowModel> getAll();

    @Query("Select count(*) FROM transactionList")
    int getAllCount();

    @Query("Select sum(amount) FROM transactionList where type=:type")
    double getTypeTotal(int type);

    @Insert
    long insert(TransactionRowModel transactionRowModel);

    @Update
    int update(TransactionRowModel transactionRowModel);
}
