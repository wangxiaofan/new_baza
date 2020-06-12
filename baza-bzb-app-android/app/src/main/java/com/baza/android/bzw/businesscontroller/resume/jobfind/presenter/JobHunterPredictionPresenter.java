package com.baza.android.bzw.businesscontroller.resume.jobfind.presenter;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeSearchBean;
import com.baza.android.bzw.businesscontroller.resume.jobfind.viewinterface.IJobHunterPredictionView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.CollectionDao;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2018/8/23.
 * Title：
 * Note：
 */
public class JobHunterPredictionPresenter extends BasePresenter {
    private IJobHunterPredictionView mPredictionView;
    private List<ResumeBean> mDataList = new ArrayList<>();
    private List<ResumeBean> mNowList = new ArrayList<>();
    private List<ResumeBean> mPreviousList = new ArrayList<>();
    private int mOffset;
    private Object mFilterTag = new Object();
    private int mNowCount;
    private int mPreviousCount;
    private ResumeDao.SearchParam mSearchParam;

    public int getNowCount() {
        return mNowCount;
    }

    public int getPreviousCount() {
        return mPreviousCount;
    }

    public JobHunterPredictionPresenter(IJobHunterPredictionView predictionView) {
        this.mPredictionView = predictionView;
    }

    @Override
    public void initialize() {
        subscribeEvents(true);
        loadNowPredictionList(true, false);
    }

    @Override
    public void onDestroy() {
        subscribeEvents(false);
    }

    public List<ResumeBean> getDataList() {
        return mDataList;
    }

//    public List<ResumeBean> getNowList() {
//        return mNowList;
//    }
//
//    public List<ResumeBean> getPreviousList() {
//        return mPreviousList;
//    }

