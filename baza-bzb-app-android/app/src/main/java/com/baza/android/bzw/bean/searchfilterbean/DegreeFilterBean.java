package com.baza.android.bzw.bean.searchfilterbean;

/**
 * Created by Vincent.Lei on 2017/3/23.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class DegreeFilterBean {
    public String name;
    public int degreeParameter;
    public int choseIndex = -1;

    public DegreeFilterBean(String name) {
        this.name = name;
    }

    public static DegreeFilterBean createForFilter(String name, int index) {
        //1--高中及以下  senior schoolName; 2--大专  associate; 3--本科  bachelor; 4--硕士  master; 5--MBA(工商管理硕士) ; 6--博士  doctor
        DegreeFilterBean degreeFilterBean = new DegreeFilterBean(name);
        degreeFilterBean.degreeParameter = index + 1;
        return degreeFilterBean;
    }
}
