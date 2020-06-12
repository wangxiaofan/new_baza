package com.baza.android.bzw.log.logger;

import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.log.ReportAgent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Vincent.Lei on 2018/4/18.
 * Title：
 * Note：
 */
public class ResumeLogger extends BaseLogger {
    private String mSearchId;
    private String mFormComponent;
    private boolean mShouldNotResetSearchId;

    public void setFormComponentName(String formComponent) {
        this.mFormComponent = formComponent;
    }

    public void sendDoSearchLog(Object pageObj, HashMap<String, String> param, int matchCount, int pageIndex) {
        sendDoSearchLog(pageObj, param, matchCount, null, pageIndex);
    }

    public String interceptRefreshSearchIdOuter(boolean force) {
        mShouldNotResetSearchId = true;
        if (mSearchId == null || force)
            refreshSearchId();
        return mSearchId;
    }

    private void refreshSearchId() {
        mSearchId = UUID.randomUUID().toString();
    }

    public void sendDoSearchLog(Object pageObj, HashMap<String, String> param, int matchCount, List<ResumeBean> resultList) {
        sendDoSearchLog(pageObj, param, matchCount, resultList, 1);
    }

    private void sendDoSearchLog(Object pageObj, HashMap<String, String> param, int matchCount, List<ResumeBean> resultList, int pageIndex) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("searchId", mSearchId);
            jsonObject.put("matchCount", matchCount);
            if (param != null && !param.isEmpty()) {
                JSONObject filter = new JSONObject();
                Iterator<Map.Entry<String, String>> iterator = param.entrySet().iterator();
                Map.Entry<String, String> entry;
                while (iterator.hasNext()) {
                    entry = iterator.next();
                    filter.put(entry.getKey(), entry.getValue());
                }
                jsonObject.put("filter", filter);
            }
        } catch (Exception e) {
            jsonObject = null;
        }

        if (jsonObject != null) {
            ReportAgent.sendEventLog(pageObj, "Search", jsonObject.toString());
        }
    }


    public void sendSearchResultClickLog(Object pageObj, int position, int pageSize, String resumeId) {
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

    public void sendSmartGroupFolderSearch(boolean newSearch, Object pageObj, String type, String keyword, int matchCount) {
        JSONObject jsonObject;
        try {
            if (newSearch || mSearchId == null)
                mSearchId = UUID.randomUUID().toString();
            jsonObject = new JSONObject();
            jsonObject.put("type", type);
            jsonObject.put("keyword", keyword);
            jsonObject.put("matchCount", matchCount);
            jsonObject.put("searchId", mSearchId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null)
            ReportAgent.sendEventLog(pageObj, "sg_ec_2", jsonObject.toString());
    }

    public void sendMarkMobileInvalid(Object pageObj, String resumeId, String mobile) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            if (resumeId != null)
                jsonObject.put("resumeId", resumeId);
            if (mobile != null)
                jsonObject.put("mobile", mobile);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null)
            ReportAgent.sendEventLog(pageObj, "mark_mobile_invalid", jsonObject.toString());
    }

    public void sendMarkEmailInvalid(Object pageObj, String resumeId, String email) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            if (resumeId != null)
                jsonObject.put("resumeId", resumeId);
            if (email != null)
                jsonObject.put("email", email);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null)
            ReportAgent.sendEventLog(pageObj, "mark_email_invalid", jsonObject.toString());
    }

    public void sendAttachmentReview(Object pageObj, String resumeId, String fileId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            if (resumeId != null)
                jsonObject.put("resumeId", resumeId);
            if (fileId != null)
                jsonObject.put("fileId", fileId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null)
            ReportAgent.sendEventLog(pageObj, "resume_attachment_review", jsonObject.toString());
    }

    public void sendAddAudioRemark(Object pageObj, String resumeId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            if (resumeId != null)
                jsonObject.put("resumeId", resumeId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null)
            ReportAgent.sendEventLog(pageObj, "add_audio_remark", jsonObject.toString());
    }

    public void sendDeleteAudioRemark(Object pageObj, String resumeId, String inquiryId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            if (resumeId != null)
                jsonObject.put("resumeId", resumeId);
            if (inquiryId != null)
                jsonObject.put("inquiryId", inquiryId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null)
            ReportAgent.sendEventLog(pageObj, "delete_audio_remark", jsonObject.toString());
    }

    public void sendPlayAudioRemark(Object pageObj, String resumeId, String inquiryId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            if (resumeId != null)
                jsonObject.put("resumeId", resumeId);
            if (inquiryId != null)
                jsonObject.put("inquiryId", inquiryId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null)
            ReportAgent.sendEventLog(pageObj, "play_audio_remark", jsonObject.toString());
    }
}
