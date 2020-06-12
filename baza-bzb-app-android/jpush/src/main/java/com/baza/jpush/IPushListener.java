package com.baza.jpush;

import android.content.Context;

import cn.jpush.android.api.NotificationMessage;

/**
 * Created by Vincent.Lei on 2019/7/1.
 * Title：
 * Note：
 */
public interface IPushListener {
    void onRegister(Context context, String jPushId);

    void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage);

    void onNotifyMessageOpened(Context context, String extras);
}
