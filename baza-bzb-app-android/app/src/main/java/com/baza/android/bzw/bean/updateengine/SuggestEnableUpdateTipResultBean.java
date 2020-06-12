package com.baza.android.bzw.bean.updateengine;

import com.baza.android.bzw.bean.BaseHttpResultBean;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/11/27.
 * Title：
 * Note：
 */

public class SuggestEnableUpdateTipResultBean extends BaseHttpResultBean {
    public List<SuggestEnableUpdateTipBean> data;

    public static class SuggestEnableUpdateTipBean {
        public String name;
    }
}
