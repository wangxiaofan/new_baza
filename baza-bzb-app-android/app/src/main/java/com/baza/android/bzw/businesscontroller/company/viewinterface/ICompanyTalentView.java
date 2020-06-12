package com.baza.android.bzw.businesscontroller.company.viewinterface;

import com.baza.android.bzw.base.IBaseView;

public interface ICompanyTalentView extends IBaseView {

    void callUpdateLoadAllDataView(boolean hasLoadAll);

    void callRefreshListItems(int targetPosition);

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callSetNoResultHintView(boolean empty);

    void callShowSpecialToastMsg(int type, String msg, int msgId);
}
