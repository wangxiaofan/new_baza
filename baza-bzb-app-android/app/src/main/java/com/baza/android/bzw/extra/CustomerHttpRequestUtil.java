package com.baza.android.bzw.extra;

import com.baza.android.bzw.bean.CustomerHttpResultBean;
import com.slib.http.HttpConfig;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Vincent.Lei on 2018/11/7.
 * Title：
 * Note：
 */
public class CustomerHttpRequestUtil {

    public static void init(HttpConfig httpConfig) {
        HttpRequestUtil.init(httpConfig);
    }

    public static <T> void doHttpGet(String url, Class<T> classZZ, final INetworkCallBack<CustomerHttpResultBean<T>> mCallBack) {
        doHttpGet(url, null, classZZ, mCallBack);
    }


    public static <T> void doHttpGet(String url, HashMap<String, String> header, Class<T> classZZ, final INetworkCallBack<CustomerHttpResultBean<T>> mCallBack) {
        HttpRequestUtil.getSingleTimeOutClient(0, 0).newCall(HttpRequestUtil.getDefaultBuild(url, header).get().build()).enqueue(new CustomerCallBack<>(url, classZZ, HttpRequestUtil.getHttpConfig(), mCallBack));
    }

    public static <T> Call doHttpPost(String url, HashMap<String, String> param, Class<T> classZZ, final INetworkCallBack<CustomerHttpResultBean<T>> mCallBack) {
        return doHttpPost(url, param, null, classZZ, mCallBack);
    }

    public static <T> Call doHttpPost(String url, HashMap<String, String> param, HashMap<String, String> header, Class<T> classZZ, final INetworkCallBack<CustomerHttpResultBean<T>> mCallBack) {
        return doHttpPost(url, param, header, classZZ, 0, 0, mCallBack);
    }

    public static <T> Call doHttpPost(String url, HashMap<String, String> param, HashMap<String, String> header, Class<T> classZZ, long readTimeOut, long writeTimeOut, final INetworkCallBack<CustomerHttpResultBean<T>> mCallBack) {
        Call call = HttpRequestUtil.getSingleTimeOutClient(readTimeOut, writeTimeOut).newCall(HttpRequestUtil.getDefaultPostBuild(url, header, param).build());
        call.enqueue(new CustomerCallBack<>(url, classZZ, HttpRequestUtil.getHttpConfig(), mCallBack));
        return call;
    }

    public static <T> void doHttpPostJson(String url, String jsonParam, HashMap<String, String> header, Class<T> classZZ, final INetworkCallBack<CustomerHttpResultBean<T>> mCallBack) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParam);
        HttpRequestUtil.getSingleTimeOutClient(0, 0).newCall(HttpRequestUtil.getDefaultBuild(url, header).post(body).build()).enqueue(new CustomerCallBack<>(url, classZZ, HttpRequestUtil.getHttpConfig(), mCallBack));
    }

}
