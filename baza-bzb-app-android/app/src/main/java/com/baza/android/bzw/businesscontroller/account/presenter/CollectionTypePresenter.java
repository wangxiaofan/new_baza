package com.baza.android.bzw.businesscontroller.account.presenter;

import android.os.Handler;
import android.os.Looper;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeSearchBean;
import com.baza.android.bzw.businesscontroller.account.viewinterface.ICollectionTypeView;
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
 * Created by Vincent.Lei on 2018/8/22.
 * Title：
 * Note：
 */
public class CollectionTypePresenter extends BasePresenter {
    private ICollectionTypeView mCollectionTypeView;
    private List<ResumeBean> mCollectionList = new ArrayList<>();
    private boolean mIsJobHunting;
    private int mTotalCount;
    private int mJobHunterCount;
    private Object mFilterTag = new Object();
    private ResumeDao.SearchParam mSearchParam;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static final long COLLECTION_RESULT_REPLY_DELAY = 3000;

    public CollectionTypePresenter(ICollectionTypeView collectionTypeView) {
        this.mCollectionTypeView = collectionTypeView;
    }

    @Override
    public void initialize() {
        subscriberResumeEvents(true);
        loadCollectionList(true, true);
    }

    @Override
    public void onDestroy() {
        subscriberResumeEvents(false);
        mHandler.removeCallbacksAndMessages(null);
    }

    public List<ResumeBean> getDataList() {
        return mCollectionList;
    }

    public int getTotalCount() {
        return mTotalCount;
    }

    public void onJobHunterChanged(boolean enable) {
        mIsJobHunting = enable;
        loadCollectionList(true, true);
    }

    public void loadCollectionList(final boolean refresh, boolean showLoadingView) {
        if (showLoadingView)
            mCollectionTypeView.callShowLoadingView(null);
        if (mSearchParam == null)
            mSearchParam = new ResumeDao.SearchParam();
        mSearchParam.offset(refresh ? 0 : mCollectionList.size()).pageSize(CommonConst.DEFAULT_PAGE_SIZE).isJobHunting(mIsJobHunting)
                .isCollected(true).isExcludeMine(false).isReturnJobHuntingCount(true).build();
        ResumeDao.doSearch(mSearchParam, new IDefaultRequestReplyListener<ResumeSearchBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeSearchBean resumeSearchBean, int errorCode, String errorMsg) {
                mCollectionTypeView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success) {
                    if (refresh)
                        mCollectionList.clear();
                    if (resumeSearchBean != null) {
                        mTotalCount = resumeSearchBean.totalCount;
                        mJobHunterCount = resumeSearchBean.jobHuntingCount;
                        if (resumeSearchBean.recordList != null)
                            mCollectionList.addAll(resumeSearchBean.recordList);
                    }
                    mCollectionTypeView.callRefreshListViews(CommonConst.LIST_POSITION_NONE, mTotalCount);
                    mCollectionTypeView.callUpdateSearchCountView(mTotalCount, mJobHunterCount);
                    mCollectionTypeView.callUpdateLoadAllDataView(mCollectionList.size() >= mTotalCount);
                    return;
                }
                mCollectionTypeView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void collection(int position) {
        if (position < 0 || position >= mCollectionList.size())
            return;
        mCollectionTypeView.callShowProgress(null, true);
        final ResumeBean resumeBean = mCollectionList.get(position);
        CollectionDao.doOrCancelCollection(resumeBean, new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(final boolean success, String s, int errorCode, final String errorMsg) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCollectionTypeView.callCancelProgress();
                        if (success) {
                            resumeBean.collectStatus = CommonConst.COLLECTION_NO;
                            int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mCollectionList);
                            if (targetPosition != CommonConst.LIST_POSITION_NONE)
                                mCollectionList.remove(targetPosition);
                            mTotalCount--;
                            mCollectionTypeView.callRefreshListViews(CommonConst.LIST_POSITION_NONE, mTotalCount);
                            mCollectionTypeView.callShowToastMessage(null, R.string.un_collection_success_delay);
                            UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_COLLECTION_RESUME_CHANGED, resumeBean, mFilterTag);
                            return;
                        }
                        mCollectionTypeView.callShowToastMessage(errorMsg, 0);
                    }
                }, COLLECTION_RESULT_REPLY_DELAY);
            }
        });
    }

    private void subscriberResumeEvents(boolean isRegister) {
        if (isRegister) {
            UIEventsObservable.getInstance().subscribeEvent(IResumeEventsSubscriber.class, this, new IResumeEventsSubscriber() {
                @Override
                public boolean isFilterByTag(Object sendTag) {
                    return mFilterTag == sendTag;
                }

                @Override
                public boolean onResumeCollectionStatusChangedObserver(ResumeBean resumeBean, Object extra) {
                    if (resumeBean == null)
                        return false;
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mCollectionList);
                    //当前收藏列表中不存在该条数据并且这条数据是收藏状态的数据 加入列表中
                    if (targetPosition == CommonConst.LIST_POSITION_NONE && resumeBean.collectStatus == CommonConst.COLLECTION_YES) {
                        mTotalCount++;
                        mCollectionList.add(0, resumeBean);
                    }
                    //当前收藏列表中某条数据由收藏变为未收藏 从列表中移除
                    else if (targetPosition != CommonConst.LIST_POSITION_NONE && resumeBean.collectStatus == CommonConst.COLLECTION_NO) {
                        mTotalCount--;
                        mCollectionList.remove(targetPosition);
                    }
                    mCollectionTypeView.callRefreshListViews(CommonConst.LIST_POSITION_NONE, mTotalCount);
                    return false;
                }

                @Override
                public boolean onResumeModifiedObserver(ResumeBean resumeBean, Object extra) {
                    if (resumeBean == null)
                        return false;
                    //通知简历被修改了
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mCollectionList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mCollectionList.get(targetPosition).refreshModifyInfo(resumeBean);
                    //只更新改变的Item
                    mCollectionTypeView.callRefreshListViews(targetPosition, mTotalCount);
                    return false;
                }

                @Override
                public boolean onResumeDeletedObserver(ResumeBean data, Object extra) {
                    //简历被删除
                    int targetPosition = ResumeDao.findTargetResumePosition(data, mCollectionList);
                    if (targetPosition == -1)
                        return false;
                    mCollectionList.remove(targetPosition);
                    //-1刷新整个列表
                    mTotalCount--;
                    mCollectionTypeView.callRefreshListViews(CommonConst.LIST_POSITION_NONE, mTotalCount);
                    return false;
                }

                @Override
                public boolean onResumeUpdateByEngine(ResumeBean data, Object extra) {
                    if (data == null)
                        return false;
                    int targetPosition = ResumeDao.findTargetResumePosition(data, mCollectionList);
                    if (targetPosition == -1)
                        return false;
                    mCollectionList.get(targetPosition).refreshModifyInfo(data);
                    mCollectionList.get(targetPosition).updateStatus = ResumeBean.UpdateState.STATE_UPDATE_DONE;
                    //只更新改变的Item
                    mCollectionTypeView.callRefreshListViews(targetPosition, mTotalCount);
                    return false;
                }
            });
        } else {
            UIEventsObservable.getInstance().stopSubscribeEvent(IResumeEventsSubscriber.class, this);
        }
    }
}
