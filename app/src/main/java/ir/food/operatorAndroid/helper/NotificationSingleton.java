package ir.food.operatorAndroid.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import ir.food.operatorAndroid.R;
import ir.food.operatorAndroid.activity.Splash;

public class NotificationSingleton {

    private static final int NOTIFICATION_ID = 1880;
    private static final String CHANNEL_ID = "Foreground Service Channel";
    private static Notification notification;

    // TODO change the group names in other application

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Notification getNotification(Context context) {
        if (notification == null) {
            Intent intent = new Intent(context, Splash.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannelGroup(new NotificationChannelGroup("PizzaOptGroupId", "PizzaOptGroupName"));
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "PizzaOptChannelName",
                    NotificationManager.IMPORTANCE_MIN);

            notificationManager.createNotificationChannel(notificationChannel);

            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(new StringBuilder(context.getResources().getString(R.string.app_name)))
                    .setContentText("درحال کار")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build();
        }

        return notification;
    }

    public static int getNotificationId() {
        return NOTIFICATION_ID;
    }
}