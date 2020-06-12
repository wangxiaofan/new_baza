package com.baza.android.bzw.bean.push;

import com.baza.android.bzw.bean.BaseHttpResultBean;

/**
 * Created by Vincent.Lei on 2018/8/21.
 * Title：
 * Note：
 */
public class NoticeUnreadResultBean extends BaseHttpResultBean {
    public Data data;

    public static class Data {
        public int resumeSynUnReadCount;
        public int systemUnReadCount;
        public int candidateShareUnHandleCount;
        public int candidateRequestUnHandleCount;
    }
}
