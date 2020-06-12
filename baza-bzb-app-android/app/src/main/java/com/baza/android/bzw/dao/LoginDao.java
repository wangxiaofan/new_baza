package com.baza.android.bzw.dao;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.common.CompanySuggestBean;
import com.baza.android.bzw.bean.user.LoginBean;
import com.baza.android.bzw.bean.user.LoginResultBean;
import com.baza.android.bzw.bean.user.WeChatLoginResultBean;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.LogUtil;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;
import com.slib.utils.AppUtil;

import java.util.HashMap;

/**
 * Created by Vincent.Lei on 2017/5/26.
 * Title：
 * Note：
 */

public class LoginDao {
    private LoginDao() {
    }

    public static void getSmSCode(String phone, final IDefaultRequestReplyListener<String> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("mobile", phone);
        CustomerRequestAssistHandler.wrapperSmsCodeParam(param);
        HttpRequestUtil.doHttpPost(URLConst.URL_SEND_SMS_CODE, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
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

    public static void login(String mobile, String code, String sessionId, final IDefaultRequestReplyListener<LoginResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("client", "android");
        param.put("mobile", mobile);
        param.put("code", code);
        if (sessionId != null)
            param.put("sessionId", sessionId);
        param.put("channel", AppUtil.getApplicationMetaData(BZWApplication.getApplication(), "UMENG_CHANNEL"));
        HttpRequestUtil.doHttpPost(URLConst.URL_LOGIN, param, LoginResultBean.class, new INetworkCallBack<LoginResultBean>() {
            @Override
            public void onSuccess(LoginResultBean loginDataBean) {
                if (listener != null) {
                    if (loginDataBean.data != null && loginDataBean.data.user != null && !TextUtils.isEmpty(loginDataBean.data.cid)) {
                        listener.onRequestReply(true, loginDataBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
                    } else {
                        listener.onRequestReply(false, loginDataBean, CustomerRequestAssistHandler.getErrorCode(loginDataBean), CustomerRequestAssistHandler.getErrorMsg(loginDataBean));
                    }
                }
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));

            }
        });
    }

    public static void getAllCompany(String name, int size, final IDefaultRequestReplyListener<CompanySuggestBean> listener) {
        HashMap<String, String> mCompanyParam = new HashMap<>(2);
        mCompanyParam.put("size", String.valueOf(size));
        mCompanyParam.put("name", name);
        HttpRequestUtil.doHttpPost(URLConst.URL_GET_ALL_COMPANY, mCompanyParam, CompanySuggestBean.class, new INetworkCallBack<CompanySuggestBean>() {
            @Override
            public void onSuccess(CompanySuggestBean companySuggestBean) {
                if (listener != null)
                    listener.onRequestReply(true, companySuggestBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loginWithWeChat(String code, final IDefaultRequestReplyListener<LoginResultBean> listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("client", "android");
        map.put("type", "1");
        map.put("channel", AppUtil.getApplicationMetaData(BZWApplication.getApplication(), "UMENG_CHANNEL"));
        HttpRequestUtil.doHttpPost(URLConst.URL_WECHAT_LOGIN, map, WeChatLoginResultBean.class, new INetworkCallBack<WeChatLoginResultBean>() {
            @Override
            public void onSuccess(WeChatLoginResultBean weChatLoginResultBean) {
                if (listener == null)
                    return;
                LoginResultBean loginResultBean = new LoginResultBean();
                loginResultBean.code = weChatLoginResultBean.code;
                loginResultBean.msg = weChatLoginResultBean.msg;
                try {
                    loginResultBean.data = JSON.parseObject(weChatLoginResultBean.data, LoginBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loginResultBean.data != null && loginResultBean.data.user != null && !TextUtils.isEmpty(loginResultBean.data.cid)) {
                    listener.onRequestReply(true, loginResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
                } else {
                    listener.onRequestReply(false, loginResultBean, CustomerRequestAssistHandler.getErrorCode(loginResultBean), CustomerRequestAssistHandler.getErrorMsg(loginResultBean));
                }
            }

            @Override
            public void onFailed(Object object) {
                if (object != null && object instanceof WeChatLoginResultBean) {
                    WeChatLoginResultBean weChatLoginResultBean = (WeChatLoginResultBean) object;
                    if (weChatLoginResultBean.code == CustomerRequestAssistHandler.NET_REQUEST_CELLPHONE_NOT_BIND_ERROR) {
                        LoginResultBean loginResultBean = new LoginResultBean();
                        loginResultBean.code = weChatLoginResultBean.code;
                        loginResultBean.msg = weChatLoginResultBean.msg;
                        loginResultBean.data = new LoginBean();
                        loginResultBean.data.unionid = weChatLoginResultBean.data;
                        listener.onRequestReply(false, loginResultBean, CustomerRequestAssistHandler.NET_REQUEST_CELLPHONE_NOT_BIND_ERROR, CustomerRequestAssistHandler.getErrorMsg(weChatLoginResultBean));
                        return;
                    }
                }
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));

            }
        });
    }

    public static void bindMobile(String phone, String smsCode, String unionid, final IDefaultRequestReplyListener<LoginResultBean> listener) {
        LogUtil.d("unionid = " + unionid);
        HashMap<String, String> map = new HashMap<>();
        map.put("mobile", phone);
        map.put("code", smsCode);
        map.put("client", "android");
        map.put("unionid", unionid);
        map.put("channel", AppUtil.getApplicationMetaData(BZWApplication.getApplication(), "UMENG_CHANNEL"));
        HttpRequestUtil.doHttpPost(URLConst.URL_BIND_MOBILE, map, LoginResultBean.class, new INetworkCallBack<LoginResultBean>() {
            @Override
            public void onSuccess(LoginResultBean loginDataBean) {
                if (listener != null) {
                    if (loginDataBean.data != null && loginDataBean.data.user != null && !TextUtils.isEmpty(loginDataBean.data.cid)) {
                        listener.onRequestReply(true, loginDataBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
                    } else {
                        listener.onRequestReply(false, loginDataBean, CustomerRequestAssistHandler.getErrorCode(loginDataBean), CustomerRequestAssistHandler.getErrorMsg(loginDataBean));
                    }
                }

            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));

            }
        });
    }

    public static void qrcodeLogin(String id, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>(1);
        param.put("id", id);
        HttpRequestUtil.doHttpPost(URLConst.URL_QR_CODE_LOGIN, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
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
