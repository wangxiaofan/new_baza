package com.baza.android.bzw.log.logger;

import com.baza.android.bzw.log.ReportAgent;

import org.json.JSONObject;

public class FloatingActionLogger extends BaseLogger {

    public void RecommendDetail(Object pageObj, String recommendId, String floatingStatus) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("recommendId", recommendId);
            jsonObject.put("floatingStatus", floatingStatus);
        } catch (Exception e) {
            jsonObject = null;
        }
        ReportAgent.sendEventLog(pageObj, "RecommendDetail", jsonObject.toString());
    }
}
