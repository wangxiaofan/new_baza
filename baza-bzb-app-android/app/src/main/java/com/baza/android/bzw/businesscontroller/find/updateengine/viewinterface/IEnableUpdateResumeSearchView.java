package com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface;

import com.baza.android.bzw.businesscontroller.search.viewinterface.IResumeSearchView;

/**
 * Created by Vincent.Lei on 2018/6/8.
 * Title：
 * Note：
 */
public interface IEnableUpdateResumeSearchView extends IResumeSearchView {
    void callUpdateOneKeyUpdateView(int amount, int listCount, int searchCount);

    void callUpdateShareToMuchView();
}
