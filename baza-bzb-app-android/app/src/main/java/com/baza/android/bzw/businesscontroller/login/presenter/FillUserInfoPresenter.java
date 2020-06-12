package com.baza.android.bzw.businesscontroller.login.presenter;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.common.CompanySuggestBean;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.businesscontroller.login.viewinterface.IFillUserInfoView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.LoginDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.UserInfoManager;

/**
 * Created by Vincent.Lei on 2017/5/17.
 * Title：
 * Note：
 */

public class FillUserInfoPresenter extends BasePresenter {
    private IFillUserInfoView mFillUserInfoView;
    private String mCityCode;
    private String mCurrentCompanyFilter;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mRunnableGetCompany;

    public FillUserInfoPresenter(IFillUserInfoView mFillUserInfoView) {
        this.mFillUserInfoView = mFillUserInfoView;
    }

    @Override
    public void initialize() {
        UserInfoBean userInfoBean = UserInfoManager.getInstance().getUserInfo();
        mCityCode = userInfoBean.location;
        mFillUserInfoView.callSetOldData(userInfoBean);
    }

    @Override
    public void onDestroy() {
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
    }

    public void setCityCode(String cityCode) {
        this.mCityCode = cityCode;
    }

    public String getCityCode() {
        return mCityCode;
    }

//    public void doSubmit() {
//        String username = mFillUserInfoView.callGetUserName();
//        if (TextUtils.isEmpty(username)) {
//            mFillUserInfoView.callShowToastMessage(null, R.string.hint_input_real_name);
//            return;
//        }
//        String email = mFillUserInfoView.callGetEmail();
//        if (TextUtils.isEmpty(email) || !AppUtil.checkEmail(email)) {
//            mFillUserInfoView.callShowToastMessage(null, R.string.hint_input_email);
//            return;
//        }
//        String company = mFillUserInfoView.callGetCompany();
//        if (TextUtils.isEmpty(company)) {
//            mFillUserInfoView.callShowToastMessage(null, R.string.hint_input_company);
//            return;
//        }
//        String job = mFillUserInfoView.callGetJob();
//        if (TextUtils.isEmpty(job)) {
//            mFillUserInfoView.callShowToastMessage(null, R.string.hint_input_job);
//            return;
//        }
//        if (TextUtils.isEmpty(mCityCode)) {
//            mFillUserInfoView.callShowToastMessage(null, R.string.hint_input_city);
//            return;
//        }
//        doSubmit(username, company, job, email);
//    }

//    private void doSubmit(final String trueName, final String company, final String title, final String email) {
//        mFillUserInfoView.callShowProgress(null, true);
//        LoginDao.fillUserInfo(trueName, company, title, email, mCityCode, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
//            @Override
//            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
//                if (success) {
//                    setFillInfoComplete(trueName, company, title, email);
//                    return;
//                }
//                mFillUserInfoView.callCancelProgress();
//                mFillUserInfoView.callShowToastMessage(errorMsg, 0);
//            }
//        });
//    }

//    private void setFillInfoComplete(final String trueName, final String company, final String title, final String email) {
//        mFillUserInfoView.callShowProgress(null, true);
//        LoginDao.setStepInfo(CommonConst.USER_OTHER_SET_STEP_USER_INFO, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
//            @Override
//            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
//                mFillUserInfoView.callCancelProgress();
//                if (success) {
//                    UserInfoBean userInfoBean = UserInfoManager.getInstance().getUserInfo();
//                    userInfoBean.trueName = trueName;
//                    userInfoBean.company = company;
//                    userInfoBean.title = title;
//                    userInfoBean.email = email;
//                    userInfoBean.location = mCityCode;
//                    UserInfoManager.getInstance().saveUserInfo(userInfoBean);
//                    UserInfoBean.ExtraInfo extraInfo = UserInfoManager.getInstance().getUserExtraInfo();
//                    extraInfo.setStep = CommonConst.USER_OTHER_SET_STEP_USER_INFO;
//                    UserInfoManager.getInstance().saveUserExtraInfo(extraInfo);
//                    BZWApplication.continueLaunch(mFillUserInfoView.callGetBindActivity());
//                    mFillUserInfoView.callGetBindActivity().finish();
//                    return;
//                }
//                mFillUserInfoView.callShowToastMessage(errorMsg, 0);
//            }
//        });
//    }

    public void getAllCompany(final String name) {
        this.mCurrentCompanyFilter = name;
        if (TextUtils.isEmpty(mCurrentCompanyFilter)) {
            mFillUserInfoView.callSetCompanyAutoCompleteLib(null);
            return;
        }
        mHandler.removeCallbacks(mRunnableGetCompany);
        if (mRunnableGetCompany == null)
            mRunnableGetCompany = new Runnable() {
                @Override
                public void run() {
                    searchCompany(mCurrentCompanyFilter);
                }
            };
        mHandler.postDelayed(mRunnableGetCompany, 300);
    }


    private void searchCompany(final String name) {
        LoginDao.getAllCompany(mCurrentCompanyFilter, CommonConst.DEFAULT_PAGE_SIZE, new IDefaultRequestReplyListener<CompanySuggestBean>() {
            @Override
            public void onRequestReply(boolean success, CompanySuggestBean companySuggestBean, int errorCode, String errorMsg) {
                if (success) {
                    if (!name.equals(mCurrentCompanyFilter)) {
                        LogUtil.d("company out of date");
                        mFillUserInfoView.callSetCompanyAutoCompleteLib(null);
                        return;
                    }
                    LogUtil.d("set company match");
                    mFillUserInfoView.callSetCompanyAutoCompleteLib(companySuggestBean.data);
                }
            }
        });
    }
}
