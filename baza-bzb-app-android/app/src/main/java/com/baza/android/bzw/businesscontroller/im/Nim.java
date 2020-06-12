package com.baza.android.bzw.businesscontroller.im;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.baza.android.bzw.bean.friend.FriendContactRecordBean;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.bean.user.VerifyStatusBean;
import com.baza.android.bzw.businesscontroller.publish.ForwardingActivity;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.ConfigConst;
import com.baza.android.bzw.constant.SharedPreferenceConst;
import com.baza.android.bzw.dao.FriendDao;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.NFManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.slib.storage.sharedpreference.SharedPreferenceManager;
import com.slib.utils.AppUtil;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/6/1.
 * Title：
 * Note：
 */

public class Nim {
    private Context mContext;
    private NimLoginSyncDataObserve mNimLoginSyncDataObserve;
    private List<IMRecentContact> mRecentContactList = new ArrayList<>();
    private boolean mHasLoadRecentContactList, mIsOnLoadRecentContactList;
    private boolean mFriendNotify = true;
    private int mOnLineStatusCode;
    private int mFriendNoticeUnreadMsgCount;
    private String mCurrentAccount;
    private HashMap<String, Object> mIosPush = new HashMap(1);
    private HashMap<String, Object> mDefaultExtension = new HashMap(1);

    public Nim(Context mContext) {
        this.mContext = mContext;
    }

