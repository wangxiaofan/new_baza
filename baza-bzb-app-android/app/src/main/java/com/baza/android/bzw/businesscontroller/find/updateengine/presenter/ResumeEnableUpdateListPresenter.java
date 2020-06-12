package com.baza.android.bzw.businesscontroller.find.updateengine.presenter;

import android.content.Intent;
import android.content.res.Resources;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeDetailBean;
import com.baza.android.bzw.bean.resume.ResumeEnableUpdateBaseInfoBean;
import com.baza.android.bzw.bean.resume.ResumeSearchBean;
import com.baza.android.bzw.bean.updateengine.OneKeyUpdateResultBean;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IResumeEnableUpdateListView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.SharedPreferenceConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.dao.ResumeUpdateDao;
import com.baza.android.bzw.events.IDefaultEventsSubscriber;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.logger.ResumeUpdateLogger;
import com.bznet.android.rcbox.R;
import com.slib.storage.sharedpreference.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.List;

import social.IShareCallBack;
import social.SocialHelper;
import social.data.ShareData;

/**
 * Created by Vincent.Lei on 2017/8/22.
 * Title：
 * Note：
 */

public class ResumeEnableUpdateListPresenter extends BasePresenter {
    private IResumeEnableUpdateListView mResumeEnableUpdateListView;
    private List<ResumeBean> mEnableUpdateList = new ArrayList<>();
    private Object mFilter = new Object();
    private ResumeEnableUpdateBaseInfoBean mResumeEnableUpdateBaseInfo;
    private ResumeUpdateLogger mUpdateLogger = new ResumeUpdateLogger();
    private boolean mShouldRefresh = true;
    private int mTotalEnableUpdateCount;
    private boolean mIsJobHunting;
    private boolean mShareToMuch;

    public ResumeEnableUpdateListPresenter(IResumeEnableUpdateListView resumeEnableUpdateListView, Intent intent) {
        this.mResumeEnableUpdateListView = resumeEnableUpdateListView;
    }

