package com.baza.android.bzw.businesscontroller.friend.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/9/28.
 * Title：
 * Note：
 */

public interface IDynamicView extends IBaseView {
    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callRefreshListItems();

    void callUpdateLoadAllDataView(boolean hasLoadAll);
}
