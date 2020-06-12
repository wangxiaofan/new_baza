package com.baza.android.bzw.businesscontroller.account.presenter;

import android.text.TextUtils;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.user.InviteCodeBean;
import com.baza.android.bzw.bean.user.InviteInfoResultBean;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IInvitedView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.ConfigConst;
import com.baza.android.bzw.dao.AccountDao;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.widget.dialog.ShareDialog;
import com.slib.http.FileLoadTool;
import com.slib.http.IFileLoadObserve;
import com.bznet.android.rcbox.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import social.IShareCallBack;
import social.SocialHelper;
import social.data.ShareData;

/**
 * Created by Vincent.Lei on 2018/12/5.
 * Title：
 * Note：
 */
public class InvitedPresenter extends BasePresenter implements IFileLoadObserve {
    private IInvitedView mInvitedView;
    private String mInvitedCode;
    private List<InviteInfoResultBean.InviteInfoBean> mInviteList = new ArrayList<>();
    private String mCodeImageUrl;
    private int mPlatformType = CommonConst.LIST_POSITION_NONE;

    public InvitedPresenter(IInvitedView invitedView) {
        this.mInvitedView = invitedView;
    }

    @Override
    public void initialize() {
        loadInviteCodeInfo();
        loadInvitedList();
    }

    public List<InviteInfoResultBean.InviteInfoBean> getInviteData() {
        return mInviteList;
    }

    public String getInvitedCode() {
        return mInvitedCode;
    }

    public void loadInviteCodeInfo() {
        AccountDao.loadInviteCode(new IDefaultRequestReplyListener<InviteCodeBean>() {
            @Override
            public void onRequestReply(boolean success, InviteCodeBean inviteCodeBean, int errorCode, String errorMsg) {
                mInvitedView.callCancelLoadingView(success, errorCode, errorMsg);
                if (inviteCodeBean != null) {
                    mInvitedCode = inviteCodeBean.code;
                    mInvitedView.callUpdateInviteCodeView();
                    mInvitedView.callUpdateEnableInputInviteCodeView(inviteCodeBean.showFillCodeButton);
                }
            }
        });
    }

    public void loadInvitedList() {
        AccountDao.loadInviteInfos(new IDefaultRequestReplyListener<InviteInfoResultBean>() {
            @Override
            public void onRequestReply(boolean success, InviteInfoResultBean inviteInfoBeans, int errorCode, String errorMsg) {
                mInviteList.clear();
                if (success && inviteInfoBeans != null) {
                    if (inviteInfoBeans.list != null)
                        mInviteList.addAll(inviteInfoBeans.list);
                    mInvitedView.callUpdateInvitedSuccessCountView(inviteInfoBeans.inviteCount);
                }
                mInvitedView.callRefreshListItems();
            }
        });
    }

    public void inviteCodeExchange(String code) {
        mInvitedView.callShowProgress(null, true);
        AccountDao.inviteCodeExchange(code, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mInvitedView.callCancelProgress();
                if (success) {
                    mInvitedView.callUpdateInviteCodeSuccessView();
                    mInvitedView.callUpdateEnableInputInviteCodeView(false);
                    loadInvitedList();
                    return;
                }
                if (errorCode != CustomerRequestAssistHandler.NET_REQUEST_NO_RESPONSE && errorCode != CustomerRequestAssistHandler.NET_REQUEST_NOT_SERVICE_ERROR && errorCode != CustomerRequestAssistHandler.NET_ERROR_CODE_NOT_LOGGED_IN && errorCode != CustomerRequestAssistHandler.NET_REQUEST_LOGGED_EXPIRED)
                    mInvitedView.callUpdateInviteCodeErrorView();
                else
                    mInvitedView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    private void downLoadCodeImageFile() {
        FileLoadTool.getInstance().registerFileLoadObserve(this);
        FileLoadTool.getInstance().downLoadFile(mCodeImageUrl, ".png");
    }

    @Override
    public void onDestroy() {
        FileLoadTool.getInstance().unRegisterFileLoadObserve(this);
    }

    private IShareCallBack mShareCallBack = new IShareCallBack() {
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
                case IShareCallBack.ERROR_CODE_QQ_NOT_INSTALL:
                    resultCode = R.string.qq_not_install;
                    break;
            }
            if (resultCode != 0)
                mInvitedView.callShowToastMessage(null, resultCode);
        }
    };

