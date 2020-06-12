package com.baza.android.bzw.bean.searchfilterbean;

import com.baza.android.bzw.bean.common.LocalAreaBean;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/3/21.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class FilterCityBean {
    public String title;
    public List<LocalAreaBean> cities;
}
