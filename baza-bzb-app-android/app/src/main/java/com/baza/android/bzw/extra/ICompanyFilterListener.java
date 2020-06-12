package com.baza.android.bzw.extra;


import com.baza.android.bzw.bean.common.LocalAreaBean;
import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.searchfilterbean.DegreeFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SchoolFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SexFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SourceFromFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.WorkYearFilterBean;

import java.util.HashMap;

/**
 * Created by Vincent.Lei on 2017/3/21.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public interface ICompanyFilterListener {

    void onCityFilterSelected(LocalAreaBean cityBean);

    void onMoreFilterSelected(WorkYearFilterBean workYearFilter, DegreeFilterBean degreeFilter, SchoolFilterBean schoolFilter, SourceFromFilterBean sourceFromFilter, SexFilterBean sexFilter);

    void onLabelSelected(HashMap<String, Label> mLabelSelected);

    void onSortOrderSelected(int sortType);

    void clearMoreFilter();

    void clearLabelsFilter();

    void onYearFilterSelected(WorkYearFilterBean mWorkYearFilter);
}
