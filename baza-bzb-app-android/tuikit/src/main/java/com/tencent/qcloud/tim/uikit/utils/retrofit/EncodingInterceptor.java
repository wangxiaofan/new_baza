package com.tencent.qcloud.tim.uikit.utils.retrofit;

import java.io.IOException;
import java.lang.reflect.Field;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * <pre>
 *     time   : 2019/05/23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class EncodingInterceptor implements Interceptor {


    /**
     * 自定义编码
     */
    private String encoding;

    public EncodingInterceptor(String encoding) {
        this.encoding = encoding;
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        settingClientCustomEncoding(response);
        return response;
    }

    /**
     * setting client custom encoding when server not return encoding
     * @param response
     * @throws IOException
     */
    private void settingClientCustomEncoding(Response response) throws IOException {
        setBodyContentType(response);

    }


    /**
     * set body contentType
     * @param response
     * @throws IOException
     */
    private void setBodyContentType(Response response) throws IOException {
        ResponseBody body = response.body();
        // setting body contentTypeString using reflect
        Class<? extends ResponseBody> aClass = body.getClass();
        try {
            Field field = aClass.getDeclaredField("contentTypeString");
            field.setAccessible(true);
            String contentTypeString = String.valueOf(field.get(body));
            field.set(body, "application/rss+xml;charset=" + encoding);
            //field.set(body, "application/form-data;charset=" + encoding);

        } catch (NoSuchFieldException e) {
            throw new IOException("use reflect to setting header occurred an error", e);
        } catch (IllegalAccessException e) {
            throw new IOException("use reflect to setting header occurred an error", e);
        }
    }

}