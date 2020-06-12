package com.baza.android.bzw.businesscontroller.home.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.operational.ActivityStatusResultBean;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeSearchBean;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.bean.user.VersionBean;
import com.baza.android.bzw.businesscontroller.home.viewinterface.IHomeView;
import com.baza.android.bzw.businesscontroller.im.IFriendMsgObserve;
import com.baza.android.bzw.businesscontroller.im.IImLoginListener;
import com.baza.android.bzw.businesscontroller.im.IMConst;
import com.baza.android.bzw.businesscontroller.im.IUnreadCountObserve;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.SharedPreferenceConst;
import com.baza.android.bzw.dao.ActivityPrepareDao;
import com.baza.android.bzw.dao.CheckUpdateDao;
import com.baza.android.bzw.dao.OperationalDao;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.IMManager;
import com.baza.android.bzw.manager.SystemNoticeManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.netease.nimlib.sdk.Observer;
import com.slib.storage.database.listener.IDBReplyListener;
import com.slib.storage.sharedpreference.SharedPreferenceManager;
import com.slib.utils.AppUtil;

import java.util.LinkedList;

/**
 * Created by Vincent.Lei on 2017/5/17.
 * Title：
 * Note：
 */

public class HomePresenter extends BasePresenter {
    private IHomeView mHomeView;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Observer<Void> mIsImSyncDataCompletedObserver;
    private IUnreadCountObserve mUnreadCountObserve;
    private IFriendMsgObserve mFriendMsgObserve;
    private boolean mImHasLogin, mImOnLogin;
    private BroadcastReceiver mNetWorkReceiver;
    private IMManager mImManager;
    private LinkedList<DialogTask> mDialogTaskLinkedList;
    private DialogTask mCurrentShowDialogTask;
    private String mCachedActivityResumeId;
    private int mMessageUnreadCount;
    private int mFriendRequestCount;
    private int mWaitProcessUnreadCount;
    private int mUnCompleteRecommendCount;

    private RecommendHomeHintPresenter mRecommendHomeHintPresenter;

    public HomePresenter(IHomeView mHomeView, Intent intent) {
        this.mHomeView = mHomeView;
        this.mImManager = IMManager.getInstance(mHomeView.callGetBindActivity());
        this.mRecommendHomeHintPresenter = new RecommendHomeHintPresenter(mHomeView, this);
    }

