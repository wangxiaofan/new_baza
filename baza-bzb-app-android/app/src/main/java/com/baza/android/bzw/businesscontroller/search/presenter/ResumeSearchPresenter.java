package com.baza.android.bzw.businesscontroller.search.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.common.LocalAreaBean;
import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeSearchBean;
import com.baza.android.bzw.bean.searchfilterbean.SearchFilterInfoBean;
import com.baza.android.bzw.businesscontroller.search.EditSearchConfigActivity;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IResumeSearchView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.dao.CollectionDao;
import com.baza.android.bzw.dao.LabelDao;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.dao.SmartGroupDao;
import com.baza.android.bzw.events.ILabelEventSubscriber;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.logger.CompanySearchLogger;
import com.baza.android.bzw.log.logger.ResumeLogger;
import com.baza.android.bzw.manager.LabelCacheManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/22.
 * Title：
 * Note：
 */

public class ResumeSearchPresenter extends BasePresenter {
    private IResumeSearchView mResumeSearchView;
    private SearchFilterInfoBean mSearchFilterInfo;
    private ArrayList<Label> mLabelLibrary = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    //搜索自己的人才还是全部人才
    private boolean mShouldDoSearchForOutSideFilter;
    private String mLastKeyword;
    private Object mFilterTag = new Object();
    private List<ResumeBean> mResumeList = new ArrayList<>();
    private int mCurrentRequestId;
    private int mTotalCount;
    private int mJobHunterOrNameListCount;
    private boolean mJobHunterTagSet;
    private boolean mBackTagForNeverSearch;
    private ResumeLogger mResumeSearchLogger = new ResumeLogger();
    private CompanySearchLogger companySearchLogger = new CompanySearchLogger();
    private HashSet<String> mSelectedIdForGroupAdd;
    private IResumeSearchView.Param mIntentParam;
    private ResumeDao.SearchParam mSearchParam;

    public ResumeSearchPresenter(IResumeSearchView mResumeSearchView, IResumeSearchView.Param intentParam) {
        this.mResumeSearchView = mResumeSearchView;
        this.mIntentParam = intentParam;
        setSearchFilterInfo(mIntentParam.searchFilterInfoPrevious);
        this.mShouldDoSearchForOutSideFilter = (mIntentParam.searchFilterInfoPrevious != null);
        mResumeSearchLogger.setPageCode(mResumeSearchView.callGetBindActivity(), "MyResumesSearch");
        mResumeSearchLogger.setFormComponentName(mIntentParam.formComponentName);
        companySearchLogger.setPageCode(mResumeSearchView.callGetBindActivity(), "MyTalent");
        if (mIntentParam.searchMode == CommonConst.INT_SEARCH_TYPE_GROUP_ADD)
            mSelectedIdForGroupAdd = new HashSet<>();
        else if (mIntentParam.searchMode == CommonConst.INT_SEARCH_TYPE_MINE_JOB_HUNTER_PREDICTION)
            mJobHunterTagSet = true;

    }

    public boolean hasBackTagForNeverSearch() {
        return mBackTagForNeverSearch;
    }

    public ResumeLogger getResumeSearchLogger() {
        return mResumeSearchLogger;
    }

    public HashSet<String> getSelectedIdSetForGroupAdd() {
        return mSelectedIdForGroupAdd;
    }

