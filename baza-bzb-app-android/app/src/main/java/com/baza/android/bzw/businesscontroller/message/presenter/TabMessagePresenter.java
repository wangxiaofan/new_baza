package com.baza.android.bzw.businesscontroller.message.presenter;

import android.os.Handler;
import android.os.Looper;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.push.NoticeUnreadResultBean;
import com.baza.android.bzw.bean.recommend.RecommendBean;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.businesscontroller.im.BZWIMMessage;
import com.baza.android.bzw.businesscontroller.im.IFriendMsgObserve;
import com.baza.android.bzw.businesscontroller.im.IIMMessageStatusObserver;
import com.baza.android.bzw.businesscontroller.im.IIMOnLineStatusObserve;
import com.baza.android.bzw.businesscontroller.im.IMRecentContact;
import com.baza.android.bzw.businesscontroller.im.IMUserInfoProvider;
import com.baza.android.bzw.businesscontroller.im.IRecentContactGetCallObserve;
import com.baza.android.bzw.businesscontroller.im.IRecentContactMessageObserve;
import com.baza.android.bzw.businesscontroller.im.IUserInfoObserve;
import com.baza.android.bzw.businesscontroller.message.viewinterface.ITabMessageView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.PushDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.IMManager;
import com.baza.android.bzw.manager.RecommendManager;
import com.bznet.android.rcbox.R;
import com.netease.nimlib.sdk.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/6/1.
 * Title：
 * Note：
 */

public class TabMessagePresenter extends BasePresenter {
    private ITabMessageView mTabMessageView;
    private IMManager mImManager;
    private Observer<Void> mIsImSyncDataCompletedObserver;
    private IRecentContactMessageObserve mRecentContactMessageObserve;
    private List<IMRecentContact> mRecentContactList = new ArrayList<>();
    private boolean mShouldRefreshOnResume;
    private boolean mOnRefreshingNoticeUnreadCount;
    private boolean mHidden;
    private IIMMessageStatusObserver mIImMessageStatusObserver;
    private IIMOnLineStatusObserve mIImOnLineStatusObserve;
    private IFriendMsgObserve mFriendMsgObserve;
    private IUserInfoObserve mUserInfoObserve;
    private RecommendManager.IRecommendListener mRecommendListener;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public TabMessagePresenter(ITabMessageView mTabMessageView) {
        this.mTabMessageView = mTabMessageView;
        this.mImManager = IMManager.getInstance(null);
    }

    @Override
    public void initialize() {
        mergeImAndOther();
        registerObserver(true);
        mTabMessageView.callRefreshTargetRecentView(CommonConst.LIST_POSITION_NONE);
    }

    public List<IMRecentContact> getRecentContactList() {
        return mRecentContactList;
    }

    @Override
    public void onResume() {
        onHiddenChanged(mHidden);
    }

    public void onHiddenChanged(boolean hidden) {
        mHidden = hidden;
        if (!mHidden) {
            if (mShouldRefreshOnResume) {
                mShouldRefreshOnResume = false;
                mTabMessageView.callRefreshTargetRecentView(CommonConst.LIST_POSITION_NONE);
            }
            loadNoticeUnreadCount();
        }
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        registerObserver(false);
    }


