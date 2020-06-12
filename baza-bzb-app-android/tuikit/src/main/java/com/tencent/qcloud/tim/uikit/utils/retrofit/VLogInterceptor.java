package com.tencent.qcloud.tim.uikit.utils.retrofit;

import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.URLConst;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Desc: 日志拦截器
 * Created by fww on 2019/3/29
 */
public class VLogInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request()
                .newBuilder()
                .addHeader("AuthorizationIMApp", Configs.imcenterToken)
                .addHeader("LoginedUserUnionId", Configs.unionId)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        //Response response = chain.proceed(request);

        //Request request = chain.request();
        String url = request.url().toString();
        String params = requestBodyToString(request.body());
        //Response response = chain.proceed(request);
        //String responseString = JsonHandleUtils.jsonHandle(response.body().string());
        //String time = DateUtils.getNowDateFormat(DateUtils.DATE_FORMAT_2);
        String log = "-------\n\n*******路径*******:\n" + url + "\n*******参数*******:\n" + params + "\n\n";
        return chain.proceed(request);
    }

    public String requestBodyToString(final RequestBody request) {
        try {
            RequestBody copy = request;
            Buffer buffer = new Buffer();
            if (copy != null) {
                copy.writeTo(buffer);
            } else {
                return "";
            }
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
