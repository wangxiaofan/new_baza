package com.baza.android.bzw.businesscontroller.floating.presenter;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.resume.AcceptOrRejectResultBean;
import com.baza.android.bzw.bean.resume.FirmMembersResultBean;
import com.baza.android.bzw.bean.resume.FloatingListAcceptBean;
import com.baza.android.bzw.bean.resume.FloatingListBean;
import com.baza.android.bzw.bean.resume.FlowInterviewResultBean;
import com.baza.android.bzw.bean.resume.FlowObsoleteResultBean;
import com.baza.android.bzw.bean.resume.FlowStageResultBean;
import com.baza.android.bzw.bean.resume.InterviewFeedbackResultBean;
import com.baza.android.bzw.bean.resume.OneKeyOfferResultBean;
import com.baza.android.bzw.bean.resume.ResumeStatus;
import com.baza.android.bzw.bean.resume.SplitInfoListResultBean;
import com.baza.android.bzw.businesscontroller.floating.viewinterface.IFloatingListView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.FloatingDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.logger.FloatingActionLogger;
import com.baza.android.bzw.log.logger.FloatingLogger;
import com.slib.utils.string.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FloatingListPresenter extends BasePresenter {

    IFloatingListView floatingListView;

    List<FloatingListBean> mData = new ArrayList<>();

    List<String> jobs = new ArrayList<>();

    List<ResumeStatus> status = new ArrayList<>();

    int currentStatus = -1;

    String currentJob = "";

    String currentName = "";

    List<FirmMembersResultBean.FirmMembersBean> firmMembers = new ArrayList<>();

    List<SplitInfoListResultBean.SplitInfoBean> splitInfoBeans = new ArrayList<>();

    FloatingLogger floatingLogger = new FloatingLogger();

    FloatingActionLogger floatingActionLogger = new FloatingActionLogger();

    public FloatingListPresenter(IFloatingListView floatingListView) {
        this.floatingListView = floatingListView;
    }

    public FloatingLogger getFloatingLogger() {
        return floatingLogger;
    }

    public FloatingActionLogger getFloatingActionLogger() {
        return floatingActionLogger;
    }

    @Override
    public void initialize() {
        subscriberResumeEvents(true);
    }

    public List<FirmMembersResultBean.FirmMembersBean> getFirmMembers() {
        return firmMembers;
    }

    public List<SplitInfoListResultBean.SplitInfoBean> getSplitInfoBeans() {
        return splitInfoBeans;
    }

    public void loadFloatingList(String startDate, String endDate, String type) {
        floatingListView.callShowLoadingView(null);
        FloatingDao.loadFloatingList(startDate, endDate, type, new IDefaultRequestReplyListener<List<FloatingListBean>>() {
            @Override
            public void onRequestReply(boolean success, List<FloatingListBean> floatingListBeans, int errorCode, String errorMsg) {
                floatingListView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success) {
                    if (floatingListBeans != null && floatingListBeans.size() > 0) {
                        mData.clear();
                        mData.addAll(floatingListBeans);
                        delJobs();
                        floatingListView.callRefreshListViews(CommonConst.LIST_POSITION_NONE);
                    } else {
                        floatingListView.callShowEmpty();
                    }
                    return;
                }
                floatingListView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public List<FloatingListBean> getDataList() {
        return getFilterList();
    }

    /**
     * 获取筛选职位列表
     *
     * @param job
     * @return
     */
    public List<FloatingListBean> getListForJob(String job) {
        currentJob = job;
        return getFilterList();
    }

    /**
     * 获取筛选状态列表
     *
     * @param status
     * @return
     */
    public List<FloatingListBean> getListForStatus(int status) {
        currentStatus = status;
        return getFilterList();
    }

    /**
     * 获取筛选人名列表
     *
     * @param name
     * @return
     */
    public List<FloatingListBean> getListForName(String name) {
        currentName = name;
        return getFilterList();
    }

    private List<FloatingListBean> getFilterList() {
        List<FloatingListBean> filterList = new ArrayList<>();
        if (StringUtil.isEmpty(currentJob)) {
            if (StringUtil.isEmpty(currentName)) {
                if (currentStatus != -1) {
                    for (FloatingListBean bean : mData) {
                        if (bean.getStatus() == currentStatus) {
                            filterList.add(bean);
                        }
                    }
                } else {
                    return mData;
                }
            } else {
                if (currentStatus != -1) {
                    for (FloatingListBean bean : mData) {
                        if (bean.getStatus() == currentStatus && bean.getCandidateName().contains(currentName)) {
                            filterList.add(bean);
                        }
                    }
                } else {
                    for (FloatingListBean bean : mData) {
                        if (bean.getCandidateName().contains(currentName)) {
                            filterList.add(bean);
                        }
                    }
                }
            }
        } else {
            if (StringUtil.isEmpty(currentName)) {
                if (currentStatus != -1) {
                    for (FloatingListBean bean : mData) {
                        if (bean.getStatus() == currentStatus && bean.getJob().equals(currentJob)) {
                            filterList.add(bean);
                        }
                    }
                } else {
                    for (FloatingListBean bean : mData) {
                        if (bean.getJob().equals(currentJob)) {
                            filterList.add(bean);
                        }
                    }
                }
            } else {
                if (currentStatus != -1) {
                    for (FloatingListBean bean : mData) {
                        if (bean.getStatus() == currentStatus && bean.getJob().equals(currentJob) && bean.getCandidateName().contains(currentName)) {
                            filterList.add(bean);
                        }
                    }
                } else {
                    for (FloatingListBean bean : mData) {
                        if (bean.getJob().equals(currentJob) && bean.getCandidateName().contains(currentName)) {
                            filterList.add(bean);
                        }
                    }
                }
            }
        }
        return filterList;
    }

    /**
     * 处理全部职位列表
     */
    private void delJobs() {
        jobs.clear();
        jobs.add("全部");
        Set set = new HashSet();
        for (FloatingListBean bean : mData) {
            if (set.add(bean.getJob())) {
                jobs.add(bean.getJob());
            }
        }
    }

    public List<String> getJobs() {
        if (jobs.size() <= 0) {
            jobs.add("全部");
        }
        return jobs;
    }

    public String getCurrentJob() {
        return currentJob;
    }

    private void subscriberResumeEvents(boolean isRegister) {

    }

    public void accept(List<FloatingListBean> selectList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (FloatingListBean bean : selectList) {
            stringBuilder.append(bean.getId()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        floatingLogger.ClickReceiveRecommend(floatingListView.callGetBindActivity(), stringBuilder.toString());
        floatingListView.callShowProgress(null, true);
        FloatingDao.accept(stringBuilder.toString(), new IDefaultRequestReplyListener<FloatingListAcceptBean>() {
            @Override
            public void onRequestReply(boolean success, FloatingListAcceptBean floatingListBeans, int errorCode, String errorMsg) {
                floatingListView.callCancelProgress();
                if (success) {
                    floatingListView.callUpdateListStatus(floatingListBeans.successIds);
                }
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
                        splitInfoBeans.clear();
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
