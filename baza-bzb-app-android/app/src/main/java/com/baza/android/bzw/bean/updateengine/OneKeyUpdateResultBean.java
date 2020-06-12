package com.baza.android.bzw.bean.updateengine;

import com.baza.android.bzw.bean.BaseHttpResultBean;

/**
 * Created by Vincent.Lei on 2017/8/24.
 * Title：
 * Note：
 */

public class OneKeyUpdateResultBean extends BaseHttpResultBean {
    public Data data;

    public static class Data {
        public int total;
//        public ArrayList<ResumeBean> list;
    }
}
