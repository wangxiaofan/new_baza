package com.baza.android.bzw.businesscontroller.message.presenter;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.friend.FriendAddResultBean;
import com.baza.android.bzw.bean.friend.FriendInfoResultBean;
import com.baza.android.bzw.bean.message.MessageProcessLocalTagBean;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.businesscontroller.audio.tools.AudioPlayerTool;
import com.baza.android.bzw.businesscontroller.audio.tools.RecorderTool;
import com.baza.android.bzw.businesscontroller.friend.FriendHomeActivity;
import com.baza.android.bzw.businesscontroller.im.BZWIMMessage;
import com.baza.android.bzw.businesscontroller.im.IIMMessageGetListener;
import com.baza.android.bzw.businesscontroller.im.IIMMessageObserve;
import com.baza.android.bzw.businesscontroller.im.IIMMessageStatusObserver;
import com.baza.android.bzw.businesscontroller.im.IIMOnLineStatusObserve;
import com.baza.android.bzw.businesscontroller.im.IMConst;
import com.baza.android.bzw.businesscontroller.im.IMUserInfoProvider;
import com.baza.android.bzw.businesscontroller.im.ISendMessageListener;
import com.baza.android.bzw.businesscontroller.im.IUserInfoObserve;
import com.baza.android.bzw.businesscontroller.message.ChatActivity;
import com.baza.android.bzw.businesscontroller.message.viewinterface.IChatView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.FriendDao;
import com.baza.android.bzw.dao.MessageDao;
import com.baza.android.bzw.events.IFriendOperateSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.IMManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.slib.storage.database.listener.IDBReplyListener;
import com.slib.storage.file.ImageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Vincent.Lei on 2017/6/2.
 * Title：
 * Note：
 */

public class ChatPresenter extends BasePresenter {
    private IChatView mChatView;
    private List<BZWIMMessage> mMessages = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int mMessageLoadPageCount = 10;
    private Runnable mRunnableLoadMessage;
    private IIMMessageObserve mIImMessageObserve;
    private IIMMessageStatusObserver mIImMessageStatusObserver;
    private IIMOnLineStatusObserve mIImOnLineStatusObserve;
    private IMManager mImManager;
    private IUserInfoObserve mUserInfoObserve;
    private int mMaxShowMessageCount = 200;
    private Set<String> mFlagTimeSet = new HashSet<>();
    private String mUnionId;
    private HashSet<String> mPassiveShareProcessedTagSet = new HashSet<>();
    private boolean mIsStrange;
    private ChatActivity.ChatParam mChatParam;

    public ChatPresenter(IChatView mChatView, ChatActivity.ChatParam chatParam) {
        this.mChatView = mChatView;
        this.mImManager = IMManager.getInstance(mChatView.callGetBindActivity());
        this.mChatParam = chatParam;
    }

    public void refreshNewParam(ChatActivity.ChatParam chatParam) {
        mChatParam = chatParam;
        mImManager.setChattingAccount(mChatParam.getAccount(), mChatParam.getSessionType(), false);
        mMessages.clear();
        mUnionId = null;
        prepareLocalExtraMsg();
    }

    @Override
    public void initialize() {
        registerNewMessage(true);
        registerMessageStatus(true);
        registerOnLineStatusObserve(true);
        registerUserInfoObserve(true);
        registerFriendObserve(true);
        prepareLocalExtraMsg();
    }

    private void prepareLocalExtraMsg() {
        MessageDao.readPassiveShareHasProcessedTags(UserInfoManager.getInstance().getUserInfo().unionId, mChatParam.getAccount(), new IDBReplyListener<HashSet<String>>() {
            @Override
            public void onDBReply(HashSet<String> set) {
                mPassiveShareProcessedTagSet.clear();
                if (set != null && !set.isEmpty())
                    mPassiveShareProcessedTagSet.addAll(set);
                prepareChat();
            }
        });
    }

