package com.baza.android.bzw.businesscontroller.account.presenter;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.email.ListSyncEmailBean;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IAddEmailSyncAccountView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.EmailDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/31.
 * Title：
 * Note：
 */

public class AddEmailSyncAccountPresenter extends BasePresenter {
    private IAddEmailSyncAccountView mAddEmailSyncAccountView;
    private String mOldAccount;
    private List<ListSyncEmailBean> mEmailListAdded;
    private List<String> mEmailHintList = new ArrayList<>();
    private String[] mEmialSuffix;
    private String mSuffixStr;
    private Runnable mRunnableEmailSuffix;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public AddEmailSyncAccountPresenter(IAddEmailSyncAccountView mAddEmailSyncAccountView, Intent intent) {
        this.mAddEmailSyncAccountView = mAddEmailSyncAccountView;
        this.mOldAccount = intent.getStringExtra("account");
        this.mEmialSuffix = mAddEmailSyncAccountView.callGetResources().getStringArray(R.array.email_suffix_list);
        mEmailListAdded = (List<ListSyncEmailBean>) BZWApplication.getApplication().getCachedTransformData(CommonConst.STR_TRANSFORM_EMAIL_LIST);
    }

    @Override
    public void initialize() {
        mAddEmailSyncAccountView.callMayInModifyMode(mOldAccount);
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
    }

    public List<String> getEmailHintList() {
        return mEmailHintList;
    }

    private boolean isEmailHasAdded(String email) {
        //mAccount不为空表示当前为修改账号信息，邮箱是不可编辑的
        if (!TextUtils.isEmpty(mOldAccount) || mEmailListAdded == null || mEmailListAdded.isEmpty())
            return false;
        for (int i = 0, size = mEmailListAdded.size(); i < size; i++) {
            if (mEmailListAdded.get(i).account.equals(email))
                return true;
        }
        return false;
    }

    public void syncEmailAccount(String email, String password, String host, String port, boolean ssl) {
        if (!AppUtil.checkEmail(email)) {
            mAddEmailSyncAccountView.callShowToastMessage(null, R.string.input_email_correctly);
            return;
        }
        if (isEmailHasAdded(email)) {
            mAddEmailSyncAccountView.callShowToastMessage(null, R.string.email_account_has_added);
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 4) {
            mAddEmailSyncAccountView.callShowToastMessage(null, R.string.input_password_correctly);
            return;
        }
        if (!TextUtils.isEmpty(host) && !AppUtil.checkDomainName(host)) {
            mAddEmailSyncAccountView.callShowToastMessage(null, R.string.email_host_error);
            return;
        }
        doSync(email, password, host, port, ssl);
    }

    private void doSync(String email, String password, String host, String port, boolean ssl) {
        mAddEmailSyncAccountView.callUpdateOnSyncingViews();
        EmailDao.saveSyncEmail(mAddEmailSyncAccountView.callGetUnknowEmailType(), email, password, host, port, ssl, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                if (success) {
                    mAddEmailSyncAccountView.callFinished(true);
                    return;
                }
                mAddEmailSyncAccountView.callCancelOnSyncingViews();
                if (errorCode == CustomerRequestAssistHandler.NET_REQUEST_EMAIL_PASSWORD_ERROR || errorCode == CustomerRequestAssistHandler.NET_REQUEST_EMAIL_MSG_ERROR)
                    mAddEmailSyncAccountView.callShowImportErrorDialog(errorCode);
                else
                    mAddEmailSyncAccountView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void filterEmailHint(String input) {
        if (mRunnableEmailSuffix != null)
            mHandler.removeCallbacks(mRunnableEmailSuffix);
        if (TextUtils.isEmpty(input)) {
            mEmailHintList.clear();
            mAddEmailSyncAccountView.callUpdateEmailHintList();
            return;
        }
        mSuffixStr = input;
        if (mSuffixStr.equals("\\@") || mSuffixStr.equals("."))
            return;
        if (mRunnableEmailSuffix == null)
            mRunnableEmailSuffix = new Runnable() {
                @Override
                public void run() {
                    mEmailHintList.clear();
                    int indexOfSuffix = mSuffixStr.indexOf("@");
                    String currentSuffix = (indexOfSuffix == CommonConst.LIST_POSITION_NONE ? null : mSuffixStr.substring(indexOfSuffix, mSuffixStr.length()));
                    mSuffixStr = (indexOfSuffix == CommonConst.LIST_POSITION_NONE ? mSuffixStr : mSuffixStr.substring(0, indexOfSuffix));
                    for (int i = 0; i < mEmialSuffix.length; i++) {
                        if (currentSuffix == null || mEmialSuffix[i].startsWith(currentSuffix))
                            mEmailHintList.add(mSuffixStr + mEmialSuffix[i]);
                    }
                    mAddEmailSyncAccountView.callUpdateEmailHintList();
                }
            };
        mHandler.postDelayed(mRunnableEmailSuffix, 100);
    }

    public boolean shouldHintUnknowEmailType(String input) {
        if (TextUtils.isEmpty(input))
            return false;
        int indexOfSuffix = mSuffixStr.indexOf("@");
        if ((indexOfSuffix != CommonConst.LIST_POSITION_NONE) && input.lastIndexOf(".") > indexOfSuffix) {
            String currentSuffix = mSuffixStr.substring(indexOfSuffix, mSuffixStr.length());
            for (int i = 0; i < mEmialSuffix.length; i++) {
                if (mEmialSuffix[i].startsWith(currentSuffix))
                    return false;
            }
            return true;
        }
        return false;

    }
}
