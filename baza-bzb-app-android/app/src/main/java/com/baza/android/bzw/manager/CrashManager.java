package com.baza.android.bzw.manager;

import android.content.Context;
import android.content.Intent;

import com.baza.android.bzw.businesscontroller.publish.LauncherActivity;
import com.baza.android.bzw.constant.ConfigConst;

/**
 * Created by Vincent.Lei on 2017/5/10.
 * Title：
 * Note：
 */

public class CrashManager implements Thread.UncaughtExceptionHandler {
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;

    private CrashManager(Context context) {
        this.mContext = context.getApplicationContext();
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    public static void init(Context context) {
        new CrashManager(context);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null)
            mDefaultHandler.uncaughtException(thread, ex);
    }

    private boolean handleException(Throwable ex) {
        if (ex != null && ConfigConst.IS_PROCESS_CRASH_MSG) {
            ex.printStackTrace();
            Intent intent = new Intent(mContext, LauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("isRestartForCrash", true);
            mContext.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            return true;
        }

        return false;
    }
}
