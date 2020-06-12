package com.baza.android.bzw.log.logger;

import com.baza.android.bzw.log.ReportAgent;

import org.json.JSONObject;

public class FirmTalentLogger extends BaseLogger {

    public void collecEvent(Object pageObj, String eventCode, String candidateId, String firmId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("candidateId", candidateId);
            jsonObject.put("firmId", firmId);
        } catch (Exception e) {
            jsonObject = null;
        }
        ReportAgent.sendEventLog(pageObj, eventCode, jsonObject.toString());
    }
}
