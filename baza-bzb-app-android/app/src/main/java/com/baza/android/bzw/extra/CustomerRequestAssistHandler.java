package com.baza.android.bzw.extra;

import android.content.res.Resources;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.baza.android.bzw.manager.AppGlobalManager;
import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.ConfigConst;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.BuildConfig;
import com.bznet.android.rcbox.R;
import com.slib.http.IRequestAssistHandler;
import com.slib.utils.DateUtil;
import com.slib.utils.RSAUtil;
import com.slib.utils.string.MD5;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Vincent.Lei on 2017/5/9.
 * Title：
 * Note：
 */

public class CustomerRequestAssistHandler implements IRequestAssistHandler {
    public static final long CONNECT_TIME_OUT = 5000;
    public static final long READ_TIME_OUT = 15000;
    public static final long WRITE_TIME_OUT = 10000;
    private static final boolean IS_SIGN_AUTH_HEADER = BuildConfig.IS_SIGN_AUTH_HEADER;
    /*
     * 网络请求相关code
     */
    //未登录
    public static final int NET_ERROR_CODE_NOT_LOGGED_IN = 7;
    //登录信息过期
    public static final int NET_REQUEST_LOGGED_EXPIRED = 8;
    //连接错误码
    public static final int NET_REQUEST_NO_RESPONSE = -1;
    //正常网络请求
    public static final int NET_REQUEST_OK = 0;
    //3020-邮箱账号或密码错误
    public static final int NET_REQUEST_EMAIL_PASSWORD_ERROR = 3020;
    //3021-账户信息不完整
    public static final int NET_REQUEST_EMAIL_MSG_ERROR = 3021;
    //全部未知类型错误
//    public static final int NET_REQUEST_COMMON_ERROR = 9999;
    //服务暂时不可用
    public static final int NET_REQUEST_NOT_SERVICE_ERROR = 1;
    //简历已经被删除
    public static final int NET_REQUEST_RESUME_DELETED_ERROR = 3003;
    //简历已经存在
    public static final int NET_REQUEST_RESUME_EXIST_ERROR = 3002;
    //手机未绑定
    public static final int NET_REQUEST_CELLPHONE_NOT_BIND_ERROR = 4101;
    //忽略红包错误
    public static final int NET_REQUEST_IGNORE_RED_PACKAGE_ERROR = 7005;
    //今日更新额度不足
    public static final int NET_REQUEST_UPDATE_AMOUNT_NOT_ENOUGH_ERROR = 8000;
    //求分享已处理
    public static final int NET_SHARE_REQUEST_HAS_PROCESSED = 8010;
    //求分享额度不足
    public static final int NET_REQUEST_SHARE_AMOUNT_NOT_ENOUGH = 6100;
    //TBD错误
    public static final int NET_REQUEST_TDB_ERROR = 1013;
    //用户验证账号过度使用
    public static final int NET_REQUEST_CAN_NOT_VERIFY_THIS_USER_ANY_MORE = 11013;
    //用户未验证，行为受限制
    public static final int NET_ERROR_NOT_IDENTIFY = 11014;
    public static final int NET_ERROR_SHARE_TO_MUCH = 8017;

    public static final class NetRequestType {
        public static final int TYPE_NORMAL = 0;
        public static final int TYPE_REPLY_REQUEST_SHARE = 1;
    }

    @Override
    public boolean isResultOK(Object result) {
        BaseHttpResultBean bm = null;
        try {
            if (result != null)
                bm = (BaseHttpResultBean) result;
        } catch (Exception e) {
            return true;
        }
        if (bm == null)
            return false;
        if (bm.code == NET_REQUEST_OK)
            return true;
        processSpecialError(bm.code);
        return false;
    }

    private static PublicKey mPublicKey;

    @Override
    public HashMap<String, String> getDefaultHeader() {
        HashMap<String, String> header = UserInfoManager.getInstance().getDefaultPassportHeader();
        if (header == null)
            return null;
        String authorizationUnSign = UserInfoManager.getInstance().getAuthorization();
        if (!IS_SIGN_AUTH_HEADER) {
            if (!TextUtils.isEmpty(authorizationUnSign))
                header.put(CommonConst.STR_AUTHORIZATION, authorizationUnSign);
            return header;
        }
        if (!TextUtils.isEmpty(authorizationUnSign)) {
            try {
                if (mPublicKey == null)
                    mPublicKey = RSAUtil.getPublicKey(ConfigConst.API_PUBLIC_SIGN_KEY);
                header.put(CommonConst.STR_AUTHORIZATION, RSAUtil.encrypt(mPublicKey, authorizationUnSign + System.currentTimeMillis()));
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d("rsa sign failed");
            }
        }
        return header;
    }

