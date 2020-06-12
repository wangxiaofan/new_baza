package com.baza.android.bzw.businesscontroller.find.updateengine.presenter;

import android.content.Intent;
import android.content.res.Resources;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.common.LocalAreaBean;
import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeDetailBean;
import com.baza.android.bzw.bean.resume.ResumeEnableUpdateBaseInfoBean;
import com.baza.android.bzw.bean.resume.ResumeSearchBean;
import com.baza.android.bzw.bean.searchfilterbean.SearchFilterInfoBean;
import com.baza.android.bzw.bean.updateengine.OneKeyUpdateResultBean;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IEnableUpdateResumeSearchView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.dao.LabelDao;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.dao.ResumeUpdateDao;
import com.baza.android.bzw.events.IDefaultEventsSubscriber;
import com.baza.android.bzw.events.ILabelEventSubscriber;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.logger.ResumeUpdateLogger;
import com.baza.android.bzw.manager.LabelCacheManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import social.IShareCallBack;
import social.SocialHelper;
import social.data.ShareData;

/**
 * Created by Vincent.Lei on 2018/6/8.
 * Title：
 * Note：
 */
public class EnableUpdateResumeSearchPresenter extends BasePresenter {
    private IEnableUpdateResumeSearchView mSearchView;
    private ArrayList<Label> mLabelLibrary = new ArrayList<>();
    private SearchFilterInfoBean mSearchFilterInfo;
    private List<ResumeBean> mEnableUpdateList = new ArrayList<>();
    private int mTotalEnableUpdateCount;
    private Object mFilter = new Object();
    private ResumeUpdateLogger mUpdateLogger = new ResumeUpdateLogger();
    private int mCurrentSearchId;
    private ResumeEnableUpdateBaseInfoBean mResumeEnableUpdateBaseInfo;
    private boolean mBackTagForNeverSearch = true;
    private boolean mShouldRefresh;
    private boolean mIsJobHunting;
    private boolean mShareToMuch;

    public EnableUpdateResumeSearchPresenter(IEnableUpdateResumeSearchView searchView) {
        this.mSearchView = searchView;
        setSearchFilterInfo(null);
    }

    @Override
    public void initialize() {
        subscribeEvent(true);
    }

    @Override
    public void onDestroy() {
        subscribeEvent(false);
    }

    @Override
    public void onResume() {
        if (mShouldRefresh) {
            mShouldRefresh = false;
            loadInitData();
        }
    }

    public int getTotalEnableUpdateCount() {
        return mTotalEnableUpdateCount;
    }

    public boolean hasBackTagForNeverSearch() {
        return mBackTagForNeverSearch;
    }

    public List<ResumeBean> getEnableUpdateList() {
        return mEnableUpdateList;
    }

    public SearchFilterInfoBean getSearchFilterInfo() {
        return mSearchFilterInfo;
    }

    public ArrayList<Label> getLabelLibrary() {
        return mLabelLibrary;
    }

    private void setSearchFilterInfo(SearchFilterInfoBean sf) {
        if (sf != null)
            this.mSearchFilterInfo = sf;
        if (mSearchFilterInfo == null)
            mSearchFilterInfo = new SearchFilterInfoBean();
        if (mSearchFilterInfo.cityCode == LocalAreaBean.CODE_NONE) {
            mSearchFilterInfo.cityName = mSearchView.callGetResources().getString(R.string.city_all);
        }
    }

    private void subscribeEvent(boolean register) {
        if (register) {
            UIEventsObservable.getInstance().subscribeEvent(IResumeEventsSubscriber.class, this, new IResumeEventsSubscriber() {
                @Override
                public boolean isFilterByTag(Object sendTag) {
                    return (mFilter == sendTag);
                }

                @Override
                public boolean onResumeUpdateByEngine(ResumeBean data, Object extra) {
                    if (data == null)
                        return false;
                    mShouldRefresh = true;
                    return false;
                }
            });
            UIEventsObservable.getInstance().subscribeEvent(IDefaultEventsSubscriber.class, this, new IDefaultEventsSubscriber() {
                @Override
                public boolean isFilterByTag(Object sendTag) {
                    return mFilter == sendTag;
                }

                @Override
                public boolean killEvent(String action, Object data) {
                    if (ActionConst.ACTION_EVENT_UPDATE_AMOUNT_CHANGED.equals(action) || ActionConst.ACTION_EVENT_UPDATE_BY_ONEKEY.equals(action))
                        mShouldRefresh = true;
                    return false;
                }
            });
        } else {
            UIEventsObservable.getInstance().stopSubscribeEvent(IResumeEventsSubscriber.class, this);
            UIEventsObservable.getInstance().stopSubscribeEvent(IDefaultEventsSubscriber.class, this);
        }
    }

