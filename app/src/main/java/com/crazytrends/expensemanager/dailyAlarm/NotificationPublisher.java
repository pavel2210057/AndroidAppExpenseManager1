package com.crazytrends.expensemanager.dailyAlarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.MyApp;
import com.crazytrends.expensemanager.appBase.view.MainAct;


public class NotificationPublisher extends BroadcastReceiver {
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;
        if (intent.hasExtra(NOTIFICATION)) {
            notification = intent.getParcelableExtra(NOTIFICATION);
        } else {
            notification = buildNotification(ctx);
        }

        int notificationId = (int) System.currentTimeMillis();

        notificationManager.notify(notificationId, notification);
    }

    private Notification buildNotification(Context ctx) {
        Intent intent =
                new Intent(ctx, MainAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0);
        Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, MyApp.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmap)
                .setContentTitle("Expense Manager")
                .setContentText("Look at your balanse control")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        return builder.build();
    }
}
