package com.baza.android.bzw.businesscontroller.resume.mine.presenter;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeSearchBean;
import com.baza.android.bzw.businesscontroller.resume.mine.viewinterface.ISeekView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.CollectionDao;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;

/**
 * Created by Vincent.Lei on 2017/6/7.
 * Title：
 * Note：
 */

public class SeekPresenter extends BasePresenter {
    private ISeekView mSeekView;
    private ArrayList<ResumeBean> mList = new ArrayList<>();
    private Object mFilterTag = new Object();
    private int mTotalCount;
    private boolean mJobHunterFilterSet;
    private ResumeDao.SearchParam mSearchParam;

    public SeekPresenter(ISeekView mSeekView) {
        this.mSeekView = mSeekView;
    }

    @Override
    public void initialize() {
        subscribeEvents(true);
        getSeekList(true);
    }

    public ArrayList<ResumeBean> getDataList() {
        return mList;
    }

    public int getTotalCount() {
        return mTotalCount;
    }

    @Override
    public void onDestroy() {
        subscribeEvents(false);
    }

    public void onJobHunterChanged(boolean enable) {
        mJobHunterFilterSet = enable;
        mSeekView.callShowLoadingView(null);
        getSeekList(true);
    }

    public void getSeekList(final boolean refresh) {
        if (mSearchParam == null)
            mSearchParam = new ResumeDao.SearchParam();
        mSearchParam.offset(refresh ? 0 : mList.size()).pageSize(CommonConst.DEFAULT_PAGE_SIZE).isSearchMineResume(true)
                .isJobHunting(mJobHunterFilterSet)
                .isRecentContact(true).build();

        ResumeDao.doSearch(mSearchParam, new IDefaultRequestReplyListener<ResumeSearchBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeSearchBean candidateSearchBean, int errorCode, String errorMsg) {
                mSeekView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success) {
                    if (refresh)
                        mList.clear();
                    if (candidateSearchBean != null) {
                        mTotalCount = candidateSearchBean.totalCount;
                        if (candidateSearchBean.recordList != null)
                            mList.addAll(candidateSearchBean.recordList);
                    }
                    mSeekView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    mSeekView.callUpdateLoadAllDataView(candidateSearchBean == null || candidateSearchBean.recordList == null || candidateSearchBean.recordList.size() < CommonConst.DEFAULT_PAGE_SIZE);
                }
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
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    ResumeBean resumeBeanFound = mList.get(targetPosition);
                    resumeBeanFound.collectStatus = resumeBean.collectStatus;
                    //只更新改变的Item
                    mSeekView.callRefreshListItems(targetPosition);
                    return false;
                }

                @Override
                public boolean onResumeModifiedObserver(ResumeBean resumeBean, Object extra) {
                    if (resumeBean == null)
                        return false;
                    //通知简历被修改了
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mList.get(targetPosition).refreshModifyInfo(resumeBean);
                    //只更新改变的Item
                    mSeekView.callRefreshListItems(targetPosition);
                    return false;
                }

                @Override
                public boolean onResumeDeletedObserver(ResumeBean data, Object extra) {
                    //简历被删除
                    int targetPosition = ResumeDao.findTargetResumePosition(data, mList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mList.remove(targetPosition);
                    // CommonConst.LIST_POSITION_NONE刷新整个列表
                    mTotalCount--;
                    mSeekView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    return false;
                }

                @Override
                public boolean onResumeLabelsChanged(ResumeBean data, Object extra) {
                    if (data == null)
                        return false;
                    //通知查看的历史记录可能改变
                    int targetPosition = ResumeDao.findTargetResumePosition(data, mList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    ResumeBean c = mList.get(targetPosition);
                    if (data.tagBindingList == null || data.tagBindingList.isEmpty())
                        c.tagBindingList = null;
                    else {
                        if (c.tagBindingList == null)
                            c.tagBindingList = new ArrayList<>(data.tagBindingList.size());
                        c.tagBindingList.clear();
                        c.tagBindingList.addAll(data.tagBindingList);
                    }
                    // 只更新改变的Item
                    mSeekView.callRefreshListItems(targetPosition);
                    return false;
                }

                @Override
                public boolean onResumeUpdateByEngine(ResumeBean data, Object extra) {
                    if (data == null)
                        return false;
                    int targetPosition = ResumeDao.findTargetResumePosition(data, mList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mList.get(targetPosition).refreshModifyInfo(data);
                    //只更新改变的Item
                    mSeekView.callRefreshListItems(targetPosition);
                    return false;
                }
            });
        } else {
            UIEventsObservable.getInstance().stopSubscribeEvent(IResumeEventsSubscriber.class, this);
        }

    }

    public void collection(int position) {
        if (position < 0 || position >= mList.size())
            return;
        mSeekView.callShowProgress(null, true);
        final ResumeBean resumeBean = mList.get(position);
        CollectionDao.doOrCancelCollection(resumeBean, new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(boolean success, String s, int errorCode, String errorMsg) {
                mSeekView.callCancelProgress();
                if (success) {
                    resumeBean.collectStatus = (resumeBean.collectStatus == CommonConst.COLLECTION_NO ? CommonConst.COLLECTION_YES : CommonConst.COLLECTION_NO);
                    mSeekView.callShowToastMessage(null, (resumeBean.collectStatus == CommonConst.COLLECTION_NO ? R.string.un_collection_success : R.string.collection_success));
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mList);
                    if (targetPosition != CommonConst.LIST_POSITION_NONE)
                        mSeekView.callRefreshListItems(targetPosition);
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_COLLECTION_RESUME_CHANGED, resumeBean, mFilterTag);
                    return;
                }
                mSeekView.callShowToastMessage(errorMsg, 0);
            }
        });
    }
}
