package com.baza.android.bzw.bean.resume;

public class TalentListBean {

    public static final int COMPANY_TALENT = 1;//企业人才库
    public static final int MY_TALENT = 2;//我的人才库
    public static final int TRACKING_LIST = 3;//tracking_list
    public static final int FLOATING_LIST = 4;//floating_list
    public static final int MY_COLLECTION = 5;//我的收藏

    private int name;//名称

    private int id;//id

    private int res;//图片资源

    public TalentListBean(int name, int id, int res) {
        this.name = name;
        this.id = id;
        this.res = res;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }
}
