package com.baza.android.bzw.businesscontroller.find.updateengine.enableupdatelistui;

import android.view.View;

import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.searchfilterbean.SearchFilterInfoBean;
import com.baza.android.bzw.businesscontroller.find.updateengine.presenter.EnableUpdateResumeSearchPresenter;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IEnableUpdateResumeSearchView;
import com.baza.android.bzw.businesscontroller.search.SearchFilterUI;

import java.util.ArrayList;

/**
 * Created by Vincent.Lei on 2017/11/6.
 * Title：
 * Note：
 */

public class EnableUpdatedSearchUI extends SearchFilterUI {
    private EnableUpdateResumeSearchPresenter mPresenter;

    public EnableUpdatedSearchUI(View viewMain, IEnableUpdateResumeSearchView editUpdateSearchFilterView, EnableUpdateResumeSearchPresenter presenter) {
        super(viewMain, editUpdateSearchFilterView, null);
        this.mPresenter = presenter;
    }

    @Override
    protected SearchFilterInfoBean getSearchFilterInfo() {
        return mPresenter.getSearchFilterInfo();
    }

    @Override
    protected void prepareLabels() {
        mPresenter.prepareLabels();
    }

    @Override
    public ArrayList<Label> getLabelLibrary() {
        return mPresenter.getLabelLibrary();
    }

}
