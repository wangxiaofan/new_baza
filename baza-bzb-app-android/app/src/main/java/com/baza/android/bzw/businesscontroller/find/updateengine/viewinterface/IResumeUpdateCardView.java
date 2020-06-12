package com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface;

import com.baza.android.bzw.base.IBaseView;
import com.baza.android.bzw.bean.resume.ResumeUpdatedContentResultBean;
import com.baza.android.bzw.bean.updateengine.UpdateResumeWrapperBean;

/**
 * Created by Vincent.Lei on 2017/11/7.
 * Title：
 * Note：
 */

public interface IResumeUpdateCardView extends IBaseView {
    void callRefreshList();

    void callReachUpdateLimitDialog();

    void callScanNext();

    void callSetUpUpdateSingleResumeView();

    UpdateResumeWrapperBean callGetSelectedUpdateContent();

    ResumeUpdatedContentResultBean.Data callGetEnableUpdateContentData();
}
