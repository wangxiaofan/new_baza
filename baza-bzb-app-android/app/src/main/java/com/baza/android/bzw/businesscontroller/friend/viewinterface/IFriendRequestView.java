package com.baza.android.bzw.businesscontroller.friend.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/9/22.
 * Title：
 * Note：
 */

public interface IFriendRequestView extends IBaseView {
    void callRefreshFriendsView();

    void callShowLoadingView();

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callUpdateNoDataView(boolean hasData);
}
