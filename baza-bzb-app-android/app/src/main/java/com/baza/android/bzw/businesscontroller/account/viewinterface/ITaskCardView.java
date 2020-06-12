package com.baza.android.bzw.businesscontroller.account.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2018/11/28.
 * Title：
 * Note：
 */
public interface ITaskCardView extends IBaseView {
    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callRefreshListItems(int targetPosition);

    void callShowLoadingView(String msg);
}
