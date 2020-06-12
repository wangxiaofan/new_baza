package social.core.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

import social.IShareCallBack;
import social.SocialHelper;
import social.core.viewInterface.IQQView;
import social.data.ShareData;

/**
 * Created by Vincent.Lei on 2016/12/8.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class QQPresenter implements IUiListener {
    private static final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";
    private ShareData mShareData;
    private IQQView mQQView;
    private Activity mContext;

    private Tencent mTencent;
    private int mShareResultCode = IShareCallBack.ERROR_CODE_NO_RESULT;
    private String mShareResultMsg;

    public QQPresenter(ShareData shareData, IQQView mQQView, Activity context) {
        this.mShareData = shareData;
        this.mQQView = mQQView;
        this.mContext = context;

        init();
    }

    public void onTencentResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, this);
    }

    public void setResultBack() {
        SocialHelper.getInstance().noticeShareResult(mShareData.getKeyId(), mShareResultCode == IShareCallBack.ERROR_CODE_COMPLETE, mShareResultCode, mShareResultMsg);
    }

    private void init() {

        if (!isQQInstall(mContext)) {
            //QQ未安转
            mShareResultCode = IShareCallBack.ERROR_CODE_QQ_NOT_INSTALL;
            mQQView.callClosePage();
            return;
        }

        if (mShareData.getTitle().length() > QQShare.QQ_SHARE_TITLE_MAX_LENGTH)
            mShareData.setTitle(mShareData.getTitle().substring(0, QQShare.QQ_SHARE_TITLE_MAX_LENGTH));
        if (mShareData.getSummary().length() > QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH)
            mShareData.setSummary(mShareData.getSummary().substring(0, QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH));

        mTencent = Tencent.createInstance(SocialHelper.getInstance().getQQAppId(), mContext);
        switch (mShareData.getDataType()) {
            case ShareData.DATA_TYPE_QQ_IMAGE:
                //分享图片
                shareQQImage();
                break;
            case ShareData.DATA_TYPE_QQ_LINK:
                //分享链接
                shareQQLink();
                break;
            case ShareData.DATA_TYPE_QZONE:
                //分享到QQ空间
                shareToQZone();
                break;
        }
    }

    /**
     * 分享图片
     */

    private void shareQQImage() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, mShareData.getTitle());
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mShareData.getSummary());
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mShareData.getAppName());
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, mShareData.getImageLocalPath());
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        mTencent.shareToQQ(mContext, params, this);
    }

    /**
     * 分享链接
     */

    private void shareQQLink() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, mShareData.getTitle());
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mShareData.getSummary());
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mShareData.getAppName());
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mShareData.getShareLink());
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, (mShareData.getImageLink() == null ? mShareData.getImageLocalPath() : mShareData.getImageLink()));
        mTencent.shareToQQ(mContext, params, this);
    }

    /**
     * 分享到QQ空间
     */

    private void shareToQZone() {
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, mShareData.getTitle());//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mShareData.getSummary());//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mShareData.getShareLink());//必填
        String image = (mShareData.getImageLink() == null ? mShareData.getImageLocalPath() : mShareData.getImageLink());
        if (image != null) {
            ArrayList<String> arrayList = new ArrayList<>(1);
            arrayList.add(image);
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, arrayList);
        }
        mTencent.shareToQzone(mContext, params, this);
    }

    @Override
    public void onComplete(Object o) {
        mShareResultCode = IShareCallBack.ERROR_CODE_COMPLETE;
//        mQQView.callClosePage();
    }

    @Override
    public void onError(UiError uiError) {
        mShareResultCode = IShareCallBack.ERROR_CODE_ERROR;
        mShareResultMsg = (uiError != null ? uiError.errorMessage : null);
//        mQQView.callClosePage();
    }

    @Override
    public void onCancel() {
        mShareResultCode = IShareCallBack.ERROR_CODE_CANCEL;
//        mQQView.callClosePage();
    }

    private boolean isQQInstall(Context context) {
        try {
            context.getPackageManager().getApplicationInfo(QQ_PACKAGE_NAME, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
