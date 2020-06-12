package com.baza.android.bzw.dao;

import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.recommend.RecommendBean;
import com.baza.android.bzw.bean.recommend.RecommendListResultBean;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;
import com.slib.utils.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vincent.Lei on 2019/8/15.
 * Title：
 * Note：
 */
public class RecommendDao {
    public static void addRecommend(String isFirm, String resumeId, String content, Date date, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("resumeId", resumeId);
        param.put("remindTime", DateUtil.longMillions2FormatDate(date.getTime(), DateUtil.DEFAULT_API_FORMAT));
        param.put("content", content);
        param.put("isFirm", isFirm);
        HttpRequestUtil.doHttpPost(URLConst.URL_ADD_RECOMMEND, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
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

    public static void loadRecommendList(String startRemindTime, String endRemindTime, final IDefaultRequestReplyListener<List<RecommendBean>> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("startRemindTime", startRemindTime);
        param.put("endRemindTime", endRemindTime);
        HttpRequestUtil.doHttpPost(URLConst.URL_GET_RECOMMEND_LIST, param, RecommendListResultBean.class, new INetworkCallBack<RecommendListResultBean>() {
            @Override
            public void onSuccess(RecommendListResultBean recommendListResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, recommendListResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void setRecommendComplete(String reminderId, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("reminderId", reminderId);
        HttpRequestUtil.doHttpPost(URLConst.URL_SET_RECOMMEND_COMPLETE, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
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

    public static void deleteRecommend(String resumeId, String reminderId, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("resumeId", resumeId);
        param.put("reminderId", reminderId);
        HttpRequestUtil.doHttpPost(URLConst.URL_DELETE_RECOMMEND, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
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
}
