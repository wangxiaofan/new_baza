package com.baza.android.bzw.dao;

import com.baza.android.bzw.bean.resume.AcceptOrRejectResultBean;
import com.baza.android.bzw.bean.resume.FirmMembersResultBean;
import com.baza.android.bzw.bean.resume.FloatingListAcceptBean;
import com.baza.android.bzw.bean.resume.FloatingListAcceptResultBean;
import com.baza.android.bzw.bean.resume.FloatingListBean;
import com.baza.android.bzw.bean.resume.FloatingListDetailBean;
import com.baza.android.bzw.bean.resume.FloatingListDetailResultBean;
import com.baza.android.bzw.bean.resume.FloatingListResultBen;
import com.baza.android.bzw.bean.resume.FlowInterviewResultBean;
import com.baza.android.bzw.bean.resume.FlowObsoleteResultBean;
import com.baza.android.bzw.bean.resume.FlowStageResultBean;
import com.baza.android.bzw.bean.resume.InterviewFeedbackResultBean;
import com.baza.android.bzw.bean.resume.OneKeyOfferResultBean;
import com.baza.android.bzw.bean.resume.SplitInfoListResultBean;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;

import java.util.HashMap;
import java.util.List;

public class FloatingDao {

    //floatingList列表
    public static void loadFloatingList(String startDate, String endDate, String type, final IDefaultRequestReplyListener<List<FloatingListBean>> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        param.put("type", type);

        HttpRequestUtil.doHttpPost(URLConst.URL_FLOATINGLIST_SEARCH, param, FloatingListResultBen.class, new INetworkCallBack<FloatingListResultBen>() {
            @Override
            public void onSuccess(FloatingListResultBen candidateSearchResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateSearchResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    //floatingList
    public static void loadFloatingListDetail(String recommendId, final IDefaultRequestReplyListener<FloatingListDetailBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("recommendId", recommendId);

        HttpRequestUtil.doHttpPost(URLConst.URL_FLOATINGLIST_DETAIL, param, FloatingListDetailResultBean.class, new INetworkCallBack<FloatingListDetailResultBean>() {
            @Override
            public void onSuccess(FloatingListDetailResultBean candidateSearchResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateSearchResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }


    //accept
    public static void accept(String ids, final IDefaultRequestReplyListener<FloatingListAcceptBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("recommendIds", ids);

        HttpRequestUtil.doHttpPost(URLConst.URL_FLOATINGLIST_RECOMMEND_ACCEPT, param, FloatingListAcceptResultBean.class, new INetworkCallBack<FloatingListAcceptResultBean>() {
            @Override
            public void onSuccess(FloatingListAcceptResultBean candidateSearchResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateSearchResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    //acceptOrReject
    public static void acceptOrReject(HashMap<String, String> param, final IDefaultRequestReplyListener<AcceptOrRejectResultBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_ACCEPT_OR_REJECT, param, AcceptOrRejectResultBean.class, new INetworkCallBack<AcceptOrRejectResultBean>() {
            @Override
            public void onSuccess(AcceptOrRejectResultBean candidateSearchResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateSearchResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    //firmMembers
    public static void firmMembers(final IDefaultRequestReplyListener<FirmMembersResultBean> listener) {
        HttpRequestUtil.doHttpGet(URLConst.URL_FIRM_MEMBERS, FirmMembersResultBean.class, new INetworkCallBack<FirmMembersResultBean>() {
            @Override
            public void onSuccess(FirmMembersResultBean candidateSearchResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateSearchResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    //splitInfoList
    public static void splitInfoList(String recommendId, final IDefaultRequestReplyListener<SplitInfoListResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("recommendId", recommendId);
        HttpRequestUtil.doHttpPost(URLConst.URL_SPLIT_INFO_LIST, param, SplitInfoListResultBean.class, new INetworkCallBack<SplitInfoListResultBean>() {
            @Override
            public void onSuccess(SplitInfoListResultBean candidateSearchResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateSearchResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    //flowStage
    public static void flowStage(HashMap<String, String> param, final IDefaultRequestReplyListener<FlowStageResultBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_FLOW_STAGE, param, FlowStageResultBean.class, new INetworkCallBack<FlowStageResultBean>() {
            @Override
            public void onSuccess(FlowStageResultBean candidateSearchResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateSearchResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    //flowObsolete
    public static void flowObsolete(HashMap<String, String> param, final IDefaultRequestReplyListener<FlowObsoleteResultBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_FLOW_OBSOLETE, param, FlowObsoleteResultBean.class, new INetworkCallBack<FlowObsoleteResultBean>() {
            @Override
            public void onSuccess(FlowObsoleteResultBean candidateSearchResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateSearchResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    //flowInterview
    public static void flowInterview(HashMap<String, String> param, final IDefaultRequestReplyListener<FlowInterviewResultBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_FLOW_INTERVIEW, param, FlowInterviewResultBean.class, new INetworkCallBack<FlowInterviewResultBean>() {
            @Override
            public void onSuccess(FlowInterviewResultBean candidateSearchResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateSearchResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    //interviewFeedback
    public static void interviewFeedback(HashMap<String, String> param, final IDefaultRequestReplyListener<InterviewFeedbackResultBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_FLOW_INTERVIEW_FEEDBACK, param, InterviewFeedbackResultBean.class, new INetworkCallBack<InterviewFeedbackResultBean>() {
            @Override
            public void onSuccess(InterviewFeedbackResultBean candidateSearchResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateSearchResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    //oneKeyOffer
    public static void oneKeyOffer(HashMap<String, String> param, final IDefaultRequestReplyListener<OneKeyOfferResultBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_FLOW_ONEKEYOFFER, param, OneKeyOfferResultBean.class, new INetworkCallBack<OneKeyOfferResultBean>() {
            @Override
            public void onSuccess(OneKeyOfferResultBean candidateSearchResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateSearchResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }
}
