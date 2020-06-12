package com.baza.android.bzw.manager;

import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.baza.android.bzw.bean.email.AddresseeBean;
import com.baza.android.bzw.bean.user.GrowBean;
import com.baza.android.bzw.bean.user.IllegalBean;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.bean.user.UserInfoResultBean;
import com.baza.android.bzw.businesscontroller.account.viewinterface.UserVerifyGuideActivity;
import com.baza.android.bzw.businesscontroller.im.IMUserInfoProvider;
import com.baza.android.bzw.businesscontroller.message.TabMessageFragment02;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.ConfigConst;
import com.baza.android.bzw.constant.SharedPreferenceConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.dao.AccountDao;
import com.baza.android.bzw.dao.CheckUpdateDao;
import com.baza.android.bzw.events.IDefaultEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.log.ReportAgent;
import com.bznet.android.rcbox.BuildConfig;
import com.bznet.android.rcbox.R;
import com.slib.storage.database.listener.IDBReplyListener;
import com.slib.storage.sharedpreference.SharedPreferenceManager;
import com.slib.utils.AppUtil;
import com.slib.utils.DialogUtil;
import com.slib.utils.RSAUtil;
import com.tencent.qcloud.tim.uikit.Configs;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;

import baza.dialog.simpledialog.MaterialDialog;

/**
 * Created by Vincent.Lei on 2017/5/12.
 * Title：用户信息管理
 * Note：
 */

public class UserInfoManager {
    private Application mApplication;
    private UserInfoBean mUserInfoData;
    private HashMap<String, String> mDefaultHeader;
    private boolean mIsAppOpened;
    private String mBaiDuAccessToken;
    private AddresseeBean mBindEmailData;
    private GrowBean mGrowInfo;
    private String mAuthorization;
    private String mRsaUnionId;
    private UserInfoBean.ExtraInfo mExtraInfo;
    private HashSet<String> mReadResumeCache;
    private HashMap<String, String> mIdCacheUnderMineTalent;
    private IllegalBean mIllegalInfo;
    private int mImMessageCountOfStranger = CommonConst.LIST_POSITION_NONE;
    private static UserInfoManager mInstance = new UserInfoManager();
    private boolean mIsUserVerifyDialogShowing;

    public static UserInfoManager getInstance() {
        return mInstance;
    }

    private UserInfoManager() {
    }

    public void init(Application application) {
        this.mApplication = application;
        initUserInfoData();
    }

    public UserInfoBean getUserInfo() {
        if (mUserInfoData == null)
            mUserInfoData = new UserInfoBean();
        return mUserInfoData;
    }

    public void cacheMineResumeIdUnderTargetTalent(String talentId, String resumeId) {
        if (talentId != null && resumeId != null) {
            if (mIdCacheUnderMineTalent == null)
                mIdCacheUnderMineTalent = new HashMap<>();
            mIdCacheUnderMineTalent.put(talentId, resumeId);
        }
    }

    public String getMineResumeIdUnderTargetTalent(String talentId) {
        return ((talentId != null && mIdCacheUnderMineTalent != null) ? mIdCacheUnderMineTalent.get(talentId) : null);
    }

    public void cacheReadResume(String resumeId) {
        if (resumeId != null) {
            if (mReadResumeCache == null)
                mReadResumeCache = new HashSet<>();
            mReadResumeCache.add(resumeId);
        }
    }

    public boolean isResumeReadByCurrentUser(String resumeId) {
        return (resumeId != null && mReadResumeCache != null && mReadResumeCache.contains(resumeId));
    }

    public boolean isUserSignIn() {
        return (mUserInfoData != null && mDefaultHeader != null && mUserInfoData.userId > 0 && !TextUtils.isEmpty(mAuthorization));
    }

    public boolean isIdentifyStatusOk() {
        return (mUserInfoData != null && mUserInfoData.channelVerifyStatus == CommonConst.VerifyStatus.VERIFY_SUCCESS);
    }

//    public boolean isNeedHintIdentify() {
//        return (mUserInfoData == null || mUserInfoData.channelVerifyStatus == CommonConst.VerifyStatus.VERIFY_NONE || mUserInfoData.channelVerifyStatus == CommonConst.VerifyStatus.VERIFY_FAILED);
//    }

