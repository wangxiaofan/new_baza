package com.baza.android.bzw.bean.resume;

import com.baza.android.bzw.bean.BaseHttpResultBean;

/**
 * Created by Vincent.Lei on 2017/9/11.
 * Title：
 * Note：
 */

public class ResumeUpdatedHistoryResultBean extends BaseHttpResultBean {
    public Data data;

    public static class Data {
        public ResumeDetailBean before;
        public ResumeDetailBean current;
    }
}