    @Override
    public void initialize() {
        mResumeSearchView.callUpdateKeywordHint(R.string.hint_search_mine_candidate);
        subscriberResumeEvents(true);
        if (mShouldDoSearchForOutSideFilter)
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    doSearch(true);
                }
            });
        else {
            mBackTagForNeverSearch = true;
            EditSearchConfigActivity.launch(RequestCodeConst.INT_REQUEST_SET_SEARCH_CONDITION, mResumeSearchView.callGetBindActivity(), mIntentParam.searchMode, null);
        }
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        subscriberResumeEvents(false);
    }

    public List<ResumeBean> getResumeList() {
        return mResumeList;
    }

    public int getSearchMode() {
        return mIntentParam.searchMode;
    }

    public String getLastKeyword() {
        return mLastKeyword;
    }

    public boolean isSortByCreateTime() {
        return (mSearchFilterInfo != null && mSearchFilterInfo.mandatorySort == CommonConst.RESUME_SEARCH_SORT_ORDER_CREATE_TIME);
    }

    private void setSearchFilterInfo(SearchFilterInfoBean sf) {
        if (sf != null)
            this.mSearchFilterInfo = sf;
        if (mSearchFilterInfo == null)
            mSearchFilterInfo = new SearchFilterInfoBean();
        if (mSearchFilterInfo.cityCode == LocalAreaBean.CODE_NONE) {
            mSearchFilterInfo.cityName = mResumeSearchView.callGetResources().getString(R.string.city_all);
        }
    }

    public SearchFilterInfoBean getSearchFilterInfo() {
        return mSearchFilterInfo;
    }

    public ArrayList<Label> getLabelLibrary() {
        return mLabelLibrary;
    }

    public void prepareLabels() {
        if (mLabelLibrary.isEmpty())
            mLabelLibrary.addAll(LabelCacheManager.getInstance().getAllLabels());
        if (mLabelLibrary.isEmpty()) {
            LabelDao.loadLabelLibrary(new IDefaultRequestReplyListener<List<Label>>() {
                @Override
                public void onRequestReply(boolean success, List<Label> list, int errorCode, String errorMsg) {
                    if (success) {
                        if (list != null && !list.isEmpty()) {
                            mLabelLibrary.clear();
                            mLabelLibrary.addAll(list);
                            mResumeSearchView.callSetLabelLibrary();
                            LabelCacheManager.getInstance().setLabels(mLabelLibrary);
                            UIEventsObservable.getInstance().postEvent(ILabelEventSubscriber.class, ActionConst.ACTION_EVENT_LABEL_LIBRARY_GET, null, mFilterTag);
                        }
                        return;
                    }
                    mResumeSearchView.callShowToastMessage(errorMsg, 0);
                }
            });
        }
    }

    public void parseSearchCondition(Intent data) {
        mBackTagForNeverSearch = false;
        SearchFilterInfoBean searchFilterInfoBean = (SearchFilterInfoBean) data.getSerializableExtra("searchFilter");
        mLastKeyword = (searchFilterInfoBean != null ? searchFilterInfoBean.keyWord : data.getStringExtra("keyword"));
        mResumeSearchView.callUpdateKeywordView(mLastKeyword);
        if (searchFilterInfoBean != null) {
            companySearchLogger.sendHistoryClickLog(mResumeSearchView.callGetBindActivity(), searchFilterInfoBean.toString());
            setSearchFilterInfo(searchFilterInfoBean);
            mResumeSearchView.callUpdateSearchFilterView();
        }
        mSearchFilterInfo.attachKeyWord(mLastKeyword);
        doSearch(true);
    }

    /**
     * 订阅简历事件
     */
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
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mResumeList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    ResumeBean resumeBeanFound = mResumeList.get(targetPosition);
                    resumeBeanFound.collectStatus = resumeBean.collectStatus;
                    //只更新改变的Item
                    mResumeSearchView.callRefreshListItems(targetPosition, CommonConst.LIST_POSITION_NONE);
                    return false;
                }

                @Override
                public boolean onResumeModifiedObserver(ResumeBean resumeBean, Object extra) {
                    if (resumeBean == null)
                        return false;
                    //通知简历被修改了
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mResumeList);
                    if (targetPosition == -1)
                        return false;
                    //搜索时的显示细节特殊处理
                    mResumeList.get(targetPosition).refreshModifyInfo(resumeBean);
                    //只更新改变的Item
                    mResumeSearchView.callRefreshListItems(targetPosition, CommonConst.LIST_POSITION_NONE);
                    return false;
                }

                @Override
                public boolean onResumeDeletedObserver(ResumeBean data, Object extra) {
                    //简历被删除
                    int targetPosition = ResumeDao.findTargetResumePosition(data, mResumeList);
                    if (targetPosition == -1)
                        return false;
                    ResumeBean resumeBean = mResumeList.remove(targetPosition);
                    //-1刷新整个列表
                    mResumeSearchView.callRefreshListItems(CommonConst.LIST_POSITION_NONE, CommonConst.LIST_POSITION_NONE);
                    //搜索到的总数量建一
                    --mTotalCount;
                    if (resumeBean.isJobHunting && mJobHunterOrNameListCount > 0)
                        mJobHunterOrNameListCount--;
                    mResumeSearchView.callUpdateSearchResultHint(mTotalCount, mJobHunterOrNameListCount, true, mIntentParam.searchMode != CommonConst.INT_SEARCH_TYPE_MINE_JOB_HUNTER_PREDICTION);
                    if (mSelectedIdForGroupAdd != null)
                        mSelectedIdForGroupAdd.remove(resumeBean.candidateId);
                    return false;
                }

                @Override
                public boolean onResumeBeWatchedObserver(ResumeBean resumeBean, Object extra) {
                    if (resumeBean == null)
                        return false;
                    int targetPosition = ResumeDao.findTargetResumePositionByTalentId(resumeBean.talentId, mResumeList);
                    if (targetPosition != CommonConst.LIST_POSITION_NONE)
                        mResumeSearchView.callRefreshListItems(targetPosition, CommonConst.LIST_POSITION_NONE);
                    return false;
                }
                //                @Override
