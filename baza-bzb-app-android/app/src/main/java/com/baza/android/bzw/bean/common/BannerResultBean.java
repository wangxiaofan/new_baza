package com.baza.android.bzw.bean.common;

import com.baza.android.bzw.bean.BaseHttpResultBean;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/3/2.
 * Title：
 * Note：
 */

public class BannerResultBean extends BaseHttpResultBean {
    public List<Data> data;

    public static class Data {
//        public static final int TYPE_INVITED = 4;
        public String bannerUrl;
        public String link;
        public int type;
    }
}
