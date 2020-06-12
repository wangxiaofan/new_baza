package com.baza.android.bzw.businesscontroller.email.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/8/1.
 * Title：
 * Note：
 */

public interface ISendEmailView extends IBaseView {
    void callSetPreviousAssigner(String assigner);

    String callGetEmailAssigner();

    String callGetEmailTitle();

    String callGetEmailContent();

    void callUpdateAttachmentView(int targetPosition);

    void callUpdateAttachmentCountView(int count);

    void callResetAllViews();
}
