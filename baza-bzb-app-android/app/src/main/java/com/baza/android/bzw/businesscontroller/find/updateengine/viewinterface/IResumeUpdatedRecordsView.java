package com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/9/11.
 * Title：
 * Note：
 */

public interface IResumeUpdatedRecordsView extends IBaseView {
    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callRefreshListItems();

    void callUpdateLoadAllDataView(boolean hasLoadAll);

    void callUpdateAllCountView(int totalCount);
}
