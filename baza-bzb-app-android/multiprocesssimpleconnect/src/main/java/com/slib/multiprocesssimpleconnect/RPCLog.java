package com.slib.multiprocesssimpleconnect;

import android.util.Log;

/**
 * Created by Vincent.Lei on 2019/7/4.
 * Title：
 * Note：
 */
public class RPCLog {
    private static final String TAG = "RPCLog";

    public static void d(String msg) {
        if (BuildConfig.RPC_LOG_ENABLE)
            Log.d(TAG, msg);
    }
}
