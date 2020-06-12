package com.baza.android.bzw.businesscontroller.resume.jobfind.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2018/8/23.
 * Title：
 * Note：
 */
public interface IJobHunterPredictionView extends IBaseView {
    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callRefreshListItems(int targetPosition);

    void callUpdateLoadAllDataView(boolean hasLoadAll);

    void callShowLoadingView(String msg);

    void callShowEmptyView(boolean hasData);
}