    public HashSet<String> getPassiveShareProcessedTagSet() {
        return mPassiveShareProcessedTagSet;
    }

    private void prepareChat() {
        loadImMessage(false);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                IMUserInfoProvider imUserInfoProvider = IMUserInfoProvider.getInstance(mChatView.callGetBindActivity());
                UserInfoBean userInfoBean = imUserInfoProvider.getBZWUserInfo(mChatParam.getAccount());
                String name = CommonConst.STR_DEFAULT_USER_NAME_SX;
                if (userInfoBean != null)
                    name = !TextUtils.isEmpty(userInfoBean.nickName) ? userInfoBean.nickName : !TextUtils.isEmpty(userInfoBean.trueName) ? userInfoBean.trueName : name;
                mChatView.callSetTitle(name);
                imUserInfoProvider.refreshUserInfoByAccount(mChatParam.getAccount());
            }
        });
        mChatView.callUpdateIsFriendView(true);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                checkIsMyFriend();
            }
        });
    }

    @Override
    public void onResume() {
        mImManager.setChattingAccount(mChatParam.getAccount(), mChatParam.getSessionType(), false);
    }

    @Override
    public void onPause() {
        mImManager.setChattingAccount(null, IMConst.SESSION_TYPE_NONE, false);
    }

    public String getUnionId() {
        return mUnionId;
    }

    @Override
    public void onDestroy() {
        registerOnLineStatusObserve(false);
        registerNewMessage(false);
        registerMessageStatus(false);
        registerUserInfoObserve(false);
        registerFriendObserve(false);
        releaseRecordResource();
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
    }

    public List<BZWIMMessage> getMessages() {
        return mMessages;
    }

    private void registerNewMessage(boolean register) {
        if (register) {
            if (mIImMessageObserve == null)
                mIImMessageObserve = new IIMMessageObserve() {
                    @Override
                    public void onNewIMMessageArrival(List<BZWIMMessage> list) {
                        LogUtil.d("onNewIMMessageArrival onNewIMMessageArrival");
                        if (list == null)
                            return;
                        int lastCount = mMessages.size();
                        BZWIMMessage bzwMessage;
                        boolean needRefresh = false;
                        for (int i = 0, size = list.size(); i < size; i++) {
                            bzwMessage = list.get(i);
                            if (isMyMessage(bzwMessage)) {
                                mMessages.add(bzwMessage);
                                needRefresh = true;
                            }
                        }
                        prepareFlagTimes(false);
                        if (needRefresh)
                            mChatView.callRefreshMessageView(-1, -1, lastCount, true);
                    }
                };
            mImManager.registerNewIMMessageObserve(mIImMessageObserve);
        } else if (mIImMessageObserve != null)
            mImManager.unRegisterNewIMMessageObserve(mIImMessageObserve);
    }

    private void prepareFlagTimes(boolean justNewest) {
        BZWIMMessage previous;
        BZWIMMessage current;
        if (justNewest) {
            if (mMessages.isEmpty())
                return;
            if (mMessages.size() == 1)
                mFlagTimeSet.add(mMessages.get(0).getUuid());
            else {
                previous = mMessages.get(mMessages.size() - 2);
                current = mMessages.get(mMessages.size() - 1);
                if (current.imMessage.getTime() - previous.imMessage.getTime() >= CommonConst.CHAT_FLAT_TIME_DISTANCE)
                    mFlagTimeSet.add(current.getUuid());
            }
            return;
        }
        mFlagTimeSet.clear();

        if (!mMessages.isEmpty()) {
            for (int i = 0, size = mMessages.size(); i < size; i++) {
                if (i == 0)
                    mFlagTimeSet.add(mMessages.get(i).getUuid());
                else {
                    previous = mMessages.get(i - 1);
                    current = mMessages.get(i);
                    if (current.imMessage.getTime() - previous.imMessage.getTime() >= CommonConst.CHAT_FLAT_TIME_DISTANCE)
                        mFlagTimeSet.add(current.getUuid());
                }
            }
        }
    }

    public Set<String> getFlagTimes() {
        return mFlagTimeSet;
    }

    private void registerMessageStatus(boolean register) {
        if (register) {
            if (mIImMessageStatusObserver == null)
                mIImMessageStatusObserver = new IIMMessageStatusObserver() {
                    @Override
                    public void onIMMessageStatusChanged(BZWIMMessage bzwimMessage) {
                        onMessageStatusChanged(bzwimMessage);
                    }
                };
            mImManager.registerIMMessageStatusObserves(mIImMessageStatusObserver, true);
        } else
            mImManager.registerIMMessageStatusObserves(mIImMessageStatusObserver, false);
    }

    private void onMessageStatusChanged(BZWIMMessage bzwimMessage) {
        if (isMyMessage(bzwimMessage)) {
            int targetPosition = CommonConst.LIST_POSITION_NONE;
            String msgUuid;
            for (int i = 0, size = mMessages.size(); i < size; i++) {
                msgUuid = mMessages.get(i).getUuid();
                if (msgUuid != null && msgUuid.equals(bzwimMessage.getUuid())) {
                    targetPosition = i;
                    break;
                }
            }
            if (targetPosition != CommonConst.LIST_POSITION_NONE) {
                BZWIMMessage bzw = mMessages.get(targetPosition);
                if (bzw != bzwimMessage)
                    bzw.resetStatusByNewest(bzwimMessage);
                mChatView.callRefreshMessageView(targetPosition, -1, 0, false);

            }
        }
    }

    private boolean isMyMessage(BZWIMMessage message) {
        return (message.getSessionType() == mChatParam.getSessionType() && message.getSessionId() != null && message.getSessionId().equals(mChatParam.getAccount()));
    }

    public void loadImMessage(boolean shouldDelay) {
        if (mMessages.size() >= mMaxShowMessageCount)
            return;
        if (mRunnableLoadMessage == null)
            mRunnableLoadMessage = new Runnable() {
                @Override
                public void run() {
                    mImManager.loadP2PImMessage((!mMessages.isEmpty() ? mMessages.get(0) : null), mMessageLoadPageCount, mChatParam.getAccount(), mChatParam.getSessionType(), new IIMMessageGetListener() {
                        @Override
                        public void onIMMessageGet(List<BZWIMMessage> messages) {
                            int lastCount = mMessages.size();
                            if (messages != null)
                                mMessages.addAll(0, messages);
                            prepareFlagTimes(false);
                            mChatView.callRefreshMessageView(-1, ((messages == null || messages.isEmpty()) ? -1 : (messages.size() - 1)), lastCount, false);
                        }
                    });
                }
            };
        mHandler.postDelayed(mRunnableLoadMessage, (shouldDelay ? 800 : 0));

    }

    private void registerOnLineStatusObserve(boolean register) {
        if (register) {
            if (mIImOnLineStatusObserve == null)
                mIImOnLineStatusObserve = new IIMOnLineStatusObserve() {
                    @Override
                    public void onImOnLineStatusChanged(int status) {
                        mChatView.callOnLineStatusChanged(status);
                    }
                };
            mImManager.registerOnlineStatusObserver(mIImOnLineStatusObserve, true);
        } else if (mIImOnLineStatusObserve != null)
            mImManager.registerOnlineStatusObserver(mIImOnLineStatusObserve, false);
    }

    public void sendTextMessage(String text) {
        if (TextUtils.isEmpty(text)) {
            mChatView.callShowToastMessage(null, R.string.input_chat_msg);
            return;
        }
        if (shouldIgnoreMsgForStranger() || mImManager.isTextMessageIllegal(text))
            return;
        mImManager.sendTextMessage(mChatParam.getAccount(), mChatParam.getSessionType(), text, new ISendMessageListener() {
            @Override
            public void onMessageSend(BZWIMMessage bzwimMessage) {
                int lastCount = mMessages.size();
                mMessages.add(bzwimMessage);
                prepareFlagTimes(true);
                mChatView.callRefreshMessageView(-1, -1, lastCount, true);
            }
        });
        handleJobAfterSendMessage();
    }

    private void sendAudioMessage(File audioFile, long audioLength) {
        if (shouldIgnoreMsgForStranger())
            return;
        mImManager.sendAudioMessage(mChatParam.getAccount(), mChatParam.getSessionType(), audioFile, audioLength, new ISendMessageListener() {
            @Override
            public void onMessageSend(BZWIMMessage bzwimMessage) {
                int lastCount = mMessages.size();
                mMessages.add(bzwimMessage);
                prepareFlagTimes(true);
                mChatView.callRefreshMessageView(-1, -1, lastCount, true);
            }
        });
        handleJobAfterSendMessage();
    }

