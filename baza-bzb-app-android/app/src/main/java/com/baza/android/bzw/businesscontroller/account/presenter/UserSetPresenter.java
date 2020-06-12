package com.baza.android.bzw.businesscontroller.account.presenter;

import android.text.TextUtils;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.email.AddresseeBean;
import com.baza.android.bzw.bean.email.BindEmailResultBean;
import com.baza.android.bzw.bean.user.UploadAvatarResultBean;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.bean.user.UserInfoResultBean;
import com.baza.android.bzw.bean.user.VersionBean;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IUserSetView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.dao.AccountDao;
import com.baza.android.bzw.dao.CheckUpdateDao;
import com.baza.android.bzw.dao.EmailDao;
import com.baza.android.bzw.events.IDefaultEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.manager.AddressManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;

import java.io.File;

/**
 * Created by Vincent.Lei on 2017/8/2.
 * Title：
 * Note：
 */

public class UserSetPresenter extends BasePresenter {
    private IUserSetView mUserInfoEditView;
    private boolean mIsOnCheckEmail;

    public UserSetPresenter(IUserSetView userInfoEditView) {
        this.mUserInfoEditView = userInfoEditView;
    }

    @Override
    public void initialize() {
        subscribeEvents(true);
        setUserInfo();
        loadUserInfo();
        checkEmail(true, false);
        checkNewVersion(true);
    }

    @Override
    public void onDestroy() {
        subscribeEvents(false);
    }

    private void setUserInfo() {
        UserInfoBean userInfoBean = UserInfoManager.getInstance().getUserInfo();
        mUserInfoEditView.callUpdateAvatar(userInfoBean);
        mUserInfoEditView.callUpdateCity(AddressManager.getInstance().getCityNameByStringCode(userInfoBean.location));
        mUserInfoEditView.callUpdateNickName(userInfoBean.nickName);
    }

    public void checkEmail(final boolean updateEmailView, final boolean checkForBind) {
        AddresseeBean bindEmailData = UserInfoManager.getInstance().getBindEmailData();
        if (bindEmailData != null && (bindEmailData.validStatus == AddresseeBean.VALID_OK)) {
            if (updateEmailView)
                mUserInfoEditView.callUpdateEmail(bindEmailData.email, true);
            if (checkForBind)
                mUserInfoEditView.callSetNewEmail(true);
            return;
        }
        if (mIsOnCheckEmail)
            return;
        mIsOnCheckEmail = true;
        if (checkForBind)
            mUserInfoEditView.callShowProgress(null, false);
        EmailDao.checkBindEmail(new IDefaultRequestReplyListener<BindEmailResultBean>() {
            @Override
            public void onRequestReply(boolean success, BindEmailResultBean bindEmailResultBean, int errorCode, String errorMsg) {
                mUserInfoEditView.callCancelProgress();
                mIsOnCheckEmail = false;
                if (success) {
                    UserInfoManager.getInstance().setBindEmailData(bindEmailResultBean.data);
                    if (updateEmailView && bindEmailResultBean.data != null)
                        mUserInfoEditView.callUpdateEmail(bindEmailResultBean.data.email, bindEmailResultBean.data.validStatus == AddresseeBean.VALID_OK);
                    if (checkForBind)
                        mUserInfoEditView.callSetNewEmail(bindEmailResultBean.data != null && bindEmailResultBean.data.validStatus == AddresseeBean.VALID_OK);
                }
            }
        });
    }

    public String getEmail() {
        AddresseeBean bindEmailData = UserInfoManager.getInstance().getBindEmailData();
        if (bindEmailData != null)
            return bindEmailData.email;
        return null;
    }

