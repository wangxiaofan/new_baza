package com.baza.android.bzw.businesscontroller.login.presenter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.user.LoginResultBean;
import com.baza.android.bzw.businesscontroller.login.viewinterface.ILoginView;
import com.baza.android.bzw.businesscontroller.message.TabMessageFragment02;
import com.baza.android.bzw.businesscontroller.publish.presenter.CutDownPresenter;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.SharedPreferenceConst;
import com.baza.android.bzw.dao.LoginDao;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.AppGlobalManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.slib.storage.sharedpreference.SharedPreferenceManager;
import com.slib.utils.AppUtil;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.URLConst;

import social.ISocialLoginCallBack;
import social.SocialLoginHelper;

/**
 * Created by Vincent.Lei on 2017/5/11.
 * Title：登录逻辑控制器
 * Note：
 */

public class LoginPresenter extends BasePresenter {
    private ILoginView mLoginView;
    private int mSmsCodeRequireId;//过滤多次请求 请求回调所携带的requestId==mSmsCodeRequireId 本次网络请求才有效
    private int mLoginRequireId;//过滤多次请求 请求回调所携带的requestId==mLoginRequireId 本次网络请求才有效
    private int mAmountSecond = -1;//距离下一次请求验证吗间隔
    private CutDownPresenter mCutDownPresenter;
    private Runnable mRunnableSendSmsCodeTimerCut;
    private Handler mHandler;
    private String mPhone, mSmsCode, mSecurityCode;

    public LoginPresenter(ILoginView loginView) {
        this.mLoginView = loginView;
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void initialize() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mLoginView.callPreviousUserView(SharedPreferenceManager.getString(SharedPreferenceConst.SP_USER_PHONE));
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mCutDownPresenter != null)
            mCutDownPresenter.stop();
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    public void getSmSCode() {
        String phone = mLoginView.callGetUserName();
        if (!AppUtil.checkPhone(phone)) {
            mLoginView.callUpdateUserNameErrorView(true);
            return;
        }
        ++mSmsCodeRequireId;
        requireSmSCode(mSmsCodeRequireId, phone);
    }

    /**
     * 请求沿验证码返回
     *
     * @param requestId 本次请求id
     */
    private void requireSmSCode(final int requestId, String phone) {
        mLoginView.callShowProgress(null, true);
        LoginDao.getSmSCode(phone, new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(boolean success, String s, int errorCode, String errorMsg) {
                mLoginView.callCancelProgress();
                if (mSmsCodeRequireId != requestId)
                    return;
                mLoginView.callOnSendSmsCodeReply(success, errorMsg);
                if (success)
                    scheduleNextSendSmCodeTask();
            }
        });
    }

    public boolean checkIsEnableLogin() {
        mPhone = mLoginView.callGetUserName();
        mSmsCode = mLoginView.callGetSmsCode();
        if (!AppUtil.checkPhone(mPhone)) {
            mLoginView.callUpdateUserNameErrorView(true);
            return false;
        }
        if (mSmsCode == null || mSmsCode.length() < 6) {
            mLoginView.callUpdateSmsCodeErrorView(true);
            return false;
        }
        return true;
    }

    public void securitySuccessAndLogin(String code) {
        mSecurityCode = code;
        doLogin();
    }

    private void doLogin() {
        if (checkIsEnableLogin()) {
            mLoginRequireId++;
            final int requestId = mLoginRequireId;
            mLoginView.callShowProgress(null, true);
            LoginDao.login(mPhone, mSmsCode, mSecurityCode, new IDefaultRequestReplyListener<LoginResultBean>() {
                @Override
                public void onRequestReply(boolean success, LoginResultBean loginResultBean, int errorCode, String errorMsg) {
                    if (requestId != mLoginRequireId)
                        return;
                    afterLogin(success, loginResultBean, errorCode, errorMsg);
                }
            });
        }
    }

    public void afterLogin(boolean success, LoginResultBean loginResultBean, int errorCode, String errorMsg) {
        mLoginView.callCancelProgress();
        if (success) {
            Configs.imcenterToken = loginResultBean.data.imcenterToken;
            Configs.unionId = loginResultBean.data.user.unionId;
            Log.e("herb", "imcenterToken>>" + loginResultBean.data.imcenterToken);
            Log.e("herb", "unionId>>" + loginResultBean.data.user.unionId);

            UserInfoManager.getInstance().saveUserPassportMsg(loginResultBean.data.cid, loginResultBean.data.token);
            UserInfoManager.getInstance().saveUserInfo(loginResultBean.data.user);
            UIEventsObservable.getInstance().postEvent(ActionConst.ACTION_EVENT_LOGIN, null);
            AppGlobalManager.getInstance().prepareDelayInitData(AppGlobalManager.DelayInitTask.TYPE_USER_LOGIN, new AppGlobalManager.IAppDelayInitTaskListener() {
                @Override
                public void onApplicationDelayInitComplete() {
                    BZWApplication.continueLaunch(mLoginView.callGetBindActivity());
                }
            });
            return;
        }
        if (errorCode == CustomerRequestAssistHandler.NET_REQUEST_CELLPHONE_NOT_BIND_ERROR) {
            //手机未绑定 去绑定手机
            LogUtil.d("unionid = " + loginResultBean.data.unionid);
            mLoginView.callBindMobile(loginResultBean.data.unionid);
            return;
        }
        mLoginView.callShowToastMessage(errorMsg, 0);
    }

    /**
     * 启动下次可以请求验证码的计时器
     */
    private void scheduleNextSendSmCodeTask() {
        if (mCutDownPresenter == null)
            mCutDownPresenter = new CutDownPresenter(CommonConst.SMS_CODE_DEPART_TIME, new CutDownPresenter.ICallBack() {
                @Override
                public void onTimeCut(int amountSecond) {
                    LoginPresenter.this.mAmountSecond = amountSecond;
                    mHandler.post(mRunnableSendSmsCodeTimerCut);
                }
            });
        if (mRunnableSendSmsCodeTimerCut == null)
            mRunnableSendSmsCodeTimerCut = new Runnable() {
                @Override
                public void run() {
                    mLoginView.callUpdateSendSmsCodeTextViewStatus(mAmountSecond);
                }
            };
        mCutDownPresenter.start();
    }

    public void doWeChatLogin() {
        SocialLoginHelper.getInstance().loginWithWeChat(mLoginView.callGetBindActivity(), new ISocialLoginCallBack() {
            @Override
            public void onSocialLoginResult(boolean success, int resultCode, String code) {
                if (success && code != null) {
                    loginWithWeChat(code);
                    return;
                }
                switch (resultCode) {
                    case ISocialLoginCallBack.ERROR_CODE_WECHAT_NOT_INSTALL:
                        mLoginView.callShowToastMessage(null, R.string.wechat_not_install);
                        break;
                    default:
                        mLoginView.callShowToastMessage(null, R.string.wechat_auth_failed);
                        break;
                }
            }
        });
    }

    private void loginWithWeChat(String code) {
        ++mLoginRequireId;
        mLoginView.callShowProgress(null, true);
        final int requestId = mLoginRequireId;
        LoginDao.loginWithWeChat(code, new IDefaultRequestReplyListener<LoginResultBean>() {
            @Override
            public void onRequestReply(boolean success, LoginResultBean loginResultBean, int errorCode, String errorMsg) {
                if (requestId != mLoginRequireId)
                    return;
                afterLogin(success, loginResultBean, errorCode, errorMsg);
            }
        });
    }
}
