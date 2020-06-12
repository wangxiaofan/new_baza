package com.baza.android.bzw.log;

import android.util.Log;

import com.baza.android.bzw.constant.ConfigConst;

/**
 * Created by LW on 2016/5/20.
 * Title : log
 * Note :
 */
public class LogUtil {
    private static final boolean IS_LOGIN_ENABLE = ConfigConst.INT_LOG_LEVEL > 0;

    public static final String TAG = "RCBox";
    public static final String SCHEME_APPEND = " ===>>> ";
    public static final String SCHEME_DEFAULT = "INFO";

    public static void d(String msg) {
        d(TAG, SCHEME_DEFAULT, msg);
    }

    public static void d(String scheme, String msg) {
        if (IS_LOGIN_ENABLE)
            d(TAG, scheme, msg);
    }

    public static void d(String tag, String scheme, String msg) {
        if (IS_LOGIN_ENABLE)
            Log.d(tag, scheme + SCHEME_APPEND + msg);
    }

    public static void e(String msg) {
        e(TAG, SCHEME_DEFAULT, msg);
    }

    public static void e(String scheme, String msg) {
        if (IS_LOGIN_ENABLE)
            e(TAG, scheme, msg);
    }

    public static void e(String tag, String scheme, String msg) {
        if (IS_LOGIN_ENABLE)
            Log.e(tag, scheme + SCHEME_APPEND + msg);
    }

}
