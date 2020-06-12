package com.tencent.qcloud.tim.uikit.utils.retrofit;


import com.tencent.qcloud.tim.uikit.URLConst;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 辅助类
 */

public class RetrofitHelper {
    private static String TGA = "RetrofitHelper";
    private volatile static RetrofitHelper mInstance = null;
    private Retrofit mRetrofit = null;

    public static RetrofitHelper getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitHelper.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitHelper();
                }
            }
        }
        return mInstance;
    }

    private RetrofitHelper() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new EncodingInterceptor("UTF-8"))
                .addInterceptor(new VLogInterceptor())
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(URLConst.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public URLConst getServer() {
        return mRetrofit.create(URLConst.class);
    }

    public <T> T getServer(Class<T> service) {
        return mRetrofit.create(service);
    }

}
