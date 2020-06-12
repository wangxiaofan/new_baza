package com.baza.android.bzw.constant;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.baza.android.bzw.businesscontroller.home.HomeActivity;
import com.bznet.android.rcbox.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CommonUtils {

    public static int notificationId = 1000;

    public static boolean setNotificationBadge(int count, Context context) {
        if (count != -1) {
            return false;
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService
                (Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 8.0之后添加角标需要NotificationChannel
            NotificationChannel channel = new NotificationChannel("badge", "badge",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(context, "badge")
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText("您有" + count + "条未读消息")
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setSmallIcon(R.drawable.bottom_action_border)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setChannelId("badge")
                .setNumber(count)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL).build();
        // 小米
        try {
            Field field = notification.getClass().getDeclaredField("extraNotification");
            Object extraNotification = field.get(notification);
            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int
                    .class);
            method.invoke(extraNotification, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        notificationManager.notify(notificationId++, notification);
        return true;
    }

}
