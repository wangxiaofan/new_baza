package com.baza.android.bzw.dao;

import android.text.TextUtils;

import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeDetailBean;
import com.baza.android.bzw.bean.resume.ResumeDetailResultBean;
import com.baza.android.bzw.bean.resume.ResumeEnableUpdateBaseInfoBean;
import com.baza.android.bzw.bean.resume.ResumeEnableUpdateInfoResultBean;
import com.baza.android.bzw.bean.resume.ResumeSearchBean;
import com.baza.android.bzw.bean.resume.ResumeSearchResultBean;
import com.baza.android.bzw.bean.resume.ResumeUpdateListResultBean;
import com.baza.android.bzw.bean.resume.ResumeUpdateLogResultBean;
import com.baza.android.bzw.bean.resume.ResumeUpdatedContentResultBean;
import com.baza.android.bzw.bean.resume.ResumeUpdatedHistoryResultBean;
import com.baza.android.bzw.bean.searchfilterbean.SearchFilterInfoBean;
import com.baza.android.bzw.bean.updateengine.OneKeyUpdateResultBean;
import com.baza.android.bzw.bean.updateengine.SuggestEnableUpdateTipResultBean;
import com.baza.android.bzw.bean.updateengine.UpdateResumeWrapperBean;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.LogUtil;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;
import com.slib.utils.AppUtil;
import com.slib.utils.DateUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/8/21.
 * Title：
 * Note：
 */

public class ResumeUpdateDao {
    private ResumeUpdateDao() {
    }