//                public boolean onResumeLabelsChanged(ResumeBean data, Object extra) {
//                    if (data == null)
//                        return false;
//                    //通知查看的历史记录可能改变
//                    int targetPosition = ResumeDao.findTargetResumePosition(data, mResumeList);
//                    if (targetPosition == -1)
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
//                    mResumeSearchView.callRefreshListItems(targetPosition, CommonConst.LIST_POSITION_NONE);
//                    return false;
//                }

                @Override
                public boolean onResumeUpdateByEngine(ResumeBean data, Object extra) {
                    if (data == null)
                        return false;
                    int targetPosition = ResumeDao.findTargetResumePosition(data, mResumeList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mResumeList.get(targetPosition).refreshModifyInfo(data);
                    mResumeList.get(targetPosition).updateStatus = ResumeBean.UpdateState.STATE_UPDATE_DONE;
                    //只更新改变的Item
                    mResumeSearchView.callRefreshListItems(targetPosition, CommonConst.LIST_POSITION_NONE);
                    return false;
                }
            });

            UIEventsObservable.getInstance().subscribeEvent(ILabelEventSubscriber.class, this, new ILabelEventSubscriber() {
                @Override
                public boolean isFilterByTag(Object sendTag) {
                    return mFilterTag == sendTag;
                }

                @Override
                public boolean onLabelCreated(Label label) {
                    mLabelLibrary.clear();
                    mLabelLibrary.addAll(LabelCacheManager.getInstance().getAllLabels());
                    mResumeSearchView.callSetLabelLibrary();
                    return false;
                }

                @Override
                public boolean onLabelDeleted(Label label) {
                    mLabelLibrary.clear();
                    mLabelLibrary.addAll(LabelCacheManager.getInstance().getAllLabels());
                    mResumeSearchView.callSetLabelLibrary();
                    return false;
                }
            });
        } else {
            UIEventsObservable.getInstance().stopSubscribeEvent(ILabelEventSubscriber.class, this);
            UIEventsObservable.getInstance().stopSubscribeEvent(IResumeEventsSubscriber.class, this);
        }
    }

    public void doSearch(boolean refresh) {
        ++mCurrentRequestId;
        realSearch(mCurrentRequestId, (refresh ? 0 : mResumeList.size()));
        if (mSearchFilterInfo.isCurrentSearchCanBeSave()) {
            //保存搜索条件
            ResumeDao.saveSearchFilterToLocalDb(mSearchFilterInfo, UserInfoManager.getInstance().getUserInfo().unionId);
        }
    }

    private void realSearch(final int requestId, final int offset) {
        if (offset == 0)
            mResumeSearchView.callShowLoadingView(null);
        if (mSearchParam == null)
            mSearchParam = new ResumeDao.SearchParam();
        mSearchParam
                .offset(offset)
                .isSearchMineResume(true)
                .isReturnJobHuntingCount(true)
                .searchId(mResumeSearchLogger.interceptRefreshSearchIdOuter(offset == 0))
                .pageSize(CommonConst.DEFAULT_PAGE_SIZE)
                .searchFilter(mSearchFilterInfo)
                .isJobHunting(mJobHunterTagSet)
                .isRecentContact(mIntentParam.searchMode == CommonConst.INT_SEARCH_TYPE_SEEK)
                .isCollected(mIntentParam.searchMode == CommonConst.INT_SEARCH_TYPE_COLLECTION)
                .build();
        ResumeDao.doSearch(mSearchParam, new IDefaultRequestReplyListener<ResumeSearchBean>() {

            @Override
            public void onRequestReply(boolean success, final ResumeSearchBean candidateSearchBean, int errorCode, String errorMsg) {
                if (requestId != mCurrentRequestId)
                    return;
                mResumeSearchView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success && candidateSearchBean != null) {
                    if (offset == 0) {
                        mResumeList.clear();
                        if (mSelectedIdForGroupAdd != null)
                            mSelectedIdForGroupAdd.clear();
                    }
                    if (candidateSearchBean.recordList != null && !candidateSearchBean.recordList.isEmpty())
                        mResumeList.addAll(candidateSearchBean.recordList);
                    mResumeSearchView.callRefreshListItems(CommonConst.LIST_POSITION_NONE, (offset == 0 ? 0 : CommonConst.LIST_POSITION_NONE));
                    mTotalCount = candidateSearchBean.totalCount;
                    mJobHunterOrNameListCount = candidateSearchBean.jobHuntingCount;
                    mResumeSearchView.callUpdateSearchResultHint(mTotalCount, mJobHunterOrNameListCount, true, mIntentParam.searchMode != CommonConst.INT_SEARCH_TYPE_MINE_JOB_HUNTER_PREDICTION);
                    mResumeSearchView.callUpdateLoadAllDataView(candidateSearchBean.recordList == null || candidateSearchBean.recordList.size() < CommonConst.DEFAULT_PAGE_SIZE);
                    if (offset == 0)
                        mResumeSearchLogger.sendDoSearchLog(mResumeSearchView.callGetBindActivity(), mSearchParam.getKeyMap(), mTotalCount, mResumeList);
                    else
                        mResumeSearchLogger.sendDoSearchLog(mResumeSearchView.callGetBindActivity(), mSearchParam.getKeyMap(), mTotalCount, mResumeList.size() / CommonConst.DEFAULT_PAGE_SIZE + (mResumeList.size() % CommonConst.DEFAULT_PAGE_SIZE == 0 ? 0 : 1));
                }
            }
        });
    }

    public void collection(int position) {
        if (position < 0 || position >= mResumeList.size())
            return;
        mResumeSearchView.callShowProgress(null, true);
        final ResumeBean resumeBean = mResumeList.get(position);
        CollectionDao.doOrCancelCollection(resumeBean, new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(boolean success, String s, int errorCode, String errorMsg) {
                mResumeSearchView.callCancelProgress();
                if (success) {
                    resumeBean.collectStatus = (resumeBean.collectStatus == CommonConst.COLLECTION_NO ? CommonConst.COLLECTION_YES : CommonConst.COLLECTION_NO);
                    mResumeSearchView.callShowToastMessage(null, (resumeBean.collectStatus == CommonConst.COLLECTION_NO ? R.string.un_collection_success : R.string.collection_success));
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mResumeList);
                    if (targetPosition != CommonConst.LIST_POSITION_NONE)
                        mResumeSearchView.callRefreshListItems(targetPosition, CommonConst.LIST_POSITION_NONE);
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_COLLECTION_RESUME_CHANGED, resumeBean, mFilterTag);
                    return;
                }
                mResumeSearchView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void onOnlySearchJobFinderSet(boolean enable) {
        mJobHunterTagSet = enable;
        doSearch(true);
    }

    public void addCurrentAllResumesToGroup() {
        for (int i = 0, size = mResumeList.size(); i < size; i++) {
            mSelectedIdForGroupAdd.add(mResumeList.get(i).candidateId);
        }
        mResumeSearchView.callRefreshListItems(CommonConst.LIST_POSITION_NONE, CommonConst.LIST_POSITION_NONE);
    }

    public void submitResumesToGroup() {
        if (mSelectedIdForGroupAdd.size() == 0) {
            mResumeSearchView.callShowToastMessage(null, R.string.please_select_resume_to_group);
            return;
        }
        mResumeSearchView.callShowProgress(null, true);
        SmartGroupDao.addResumesToGroup(mIntentParam.smartGroupId, mSelectedIdForGroupAdd, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mResumeSearchView.callCancelProgress();
                if (success) {
                    mResumeSearchView.callGetBindActivity().setResult(Activity.RESULT_OK);
                    mResumeSearchView.callGetBindActivity().finish();
                    return;
                }
                mResumeSearchView.callShowToastMessage(errorMsg, 0);
            }
        });
    }
}