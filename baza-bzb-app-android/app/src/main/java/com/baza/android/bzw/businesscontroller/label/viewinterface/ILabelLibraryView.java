package com.baza.android.bzw.businesscontroller.label.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/5/25.
 * Title：
 * Note：
 */

public interface ILabelLibraryView extends IBaseView {
    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callShowLoadingView();

    void callShowNoDataView(boolean noData);

    void callRefreshLabelsView();
}
