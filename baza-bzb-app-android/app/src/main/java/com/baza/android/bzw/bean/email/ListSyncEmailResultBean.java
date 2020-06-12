package com.baza.android.bzw.bean.email;

import com.baza.android.bzw.bean.BaseHttpResultBean;

import java.util.ArrayList;

/**
 * Created by Vincent.Lei on 2017/5/2.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class ListSyncEmailResultBean extends BaseHttpResultBean {
    public ArrayList<ListSyncEmailBean> data;
}