    public void unBindEmail() {
        mUserInfoEditView.callShowProgress(null, true);
        EmailDao.unBindEmail(new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mUserInfoEditView.callCancelProgress();
                if (success) {
                    mUserInfoEditView.callUpdateEmail(null, false);
                    UserInfoManager.getInstance().setBindEmailData(null);
                    mUserInfoEditView.callShowToastMessage(null, R.string.email_unbind_success);
                } else
                    mUserInfoEditView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void updateAvatar(String avatarPath) {
        File file = new File(avatarPath);
        if (!file.exists())
            return;
        mUserInfoEditView.callShowProgress(null, true);
        AccountDao.updateAvatar(file, new IDefaultRequestReplyListener<UploadAvatarResultBean>() {
            @Override
            public void onRequestReply(boolean success, UploadAvatarResultBean uploadAvatarResultBean, int errorCode, String errorMsg) {
                mUserInfoEditView.callCancelProgress();
                if (success) {
                    UserInfoBean userInfoBean = UserInfoManager.getInstance().getUserInfo();
                    userInfoBean.avatar = uploadAvatarResultBean.data;
                    mUserInfoEditView.callUpdateAvatar(userInfoBean);
                    UIEventsObservable.getInstance().postEvent(IDefaultEventsSubscriber.class, ActionConst.ACTION_EVENT_ACCOUNT_INFO_CHANGED, null, null);
                    UserInfoManager.getInstance().saveUserInfo(userInfoBean);
                    return;
                }
                mUserInfoEditView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void updateUserNickName(String nickName) {
        if (TextUtils.isEmpty(nickName))
            return;
        updateUserNormalInfo(AccountDao.ACCOUNT_EDIT_NICK_NAME, nickName);
    }

//    public void updateUserRealName(String realName) {
//        if (TextUtils.isEmpty(realName))
//            return;
//        updateUserNormalInfo(AccountDao.ACCOUNT_EDIT_REAL_NAME, realName);
//    }
//
//    public void updateUserCompany(String company) {
//        if (TextUtils.isEmpty(company))
//            return;
//        updateUserNormalInfo(AccountDao.ACCOUNT_EDIT_COMPANY, company);
//    }
//
//    public void updateUserJob(String job) {
//        if (TextUtils.isEmpty(job))
//            return;
//        updateUserNormalInfo(AccountDao.ACCOUNT_EDIT_JOB, job);
//    }

    public void updateUserCity(int cityCode) {
        updateUserNormalInfo(AccountDao.ACCOUNT_EDIT_CITY, String.valueOf(cityCode));
    }

    public void bindEmail(final String email) {
        if (!AppUtil.checkEmail(email))
            return;
        mUserInfoEditView.callShowProgress(null, true);
        EmailDao.bindEmail(email, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mUserInfoEditView.callCancelProgress();
                if (success) {
                    mUserInfoEditView.callUpdateEmail(email, false);
                    mUserInfoEditView.callShowToastMessage(mUserInfoEditView.callGetResources().getString(R.string.verification_email_has_send_please_check_with_email, email), 0);
                    return;
                }
                mUserInfoEditView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public String getCityCode() {
        return UserInfoManager.getInstance().getUserInfo().location;
    }

    private void updateUserNormalInfo(final int type, final String value) {
        mUserInfoEditView.callShowProgress(null, true);
        AccountDao.updateUserInfo(type, value, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mUserInfoEditView.callCancelProgress();
                if (success) {
                    UserInfoBean userInfoBean = UserInfoManager.getInstance().getUserInfo();
                    switch (type) {
                        case AccountDao.ACCOUNT_EDIT_NICK_NAME:
                            userInfoBean.nickName = value;
                            mUserInfoEditView.callUpdateNickName(value);
                            break;
//                        case AccountDao.ACCOUNT_EDIT_REAL_NAME:
//                            userInfoBean.trueName = value;
//                            mUserInfoEditView.callUpdateRealName(value);
//                            break;
//                        case AccountDao.ACCOUNT_EDIT_COMPANY:
//                            userInfoBean.company = value;
//                            mUserInfoEditView.callUpdateCompany(value);
//                            break;
//                        case AccountDao.ACCOUNT_EDIT_JOB:
//                            userInfoBean.title = value;
//                            mUserInfoEditView.callUpdateJob(value);
//                            break;
                        case AccountDao.ACCOUNT_EDIT_CITY:
                            userInfoBean.location = value;
                            mUserInfoEditView.callUpdateCity(AddressManager.getInstance().getCityNameByCode(Integer.parseInt(value)));
                            break;
                    }
                    UIEventsObservable.getInstance().postEvent(IDefaultEventsSubscriber.class, ActionConst.ACTION_EVENT_ACCOUNT_INFO_CHANGED, null, null);
                    UserInfoManager.getInstance().saveUserInfo(userInfoBean);
                    return;
                }
                mUserInfoEditView.callShowToastMessage(errorMsg, 0);
            }
        });
    }


    private void loadUserInfo() {
        AccountDao.getUserInfo(new IDefaultRequestReplyListener<UserInfoResultBean>() {
            @Override
            public void onRequestReply(boolean success, UserInfoResultBean userInfoResultBean, int errorCode, String errorMsg) {
                if (success && userInfoResultBean.data != null) {
                    UserInfoManager.getInstance().saveUserInfo(userInfoResultBean.data);
                    setUserInfo();
                    UIEventsObservable.getInstance().postEvent(IDefaultEventsSubscriber.class, ActionConst.ACTION_EVENT_ACCOUNT_INFO_CHANGED, null, null);
                }
            }
        });
    }

    public void checkNewVersion(final boolean isAutoCheck) {
        CheckUpdateDao.getInstance().checkUpdate(new CheckUpdateDao.INewVersionListener() {
            @Override
            public void noticeCurrentVersionIsNewest() {
                if (!isAutoCheck)
                    mUserInfoEditView.callShowToastMessage(null, R.string.is_last_version);

            }

            @Override
            public void noticeOnLoadingNewVersion() {
                if (!isAutoCheck)
                    mUserInfoEditView.callShowToastMessage(null, R.string.text_is_loaddowning_apk);
            }

            @Override
            public void noticeFindNewVersion(VersionBean data) {
                //第一次检测到新版本在界面上显示 其余弹框提醒
                if (isAutoCheck) {
                    mUserInfoEditView.callUpdateVersion(data);
                    return;
                }
                mUserInfoEditView.callShowNewVersionDialog(data);
            }

            @Override
            public void noticeOnCheckingNewVersion() {
                if (!isAutoCheck)
                    mUserInfoEditView.callShowToastMessage(null, R.string.up_check_update);
            }
        });
    }

    private void subscribeEvents(boolean register) {
        if (register) {
            UIEventsObservable.getInstance().subscribeEvent(IDefaultEventsSubscriber.class, this, new IDefaultEventsSubscriber() {
                @Override
                public boolean killEvent(String action, Object data) {
                    if (ActionConst.ACTION_EVENT_ACCOUNT_INFO_CHANGED.equals(action)) {
                        mUserInfoEditView.updateIdentifyStatusView();
                        mUserInfoEditView.callUpdateCellPhone();
                    }
                    return false;
                }
            });

        } else {
            UIEventsObservable.getInstance().stopSubscribeEvent(IDefaultEventsSubscriber.class, this);
        }
    }
}