    @Override
    public void initialize() {
        registerExtraObserve(true);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                checkNewVersion();
                mRecommendHomeHintPresenter.initialize();
                checkOperationActivity();
            }
        });
    }

    @Override
    public void onResume() {
        imLogin();
        setIMNotifyEnable(!mHomeView.callIsCurrentIsMessageView());
        mRecommendHomeHintPresenter.onResume();
    }

    private void registerNetWorkReceiver(boolean register) {
        if (register) {
            if (mNetWorkReceiver == null)
                mNetWorkReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        LogUtil.d("NetWorkReceiver----------------");
                        if (AppUtil.isNetworkAvailable(mHomeView.callGetBindActivity()))
                            imLogin();
                    }
                };
            mHomeView.callGetBindActivity().registerReceiver(mNetWorkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } else if (mNetWorkReceiver != null)
            mHomeView.callGetBindActivity().unregisterReceiver(mNetWorkReceiver);
    }

    private void checkNewVersion() {
        CheckUpdateDao.getInstance().checkUpdate(new CheckUpdateDao.INewVersionListener() {
            @Override
            public void noticeCurrentVersionIsNewest() {

            }

            @Override
            public void noticeOnLoadingNewVersion() {

            }

            @Override
            public void noticeFindNewVersion(VersionBean data) {
                publishDialogShowTask(new HomePresenter.DialogTask(HomePresenter.DialogTask.TYPE_NEW_APP_VERSION, data, HomePresenter.this), false);
            }

            @Override
            public void noticeOnCheckingNewVersion() {

            }
        });
    }

    private void imLogin() {
        if (mImHasLogin || mImOnLogin)
            return;
        mImOnLogin = true;
        LogUtil.d("need to login im account");
        final UserInfoBean userInfoBean = UserInfoManager.getInstance().getUserInfo();
        mImManager.login(userInfoBean.neteaseId, userInfoBean.neteaseToken, new IImLoginListener() {
            @Override
            public void onImLoginResult(boolean success) {
                mImOnLogin = false;
                mImHasLogin = success;
                if (success) {
                    registerNetWorkReceiver(false);
                    mNetWorkReceiver = null;
                    mImManager.initialize();
                    registerIMUnreadCountObserve(true);
                    registerImSyncDataCompletedObserver(true);
                    SharedPreferenceManager.saveString(SharedPreferenceConst.SP_IM_USER_NAME, userInfoBean.neteaseId);
                    SharedPreferenceManager.saveString(SharedPreferenceConst.SP_IM_USER_TOKEN, userInfoBean.neteaseToken);
                    setIMNotifyEnable(!mHomeView.callIsCurrentIsMessageView());
                }
                LogUtil.d("onImLoginResult = " + success);
            }
        });
    }


    @Override
    public void onDestroy() {
        registerExtraObserve(false);
        registerNetWorkReceiver(false);
        registerImSyncDataCompletedObserver(false);
        registerIMUnreadCountObserve(false);
        setIMNotifyEnable(true);
        mRecommendHomeHintPresenter.onDestroy();
        mImManager.destroy();
        mImManager = null;
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
    }

    private void registerImSyncDataCompletedObserver(boolean register) {
        if (register) {
            if (mIsImSyncDataCompletedObserver == null)
                mIsImSyncDataCompletedObserver = new Observer<Void>() {
                    @Override
                    public void onEvent(Void aVoid) {
                        onImSyncDataChanged(true);
                    }
                };

            boolean isImSyncDataCompleted = mImManager.observeSyncDataCompletedEvent(mIsImSyncDataCompletedObserver);
            onImSyncDataChanged(isImSyncDataCompleted);
        } else if (mIsImSyncDataCompletedObserver != null)
            mImManager.unObserveSyncDataCompletedEvent(mIsImSyncDataCompletedObserver);
    }

    private void registerExtraObserve(boolean register) {
        if (register) {
            SystemNoticeManager.getInstance().observeSystemNotice(mHomeView.callGetApplication());
        } else {
            SystemNoticeManager.getInstance().stopObserveSystemNotice(mHomeView.callGetApplication());
        }
    }

    void onRecommendUnProcessCountChanged(int unCompletedCount) {
        mUnCompleteRecommendCount = unCompletedCount;
        updateMessageUnreadCountView();
    }

    private void registerIMUnreadCountObserve(boolean register) {
        if (register) {
            if (mUnreadCountObserve == null)
                mUnreadCountObserve = new IUnreadCountObserve() {
                    @Override
                    public void onImUnreadCountChanged(int unreadCount) {
                        mMessageUnreadCount = unreadCount;
                        updateMessageUnreadCountView();
                    }
                };

            if (mFriendMsgObserve == null)
                mFriendMsgObserve = new IFriendMsgObserve() {
                    @Override
                    public void onFriendNoticeMsgUnreadCountChanged(int unreadCount) {
                        mFriendRequestCount = unreadCount;
                        updateMessageUnreadCountView();
                    }

                    @Override
                    public void onFriendRequestGet(String account) {

                    }

                    @Override
                    public void onFriendAgreeGet(String account) {

                    }
                };
            mImManager.registerUnreadCountMessageObserve(mUnreadCountObserve);
            mImManager.registerFriendMsgObserve(mFriendMsgObserve, true);
        } else {
            if (mUnreadCountObserve != null)
                mImManager.unRegisterUnreadCountMessageObserve(mUnreadCountObserve);
            if (mFriendMsgObserve != null)
                mImManager.registerFriendMsgObserve(mFriendMsgObserve, false);
        }
    }

    public void setWaitProcessUnreadCount(int unreadCount) {
        mWaitProcessUnreadCount = unreadCount;
        updateMessageUnreadCountView();
    }

    private void updateMessageUnreadCountView() {
        //消息tab只保留事项提醒数据
        mHomeView.callMessageUnReadCountUpdate(
//                mMessageUnreadCount + mFriendRequestCount + mWaitProcessUnreadCount +
                mUnCompleteRecommendCount);
    }

    private void onImSyncDataChanged(boolean isImSyncDataCompleted) {
        LogUtil.d("isImSyncDataCompleted  =" + isImSyncDataCompleted);
        if (isImSyncDataCompleted) {
            mImManager.getRecentContact(null);
        }
    }

    public void setIMNotifyEnable(final boolean enable) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mImManager.setChattingAccount(null, IMConst.SESSION_TYPE_NONE, !enable);
            }
        });
    }

    private void checkOperationActivity() {
        OperationalDao.queryTargetActivityDialogShowCount(UserInfoManager.getInstance().getUserInfo().unionId, OperationalDao.ACTIVITY_ID_NEW_VERSION, new IDBReplyListener<Integer>() {
            @Override
            public void onDBReply(Integer integer) {
                if (integer == null || integer == 0) {
                    OperationalDao.loadStatusOfTargetActivity(OperationalDao.ACTIVITY_ID_NEW_VERSION, new IDefaultRequestReplyListener<ActivityStatusResultBean.Data>() {
                        @Override
                        public void onRequestReply(boolean success, final ActivityStatusResultBean.Data data, int errorCode, String errorMsg) {
                            if (success && data != null && data.status == ActivityStatusResultBean.Data.STATUS_OK) {
                                ResumeDao.SearchParam searchParam = new ResumeDao.SearchParam().isSearchMineResume(true).offset(0).pageSize(1).build();
                                ResumeDao.doSearch(searchParam, new IDefaultRequestReplyListener<ResumeSearchBean>() {
                                    @Override
                                    public void onRequestReply(boolean success, ResumeSearchBean resumeSearchBean, int errorCode, String errorMsg) {
                                        if (success) {
                                            if (resumeSearchBean != null && resumeSearchBean.recordList != null && !resumeSearchBean.recordList.isEmpty())
                                                mCachedActivityResumeId = resumeSearchBean.recordList.get(0).candidateId;
                                            publishDialogShowTask(new DialogTask(DialogTask.TYPE_DEFAULT_ACTIVITY, data, HomePresenter.this), false);
                                        }
                                    }
                                });
                            }
                        }
                    });

                }
            }
        });
    }

    public void receiveNewVersionGift() {
        mHomeView.callShowProgress(null, true);
        OperationalDao.receiveNewVersionGift(new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mHomeView.callCancelProgress();
                if (success) {
                    mHomeView.callShowToastMessage(null, R.string.receive_success);
                    if (mCachedActivityResumeId != null) {
                        ResumeDetailActivity.launch(mHomeView.callGetBindActivity(), new IResumeDetailView.IntentParam(mCachedActivityResumeId));
                    }
                } else
                    mHomeView.callShowToastMessage(errorMsg, 0);
                publishDialogShowTask(null, true);
            }
        });
    }

    private void doDialogTask(DialogTask dialogTask) {
        switch (dialogTask.mType) {
            case DialogTask.TYPE_NEW_APP_VERSION:
                mHomeView.callShowNewVersionDialog((VersionBean) dialogTask.mData);
                break;
            case DialogTask.TYPE_CONFIG_ACTIVITY:
                mHomeView.callShowConfigActivityDialog((ActivityPrepareDao.DialogConfig) dialogTask.mData);
                break;
//            case DialogTask.TYPE_DEFAULT_ACTIVITY:
//                mHomeView.callShowActivityDialog((ActivityStatusResultBean.Data) dialogTask.mData);
//                break;
        }
    }

    public void publishDialogShowTask(DialogTask dialogTask, boolean finishCurrentTask) {
        if (mDialogTaskLinkedList == null)
            mDialogTaskLinkedList = new LinkedList<>();
        if (dialogTask != null)
            mDialogTaskLinkedList.add(dialogTask);
        if (mCurrentShowDialogTask != null && finishCurrentTask)
            mCurrentShowDialogTask.mFinished = true;
        if (mCurrentShowDialogTask == null || mCurrentShowDialogTask.mFinished) {
            if (!mDialogTaskLinkedList.isEmpty()) {
                mCurrentShowDialogTask = mDialogTaskLinkedList.removeFirst();
                mCurrentShowDialogTask.mFinished = false;
                mHandler.post(mCurrentShowDialogTask);
                return;
            }
            mCurrentShowDialogTask = null;
            LogUtil.d("no dialogShowTask and set CurrentShowDialogTask null");
        }
    }

    public static class DialogTask implements Runnable {
        static final int TYPE_NEW_APP_VERSION = 1;
        static final int TYPE_CONFIG_ACTIVITY = 2;
        static final int TYPE_DEFAULT_ACTIVITY = 3;
        int mType;
        Object mData;
        boolean mFinished;
        HomePresenter mPresenter;

        DialogTask(int type, Object data, HomePresenter presenter) {
            this.mType = type;
            this.mData = data;
            this.mPresenter = presenter;
        }

        @Override
        public void run() {
            if (mPresenter != null) {
                try {
                    mPresenter.doDialogTask(this);
                } catch (Exception e) {
                    //ignore
                    e.printStackTrace();
                }
            }
        }
    }
}
