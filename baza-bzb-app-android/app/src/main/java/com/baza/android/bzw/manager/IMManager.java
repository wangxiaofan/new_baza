package com.baza.android.bzw.manager;

import android.content.Context;
import android.content.Intent;

import com.baza.android.bzw.businesscontroller.im.BZWIMMessage;
import com.baza.android.bzw.businesscontroller.im.IFriendMsgObserve;
import com.baza.android.bzw.businesscontroller.im.IIMMessageGetListener;
import com.baza.android.bzw.businesscontroller.im.IIMMessageObserve;
import com.baza.android.bzw.businesscontroller.im.IIMMessageStatusObserver;
import com.baza.android.bzw.businesscontroller.im.IIMOnLineStatusObserve;
import com.baza.android.bzw.businesscontroller.im.IImLoginListener;
import com.baza.android.bzw.businesscontroller.im.IMRecentContact;
import com.baza.android.bzw.businesscontroller.im.IRecentContactGetCallObserve;
import com.baza.android.bzw.businesscontroller.im.IRecentContactMessageObserve;
import com.baza.android.bzw.businesscontroller.im.ISendMessageListener;
import com.baza.android.bzw.businesscontroller.im.ISystemNoticeObserve;
import com.baza.android.bzw.businesscontroller.im.IUnreadCountObserve;
import com.baza.android.bzw.businesscontroller.im.Nim;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.widget.emotion.MoonUtil;
import com.netease.nimlib.sdk.Observer;

import java.io.File;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/6/1.
 * Title：
 * Note：
 */

public class IMManager {
    private Context mContext;
    private Nim mNim;
    private static IMManager mManager;

    private IMManager(Context context) {
        this.mContext = context;
        this.mNim = new Nim(mContext);
    }

    public static IMManager getInstance(Context context) {
        if (mManager == null) {
            synchronized (IMManager.class) {
                if (mManager == null)
                    mManager = new IMManager(context.getApplicationContext());
            }
        }
        return mManager;
    }

    public void init(String account, String token) {
        mNim.init(account, token);
    }

    public String getImRootPath() {
        return mNim.getImRootPath();
    }

    public void registerCustomAttachmentParser() {
        mNim.registerCustomAttachmentParser();
    }

    /**
     * 检测消息发送的非法单词
     */
    public boolean isTextMessageIllegal(String message) {
        return UserInfoManager.getInstance().isTextMessageIllegal(message);
    }

    public boolean isCurrentMessageOverTimeLimit() {
        return UserInfoManager.getInstance().isCurrentMessageOverTimeLimit();
    }

    public void updateImMessageCountOfStrangerOnce() {
        UserInfoManager.getInstance().updateImMessageCountOfStranger(1);
    }

    public CharSequence getCheckedTextMessage(String textOriginal) {
        if (UserInfoManager.getInstance().isTextMessageIllegal(textOriginal))
            return getDefaultShowTextForIllegal();
        return MoonUtil.makeStringEmotion(mContext, textOriginal);
    }

    public String getDefaultShowTextForIllegal() {
        return "* * * * * *";
    }

    public void login(String account, String token, IImLoginListener loginListener) {
        mNim.login(account, token, loginListener);
    }

    public void registerLoginSyncDataStatus() {
        mNim.registerNimOnlineStatusObserver(true);
        mNim.getNimLoginSyncDataObserve().registerLoginSyncDataStatus(true);
    }

//    public Nim getNim() {
//        return mNim;
//    }

    public void logout() {
        mNim.logout();
        LogUtil.d("im logout");
    }

    public void destroy() {
        mNim.getNimLoginSyncDataObserve().registerLoginSyncDataStatus(false);
        mNim.observeMessageChange(false);
        mNim.registerNimOnlineStatusObserver(false);
        mNim.clearRecentContact();
    }

    public boolean observeSyncDataCompletedEvent(Observer<Void> observer) {
        return mNim.getNimLoginSyncDataObserve().observeSyncDataCompletedEvent(observer);
    }

    public void unObserveSyncDataCompletedEvent(Observer<Void> observer) {
        mNim.getNimLoginSyncDataObserve().unObserveSyncDataCompletedEvent(observer);
    }

    public void getRecentContact(final IRecentContactGetCallObserve observer) {
        mNim.getRecentContact(observer);
    }

