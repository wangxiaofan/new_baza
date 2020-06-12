package com.baza.android.bzw.log.logger;

import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.log.ReportAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ResumeDetailLogger extends BaseLogger {

    public void sendPageOpenLog(Object pageObj, String candidateId, String firmId, String ownerUnionId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("candidateId", candidateId);
            jsonObject.put("firmId", firmId);
            jsonObject.put("ownerUnionId", ownerUnionId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null) {
            ReportAgent.sendEventLog(pageObj, "OpenResumeDetail", jsonObject.toString());
        }
    }

    public void sendViewMobileLog(Object pageObj, String candidateId, String firmId, String ownerUnionId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("candidateId", candidateId);
            jsonObject.put("firmId", firmId);
            jsonObject.put("ownerUnionId", ownerUnionId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null) {
            ReportAgent.sendEventLog(pageObj, "ViewMobile", jsonObject.toString());
        }
    }

    public void sendViewEmailLog(Object pageObj, String candidateId, String firmId, String ownerUnionId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("candidateId", candidateId);
            jsonObject.put("firmId", firmId);
            jsonObject.put("ownerUnionId", ownerUnionId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null) {
            ReportAgent.sendEventLog(pageObj, "ViewEmail", jsonObject.toString());
        }
    }

    public void sendCollectLog(Object pageObj, String candidateId, String firmId, String ownerUnionId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("candidateId", candidateId);
            jsonObject.put("firmId", firmId);
            jsonObject.put("ownerUnionId", ownerUnionId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null) {
            ReportAgent.sendEventLog(pageObj, "Collect", jsonObject.toString());
        }
    }

    public void sendCancelCollectLog(Object pageObj, String candidateId, String firmId, String ownerUnionId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("candidateId", candidateId);
            jsonObject.put("firmId", firmId);
            jsonObject.put("ownerUnionId", ownerUnionId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null) {
            ReportAgent.sendEventLog(pageObj, "CancelCollect", jsonObject.toString());
        }
    }

    public void sendAddRemindLog(Object pageObj, String candidateId, String firmId, String ownerUnionId, String remindContent, String remindTime) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("candidateId", candidateId);
            jsonObject.put("firmId", firmId);
            jsonObject.put("ownerUnionId", ownerUnionId);
            jsonObject.put("remindContent", remindContent);
            jsonObject.put("remindTime", remindTime);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null) {
            ReportAgent.sendEventLog(pageObj, "AddRemind", jsonObject.toString());
        }
    }

    public void sendAddInquiryLog(Object pageObj, String candidateId, String firmId, String ownerUnionId, String inquiryContent) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("candidateId", candidateId);
            jsonObject.put("firmId", firmId);
            jsonObject.put("ownerUnionId", ownerUnionId);
            jsonObject.put("inquiryContent", inquiryContent);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null) {
            ReportAgent.sendEventLog(pageObj, "AddInquiry", jsonObject.toString());
        }
    }

    public void sendAddTagLog(Object pageObj, String candidateId, String firmId, String ownerUnionId, ArrayList<Label> labels) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("candidateId", candidateId);
            jsonObject.put("firmId", firmId);
            jsonObject.put("ownerUnionId", ownerUnionId);
            StringBuilder stringBuilder = new StringBuilder("[");
            for (int i = 0, size = labels.size(); i < size; i++) {
                stringBuilder.append("\"").append(labels.get(i).tag).append("\"").append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("]");
            String tags = stringBuilder.toString();
            jsonObject.put("tagContent", tags);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null) {
            ReportAgent.sendEventLog(pageObj, "AddTag", jsonObject.toString());
        }
    }

    public void sendAddTrackingListLog(Object pageObj, String candidateId, String firmId, String ownerUnionId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("candidateId", candidateId);
            jsonObject.put("firmId", firmId);
            jsonObject.put("ownerUnionId", ownerUnionId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null) {
            ReportAgent.sendEventLog(pageObj, "AddTrackingList", jsonObject.toString());
        }
    }

    public void sendRemoveTrackingListLog(Object pageObj, String candidateId, String firmId, String ownerUnionId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("candidateId", candidateId);
            jsonObject.put("firmId", firmId);
            jsonObject.put("ownerUnionId", ownerUnionId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null) {
            ReportAgent.sendEventLog(pageObj, "RemoveTrackingList", jsonObject.toString());
        }
    }

    public void sendEditFirmResumeLog(Object pageObj, String candidateId, String firmId, String ownerUnionId, HashMap<String, String> param) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("candidateId", candidateId);
            jsonObject.put("firmId", firmId);
            jsonObject.put("ownerUnionId", ownerUnionId);

            JSONObject editModel = new JSONObject();
            Iterator<Map.Entry<String, String>> iterator = param.entrySet().iterator();
            Map.Entry<String, String> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();
                editModel.put(entry.getKey(), entry.getValue());
            }
            jsonObject.put("editModel", editModel);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null) {
            ReportAgent.sendEventLog(pageObj, "EditFirmResume", jsonObject.toString());
        }
    }


    public void sendClickInquiryRecordsLog(Object pageObj, String candidateId, String firmId, String ownerUnionId) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("candidateId", candidateId);
            jsonObject.put("firmId", firmId);
            jsonObject.put("ownerUnionId", ownerUnionId);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null) {
            ReportAgent.sendEventLog(pageObj, "ClickInquiryRecords", jsonObject.toString());
        }
    }
}
