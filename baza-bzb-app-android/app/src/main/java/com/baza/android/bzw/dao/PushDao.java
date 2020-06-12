package com.baza.android.bzw.dao;

import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.push.NoticeUnreadResultBean;
import com.baza.android.bzw.bean.push.PushListResultBean;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;

import java.util.HashMap;

/**
 * Created by Vincent.Lei on 2017/6/21.
 * Title：
 * Note：
 */

public class PushDao {

    private PushDao() {
    }

    public static void loadBazaHelperMsg(int offset, final IDefaultRequestReplyListener<PushListResultBean> listener) {
        loadNoticeMessage(offset, "[2]", listener);
    }

    public static void loadSystemMsg(int offset, final IDefaultRequestReplyListener<PushListResultBean> listener) {
        loadNoticeMessage(offset, "[1,3]", listener);
    }

    private static void loadNoticeMessage(int offset, String notificationTypeList, final IDefaultRequestReplyListener<PushListResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("offset", String.valueOf(offset));
        param.put("pageSize", String.valueOf(CommonConst.DEFAULT_PAGE_SIZE));
        param.put("notificationTypeList", notificationTypeList);
        HttpRequestUtil.doHttpPost(URLConst.URL_GET_ALL_PUSH_MESSAGE, param, PushListResultBean.class, new INetworkCallBack<PushListResultBean>() {
            @Override
            public void onSuccess(PushListResultBean pushListResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, pushListResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static final int BAZA_HELPER = 1;
    public static final int SYSTEM_NOTICE = 2;

    public static void markAllNoticeRead(int type, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>(1);
        param.put("notificationTypeList", type == BAZA_HELPER ? "[2]" : "[1,3]");
        HttpRequestUtil.doHttpPost(URLConst.URL_MARK_NOTIFY_READ, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadNoticeUnreadCount(final IDefaultRequestReplyListener<NoticeUnreadResultBean.Data> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_NOTICE_UN_READ_COUNT, null, NoticeUnreadResultBean.class, new INetworkCallBack<NoticeUnreadResultBean>() {
            @Override
            public void onSuccess(NoticeUnreadResultBean noticeUnreadResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, noticeUnreadResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }
}
