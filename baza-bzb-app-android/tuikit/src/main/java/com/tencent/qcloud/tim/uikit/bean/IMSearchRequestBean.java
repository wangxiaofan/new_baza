package com.tencent.qcloud.tim.uikit.bean;

import java.io.Serializable;
import java.util.List;

public class IMSearchRequestBean implements Serializable {

    @Override
    public String toString() {
        return "IMSearchRequestBean{" +
                "pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", searchType=" + searchType +
                ", keywords=" + keywords +
                '}';
    }

    public IMSearchRequestBean(int pageNo, int pageSize, int searchType, List<String> keywords) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.searchType = searchType;
        this.keywords = keywords;
    }

    /**
     * keywords : ["string"]
     * pageNo : 1
     * pageSize : 5
     * searchType : 0
     */



    private int pageNo;
    private int pageSize;
    private int searchType;
    private List<String> keywords;

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

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
