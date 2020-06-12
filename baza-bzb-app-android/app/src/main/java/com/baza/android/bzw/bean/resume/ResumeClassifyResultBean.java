package com.baza.android.bzw.bean.resume;

import android.text.TextUtils;

import com.baza.android.bzw.bean.BaseHttpResultBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/8/24.
 * Title：
 * Note：
 */
public class ResumeClassifyResultBean extends BaseHttpResultBean {
    public Data data;

    public static class Data {
        public int attach;
        public int totalCount;
        public List<ResumeClassifyBean> recordList;
    }

    public static class ResumeClassifyBean {
        public long created;
        public int insertOkCount;
        public String remark;
        public int sourceType;
        public String sourcePath;
        public String taskId;
        public String unionId;
        public String updated;

        private JSONObject jsonObjectRemark;

        public JSONObject getJsonObjectRemark() {
            if (jsonObjectRemark == null && !TextUtils.isEmpty(remark)) {
                try {
                    jsonObjectRemark = new JSONObject(remark);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return jsonObjectRemark;
        }
    }
}
