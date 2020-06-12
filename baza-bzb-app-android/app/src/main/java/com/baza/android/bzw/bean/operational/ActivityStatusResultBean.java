package com.baza.android.bzw.bean.operational;

import com.baza.android.bzw.bean.BaseHttpResultBean;

/**
 * Created by Vincent.Lei on 2017/10/27.
 * Title：
 * Note：
 */

public class ActivityStatusResultBean extends BaseHttpResultBean {
    public Data data;

    public static class Data {
        public static final int STATUS_OK = 1;

        public int id;
        public int status;
        public String link;
    }
}
