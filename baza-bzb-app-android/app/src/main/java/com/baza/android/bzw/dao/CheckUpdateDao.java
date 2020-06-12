package com.baza.android.bzw.dao;

import android.text.TextUtils;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.bean.user.VersionBean;
import com.baza.android.bzw.bean.user.VersionResultBean;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.bznet.android.rcbox.BuildConfig;

/**
 * Created by Vincent.Lei on 2016/12/6.
 * Title : 检查更新控制主持
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class CheckUpdateDao {

    private static final long TEN_MINUTES = 10 * 60 * 1000;

    public static CheckUpdateDao getInstance() {
        return mPresenter;
    }

    public interface INewVersionListener {
        void noticeCurrentVersionIsNewest();

        void noticeOnLoadingNewVersion();

        void noticeFindNewVersion(VersionBean data);

        void noticeOnCheckingNewVersion();
    }


    //版本数据
    private VersionResultBean mVersionResultBean;
    private boolean mIsOnCheckNewVersion;
    private long mLastCheckedTime;
    private static CheckUpdateDao mPresenter = new CheckUpdateDao();

    public void checkUpdate(final INewVersionListener listener) {
        if (listener == null)
            return;
        if (System.currentTimeMillis() - mLastCheckedTime >= TEN_MINUTES)
            //距离上次检测更新超过10分钟  重置状态
            destroyInfo();
        if (mVersionResultBean != null && mVersionResultBean.data != null) {
            if (BuildConfig.VERSION_CODE < mVersionResultBean.data.versionId) {
                //有新版本
                if (!LoadApkDao.getInstance(BZWApplication.getApplication()).isOnLoadingNewApk())
                    listener.noticeFindNewVersion(mVersionResultBean.data);
                else
                    listener.noticeOnLoadingNewVersion();
                return;
            }
            listener.noticeCurrentVersionIsNewest();
            return;
        }

        if (mIsOnCheckNewVersion) {
            listener.noticeOnCheckingNewVersion();
            return;
        }
        getNewVersionMsg(listener);
    }

    private void getNewVersionMsg(final INewVersionListener listener) {
        mIsOnCheckNewVersion = true;
        AccountDao.checkNewVersion(new IDefaultRequestReplyListener<VersionResultBean>() {
            @Override
            public void onRequestReply(boolean success, VersionResultBean versionResultBean, int errorCode, String errorMsg) {
                mIsOnCheckNewVersion = false;
                if (success) {
                    mLastCheckedTime = System.currentTimeMillis();
                    CheckUpdateDao.this.mVersionResultBean = versionResultBean;
                    if (BuildConfig.VERSION_CODE < versionResultBean.data.versionId) {
                        listener.noticeFindNewVersion(versionResultBean.data);
                        return;
                    }
                }
                listener.noticeCurrentVersionIsNewest();
            }
        });
    }

    public void downLoadNewVersionApk() {
        if (mVersionResultBean == null || mVersionResultBean.data == null)
            return;
        if (TextUtils.isEmpty(mVersionResultBean.data.downloadUrl))
            return;
        LoadApkDao.getInstance(BZWApplication.getApplication()).loadNewApk(mVersionResultBean.data.downloadUrl, mVersionResultBean.data.versionId);
    }

    public void destroyInfo() {
        mVersionResultBean = null;
        mIsOnCheckNewVersion = false;
        mLastCheckedTime = 0;
    }
}
