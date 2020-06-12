package com.baza.android.bzw.businesscontroller.email.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/5/31.
 * Title：
 * Note：
 */

public interface IBindEmailView extends IBaseView {
    void callFinished();

    void callSetPreviousEmail(String email);

    void callSetTitleAndHint(String title, String hint);
}
