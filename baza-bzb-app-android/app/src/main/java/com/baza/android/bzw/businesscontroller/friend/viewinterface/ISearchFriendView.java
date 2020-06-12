package com.baza.android.bzw.businesscontroller.friend.viewinterface;


import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/9/21.
 * Title：
 * Note：
 */

public interface ISearchFriendView extends IBaseView {
    void callSetMode(int mode);

    void callRefreshList();

    void callShowLoadingView();

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callUpdateNoDataView(boolean hasData);

    void callUpdateNearlyPersonCountView(int count);

}
