package com.crazytrends.expensemanager.appBase.roomsDB;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.crazytrends.expensemanager.appBase.roomsDB.category.CategoryDao;
import com.crazytrends.expensemanager.appBase.roomsDB.category.CategoryRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.dbVerson.DbVersionDao;
import com.crazytrends.expensemanager.appBase.roomsDB.dbVerson.DbVersionRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.demo.DemoDao;
import com.crazytrends.expensemanager.appBase.roomsDB.demo.DemoRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.mode.ModeDao;
import com.crazytrends.expensemanager.appBase.roomsDB.mode.ModeRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.statistics.StatisticsDao;
import com.crazytrends.expensemanager.appBase.roomsDB.transation.TransactionDao;
import com.crazytrends.expensemanager.appBase.roomsDB.transation.TransactionRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.types.TypeDao;
import com.crazytrends.expensemanager.appBase.roomsDB.types.TypeRowModel;
import com.crazytrends.expensemanager.appBase.utils.Constants;

@Database(entities = {DemoRowModel.class, TransactionRowModel.class, CategoryRowModel.class, ModeRowModel.class, TypeRowModel.class, DbVersionRowModel.class}, exportSchema = false, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    private static AppDataBase instance;

    public abstract CategoryDao categoryDao();

    public abstract DbVersionDao dbVersionDao();

    public abstract DemoDao demoDao();

    public abstract ModeDao modeDao();

    public abstract StatisticsDao statisticsDao();

    public abstract TransactionDao transactionDao();

    public abstract TypeDao typeDao();

    public static AppDataBase getAppDatabase(Context context) {
        if (instance == null) {
            instance = (AppDataBase) Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class, Constants.APP_DB_NAME).allowMainThreadQueries().build();
        }
        return instance;
    }
}
