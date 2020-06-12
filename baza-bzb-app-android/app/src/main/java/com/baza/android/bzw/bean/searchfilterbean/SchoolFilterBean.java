package com.baza.android.bzw.bean.searchfilterbean;

/**
 * Created by Vincent.Lei on 2017/3/23.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class SchoolFilterBean {
    public String name;
    public int schoolParameter;
    public int choseIndex = -1;
    public SchoolFilterBean(String name) {
        this.name = name;
    }

    public static SchoolFilterBean createForFilter(String name, int index) {
        SchoolFilterBean schoolFilterBean = new SchoolFilterBean(name);
        switch (index) {
            case 0:
                schoolFilterBean.schoolParameter = 1;
                break;
            case 1:
                schoolFilterBean.schoolParameter = 2;
                break;
            case 2:
                schoolFilterBean.schoolParameter = 3;
                break;
        }
        return schoolFilterBean;
    }
}
