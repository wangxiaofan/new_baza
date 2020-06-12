package com.baza.android.bzw.businesscontroller.search.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/5/23.
 * Title：
 * Note：
 */

public interface IEditSearchConfigView extends IBaseView {
    int MODE_RESUME_SEARCH_HISTORY = 0;
    int MODE_NAME_LIST_SEARCH_HISTORY = 1;
    int MODE_SG_COMPANY_HISTORY = 2;
    int MODE_SG_TITLE_HISTORY = 3;
    int MODE_DEFAULT_HISTORY = 4;
    int MODE_COMPANY_SEARCH_HISTORY = 5;
    int MODE_TRACKING_LIST_HISTORY = 6;

    void callSetHistory();

    void callUpdateKeywordHint(int keyWordHint);

    void callUpdateKeyWordView(String keyword);
}
