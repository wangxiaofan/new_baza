package com.baza.android.bzw.bean.exchange;

import com.baza.android.bzw.bean.BaseHttpResultBean;

/**
 * Created by Vincent.Lei on 2018/9/4.
 * Title：
 * Note：
 */
public class BenefitResultBean extends BaseHttpResultBean {
    public Data data;

    public static class Data {
        public double quantity;
    }
}
