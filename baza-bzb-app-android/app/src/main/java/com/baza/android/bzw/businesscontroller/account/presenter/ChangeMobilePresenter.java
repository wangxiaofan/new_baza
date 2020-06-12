package com.baza.android.bzw.businesscontroller.account.presenter;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.baza.android.bzw.manager.AppGlobalManager;
import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IChangeMobileView;
import com.baza.android.bzw.businesscontroller.publish.presenter.CutDownPresenter;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.SharedPreferenceConst;
import com.baza.android.bzw.dao.AccountDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.manager.UserInfoManager;
import com.slib.storage.sharedpreference.SharedPreferenceManager;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2018/1/4.
 * Title：
 * Note：
 */

public class ChangeMobilePresenter extends BasePresenter {
    private IChangeMobileView mChangeMobileView;
    private int mAmountSecond;
    private CutDownPresenter mCutDownPresenter;
    private Runnable mRunnableSendSmsCodeTimerCut;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public ChangeMobilePresenter(IChangeMobileView changeMobileView, Intent intent) {
        this.mChangeMobileView = changeMobileView;
    }

    @Override
    public void initialize() {
    }

    public void getSmSCode() {
        String phone = mChangeMobileView.callGetMobile();
        if (!AppUtil.checkPhone(phone)) {
            mChangeMobileView.callShowToastMessage(null, R.string.input_username_correctly);
            return;
        }
        if (phone.equals(UserInfoManager.getInstance().getUserInfo().mobile)) {
            mChangeMobileView.callShowToastMessage(null, R.string.cellphone_not_change);
            return;
        }
        requireSmSCode(phone);
    }

    private void requireSmSCode(String phone) {
        mChangeMobileView.callShowProgress(null, true);
        AccountDao.getSmSCode(phone, AccountDao.SmsCodeType.SMS_CODE_TYPE_UPDATE_MOBILE, new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(boolean success, String s, int errorCode, String errorMsg) {
                mChangeMobileView.callCancelProgress();
                mChangeMobileView.callOnSendSmsCodeReply(success, errorMsg);
                if (success)
                    scheduleNextSendSmCodeTask();
            }
        });
    }

    private void scheduleNextSendSmCodeTask() {
        if (mCutDownPresenter == null)
            mCutDownPresenter = new CutDownPresenter(CommonConst.SMS_CODE_DEPART_TIME, new CutDownPresenter.ICallBack() {
                @Override
                public void onTimeCut(int amountSecond) {
                    ChangeMobilePresenter.this.mAmountSecond = amountSecond;
                    mHandler.post(mRunnableSendSmsCodeTimerCut);
                }
            });
        if (mRunnableSendSmsCodeTimerCut == null)
            mRunnableSendSmsCodeTimerCut = new Runnable() {
                @Override
                public void run() {
                    mChangeMobileView.callUpdateSendSmsCodeTextViewStatus(mAmountSecond);
                }
            };
        mCutDownPresenter.start();
    }

    @Override
    public void onDestroy() {
        if (mCutDownPresenter != null)
            mCutDownPresenter.stop();
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    public void submit() {
        final String phone = mChangeMobileView.callGetMobile();
        if (!AppUtil.checkPhone(phone)) {
            mChangeMobileView.callShowToastMessage(null, R.string.input_username_correctly);
            return;
        }
        if (phone.equals(UserInfoManager.getInstance().getUserInfo().mobile)) {
            mChangeMobileView.callShowToastMessage(null, R.string.cellphone_not_change);
            return;
        }
        String smsCode = mChangeMobileView.callGetSmsCode();
        if (smsCode == null || smsCode.length() < 4) {
            mChangeMobileView.callShowToastMessage(null, (smsCode == null || smsCode.length() == 0 ? R.string.smsCode_not_null : R.string.smsCode_length_error));
            return;
        }
        mChangeMobileView.callShowProgress(null, true);
        AccountDao.updateMobile(phone, smsCode, new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(boolean success, String s, int errorCode, String errorMsg) {
                mChangeMobileView.callCancelProgress();
                if (success) {
                    mChangeMobileView.callShowToastMessage(null, R.string.mobile_update_success);
                    SharedPreferenceManager.saveString(SharedPreferenceConst.SP_USER_PHONE, phone);
                    AppGlobalManager.getInstance().logOut(mChangeMobileView.callGetBindActivity());
//                    UserInfoBean userInfoBean = UserInfoManager.getInstance().getUserInfo();
//                    userInfoBean.mobile = phone;
//                    userInfoBean.userName = phone;
//                    UserInfoManager.getInstance().saveUserInfo(userInfoBean);
//                    UIEventsObservable.getInstance().postEvent(IDefaultEventsSubscriber.class, ActionConst.ACTION_EVENT_ACCOUNT_INFO_CHANGED, null, null);
//                    mChangeMobileView.callGetBindActivity().finish();
                    return;
                }
                mChangeMobileView.callShowToastMessage(errorMsg, 0);
            }
        });
    }
}