    public void init(String account, String token) {
        LoginInfo loginInfo = null;
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            loginInfo = new LoginInfo(account, token);
        }
        NIMClient.init(mContext, loginInfo, getOptions());
    }

    public void registerCustomAttachmentParser() {
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new BZWCustomAttachParser()); // 监听的注册，必须在主进程中。
    }

    public String getImRootPath() {
        return Environment.getExternalStorageDirectory() + "/" + mContext.getApplicationInfo().packageName + "/bzwIm";
    }

    private SDKOptions getOptions() {
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。
        options.statusBarNotificationConfig = loadStatusBarNotificationConfig();
        // 配置保存图片，文件，log等数据的目录
        options.sdkStorageRootPath = getImRootPath();

        // 配置数据库加密秘钥
        options.databaseEncryptKey = "BZW99090";
        options.appKey = ConfigConst.NIM_KEY;

        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小，
        options.thumbnailSize = (int) (165.0 / 320.0 * ScreenUtil.screenWidth);

        // 用户信息提供者
        options.userInfoProvider = IMUserInfoProvider.getInstance(mContext);

//        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
//        options.messageNotifierCustomization = messageNotifierCustomization;

        // 在线多端同步未读数
        options.sessionReadAck = true;

        // 云信私有化配置项
//        configServerAddress(options);

        return options;
    }

    // 这里开发者可以自定义该应用初始的 StatusBarNotificationConfig
    private StatusBarNotificationConfig loadStatusBarNotificationConfig() {
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        // 点击通知需要跳转到的界面
        config.notificationEntrance = ForwardingActivity.class;
        config.notificationSmallIconId = R.drawable.ic_launcher;
//        config.notificationColor = mContext.getResources().getColor(R.color.main_them_color);
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.bznet.android.rcbox/raw/msg";

        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        return config;
    }

    public void login(String account, String token, final IImLoginListener loginListener) {
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(token))
            return;
        this.mCurrentAccount = account;
        mIosPush.put("apns_sessionId", mCurrentAccount);
        mDefaultExtension.put(IMConst.DEFAULT_CONTENT, mContext.getResources().getString(R.string.unkonw_im));
        AbortableFuture<LoginInfo> loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(account, token));
        loginRequest.setCallback(new RequestCallbackWrapper() {
            @Override
            public void onResult(int code, Object result, Throwable exception) {
                if (loginListener != null)
                    loginListener.onImLoginResult(code == ResponseCode.RES_SUCCESS);
            }
        });
    }

    public void clearRecentContact() {
        mRecentContactList.clear();
    }

    public NimLoginSyncDataObserve getNimLoginSyncDataObserve() {
        if (mNimLoginSyncDataObserve == null)
            mNimLoginSyncDataObserve = new NimLoginSyncDataObserve();
        return mNimLoginSyncDataObserve;
    }

    public void logout() {
        mCurrentAccount = null;
        mIosPush.clear();
        NIMClient.getService(AuthService.class).logout();
    }

    public void observeMessageChange(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeRecentContact(mNimRecentContactObserver, register);
        service.observeReceiveMessage(mIncomingMessageObserver, register);
        service.observeMsgStatus(mMessageStatusObserver, register);
        service.observeCustomNotification(mCustomNotificationObserver, register);
        if (register)
            setAddFriendUnreadCount(0);
    }

    /**
     * *************************连接状态*******************************************
     */
    private Observer<StatusCode> mOnlineStatusObserve = new Observer<StatusCode>() {
        @Override
        public void onEvent(StatusCode statusCode) {
            LogUtil.d("statusCode  = " + statusCode);
            mOnLineStatusCode = IMConst.IM_ONLINE_STATUS_LOGINED;
            if (statusCode == StatusCode.LOGINING)
                mOnLineStatusCode = IMConst.IM_ONLINE_STATUS_LOGINING;
            else if (statusCode == StatusCode.CONNECTING)
                mOnLineStatusCode = IMConst.IM_ONLINE_STATUS_CONNECTING;
            else if (statusCode == StatusCode.NET_BROKEN)
                mOnLineStatusCode = IMConst.IM_ONLINE_STATUS_NO_NET;
            else if (statusCode == StatusCode.UNLOGIN)
                mOnLineStatusCode = IMConst.IM_ONLINE_STATUS_UNLOGIN;
            else if (statusCode == StatusCode.KICKOUT)
                mOnLineStatusCode = IMConst.IM_ONLINE_STATUS_KICKOUT;
            else if (statusCode == StatusCode.PWD_ERROR)
                mOnLineStatusCode = IMConst.IM_ONLINE_STATUS_PWD_ERROR;
            if (!mOnLineStatusObserves.isEmpty()) {
                for (int i = 0, size = mOnLineStatusObserves.size(); i < size; i++) {
                    try {
                        mOnLineStatusObserves.get(i).onImOnLineStatusChanged(mOnLineStatusCode);
                    } catch (Exception e) {
                    }
                }
            }
        }
    };

    private ArrayList<IIMOnLineStatusObserve> mOnLineStatusObserves = new ArrayList<>();

    public void registerOnlineStatusObserver(IIMOnLineStatusObserve onLineStatusObserve, boolean register) {
        if (onLineStatusObserve == null)
            return;
        if (register) {
            mOnLineStatusObserves.add(onLineStatusObserve);
            onLineStatusObserve.onImOnLineStatusChanged(mOnLineStatusCode);
        } else
            mOnLineStatusObserves.remove(onLineStatusObserve);
    }

    public void registerNimOnlineStatusObserver(boolean register) {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(mOnlineStatusObserve, register);
    }
    /**
     *********************************************************************
     */
    /**
     * 消息状态变化观察者
     */
    private ArrayList<IIMMessageStatusObserver> mIMMessageStatusObserves = new ArrayList<>();

    public void registerIMMessageStatusObserves(IIMMessageStatusObserver observer, boolean register) {
        if (observer != null) {
            if (register)
                mIMMessageStatusObserves.add(observer);
            else
                mIMMessageStatusObserves.remove(observer);
        }
    }

    private Observer<IMMessage> mMessageStatusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            if (!mIMMessageStatusObserves.isEmpty()) {
                BZWIMMessage bzwimMessage = new BZWIMMessage(message);
                for (int i = 0, size = mIMMessageStatusObserves.size(); i < size; i++) {
                    mIMMessageStatusObserves.get(i).onIMMessageStatusChanged(bzwimMessage);
                }
            }
        }
    };
    /********************************************************
     * 自定义系统消息监听
     */
    Observer<CustomNotification> mCustomNotificationObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification message) {
            // 在这里处理自定义通知。
            LogUtil.d("-------------CustomNotification------------");
            if (message != null && message.getContent() != null)
                parseSystemNotice(message.getContent());

        }
    };

    /*******************************************/
    /**********************收到新消息*************************/
    Observer<List<IMMessage>> mIncomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            if (messages == null || messages.isEmpty()) {
                return;
            }
            if (!mIMMessageObserves.isEmpty()) {
                List<BZWIMMessage> list = new ArrayList<>(messages.size());
                for (int i = 0, size = messages.size(); i < size; i++) {
                    list.add(new BZWIMMessage(messages.get(i)));
                }

                for (int i = 0, size = mIMMessageObserves.size(); i < size; i++) {
                    try {
                        mIMMessageObserves.get(i).onNewIMMessageArrival(list);
                    } catch (Exception e) {
                    }
                }
            }
        }
    };
    private ArrayList<IIMMessageObserve> mIMMessageObserves = new ArrayList<>();

    public void registerNewIMMessageObserve(IIMMessageObserve observe) {
        if (observe != null)
            mIMMessageObserves.add(observe);
    }

    public void unRegisterNewIMMessageObserve(IIMMessageObserve observe) {
        if (observe != null)
            mIMMessageObserves.remove(observe);
    }

    /****************************最近联系人获取*************************************/
    private List<IRecentContactGetCallObserve> mRecentContactGetObserves = new ArrayList<>();

    public void getRecentContact(final IRecentContactGetCallObserve observer) {
        if (mHasLoadRecentContactList) {
            if (observer != null)
                observer.onGetRecentContact(mRecentContactList);
            return;
        }
        if (observer != null)
            mRecentContactGetObserves.add(observer);
        if (mIsOnLoadRecentContactList) {
            return;
        }
        mIsOnLoadRecentContactList = true;
        // 查询最近联系人列表数据
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
            @Override
            public void onResult(int code, List<RecentContact> recentContacts, Throwable throwable) {
                mIsOnLoadRecentContactList = false;
                if (code != ResponseCode.RES_SUCCESS) {
                    mHasLoadRecentContactList = true;
                }
                mRecentContactList.clear();
                if (recentContacts != null && !recentContacts.isEmpty()) {
                    for (int i = 0, size = recentContacts.size(); i < size; i++) {
                        mRecentContactList.add(new IMRecentContact(recentContacts.get(i)));
                    }
                }
                if (!mRecentContactGetObserves.isEmpty()) {
                    for (int i = 0, size = mRecentContactGetObserves.size(); i < size; i++) {
                        try {
                            mRecentContactGetObserves.get(i).onGetRecentContact(mRecentContactList);
                        } catch (Exception w) {
                        }
                    }
                }
                mRecentContactGetObserves.clear();
                noticeUnreadCount();
            }
        });
    }

    /******************************最近联系人消息改变***********************************/
    private List<IRecentContactMessageObserve> mRecentContactMessageObserves = new ArrayList<>();

    public void registerRecentContactMessageObserve(IRecentContactMessageObserve observe) {
        if (observe != null)
            mRecentContactMessageObserves.add(observe);
    }

    public void unRegisterRecentContactMessageObserve(IRecentContactMessageObserve observe) {
        if (observe != null)
            mRecentContactMessageObserves.remove(observe);
    }

    private Observer<List<RecentContact>> mNimRecentContactObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> recentContacts) {
            if (recentContacts == null || recentContacts.isEmpty())
                return;
            List<IMRecentContact> recentContactList = new ArrayList<>(recentContacts.size());
            List<FriendContactRecordBean> friendContactRecordBeans = new ArrayList<>(recentContacts.size());
            String unionId = UserInfoManager.getInstance().getUserInfo().unionId;
            for (int i = 0, size = recentContacts.size(); i < size; i++) {
                recentContactList.add(new IMRecentContact(recentContacts.get(i)));
                friendContactRecordBeans.add(new FriendContactRecordBean(unionId, recentContacts.get(i).getContactId(), System.currentTimeMillis()));
            }
            IMRecentContact icNew, icOld;
            int indexOld;
            int sizeOld = mRecentContactList.size();
            int indexTarget;
            for (int indexNew = 0, sizeNew = recentContactList.size(); indexNew < sizeNew; indexNew++) {
                icNew = recentContactList.get(indexNew);
                indexTarget = -1;
                for (indexOld = 0; indexOld < sizeOld; indexOld++) {
                    icOld = mRecentContactList.get(indexOld);
                    if (icNew.getContactId().equals(icOld.getContactId()) && icNew.nimRecentContact.getSessionType() == icOld.nimRecentContact.getSessionType()) {
                        indexTarget = indexOld;
                        break;
                    }
                }
                if (indexTarget >= 0) {
                    mRecentContactList.remove(indexTarget);
                }
                mRecentContactList.add(icNew);
            }
//            sortRecentContacts(mRecentContactList);
            if (!mRecentContactMessageObserves.isEmpty()) {
                for (int i = 0, size = mRecentContactMessageObserves.size(); i < size; i++) {
                    try {
                        mRecentContactMessageObserves.get(i).onRecentContactMessageEvent(mRecentContactList);
                    } catch (Exception e) {
                    }
                }
            }
            noticeUnreadCount();
            //更新最新的好友联系，分享选择好友排序时使用
            FriendDao.updateFriendContactRecords(friendContactRecordBeans);
        }
    };
    /******************************消息未读数***********************************/
    private List<IUnreadCountObserve> mUnreadCountObserves = new ArrayList<>();

    public void registerUnreadCountMessageObserve(IUnreadCountObserve observe) {
        if (observe != null)
            mUnreadCountObserves.add(observe);
    }

    public void unRegisterUnreadCountMessageObserve(IUnreadCountObserve observe) {
        if (observe != null)
            mUnreadCountObserves.remove(observe);
    }

    private void noticeUnreadCount() {
        if (!mUnreadCountObserves.isEmpty()) {
            int unreadCount = 0;
            for (int i = 0, size = mRecentContactList.size(); i < size; i++) {
                unreadCount += mRecentContactList.get(i).nimRecentContact.getUnreadCount();
            }
            for (int i = 0, size = mUnreadCountObserves.size(); i < size; i++) {
                mUnreadCountObserves.get(i).onImUnreadCountChanged(unreadCount);
            }
        }
    }

    /**
     * **************************** 排序 ***********************************
     */
    public static void sortRecentContacts(List<IMRecentContact> list) {
        if (list.size() == 0) {
            return;
        }
        Collections.sort(list, comp);
    }

    private static Comparator<IMRecentContact> comp = new Comparator<IMRecentContact>() {

        @Override
        public int compare(IMRecentContact o1, IMRecentContact o2) {
            // 先比较置顶tag
            long time = o1.getTime() - o2.getTime();
            return time == 0 ? 0 : (time > 0 ? -1 : 1);
        }
    };


    /**
     * 好友监听
     */
    private ArrayList<IFriendMsgObserve> mFriendListeners = new ArrayList<>(4);
    private ArrayList<ISystemNoticeObserve> mSystemNoticeListeners = new ArrayList<>(1);

    public void setFriendNotifyEnable(boolean friendNotify) {
        this.mFriendNotify = friendNotify;
    }

    public void registerFriendMsgObserve(IFriendMsgObserve listener, boolean register) {
        if (listener == null)
            return;
        if (register) {
            mFriendListeners.add(listener);
            if (mFriendNoticeUnreadMsgCount > 0)
                listener.onFriendNoticeMsgUnreadCountChanged(mFriendNoticeUnreadMsgCount);
        } else
            mFriendListeners.remove(listener);
    }

    public void registerSystemNoticeObserve(ISystemNoticeObserve listener, boolean register) {
        if (listener == null)
            return;
        LogUtil.d("registerSystemNoticeObserve");
        if (register) {
            mSystemNoticeListeners.add(listener);
        } else
            mSystemNoticeListeners.remove(listener);
    }

    private void onFriendAskCustomNotification(String from, String messageContent) {
        if (TextUtils.isEmpty(messageContent))
            return;
        FriendListResultBean.FriendBean friendBean = null;
        try {
            friendBean = JSON.parseObject(messageContent, FriendListResultBean.FriendBean.class);
        } catch (Exception e) {
            //ignore
        }
        if (friendBean == null)
            return;
        if (mFriendNotify)
            NFManager.updateFriendNotification(mContext, mContext.getResources().getString(R.string.friend_hint_request), friendBean.message);
        if (!mFriendListeners.isEmpty()) {
            for (int i = 0, size = mFriendListeners.size(); i < size; i++) {
                mFriendListeners.get(i).onFriendRequestGet(from);
            }
        }
        setAddFriendUnreadCount(1);
    }

    private void onFriendAgreeCustomNotification(String from, String messageContent) {
        // 对方通过了你的好友验证请求
        if (TextUtils.isEmpty(messageContent))
            return;
        FriendListResultBean.FriendBean friendBean = null;
        try {
            friendBean = JSON.parseObject(messageContent, FriendListResultBean.FriendBean.class);
        } catch (Exception e) {
            //ignore
        }
        if (friendBean == null)
            return;
//        if (mFriendNotify)
//            NFManager.updateFriendNotification(mContext, mContext.getResources().getString(R.string.friend_hint_pass), mContext.getResources().getString(R.string.who_agree_friend_request, (!TextUtils.isEmpty(friendBean.nickName) ? friendBean.nickName : (!TextUtils.isEmpty(friendBean.trueName) ? friendBean.trueName : CommonConst.STR_DEFAULT_USER_NAME_SX))));
        if (!mFriendListeners.isEmpty()) {
            for (int i = 0, size = mFriendListeners.size(); i < size; i++) {
                mFriendListeners.get(i).onFriendAgreeGet(from);
            }
        }
//        setAddFriendUnreadCount(1);
    }

    private void setAddFriendUnreadCount(int addCount) {
        if (addCount > 0) {
            mFriendNoticeUnreadMsgCount += addCount;
            SharedPreferenceManager.saveInt(SharedPreferenceConst.SP_FRIEND_NOTIFY_UN_READ + mCurrentAccount, mFriendNoticeUnreadMsgCount);
        } else
            mFriendNoticeUnreadMsgCount = SharedPreferenceManager.getInt(SharedPreferenceConst.SP_FRIEND_NOTIFY_UN_READ + mCurrentAccount);
        if (!mFriendListeners.isEmpty()) {
            for (int i = 0, size = mFriendListeners.size(); i < size; i++) {
                mFriendListeners.get(i).onFriendNoticeMsgUnreadCountChanged(mFriendNoticeUnreadMsgCount);
            }
        }
    }

    public void clearFriendMsgUnreadCount() {
        mFriendNoticeUnreadMsgCount = 0;
        SharedPreferenceManager.saveInt(SharedPreferenceConst.SP_FRIEND_NOTIFY_UN_READ + mCurrentAccount, mFriendNoticeUnreadMsgCount);
        if (!mFriendListeners.isEmpty()) {
            for (int i = 0, size = mFriendListeners.size(); i < size; i++) {
                mFriendListeners.get(i).onFriendNoticeMsgUnreadCountChanged(mFriendNoticeUnreadMsgCount);
            }
        }
    }

    /**
     * **************************** ***********************************
     */

    public void loadP2PImMessage(BZWIMMessage bzwimMessage, int count, String account, int sessionType, final IIMMessageGetListener listener) {
        IMMessage imMessage;
        SessionTypeEnum sessionTypeEnum;
        switch (sessionType) {
            case IMConst.SESSION_TYPE_SYSTEM:
                sessionTypeEnum = SessionTypeEnum.System;
                break;
            case IMConst.SESSION_TYPE_TEAM:
                sessionTypeEnum = SessionTypeEnum.Team;
                break;
            default:
                sessionTypeEnum = SessionTypeEnum.P2P;
                break;
        }
        if (bzwimMessage == null)
            imMessage = MessageBuilder.createEmptyMessage(account, sessionTypeEnum, 0);
        else
            imMessage = bzwimMessage.imMessage;
        NIMClient.getService(MsgService.class).queryMessageListEx(imMessage, QueryDirectionEnum.QUERY_OLD, count, true)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                    @Override
                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
                        List<BZWIMMessage> messages = null;
                        if (result != null && !result.isEmpty()) {
                            messages = new ArrayList<>(result.size());
                            for (int i = 0, size = result.size(); i < size; i++) {
                                messages.add(new BZWIMMessage(result.get(i)));
                            }
                        }
                        if (listener != null) {
                            try {
                                listener.onIMMessageGet(messages);
                            } catch (Exception w) {
                            }
                        }
                    }
                });
    }

    public void setChattingAccount(String account, int sessionType, boolean noNotify) {
        if (noNotify) {
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
            return;
        }
        account = (account == null ? MsgService.MSG_CHATTING_ACCOUNT_NONE : account);
        SessionTypeEnum sessionTypeEnum;
        switch (sessionType) {
            case IMConst.SESSION_TYPE_NONE:
                sessionTypeEnum = SessionTypeEnum.None;
                break;
            case IMConst.SESSION_TYPE_SYSTEM:
                sessionTypeEnum = SessionTypeEnum.System;
                break;
            case IMConst.SESSION_TYPE_TEAM:
                sessionTypeEnum = SessionTypeEnum.Team;
                break;
            default:
                sessionTypeEnum = SessionTypeEnum.P2P;
                break;
        }
        NIMClient.getService(MsgService.class).setChattingAccount(account, sessionTypeEnum);
    }

    private SessionTypeEnum getSessionTypeEnum(int sessionType) {
        SessionTypeEnum sessionTypeEnum;
        switch (sessionType) {
            case IMConst.SESSION_TYPE_NONE:
                sessionTypeEnum = SessionTypeEnum.None;
                break;
            case IMConst.SESSION_TYPE_SYSTEM:
                sessionTypeEnum = SessionTypeEnum.System;
                break;
            case IMConst.SESSION_TYPE_TEAM:
                sessionTypeEnum = SessionTypeEnum.Team;
                break;
            default:
                sessionTypeEnum = SessionTypeEnum.P2P;
                break;
        }
        return sessionTypeEnum;
    }

    public boolean isEnableToSendMsg() {
        return !(mOnLineStatusCode == IMConst.IM_ONLINE_STATUS_LOGINING || mOnLineStatusCode == IMConst.IM_ONLINE_STATUS_UNLOGIN || mOnLineStatusCode == IMConst.IM_ONLINE_STATUS_KICKOUT || mOnLineStatusCode == IMConst.IM_ONLINE_STATUS_PWD_ERROR || mOnLineStatusCode == IMConst.IM_ONLINE_STATUS_CONNECTING);
    }

    /******************************/
    public void sendTextMessage(String account, int sessionType, String textMessage, ISendMessageListener listener) {
        if (!isEnableToSendMsg())
            return;
        IMMessage imMessage = MessageBuilder.createTextMessage(account, getSessionTypeEnum(sessionType), textMessage);
        imMessage.setPushPayload(mIosPush);
        NIMClient.getService(MsgService.class).sendMessage(imMessage, false);
        if (listener != null) {
            BZWIMMessage bzwimMessage = new BZWIMMessage(imMessage);
            listener.onMessageSend(bzwimMessage);
        }
    }

    public void sendAudioMessage(String account, int sessionType, File audioFile, long duration, ISendMessageListener listener) {
        if (!isEnableToSendMsg())
            return;
        IMMessage imMessage = MessageBuilder.createAudioMessage(account, getSessionTypeEnum(sessionType), audioFile, duration);
        imMessage.setPushPayload(mIosPush);
        imMessage.setRemoteExtension(mDefaultExtension);
        NIMClient.getService(MsgService.class).sendMessage(imMessage, false);
        if (listener != null) {
            BZWIMMessage bzwimMessage = new BZWIMMessage(imMessage);
            listener.onMessageSend(bzwimMessage);
        }
    }

