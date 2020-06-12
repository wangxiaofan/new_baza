package com.baza.android.bzw.bean.searchfilterbean;

/**
 * Created by Vincent.Lei on 2017/3/23.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class WorkYearFilterBean {
    public String name;
    public int startYearParameter;
    public int endYearParameter;
    public int choseIndex = -1;
    public int yearParameter;

    public WorkYearFilterBean(String name) {
        this.name = name;
    }

    public static WorkYearFilterBean createForFilter(String name, int index) {
        WorkYearFilterBean workYearFilterBean = new WorkYearFilterBean(name);
        switch (index) {
            case 0:
                //3年以下
                workYearFilterBean.startYearParameter = 0;
                workYearFilterBean.endYearParameter = 3;
                workYearFilterBean.yearParameter = 1;
                break;
            case 1:
                //3-5年
                workYearFilterBean.startYearParameter = 3;
                workYearFilterBean.endYearParameter = 5;
                workYearFilterBean.yearParameter = 2;
                break;
            case 2:
                //5-10年
                workYearFilterBean.startYearParameter = 5;
                workYearFilterBean.endYearParameter = 10;
                workYearFilterBean.yearParameter = 3;
                break;
            case 3:
                //10年以上
                workYearFilterBean.startYearParameter = 10;
                workYearFilterBean.endYearParameter = -1;
                workYearFilterBean.yearParameter = 4;
                break;
        }
        return workYearFilterBean;
    }
}
