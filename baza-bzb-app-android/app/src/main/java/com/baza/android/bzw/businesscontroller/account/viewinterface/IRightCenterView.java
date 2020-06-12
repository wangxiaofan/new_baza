package com.baza.android.bzw.businesscontroller.account.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2018/9/4.
 * Title：
 * Note：
 */
public interface IRightCenterView extends IBaseView {
    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callShowLoadingView(String msg);

    void callUpdateGoodsView();

    void callUpdateBenefitView();

//    void callUpdateInvitedCodeEnableView();
}
