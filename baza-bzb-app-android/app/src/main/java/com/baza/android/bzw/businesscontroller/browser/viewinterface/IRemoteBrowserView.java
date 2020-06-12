package com.baza.android.bzw.businesscontroller.browser.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/5/11.
 * Title：
 * Note：
 */

public interface IRemoteBrowserView extends IBaseView {
    void callUpdateProgress(int newProgress);

    void callLoadNewUrl(String url);

    void callSetTitleBar(boolean enable, String title);

    String getRsaUnionId();
}