    public void onSharePlatformSelectedForInvite(int platformType) {
        mPlatformType = platformType;
        if (TextUtils.isEmpty(mCodeImageUrl)) {
            loadInviteCodeImageInfo();
            return;
        }
        downLoadCodeImageFile();
    }

    private void loadInviteCodeImageInfo() {
        mInvitedView.callShowProgress(null, true);
        AccountDao.loadInviteCodeImageInfo(new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(boolean success, String url, int errorCode, String errorMsg) {
                mInvitedView.callCancelProgress();
                if (success && !TextUtils.isEmpty(url)) {
                    mCodeImageUrl = url;
                    downLoadCodeImageFile();
                    return;
                }
                mInvitedView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    private void doShare(File file) {
        switch (mPlatformType) {
            case ShareDialog.SHARE_TYPE_WE_CHAT_CONTACT:
                ShareData shareData = new ShareData.Builder().appName(mInvitedView.callGetResources().getString(R.string.app_name)).imageLocalPath(file.getAbsolutePath()).buildWeChatImageData();
                SocialHelper.getInstance().shareToWeChatContact(mInvitedView.callGetBindActivity(), shareData, mShareCallBack);
                break;
            case ShareDialog.SHARE_TYPE_WE_CHAT_FRIEND_CIRCLE:
                shareData = new ShareData.Builder().appName(mInvitedView.callGetResources().getString(R.string.app_name)).imageLocalPath(file.getAbsolutePath()).buildWeChatImageData();
                SocialHelper.getInstance().shareToWeChatFriendCircle(mInvitedView.callGetBindActivity(), shareData, mShareCallBack);
                break;
            case ShareDialog.SHARE_TYPE_QQ_CONTACT:
                shareData = new ShareData.Builder().appName(mInvitedView.callGetResources().getString(R.string.app_name)).imageLocalPath(file.getAbsolutePath()).buildQQImageData();
                SocialHelper.getInstance().shareToQQ(mInvitedView.callGetBindActivity(), shareData, mShareCallBack);
                break;
            case ShareDialog.SHARE_TYPE_QZONE:
                shareData = new ShareData.Builder().appName(mInvitedView.callGetResources().getString(R.string.app_name)).imageLocalPath(file.getAbsolutePath()).shareLink(ConfigConst.USER_RC_INDEX).buildQZoneData();
                SocialHelper.getInstance().shareToQZONE(mInvitedView.callGetBindActivity(), shareData, mShareCallBack);
                break;
        }
    }

    @Override
    public void onFileStartLoading(String fileUrl, String tagForSameUrl) {
        if (fileUrl.equals(mCodeImageUrl))
            mInvitedView.callShowToastMessage(null, R.string.load_invited_code_image);
    }

    @Override
    public void onFileLoadProgressChanged(String fileUrl, String tagForSameUrl, int progress) {

    }

    @Override
    public void onFileLoadSuccess(String fileUrl, String tagForSameUrl, File file) {
        if (fileUrl.equals(mCodeImageUrl)) {
            FileLoadTool.getInstance().unRegisterFileLoadObserve(this);
            if (mPlatformType != CommonConst.LIST_POSITION_NONE)
                doShare(file);
        }
    }

    @Override
    public void onFileLoadFailed(String fileUrl, String tagForSameUrl, int errorCode, String errorMsg) {
        if (fileUrl.equals(mCodeImageUrl))
            FileLoadTool.getInstance().unRegisterFileLoadObserve(this);
    }
}