//    public void sendCustomMessage(String account, int sessionType, String content, ExtraMessageBean extraMessageBean, ISendMessageListener listener) {
//        if (!isEnableToSendMsg())
//            return;
//        IMMessage msg = MessageBuilder.createCustomMessage(account, getSessionTypeEnum(sessionType), content, new BZWCustomerAttachment(extraMessageBean));
//        msg.setPushPayload(mIosPush);
//        msg.setRemoteExtension(mDefaultExtension);
//        NIMClient.getService(MsgService.class).sendMessage(msg, false);
//        if (listener != null) {
//            BZWIMMessage bzwimMessage = new BZWIMMessage(msg);
//            listener.onMessageSend(bzwimMessage);
//        }
//    }

    public void sendImageMessage(String account, int sessionType, File imageFile, ISendMessageListener listener) {
        if (!isEnableToSendMsg())
            return;
        IMMessage imMessage = MessageBuilder.createImageMessage(account, getSessionTypeEnum(sessionType), imageFile, null);
        imMessage.setPushPayload(mIosPush);
        imMessage.setRemoteExtension(mDefaultExtension);
        NIMClient.getService(MsgService.class).sendMessage(imMessage, false);
        if (listener != null) {
            BZWIMMessage bzwimMessage = new BZWIMMessage(imMessage);
            listener.onMessageSend(bzwimMessage);
        }
    }

    public void sendFileMessage(String account, int sessionType, File file, ISendMessageListener listener) {
        if (!isEnableToSendMsg())
            return;
        IMMessage imMessage = MessageBuilder.createFileMessage(account, getSessionTypeEnum(sessionType), file, file.getName());
        imMessage.setPushPayload(mIosPush);
        imMessage.setRemoteExtension(mDefaultExtension);
        NIMClient.getService(MsgService.class).sendMessage(imMessage, false);
        if (listener != null) {
            BZWIMMessage bzwimMessage = new BZWIMMessage(imMessage);
            listener.onMessageSend(bzwimMessage);
        }
    }

    public void resendMessage(BZWIMMessage bzwimMessage) {
        NIMClient.getService(MsgService.class).sendMessage(bzwimMessage.imMessage, true);
    }

    public BZWIMMessage parseNotifyMessage(Intent intent) {
        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            if (messages != null && !messages.isEmpty())
                return new BZWIMMessage(messages.get(0));
        }
        return null;
    }

    public void updateIMMessageStatus(BZWIMMessage bzwimMessage) {
        if (bzwimMessage.imMessage != null)
            NIMClient.getService(MsgService.class).updateIMMessageStatus(bzwimMessage.imMessage);
    }

    public void deleteConversation(IMRecentContact imRecentContact) {
        if (imRecentContact == null || imRecentContact.nimRecentContact == null)
            return;
        NIMClient.getService(MsgService.class).deleteRecentContact(imRecentContact.nimRecentContact);
        if (imRecentContact.getSessionType() == IMConst.SESSION_TYPE_P2P)
            NIMClient.getService(MsgService.class).clearChattingHistory(imRecentContact.getContactId(), SessionTypeEnum.P2P);
        if (mRecentContactList.remove(imRecentContact)) {
            if (!mRecentContactMessageObserves.isEmpty()) {
                for (int i = 0, size = mRecentContactMessageObserves.size(); i < size; i++) {
                    try {
                        mRecentContactMessageObserves.get(i).onRecentContactMessageEvent(mRecentContactList);
                    } catch (Exception e) {
                    }
                }
            }
        }
        noticeUnreadCount();
    }

    public void downloadAttachment(BZWIMMessage bzwimMessage, boolean thumb) {
        if (bzwimMessage != null && bzwimMessage.imMessage != null)
            NIMClient.getService(MsgService.class).downloadAttachment(bzwimMessage.imMessage, thumb);

    }
