package com.baza.android.bzw.businesscontroller.resume.recommend.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2019/8/15.
 * Title：
 * Note：
 */
public interface IRecommendView extends IBaseView {
    void callRefreshListItems(int targetPosition);

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void updateStateCountView(int delayCount, int completeCount);
}
