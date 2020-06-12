package com.baza.android.bzw.businesscontroller.resume.tab.presenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeEnableUpdateBaseInfoBean;
import com.baza.android.bzw.bean.resume.ResumeSearchBean;
import com.baza.android.bzw.bean.user.ExtraCountBean;
import com.baza.android.bzw.businesscontroller.resume.tab.viewinterface.ITabMineResumeView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.AccountDao;
import com.baza.android.bzw.dao.CollectionDao;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.dao.ResumeUpdateDao;
import com.baza.android.bzw.events.IDefaultEventsSubscriber;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.ISetStatusSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/18.
 * Title：
 * Note：
 */

public class TabMineResumePresenter extends BasePresenter {
    private static final int TAG_ON_REQUIRE_RESUME_UPDATE = 1;
    private static final int TAG_ON_REQUIRE_EXTENSION = 1 << 2;
    private ITabMineResumeView mTabResumeView;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private List<ResumeBean> mResumeList = new ArrayList<>();
    private Object mFilterTag = new Object();
    private boolean mIsHidden;
    private boolean mShouldRefresh = true;
    private boolean mJobHunterFilterSet;
    private int mTotalCount;
    private int mJobHunterCount;
    private int mTagBit;
    private ResumeDao.SearchParam mSearchParam;

    public TabMineResumePresenter(ITabMineResumeView tabResumeView, Bundle bundle) {
        this.mTabResumeView = tabResumeView;
    }

