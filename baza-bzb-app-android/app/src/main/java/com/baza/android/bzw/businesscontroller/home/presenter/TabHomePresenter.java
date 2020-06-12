package com.baza.android.bzw.businesscontroller.home.presenter;

import android.os.Handler;
import android.os.Looper;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.common.BannerResultBean;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeSearchBean;
import com.baza.android.bzw.businesscontroller.home.viewinterface.ITabHomeView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.AdvertisementDao;
import com.baza.android.bzw.dao.CollectionDao;
import com.baza.android.bzw.dao.FriendDao;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.dao.ResumeUpdateDao;
import com.baza.android.bzw.events.IDefaultEventsSubscriber;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Vincent.Lei on 2017/9/27.
 * Title：
 * Note：
 */

public class TabHomePresenter extends BasePresenter {
    private static final int CACHE_RECOMMEND_FRIEND_COUNT = 300;
    private static final int REFRESH_SUGGEST_FRIEND_DELAY = 30000;
    private static final int TAG_ON_REQUIRE_BANNER = 1 << 2;
    private ITabHomeView mHomeView;
    private Object mFilterTag = new Object();
    private List<FriendListResultBean.FriendBean> mSuggestFriendList = new ArrayList<>();
    private List<ResumeBean> mEnableUpdateList = new ArrayList<>();
    private List<BannerResultBean.Data> mBannerList = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mRunnableRefreshTask;
    private Set<String> mHaveRecommendedIds = new HashSet<>();
    private String[] mMoreEditMenu;
    private boolean mHidden;
    private boolean mNeedRefreshUpdateList;
    private int mRequestType;

    public TabHomePresenter(ITabHomeView mHomeView) {
        this.mHomeView = mHomeView;
    }

    @Override
    public void initialize() {
        subscribeEvents(true);
    }

    @Override
    public void onPause() {
        cancelPostToLoadSuggestFriend();
    }

    @Override
    public void onResume() {
        onHiddenChanged(mHidden);
    }

    public void onHiddenChanged(boolean hidden) {
        mHidden = hidden;
        if (!mHidden) {
            postToLoadSuggestFriend(0);
            if (mEnableUpdateList.isEmpty() || mNeedRefreshUpdateList) {
                mNeedRefreshUpdateList = false;
                loadResumeEnableUpdateList(true);
            }
            if (mBannerList.isEmpty())
                loadBanner();
            return;
        }
        cancelPostToLoadSuggestFriend();
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        subscribeEvents(false);
    }

    public String[] getMoreEditMenu() {
        if (mMoreEditMenu == null) {
            mMoreEditMenu = new String[2];
            mMoreEditMenu[0] = mHomeView.callGetResources().getString(R.string.scan_qrcode);
            mMoreEditMenu[1] = mHomeView.callGetResources().getString(R.string.candidate_import);
        }
        return mMoreEditMenu;
    }

    public List<FriendListResultBean.FriendBean> getSuggestFriends() {
        return mSuggestFriendList;
    }

    public List<BannerResultBean.Data> getBanners() {
        return mBannerList;
    }

    public List<ResumeBean> getEnableUpdateList() {
        return mEnableUpdateList;
    }

    public Handler getHandler() {
        return mHandler;
    }

    private void postToLoadSuggestFriend(long delay) {
        delay = (mRunnableRefreshTask == null ? delay : REFRESH_SUGGEST_FRIEND_DELAY);
        if (mRunnableRefreshTask == null)
            mRunnableRefreshTask = new Runnable() {
                @Override
                public void run() {
                    loadSuggestFriend();
                }
            };
        mHandler.postDelayed(mRunnableRefreshTask, delay);
    }

    private void cancelPostToLoadSuggestFriend() {
        if (mHandler != null && mRunnableRefreshTask != null)
            mHandler.removeCallbacks(mRunnableRefreshTask);
    }

    private void loadSuggestFriend() {
        FriendDao.loadSuggestFriend(mHaveRecommendedIds, new IDefaultRequestReplyListener<List<FriendListResultBean.FriendBean>>() {
            @Override
            public void onRequestReply(boolean success, List<FriendListResultBean.FriendBean> friendBeanList, int errorCode, String errorMsg) {
                if (success && friendBeanList != null) {
                    mSuggestFriendList.clear();
                    mSuggestFriendList.addAll(friendBeanList);
                    mHomeView.callUpdateSuggestFriendView();
                    if (!mSuggestFriendList.isEmpty()) {
                        if (mHaveRecommendedIds.size() > CACHE_RECOMMEND_FRIEND_COUNT)
                            mHaveRecommendedIds.clear();
                        for (int i = 0, size = mSuggestFriendList.size(); i < size; i++) {
                            mHaveRecommendedIds.add(mSuggestFriendList.get(i).unionId);
                        }
                    }
                }
                postToLoadSuggestFriend(REFRESH_SUGGEST_FRIEND_DELAY);
            }
        });
    }