    public void parseSearchCondition(Intent data) {
        mBackTagForNeverSearch = false;
        SearchFilterInfoBean searchFilterInfoBean = (SearchFilterInfoBean) data.getSerializableExtra("searchFilter");
        String mLastKeyword = (searchFilterInfoBean != null ? searchFilterInfoBean.keyWord : data.getStringExtra("keyword"));
        mSearchView.callUpdateKeywordView(mLastKeyword);
        if (searchFilterInfoBean != null) {
            setSearchFilterInfo(searchFilterInfoBean);
            mSearchView.callUpdateSearchFilterView();
        }
        mSearchFilterInfo.attachKeyWord(mLastKeyword);
        loadInitData();
    }

    public void loadInitData() {
        if (mResumeEnableUpdateBaseInfo == null) {
            loadResumeEnableUpdateBaseInfo(true);
            return;
        }
        doSearch(true);
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
                            mSearchView.callSetLabelLibrary();
                            LabelCacheManager.getInstance().setLabels(mLabelLibrary);
                            UIEventsObservable.getInstance().postEvent(ILabelEventSubscriber.class, ActionConst.ACTION_EVENT_LABEL_LIBRARY_GET, null, null);
                        }
                        return;
                    }
                    mSearchView.callShowToastMessage(errorMsg, 0);
                }
            });
        }
    }

    public void onJobHunterFilterSelected(boolean isJobHunting) {
        mIsJobHunting = isJobHunting;
        doSearch(true);
    }

    public void doSearch(final boolean refresh) {
        mCurrentSearchId++;
        if (refresh)
            mSearchView.callShowLoadingView(null);
        final int searchId = mCurrentSearchId;
        final HashMap<String, String> searchParams = ResumeUpdateDao.buildSearchParam((refresh ? 0 : mEnableUpdateList.size()), mIsJobHunting, null, mSearchFilterInfo, null, null);
        ResumeUpdateDao.loadResumeEnableUpdateList(searchParams, new IDefaultRequestReplyListener<ResumeSearchBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeSearchBean data, int errorCode, String errorMsg) {
                if (searchId != mCurrentSearchId)
                    return;
                mSearchView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success && data != null) {
                    if (refresh)
                        mEnableUpdateList.clear();
                    mTotalEnableUpdateCount = data.totalCount;
                    if (data.recordList != null && !data.recordList.isEmpty())
                        mEnableUpdateList.addAll(data.recordList);
                    mSearchView.callRefreshListItems(CommonConst.LIST_POSITION_NONE, CommonConst.LIST_POSITION_NONE);
                    mSearchView.callUpdateSearchResultHint(mTotalEnableUpdateCount, 0, true, false);
                    mSearchView.callUpdateOneKeyUpdateView(mResumeEnableUpdateBaseInfo.limit, mEnableUpdateList.size(), mTotalEnableUpdateCount);
                    mSearchView.callUpdateLoadAllDataView(mEnableUpdateList.size() >= mTotalEnableUpdateCount);
                }
                if (success && searchParams != null)
                    mUpdateLogger.sendDoSearchLog(mSearchView.callGetBindActivity(), searchParams);
            }
        });
        if (mSearchFilterInfo.isCurrentSearchCanBeSave()) {
            //保存搜索条件
            ResumeDao.saveSearchFilterToLocalDb(mSearchFilterInfo, UserInfoManager.getInstance().getUserInfo().unionId);
        }

    }

    public void updateSingleResume(final ResumeBean resumeBean) {
        mSearchView.callShowProgress(null, true);
        ResumeUpdateDao.updateDefaultSingleResume(resumeBean.candidateId, new IDefaultRequestReplyListener<ResumeDetailBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeDetailBean resumeDetailBean, int errorCode, String errorMsg) {
                mSearchView.callCancelProgress();
                if (success) {
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mEnableUpdateList);
                    if (targetPosition != CommonConst.LIST_POSITION_NONE) {
                        mTotalEnableUpdateCount--;
                        mResumeEnableUpdateBaseInfo.limit--;
                        mEnableUpdateList.remove(targetPosition);
                        mSearchView.callUpdateSearchResultHint(mTotalEnableUpdateCount, 0, true, false);
                        mSearchView.callRefreshListItems(CommonConst.LIST_POSITION_NONE, CommonConst.LIST_POSITION_NONE);
                        mSearchView.callUpdateOneKeyUpdateView(mResumeEnableUpdateBaseInfo.limit, mEnableUpdateList.size(), mTotalEnableUpdateCount);
                        UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_RESUME_UPDATE_BY_ENGINE, resumeDetailBean, mFilter);
                        UIEventsObservable.getInstance().postEvent(IDefaultEventsSubscriber.class, ActionConst.ACTION_EVENT_UPDATE_AMOUNT_CHANGED, -1, mFilter);
                    }
                    mUpdateLogger.sendSingleUpdateLog(mSearchView.callGetBindActivity(), resumeBean);
                    return;
                }
                mSearchView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    private void loadResumeEnableUpdateBaseInfo(final boolean initResume) {
        mSearchView.callShowLoadingView(null);
        ResumeUpdateDao.loadResumeEnableUpdateBaseInfo(new IDefaultRequestReplyListener<ResumeEnableUpdateBaseInfoBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeEnableUpdateBaseInfoBean candidateEnableUpdateInfoBean, int errorCode, String errorMsg) {
                if (!initResume || !success)
                    mSearchView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success) {
                    mResumeEnableUpdateBaseInfo = candidateEnableUpdateInfoBean;
                    if (initResume)
                        doSearch(true);
                }
            }
        });
    }

    public void updateResumeList() {
        mSearchView.callShowProgress(null, true);
        ResumeUpdateDao.oneKeyUpdate(mSearchFilterInfo, null, null, new IDefaultRequestReplyListener<OneKeyUpdateResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, OneKeyUpdateResultBean.Data data, int errorCode, String errorMsg) {
                mSearchView.callCancelProgress();
                UIEventsObservable.getInstance().postEvent(ActionConst.ACTION_EVENT_UPDATE_BY_ONEKEY, null, mFilter);
                if (success) {
                    mResumeEnableUpdateBaseInfo = null;
                    loadInitData();
                    mSearchView.callShowToastMessage(null, R.string.on_one_day_update);
                    return;
                }
                mSearchView.callShowToastMessage(errorMsg, 0);
            }
        });
        if (mResumeEnableUpdateBaseInfo != null)
            mUpdateLogger.sendOneKeyUpdateLog(mSearchView.callGetBindActivity(), mEnableUpdateList, (mResumeEnableUpdateBaseInfo.limit > mTotalEnableUpdateCount ? mTotalEnableUpdateCount : mResumeEnableUpdateBaseInfo.limit), CommonConst.DEFAULT_PAGE_SIZE);

    }

    public boolean isOneKeyUpdateEnable() {
        return (mResumeEnableUpdateBaseInfo != null && mResumeEnableUpdateBaseInfo.limit > 0);
    }

    public void shareUpdateEngineAdv() {
        if (mShareToMuch) {
            mSearchView.callUpdateShareToMuchView();
            return;
        }
        Resources resources = mSearchView.callGetResources();
        String des = resources.getString(R.string.update_engine_adv_share_title);
        ShareData shareData = new ShareData.Builder().appName(resources.getString(R.string.app_name)).title(des).summary(des).shareLink(URLConst.LINK_SHARE_UPDATE_ENGINE).buildWeChatLinkData();
        SocialHelper.getInstance().shareToWeChatFriendCircle(mSearchView.callGetBindActivity(), shareData, new IShareCallBack() {
            @Override
            public void onShareReply(boolean success, int errorCode, String errorMsg) {
                int resultCode = 0;
                switch (errorCode) {
                    case IShareCallBack.ERROR_CODE_WECHAT_NOT_INSTALL:
                        resultCode = R.string.wechat_not_install;
                        break;
                    case IShareCallBack.ERROR_CODE_WECHAT_LEVEL_LOW:
                        resultCode = R.string.wechat_version_low;
                        break;
                }
                if (resultCode != 0)
                    mSearchView.callShowToastMessage(null, resultCode);
                if (success)
                    getDefaultUpdateAmount();
            }
        });
    }

    private void getDefaultUpdateAmount() {
        mSearchView.callShowProgress(null, true);
        ResumeUpdateDao.loadDefaultUpdateAmount(new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mSearchView.callCancelProgress();
                if (success) {
                    mResumeEnableUpdateBaseInfo.limit += 3;
                    mSearchView.callUpdateOneKeyUpdateView(mResumeEnableUpdateBaseInfo.limit, mEnableUpdateList.size(), mTotalEnableUpdateCount);
                    mSearchView.callShowToastMessage(mSearchView.callGetResources().getString(R.string.update_amount_get, "3"), 0);
                    return;
                }

                if (errorCode == CustomerRequestAssistHandler.NET_ERROR_SHARE_TO_MUCH) {
                    mShareToMuch = true;
                    mSearchView.callUpdateShareToMuchView();
                }
            }
        });
    }
}
