package com.baza.android.bzw.businesscontroller.publish.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/6/26.
 * Title：
 * Note：
 */

public interface IResumeImportView extends IBaseView {
    void callUpdateImportHintView(String fileName, String accountName);

    void callSetOnImportView(boolean isOnImport);

    void callFinish();
}
