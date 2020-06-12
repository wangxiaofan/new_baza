package com.baza.android.bzw.log.logger;

import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.log.ReportAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Vincent.Lei on 2018/5/16.
 * Title：
 * Note：
 */
public class ResumeUpdateLogger extends BaseLogger {
    public void sendSingleUpdateLog(Object objectPage, ResumeBean resumeBean) {
        if (resumeBean == null)
            return;
        sendSingleUpdateLog(objectPage, resumeBean.candidateId, resumeBean.currentCompletion, resumeBean.targetCompletion);
    }

    public void sendSingleUpdateLog(Object objectPage, String resumeId, float currentCompletion, float toCompletion) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("resumeId", resumeId);
            jsonObject.put("currentCompletion", currentCompletion);
            jsonObject.put("toCompletion", toCompletion);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null) {
//            LogUtil.d(jsonObject.toString());
            ReportAgent.sendEventLog(objectPage, "UpdateResumeByOneClick", jsonObject.toString());
        }
    }

    public void sendOneKeyUpdateLog(Object objectPage, List<ResumeBean> list, int updateCount, int firstPageCount) {
        if (list == null || list.isEmpty())
            return;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("updateCount", updateCount);
            JSONArray firstPageResumes = new JSONArray();
            if (updateCount > 0) {
                JSONObject child;
                ResumeBean resumeBean;
                int size = list.size();
                size = (size > firstPageCount ? firstPageCount : size);
                for (int i = 0; i < size; i++) {
                    resumeBean = list.get(i);
                    child = new JSONObject();
                    child.put("resumeId", resumeBean.candidateId);
                    child.put("currentCompletion", resumeBean.currentCompletion);
                    child.put("toCompletion", resumeBean.targetCompletion);
                    firstPageResumes.put(child);
                }

            }
            jsonObject.put("firstPageResumes", firstPageResumes);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null) {
//            LogUtil.d(jsonObject.toString());
            ReportAgent.sendEventLog(objectPage, "UpdateResumeByBatchClick", jsonObject.toString());
        }
    }

    public void sendDoSearchLog(Object pageObj, HashMap<String, String> param) {
        JSONObject jsonObject = null;
        try {
            if (param != null && !param.isEmpty()) {
                jsonObject = new JSONObject();
                JSONArray searchParas = new JSONArray();
                JSONObject p;
                Iterator<Map.Entry<String, String>> iterator = param.entrySet().iterator();
                Map.Entry<String, String> entry;
                while (iterator.hasNext()) {
                    entry = iterator.next();
                    p = new JSONObject();
                    p.put("field", entry.getKey());
                    p.put("value", entry.getValue());
                    searchParas.put(p);
                }
                jsonObject.put("searchParas", searchParas);
            }
        } catch (Exception e) {
            jsonObject = null;
        }

        ReportAgent.sendEventLog(pageObj, "UpdatingResumeSearch", (jsonObject != null ? jsonObject.toString() : null));
    }
}
