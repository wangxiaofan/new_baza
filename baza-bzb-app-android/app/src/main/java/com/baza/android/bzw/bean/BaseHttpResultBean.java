package com.baza.android.bzw.bean;

import java.io.Serializable;

/**
 * Created by LW on 2016/icon_person/30.
 * Title : 数据解析基类  所有http数据模型解析都要直接或者间接继承
 * Note :
 */
public class BaseHttpResultBean implements Serializable{
    public int code = -1;
    public String msg;
}
