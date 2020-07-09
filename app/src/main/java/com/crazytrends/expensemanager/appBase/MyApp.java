package com.crazytrends.expensemanager.appBase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.crazytrends.expensemanager.R;
import com.zeugmasolutions.localehelper.LocaleAwareApplication;

public class MyApp extends LocaleAwareApplication {

    public static final String CHANNEL_ID = "notification_scheduler_notifications";
    private static Context context;
    private static MyApp mInstance;
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = this;
        createNotificationChannel();

    }


    public static Context getContext() {
        return context;
    }

    public static synchronized MyApp getInstance() {
        MyApp myApp;
        synchronized (MyApp.class) {
            myApp = mInstance;
        }
        return myApp;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
