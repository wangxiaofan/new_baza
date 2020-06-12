package com.baza.android.bzw.businesscontroller.account.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2018/1/4.
 * Title：
 * Note：
 */

public interface IChangeMobileView extends IBaseView {
    String callGetMobile();

    String callGetSmsCode();

    void callUpdateSendSmsCodeTextViewStatus(int amountSecond);

    void callOnSendSmsCodeReply(boolean success, String errorMsg);

}
