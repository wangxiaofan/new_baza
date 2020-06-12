package com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/8/29.
 * Title：
 * Note：
 */

public interface IUpdateLogView extends IBaseView {
    void callSetTitle(String title);

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callSetLoadMoreEnable(boolean enable);

    void callRefreshListView();

    void callFinishPlayVoice();
}