//    private void sendCustomMessage(String content, ExtraMessageBean extraMessageBean) {
//        if (extraMessageBean == null)
//            return;
//        mImManager.sendCustomMessage(mChatParam.getAccount(), mChatParam.getSessionType(), content, extraMessageBean, new ISendMessageListener() {
//            @Override
//            public void onMessageSend(BZWIMMessage bzwimMessage) {
//                int lastCount = mMessages.size();
//                mMessages.add(bzwimMessage);
//                prepareFlagTimes(true);
//                mChatView.callRefreshMessageView(-1, -1, lastCount, true);
//            }
//        });
//    }

    public void sendImageMessage(String url) {
        if (shouldIgnoreMsgForStranger())
            return;
        if (!TextUtils.isEmpty(url)) {
            ImageManager.compressImageFileAsync(mChatView.callGetApplication(), url, 800, 800, 300, new ImageManager.ICompressImageFileListener() {
                @Override
                public void completeCompressTask(String compressedFilePath) {
                    mImManager.sendImageMessage(mChatParam.getAccount(), mChatParam.getSessionType(), new File(compressedFilePath), new ISendMessageListener() {
                        @Override
                        public void onMessageSend(BZWIMMessage bzwimMessage) {
                            int lastCount = mMessages.size();
                            mMessages.add(bzwimMessage);
                            prepareFlagTimes(true);
                            mChatView.callRefreshMessageView(-1, -1, lastCount, true);
                        }
                    });
                }
            });
        }
        handleJobAfterSendMessage();
    }

    public void sendFileMessage(File file) {
        if (file == null || !file.exists())
            return;
        if (file.length() > CommonConst.MAX_MESSAGE_FILE_LENGTH) {
            mChatView.callShowToastMessage(null, R.string.file_to_big);
            return;
        }
        if (shouldIgnoreMsgForStranger())
            return;
        mImManager.sendFileMessage(mChatParam.getAccount(), mChatParam.getSessionType(), file, new ISendMessageListener() {
            @Override
            public void onMessageSend(BZWIMMessage bzwimMessage) {
                int lastCount = mMessages.size();
                mMessages.add(bzwimMessage);
                prepareFlagTimes(true);
                mChatView.callRefreshMessageView(CommonConst.LIST_POSITION_NONE, CommonConst.LIST_POSITION_NONE, lastCount, true);
            }
        });
    }

    private boolean shouldIgnoreMsgForStranger() {
        return (mIsStrange && mImManager.isCurrentMessageOverTimeLimit());
    }

    private void handleJobAfterSendMessage() {
        if (mIsStrange)
            mImManager.updateImMessageCountOfStrangerOnce();
    }

    public void resendMessage(BZWIMMessage bzwimMessage) {
        if (bzwimMessage.imMessage == null)
            return;
        mMessages.remove(bzwimMessage);
        mMessages.add(bzwimMessage);
        bzwimMessage.setSendStatus();
        prepareFlagTimes(true);
        mChatView.callRefreshMessageView(CommonConst.LIST_POSITION_NONE, CommonConst.LIST_POSITION_NONE, mMessages.size(), true);
        mImManager.resendMessage(bzwimMessage);
    }


    private void registerUserInfoObserve(boolean register) {
        if (register) {
            if (mUserInfoObserve == null)
                mUserInfoObserve = new IUserInfoObserve() {
                    @Override
                    public void onUserInfoGet(UserInfoBean userInfoBean) {
                        if ((userInfoBean.neteaseId != null && userInfoBean.neteaseId.equals(UserInfoManager.getInstance().getUserInfo().neteaseId))) {
                            mChatView.callRefreshMessageView(-1, -1, 0, false);
                            return;
                        }
                        if ((mChatParam.getAccount() != null && mChatParam.getAccount().equals(userInfoBean.neteaseId))) {
                            mChatView.callSetTitle((!TextUtils.isEmpty(userInfoBean.nickName) ? userInfoBean.nickName : (!TextUtils.isEmpty(userInfoBean.trueName) ? userInfoBean.trueName : CommonConst.STR_DEFAULT_USER_NAME_SX)));
                            mChatView.callRefreshMessageView(-1, -1, 0, false);
                        }
                    }
                };

            IMUserInfoProvider.getInstance(mChatView.callGetBindActivity()).registerUserInfoObserve(mUserInfoObserve, true);
        } else if (mUserInfoObserve != null)
            IMUserInfoProvider.getInstance(mChatView.callGetBindActivity()).registerUserInfoObserve(mUserInfoObserve, false);
    }

    private void registerFriendObserve(boolean register) {
        if (register) {
            UIEventsObservable.getInstance().subscribeEvent(IFriendOperateSubscriber.class, this, new IFriendOperateSubscriber() {
                @Override
                public boolean onDeleteFriendDirectly(Object data) {
                    FriendInfoResultBean.FriendInfoBean mFriendInfoBean = (FriendInfoResultBean.FriendInfoBean) data;
                    if (mFriendInfoBean != null && mFriendInfoBean.unionId != null && mFriendInfoBean.unionId.equals(mUnionId)) {
                        mChatView.callUpdateIsFriendView(false);
                    }
                    return false;
                }
            });
        } else
            UIEventsObservable.getInstance().stopSubscribeEvent(IFriendOperateSubscriber.class, this);
    }

    public void startRecord() {
        mChatView.callFinishPlayVoice();
        RecorderTool.getInstance().startRecord(new RecorderTool.IBZWRecorderListener() {
            @Override
            public void onRecordReady() {

            }

            @Override
            public void onRecordStart(File audioFile) {

            }

            @Override
            public void onRecordSuccess(File audioFile, long audioLength) {
                if (audioFile != null && audioFile.exists()) {
                    LogUtil.d("audioFilePath = " + audioFile.getAbsolutePath());
                    sendAudioMessage(audioFile, audioLength);
                }
            }

            @Override
            public void onRecordFail() {
                mChatView.callShowToastMessage(null, R.string.audio_error_default);
            }

            @Override
            public void onRecordCancel() {
                mChatView.callShowToastMessage(null, R.string.audio_error_cancelled);
            }

            @Override
            public void onRecordReachedMaxTime(int maxTime) {
                mChatView.callShowToastMessage(null, R.string.audio_error_time_overflow);
            }
        });
    }

    /**
     * 结束录音(注意 startRecord 和finishRecord请配套使用 页面销毁时请调用releaseRecordResource)
     */
    public void finishRecord() {
        RecorderTool.getInstance().stopRecord(false);
    }

    public void cancelRecord() {
        RecorderTool.getInstance().stopRecord(true);
    }

    /**
     * 销毁录音控件(注意 startRecord 和finishRecord请配套使用 页面销毁时请调用releaseRecordResource)
     */
    private void releaseRecordResource() {
        RecorderTool.getInstance().destroyRecord();
        AudioPlayerTool.getInstance().stop();
    }

    /**
     * 播放录音
     */
    public void playVoice(File source, boolean changePlayMode) {
        AudioPlayerTool.getInstance().startPlay(source, changePlayMode, new AudioPlayerTool.IAudioPlayerListener() {
            @Override
            public void onPrepared() {

            }

            @Override
            public void onCompletion() {
                mChatView.callFinishPlayVoice();
            }

            @Override
            public void onInterrupt() {
//                LogUtil.d("playVoice onInterrupt");
//                mChatView.callFinishPlayVoice();
            }

            @Override
            public void onError(String error) {
                mChatView.callFinishPlayVoice();
            }

            @Override
            public void onDurationChanged(long curPosition) {

            }
        });
    }

    /**
     * 停止播放
     */
    public void stopPlayRecord() {
        LogUtil.d("stopPlayRecord");
        AudioPlayerTool.getInstance().stop();
    }

    private void checkIsMyFriend() {
        if (TextUtils.isEmpty(mChatParam.getAccount()))
            return;
        FriendDao.getFriendInfo(null, mChatParam.getAccount(), new IDefaultRequestReplyListener<FriendInfoResultBean.FriendInfoBean>() {
            @Override
            public void onRequestReply(boolean success, FriendInfoResultBean.FriendInfoBean friendInfoBean, int errorCode, String errorMsg) {
                if (success && friendInfoBean != null) {
                    mUnionId = friendInfoBean.unionId;
                    mIsStrange = (friendInfoBean.isFriend != FriendInfoResultBean.FriendInfoBean.FRIEND_YES);
                    mChatView.callUpdateIsFriendView((friendInfoBean.isFriend == FriendInfoResultBean.FriendInfoBean.FRIEND_YES));
                }
            }
        });
    }

    public void addFriend(String hello) {
        if (TextUtils.isEmpty(mChatParam.getAccount()) || TextUtils.isEmpty(mUnionId))
            return;
        mChatView.callShowProgress(null, true);
        FriendDao.addFriend(mUnionId, hello, new IDefaultRequestReplyListener<FriendAddResultBean>() {
            @Override
            public void onRequestReply(boolean success, FriendAddResultBean friendAddResultBean, int errorCode, String errorMsg) {
                mChatView.callCancelProgress();
                if (success) {
                    if (friendAddResultBean.data != null) {
                        mChatView.callUpdateIsFriendView(true);
                        UIEventsObservable.getInstance().postEvent(IFriendOperateSubscriber.class, ActionConst.ACTION_EVENT_ADD_FRIEND_DIRECTLY, friendAddResultBean.data, mChatParam.getAccount());
                    } else {
                        UIEventsObservable.getInstance().postEvent(IFriendOperateSubscriber.class, ActionConst.ACTION_EVENT_ADD_FRIEND_ASK, mUnionId, mChatParam.getAccount());
                        mChatView.callShowToastMessage(null, R.string.add_friend_request_send);
                    }
                    return;
                }
                mChatView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void watchFriendHome(BZWIMMessage bzwimMessage) {
        String unionId = mUnionId;
        if (bzwimMessage != null && !bzwimMessage.isReceivedMessage())
            unionId = UserInfoManager.getInstance().getUserInfo().unionId;
        if (unionId != null)
            FriendHomeActivity.launch(mChatView.callGetBindActivity(), unionId);

    }

    private void markPassiveShareProcessed(BZWIMMessage bzwimMessage) {
        mPassiveShareProcessedTagSet.add(bzwimMessage.getUuid());
        MessageDao.markPassiveShareHasProcessed(new MessageProcessLocalTagBean(UserInfoManager.getInstance().getUserInfo().unionId, mChatParam.getAccount(), bzwimMessage.getUuid()));
    }
}
