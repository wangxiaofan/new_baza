package com.slib.http;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import okhttp3.Callback;

/**
 * Created by Vincent.Lei on 2017/11/2.
 * Title：
 * Note：
 */

public abstract class BaseResultCallBack<T> implements Runnable, Callback {
    protected static final Handler mHandler = new Handler(Looper.getMainLooper());
    protected static final long DELAY_TIME = 300;

    protected String mUrl;
    protected Class<T> mClass;
    protected T result;
    protected HttpConfig mHttpConfig;

    protected BaseResultCallBack(String mUrl, Class<T> mClass, HttpConfig mHttpConfig) {
        this.mUrl = mUrl;
        this.mClass = mClass;
        this.mHttpConfig = mHttpConfig;
        log(mUrl);
    }

    protected void log(String resultStr) {
        if (mHttpConfig.mLogEnable && resultStr != null)
            Log.d(mHttpConfig.mLogTag, resultStr);
    }

    protected void logE(String resultStr) {
        if (mHttpConfig.mLogEnable && resultStr != null)
            Log.e(mHttpConfig.mLogTag, resultStr);
    }
}
