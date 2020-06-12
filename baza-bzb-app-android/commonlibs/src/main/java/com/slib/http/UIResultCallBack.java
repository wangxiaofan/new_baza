package com.slib.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Vincent.Lei on 2017/10/27.
 * Title：
 * Note：
 */

public class UIResultCallBack<T> extends BaseResultCallBack<T> {
    private INetworkCallBack<T> mCallBack;
    private long mStartTime;

    protected UIResultCallBack(String url, Class<T> classZZ, INetworkCallBack<T> mCallBack, HttpConfig httpConfig) {
        super(url, classZZ, httpConfig);
        this.mCallBack = mCallBack;
        this.mStartTime = System.currentTimeMillis();

    }

    @Override
    public void run() {
        if (mCallBack == null)
            return;
        if (mHttpConfig.mRequestAssistHandler == null) {
            realBack(result != null, mCallBack, result);
            return;
        }
        boolean success = mHttpConfig.mRequestAssistHandler.isResultOK(result);
        realBack(success, mCallBack, result);
    }

    private void realBack(boolean success, final INetworkCallBack<T> mCallBack, T result) {
        try {
            if (success)
                mCallBack.onSuccess(result);
            else
                mCallBack.onFailed(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        if (mCallBack != null)
            mHandler.postDelayed(this, (System.currentTimeMillis() - mStartTime) > DELAY_TIME ? 0 : DELAY_TIME);
    }

    @Override
    public void onResponse(Call call, Response response) {
        if (response != null && response.isSuccessful() && response.body() != null) {
            try {
                String resultStr = response.body().string();
                log(resultStr);
                if (!TextUtils.isEmpty(resultStr)) {
                    result = JSON.parseObject(resultStr, mClass);
                }
            } catch (Exception e) {
//                e.printStackTrace();
                logE("an error happened when parse http result to object target class is : " + mClass.getName());
            }
        }
        if (mCallBack != null)
            mHandler.postDelayed(this, (System.currentTimeMillis() - mStartTime) > DELAY_TIME ? 0 : DELAY_TIME);
    }
}
