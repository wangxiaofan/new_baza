package com.tencent.qcloud.tim.uikit.bean;

import java.io.Serializable;

public class IMGetExternalRequestBean implements Serializable {

    public IMGetExternalRequestBean(String keyword, int offset, int pageSize) {
        this.keyword = keyword;
        this.offset = offset;
        this.pageSize = pageSize;
    }

    /**
     * keyword : string
     * offset : 0
     * pageSize : 20
     */

    private String keyword;
    private int offset;
    private int pageSize;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
