package com.baza.android.bzw.businesscontroller.account.presenter;

import com.baza.android.bzw.bean.user.VersionBean;
import com.baza.android.bzw.dao.CheckUpdateDao;
import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IAccountExtraView;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/5/26.
 * Title：
 * Note：
 */

public class AccountExtraPresenter extends BasePresenter {
    private IAccountExtraView mAccountExtraView;

    public AccountExtraPresenter(IAccountExtraView mAccountExtraView) {
        this.mAccountExtraView = mAccountExtraView;
    }

    @Override
    public void initialize() {
        checkNewVersion(true);
    }

    public void checkNewVersion(final boolean isAutoCheck) {
        CheckUpdateDao.getInstance().checkUpdate(new CheckUpdateDao.INewVersionListener() {
            @Override
            public void noticeCurrentVersionIsNewest() {
                if (!isAutoCheck)
                    mAccountExtraView.callShowToastMessage(null, R.string.is_last_version);

            }

            @Override
            public void noticeOnLoadingNewVersion() {
                if (!isAutoCheck)
                    mAccountExtraView.callShowToastMessage(null, R.string.text_is_loaddowning_apk);
            }

            @Override
            public void noticeFindNewVersion(VersionBean data) {
                //第一次检测到新版本在界面上显示 其余弹框提醒
                if (isAutoCheck) {
                    mAccountExtraView.callUpdateNewVersionView(data);
                    return;
                }
                mAccountExtraView.callShowNewVersionDialog(data);
            }

            @Override
            public void noticeOnCheckingNewVersion() {
                if (!isAutoCheck)
                    mAccountExtraView.callShowToastMessage(null, R.string.up_check_update);
            }
        });
    }
}
