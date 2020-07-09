package com.crazytrends.expensemanager.appBase.roomsDB.statistics;



import androidx.room.Dao;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

@Dao
public interface StatisticsDao {
    @RawQuery
    List<StatisticRowModel> getFilterList(SupportSQLiteQuery supportSQLiteQuery);
}
