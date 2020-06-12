package com.baza.android.bzw.bean.resume;

import com.baza.android.bzw.bean.BaseHttpResultBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vincent.Lei on 2017/8/23.
 * Title：
 * Note：
 */

public class ResumeUpdateListResultBean extends BaseHttpResultBean {
    public Data data;

    public static class Data implements Serializable {
        public ArrayList<ResumeBean> list;
        public int total;
    }

}
