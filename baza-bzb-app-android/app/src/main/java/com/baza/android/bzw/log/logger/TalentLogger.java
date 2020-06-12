package com.baza.android.bzw.log.logger;

import com.baza.android.bzw.log.ReportAgent;

public class TalentLogger extends BaseLogger {

    public void sendClickLog(Object pageObj, String eventCode) {
        ReportAgent.sendEventLog(pageObj, eventCode, "");
    }
}
