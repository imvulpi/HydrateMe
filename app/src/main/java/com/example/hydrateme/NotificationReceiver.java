package com.example.hydrateme;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;


public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE);
            System.out.println("Version >= Android Oreo, setting notification");
            NotificationChannel notificationChannel = new NotificationChannel("9091", "Hydration", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(notificationChannel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "9091");
            builder.setContentTitle("Hydration Notification")
                    .setContentText("Drink water to stay hydrated.")
                    .setSmallIcon(R.drawable.bottle_water_universal)
                    .setAutoCancel(true); // Automatically remove the notification when clicked

            builder.setContentIntent(pendingIntent);

            builder.setChannelId("9091");
            Notification notification = builder.build();
            notificationManager.notify(0, notification);
        }else{
            System.out.println("Version is not >= Android Oreo, setting notification");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            @SuppressWarnings("deprecation") NotificationCompat.Builder builder = new NotificationCompat.Builder(context); //   Needed for api <= 16

            builder.setContentTitle("Hydration Notification")
                    .setContentText("Drink water to stay hydrated.")
                    .setSmallIcon(R.drawable.bottle_water_universal)
                    .setAutoCancel(true); // Automatically remove the notification when clicked

            Notification notification = builder.build();
            notificationManager.notify(0,notification);
        }
    }
}
