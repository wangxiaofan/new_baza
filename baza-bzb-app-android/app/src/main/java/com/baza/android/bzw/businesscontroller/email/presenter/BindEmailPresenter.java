package com.baza.android.bzw.businesscontroller.email.presenter;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.email.AddresseeBean;
import com.baza.android.bzw.businesscontroller.email.viewinterface.IBindEmailView;
import com.baza.android.bzw.dao.EmailDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/5/31.
 * Title：
 * Note：
 */

public class BindEmailPresenter extends BasePresenter {
    private IBindEmailView mBindEmailView;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mIsBindForSendEmailForTargetResume;

    public BindEmailPresenter(IBindEmailView mBindEmailView, Intent intent) {
        this.mBindEmailView = mBindEmailView;
        this.mIsBindForSendEmailForTargetResume = intent.getBooleanExtra("isBindForSendEmailForTargetResume", false);
    }

    @Override
    public void initialize() {
        AddresseeBean bindEmailData = UserInfoManager.getInstance().getBindEmailData();
        if (bindEmailData != null && bindEmailData.email != null)
            mBindEmailView.callSetPreviousEmail(bindEmailData.email);
        mBindEmailView.callSetTitleAndHint(mBindEmailView.callGetResources().getString((mIsBindForSendEmailForTargetResume ? R.string.write_email : R.string.share_resume)), mBindEmailView.callGetResources().getString((mIsBindForSendEmailForTargetResume ? R.string.check_email_for_reply : R.string.no_bind_email_yet)));
    }

    @Override
    public void onDestroy() {
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    public void bindEmail(final String email) {
        mBindEmailView.callShowProgress(null, true);
        EmailDao.bindEmail(email, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mBindEmailView.callCancelProgress();
                if (success) {
                    mBindEmailView.callShowToastMessage(mBindEmailView.callGetResources().getString(R.string.verification_email_has_send_please_check_with_email, email), 0);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBindEmailView.callFinished();
                        }
                    }, 2000);
                    return;
                }
                mBindEmailView.callShowToastMessage(errorMsg, 0);
            }
        });
    }
}