    @Override
    public void initialize() {
        subscribeEvent(true);
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

    @Override
    public void onDestroy() {
        subscribeEvent(false);
    }

    public List<ResumeBean> getEnableUpdateList() {
        return mEnableUpdateList;
    }

    public ResumeEnableUpdateBaseInfoBean getAmountInfo() {
        return mResumeEnableUpdateBaseInfo;
    }

    public void loadInitData() {
        loadResumeEnableUpdateBaseInfo(true);
    }


    private void loadResumeEnableUpdateBaseInfo(final boolean initResume) {
        mResumeEnableUpdateListView.callShowLoadingView();
        ResumeUpdateDao.loadResumeEnableUpdateBaseInfo(new IDefaultRequestReplyListener<ResumeEnableUpdateBaseInfoBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeEnableUpdateBaseInfoBean candidateEnableUpdateInfoBean, int errorCode, String errorMsg) {
                if (!initResume || !success)
                    mResumeEnableUpdateListView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success) {
                    mResumeEnableUpdateBaseInfo = candidateEnableUpdateInfoBean;
                    mResumeEnableUpdateListView.callUpdateAmountInfo();
                    if (initResume)
                        loadResumeEnableUpdateList(true, true, false);
                }
            }
        });
    }

    public void loadResumeEnableUpdateList(final boolean refresh, final boolean showInnerLoadView, final boolean showProgress) {
        if (showInnerLoadView)
            mResumeEnableUpdateListView.callShowLoadingView();
        if (showProgress)
            mResumeEnableUpdateListView.callShowProgress(null, true);
        ResumeUpdateDao.loadResumeEnableUpdateList(ResumeUpdateDao.buildSearchParam((refresh ? 0 : mEnableUpdateList.size()), mIsJobHunting, null, null, null, null), new IDefaultRequestReplyListener<ResumeSearchBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeSearchBean data, int errorCode, String errorMsg) {
                if (mResumeEnableUpdateBaseInfo != null)
                    mResumeEnableUpdateListView.callCancelLoadingView(success, errorCode, errorMsg);
                if (showProgress)
                    mResumeEnableUpdateListView.callCancelProgress();
                if (success) {
                    if (refresh)
                        mEnableUpdateList.clear();
                    if (data != null) {
                        mTotalEnableUpdateCount = data.totalCount;
                        if (data.recordList != null && !data.recordList.isEmpty())
                            mEnableUpdateList.addAll(data.recordList);
                    }
                    if (mResumeEnableUpdateBaseInfo != null)
                        mResumeEnableUpdateListView.callUpdateOneKeyUpdateView(mResumeEnableUpdateBaseInfo.limit, mEnableUpdateList.size(), mTotalEnableUpdateCount);
                    mResumeEnableUpdateListView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    mResumeEnableUpdateListView.callUpdateSearchCount(mTotalEnableUpdateCount);
                    mResumeEnableUpdateListView.callUpdateNoDataView((!mEnableUpdateList.isEmpty()));
                    mResumeEnableUpdateListView.callUpdateLoadMoreEnable((data != null && mEnableUpdateList.size() < mTotalEnableUpdateCount));
                    return;
                }
                mResumeEnableUpdateListView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void updateResumeList() {
        mResumeEnableUpdateListView.callShowProgress(null, true);
        ResumeUpdateDao.oneKeyUpdate(null, null, null, new IDefaultRequestReplyListener<OneKeyUpdateResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, OneKeyUpdateResultBean.Data data, int errorCode, String errorMsg) {
                mResumeEnableUpdateListView.callCancelProgress();
                UIEventsObservable.getInstance().postEvent(ActionConst.ACTION_EVENT_UPDATE_BY_ONEKEY, null);
                if (success) {
                    loadInitData();
                    mResumeEnableUpdateListView.callShowToastMessage(null, R.string.on_one_day_update);
                    return;
                }
                mResumeEnableUpdateListView.callShowToastMessage(errorMsg, 0);
            }
        });
        if (mResumeEnableUpdateBaseInfo != null)
            mUpdateLogger.sendOneKeyUpdateLog(mResumeEnableUpdateListView.callGetBindActivity(), mEnableUpdateList, (mResumeEnableUpdateBaseInfo.limit > mTotalEnableUpdateCount ? mTotalEnableUpdateCount : mResumeEnableUpdateBaseInfo.limit), CommonConst.DEFAULT_PAGE_SIZE);
    }

    public void updateSingleResume(final ResumeBean resumeBean) {
        if (mResumeEnableUpdateBaseInfo.limit <= 0) {
            mResumeEnableUpdateListView.callUpdateAmountNotEnoughView();
            return;
        }
        mResumeEnableUpdateListView.callShowProgress(null, true);
        ResumeUpdateDao.updateDefaultSingleResume(resumeBean.candidateId, new IDefaultRequestReplyListener<ResumeDetailBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeDetailBean resumeDetailBean, int errorCode, String errorMsg) {
                mResumeEnableUpdateListView.callCancelProgress();
                if (success) {
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mEnableUpdateList);
                    if (targetPosition != CommonConst.LIST_POSITION_NONE) {
                        mEnableUpdateList.remove(targetPosition);
                        mTotalEnableUpdateCount--;
                        mResumeEnableUpdateBaseInfo.limit--;
                        mResumeEnableUpdateListView.callUpdateAmountInfo();
                        mResumeEnableUpdateListView.callUpdateSearchCount(mTotalEnableUpdateCount);
                        mResumeEnableUpdateListView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                        mResumeEnableUpdateListView.callUpdateOneKeyUpdateView(mResumeEnableUpdateBaseInfo.limit, mEnableUpdateList.size(), mTotalEnableUpdateCount);
                        UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_RESUME_UPDATE_BY_ENGINE, resumeDetailBean, mFilter);
                        UIEventsObservable.getInstance().postEvent(IDefaultEventsSubscriber.class, ActionConst.ACTION_EVENT_UPDATE_AMOUNT_CHANGED, CommonConst.LIST_POSITION_NONE, mFilter);
                    }
                    mUpdateLogger.sendSingleUpdateLog(mResumeEnableUpdateListView.callGetBindActivity(), resumeBean);
                    return;
                }
                mResumeEnableUpdateListView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public boolean isEnableGoBack() {
        if (!SharedPreferenceManager.getBoolean(SharedPreferenceConst.SP_UPDATE_BY_ENGINE_FEED_BACK_CLOSED_TAG)) {
            mResumeEnableUpdateListView.callShowFeedBackDialog();
            return false;
        }
        return true;
    }

    public void setFeedBackDialogNotShowAnymore() {
        SharedPreferenceManager.saveBoolean(SharedPreferenceConst.SP_UPDATE_BY_ENGINE_FEED_BACK_CLOSED_TAG, true);
    }

    public void feedBack(int commentType, String msg) {
        mResumeEnableUpdateListView.callShowProgress(null, true);
        ResumeUpdateDao.updateFeedBack(commentType, msg, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mResumeEnableUpdateListView.callCancelProgress();
                if (success) {
                    mResumeEnableUpdateListView.callShowToastMessage(null, R.string.submit_success);
                    setFeedBackDialogNotShowAnymore();
                    mResumeEnableUpdateListView.callGetBindActivity().finish();
                    return;
                }
                mResumeEnableUpdateListView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public boolean isOneKeyUpdateEnable() {
        return (mResumeEnableUpdateBaseInfo != null && mResumeEnableUpdateBaseInfo.limit > 0);
    }

    public void shareUpdateEngineAdv() {
        if (mShareToMuch) {
            mResumeEnableUpdateListView.callUpdateShareToMuchView();
            return;
        }
        Resources resources = mResumeEnableUpdateListView.callGetResources();
        String des = resources.getString(R.string.update_engine_adv_share_title);
        ShareData shareData = new ShareData.Builder().appName(resources.getString(R.string.app_name)).title(des).summary(des).shareLink(URLConst.LINK_SHARE_UPDATE_ENGINE).buildWeChatLinkData();
        SocialHelper.getInstance().shareToWeChatFriendCircle(mResumeEnableUpdateListView.callGetBindActivity(), shareData, new IShareCallBack() {
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
                    mResumeEnableUpdateListView.callShowToastMessage(null, resultCode);
                if (success)
                    getDefaultUpdateAmount();
            }
        });
    }

    private void getDefaultUpdateAmount() {
        mResumeEnableUpdateListView.callShowProgress(null, true);
        ResumeUpdateDao.loadDefaultUpdateAmount(new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mResumeEnableUpdateListView.callCancelProgress();
                if (success) {
                    mResumeEnableUpdateBaseInfo.limit += 3;
                    mResumeEnableUpdateListView.callUpdateAmountInfo();
                    mResumeEnableUpdateListView.callUpdateOneKeyUpdateView(mResumeEnableUpdateBaseInfo.limit, mEnableUpdateList.size(), mTotalEnableUpdateCount);
                    mResumeEnableUpdateListView.callShowToastMessage(mResumeEnableUpdateListView.callGetResources().getString(R.string.update_amount_get, "3"), 0);
                    return;
                }
                if (errorCode == CustomerRequestAssistHandler.NET_ERROR_SHARE_TO_MUCH) {
                    mShareToMuch = true;
                    mResumeEnableUpdateListView.callUpdateShareToMuchView();
                }
            }
        });
    }

    public void onJobHunterFilterSelected(boolean isJobHunting) {
        mIsJobHunting = isJobHunting;
        loadResumeEnableUpdateList(true, true, false);
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
                    if (ActionConst.ACTION_EVENT_UPDATE_AMOUNT_CHANGED.equals(action))
                        mShouldRefresh = true;
                    return false;
                }
            });
        } else {
            UIEventsObservable.getInstance().stopSubscribeEvent(IResumeEventsSubscriber.class, this);
            UIEventsObservable.getInstance().stopSubscribeEvent(IDefaultEventsSubscriber.class, this);
        }
    }
}
