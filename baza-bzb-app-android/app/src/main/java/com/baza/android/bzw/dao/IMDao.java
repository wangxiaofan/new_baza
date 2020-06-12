package com.baza.android.bzw.dao;

import android.util.Log;

import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.bean.IMSearchBean;
import com.tencent.qcloud.tim.uikit.bean.IMSigBean;

import java.util.HashMap;

public class IMDao {
    private IMDao() {
    }

    public static void getSig(final IDefaultRequestReplyListener<IMSigBean> listener) {
        Log.e("herb", "签名获取开始>>");
        HashMap<String, String> header = new HashMap<>();
        header.put("AuthorizationIMApp", Configs.imcenterToken);
        header.put("LoginedUserUnionId", Configs.unionId);

        HttpRequestUtil.doHttpPost(URLConst.URL_GET_SIG, null, IMSigBean.class, new INetworkCallBack<IMSigBean>() {
            @Override
            public void onSuccess(IMSigBean bean) {
                Log.e("herb", "签名获取成功>>");
                if (listener != null)
                    listener.onRequestReply(true, bean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                Log.e("herb", "签名获取失败>>" + CustomerRequestAssistHandler.getErrorCode(object) +
                        ">>>" + CustomerRequestAssistHandler.getErrorMsg(object));
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        }, header);
    }
}
