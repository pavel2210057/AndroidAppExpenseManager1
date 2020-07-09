package com.crazytrends.expensemanager.appBase.view;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.appPref.AppPref;
import com.crazytrends.expensemanager.appBase.roomsDB.AppDataBase;
import com.crazytrends.expensemanager.appBase.roomsDB.category.CategoryRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.dbVerson.DbVersionRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.mode.ModeRowModel;
import com.crazytrends.expensemanager.appBase.roomsDB.types.TypeRowModel;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.BackgroundAsync;
import com.crazytrends.expensemanager.appBase.utils.Constants;
import com.crazytrends.expensemanager.appBase.utils.OnAsyncBackground;
import com.crazytrends.expensemanager.dailyAlarm.NotificationPublisher;
import com.crazytrends.expensemanager.intro.Util;

import java.lang.ref.WeakReference;
import java.util.Calendar;

public class SplashActivity extends AppCompatActivity {
    public Context context;
    private AppDataBase db;
    SplashActivity splash_activity;
    public WeakReference<SplashActivity> splash_activityWeakReference;




    public void onCreate(Bundle bundle) {
        context = this;
        db = AppDataBase.getAppDatabase(context);
        splash_activityWeakReference = new WeakReference<>(this);
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_splash);
        if (!AppPref.isFirstLaunch(context)) {
            firststart();
        } else if (!AppPref.isDefaultInserted(context)) {
            insertDefaultDataList();
        } else {
            openMainActivity();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            Calendar startTime = valueToCalendar();
            Calendar now = Calendar.getInstance();
            if (now.after(startTime)) {
                startTime.add(Calendar.DATE, 1);
            }
            scheduleNotification(startTime);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }

    }

    private static final int REQUEST_CODE = 0;

    public PendingIntent getNotificationPublisherIntent(Notification notification) {
        Intent intent = new Intent(this, NotificationPublisher.class);
        if (notification != null) {
            intent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        }

        return PendingIntent.getBroadcast(
                this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public  void scheduleNotification(Calendar startTime) {
        scheduleIntent(getNotificationPublisherIntent(null), startTime);
    }

    public void scheduleIntent(PendingIntent intent, Calendar startTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, intent);
    }

    public static Calendar valueToCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, getHours(480));
        calendar.set(Calendar.MINUTE, getMinutes(480));
        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }
    private static int getHours(int minAfterMidnight) {
        return minAfterMidnight / 60;
    }

    private static int getMinutes(int minAfterMidnight) {
        return minAfterMidnight % 60;
    }

    public void firststart() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    openMainActivity();
            }
        }, 3000);
        splash_activity = this;
        splash_activityWeakReference = new WeakReference<>(splash_activity);
    }

    private void insertDefaultDataList() {
        new BackgroundAsync(context, true, "", new OnAsyncBackground() {
            public void onPreExecute() {
            }

            public void doInBackground() {
                insertDefaultList();
            }

            public void onPostExecute() {
                AppPref.setDefaultInserted(context, true);
                AppPref.setFirstLaunch(context, false);
                firststart();
            }
        }).execute(new Object[0]);
    }

    public void insertDefaultList() {
        addTypeList();
        addModeList();
        addCategoryList();
        db.dbVersionDao().insert(new DbVersionRowModel(1));
    }

    private void addTypeList() {
        db.typeDao().insert(new TypeRowModel(Constants.CAT_TYPE_INCOME, Constants.CAT_TYPE_INCOME_NAME));
        db.typeDao().insert(new TypeRowModel(Constants.CAT_TYPE_EXPENSE, Constants.CAT_TYPE_EXPENSE_NAME));
    }

    private void addModeList() {
        db.modeDao().insert(new ModeRowModel(AppConstants.getDefaultModeId(Constants.MODE_TYPE_CASH), Constants.MODE_TYPE_CASH, true));
        db.modeDao().insert(new ModeRowModel(AppConstants.getDefaultModeId(Constants.MODE_TYPE_CREDIT_CARD), Constants.MODE_TYPE_CREDIT_CARD, true));
        db.modeDao().insert(new ModeRowModel(AppConstants.getDefaultModeId(Constants.MODE_TYPE_DEBIT_CARD), Constants.MODE_TYPE_DEBIT_CARD, true));
        db.modeDao().insert(new ModeRowModel(AppConstants.getDefaultModeId(Constants.MODE_TYPE_NET_BANKING), Constants.MODE_TYPE_NET_BANKING, true));
        db.modeDao().insert(new ModeRowModel(AppConstants.getDefaultModeId(Constants.MODE_TYPE_CHEQUE), Constants.MODE_TYPE_CHEQUE, true));
    }

    private void addCategoryList() {
        try {
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_BUSINESS), Constants.CAT_TYPE_BUSINESS, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_LOAN), Constants.CAT_TYPE_LOAN, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_SALARY), Constants.CAT_TYPE_SALARY, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_CLOTHING), Constants.CAT_TYPE_CLOTHING, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_DRINKS), Constants.CAT_TYPE_DRINKS, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_EDUCATION), Constants.CAT_TYPE_EDUCATION, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_FOOD), Constants.CAT_TYPE_FOOD, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_FUEL), Constants.CAT_TYPE_FUEL, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_FUN), Constants.CAT_TYPE_FUN, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_HOSPITAL), Constants.CAT_TYPE_HOSPITAL, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_HOTEL), Constants.CAT_TYPE_HOTEL, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_MEDICAL), Constants.CAT_TYPE_MEDICAL, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_MERCHANDISE), Constants.CAT_TYPE_MERCHANDISE, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_MOVIE), Constants.CAT_TYPE_MOVIE, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_OTHER), Constants.CAT_TYPE_OTHER, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_PERSONAL), Constants.CAT_TYPE_PERSONAL, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_PETS), Constants.CAT_TYPE_PETS, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_RESTAURANT), Constants.CAT_TYPE_RESTAURANT, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_SHOPPING), Constants.CAT_TYPE_SHOPPING, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_TIPS), Constants.CAT_TYPE_TIPS, true));
            db.categoryDao().insert(new CategoryRowModel(AppConstants.getDefaultCategoryId(Constants.CAT_TYPE_TRANSPORT), Constants.CAT_TYPE_TRANSPORT, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void openMainActivity() {
        try {
            if (!AppPref.IsTermsAccept(context)) {
                startActivity(new Intent(this, ExpenseDisclosure.class));
            } else if (!AppPref.isFirstLaunch(context)) {
                startActivity(new Intent(context, MainActivity.class));
            } else {
                startActivity(new Intent(context, MainActivity.class));
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
