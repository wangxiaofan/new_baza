package com.baza.android.bzw.bean.exchange;

import com.baza.android.bzw.bean.BaseHttpResultBean;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/9/4.
 * Title：
 * Note：
 */
public class BenefitDetailResultBean extends BaseHttpResultBean {
    public Data data;

    public static class Data {
        public int count;
        public List<BenefitRecord> data;
    }

    public static class BenefitRecord {
        public String title;
        public String content;
        public long createTime;
        public long invalidTime;
    }


}
