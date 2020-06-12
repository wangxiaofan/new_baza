package com.baza.android.bzw.businesscontroller.resume.tab.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/5/18.
 * Title：
 * Note：
 */

public interface ITabMineResumeView extends IBaseView {
    void callUpdateLoadAllDataView(boolean hasLoadAll);

    void callRefreshListItems(int targetPosition);

    void callUpdateAllResumeCountView(int allCount);

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callUpdateResumeUpdateView(int count);

    void callUpdateSearchCountView(int totalCount,int jobHunterCount);

    void callSetNoResultHintView(boolean empty);

    void callUpdatePredictionHintView(boolean shouldHint);
}
