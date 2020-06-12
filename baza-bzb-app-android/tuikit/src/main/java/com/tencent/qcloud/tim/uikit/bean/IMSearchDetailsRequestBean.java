package com.tencent.qcloud.tim.uikit.bean;

import java.io.Serializable;
import java.util.List;

public class IMSearchDetailsRequestBean implements Serializable {

    @Override
    public String toString() {
        return "IMSearchDetailsRequestBean{" +
                "key='" + key + '\'' +
                ", pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", keywords=" + keywords +
                '}';
    }

    public IMSearchDetailsRequestBean(String key, int pageNo, int pageSize, List<String> keywords) {
        this.key = key;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.keywords = keywords;
    }

    /**
     * key : string
     * keywords : ["string"]
     * pageNo : 1
     * pageSize : 5
     */

    private String key;
    private int pageNo;
    private int pageSize;
    private List<String> keywords;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
