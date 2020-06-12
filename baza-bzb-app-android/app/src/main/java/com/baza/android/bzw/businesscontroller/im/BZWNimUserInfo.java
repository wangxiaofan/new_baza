package com.baza.android.bzw.businesscontroller.im;

import android.text.TextUtils;

import com.baza.android.bzw.bean.user.UserInfoBean;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

/**
 * Created by Vincent.Lei on 2017/6/6.
 * Title：
 * Note：
 */

public class BZWNimUserInfo implements UserInfo {
    private UserInfoBean userInfoBean;
    private String account;

    public BZWNimUserInfo(UserInfoBean userInfoBean, String account) {
        this.userInfoBean = userInfoBean;
        this.account = account;
    }

    @Override
    public String getAccount() {
        return account;
    }

    @Override
    public String getName() {
        return (!TextUtils.isEmpty(userInfoBean.nickName) ? userInfoBean.nickName : (!TextUtils.isEmpty(userInfoBean.trueName) ? userInfoBean.trueName : account));
    }

    @Override
    public String getAvatar() {
        return userInfoBean.avatar;
    }
}
