package com.baza.android.bzw.businesscontroller.search.viewinterface;

import android.content.Context;

import com.baza.android.bzw.bean.searchfilterbean.FilterCityBean;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/23.
 * Title：
 * Note：
 */

public interface ICityView {
    void callSetCities(List<FilterCityBean> filters);

//    void callRefreshTargetListItem(int targetPosition);

    Context callGetContext();
}
