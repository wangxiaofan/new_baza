package com.baza.android.bzw.log.logger;

import com.baza.android.bzw.log.ReportAgent;

import org.json.JSONObject;

/**
 * Created by Vincent.Lei on 2019/8/21.
 * Title：
 * Note：
 */
public class RecommendLogger extends BaseLogger {
    public void sendAddRecommend(Object pageObj, String resumeId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            if (resumeId != null)
                jsonObject.put("resumeId", resumeId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null)
            ReportAgent.sendEventLog(pageObj, "recommend_add", jsonObject.toString());
    }

    public void sendCompleteRecommend(Object pageObj, String resumeId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            if (resumeId != null)
                jsonObject.put("resumeId", resumeId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null)
            ReportAgent.sendEventLog(pageObj, "recommend_complete", jsonObject.toString());
    }

    public void sendDeleteRecommend(Object pageObj, String resumeId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            if (resumeId != null)
                jsonObject.put("resumeId", resumeId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null)
            ReportAgent.sendEventLog(pageObj, "recommend_delete", jsonObject.toString());
    }
}