    @Override
    public String processFileLoadError(String url, String error) {
        //下载任务出现错误 解析errorMsg
        BaseHttpResultBean baseHttpRequestBean = null;
        try {
            baseHttpRequestBean = JSON.parseObject(error, BaseHttpResultBean.class);
        } catch (Exception e) {
            //ignore
        }
        if (baseHttpRequestBean != null) {
            processSpecialError(baseHttpRequestBean.code);
            return baseHttpRequestBean.msg;
        }
        return error;
    }

    private void processSpecialError(int errorCode) {
        if (errorCode == NET_ERROR_CODE_NOT_LOGGED_IN || errorCode == NET_REQUEST_LOGGED_EXPIRED) {
            //登录无效
            AppGlobalManager.getInstance().forceLogoutForSinglePlatform();
            return;
        }
        if (errorCode == NET_ERROR_NOT_IDENTIFY) {
            AppGlobalManager.getInstance().forceIdentify();
        }
    }

    @Override
    public boolean checkLoadSourceIsFile(String targetUrl, String contentType) {
        return !(TextUtils.isEmpty(contentType) || contentType.startsWith("application/json"));
    }

    public static String getErrorMsg(Object error) {
        return getErrorMsg(error, NetRequestType.TYPE_NORMAL);
    }

    public static String getErrorMsg(Object error, int requestType) {
        if (BZWApplication.getApplication() == null)
            return null;
        if (error == null)
            return BZWApplication.getApplication().getResources().getString(R.string.network_no_response);
        if (error instanceof BaseHttpResultBean) {
            BaseHttpResultBean bm = (BaseHttpResultBean) error;
            return getTargetErrorMsg(bm, BZWApplication.getApplication().getResources(), requestType, bm.code);
        }
        return error.toString();
    }

    private static String getTargetErrorMsg(BaseHttpResultBean resultBean, Resources resources, int type, int errorCode) {
        switch (errorCode) {
            case NET_REQUEST_NOT_SERVICE_ERROR:
            case NET_REQUEST_TDB_ERROR:
                resultBean.msg = resources.getString(R.string.network_not_service_error);
                break;
//            case NET_REQUEST_RESUME_DELETED_ERROR:
//                if (type == NetRequestType.TYPE_REPLY_REQUEST_SHARE)
//                    resultBean.msg = resources.getString(R.string.network_candidate_deleted_error_b);
//                else
//                    resultBean.msg = resources.getString(R.string.network_candidate_deleted_error);
//                break;
            case NET_REQUEST_RESUME_EXIST_ERROR:
                if (type == NetRequestType.TYPE_REPLY_REQUEST_SHARE)
                    resultBean.msg = resources.getString(R.string.network_candidate_reply_exist_error);
                else
                    resultBean.msg = resources.getString(R.string.network_candidate_exist_error);
                break;
            case NET_REQUEST_CELLPHONE_NOT_BIND_ERROR:
                resultBean.msg = resources.getString(R.string.network_cellphone_not_bind_error);
                break;
            case NET_REQUEST_IGNORE_RED_PACKAGE_ERROR:
                resultBean.msg = null;
                break;

        }
        return resultBean.msg;
    }

    public static int getErrorCode(Object error) {
        if (error == null)
            return NET_REQUEST_NO_RESPONSE;
        if (error instanceof BaseHttpResultBean) {
            BaseHttpResultBean bm = (BaseHttpResultBean) error;
            return bm.code;
        }
        return NET_REQUEST_NO_RESPONSE;
    }

    public static void wrapperSmsCodeParam(Map<String, String> param) {
        if (param == null)
            return;
        String p1 = UUID.randomUUID().toString();
        String salt = "BaZhuaHeZi-" + DateUtil.longMillions2FormatDate(System.currentTimeMillis(), DateUtil.SDF_YMD_H);
        String p2 = MD5.getStringMD5(p1 + salt);
        param.put("p1", p1);
        param.put("p2", p2);
    }
}
