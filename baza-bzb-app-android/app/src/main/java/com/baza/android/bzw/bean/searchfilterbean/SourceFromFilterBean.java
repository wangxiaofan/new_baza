package com.baza.android.bzw.bean.searchfilterbean;

/**
 * Created by Vincent.Lei on 2017/3/23.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class SourceFromFilterBean {
    public String name;
    public String sourceParameter;
    public int choseIndex = -1;

    public SourceFromFilterBean(String name) {
        this.name = name;
    }

    //Local-本地 ZhiLian 51Job LiePin-猎聘   Gllue-谷露
    public static SourceFromFilterBean createForFilter(String name, int index) {
        SourceFromFilterBean sourceFromFilterBean = new SourceFromFilterBean(name);
        switch (index) {
            case 0:
                sourceFromFilterBean.sourceParameter = "Local";
                break;
            case 1:
                sourceFromFilterBean.sourceParameter = "ZhiLian";
                break;
            case 2:
                sourceFromFilterBean.sourceParameter = "51Job";
                break;
            case 3:
                sourceFromFilterBean.sourceParameter = "LiePin";
                break;
            case 4:
                sourceFromFilterBean.sourceParameter = "Share";
                break;
            case 5:
                sourceFromFilterBean.sourceParameter = "Email";
                break;
            case 6:
                sourceFromFilterBean.sourceParameter = "Gllue";
                break;
            case 7:
                sourceFromFilterBean.sourceParameter = "Import";
                break;
            case 8:
                sourceFromFilterBean.sourceParameter = "NameList";
                break;
            case 9:
                sourceFromFilterBean.sourceParameter = "LaGou";
                break;
            case 10:
                sourceFromFilterBean.sourceParameter = "XLS";
                break;
            case 11:
                sourceFromFilterBean.sourceParameter = "ZhiLianHP";
                break;
        }
        return sourceFromFilterBean;
    }
}
