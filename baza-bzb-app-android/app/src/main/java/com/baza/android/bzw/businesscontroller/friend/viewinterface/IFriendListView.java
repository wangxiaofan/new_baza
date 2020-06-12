package com.baza.android.bzw.businesscontroller.friend.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/9/21.
 * Title：
 * Note：
 */

public interface IFriendListView extends IBaseView {
    void callRefreshFriendsView();

    void callShowLoadingView();

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callFriendNoticeUnReadCountUpdate(int unreadCount);

    void callUpdateNoDataView(boolean hasData);
}
