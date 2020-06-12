package com.baza.android.bzw.businesscontroller.login.viewinterface;

import com.baza.android.bzw.bean.user.LoginResultBean;
import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/8/7.
 * Title：
 * Note：
 */

public interface IBindMobileView extends IBaseView {
    void callUpdateSendSmsCodeTextViewStatus(int amountSeconds);

    String callGetUserName();

    String callGetSmsCode();

    void callSetBack(LoginResultBean loginResultBean);
}
