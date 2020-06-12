package com.tencent.qcloud.tim.uikit.utils.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Desc:http配置
 * Created by fww on 2019/3/29
 */
public class HttpConfig {
    public static String TGA = "HttpConfig";
    public static long CONNECT_TIMEOUT = 60L;
    public static long READ_TIMEOUT = 30L;
    public static long WRITE_TIMEOUT = 30L;

    /**
     * 获取OkHttpClient实例
     */
    public static OkHttpClient getOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                //.addInterceptor(new HeadRequestInterceptor())
                .addInterceptor(new VLogInterceptor())
                .build();
        return okHttpClient;
    }
}
