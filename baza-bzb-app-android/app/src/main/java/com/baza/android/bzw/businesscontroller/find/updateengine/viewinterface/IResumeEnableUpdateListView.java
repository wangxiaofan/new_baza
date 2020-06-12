package com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/8/22.
 * Title：
 * Note：
 */

public interface IResumeEnableUpdateListView extends IBaseView {

    void callRefreshListItems(int targetPosition);

    void callShowLoadingView();

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callUpdateLoadMoreEnable(boolean enable);

    void callUpdateNoDataView(boolean hasData);

    void callUpdateOneKeyUpdateView(int amount, int listCount, int searchCount);

    void callUpdateAmountInfo();

    void callShowFeedBackDialog();

    void callUpdateSearchCount(int totalCount);

    void callUpdateAmountNotEnoughView();

    void callUpdateShareToMuchView();
}
