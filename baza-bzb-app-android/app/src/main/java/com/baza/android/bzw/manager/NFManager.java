package com.baza.android.bzw.manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.widget.RemoteViews;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.businesscontroller.friend.FriendRequestActivity;
import com.bznet.android.rcbox.R;

/**
 * Created by LW on 2016/9/14.
 * Title : 通知栏帮助类
 * Note :
 */
public class NFManager {
    private static android.app.NotificationManager mNotificationManager;
    private static NotificationCompat.Builder mFriendNotificationBuilder;
    private static final int NOTIFY_LOAD_APK_ID = 100;
    private static final int NOTIFY_FRIEND_ID = 101;

    public static Notification buildLoadApkNotification(Context context) {
        if (mNotificationManager == null)
            mNotificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("download_notify", "download_notify_name", NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.download_notification_layout);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), "download_notify");
        builder.setContent(contentView)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.status_bar_logo);
        Notification notification = builder.build();
        mNotificationManager.notify(NOTIFY_LOAD_APK_ID, notification);
        return notification;
    }

    public static void updateLoadApkNotification(Notification mNotification, int rate) {
        if (mNotificationManager == null)
            return;
        RemoteViews contentView = mNotification.contentView;
        contentView.setProgressBar(R.id.down_progressbar, 100, rate, false);
        mNotificationManager.notify(NOTIFY_LOAD_APK_ID, mNotification);
    }

    public static void cancelLoadApkNotification() {
        if (mNotificationManager == null)
            return;
        mNotificationManager.cancel(NOTIFY_LOAD_APK_ID);
    }

    public static void cancelFriendNotification() {
        if (mNotificationManager == null)
            return;
        mNotificationManager.cancel(NOTIFY_FRIEND_ID);
    }

    public static void cancelAllNotification() {
        if (mNotificationManager == null)
            return;
        mNotificationManager.cancel(NOTIFY_LOAD_APK_ID);
        mNotificationManager.cancel(NOTIFY_FRIEND_ID);
        mFriendNotificationBuilder = null;
        mNotificationManager = null;
    }

    public static void updateFriendNotification(Context context, String title, String msg) {
        if (mNotificationManager == null)
            mNotificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mFriendNotificationBuilder == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("friend_notify", "friend_notify_name", NotificationManager.IMPORTANCE_HIGH);
                mNotificationManager.createNotificationChannel(channel);
            }
            mFriendNotificationBuilder = new NotificationCompat.Builder(context.getApplicationContext(), "friend_notify");
            //设置通知栏点击意图
            mFriendNotificationBuilder.setContentIntent(PendingIntent.getActivity(BZWApplication.getApplication(), 0, new Intent(BZWApplication.getApplication(), FriendRequestActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                    .setSound(Uri.parse("android.resource://com.bznet.android.rcbox/raw/msg"))
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    //设置通知小ICON
                    .setSmallIcon(R.drawable.ic_launcher);
        }
        mFriendNotificationBuilder.setContentTitle(title)
                .setContentText(msg)
                .setWhen(System.currentTimeMillis());
        mNotificationManager.notify(NOTIFY_FRIEND_ID, mFriendNotificationBuilder.build());
    }
}
