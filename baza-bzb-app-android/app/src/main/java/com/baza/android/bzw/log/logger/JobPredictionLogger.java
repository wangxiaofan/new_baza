package com.baza.android.bzw.log.logger;

import com.baza.android.bzw.log.ReportAgent;

import org.json.JSONObject;

/**
 * Created by Vincent.Lei on 2018/9/14.
 * Title：
 * Note：
 */
public class JobPredictionLogger extends BaseLogger {
    public void sendClickEntranceLog(Object pageObj) {
        ReportAgent.sendEventLog(pageObj, "jobPre_ec_1", null);
    }

    public void sendClickRefreshLog(Object pageObj) {
        ReportAgent.sendEventLog(pageObj, "jobPre_ec_2", null);
    }

    public void sendAddRemarkLog(Object pageObj, String resumeId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("resumeId", resumeId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null)
            ReportAgent.sendEventLog(pageObj, "jobPre_ec_3", jsonObject.toString());
    }
}
