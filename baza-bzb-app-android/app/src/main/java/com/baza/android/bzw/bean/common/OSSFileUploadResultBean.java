package com.baza.android.bzw.bean.common;

import com.baza.android.bzw.bean.BaseHttpResultBean;

/**
 * Created by Vincent.Lei on 2018/8/17.
 * Title：
 * Note：
 */
public class OSSFileUploadResultBean extends BaseHttpResultBean {
    public Data data;

    public static class Data {
        public String fileName;
        public String ossKey;

    }
}
