package com.slib.http;

/**
 * 网络请求回调
 * onFailed(Object object) 错误信息需要自定义
 *
 * @param <T>
 */
public interface INetworkCallBack<T> {
    void onSuccess(T t);

    void onFailed(Object object);
}
