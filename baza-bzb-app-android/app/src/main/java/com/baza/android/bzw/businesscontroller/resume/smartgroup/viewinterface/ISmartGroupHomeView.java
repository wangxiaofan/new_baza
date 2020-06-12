package com.baza.android.bzw.businesscontroller.resume.smartgroup.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2018/8/29.
 * Title：
 * Note：
 */
public interface ISmartGroupHomeView extends IBaseView {
    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callRefreshListItems(int targetPosition);

    void callUpdateLoadAllDataView(boolean hasLoadAll);

    void callShowLoadingView(String msg);
}
