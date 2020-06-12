package com.baza.android.bzw.bean.operational;

import android.text.TextUtils;

import com.baza.android.bzw.bean.BaseHttpResultBean;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/2/11.
 * Title：
 * Note：
 */

public class DialogActivityResultBean extends BaseHttpResultBean {
    public List<Data> data;

    public static class Data {
        private static final int STATUS_OK = 1;
        public static final int SHOW_ALWAYS = 0;

        public String backgroundImageUrl;
        public String buttonImageUrl;
        public String buttonEventUrl;
        public int id;
        public int status;
        public int showCount;

        public boolean isDataEnable() {
            return (status == STATUS_OK && !TextUtils.isEmpty(backgroundImageUrl) && !TextUtils.isEmpty(buttonImageUrl) && !TextUtils.isEmpty(buttonEventUrl));
        }
    }


}
