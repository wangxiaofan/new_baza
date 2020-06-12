package com.baza.jpush;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Vincent.Lei on 2017/3/9.
 */

public class Pusher {
    /**
     * manifest 注册cn.jpush.android.service.PushService
     * 请保持process为这个
     */
    private static final String PUSH_PROCESS_NAME = "com.baza.push";
    private static final String TAG = "Push-Log";
    private static IPushListener mPushListener;

    static IPushListener getPushListener() {
        return mPushListener;
    }

    private static boolean isPushProcessRunning(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        if (am == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
            log(info.processName);
            if (PUSH_PROCESS_NAME.equals(info.processName))
                return true;
        }
        return false;
    }

    public static void startPush(Context context, IPushListener pushListener) {
        mPushListener = pushListener;
        if (mPushListener == null)
            throw new IllegalArgumentException("IPushListener can not be null！");
        //尝试绑定推送和用户关系bindUserToPush(0, null);
        if (JPushInterface.isPushStopped(context.getApplicationContext())) {
            log("------resumePush------");
            JPushInterface.resumePush(context.getApplicationContext());
            return;
        }
        if (!Pusher.isPushProcessRunning(context.getApplicationContext())) {
            log("------initPush------");
            JPushInterface.setDebugMode(false);
            JPushInterface.init(context.getApplicationContext());
        }
    }

    public static void stopPush(Context context) {
        log("------stopPush------");
        JPushInterface.stopPush(context.getApplicationContext());
    }

    public static void log(String msg) {
        Log.d(TAG, msg);
    }
}
