package com.baza.android.bzw.bean.resumeelement;

/**
 * Created by Vincent.Lei on 2017/8/25.
 * Title：
 * Note：
 */

public class ShortAttachmentBean {
    public ShortAttachmentBean() {
    }

    public ShortAttachmentBean(String fileName, String ossKey) {
        this.fileName = fileName;
        this.ossKey = ossKey;
    }

    public String fileName;
    public String ossKey;
}
