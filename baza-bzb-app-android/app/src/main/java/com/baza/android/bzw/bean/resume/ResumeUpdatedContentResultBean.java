package com.baza.android.bzw.bean.resume;

import com.baza.android.bzw.bean.BaseHttpResultBean;

/**
 * Created by Vincent.Lei on 2017/8/26.
 * Title：
 * Note：
 */

public class ResumeUpdatedContentResultBean extends BaseHttpResultBean {
    public Data data;

    public static class Data {
        public ResumeDetailBean current;
        public ResumeDetailBean target;
        public String fileName;
        public String fileUrl;
        public String content;
        public boolean extraHasLineFeed;
    }
}
