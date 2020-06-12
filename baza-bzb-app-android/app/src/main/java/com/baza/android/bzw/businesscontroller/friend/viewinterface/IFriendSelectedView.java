package com.baza.android.bzw.businesscontroller.friend.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2018/1/11.
 * Title：
 * Note：
 */

public interface IFriendSelectedView extends IBaseView{
    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callUpdateNoDataView(boolean hasData);

    void callRefreshFriendsView();
}
