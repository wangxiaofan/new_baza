package com.baza.android.bzw.log.logger;

import com.baza.android.bzw.log.ReportAgent;

import org.json.JSONObject;

public class FloatingDetailLogger extends BaseLogger {

    public void RecommendDetail(Object pageObj, String recommendId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("recommendId", recommendId);
        } catch (Exception e) {
            jsonObject = null;
        }
        ReportAgent.sendEventLog(pageObj, "RecommendDetail", jsonObject.toString());
    }
}
