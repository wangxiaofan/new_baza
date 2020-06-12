package com.baza.android.bzw.businesscontroller.account.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/5/31.
 * Title：
 * Note：
 */

public interface IAddEmailSyncAccountView extends IBaseView {
    void callUpdateOnSyncingViews();

    void callCancelOnSyncingViews();

    void callShowImportErrorDialog(int errorCode);

    void callSetDetailSettingViews(boolean show);

    void callMayInModifyMode(String oldAccount);

    void callFinished(boolean isAddSuccess);

    void callUpdateEmailHintList();

    int callGetUnknowEmailType();
}
