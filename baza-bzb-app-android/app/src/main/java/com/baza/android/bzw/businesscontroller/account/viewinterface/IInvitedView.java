package com.baza.android.bzw.businesscontroller.account.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2018/12/5.
 * Title：
 * Note：
 */
public interface IInvitedView extends IBaseView {
    void callShowShareMenu();

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callRefreshListItems();

    void callShowLoadingView(String msg);

    void callUpdateInviteCodeView();

    void callUpdateInviteCodeErrorView();

    void callUpdateInviteCodeSuccessView();

    void callUpdateEnableInputInviteCodeView(boolean enable);

    void callUpdateInvitedSuccessCountView(int count);
}