    public void loadNowPredictionList(final boolean autoLoadPrevious, final boolean showProgress) {
        if (showProgress)
            mPredictionView.callShowProgress(null, true);
        ResumeDao.SearchParam searchParam = new ResumeDao.SearchParam().offset(0).pageSize(100).isJobHunting(true)
                .jobHuntingTimeType(CommonConst.JobHunterPredictionType.NOW).isSearchMineResume(true).build();
        ResumeDao.doSearch(searchParam, new IDefaultRequestReplyListener<ResumeSearchBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeSearchBean resumeSearchBean, int errorCode, String errorMsg) {
                if (showProgress)
                    mPredictionView.callCancelProgress();
                if (!success && autoLoadPrevious) {
                    mPredictionView.callCancelLoadingView(false, errorCode, errorMsg);
                    return;
                }
                mNowList.clear();
                if (success) {
                    if (resumeSearchBean != null) {
                        mNowCount = resumeSearchBean.totalCount;
                        if (resumeSearchBean.recordList != null)
                            mNowList.addAll(resumeSearchBean.recordList);
                    }
                    if (autoLoadPrevious) {
                        loadPreviousPredictionList(true);
                        return;
                    }
                } else
                    mPredictionView.callShowToastMessage(errorMsg, 0);
                mergeDataAndShow();
            }
        });
    }

    public void loadPreviousPredictionList(final boolean refresh) {
        mOffset = (refresh ? 0 : mPreviousList.size());
        if (mSearchParam == null)
            mSearchParam = new ResumeDao.SearchParam();
        mSearchParam.offset(mOffset).pageSize(CommonConst.DEFAULT_PAGE_SIZE).isJobHunting(true)
                .jobHuntingTimeType(CommonConst.JobHunterPredictionType.PREVIOUS).isSearchMineResume(true).build();
        ResumeDao.doSearch(mSearchParam, new IDefaultRequestReplyListener<ResumeSearchBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeSearchBean resumeSearchBean, int errorCode, String errorMsg) {
                mPredictionView.callCancelLoadingView(success, errorCode, errorMsg);
                if (mOffset == 0)
                    mPreviousList.clear();
                if (success) {
                    if (resumeSearchBean != null) {
                        mPreviousCount = resumeSearchBean.totalCount;
                        if (resumeSearchBean.recordList != null)
                            mPreviousList.addAll(resumeSearchBean.recordList);
                        mPredictionView.callUpdateLoadAllDataView(mPreviousList.size() >= resumeSearchBean.totalCount);
                    }
                }
                mergeDataAndShow();
                mPredictionView.callShowEmptyView((!mPreviousList.isEmpty()) || (!mNowList.isEmpty()));
            }
        });
    }

    public void collection(int position) {
        if (position < 0 || position >= mDataList.size())
            return;
        final ResumeBean resumeBean = mDataList.get(position);
        if (resumeBean == null)
            return;
        mPredictionView.callShowProgress(null, true);
        CollectionDao.doOrCancelCollection(resumeBean, new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(boolean success, String s, int errorCode, String errorMsg) {
                mPredictionView.callCancelProgress();
                if (success) {
                    resumeBean.collectStatus = (resumeBean.collectStatus == CommonConst.COLLECTION_NO ? CommonConst.COLLECTION_YES : CommonConst.COLLECTION_NO);
                    mPredictionView.callShowToastMessage(null, (resumeBean.collectStatus == CommonConst.COLLECTION_NO ? R.string.un_collection_success : R.string.collection_success));
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mDataList);
                    if (targetPosition != CommonConst.LIST_POSITION_NONE)
                        mPredictionView.callRefreshListItems(targetPosition);
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_COLLECTION_RESUME_CHANGED, resumeBean, mFilterTag);
                    return;
                }
                mPredictionView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    private void subscribeEvents(boolean isSubscribe) {
        if (isSubscribe) {
            UIEventsObservable.getInstance().subscribeEvent(IResumeEventsSubscriber.class, this, new IResumeEventsSubscriber() {
                @Override
                public boolean isFilterByTag(Object sendTag) {
                    return mFilterTag == sendTag;
                }

                @Override
                public boolean onResumeCollectionStatusChangedObserver(ResumeBean resumeBean, Object extra) {
                    if (resumeBean == null)
                        return false;
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mDataList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mDataList.get(targetPosition).collectStatus = resumeBean.collectStatus;
                    //只更新改变的Item
                    mPredictionView.callRefreshListItems(targetPosition);
                    return false;
                }

                @Override
                public boolean onResumeModifiedObserver(ResumeBean resumeBean, Object extra) {
                    if (resumeBean == null)
                        return false;
                    //通知简历被修改了
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mDataList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mDataList.get(targetPosition).refreshModifyInfo(resumeBean);
                    //只更新改变的Item
                    mPredictionView.callRefreshListItems(targetPosition);
                    return false;
                }

                @Override
                public boolean onResumeDeletedObserver(ResumeBean data, Object extra) {
                    //简历被删除
                    int targetPosition = ResumeDao.findTargetResumePosition(data, mDataList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    ResumeBean resumeBean = mDataList.remove(targetPosition);
                    if (mNowList.remove(resumeBean))
                        mNowCount--;
                    if (mPreviousList.remove(resumeBean))
                        mPreviousCount--;
                    //CommonConst.LIST_POSITION_NONE刷新整个列表
                    mPredictionView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    return false;
                }

                @Override
                public boolean onResumeUpdateByEngine(ResumeBean data, Object extra) {
                    if (data == null)
                        return false;
                    //通知简历被修改了
                    int targetPosition = ResumeDao.findTargetResumePosition(data, mDataList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mDataList.get(targetPosition).refreshModifyInfo(data);
                    mDataList.get(targetPosition).updateStatus = ResumeBean.UpdateState.STATE_UPDATE_DONE;
                    //只更新改变的Item
                    mPredictionView.callRefreshListItems(targetPosition);
                    return false;
                }
            });

        } else {
            UIEventsObservable.getInstance().stopSubscribeEvent(IResumeEventsSubscriber.class, this);
        }
    }

    private void mergeDataAndShow() {
//        testData(mNowList, 2);
//        testData(mPreviousList, 30);
        mDataList.clear();
        mDataList.add(null);
        mDataList.addAll(mNowList);
        mDataList.add(null);
        mDataList.addAll(mPreviousList);
        mPredictionView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
    }

//    static int index = 1;
//
//    private void testData(List<ResumeBean> list, int count) {
//        ResumeBean resumeBean;
//        if (list.isEmpty()) {
//            for (int i = 0; i < count; i++) {
//                resumeBean = new ResumeBean();
//                resumeBean.realName = "王大拿" + index;
//                resumeBean.title = "产品经理" + index;
//                index++;
//                resumeBean.candidateId = "idididi";
//                resumeBean.gender = CommonConst.SEX_MALE;
//                resumeBean.degree = 3;
//                resumeBean.location = 432;
//                resumeBean.yearExpr = 8;
//                resumeBean.collectStatus = 0;
//                resumeBean.company = "小米科技有限公司 / 产品部";
//                resumeBean.currentCompletion = 88;
//                resumeBean.isJobHunting = true;
//                resumeBean.jobHuntingExpiryTime = System.currentTimeMillis();
//                list.add(resumeBean);
//            }
//        }
//    }
}
