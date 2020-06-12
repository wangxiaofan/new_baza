package com.baza.android.bzw.businesscontroller.resume.detail.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/8/14.
 * Title：
 * Note：
 */

public interface IResumeSourceFileView extends IBaseView {
    void callSetTitle(String title);

    void callSetTextInfo(String text);

    void callOpenFileMode();
}
