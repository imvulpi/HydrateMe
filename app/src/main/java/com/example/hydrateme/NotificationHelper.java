package com.example.hydrateme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationHelper {

    public void scheduleNotification(Context context, long time, String title, String text) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("text", text);
        PendingIntent pending = PendingIntent.getBroadcast(context, 42, intent, PendingIntent.FLAG_IMMUTABLE);

        time = System.currentTimeMillis() + time;
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending);
        } else {
            // For devices below API level 23, use setExact() instead
            manager.setExact(AlarmManager.RTC_WAKEUP, time, pending);
        }
    }

    public void cancelNotification(Context context, String title, String text) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("text", text);
        PendingIntent pending = PendingIntent.getBroadcast(context, 42, intent, PendingIntent.FLAG_IMMUTABLE);
        // Cancel notification
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pending);
    }
}
