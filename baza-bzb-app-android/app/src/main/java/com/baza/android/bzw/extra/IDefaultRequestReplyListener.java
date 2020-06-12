package com.baza.android.bzw.extra;

/**
 * Created by Vincent.Lei on 2016/11/21.
 * Title : 默认http回到到Presenter接口
 * Note :
 */

public interface IDefaultRequestReplyListener<T> {
    void onRequestReply(boolean success, T t, int errorCode, String errorMsg);
}
