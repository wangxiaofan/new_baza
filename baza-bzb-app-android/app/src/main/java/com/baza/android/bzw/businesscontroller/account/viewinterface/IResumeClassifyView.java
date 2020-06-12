package com.baza.android.bzw.businesscontroller.account.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2018/8/24.
 * Title：
 * Note：
 */
public interface IResumeClassifyView extends IBaseView {
    int TYPE_ALL = 0;
    int IMPORT_FROM_LOCAL = 1;
    int IMPORT_FROM_OTHER_PLATFORM = 2;
    int OTHER_SHARE = 3;
    int LIST_RECEIVE = 4;
//    int LIST_MATCH = 5;
    int DELETE = 6;
    int HISTORY = 10;

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callShowLoadingView(String msg);

    void callRefreshListItems();

    void callUpdateLoadAllDataView(boolean hasLoadAll);

    void callUpdateAllCountView(int totalCount);
}