    private void loadBanner() {
        if ((mRequestType & TAG_ON_REQUIRE_BANNER) > 0)
            return;
        mRequestType |= TAG_ON_REQUIRE_BANNER;
        AdvertisementDao.loadBanner(new IDefaultRequestReplyListener<List<BannerResultBean.Data>>() {
            @Override
            public void onRequestReply(boolean success, List<BannerResultBean.Data> data, int errorCode, String errorMsg) {
                mRequestType &= ~TAG_ON_REQUIRE_BANNER;
                if (data != null && !data.isEmpty()) {
                    mBannerList.clear();
                    mBannerList.addAll(data);
                    mHomeView.callUpdateBannerView();
                }
            }
        });
    }

    public void loadResumeEnableUpdateList(final boolean refresh) {
        ResumeUpdateDao.loadResumeEnableUpdateList(ResumeUpdateDao.buildSearchParam((refresh ? 0 : mEnableUpdateList.size()), false, null, null, null, null), new IDefaultRequestReplyListener<ResumeSearchBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeSearchBean data, int errorCode, String errorMsg) {
                if (success) {
                    if (refresh)
                        mEnableUpdateList.clear();
                    boolean loadAll = false;
                    if (data != null) {
                        if (data.recordList != null && !data.recordList.isEmpty())
                            mEnableUpdateList.addAll(data.recordList);
                        loadAll = (data.recordList == null || data.recordList.size() < CommonConst.DEFAULT_PAGE_SIZE);
                    }
                    mHomeView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    mHomeView.callUpdateLoadMoreEnable(!loadAll);
                    mHomeView.callSetNoResultHintView(mEnableUpdateList.isEmpty());
                    return;
                }
                mHomeView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void collection(int position) {
        if (position < 0 || position >= mEnableUpdateList.size())
            return;
        mHomeView.callShowProgress(null, true);
        final ResumeBean resumeBean = mEnableUpdateList.get(position);
        CollectionDao.doOrCancelCollection(resumeBean, new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(boolean success, String s, int errorCode, String errorMsg) {
                mHomeView.callCancelProgress();
                if (success) {
                    resumeBean.collectStatus = (resumeBean.collectStatus == CommonConst.COLLECTION_NO ? CommonConst.COLLECTION_YES : CommonConst.COLLECTION_NO);
                    mHomeView.callShowToastMessage(null, (resumeBean.collectStatus == CommonConst.COLLECTION_NO ? R.string.un_collection_success : R.string.collection_success));
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mEnableUpdateList);
                    if (targetPosition != CommonConst.LIST_POSITION_NONE)
                        mHomeView.callRefreshListItems(targetPosition);
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_COLLECTION_RESUME_CHANGED, resumeBean, mFilterTag);
                    return;
                }
                mHomeView.callShowToastMessage(errorMsg, 0);
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
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mEnableUpdateList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mEnableUpdateList.get(targetPosition).collectStatus = resumeBean.collectStatus;
                    //只更新改变的Item
                    mHomeView.callRefreshListItems(targetPosition);
                    return false;
                }

                @Override
                public boolean onResumeModifiedObserver(ResumeBean resumeBean, Object extra) {
                    if (resumeBean == null)
                        return false;
                    //通知简历被修改了
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mEnableUpdateList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mEnableUpdateList.get(targetPosition).refreshModifyInfo(resumeBean);
                    //只更新改变的Item
                    mHomeView.callRefreshListItems(targetPosition);
                    return false;
                }

                @Override
                public boolean onResumeDeletedObserver(ResumeBean data, Object extra) {
                    //简历被删除
                    int targetPosition = ResumeDao.findTargetResumePosition(data, mEnableUpdateList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mEnableUpdateList.remove(targetPosition);
                    //CommonConst.LIST_POSITION_NONE刷新整个列表
                    mHomeView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    return false;
                }

                @Override
                public boolean onResumeUpdateByEngine(ResumeBean data, Object extra) {
                    if (data == null)
                        return false;
                    //通知简历被修改了
                    int targetPosition = ResumeDao.findTargetResumePosition(data, mEnableUpdateList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mEnableUpdateList.remove(targetPosition);
                    //只更新改变的Item
                    mHomeView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    return false;
                }
            });

            UIEventsObservable.getInstance().subscribeEvent(IDefaultEventsSubscriber.class, this, new IDefaultEventsSubscriber() {
                @Override
                public boolean killEvent(String action, Object data) {
                    if (ActionConst.ACTION_EVENT_UPDATE_BY_ONEKEY.equals(action)) {
                        if (!mHidden) {
                            loadResumeEnableUpdateList(true);
                        } else
                            mNeedRefreshUpdateList = true;
                    }
                    return false;
                }
            });
        } else {
            UIEventsObservable.getInstance().stopSubscribeEvent(IResumeEventsSubscriber.class, this);
            UIEventsObservable.getInstance().stopSubscribeEvent(IDefaultEventsSubscriber.class, this);
        }
    }
}
