package com.baza.android.bzw.businesscontroller.friend.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/9/26.
 * Title：
 * Note：
 */

public interface IFriendHomeView extends IBaseView {
    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callUpdateFriendInfoView();

    void callRefreshDynamicViews();

    void callSetLoadMoreEnable(boolean enable);

    void callUpdateNoDataView(boolean hasData);
}