    public void initialize() {
        mNim.observeMessageChange(true);
    }

    public void registerRecentContactMessageObserve(IRecentContactMessageObserve observe) {
        mNim.registerRecentContactMessageObserve(observe);
    }

    public void unRegisterRecentContactMessageObserve(IRecentContactMessageObserve observe) {
        mNim.unRegisterRecentContactMessageObserve(observe);
    }

    public void registerUnreadCountMessageObserve(IUnreadCountObserve observe) {
        mNim.registerUnreadCountMessageObserve(observe);
    }

    public void unRegisterUnreadCountMessageObserve(IUnreadCountObserve observe) {
        mNim.unRegisterUnreadCountMessageObserve(observe);
    }

    public void loadP2PImMessage(BZWIMMessage bzwimMessage, int count, String account, int sessionType, IIMMessageGetListener listener) {
        mNim.loadP2PImMessage(bzwimMessage, count, account, sessionType, listener);
    }

    public void registerNewIMMessageObserve(IIMMessageObserve observe) {
        mNim.registerNewIMMessageObserve(observe);
    }

    public void unRegisterNewIMMessageObserve(IIMMessageObserve observe) {
        mNim.unRegisterNewIMMessageObserve(observe);
    }

    public void setChattingAccount(String account, int sessionType, boolean noNotify) {
        mNim.setChattingAccount(account, sessionType, noNotify);
    }

    public void sendTextMessage(String account, int sessionType, String textMessage, ISendMessageListener listener) {
        mNim.sendTextMessage(account, sessionType, textMessage, listener);
    }

    public void sendAudioMessage(String account, int sessionType, File audioFile, long duration, ISendMessageListener listener) {
        mNim.sendAudioMessage(account, sessionType, audioFile, duration, listener);
    }

//    public void sendCustomMessage(String account, int sessionType, String content, ExtraMessageBean extraMessageBean, ISendMessageListener listener) {
//        mNim.sendCustomMessage(account, sessionType, content, extraMessageBean, listener);
//    }

    public void updateIMMessageStatus(BZWIMMessage bzwimMessage) {
        mNim.updateIMMessageStatus(bzwimMessage);
    }

    public boolean isEnableToSendMessage() {
        return mNim.isEnableToSendMsg();
    }

    public void resendMessage(BZWIMMessage bzwimMessage) {
        mNim.resendMessage(bzwimMessage);
    }

    public void registerIMMessageStatusObserves(IIMMessageStatusObserver observer, boolean register) {
        mNim.registerIMMessageStatusObserves(observer, register);
    }

    public void registerOnlineStatusObserver(IIMOnLineStatusObserve onLineStatusObserve, boolean register) {
        mNim.registerOnlineStatusObserver(onLineStatusObserve, register);
    }

    public BZWIMMessage parseNotifyMessage(Intent intent) {
        return mNim.parseNotifyMessage(intent);
    }

    public static void sortRecentContacts(List<IMRecentContact> list) {
        Nim.sortRecentContacts(list);
    }

    public void registerFriendMsgObserve(IFriendMsgObserve listener, boolean register) {
        mNim.registerFriendMsgObserve(listener, register);
    }

    public void registerSystemNoticeObserve(ISystemNoticeObserve listener, boolean register) {
        mNim.registerSystemNoticeObserve(listener, register);
    }

    public void clearFriendMsgUnreadCount() {
        mNim.clearFriendMsgUnreadCount();
    }

    public void setFriendNotifyEnable(boolean friendNotify) {
        mNim.setFriendNotifyEnable(friendNotify);
    }

    public void deleteConversation(IMRecentContact imRecentContact) {
        mNim.deleteConversation(imRecentContact);
    }

    //    public void sendAgreeFriendNotify(String toAccount) {
//        mNim.sendAgreeFriendNotify(toAccount);
//    }
    public void sendImageMessage(String account, int sessionType, File imageFile, ISendMessageListener listener) {
        mNim.sendImageMessage(account, sessionType, imageFile, listener);
    }

    public void sendFileMessage(String account, int sessionType, File file, ISendMessageListener listener) {
        mNim.sendFileMessage(account, sessionType, file, listener);
    }

    public void downloadAttachment(BZWIMMessage bzwimMessage, boolean thumb) {
        mNim.downloadAttachment(bzwimMessage, thumb);
    }
}
