package com.baza.android.bzw.bean.user;

import com.baza.android.bzw.bean.BaseHttpResultBean;

/**
 * Created by Vincent.Lei on 2018/3/7.
 * Title：
 * Note：
 */

public class HrdCheckResultBean extends BaseHttpResultBean {
    public Data data;

    public static class Data {
        public static final int CHECK_OK = 1;
        public int isSuccess;
    }
}
