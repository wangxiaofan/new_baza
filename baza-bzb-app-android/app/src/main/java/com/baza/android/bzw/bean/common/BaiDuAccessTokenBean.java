package com.baza.android.bzw.bean.common;

/**
 * Created by Vincent.Lei on 2017/2/15.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class BaiDuAccessTokenBean {
    public String access_token;
    public String session_key;
    public String scope;
    public String session_secret;
}
