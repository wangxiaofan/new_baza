package com.baza.android.bzw.businesscontroller.resume.smartgroup.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2018/11/27.
 * Title：
 * Note：
 */
public interface ISmartGroupIndexView extends IBaseView {
    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callRefreshListItems(int targetPosition);

    void callShowLoadingView(String msg);
}
