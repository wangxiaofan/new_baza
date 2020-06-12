package com.baza.android.bzw.businesscontroller.floating.presenter;

import android.util.Log;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.resume.AcceptOrRejectResultBean;
import com.baza.android.bzw.bean.resume.FirmMembersResultBean;
import com.baza.android.bzw.bean.resume.FloatingListDetailBean;
import com.baza.android.bzw.bean.resume.FlowInterviewResultBean;
import com.baza.android.bzw.bean.resume.FlowObsoleteResultBean;
import com.baza.android.bzw.bean.resume.FlowStageResultBean;
import com.baza.android.bzw.bean.resume.InterviewFeedbackResultBean;
import com.baza.android.bzw.bean.resume.OneKeyOfferResultBean;
import com.baza.android.bzw.bean.resume.SplitInfoListResultBean;
import com.baza.android.bzw.businesscontroller.floating.viewinterface.IFloatingView;
import com.baza.android.bzw.dao.FloatingDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.logger.FloatingActionLogger;
import com.baza.android.bzw.log.logger.FloatingLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FloatingPresenter extends BasePresenter {

    IFloatingView floatingListView;

    int status;

    List<FirmMembersResultBean.FirmMembersBean> firmMembers = new ArrayList<>();

    List<SplitInfoListResultBean.SplitInfoBean> splitInfoBeans = new ArrayList<>();

    FloatingLogger floatingLogger = new FloatingLogger();

    FloatingActionLogger floatingActionLogger = new FloatingActionLogger();

    FloatingListDetailBean mBean;

    public FloatingPresenter(IFloatingView floatingListView) {
        this.floatingListView = floatingListView;
    }

    @Override
    public void initialize() {

    }

    public FloatingLogger getFloatingLogger() {
        return floatingLogger;
    }

    public FloatingActionLogger getFloatingActionLogger() {
        return floatingActionLogger;
    }

    public List<FirmMembersResultBean.FirmMembersBean> getFirmMembers() {
        return firmMembers;
    }

    public List<SplitInfoListResultBean.SplitInfoBean> getSplitInfoBeans() {
        return splitInfoBeans;
    }

    public void loadFloatingListDetail(String recommendId) {
        Log.e("herb", "开始加载简历>>");
        floatingListView.callShowLoadingView();
        FloatingDao.loadFloatingListDetail(recommendId, new IDefaultRequestReplyListener<FloatingListDetailBean>() {
            @Override
            public void onRequestReply(boolean success, FloatingListDetailBean floatingListBeans, int errorCode, String errorMsg) {
                Log.e("herb", "加载简历结束>>" + success + "，" + errorCode + "，" + errorMsg);
                floatingListView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success && floatingListBeans != null) {
                    status = floatingListBeans.recommendStatus;
                    mBean = floatingListBeans;
                    floatingListView.callUpdateContent(floatingListBeans);
                    return;
                }
                floatingListView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    /**
     * 接收或拒绝
     *
     * @param recommendId
     * @param acceptable
     * @param rejectReason
     * @param rejectDetail
     */
    public void acceptOrReject(String recommendId, boolean acceptable, String rejectReason, String rejectDetail) {
        HashMap<String, String> param = new HashMap<>();
        param.put("recommendId", recommendId);
        param.put("acceptable", String.valueOf(acceptable));
        if (!acceptable) {
            param.put("rejectReason", rejectReason);
            param.put("rejectDetail", rejectDetail);
        }
        floatingListView.callShowProgress(null, true);
        floatingActionLogger.RecommendDetail(floatingListView.callGetBindActivity(), recommendId, acceptable ? "接受" : "拒绝");
        FloatingDao.acceptOrReject(param, new IDefaultRequestReplyListener<AcceptOrRejectResultBean>() {
            @Override
            public void onRequestReply(boolean success, AcceptOrRejectResultBean bean, int errorCode, String errorMsg) {
                floatingListView.callCancelProgress();
                if (success) {
                    floatingListView.callUpdatePage();
                } else {
                    floatingListView.callShowToastMessage(errorMsg, 0);
                }
            }
        });
    }

    /**
     * 获取企业用户列表
     */
    public void firmMembers() {
        floatingListView.callShowProgress(null, true);
        FloatingDao.firmMembers(new IDefaultRequestReplyListener<FirmMembersResultBean>() {
            @Override
            public void onRequestReply(boolean success, FirmMembersResultBean bean, int errorCode, String errorMsg) {
                floatingListView.callCancelProgress();
                if (success) {
                    firmMembers.clear();
                    firmMembers.addAll(bean.data);
                    floatingListView.callShowDialog();

                } else {
                    floatingListView.callShowToastMessage(errorMsg, 0);
                }
            }
        });
    }

    /**
     * 获取offer业绩分成
     */
    public void splitInfoList(String id) {
        floatingListView.callShowProgress(null, true);
        FloatingDao.splitInfoList(id, new IDefaultRequestReplyListener<SplitInfoListResultBean>() {
            @Override
            public void onRequestReply(boolean success, SplitInfoListResultBean bean, int errorCode, String errorMsg) {
                floatingListView.callCancelProgress();
                if (success) {
                    if (bean.data.size() > 0) {
                        splitInfoBeans.addAll(bean.data);
                        if (firmMembers.size() > 0) {
                            floatingListView.callShowDialog();
                        } else {
                            firmMembers();
                        }
                    } else {
                        if (firmMembers.size() > 0) {
                            floatingListView.callShowDialog();
                        } else {
                            firmMembers();
                        }
                    }
                } else {
                    floatingListView.callShowToastMessage(errorMsg, 0);
                }
            }
        });
    }


    /**
     * 面试/入职/离职/offer 接口
     */
    public void flowStage(HashMap<String, String> param) {
        floatingListView.callShowProgress(null, true);
        floatingActionLogger.RecommendDetail(floatingListView.callGetBindActivity(), param.get("recommendId"), getStatusName(Integer.valueOf(param.get("status"))));
        FloatingDao.flowStage(param, new IDefaultRequestReplyListener<FlowStageResultBean>() {
            @Override
            public void onRequestReply(boolean success, FlowStageResultBean bean, int errorCode, String errorMsg) {
                floatingListView.callCancelProgress();
                if (success) {
                    floatingListView.callUpdatePage();
                } else {
                    floatingListView.callShowToastMessage(errorMsg, 0);
                }
            }
        });
    }

    /**
     * 淘汰
     */
    public void flowObsolete(String recommendId, String reason, String reasonDetail) {
        HashMap<String, String> param = new HashMap<>();
        param.put("recommendId", recommendId);
        param.put("reason", reason);
        param.put("reasonDetail", reasonDetail);
        floatingListView.callShowProgress(null, true);
        floatingActionLogger.RecommendDetail(floatingListView.callGetBindActivity(), param.get("recommendId"), "淘汰");
        FloatingDao.flowObsolete(param, new IDefaultRequestReplyListener<FlowObsoleteResultBean>() {
            @Override
            public void onRequestReply(boolean success, FlowObsoleteResultBean bean, int errorCode, String errorMsg) {
                floatingListView.callCancelProgress();
                if (success) {
                    floatingListView.callUpdatePage();
                } else {
                    floatingListView.callShowToastMessage(errorMsg, 0);
                }
            }
        });
    }

    /**
     * 安排面试
     */
    public void flowInterview(HashMap<String, String> param) {
        floatingListView.callShowProgress(null, true);
        floatingActionLogger.RecommendDetail(floatingListView.callGetBindActivity(), param.get("recommendId"), "安排面试");
        FloatingDao.flowInterview(param, new IDefaultRequestReplyListener<FlowInterviewResultBean>() {
            @Override
            public void onRequestReply(boolean success, FlowInterviewResultBean bean, int errorCode, String errorMsg) {
                floatingListView.callCancelProgress();
                if (success) {
                    floatingListView.callUpdatePage();
                } else {
                    floatingListView.callShowToastMessage(errorMsg, 0);
                }
            }
        });
    }

    /**
     * 面试反馈
     */
    public void interviewFeedback(HashMap<String, String> param) {
        floatingListView.callShowProgress(null, true);
        floatingActionLogger.RecommendDetail(floatingListView.callGetBindActivity(), param.get("recommendId"), "面试反馈");
        FloatingDao.interviewFeedback(param, new IDefaultRequestReplyListener<InterviewFeedbackResultBean>() {
            @Override
            public void onRequestReply(boolean success, InterviewFeedbackResultBean bean, int errorCode, String errorMsg) {
                floatingListView.callCancelProgress();
                if (success) {
                    floatingListView.callUpdatePage();
                } else {
                    floatingListView.callShowToastMessage(errorMsg, 0);
                }
            }
        });
    }

    /**
     * offer
     */
    public void oneKeyOffer(HashMap<String, String> param) {
        floatingListView.callShowProgress(null, true);
        floatingActionLogger.RecommendDetail(floatingListView.callGetBindActivity(), param.get("recommendId"), "一键offer");
        FloatingDao.oneKeyOffer(param, new IDefaultRequestReplyListener<OneKeyOfferResultBean>() {
            @Override
            public void onRequestReply(boolean success, OneKeyOfferResultBean bean, int errorCode, String errorMsg) {
                floatingListView.callCancelProgress();
                if (success) {
                    floatingListView.callUpdatePage();
                } else {
                    floatingListView.callShowToastMessage(errorMsg, 0);
                }
            }
        });
    }

    private void subscriberResumeEvents(boolean isRegister) {

    }

    public int getStatus() {
        return status;
    }


    private String getStatusName(int code) {
        String codeName = "";
        switch (code) {
            case 4:
                codeName = "面试待安排";
                break;
            case 8:
                codeName = "offer";
                break;
            case 16:
                codeName = "已入职";
                break;
            case 32:
                codeName = "已离职";
                break;
        }

        return codeName;
    }
}