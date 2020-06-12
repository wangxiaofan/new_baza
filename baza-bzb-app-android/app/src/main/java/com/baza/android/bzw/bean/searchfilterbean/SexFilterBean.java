package com.baza.android.bzw.bean.searchfilterbean;


import com.baza.android.bzw.constant.CommonConst;

/**
 * Created by Vincent.Lei on 2017/3/23.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class SexFilterBean {
    //    public static final String SEX_MALE = "M";
//    public static final String SEX_FEMALE = "F";
    public String name;
    public int sexParameter;
    public int choseIndex = -1;

    public SexFilterBean(String name) {
        this.name = name;
    }

    public static SexFilterBean createForFilter(String name, int index) {
        SexFilterBean sexFilterBean = new SexFilterBean(name);
        sexFilterBean.sexParameter = (index == 0 ? CommonConst.SEX_MALE : CommonConst.SEX_FEMALE);
        return sexFilterBean;
    }
}
