package com.baza.android.bzw.businesscontroller.account.viewinterface;

import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.base.IBaseView;
import com.baza.android.bzw.bean.user.VersionBean;

/**
 * Created by Vincent.Lei on 2017/8/2.
 * Title：
 * Note：
 */

public interface IUserSetView extends IBaseView {
    void callUpdateAvatar(UserInfoBean userInfoBean);

//    void callUpdateRealName(String realName);

    void callUpdateNickName(String nickName);

    void callUpdateEmail(String email, boolean valid);

//    void callUpdateCompany(String company);

    //    void callUpdateJob(String job);
    void updateIdentifyStatusView();

    void callUpdateCity(String cityName);

    void callSetNewEmail(boolean valid);

    void callUpdateVersion(VersionBean versionBean);

    void callUpdateCellPhone();

    void callUpdateShareResumeMode();

    void callShowNewVersionDialog(VersionBean versionBean);
}
