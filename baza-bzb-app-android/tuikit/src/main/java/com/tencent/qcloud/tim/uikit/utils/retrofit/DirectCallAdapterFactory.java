package com.tencent.qcloud.tim.uikit.utils.retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * 实现可以直接返回数据类的核心类
 */
public class DirectCallAdapterFactory extends CallAdapter.Factory {
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        final Type responseType = getResponseType(returnType);

        return new CallAdapter<Object, Object>() {

            public Type responseType() {
                return responseType;
            }

            public Object adapt(Call<Object> call) {
                // todo 可以在这里判断接口数据格式
                try {
                    Object object = call.execute().body();
                    if (object != null) {
                    }
                    return object;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    private Type getResponseType(Type type) {
        if (type instanceof WildcardType) {
            return ((WildcardType) type).getUpperBounds()[0];
        }
        return type;
    }

}