    public boolean checkIdentifyStatusAndVerifyIfNeed(final Activity activity) {
        return checkIdentifyStatusAndVerifyIfNeed(activity, false, null);
    }

    public boolean checkIdentifyStatusAndVerifyIfNeed(final Activity activity, boolean directTurnRo) {
        return checkIdentifyStatusAndVerifyIfNeed(activity, directTurnRo, null);
    }

    public boolean checkIdentifyStatusAndVerifyIfNeed(final Activity activity, boolean directTurnRo, CharSequence msg) {
        if (activity == null || activity.isFinishing())
            return false;
        if (!isIdentifyStatusOk()) {
            if (directTurnRo) {
                UserVerifyGuideActivity.launch(activity);
                return false;
            }
            if (mIsUserVerifyDialogShowing)
                return false;
            mIsUserVerifyDialogShowing = true;
            final MaterialDialog materialDialog = DialogUtil.doubleButtonShow(activity, null, msg != null ? msg : activity.getResources().getString(R.string.hint_identify_for_advance_function), activity.getResources().getString(R.string.cancel), activity.getResources().getString(R.string.go_verify), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ToastUtil.showToast(activity, R.string.login_on_pc_to_identify);
                    UserVerifyGuideActivity.launch(activity);
                }
            }, null, true);

            materialDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    materialDialog.dismiss();
                    mIsUserVerifyDialogShowing = false;
                }
            });
            return false;
        }
        return true;
    }

    public String getAuthorization() {
        return mAuthorization;
    }

    public void saveUserExtraInfo(UserInfoBean.ExtraInfo extraInfo) {
        if (extraInfo != null) {
            this.mExtraInfo = extraInfo;
            SharedPreferenceManager.saveString(SharedPreferenceConst.SP_NAME_USER_EXTRA_INFO, AppUtil.objectToJson(mExtraInfo));
        }
    }

    public UserInfoBean.ExtraInfo getUserExtraInfo() {
        if (mExtraInfo == null)
            mExtraInfo = new UserInfoBean.ExtraInfo();
        return mExtraInfo;
    }

    public void saveUserInfo(UserInfoBean userInfo) {
        if (userInfo != null) {
            boolean isUserNotChange = (mUserInfoData != null && mUserInfoData.unionId != null && mUserInfoData.unionId.equals(userInfo.unionId));
            if (isUserNotChange && mUserInfoData.bbsUserName != null)
                userInfo.bbsUserName = mUserInfoData.bbsUserName;
            this.mUserInfoData = userInfo;
            SharedPreferenceManager.saveString(SharedPreferenceConst.SP_NAME_USER_INFO, AppUtil.objectToJson(mUserInfoData));
            SharedPreferenceManager.saveString(SharedPreferenceConst.SP_USER_PHONE, mUserInfoData.mobile);
            SharedPreferenceManager.saveString(SharedPreferenceConst.SP_USER_IMCENTER_TOKEN, Configs.imcenterToken);
            SharedPreferenceManager.saveString(SharedPreferenceConst.SP_USER_UNION_ID, Configs.unionId);
            IMUserInfoProvider.getInstance(mApplication).updateMySelfInfo(mUserInfoData);
            if (!isUserNotChange)
                ReportAgent.setUserInfo(mUserInfoData);
        }
    }

    public HashMap<String, String> getDefaultPassportHeader() {
        return mDefaultHeader;
    }

    public boolean isAppOpened() {
        return mIsAppOpened;
    }

    public void setAppOpenStatus(boolean isAppOpened) {
        this.mIsAppOpened = isAppOpened;
    }

    public String getDeviceInfo() {
        StringBuilder stringBuilder = new StringBuilder("client=android")
                .append(";osVersion=").append(Build.VERSION.RELEASE)
                .append(";appVersion=").append(BuildConfig.VERSION_NAME);
        DisplayMetrics displayMetrics = mApplication.getResources().getDisplayMetrics();
        stringBuilder.append(";screenWidth=").append(displayMetrics.widthPixels)
                .append(";screenHeight=").append(displayMetrics.heightPixels);
        return stringBuilder.toString();
    }

    private void initUserInfoData() {
        mDefaultHeader = new HashMap<>();
        mDefaultHeader.put(CommonConst.STR_AGENT_INFO, getDeviceInfo());
        mDefaultHeader.put(CommonConst.STR_SEC, CommonConst.STR_SEC_TAG);
        mDefaultHeader.put(CommonConst.STR_USER_AGENT, "BaZhuaHeZi-Android/" + URLConst.VERSION_NAME + " " + AppUtil.getUserAgent(mApplication));

        String loginDataStr = SharedPreferenceManager.getString(SharedPreferenceConst.SP_NAME_USER_INFO);
        if (!TextUtils.isEmpty(loginDataStr)) {
            mUserInfoData = JSON.parseObject(loginDataStr, UserInfoBean.class);
            String authorization = decryptAndBase64CidAndToken();
            if (authorization != null)
                setAuthorization(authorization);
        }
        String extraInfo = SharedPreferenceManager.getString(SharedPreferenceConst.SP_NAME_USER_EXTRA_INFO);
        Configs.imcenterToken = SharedPreferenceManager.getString(SharedPreferenceConst.SP_USER_IMCENTER_TOKEN);
        Configs.unionId = SharedPreferenceManager.getString(SharedPreferenceConst.SP_USER_UNION_ID);

        if (!TextUtils.isEmpty(extraInfo)) {
            mExtraInfo = JSON.parseObject(extraInfo, UserInfoBean.ExtraInfo.class);
        }
        ReportAgent.setUserInfo(mUserInfoData);
    }

    private void setAuthorization(String authorization) {
        LogUtil.d("authorization :" + authorization);
        mAuthorization = authorization;
    }

    public void saveUserPassportMsg(String cid, String token) {
        if (cid == null || token == null)
            return;
        String str = cid + ":" + token;
        String authorization = doBase64(CommonConst.STR_BASE64_SCHEME, str);
        SharedPreferenceManager.saveString(SharedPreferenceConst.SP_CID, cid);
        SharedPreferenceManager.saveString(SharedPreferenceConst.SP_TOKEN, token);
        setAuthorization(authorization);
    }

    private String decryptAndBase64CidAndToken() {
        String cid = SharedPreferenceManager.getString(SharedPreferenceConst.SP_CID);
        String token = SharedPreferenceManager.getString(SharedPreferenceConst.SP_TOKEN);
        if (TextUtils.isEmpty(cid) || TextUtils.isEmpty(token))
            return null;
        return doBase64(CommonConst.STR_BASE64_SCHEME, (cid + ":" + token));
    }


    private void clearUserData() {
        mUserInfoData = null;
        mBindEmailData = null;
        mGrowInfo = null;
        mAuthorization = null;
        mRsaUnionId = null;
        mExtraInfo = null;
        mReadResumeCache = null;
        mIdCacheUnderMineTalent = null;
        mIllegalInfo = null;
        mImMessageCountOfStranger = CommonConst.LIST_POSITION_NONE;
        SharedPreferenceManager.saveString(SharedPreferenceConst.SP_NAME_USER_INFO, null);
        SharedPreferenceManager.saveString(SharedPreferenceConst.SP_NAME_USER_EXTRA_INFO, null);
        SharedPreferenceManager.saveString(SharedPreferenceConst.SP_IM_USER_NAME, null);
        SharedPreferenceManager.saveString(SharedPreferenceConst.SP_IM_USER_TOKEN, null);
        SharedPreferenceManager.saveString(SharedPreferenceConst.SP_CID, null);
        SharedPreferenceManager.saveString(SharedPreferenceConst.SP_TOKEN, null);
        SharedPreferenceManager.saveString(SharedPreferenceConst.SP_USER_IMCENTER_TOKEN, null);
        SharedPreferenceManager.saveString(SharedPreferenceConst.SP_USER_UNION_ID, null);
        CheckUpdateDao.getInstance().destroyInfo();
        //清除日志中用户信息
        ReportAgent.clearUserInfo();
    }


    private static String doBase64(String scheme, String info) {
        String result = null;
        try {
            String base64Str = new String(Base64.encode(info.getBytes(), Base64.NO_WRAP), "utf-8");
            result = (scheme != null ? (scheme + base64Str) : base64Str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getBaiDuVoiceAccessToken() {
        return mBaiDuAccessToken;
    }

    public void setBaiDuVoiceAccessToken(String mBaiDuAccessToken) {
        this.mBaiDuAccessToken = mBaiDuAccessToken;
    }

    public AddresseeBean getBindEmailData() {
        return mBindEmailData;
    }

    public void setBindEmailData(AddresseeBean mBindEmailData) {
        this.mBindEmailData = mBindEmailData;
    }

//    public int getMineResumeCount() {
//        return mMineResumeCount;
//    }
//
//    public void setMineResumeCount(int mMineResumeCount) {
//        this.mMineResumeCount = mMineResumeCount;
//    }

    public GrowBean getGrowInfo() {
        return mGrowInfo;
    }

    public void setGrowInfo(GrowBean growInfo) {
        this.mGrowInfo = growInfo;
    }

    public String getRsaUnionId() {
        if (mRsaUnionId == null)
            try {
                mRsaUnionId = RSAUtil.encrypt(ConfigConst.API_PUBLIC_SIGN_KEY, mUserInfoData.unionId);
            } catch (Exception e) {
                //ignore
                e.printStackTrace();
            }
        return mRsaUnionId;
    }

    public void clearCurrentUserInfoWhenSignOut() {
        if (mUserInfoData != null && !TextUtils.isEmpty(mUserInfoData.neteaseId))
            IMManager.getInstance(mApplication).logout();
        clearUserData();
        LabelCacheManager.getInstance().clear();
        PushManager.getInstance().stopPush(mApplication);
    }

    public void updateUserInfo() {
//        checkToUpdateBBSUserName();
        AccountDao.readImMessageCountOfStanger(mUserInfoData.unionId, new IDBReplyListener<Integer>() {
            @Override
            public void onDBReply(Integer integer) {
                if (integer != null)
                    mImMessageCountOfStranger = integer;
            }
        });

        AccountDao.getUserInfo(new IDefaultRequestReplyListener<UserInfoResultBean>() {
            @Override
            public void onRequestReply(boolean success, UserInfoResultBean userInfoResultBean, int errorCode, String errorMsg) {
                if (success && userInfoResultBean.data != null) {
                    saveUserInfo(userInfoResultBean.data);
                    UIEventsObservable.getInstance().postEvent(IDefaultEventsSubscriber.class, ActionConst.ACTION_EVENT_ACCOUNT_INFO_CHANGED, null, null);
                }
            }
        });
    }

    public void updateUserIllegalWord() {
        AccountDao.loadIllegalWord(new IDefaultRequestReplyListener<IllegalBean>() {
            @Override
            public void onRequestReply(boolean success, IllegalBean data, int errorCode, String errorMsg) {
                if (success && data != null) {
                    mIllegalInfo = data;
                }
            }
        });
    }

    /**
     * 检测消息发送的非法单词
     */
    public boolean isTextMessageIllegal(String message) {
        if (message != null && mIllegalInfo != null && mIllegalInfo.word != null && mIllegalInfo.word.length > 0) {
            for (int i = 0; i < mIllegalInfo.word.length; i++) {
                if (message.contains(mIllegalInfo.word[i]))
                    return true;
            }
        }
        return false;
    }

    public boolean isCurrentMessageOverTimeLimit() {
        return (mIllegalInfo != null && mImMessageCountOfStranger != CommonConst.LIST_POSITION_NONE && mImMessageCountOfStranger > mIllegalInfo.limit);
    }

    public void updateImMessageCountOfStranger(int count) {
        mImMessageCountOfStranger += count;
        AccountDao.updateImMessageCountOfStanger(mUserInfoData.unionId, count);
    }

//    private void checkToUpdateBBSUserName() {
//        String name = SharedPreferenceManager.getString(SharedPreferenceConst.SP_BBS_USER_NAME + getUserInfo().userId);
//        if (!TextUtils.isEmpty(name))
//            updateBBSUserName(name);
//    }
//
//    public void createUpdateBBSUserNameTask(String name, boolean autoUpdate) {
//        SharedPreferenceManager.saveString(SharedPreferenceConst.SP_BBS_USER_NAME + getUserInfo().userId, name);
//        if (autoUpdate)
//            updateBBSUserName(name);
//    }
//
//    private void updateBBSUserName(String name) {
//        AccountDao.updateUserInfo(AccountDao.ACCOUNT_BBS_USER_NAME, name, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
//            @Override
//            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
//                if (success) {
//                    SharedPreferenceManager.saveString(SharedPreferenceConst.SP_BBS_USER_NAME + getUserInfo().userId, null);
//                }
//            }
//        });
//    }
}
