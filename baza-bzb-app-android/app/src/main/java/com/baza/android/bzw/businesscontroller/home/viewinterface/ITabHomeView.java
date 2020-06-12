package com.baza.android.bzw.businesscontroller.home.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/9/27.
 * Title：
 * Note：
 */

public interface ITabHomeView extends IBaseView {
    void callUpdateSuggestFriendView();

    void callRefreshListItems(int targetPosition);

    void callUpdateBannerView();

    void callUpdateLoadMoreEnable(boolean enable);

    void callSetNoResultHintView(boolean noResult);

}
