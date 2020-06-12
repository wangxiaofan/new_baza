package com.baza.android.bzw.businesscontroller.resume.mine.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/6/7.
 * Title：
 * Note：
 */

public interface ISeekView extends IBaseView {

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callRefreshListItems(int targetPosition);

    void callUpdateLoadAllDataView(boolean hasLoadAll);

    void callShowLoadingView(String msg);
}
