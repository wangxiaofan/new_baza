package com.baza.android.bzw.businesscontroller.tracking.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.common.LocalAreaBean;
import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.resume.BubbleBean;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.TrackingListBean;
import com.baza.android.bzw.bean.resume.TrackingSearchResultBean;
import com.baza.android.bzw.bean.searchfilterbean.TrackingSearchFilterInfoBean;
import com.baza.android.bzw.businesscontroller.search.EditSearchConfigActivity;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IEditSearchConfigView;
import com.baza.android.bzw.businesscontroller.tracking.viewinterface.ITrackingSearchView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.dao.CollectionDao;
import com.baza.android.bzw.dao.LabelDao;
import com.baza.android.bzw.dao.SmartGroupDao;
import com.baza.android.bzw.dao.TrackingResumeDao;
import com.baza.android.bzw.events.ILabelEventSubscriber;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.logger.CompanySearchLogger;
import com.baza.android.bzw.manager.LabelCacheManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * trackingList 搜索
 */
public class TrackingSearchPresenter extends BasePresenter {

    private ITrackingSearchView mResumeSearchView;
    private TrackingSearchFilterInfoBean mSearchFilterInfo;
    private ArrayList<Label> mLabelLibrary = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    //搜索自己的人才还是全部人才
    private boolean mShouldDoSearchForOutSideFilter;
    private String mLastKeyword;
    private Object mFilterTag = new Object();
    private List<TrackingListBean> mResumeList = new ArrayList<>();
    private List<BubbleBean> mBubbleList = new ArrayList<>();
    private int mCurrentRequestId;
    private int mTotalCount;
    private int mJobHunterOrNameListCount;
    private boolean mJobHunterTagSet;
    private boolean mBackTagForNeverSearch;
    private CompanySearchLogger mResumeSearchLogger = new CompanySearchLogger();
    private HashSet<String> mSelectedIdForGroupAdd;
    private ITrackingSearchView.Param mIntentParam;
    private TrackingResumeDao.SearchParam mSearchParam;
    public static final int SELF_TOAST_COLLECTION = 2;

    public TrackingSearchPresenter(ITrackingSearchView mResumeSearchView, ITrackingSearchView.Param intentParam) {
        this.mResumeSearchView = mResumeSearchView;
        this.mIntentParam = intentParam;
        setSearchFilterInfo(mIntentParam.searchFilterInfoPrevious);
        this.mShouldDoSearchForOutSideFilter = (mIntentParam.searchFilterInfoPrevious != null);
        mResumeSearchLogger.setPageCode(mResumeSearchView.callGetBindActivity(), "TrackingListSearh");
    }

    public boolean hasBackTagForNeverSearch() {
        return mBackTagForNeverSearch;
    }

