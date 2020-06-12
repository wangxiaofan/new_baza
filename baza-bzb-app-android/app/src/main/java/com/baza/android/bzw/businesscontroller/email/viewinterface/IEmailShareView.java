package com.baza.android.bzw.businesscontroller.email.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/5/31.
 * Title：
 * Note：
 */

public interface IEmailShareView extends IBaseView {
    void callResetAddresseeView(boolean couldContinueAdd);

    void callSetPreviousAddresseeView(int mMaxPreviousCount);

    void callHideAttachmentShareSelection();

    void callSetAddresseeHint();

    void callFinished();

}
