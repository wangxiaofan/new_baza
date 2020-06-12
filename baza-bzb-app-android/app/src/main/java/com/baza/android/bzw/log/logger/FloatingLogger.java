package com.baza.android.bzw.log.logger;

import com.baza.android.bzw.log.ReportAgent;

import org.json.JSONObject;

public class FloatingLogger extends BaseLogger {

    public void ClickMyRecommend(Object pageObj) {
        ReportAgent.sendEventLog(pageObj, "ClickMyRecommend", "");
    }

    public void ClickMyReceive(Object pageObj) {
        ReportAgent.sendEventLog(pageObj, "ClickMyReceive", "");
    }

    public void ClickBatchHandle(Object pageObj) {
        ReportAgent.sendEventLog(pageObj, "ClickBatchHandle", "");
    }

    public void ClickReceiveRecommend(Object pageObj, String json) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("candidateIds", json);
        } catch (Exception e) {
            jsonObject = null;
        }
        ReportAgent.sendEventLog(pageObj, "ClickReceiveRecommend", jsonObject.toString());
    }

    public void Fliter(Object pageObj, String timeType, String title, String name, String status) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("timeType", timeType);
            jsonObject.put("title", title);
            jsonObject.put("name", name);
            jsonObject.put("status", status);
        } catch (Exception e) {
            jsonObject = null;
        }
        ReportAgent.sendEventLog(pageObj, "Fliter", jsonObject.toString());
    }
}
