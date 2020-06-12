package com.baza.android.bzw.businesscontroller.login.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/5/10.
 * Title：
 * Note：
 */

public interface ILoginView extends IBaseView {
    void callUpdateSendSmsCodeTextViewStatus(int amountSeconds);

    String callGetUserName();

    String callGetSmsCode();

    void callPreviousUserView(String phone);

    void callOnSendSmsCodeReply(boolean success, String errorMsg);

    void callBindMobile(String uunionId);

    void callUpdateUserNameErrorView(boolean hasError);

    void callUpdateSmsCodeErrorView(boolean hasError);
}