    @Override
    public void initialize() {
        subscribeEvents(true);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadNowPrediction();
            }
        }, 2000);
    }

    @Override
    public void onResume() {
        onHiddenChanged(mIsHidden);
    }

    public void onHiddenChanged(boolean hidden) {
        mIsHidden = hidden;
        if (!mIsHidden) {
            loadExtensionCountInfo();
            loadResumeEnableUpdateInfo();
            if (mShouldRefresh) {
                mShouldRefresh = false;
                loadMyResumeList(true);
            }
        }
    }


    public List<ResumeBean> getDataList() {
        return mResumeList;
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        subscribeEvents(false);
        mHandler = null;
    }


    public void collection(int position) {
        if (position < 0 || position >= mResumeList.size())
            return;
        mTabResumeView.callShowProgress(null, true);
        final ResumeBean resumeBean = mResumeList.get(position);
        CollectionDao.doOrCancelCollection(resumeBean, new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(boolean success, String s, int errorCode, String errorMsg) {
                mTabResumeView.callCancelProgress();
                if (success) {
                    resumeBean.collectStatus = (resumeBean.collectStatus == CommonConst.COLLECTION_NO ? CommonConst.COLLECTION_YES : CommonConst.COLLECTION_NO);
                    mTabResumeView.callShowToastMessage(null, (resumeBean.collectStatus == CommonConst.COLLECTION_NO ? R.string.un_collection_success : R.string.collection_success));
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mResumeList);
                    if (targetPosition != CommonConst.LIST_POSITION_NONE)
                        mTabResumeView.callRefreshListItems(targetPosition);
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_COLLECTION_RESUME_CHANGED, resumeBean, mFilterTag);
                    return;
                }
                mTabResumeView.callShowToastMessage(errorMsg, 0);
            }
        });
    }


    public void loadMyResumeList(final boolean refresh) {
        if (mSearchParam == null)
            mSearchParam = new ResumeDao.SearchParam();
        mSearchParam.isSearchMineResume(true).isJobHunting(mJobHunterFilterSet).isReturnJobHuntingCount(true).offset(refresh ? 0 : mResumeList.size()).pageSize(CommonConst.DEFAULT_PAGE_SIZE).build();
        ResumeDao.doSearch(mSearchParam, new IDefaultRequestReplyListener<ResumeSearchBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeSearchBean resumeSearchBean, int errorCode, String errorMsg) {
                mTabResumeView.callCancelLoadingView(success, errorCode, errorMsg);
                mTabResumeView.callCancelProgress();
                if (success) {
                    if (refresh)
                        mResumeList.clear();
                    if (resumeSearchBean != null) {
                        mTotalCount = resumeSearchBean.totalCount;
                        mJobHunterCount = resumeSearchBean.jobHuntingCount;
                        if (resumeSearchBean.recordList != null)
                            mResumeList.addAll(resumeSearchBean.recordList);
                        mTabResumeView.callUpdateLoadAllDataView(resumeSearchBean.recordList == null || resumeSearchBean.recordList.size() < CommonConst.DEFAULT_PAGE_SIZE);
                        mTabResumeView.callUpdateSearchCountView(mTotalCount, mJobHunterCount);
                    }
                    mTabResumeView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    mTabResumeView.callSetNoResultHintView(mResumeList.isEmpty());
                }
            }
        });
    }


    public void onJobHunterChanged(boolean enable) {
        mJobHunterFilterSet = enable;
        mTabResumeView.callShowProgress(null, true);
        loadMyResumeList(true);
    }

    private void loadResumeEnableUpdateInfo() {
        if ((mTagBit & TAG_ON_REQUIRE_RESUME_UPDATE) > 0)
            return;
        mTagBit |= TAG_ON_REQUIRE_RESUME_UPDATE;
        ResumeUpdateDao.loadResumeEnableUpdateBaseInfo(new IDefaultRequestReplyListener<ResumeEnableUpdateBaseInfoBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeEnableUpdateBaseInfoBean mResumeEnableUpdateInfoBean, int errorCode, String errorMsg) {
                mTagBit &= ~TAG_ON_REQUIRE_RESUME_UPDATE;
                if (success && mResumeEnableUpdateInfoBean != null)
                    mTabResumeView.callUpdateResumeUpdateView(mResumeEnableUpdateInfoBean.waitUpdateCount);
            }
        });
    }

    private void loadExtensionCountInfo() {
        if ((mTagBit & TAG_ON_REQUIRE_EXTENSION) > 0)
            return;
        mTagBit |= TAG_ON_REQUIRE_EXTENSION;
        AccountDao.getExtensionCountInfo(new IDefaultRequestReplyListener<ExtraCountBean>() {
            @Override
            public void onRequestReply(boolean success, ExtraCountBean extraCountBean, int errorCode, String errorMsg) {
                mTagBit &= ~TAG_ON_REQUIRE_EXTENSION;
                if (success && extraCountBean != null) {
                    mTabResumeView.callUpdateAllResumeCountView(extraCountBean.candidateCount);
                }
            }
        });
    }

    private void loadNowPrediction() {
        ResumeDao.SearchParam searchParam = new ResumeDao.SearchParam().offset(0).pageSize(1).isJobHunting(true)
                .jobHuntingTimeType(CommonConst.JobHunterPredictionType.NOW).isSearchMineResume(true).build();
        ResumeDao.doSearch(searchParam, new IDefaultRequestReplyListener<ResumeSearchBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeSearchBean resumeSearchBean, int errorCode, String errorMsg) {
                if (success)
                    mTabResumeView.callUpdatePredictionHintView((resumeSearchBean != null && resumeSearchBean.totalCount > 0));

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
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mResumeList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mResumeList.get(targetPosition).collectStatus = resumeBean.collectStatus;
                    //只更新改变的Item
                    mTabResumeView.callRefreshListItems(targetPosition);
                    return false;
                }

                @Override
                public boolean onResumeModifiedObserver(ResumeBean resumeBean, Object extra) {
                    if (resumeBean == null)
                        return false;
                    //通知简历被修改了
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mResumeList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mResumeList.get(targetPosition).refreshModifyInfo(resumeBean);
                    //只更新改变的Item
                    mTabResumeView.callRefreshListItems(targetPosition);
                    return false;
                }

                @Override
                public boolean onResumeDeletedObserver(ResumeBean data, Object extra) {
                    //简历被删除
                    int targetPosition = ResumeDao.findTargetResumePosition(data, mResumeList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    ResumeBean resumeBean = mResumeList.remove(targetPosition);
                    //CommonConst.LIST_POSITION_NONE刷新整个列表
                    mTabResumeView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    mTotalCount--;
                    if (resumeBean.isJobHunting && mJobHunterCount > 0)
                        mJobHunterCount--;
                    mTabResumeView.callUpdateSearchCountView(mTotalCount, mJobHunterCount);
                    return false;
                }

                @Override
                public boolean onResumeUpdateByEngine(ResumeBean data, Object extra) {
                    if (data == null)
                        return false;
                    //通知简历被修改了
                    int targetPosition = ResumeDao.findTargetResumePosition(data, mResumeList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mResumeList.get(targetPosition).refreshModifyInfo(data);
                    mResumeList.get(targetPosition).updateStatus = ResumeBean.UpdateState.STATE_UPDATE_DONE;
                    //只更新改变的Item
                    mTabResumeView.callRefreshListItems(targetPosition);
                    return false;
                }

//                @Override
//                public boolean onResumeLabelsChanged(ResumeBean data, Object extra) {
//                    if (data == null)
//                        return false;
//                    //通知查看的历史记录可能改变
//                    int targetPosition = ResumeDao.findTargetResumePosition(data, mResumeList);
//                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
//                        return false;
//                    ResumeBean c = mResumeList.get(targetPosition);
//                    if (data.tagBindingList == null || data.tagBindingList.isEmpty())
//                        c.tagBindingList = null;
//                    else {
//                        if (c.tagBindingList == null)
//                            c.tagBindingList = new ArrayList<>(data.tagBindingList.size());
//                        c.tagBindingList.clear();
//                        c.tagBindingList.addAll(data.tagBindingList);
//                    }
//                    // 只更新改变的Item
//                    mTabResumeView.callRefreshListItems(targetPosition);
//                    return false;
//                }

                @Override
                public boolean onResumeCreatedObserver(ResumeBean data, Object extra) {
                    if (!mIsHidden) {
                        loadMyResumeList(true);
                        return false;
                    }
                    mShouldRefresh = true;
                    return false;
                }
            });
            UIEventsObservable.getInstance().subscribeEvent(ISetStatusSubscriber.class, this, new ISetStatusSubscriber() {
                @Override
                public boolean onTradeOpen(Object extra) {
                    onTradeStatusChanged();
                    return false;
                }

                @Override
                public boolean onTradeClose(Object extra) {
                    onTradeStatusChanged();
                    return false;
                }
            });

            UIEventsObservable.getInstance().subscribeEvent(IDefaultEventsSubscriber.class, this, new IDefaultEventsSubscriber() {
                @Override
                public boolean killEvent(String action, Object data) {
                    if (ActionConst.ACTION_EVENT_RECEIVE_NAME_LIST.equals(action))
                        mShouldRefresh = true;
                    return false;
                }
            });
        } else {
            UIEventsObservable.getInstance().stopSubscribeEvent(IResumeEventsSubscriber.class, this);
            UIEventsObservable.getInstance().stopSubscribeEvent(ISetStatusSubscriber.class, this);
            UIEventsObservable.getInstance().stopSubscribeEvent(IDefaultEventsSubscriber.class, this);
        }
    }

    private void onTradeStatusChanged() {
        mResumeList.clear();
        mTabResumeView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
        if (!mIsHidden) {
            loadMyResumeList(true);
            return;
        }
        mShouldRefresh = true;
    }
}
