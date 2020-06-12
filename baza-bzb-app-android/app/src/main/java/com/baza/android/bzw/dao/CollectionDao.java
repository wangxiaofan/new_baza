package com.baza.android.bzw.dao;

import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;

import java.util.HashMap;

/**
 * Created by Vincent.Lei on 2017/6/6.
 * Title：
 * Note：
 */

public class CollectionDao {
    private CollectionDao() {
    }

    public static void doOrCancelCollection(ResumeBean resumeBean, final IDefaultRequestReplyListener<String> listener) {
        if (resumeBean == null)
            return;
        HashMap<String, String> param = new HashMap<>();
        param.put("candidateId", resumeBean.candidateId);
        //resumeBean.collectStatus 0未收藏  1已经收藏
        HttpRequestUtil.doHttpPost(resumeBean.collectStatus == CommonConst.COLLECTION_NO ? URLConst.URL_COLLECTION_RESUME : URLConst.URL_UN_COLLECTION_RESUME, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, null, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }
}
