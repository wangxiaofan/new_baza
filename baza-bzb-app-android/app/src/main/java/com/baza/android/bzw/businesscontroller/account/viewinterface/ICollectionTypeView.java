package com.baza.android.bzw.businesscontroller.account.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2018/8/22.
 * Title：
 * Note：
 */
public interface ICollectionTypeView extends IBaseView {
    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callRefreshListViews(int targetPosition, int totalCount);

    void callUpdateLoadAllDataView(boolean hasLoadAll);

    void callShowLoadingView(String msg);

    void callUpdateSearchCountView(int totalCount, int extraCount);
}
