package com.baza.jpush;

import android.content.Context;

import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * Created by Vincent.Lei on 2017/6/9.
 * Title：
 * Note：
 */

public class CustomerPushReceiver extends JPushMessageReceiver {
    @Override
    public void onRegister(Context context, String jPushId) {
        Pusher.log("get JPush userInfo");
        Pusher.getPushListener().onRegister(context, jPushId);
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        Pusher.log("CustomerPushReceiver onNotifyMessageArrived");
        Pusher.getPushListener().onNotifyMessageArrived(context, notificationMessage);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        Pusher.log("user open a notification");
        String extras = notificationMessage.notificationExtras;
        Pusher.log("JPush message extra = " + extras);
        Pusher.getPushListener().onNotifyMessageOpened(context, extras);
    }
}