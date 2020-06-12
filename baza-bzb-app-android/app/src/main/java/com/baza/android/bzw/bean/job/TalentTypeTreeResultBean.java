package com.baza.android.bzw.bean.job;

import com.baza.android.bzw.bean.BaseHttpResultBean;

import java.util.ArrayList;

/**
 * Created by Vincent.Lei on 2018/8/1.
 * Title：
 * Note：
 */
public class TalentTypeTreeResultBean extends BaseHttpResultBean {
    public ArrayList<TalentTypeParentBean> data;

    public static class TalentTypeChildBean {
        public TypeBean item;
    }

    public static class TalentTypeParentBean {
        public TypeBean item;
        public ArrayList<TalentTypeChildBean> children;
    }

    public static class TypeBean {
        public int id;
        //    public int level;
        public String name;
//        public int pid;
    }
}
