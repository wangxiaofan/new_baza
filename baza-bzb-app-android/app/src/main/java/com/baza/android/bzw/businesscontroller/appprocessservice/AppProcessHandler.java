package com.baza.android.bzw.businesscontroller.appprocessservice;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.constant.ConfigConst;
import com.baza.android.bzw.constant.RPCConst;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.slib.multiprocesssimpleconnect.RPCConnectManager;
import com.slib.multiprocesssimpleconnect.RPCConnectWrapper;

import org.json.JSONObject;

import java.util.HashSet;

import social.IShareCallBack;
import social.SocialHelper;
import social.data.ShareData;

/**
 * Created by Vincent.Lei on 2018/4/20.
 * Title：
 * Note：
 */
class AppProcessHandler {
    private void shareToQQ(boolean isQzone, final RPCConnectWrapper.RPCRequireHolder holder, final ShareData shareData) {
        if (shareData == null) {
            mRpcConnectManager.doRPCReply(holder, null);
            return;
        }
        IShareCallBack iShareCallBack = new IShareCallBack() {
            @Override
            public void onShareReply(boolean success, int errorCode, String errorMsg) {
                if (holder.needReplyAsync) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("success", errorCode == IShareCallBack.ERROR_CODE_COMPLETE);
                        jsonObject.put("errorMsg", errorCode == IShareCallBack.ERROR_CODE_QQ_NOT_INSTALL ? BZWApplication.getApplication().getString(R.string.qq_not_install) : null);
                        mRpcConnectManager.doRPCReply(holder, jsonObject.toString());
                    } catch (Exception e) {
                        //ignore
                    }
                }
            }
        };
        if (!isQzone)
            SocialHelper.getInstance().shareToQQ(BZWApplication.getApplication(), shareData, iShareCallBack);
        else {
            if (TextUtils.isEmpty(shareData.getShareLink()))
                shareData.setShareLink(ConfigConst.USER_RC_INDEX);
            SocialHelper.getInstance().shareToQZONE(BZWApplication.getApplication(), shareData, iShareCallBack);
        }
    }

    private void shareToWeChat(boolean isContact, final RPCConnectWrapper.RPCRequireHolder holder, ShareData shareData) {
        if (shareData == null) {
            mRpcConnectManager.doRPCReply(holder, null);
            return;
        }
        IShareCallBack iShareCallBack = new IShareCallBack() {
            @Override
            public void onShareReply(boolean success, int errorCode, String errorMsg) {
                int resultCode = 0;
                switch (errorCode) {
                    case IShareCallBack.ERROR_CODE_WECHAT_NOT_INSTALL:
                        resultCode = R.string.wechat_not_install;
                        break;
                    case IShareCallBack.ERROR_CODE_WECHAT_LEVEL_LOW:
                        resultCode = R.string.wechat_version_low;
                        break;
                }
                if (holder.needReplyAsync) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("success", errorCode == IShareCallBack.ERROR_CODE_COMPLETE);
                        jsonObject.put("errorMsg", resultCode != 0 ? BZWApplication.getApplication().getString(resultCode) : null);
                        mRpcConnectManager.doRPCReply(holder, jsonObject.toString());
                    } catch (Exception e) {
                        //ignore
                    }
                }
            }
        };
        if (isContact)
            SocialHelper.getInstance().shareToWeChatContact(BZWApplication.getApplication(), shareData, iShareCallBack);
        else
            SocialHelper.getInstance().shareToWeChatFriendCircle(BZWApplication.getApplication(), shareData, iShareCallBack);
    }


    private String getFastData(String dataName) {
        if (!TextUtils.isEmpty(dataName)) {
            if (RPCConst.DATA_NAME_RSA_UNION_ID.equals(dataName))
                return UserInfoManager.getInstance().getRsaUnionId();
        }
        return null;
    }
//    *****************************************************************************************************
//    *****************************************************************************************************
//    *****************************************************************************************************
//    *****************************************************************************************************
//    *****************************************************************************************************
//    *****************************************************************************************************
//    *****************************************************************************************************

    private RPCConnectManager mRpcConnectManager;
    private static final HashSet<String> mUIEventSet = new HashSet<>();

    static {
        mUIEventSet.add(RPCConst.ACTION_NAME_SHARE_TO_QQ);
        mUIEventSet.add(RPCConst.ACTION_NAME_SHARE_TO_QZONE);
        mUIEventSet.add(RPCConst.ACTION_NAME_SHARE_TO_WECHAT_CONTACT);
        mUIEventSet.add(RPCConst.ACTION_NAME_SHARE_TO_WECHAT_FC);
    }

    AppProcessHandler(RPCConnectManager mRpcConnectManager) {
        this.mRpcConnectManager = mRpcConnectManager;
    }

    boolean shouldRPCEventCostByUIThread(String requireId, String action) {
        return mUIEventSet.contains(action);
    }

    String onRPCConnectionAction(RPCConnectWrapper.RPCRequireHolder holder) {
        if (RPCConst.ACTION_NAME_FAST_GET_SIMPLE_DATA.equals(holder.action)) {
            return getFastData(holder.data);
        } else if (RPCConst.ACTION_NAME_SHARE_TO_QQ.equals(holder.action)) {
            ShareData shareData = parseShareData(ShareData.class, holder.data);
            shareToQQ(false, holder, shareData);
        }
        if (RPCConst.ACTION_NAME_SHARE_TO_QZONE.equals(holder.action)) {
            ShareData shareData = parseShareData(ShareData.class, holder.data);
            shareToQQ(true, holder, shareData);
        }
        if (RPCConst.ACTION_NAME_SHARE_TO_WECHAT_CONTACT.equals(holder.action)) {
            ShareData shareData = parseShareData(ShareData.class, holder.data);
            shareToWeChat(true, holder, shareData);
        }
        if (RPCConst.ACTION_NAME_SHARE_TO_WECHAT_FC.equals(holder.action)) {
            ShareData shareData = parseShareData(ShareData.class, holder.data);
            shareToWeChat(false, holder, shareData);
        }
        return null;
    }



    private static <T> T parseShareData(Class<T> dataClass, String data) {
        if (TextUtils.isEmpty(data))
            return null;
        return JSON.parseObject(data, dataClass);
    }

}
