package com.tencent.qcloud.tim.uikit.utils.retrofit;

import android.accounts.NetworkErrorException;
import android.text.TextUtils;

import androidx.annotation.CallSuper;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Desc: 统一封装的Observer
 */
public abstract class VObserver<T> implements Observer<T> {
    private static final String TAG = "ATObserver";

    protected VObserver() {
    }

    @Override
    public void onSubscribe(Disposable d) {
        //请求开始

    }

//    @Override
////    public void onNext(BaseResponse<T> tBaseResponse) {
////        //根据业务来分
////        if (tBaseResponse.isSuccess()) {
////            onSuccess(tBaseResponse.getData());
////        } else {
////            //onFailure(tBaseResponse.getStatus(), tBaseResponse.getMsg(), true);
////            onFailure(0, "error", true);
////        }
////    }

    @Override
    public void onNext(T tBaseResponse) {
        //根据业务来分
        if (tBaseResponse != null) {
            onSuccess(tBaseResponse);
        } else {
            //onFailure(tBaseResponse.getStatus(), tBaseResponse.getMsg(), true);
            onFailure(0, "数据获取失败", true);
        }
    }

    @Override
    public void onError(Throwable e) {
        try {
            if (e instanceof ConnectException || e instanceof NetworkErrorException) {
                onFailure(StatusCode.ERROR_NONETWORK, "网络错误", true);
            } else if (e instanceof TimeoutException) {
                onFailure(StatusCode.ERROR_TIMEOUT, "请求超时", true);
            } else {
                onFailure(StatusCode.ERROR_EXCEPTION, "系统异常", true);
            }
        } catch (Exception e1) {
            onFailure(StatusCode.ERROR_EXCEPTION, e1.getMessage(), true);
        }
    }

    @Override
    public void onComplete() {
        //请求完成
    }

    /**
     * 返回成功
     *
     * @param t 返回体
     */
    protected abstract void onSuccess(T t);

    /**
     * 返回失败  子类覆盖时必须super
     * 可对通用问题进行拦截
     *
     * @param code      错误码
     * @param message   消息
     * @param isShowMsg 是否显示错误消息
     */
    @CallSuper
    protected void onFailure(int code, String message, boolean isShowMsg) {
        if (!TextUtils.isEmpty(message) && isShowMsg) {

        }
        //判断状态码 进行拦截
    }
}

