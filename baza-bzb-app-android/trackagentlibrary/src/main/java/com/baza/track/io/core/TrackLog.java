package com.baza.track.io.core;

import android.util.Log;

import com.baza.track.core.BuildConfig;

/**
 * Created by Vincent.Lei on 2018/4/18.
 * Title：
 * Note：
 */
public class TrackLog {
    private static final String TAG = "TrackLog";
    private static boolean logEnable = BuildConfig.LOG_ENABLE;

    public static void d(String msg) {
        if (logEnable && msg != null) {
            Log.d(TAG, msg);
        }
    }
}
