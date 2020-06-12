package com.baza.android.bzw.businesscontroller.login.presenter;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.baza.android.bzw.bean.user.LoginResultBean;
import com.baza.android.bzw.dao.LoginDao;
import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.businesscontroller.publish.presenter.CutDownPresenter;
import com.baza.android.bzw.businesscontroller.login.viewinterface.IBindMobileView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/8/7.
 * Title：
 * Note：
 */

public class BindMobilePresenter extends BasePresenter {
    private IBindMobileView mBindMobileView;
    private CutDownPresenter mCutDownPresenter;
    private Runnable mRunnableSendSmsCodeTimerCut;
    private Handler mHandler;
    private int mAmountSecond = -1;//距离下一次请求验证吗间隔
    private String mUnionid;

    public BindMobilePresenter(IBindMobileView bindMobileView, Intent intent) {
        this.mBindMobileView = bindMobileView;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mUnionid = intent.getStringExtra("unionid");
    }

    @Override
    public void initialize() {

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
        String phone = mBindMobileView.callGetUserName();
        if (!AppUtil.checkPhone(phone)) {
            mBindMobileView.callShowToastMessage(null, R.string.input_username_correctly);
            return;
        }
        mBindMobileView.callShowProgress(null, true);
        LoginDao.getSmSCode(phone, new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(boolean success, String s, int errorCode, String errorMsg) {
                mBindMobileView.callCancelProgress();
                if (success) {
                    scheduleNextSendSmCodeTask();
                    return;
                }
                mBindMobileView.callShowToastMessage(errorMsg, 0);
            }
        });
    }


    /**
     * 启动下次可以请求验证码的计时器
     */
    private void scheduleNextSendSmCodeTask() {
        if (mCutDownPresenter == null)
            mCutDownPresenter = new CutDownPresenter(CommonConst.SMS_CODE_DEPART_TIME, new CutDownPresenter.ICallBack() {
                @Override
                public void onTimeCut(int amountSecond) {
                    mAmountSecond = amountSecond;
                    mHandler.post(mRunnableSendSmsCodeTimerCut);
                }
            });
        if (mRunnableSendSmsCodeTimerCut == null)
            mRunnableSendSmsCodeTimerCut = new Runnable() {
                @Override
                public void run() {
                    mBindMobileView.callUpdateSendSmsCodeTextViewStatus(mAmountSecond);
                }
            };
        mCutDownPresenter.start();
    }

    public void bindMobile() {
        String phone = mBindMobileView.callGetUserName();
        String smsCode = mBindMobileView.callGetSmsCode();
        if (!AppUtil.checkPhone(phone)) {
            mBindMobileView.callShowToastMessage(null, R.string.input_username_correctly);
            return;
        }
        if (smsCode == null || smsCode.length() < 4) {
            mBindMobileView.callShowToastMessage(null, (TextUtils.isEmpty(smsCode) ? R.string.smsCode_not_null : R.string.smsCode_length_error));
            return;
        }
        mBindMobileView.callShowProgress(null, true);
        LoginDao.bindMobile(phone, smsCode, mUnionid, new IDefaultRequestReplyListener<LoginResultBean>() {
            @Override
            public void onRequestReply(boolean success, LoginResultBean loginResultBean, int errorCode, String errorMsg) {
                mBindMobileView.callCancelProgress();
                if (success) {
                    mBindMobileView.callSetBack(loginResultBean);
                    return;
                }
                mBindMobileView.callShowToastMessage(errorMsg, 0);
            }
        });
    }
}
