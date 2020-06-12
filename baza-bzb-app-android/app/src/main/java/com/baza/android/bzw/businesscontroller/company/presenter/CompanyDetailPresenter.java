package com.baza.android.bzw.businesscontroller.company.presenter;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeDetailBean;
import com.baza.android.bzw.bean.resume.ResumeDetailResultBean;
import com.baza.android.bzw.bean.resume.ResumeUpdatedHistoryResultBean;
import com.baza.android.bzw.bean.resumeelement.RemarkBean;
import com.baza.android.bzw.businesscontroller.company.viewinterface.ICompanyDetailView;
import com.baza.android.bzw.businesscontroller.resume.base.presenter.ResumeBasePresenter;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.ResumeCompareDao;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.dao.ResumeUpdateDao;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.logger.RecommendLogger;
import com.baza.android.bzw.log.logger.ResumeLogger;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;

public class CompanyDetailPresenter extends ResumeBasePresenter {

    private ICompanyDetailView mResumeDetailView;
    private Resources mResources;
    private String[] mMoreEditMenuNormal;
    private ICompanyDetailView.IntentParam mIntentParam;
    private ResumeDetailBean mResumeDetailBean;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ArrayList<RemarkBean> mRemarkList = new ArrayList<>();
    private Object mFilterTag = new Object();
    private ResumeCompareDao mResumeCompareDao;
    private ResumeLogger mResumeLogger = new ResumeLogger();
    private RecommendLogger mRecommendLogger = new RecommendLogger();

    public CompanyDetailPresenter(ICompanyDetailView mResumeDetailView, Intent intent) {
        this.mResumeDetailView = mResumeDetailView;
        this.mResources = mResumeDetailView.callGetResources();
        this.mIntentParam = (ICompanyDetailView.IntentParam) intent.getSerializableExtra("intentParam");
    }

    public ArrayList<RemarkBean> getRemarkListData() {
        return mRemarkList;
    }

    @Override
    public ResumeDetailBean getCurrentResumeData() {
        return mResumeDetailBean;
    }

    @Override
    public ResumeCompareDao getResumeCompareDao() {
        return mResumeCompareDao;
    }

    public ResumeLogger getResumeLogger() {
        return mResumeLogger;
    }

    public String[] getMoreEditMenu() {
        if (mResumeDetailBean == null)
            return null;
        if (mMoreEditMenuNormal == null)
            mMoreEditMenuNormal = mResources.getStringArray(R.array.menu_company_detail_more_add);
        return mMoreEditMenuNormal;
    }

    @Override
    public void onDestroy() {
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        subscribeEvents(false);
    }

    @Override
    public void initialize() {
        subscribeEvents(true);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                loadInitData();
            }
        });
    }

    public void loadInitData() {
        if (mIntentParam.updateHistoryId != null)
            loadUpdatedContent();
        else
            loadResumeDetail();
    }

    private void loadUpdatedContent() {
        ResumeUpdateDao.loadResumeUpdatedHistory(mIntentParam.updateHistoryId, new IDefaultRequestReplyListener<ResumeUpdatedHistoryResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, ResumeUpdatedHistoryResultBean.Data data, int errorCode, String errorMsg) {
                mResumeDetailView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success && data != null && data.before != null && data.current != null) {
                    mResumeDetailBean = data.before;
                    //当前查看的自己的简历更新内容,手动重置unionId
                    mResumeDetailBean.unionId = UserInfoManager.getInstance().getUserInfo().unionId;
                    mResumeCompareDao = new ResumeCompareDao();
                    mResumeCompareDao.buildNewContentToCurrentData(mResumeDetailBean, data.current);
                    //更新当前要展示的更新时间
                    mResumeDetailBean.sourceUpdateTime = data.current.sourceUpdateTime;
                    mResumeDetailView.callUpdateHandleResumeDataViews();
                    mResumeDetailView.updateViewForUpdateHistory();
                }
            }
        });
    }

    private void loadResumeDetail() {
        ResumeDao.getResumeDetail(mIntentParam.resumeId, new IDefaultRequestReplyListener<ResumeDetailResultBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeDetailResultBean candidateDetailResultBean, int errorCode, String errorMsg) {
                mResumeDetailView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success && candidateDetailResultBean.data != null) {
                    mResumeDetailBean = candidateDetailResultBean.data;
                    mResumeDetailView.updateViewForCurrentMode();
                    mResumeDetailView.callUpdateHandleResumeDataViews();

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mResumeDetailBean.inquiryList != null && !mResumeDetailBean.inquiryList.isEmpty()) {
                                mRemarkList.addAll(mResumeDetailBean.inquiryList);
                                for (int i = 0, size = mRemarkList.size(); i < size; i++) {
                                    mRemarkList.get(i).validContent();
                                }
                            }
                            mResumeDetailView.callUpdateRemarkViews(CommonConst.LIST_POSITION_NONE);
                        }
                    }, 500);

                    if (mIntentParam.isAddRemarkMode) {
                        mIntentParam.isAddRemarkMode = false;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mResumeDetailView.callShowAddRemarkView();
                            }
                        });
                    }
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_SHOULD_RESET_SCAN_HISTORY, mResumeDetailBean, mFilterTag);
                }

                if (errorCode == CustomerRequestAssistHandler.NET_REQUEST_RESUME_DELETED_ERROR) {
                    ResumeBean resumeBean = new ResumeBean();
                    resumeBean.candidateId = mIntentParam.resumeId;
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_DELETED_RESUME, resumeBean, mFilterTag);
                }
            }
        });
    }

    private void subscribeEvents(boolean isSubscribe) {
        if (isSubscribe) {

        } else {

        }
    }

    public void addOrUpdateRemark(RemarkBean newRemarkBean) {
        newRemarkBean.validContent();
        boolean isNewAdd = true;
        for (int i = 0, size = mRemarkList.size(); i < size; i++) {
            if (TextUtils.equals(newRemarkBean.inquiryId, mRemarkList.get(i).inquiryId)) {
                mRemarkList.set(i, newRemarkBean);
                isNewAdd = false;
                break;
            }
        }
        if (isNewAdd) {
            mRemarkList.add(0, newRemarkBean);
            if (mResumeDetailBean.inquiryList == null) {
                mResumeDetailBean.inquiryList = new ArrayList<>(1);
                mResumeDetailBean.inquiryList.add(newRemarkBean);
            } else
                mResumeDetailBean.inquiryList.add(0, newRemarkBean);
        }
        mResumeDetailView.callUpdateRemarkViews(CommonConst.LIST_POSITION_NONE);
    }
}
