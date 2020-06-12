package com.baza.android.bzw.log.logger;

import com.baza.android.bzw.log.ReportAgent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class CompanySearchLogger extends BaseLogger {

    private String mSearchId;
    private String mFormComponent;
    private boolean mShouldNotResetSearchId;

    public String interceptRefreshSearchIdOuter(boolean force) {
        mShouldNotResetSearchId = true;
        if (mSearchId == null || force)
            refreshSearchId();
        return mSearchId;
    }

    private void refreshSearchId() {
        mSearchId = UUID.randomUUID().toString();
    }

    public void sendDoSearchLog(Object pageObj, HashMap<String, String> param, int matchCount) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("searchId", mSearchId);
            jsonObject.put("matchCount", matchCount);
            if (param != null && !param.isEmpty()) {
                JSONObject filter = new JSONObject();
                JSONObject bubble = new JSONObject();
                Iterator<Map.Entry<String, String>> iterator = param.entrySet().iterator();
                Map.Entry<String, String> entry;
                while (iterator.hasNext()) {
                    entry = iterator.next();
                    if (entry.getKey().equals("isJobHunting") || entry.getKey().equals("isExcludeEmptyMobile") || entry.getKey().equals("notScan")
                            || entry.getKey().equals("schoolType") || entry.getKey().equals("filterType")) {
                        bubble.put(entry.getKey(), entry.getValue());
                    } else
                        filter.put(entry.getKey(), entry.getValue());
                }
                jsonObject.put("bubble", bubble);
                jsonObject.put("filter", filter);
            }
        } catch (Exception e) {
            jsonObject = null;
        }

        if (jsonObject != null) {
            ReportAgent.sendEventLog(pageObj, "Search", jsonObject.toString());
        }
    }

    public void sendSearchResultClickLog(Object pageObj, String resumeId) {
        if (mSearchId == null)
            return;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("searchId", mSearchId);
            jsonObject.put("candidateId", resumeId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null) {
            ReportAgent.sendEventLog(pageObj, "SearchResultClick", jsonObject.toString());
        }
    }

    public void sendHistoryClickLog(Object pageObj, String history) {
        if (mSearchId == null)
            return;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("searchId", mSearchId);
            jsonObject.put("history", history);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null) {
            ReportAgent.sendEventLog(pageObj, "SearchHistoryClick", jsonObject.toString());
        }
    }
}
