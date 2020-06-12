package com.baza.android.bzw.bean.exchange;

import com.baza.android.bzw.bean.BaseHttpResultBean;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/9/4.
 * Title：
 * Note：
 */
public class GoodListResultBean extends BaseHttpResultBean {
    public List<Good> data;

    public static class Good {
        public String title;
        public String id;
        public float price;
        public Specification specification;
    }

    public static class Specification {
        public int limit;
    }
}
