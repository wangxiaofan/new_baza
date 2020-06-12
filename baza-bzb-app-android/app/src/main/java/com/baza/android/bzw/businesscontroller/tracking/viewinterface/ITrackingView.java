package com.baza.android.bzw.businesscontroller.tracking.viewinterface;

import com.baza.android.bzw.base.IBaseView;

public interface ITrackingView extends IBaseView {
    void callUpdateLoadAllDataView(boolean hasLoadAll);

    void callRefreshListItems(int targetPosition);

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callSetNoResultHintView(boolean empty);

    void callShowSpecialToastMsg(int type, String msg, int msgId);
}