    public CompanySearchLogger getResumeSearchLogger() {
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
            EditSearchConfigActivity.launch(RequestCodeConst.INT_REQUEST_SET_SEARCH_CONDITION, mResumeSearchView.callGetBindActivity(),
                    IEditSearchConfigView.MODE_TRACKING_LIST_HISTORY, mIntentParam.searchMode, null);
        }
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        subscriberResumeEvents(false);
    }

    public List<TrackingListBean> getResumeList() {
        return mResumeList;
    }

    public List<BubbleBean> getBubbleList() {
        return mBubbleList;
    }

    public int getSearchMode() {
        return mIntentParam.searchMode;
    }

    public String getLastKeyword() {
        return mLastKeyword;
    }

    private void setSearchFilterInfo(TrackingSearchFilterInfoBean sf) {
        if (sf != null)
            this.mSearchFilterInfo = sf;
        if (mSearchFilterInfo == null)
            mSearchFilterInfo = new TrackingSearchFilterInfoBean();
        if (mSearchFilterInfo.cityCode == LocalAreaBean.CODE_NONE) {
            mSearchFilterInfo.cityName = mResumeSearchView.callGetResources().getString(R.string.city_all);
        }
    }

    public TrackingSearchFilterInfoBean getSearchFilterInfo() {
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
        TrackingSearchFilterInfoBean searchFilterInfoBean = (TrackingSearchFilterInfoBean) data.getSerializableExtra("searchFilter");
        mLastKeyword = (searchFilterInfoBean != null ? searchFilterInfoBean.keyWord : data.getStringExtra("keyword"));
        mResumeSearchView.callUpdateKeywordView(mLastKeyword);
        if (searchFilterInfoBean != null) {
            mResumeSearchLogger.sendHistoryClickLog(mResumeSearchView.callGetBindActivity(), searchFilterInfoBean.toString());
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

            UIEventsObservable.getInstance().subscribeEvent(IResumeEventsSubscriber.class, this, new IResumeEventsSubscriber() {
                @Override
                public boolean onResumeModifiedObserver(ResumeBean resumeBean, Object extra) {
                    if (resumeBean == null)
                        return false;
                    //通知简历被修改了
                    int targetPosition = TrackingResumeDao.findTargetResumePosition(resumeBean, mResumeList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mResumeList.get(targetPosition).refreshModifyInfo(resumeBean);
                    //只更新改变的Item
                    mResumeSearchView.callRefreshListItems(targetPosition, 0);
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
            TrackingResumeDao.saveCompanySearchFilterToLocalDb(mSearchFilterInfo, UserInfoManager.getInstance().getUserInfo().unionId);
        }
    }

    private void realSearch(final int requestId, final int offset) {
        if (offset == 0)
            mResumeSearchView.callShowLoadingView(null);
        if (mSearchParam == null)
            mSearchParam = new TrackingResumeDao.SearchParam();
        mSearchParam
                .offset(offset)
                .pageSize(CommonConst.DEFAULT_PAGE_SIZE)
                .searchId(mResumeSearchLogger.interceptRefreshSearchIdOuter(offset == 0))
                .searchFilter(mSearchFilterInfo)
                .build();
        TrackingResumeDao.doTrackingSearch(mSearchParam, new IDefaultRequestReplyListener<TrackingSearchResultBean>() {

            @Override
            public void onRequestReply(boolean success, final TrackingSearchResultBean resumeSearchBean, int errorCode, String errorMsg) {
                if (requestId != mCurrentRequestId)
                    return;
                mResumeSearchView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success && resumeSearchBean != null) {
                    if (offset == 0) {
                        mResumeList.clear();
                        mBubbleList.clear();
                    }
                    if (resumeSearchBean.data != null && resumeSearchBean.data.getData() != null && resumeSearchBean.data.getData().size() > 0) {
                        if (resumeSearchBean.data.getData().get(0).getTrackingListData() != null) {
                            mResumeList.addAll(resumeSearchBean.data.getData().get(0).getTrackingListData());
                        }

                        if (resumeSearchBean.data.getData().get(0).getBubbleData() != null) {
                            mBubbleList.addAll(resumeSearchBean.data.getData().get(0).getBubbleData());
                        }
                    }
                    mResumeSearchView.callRefreshListItems(CommonConst.LIST_POSITION_NONE, (offset == 0 ? 0 : CommonConst.LIST_POSITION_NONE));
                    mResumeSearchView.callShowBubbleData();
                    mTotalCount = resumeSearchBean.data.getCount();
                    mResumeSearchView.callUpdateSearchResultHint(mTotalCount, mJobHunterOrNameListCount, true, mIntentParam.searchMode != CommonConst.INT_SEARCH_TYPE_MINE_JOB_HUNTER_PREDICTION);
                    mResumeSearchView.callUpdateLoadAllDataView(resumeSearchBean.data.getData().get(0).getTrackingListData() == null || resumeSearchBean.data.getData().get(0).getTrackingListData().size() < CommonConst.DEFAULT_PAGE_SIZE);
                    mResumeSearchLogger.sendDoSearchLog(mResumeSearchView.callGetBindActivity(), mSearchParam.getKeyMap(), mTotalCount);
                }
            }
        });
    }

    public void collection(int position, ResumeBean mResumeDetailBean) {
        mResumeSearchView.callShowProgress(null, true);
        CollectionDao.doOrCancelCollection(mResumeDetailBean, new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(boolean success, String s, int errorCode, String errorMsg) {
                mResumeSearchView.callCancelProgress();
                if (success) {
                    mResumeDetailBean.collectStatus = (mResumeDetailBean.collectStatus == CommonConst.COLLECTION_NO ? CommonConst.COLLECTION_YES : CommonConst.COLLECTION_NO);
                    mResumeSearchView.callShowSpecialToastMsg(SELF_TOAST_COLLECTION, null, (mResumeDetailBean.collectStatus == CommonConst.COLLECTION_NO ? R.string.un_collection_success : R.string.collection_success));
                    mResumeSearchView.callRefreshListItems(position, 0);
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

    public void filterType(String type) {
        mSearchFilterInfo.filterType = type;
        doSearch(true);
    }
}