    public static void loadResumeEnableUpdateBaseInfo(final IDefaultRequestReplyListener<ResumeEnableUpdateBaseInfoBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_GET_ENABLE_UPDATE_RESUME_INFO, null, ResumeEnableUpdateInfoResultBean.class, new INetworkCallBack<ResumeEnableUpdateInfoResultBean>() {
            @Override
            public void onSuccess(ResumeEnableUpdateInfoResultBean candidateEnableUpdateInfoResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateEnableUpdateInfoResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static HashMap<String, String> buildSearchParam(int offset, boolean isJobHunting, List<ResumeBean> ignoreList, SearchFilterInfoBean searchFilter, SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean label, SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean titleSuggest) {
        HashMap<String, String> param = new HashMap<>();
        param.put("pageSize", String.valueOf(CommonConst.DEFAULT_PAGE_SIZE));
        param.put("offset", String.valueOf(offset));
        if (isJobHunting)
            param.put("isJobHunting", "true");
        if (searchFilter != null)
            SearchFilterInfoBean.setParameterToMap(searchFilter, param);
        if (ignoreList != null && !ignoreList.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder("[");
            for (int i = 0, size = ignoreList.size(); i < size; i++)
                stringBuilder.append("\"").append(ignoreList.get(i).candidateId).append("\"").append(",");
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("]");
            LogUtil.d(stringBuilder.toString());
            param.put("excludeCandidateIds", stringBuilder.toString());
        }
        if (titleSuggest != null)
            param.put("positionTags", "[\"" + titleSuggest.name + "\"]");
        if (label != null)
            param.put("tag", "[\"" + label.name + "\"]");
        return param;
    }

    public static void loadResumeEnableUpdateList(HashMap<String, String> param, final IDefaultRequestReplyListener<ResumeSearchBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_GET_ENABLE_UPDATE_RESUME_LIST, param, ResumeSearchResultBean.class, new INetworkCallBack<ResumeSearchResultBean>() {
            @Override
            public void onSuccess(ResumeSearchResultBean resumeSearchResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, resumeSearchResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void oneKeyUpdate(SearchFilterInfoBean searchFilter, SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean label, SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean titleSuggest, final IDefaultRequestReplyListener<OneKeyUpdateResultBean.Data> listener) {
        HashMap<String, String> mSearchParam = null;
        if (searchFilter != null || label != null || titleSuggest != null)
            mSearchParam = new HashMap<>();
        if (searchFilter != null)
            SearchFilterInfoBean.setParameterToMap(searchFilter, mSearchParam);
        if (titleSuggest != null)
            mSearchParam.put("positionTags", "[\"" + titleSuggest.name + "\"]");
        if (label != null)
            mSearchParam.put("tag", "[\"" + label.name + "\"]");
        HttpRequestUtil.doHttpPost(URLConst.URL_UPDATE_ONE_KEY, mSearchParam, OneKeyUpdateResultBean.class, new INetworkCallBack<OneKeyUpdateResultBean>() {
            @Override
            public void onSuccess(OneKeyUpdateResultBean oneKeyUpdateResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, oneKeyUpdateResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadResumeUpdateContent(String resumeId, final IDefaultRequestReplyListener<ResumeUpdatedContentResultBean.Data> listener) {
        if (resumeId == null) return;
        HashMap<String, String> param = new HashMap<>();
        param.put("resumeId", resumeId);
        HttpRequestUtil.doHttpPost(URLConst.URL_GET_UPDATE_CONTENT, param, ResumeUpdatedContentResultBean.class, new INetworkCallBack<ResumeUpdatedContentResultBean>() {
            @Override
            public void onSuccess(ResumeUpdatedContentResultBean candidateUpdatedContentResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateUpdatedContentResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void updateResumeContent(String resumeId, UpdateResumeWrapperBean wrapper, final IDefaultRequestReplyListener<ResumeDetailBean> listener) {
        if (resumeId == null || wrapper == null) return;
        HashMap<String, String> param = new HashMap<>();
        param.put("resumeId", resumeId);
        if (wrapper.mainInfo != null) {
            if (wrapper.mainInfo.realName != null)
                param.put("realName", wrapper.mainInfo.realName);
            if (wrapper.mainInfo.mobile != null)
                param.put("mobile", wrapper.mainInfo.mobile);
            if (wrapper.mainInfo.email != null)
                param.put("email", wrapper.mainInfo.email);
            if (wrapper.mainInfo.title != null)
                param.put("title", wrapper.mainInfo.title);
            if (wrapper.mainInfo.company != null)
                param.put("company", wrapper.mainInfo.company);
            if (wrapper.mainInfo.major != null)
                param.put("major", wrapper.mainInfo.major);
            if (wrapper.mainInfo.school != null)
                param.put("school", wrapper.mainInfo.school);
            if (wrapper.mainInfo.yearExpr >= 0)
                param.put("yearExpr", String.valueOf(wrapper.mainInfo.yearExpr));
            if (wrapper.mainInfo.degree > 0)
                param.put("degree", String.valueOf(wrapper.mainInfo.degree));
            if (wrapper.mainInfo.location > 0)
                param.put("location", String.valueOf(wrapper.mainInfo.location));
            if (wrapper.mainInfo.gender > 0)
                param.put("gender", String.valueOf(wrapper.mainInfo.gender));
            if (wrapper.mainInfo.marriage > 0)
                param.put("marriage", String.valueOf(wrapper.mainInfo.marriage));
            if (Long.parseLong(wrapper.mainInfo.birthday) > 0)
                param.put("birthday", DateUtil.longMillions2FormatDate(Long.parseLong(wrapper.mainInfo.birthday), DateUtil.DEFAULT_API_FORMAT));
            if (wrapper.mainInfo.huKou > 0)
                param.put("huKou", String.valueOf(wrapper.mainInfo.huKou));
        }
        if (wrapper.selfEvaluation != null)
            param.put("selfEvaluation", wrapper.selfEvaluation);
        if (wrapper.workList != null)
            param.put("workLists", AppUtil.objectToJson(wrapper.workList));
        if (wrapper.eduList != null)
            param.put("eduLists", AppUtil.objectToJson(wrapper.eduList));
        if (wrapper.intentions != null)
            param.put("intentionList", AppUtil.objectToJson(wrapper.intentions));
        if (wrapper.projectExperienceList != null)
            param.put("projectExperienceLists", AppUtil.objectToJson(wrapper.projectExperienceList));
//        LogUtil.d(param.toString());
        HttpRequestUtil.doHttpPost(URLConst.URL_UPDATE_RESUME_CONTENT, param, ResumeDetailResultBean.class, new INetworkCallBack<ResumeDetailResultBean>() {
            @Override
            public void onSuccess(ResumeDetailResultBean candidateDetailResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateDetailResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void updateFeedBack(int commentType, String msg, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("commentType", String.valueOf(commentType));
        if (!TextUtils.isEmpty(msg))
            param.put("content", msg);
        HttpRequestUtil.doHttpPost(URLConst.URL_UPDATE_RESUME_FEED_BACK, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadUpdateLogs(int currentPage, String resumeId, final IDefaultRequestReplyListener<ResumeUpdateLogResultBean.Data> listener) {
        if (resumeId == null)
            return;
        HashMap<String, String> param = new HashMap<>();
        param.put("currentPage", String.valueOf(currentPage));
        param.put("resumeId", resumeId);
        HttpRequestUtil.doHttpPost(URLConst.URL_GET_UPDATE_RESUME_LOG, param, ResumeUpdateLogResultBean.class, new INetworkCallBack<ResumeUpdateLogResultBean>() {
            @Override
            public void onSuccess(ResumeUpdateLogResultBean candidateUpdateLogResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateUpdateLogResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void sendUpdateLog(int senceId, String resumeId, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        if (resumeId == null)
            return;
        HashMap<String, String> param = new HashMap<>();
        param.put("senceId", String.valueOf(senceId));
        param.put("resumeId", resumeId);
        HttpRequestUtil.doHttpPost(URLConst.URL_SEND_UPDATE_RESUME_LOG, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadAlreadyUpdatedList(int pageNo, final IDefaultRequestReplyListener<ResumeUpdateListResultBean.Data> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("pageSize", String.valueOf(CommonConst.DEFAULT_PAGE_SIZE));
        param.put("pageNo", String.valueOf(pageNo));
        HttpRequestUtil.doHttpPost(URLConst.URL_GET_ALREADY_UPDATED_LIST, param, ResumeUpdateListResultBean.class, new INetworkCallBack<ResumeUpdateListResultBean>() {
            @Override
            public void onSuccess(ResumeUpdateListResultBean alreadyUpdatedResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, alreadyUpdatedResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadResumeUpdatedHistory(String logId, final IDefaultRequestReplyListener<ResumeUpdatedHistoryResultBean.Data> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("logId", logId);
        HttpRequestUtil.doHttpPost(URLConst.URL_GET_RESUME_UPDATED_HISTORY, param, ResumeUpdatedHistoryResultBean.class, new INetworkCallBack<ResumeUpdatedHistoryResultBean>() {
            @Override
            public void onSuccess(ResumeUpdatedHistoryResultBean candidateUpdatedHistoryResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateUpdatedHistoryResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadDefaultUpdateAmount(final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_SHARE_TO_GET_DEFAULT_UPDATE_AMOUNT, null, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void updateDefaultSingleResume(String resumeId, final IDefaultRequestReplyListener<ResumeDetailBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("resumeId", resumeId);
        HttpRequestUtil.doHttpPost(URLConst.URL_UPDATE_SINGLE_RESUME_DEFAULT, param, ResumeDetailResultBean.class, new INetworkCallBack<ResumeDetailResultBean>() {
            @Override
            public void onSuccess(ResumeDetailResultBean candidateDetailResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateDetailResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

}
