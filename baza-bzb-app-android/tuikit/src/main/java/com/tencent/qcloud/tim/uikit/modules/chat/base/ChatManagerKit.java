package com.tencent.qcloud.tim.uikit.modules.chat.base;

import android.text.TextUtils;
import android.util.Log;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMGroupSystemElem;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMMessageOfflinePushSettings;
import com.tencent.imsdk.TIMSNSSystemElem;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMMessageLocator;
import com.tencent.imsdk.ext.message.TIMMessageReceipt;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.bean.IMGetMessageRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetMessageResponsetBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetNameRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetNameResponseBean;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationManagerKit;
import com.tencent.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfoUtil;
import com.tencent.qcloud.tim.uikit.modules.message.MessageRevokedManager;
import com.tencent.qcloud.tim.uikit.utils.TUIKitLog;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.tencent.qcloud.tim.uikit.utils.retrofit.RetrofitHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ChatManagerKit implements TIMMessageListener, MessageRevokedManager.MessageRevokeHandler {

    private String TAG = "ChatManagerKit";
    protected static final int MSG_PAGE_COUNT = 20;
    protected static final int REVOKE_TIME_OUT = 6223;
    protected ChatProvider mCurrentProvider;
    protected TIMConversation mCurrentConversation;
    private ChatInfo currInfo;

    protected boolean mIsMore;
    private boolean mIsLoading;

    protected void init() {
        destroyChat();
        TIMManager.getInstance().addMessageListener(this);
        MessageRevokedManager.getInstance().addHandler(this);
    }

    public void destroyChat() {
        mCurrentConversation = null;
        mCurrentProvider = null;
    }

    public abstract ChatInfo getCurrentChatInfo();

    public void setCurrentChatInfo(ChatInfo info) {
        if (info == null) {
            return;
        }
        currInfo = info;
        mCurrentConversation = TIMManager.getInstance().getConversation(info.getType(), info.getId());

        Log.e(TAG, "info.mCurrentConversation()>>" + mCurrentConversation);

        mCurrentProvider = new ChatProvider();
        mIsMore = true;
        mIsLoading = false;
    }

    public void onReadReport(List<TIMMessageReceipt> receiptList) {
        TUIKitLog.i(TAG, "onReadReport:" + receiptList.size());
        if (!safetyCall()) {
            TUIKitLog.w(TAG, "onReadReport unSafetyCall");
            return;
        }
        if (receiptList.size() == 0) {
            return;
        }
        TIMMessageReceipt max = receiptList.get(0);
        for (TIMMessageReceipt msg : receiptList) {
            if (!TextUtils.equals(msg.getConversation().getPeer(), mCurrentConversation.getPeer())
                    || msg.getConversation().getType() == TIMConversationType.Group) {
                continue;
            }
            if (max.getTimestamp() < msg.getTimestamp()) {
                max = msg;
            }
        }
        mCurrentProvider.updateReadMessage(max);
    }

    @Override
    public boolean onNewMessages(List<TIMMessage> msgs) {
        TUIKitLog.i(TAG, "onNewMessages:" + msgs.size());
        if (null != msgs && msgs.size() > 0) {
            for (TIMMessage msg : msgs) {
                TIMConversation conversation = msg.getConversation();
                TIMConversationType type = conversation.getType();
                if (type == TIMConversationType.C2C) {
                    if (MessageInfoUtil.isTyping(msg)) {
                        notifyTyping();
                    } else {
                        onReceiveMessage(conversation, msg);
                    }
                } else if (type == TIMConversationType.Group) {
                    onReceiveMessage(conversation, msg);
                } else if (type == TIMConversationType.System) {
                    onReceiveSystemMessage(msg);
                }
            }
        }
        return false;
    }

    private void notifyTyping() {
        if (!safetyCall()) {
            TUIKitLog.w(TAG, "notifyTyping unSafetyCall");
            return;
        }
        mCurrentProvider.notifyTyping();
    }

    // GroupChatManager会重写该方法
    protected void onReceiveSystemMessage(TIMMessage msg) {
        TIMElem ele = msg.getElement(0);
        TIMElemType eleType = ele.getType();
        // 用户资料修改通知，不需要在聊天界面展示，可以通过 TIMUserConfig 中的 setFriendshipListener 处理
        if (eleType == TIMElemType.ProfileTips) {
            TUIKitLog.i(TAG, "onReceiveSystemMessage eleType is ProfileTips, ignore");
        } else if (eleType == TIMElemType.SNSTips) {
            TUIKitLog.i(TAG, "onReceiveSystemMessage eleType is SNSTips");
            TIMSNSSystemElem m = (TIMSNSSystemElem) ele;
            if (m.getRequestAddFriendUserList().size() > 0) {
                ToastUtil.toastLongMessage("好友申请通过");
            }
            if (m.getDelFriendAddPendencyList().size() > 0) {
                ToastUtil.toastLongMessage("好友申请被拒绝");
            }
        } else if (eleType == TIMElemType.GroupSystem) {
            TIMGroupSystemElem elem = (TIMGroupSystemElem) ele;
            //ToastUtil.toastLongMessage("系统通知：" + new String(elem.getUserData()));
        }
    }

    protected void onReceiveMessage(final TIMConversation conversation, final TIMMessage msg) {
        if (!safetyCall()) {
            TUIKitLog.w(TAG, "onReceiveMessage unSafetyCall");
            return;
        }
        if (conversation == null || conversation.getPeer() == null) {
            return;
        }
        addMessage(conversation, msg);
    }

    protected abstract boolean isGroup();

    protected void addMessage(TIMConversation conversation, TIMMessage msg) {
        //收到新消息，回调到此方法
        Log.e("herb", "ChatManagerKit -> 聊天的每条消息的加载addMessage");
        if (!safetyCall()) {
            TUIKitLog.w(TAG, "addMessage unSafetyCall");
            return;
        }
        final List<MessageInfo> list;
        if (msg != null) {
            list = MessageInfoUtil.TIMMessage2MessageInfo(msg, isGroup());
        } else {
            list = MessageInfoUtil.TIMMessage2MessageInfo(msg, isGroup());
        }

        Log.e("herb", "ChatManagerKit -> list >>" + list.size());
        if (list != null && list.size() != 0 && mCurrentConversation.getPeer().equals(conversation.getPeer())) {
            mCurrentProvider.addMessageInfoList(list);
            for (MessageInfo msgInfo : list) {
                msgInfo.setRead(true);
                Log.e("herb", "msgInfo>>>" + msgInfo.getElement().toString());
                addGroupMessage(msgInfo);
            }
            mCurrentConversation.setReadMessage(msg, new TIMCallBack() {
                @Override
                public void onError(int code, String desc) {
                    TUIKitLog.v(TAG, "addMessage() setReadMessage failed, code = " + code + ", desc = " + desc);
                }

                @Override
                public void onSuccess() {
                    TUIKitLog.v(TAG, "addMessage() setReadMessage success");
                }
            });
        }
    }

    protected void addGroupMessage(MessageInfo msgInfo) {
        // GroupChatManagerKit会重写该方法
    }

    public void deleteMessage(int position, MessageInfo messageInfo) {
        if (!safetyCall()) {
            TUIKitLog.w(TAG, "deleteMessage unSafetyCall");
            return;
        }
        if (messageInfo.remove()) {
            mCurrentProvider.remove(position);
        }
    }

    public void revokeMessage(final int position, final MessageInfo messageInfo) {
        if (!safetyCall()) {
            TUIKitLog.w(TAG, "revokeMessage unSafetyCall");
            return;
        }
        mCurrentConversation.revokeMessage(messageInfo.getTIMMessage(), new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                if (code == REVOKE_TIME_OUT) {
                    ToastUtil.toastLongMessage("消息发送已超过2分钟");
                } else {
                    ToastUtil.toastLongMessage("撤回失败:" + code + "=" + desc);
                }
            }

            @Override
            public void onSuccess() {
                if (!safetyCall()) {
                    TUIKitLog.w(TAG, "revokeMessage unSafetyCall");
                    return;
                }
                mCurrentProvider.updateMessageRevoked(messageInfo.getId());
                ConversationManagerKit.getInstance().loadConversation(null);
            }
        });
    }

    public void sendMessage(final MessageInfo message, boolean retry, final IUIKitCallBack callBack) {
        //发送消息
        Log.e("herb", "发送消息>>" + message.toString());
        if (!safetyCall()) {
            TUIKitLog.w(TAG, "sendMessage unSafetyCall");
            return;
        }
        if (message == null || message.getStatus() == MessageInfo.MSG_STATUS_SENDING) {
            return;
        }
        message.setSelf(true);
        message.setRead(true);
        assembleGroupMessage(message);
        //消息先展示，通过状态来确认发送是否成功
        if (message.getMsgType() < MessageInfo.MSG_TYPE_TIPS) {
            message.setStatus(MessageInfo.MSG_STATUS_SENDING);
            if (retry) {
                mCurrentProvider.resendMessageInfo(message);
            } else {
                mCurrentProvider.addMessageInfo(message);
            }
        }

        //离线推送测试代码
        TIMMessageOfflinePushSettings settings = new TIMMessageOfflinePushSettings();
        settings.setExt("test".getBytes());
        TIMMessageOfflinePushSettings.AndroidSettings androidSettings = settings.getAndroidSettings();
        // OPPO必须设置ChannelID才可以收到推送消息，这个channelID需要和控制台一直
        androidSettings.setOPPOChannelID("tuikit");
        message.getTIMMessage().setOfflinePushSettings(settings);

        mCurrentConversation.sendMessage(message.getTIMMessage(), new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(final int code, final String desc) {
                TUIKitLog.v(TAG, "sendMessage fail:" + code + "=" + desc);
                if (!safetyCall()) {
                    TUIKitLog.w(TAG, "sendMessage unSafetyCall");
                    return;
                }
                if (callBack != null) {
                    callBack.onError(TAG, code, desc);
                }
                message.setStatus(MessageInfo.MSG_STATUS_SEND_FAIL);
                mCurrentProvider.updateMessageInfo(message);

            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                TUIKitLog.v(TAG, "sendMessage onSuccess");
                if (!safetyCall()) {
                    TUIKitLog.w(TAG, "sendMessage unSafetyCall");
                    return;
                }
                if (callBack != null) {
                    callBack.onSuccess(mCurrentProvider);
                }
                message.setStatus(MessageInfo.MSG_STATUS_SEND_SUCCESS);
                message.setId(timMessage.getMsgId());
                mCurrentProvider.updateMessageInfo(message);
            }
        });
    }

    protected void assembleGroupMessage(MessageInfo message) {
        // GroupChatManager会重写该方法
    }

    public void loadLocalChatMessages(MessageInfo lastMessage, final IUIKitCallBack callBack) {
        if (!safetyCall()) {
            TUIKitLog.w(TAG, "loadLocalChatMessages unSafetyCall");
            return;
        }
        if (mIsLoading) {
            return;
        }
        mIsLoading = true;
        if (!mIsMore) {
            mCurrentProvider.addMessageInfo(null);
            callBack.onSuccess(null);
            mIsLoading = false;
            return;
        }

        TIMMessage lastTIMMsg = null;
        if (lastMessage == null) {
            mCurrentProvider.clear();
        } else {
            lastTIMMsg = lastMessage.getTIMMessage();
        }
        final int unread = (int) mCurrentConversation.getUnreadMessageNum();
        mCurrentConversation.getLocalMessage(MSG_PAGE_COUNT
                , lastTIMMsg, new TIMValueCallBack<List<TIMMessage>>() {
                    @Override
                    public void onError(int code, String desc) {
                        mIsLoading = false;
                        callBack.onError(TAG, code, desc);
                        TUIKitLog.e(TAG, "loadChatMessages() getMessage failed, code = " + code + ", desc = " + desc);
                    }

                    @Override
                    public void onSuccess(final List<TIMMessage> timMessages) {
                        mIsLoading = false;
                        if (!safetyCall()) {
                            TUIKitLog.w(TAG, "getLocalMessage unSafetyCall");
                            return;
                        }
                        if (unread > 0) {
                            mCurrentConversation.setReadMessage(null, new TIMCallBack() {
                                @Override
                                public void onError(int code, String desc) {
                                    TUIKitLog.e(TAG, "loadChatMessages() setReadMessage failed, code = " + code + ", desc = " + desc);
                                }

                                @Override
                                public void onSuccess() {
                                    TUIKitLog.d(TAG, "loadChatMessages() setReadMessage success");
                                }
                            });
                        }
                        if (timMessages.size() < MSG_PAGE_COUNT) {
                            mIsMore = false;
                        }
                        final ArrayList<TIMMessage> messages = new ArrayList<>(timMessages);
                        final Map<String, String> nameMap = new HashMap<>();
                        Collections.reverse(messages);

                        //更换名字
                        final List<IMGetNameRequestBean.DataListBean> dataList = new ArrayList<>();
                        for (int i = 0; i < messages.size(); i++) {
//                            for (int j = 0; j < messages.get(i).getElementCount(); j++) {
//                                if(messages.get(i).getElement(j).getType()== TIMElemType.GroupTips){
//
//                                }
//                            }
                            Log.e("herb", i + ">>timMessages>>" + messages.get(i).getSender());
                            dataList.add(new IMGetNameRequestBean.DataListBean(messages.get(i).getSender(), 0));
                        }
                        IMGetNameRequestBean bean = new IMGetNameRequestBean(dataList);
                        RetrofitHelper.getInstance().getServer().getIMName(URLConst.getIMName, bean).enqueue(new Callback<IMGetNameResponseBean>() {

                            @Override
                            public void onResponse(Call<IMGetNameResponseBean> call, Response<IMGetNameResponseBean> response) {
                                IMGetNameResponseBean resultEntity = response.body();
                                if (resultEntity != null && resultEntity.getData() != null) {
                                    for (int i = 0; i < messages.size(); i++) {
                                        for (int j = 0; j < resultEntity.getData().size(); j++) {
                                            Log.e("herb", "timMessages.get(i).getSender()>>" + messages.get(i).getSender());
                                            Log.e("herb", "resultEntity.getData().get(j).getAccountId()>>" + resultEntity.getData().get(j).getAccountId());
                                            if (messages.get(i).getSender().equals(resultEntity.getData().get(j).getAccountId())) {
                                                nameMap.put(messages.get(i).getSender(), resultEntity.getData().get(j).getAccountUserName());
                                            }
                                        }
                                    }
                                }

                                List<MessageInfo> msgInfos = MessageInfoUtil.TIMMessages2MessageInfos(messages, isGroup());
                                mCurrentProvider.addMessageList(msgInfos, true);
                                for (int i = 0; i < msgInfos.size(); i++) {
                                    MessageInfo info = msgInfos.get(i);
                                    if (info.getStatus() == MessageInfo.MSG_STATUS_SENDING) {
                                        sendMessage(info, true, null);
                                    }
                                }
                                callBack.onSuccess(mCurrentProvider);
                            }

                            @Override
                            public void onFailure(Call<IMGetNameResponseBean> call, Throwable t) {
                            }
                        });
                    }
                });
    }


    public int nextReqMessageId = Integer.MAX_VALUE;//群聊用的
    public int nextReqMessageTime = (int) (System.currentTimeMillis() / 1000);//单聊的
    public String currId;//当前聊天的id

    public void loadChatMessages(MessageInfo lastMessage, final IUIKitCallBack callBack) {
        if (getCurrentChatInfo().getId() != null && !getCurrentChatInfo().getId().equals("")) {
            currId = getCurrentChatInfo().getId();
        }

        if (!safetyCall()) {
            TUIKitLog.w(TAG, "loadChatMessages unSafetyCall");
            return;
        }
        //正在加载消息则返回
        if (mIsLoading) {
            return;
        }
        //没有加载消息进行加载
        mIsLoading = true;
        if (!mIsMore) {
            mCurrentProvider.addMessageInfo(null);
            callBack.onSuccess(null);
            mIsLoading = false;
            return;
        }

        TIMMessage lastTIMMsg = null;
        //获取当前聊天信息(用于获取id)
        if (currInfo != null) {
            Log.e(TAG, "info.getId()>>" + currInfo.getId());
            Log.e(TAG, "info.getType()>>" + currInfo.getType());
        }
        Log.e(TAG, "lastMessage>>" + lastMessage);

        if (lastMessage == null) {
            //第一次加载
            mCurrentProvider.clear();
        } else {
            //非第一次加载（lastTIMMsg用来记载下次的传参）
            lastTIMMsg = lastMessage.getTIMMessage();
        }

        //腾讯IM获取消息
        final int unread = (int) mCurrentConversation.getUnreadMessageNum();

        /**
         * count (integer, optional): 每页大小，默认为10 ,
         * flipOverType (integer, optional): 翻页方式（0-往前翻，1-往后翻，默认往前翻） ,
         * fromAccountId (string, optional): 发送者，获取C2C单聊消息时必传 ,
         * groupId (string, optional): 群组id，获取群聊消息时必传 ,
         * nextReqMessageId (integer, optional): 最近的消息序列号 ,
         * nextReqMessageTime (integer, optional): 最近的消息发送时间 ,
         * positionFlag (boolean, optional): 是否定位（定位前后N条，N即参数里面的count，最好不要超过20，默认为false） ,
         * toAccountId (string, optional): 接收者，获取C2C单聊消息时必传 ,
         * type (integer): 查询类型，0：C2C单聊消息，1：群聊消息
         */
        //批量加载服务端的消息
        /*IMGetMessageRequestBean getMessageRequestBean;
        if (mCurrentConversation.getType() == TIMConversationType.Group) {
            //群组
            getMessageRequestBean = new IMGetMessageRequestBean(
                    10,
                    0,
                    "",
                    currId,
                    nextReqMessageId,
                    0,
                    false,
                    "",
                    1);
        } else {
            String form, to;
            if (Configs.unionId.equals(currInfo.getId())) {
                //发给别人
                form = Configs.unionId;
                to = currInfo.getId();
            } else {
                //别人发给你
                form = currInfo.getId();
                to = Configs.unionId;
            }
            getMessageRequestBean = new IMGetMessageRequestBean(
                    10,
                    0,
                    form,
                    currInfo.getId(),
                    0,
                    nextReqMessageTime,
                    false,
                    to,
                    0);
        }
        Log.e("GET_MESSAGE", "开始获取消息>>" + getMessageRequestBean.toString());
        RetrofitHelper.getInstance().getServer().getIMMessages(URLConst.getHistoryMessage, getMessageRequestBean).enqueue(new Callback<IMGetMessageResponsetBean>() {

            @Override
            public void onResponse(Call<IMGetMessageResponsetBean> call, Response<IMGetMessageResponsetBean> response) {
                mIsLoading = false;
                Log.e("GET_MESSAGE", "获取消息成功>>" + response.body());
                IMGetMessageResponsetBean resultEntity = response.body();
                if (resultEntity == null || resultEntity.getData() == null) {
                    return;
                }

                nextReqMessageId = resultEntity.getData().getNextReqMessageId();
                nextReqMessageTime = resultEntity.getData().getNextReqMessageTime();

                Log.e("GET_MESSAGE", "获取消息转换成功>>" + resultEntity.toString());

                if (resultEntity.getData().getMessageList() == null) {
                    return;
                }
                //List<TIMMessage> timMessages = new ArrayList<>();
                List<MessageInfo> messageInfos = new ArrayList<>();
                for (int i = 0; i < resultEntity.getData().getMessageList().size(); i++) {
                    IMGetMessageResponsetBean.DataBeanX.MessageListBean messageListBean = resultEntity.getData().getMessageList().get(i);

                    if (messageListBean == null) {
                        return;
                    }
                    //组装内容
                    //TIMMessage timMessage = new TIMMessage();
                    MessageInfo messageInfo = new MessageInfo();
                    messageInfo.setId(messageListBean.getMsgContent().getUuid());
                    messageInfo.setGroup(isGroup());
                    messageInfo.setFromUser(messageListBean.getFromAccountId());
                    if (messageListBean.getType() != null && messageListBean.getType().equals("jobShare")) {
                        //职位分享
                        //Elem
                        TIMCustomElem timCustomElem = new TIMCustomElem();
                        timCustomElem.setExt(messageListBean.getMsgContent().getExt().getBytes());
                        messageInfo.setElement(timCustomElem);
                        //timMessage.addElement(timCustomElem);
                    } else if (messageListBean.getType() != null && messageListBean.getType().equals("candidateShare")) {
                        //简历分享
                    } else if (messageListBean.getType() != null && messageListBean.getType().equals("atMessage")) {
                        //@消息
                    } else if (messageListBean.getType() != null && messageListBean.getType().equals("kickedGroup")) {
                        //踢出群聊消息
                    } else {
                        if (messageListBean.getMsgType().equals("TIMTextElem")) {
                            //普通消息
                            TIMTextElem timTextElem = new TIMTextElem();
                            timTextElem.setText(messageListBean.getMsgContent().getText());
                            messageInfo.setElement(timTextElem);
                            //timMessage.addElement(timTextElem);
                        }

                    }
                    //消息信息
                    *//*timMessage.setSender(messageListBean.getFromAccountId());
                    timMessage.setTimestamp(messageListBean.getMsgTime());
                    timMessages.add(timMessage);*//*
                    messageInfos.add(messageInfo);

                    //对话信息
                    if (mCurrentConversation.getType() == TIMConversationType.C2C) {
                        ChatInfo chatInfo = new ChatInfo();
                        chatInfo.setType(mCurrentConversation.getType());
                        setCurrentChatInfo(chatInfo);
                    } else if (mCurrentConversation.getType() == TIMConversationType.Group) {
                        GroupInfo groupInfo = new GroupInfo();
                        groupInfo.setType(mCurrentConversation.getType());
                        setCurrentChatInfo(groupInfo);
                    }
                }

                if (!safetyCall()) {
                    TUIKitLog.w(TAG, "getMessage unSafetyCall");
                    return;
                }
                if (unread > 0) {
                    mCurrentConversation.setReadMessage(null, new TIMCallBack() {
                        @Override
                        public void onError(int code, String desc) {
                            TUIKitLog.e(TAG, "loadChatMessages() setReadMessage failed, code = " + code + ", desc = " + desc);
                        }

                        @Override
                        public void onSuccess() {
                            TUIKitLog.d(TAG, "loadChatMessages() setReadMessage success");
                        }
                    });
                }
                *//*if (timMessages.size() < MSG_PAGE_COUNT) {
                    mIsMore = false;
                }
                final ArrayList<TIMMessage> messages = new ArrayList<>(timMessages);
                //升降序反转
                Collections.reverse(messages);

                //TIMMessage转换为MessageInfo（此过程涉及复杂），尝试把服务端的数据直接转化为MessageInfo，而不是TIMMessage
                List<MessageInfo> msgInfos = MessageInfoUtil.TIMMessages2MessageInfos(messages, isGroup());*//*
                List<MessageInfo> msgInfos = new ArrayList<>();
                msgInfos.addAll(messageInfos);
                mCurrentProvider.addMessageList(msgInfos, true);

                for (int i = 0; i < msgInfos.size(); i++) {
                    MessageInfo info = msgInfos.get(i);
                    if (info.getStatus() == MessageInfo.MSG_STATUS_SENDING) {
                        sendMessage(info, true, null);
                    }
                }
                callBack.onSuccess(mCurrentProvider);

            }

            @Override
            public void onFailure(Call<IMGetMessageResponsetBean> call, Throwable t) {
                Log.e("GET_MESSAGE", "获取消息失败>>");
                mIsLoading = false;
                List<MessageInfo> msgInfos = new ArrayList<>();
                mCurrentProvider.addMessageList(msgInfos, true);
                callBack.onError(TAG, 1000, "获取服务器数据错误");
                Log.e("herb", "loadChatMessages() getMessage failed, code = " + 1000 + ", desc = " + "获取服务器数据错误");
            }
        });*/

        //旧版加载消息
        Log.e("herb", "mCurrentConversation.getMessage>>");
        mCurrentConversation.getMessage(MSG_PAGE_COUNT, lastTIMMsg, new TIMValueCallBack<List<TIMMessage>>() {
            @Override
            public void onError(int code, String desc) {
                mIsLoading = false;
                List<MessageInfo> msgInfos = new ArrayList<>();
                mCurrentProvider.addMessageList(msgInfos, true);
                callBack.onError(TAG, code, desc);
                Log.e("herb", "loadChatMessages() getMessage failed, code = " + code + ", desc = " + desc);
            }

            @Override
            public void onSuccess(List<TIMMessage> timMessages) {
                for (int i = 0; i < timMessages.size(); i++) {
                    Log.e("herb", i + ">>loadChatMessages>>" + timMessages.get(i).toString());
                }
                mIsLoading = false;
                if (!safetyCall()) {
                    TUIKitLog.w(TAG, "getMessage unSafetyCall");
                    return;
                }
                if (unread > 0) {
                    mCurrentConversation.setReadMessage(null, new TIMCallBack() {
                        @Override
                        public void onError(int code, String desc) {
                            TUIKitLog.e(TAG, "loadChatMessages() setReadMessage failed, code = " + code + ", desc = " + desc);
                        }

                        @Override
                        public void onSuccess() {
                            TUIKitLog.d(TAG, "loadChatMessages() setReadMessage success");
                        }
                    });
                }
                if (timMessages.size() < MSG_PAGE_COUNT) {
                    mIsMore = false;
                }
                final ArrayList<TIMMessage> messages = new ArrayList<>(timMessages);
                //处理下面的
                Collections.reverse(messages);

                //加载聊天消息，加载之前，把昵称给换了
                List<MessageInfo> msgInfos = MessageInfoUtil.TIMMessages2MessageInfos(messages, isGroup());
                mCurrentProvider.addMessageList(msgInfos, true);
                for (int i = 0; i < msgInfos.size(); i++) {
                    MessageInfo info = msgInfos.get(i);
                    if (info.getStatus() == MessageInfo.MSG_STATUS_SENDING) {
                        sendMessage(info, true, null);
                    }
                }
                callBack.onSuccess(mCurrentProvider);
            }
        });
    }

    @Override
    public void handleInvoke(TIMMessageLocator locator) {
        if (!safetyCall()) {
            TUIKitLog.w(TAG, "handleInvoke unSafetyCall");
            return;
        }
        if (locator.getConversationId().equals(getCurrentChatInfo().getId())) {
            TUIKitLog.i(TAG, "handleInvoke locator = " + locator);
            mCurrentProvider.updateMessageRevoked(locator);
        }
    }

    protected boolean safetyCall() {
        if (mCurrentConversation == null
                || mCurrentProvider == null
                || getCurrentChatInfo() == null
        ) {
            return false;
        }
        return true;
    }
}
