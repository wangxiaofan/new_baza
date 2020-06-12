package com.baza.android.bzw.businesscontroller.resume.create.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/5/31.
 * Title：
 * Note：
 */

public interface ICreateResumeView extends IBaseView {
    void callSetUpItemViews();

    void callSetMode(boolean isEdited);

    String callGetName();

    String callGetMobile();

    String callGetEmail();

    String callGetCompany();

    String callGetTitle();
}
