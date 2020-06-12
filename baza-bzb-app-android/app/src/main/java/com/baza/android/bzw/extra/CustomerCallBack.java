package com.baza.android.bzw.extra;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.baza.android.bzw.bean.CustomerHttpResultBean;
import com.slib.http.BaseResultCallBack;
import com.slib.http.HttpConfig;
import com.slib.http.INetworkCallBack;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Vincent.Lei on 2018/11/7.
 * Title：
 * Note：
 */
public class CustomerCallBack<T> extends BaseResultCallBack<T> {
    private INetworkCallBack<CustomerHttpResultBean<T>> mCallBack;
    private long mStartTime;
    private CustomerHttpResultBean<T> backResult;

    protected CustomerCallBack(String mUrl, Class<T> classzz, HttpConfig mHttpConfig, INetworkCallBack<CustomerHttpResultBean<T>> callBack) {
        super(mUrl, classzz, mHttpConfig);
        this.mCallBack = callBack;
        this.mStartTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        if (mCallBack == null)
            return;
        if (mHttpConfig.getRequestAssistHandler() == null) {
            realBack(backResult != null, mCallBack, backResult);
            return;
        }
        boolean success = mHttpConfig.getRequestAssistHandler().isResultOK(backResult);
        realBack(success, mCallBack, backResult);
    }

    private void realBack(boolean success, final INetworkCallBack<CustomerHttpResultBean<T>> mCallBack, CustomerHttpResultBean<T> result) {
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
                    JSONObject jsonObject = new JSONObject(resultStr);
                    backResult = new CustomerHttpResultBean<T>();
                    backResult.code = jsonObject.getInt("code");
                    backResult.msg = jsonObject.getString("msg");
                    String dataStr = jsonObject.getString("data");
                    if (!TextUtils.isEmpty(dataStr))
                        backResult.data = JSON.parseObject(dataStr, mClass);
                }
            } catch (Exception e) {
                backResult = null;
                logE("an error happened when parse http result to object target class is : " + mClass.getName());
            }
        }
        if (mCallBack != null)
            mHandler.postDelayed(this, (System.currentTimeMillis() - mStartTime) > DELAY_TIME ? 0 : DELAY_TIME);
    }
}