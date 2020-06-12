package com.tencent.qcloud.tim.uikit.utils.retrofit;

import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.URLConst;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Desc: 请求头拦截器
 */
public class HeadRequestInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader("AuthorizationIMApp", Configs.imcenterToken)
                .addHeader("LoginedUserUnionId", Configs.unionId)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = chain.proceed(request);
        return response;
    }
}
