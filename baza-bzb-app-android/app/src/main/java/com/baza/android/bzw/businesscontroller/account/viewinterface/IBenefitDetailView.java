package com.baza.android.bzw.businesscontroller.account.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2018/9/4.
 * Title：
 * Note：
 */
public interface IBenefitDetailView extends IBaseView {
    int TYPE_IN = 1;
    int TYPE_OUT = 0;

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callRefreshListItems();

    void callUpdateLoadAllDataView(boolean hasLoadAll);

    void callShowLoadingView(String msg);
}