    private void registerObserver(boolean register) {
        if (register) {
            //注册同步状态监听
            if (mIsImSyncDataCompletedObserver == null)
                mIsImSyncDataCompletedObserver = new Observer<Void>() {
                    @Override
                    public void onEvent(Void aVoid) {
                        onImSyncDataChanged(true);
                    }
                };

            boolean isImSyncDataCompleted = mImManager.observeSyncDataCompletedEvent(mIsImSyncDataCompletedObserver);
            onImSyncDataChanged(isImSyncDataCompleted);
            //注册最近联系人监听
            if (mRecentContactMessageObserve == null)
                mRecentContactMessageObserve = new IRecentContactMessageObserve() {
                    @Override
                    public void onRecentContactMessageEvent(List<IMRecentContact> recentContactList) {
                        mRecentContactList.clear();
                        mRecentContactList.addAll(recentContactList);
                        mergeImAndOther();
                        if (mHidden)
                            mShouldRefreshOnResume = true;
                        else
                            mTabMessageView.callRefreshTargetRecentView(CommonConst.LIST_POSITION_NONE);
                    }
                };
            mImManager.registerRecentContactMessageObserve(mRecentContactMessageObserve);
            //注册消息发送状态监听
            if (mIImMessageStatusObserver == null)
                mIImMessageStatusObserver = new IIMMessageStatusObserver() {
                    @Override
                    public void onIMMessageStatusChanged(BZWIMMessage bzwimMessage) {
                        int targetPosition = CommonConst.LIST_POSITION_NONE;
                        String uuid;
                        IMRecentContact baseConversation;
                        for (int i = 0, size = mRecentContactList.size(); i < size; i++) {
                            baseConversation = mRecentContactList.get(i);
                            uuid = baseConversation.getUuid();
                            if (uuid != null && uuid.equals(bzwimMessage.getUuid())) {
                                targetPosition = i;
                                break;
                            }
                        }
                        if (targetPosition != CommonConst.LIST_POSITION_NONE) {
                            IMRecentContact im = mRecentContactList.get(targetPosition);
                            im.resetStatusByIMMessage(bzwimMessage);
                            mTabMessageView.callRefreshTargetRecentView(targetPosition);
                        }
                    }
                };
            mImManager.registerIMMessageStatusObserves(mIImMessageStatusObserver, true);
            //注册在线状态监听
            if (mIImOnLineStatusObserve == null)
                mIImOnLineStatusObserve = new IIMOnLineStatusObserve() {
                    @Override
                    public void onImOnLineStatusChanged(int status) {
                        mTabMessageView.callOnLineStatusChanged(status);
                    }
                };
            mImManager.registerOnlineStatusObserver(mIImOnLineStatusObserve, true);
            //注册用户信息变化监听
            if (mUserInfoObserve == null)
                mUserInfoObserve = new IUserInfoObserve() {
                    @Override
                    public void onUserInfoGet(UserInfoBean userInfoBean) {
                        int targetPosition = CommonConst.LIST_POSITION_NONE;
                        IMRecentContact baseConversation;
                        for (int i = 0, size = mRecentContactList.size(); i < size; i++) {
                            baseConversation = mRecentContactList.get(i);
                            if (userInfoBean.neteaseId != null && userInfoBean.neteaseId.equals(baseConversation.getContactId())) {
                                targetPosition = i;
                                break;
                            }
                        }
                        if (targetPosition != CommonConst.LIST_POSITION_NONE)
                            mTabMessageView.callRefreshTargetRecentView(targetPosition);
                    }
                };

            IMUserInfoProvider.getInstance(mTabMessageView.callGetBindActivity()).registerUserInfoObserve(mUserInfoObserve, true);

            if (mFriendMsgObserve == null)
                mFriendMsgObserve = new IFriendMsgObserve() {
                    @Override
                    public void onFriendNoticeMsgUnreadCountChanged(int unreadCount) {
                        mTabMessageView.callUpdateFriendRequestView(unreadCount);
                    }

                    @Override
                    public void onFriendRequestGet(String account) {

                    }

                    @Override
                    public void onFriendAgreeGet(String account) {

                    }
                };
            mImManager.registerFriendMsgObserve(mFriendMsgObserve, true);

            if (mRecommendListener == null)
                mRecommendListener = new RecommendManager.IRecommendListener() {
                    @Override
                    public void onUnCompletedRecommendOfTodayGet(List<RecommendBean> recommendList) {
                        mTabMessageView.callUpdateRecommendCountView(recommendList != null ? recommendList.size() : 0);
                    }
                };
            RecommendManager.getInstance().registerListener(mRecommendListener);
        } else {
            if (mFriendMsgObserve != null)
                mImManager.registerFriendMsgObserve(mFriendMsgObserve, false);
            if (mIsImSyncDataCompletedObserver != null)
                mImManager.unObserveSyncDataCompletedEvent(mIsImSyncDataCompletedObserver);

            if (mRecentContactMessageObserve != null)
                mImManager.unRegisterRecentContactMessageObserve(mRecentContactMessageObserve);

            if (mIImMessageStatusObserver != null)
                mImManager.registerIMMessageStatusObserves(mIImMessageStatusObserver, false);

            if (mIImOnLineStatusObserve != null)
                mImManager.registerOnlineStatusObserver(mIImOnLineStatusObserve, false);
            if (mUserInfoObserve != null)
                IMUserInfoProvider.getInstance(mTabMessageView.callGetBindActivity()).registerUserInfoObserve(mUserInfoObserve, false);
            if (mRecommendListener != null)
                RecommendManager.getInstance().unRegisterListener(mRecommendListener);
        }

    }

    private void onImSyncDataChanged(boolean isImSyncDataCompleted) {
        if (!isImSyncDataCompleted)
            mTabMessageView.callShowInnerProgress(R.string.on_prepare_im_data);
        else {
            mTabMessageView.callCancelInnerProgress();
            mImManager.getRecentContact(new IRecentContactGetCallObserve() {
                @Override
                public void onGetRecentContact(List<IMRecentContact> recentContactList) {
                    mRecentContactList.clear();
                    mRecentContactList.addAll(recentContactList);
                    mergeImAndOther();
                    mTabMessageView.callRefreshTargetRecentView(CommonConst.LIST_POSITION_NONE);
                }
            });
        }
    }

    private void mergeImAndOther() {
        LogUtil.d("mergeImAndPush");
        IMManager.sortRecentContacts(mRecentContactList);
    }

    public void deleteConversation(IMRecentContact imRecentContact) {
        IMManager.getInstance(mTabMessageView.callGetBindActivity()).deleteConversation(imRecentContact);
    }

    private void loadNoticeUnreadCount() {
        if (mOnRefreshingNoticeUnreadCount)
            return;
        mOnRefreshingNoticeUnreadCount = true;
        PushDao.loadNoticeUnreadCount(new IDefaultRequestReplyListener<NoticeUnreadResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, NoticeUnreadResultBean.Data data, int errorCode, String errorMsg) {
                mOnRefreshingNoticeUnreadCount = false;
                if (success && data != null)
//                    mTabMessageView.callUpdateNoticeUnreadCountView(data.systemUnReadCount + data.resumeSynUnReadCount, data.candidateRequestUnHandleCount + data.candidateShareUnHandleCount);
                    mTabMessageView.callUpdateNoticeUnreadCountView(data.systemUnReadCount + data.resumeSynUnReadCount, 0);
            }
        });
    }
}