//    public void sendAgreeFriendNotify(String toAccount) {
//        NIMClient.getService(FriendService.class).ackAddFriendRequest(toAccount, true);
//    }

    private void parseSystemNotice(String content) {
        if (TextUtils.isEmpty(content))
            return;
        int sceneType = CommonConst.IMNoticeType.SCENE_TYPE_NONE;
        String messageContent = null;
        String from = null;
        LogUtil.d(content);
        try {
            JSONObject jsonObject = new JSONObject(content);
            sceneType = jsonObject.getInt("sceneType");
            messageContent = jsonObject.getString("content");
            if (jsonObject.has("from"))
                from = jsonObject.getString("from");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (sceneType == CommonConst.IMNoticeType.SCENE_TYPE_NONE)
            return;
        switch (sceneType) {
            case CommonConst.IMNoticeType.SCENE_TYPE_FRIEND_ASK:
                onFriendAskCustomNotification(from, messageContent);
                break;
            case CommonConst.IMNoticeType.SCENE_TYPE_FRIEND_ACCEPT:
                onFriendAgreeCustomNotification(from, messageContent);
                break;
            case CommonConst.IMNoticeType.SCENE_TYPE_USER_VERIFY:
                VerifyStatusBean verifyStatusBean = AppUtil.jsonToObject(messageContent, VerifyStatusBean.class);
                if (verifyStatusBean != null)
                    notifySystemNotice(ISystemNoticeObserve.TYPE_USER_VERIFY, verifyStatusBean);
                break;
        }
    }

    private void notifySystemNotice(int type, Object value) {
        for (int i = 0, size = mSystemNoticeListeners.size(); i < size; i++) {
            mSystemNoticeListeners.get(i).onSystemNoticeArrival(type, value);
        }
    }
}
