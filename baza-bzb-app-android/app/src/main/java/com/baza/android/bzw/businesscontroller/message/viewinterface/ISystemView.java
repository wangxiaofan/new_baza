package com.baza.android.bzw.businesscontroller.message.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/6/21.
 * Title：
 * Note：
 */

public interface ISystemView extends IBaseView {

    int MSG_SYSTEM = 0;
    int MSG_BAZA_HELPER = 1;

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callRefreshMessageViews(int targetPosition);

    void callUpdateLoadMoreView(boolean enable);
}
