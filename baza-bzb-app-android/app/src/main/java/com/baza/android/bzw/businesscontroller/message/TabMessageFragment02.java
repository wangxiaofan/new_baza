package com.baza.android.bzw.businesscontroller.message;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.businesscontroller.home.HomeActivity;
import com.baza.android.bzw.businesscontroller.message.viewinterface.ITabMessageView;
import com.baza.android.bzw.businesscontroller.resume.recommend.RecommendActivity;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonUtils;
import com.baza.android.bzw.dao.IMDao;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMGroupEventListener;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMMessageUpdateListener;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.conversation.ConversationManager;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.bean.IMSigBean;
import com.tencent.qcloud.tim.uikit.component.UnreadCountTextView;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.input.InputLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationListLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationManagerKit;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.tencent.qcloud.tim.uikit.utils.retrofit.WaitingView;

import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class TabMessageFragment02 extends BaseFragment implements View.OnClickListener, ITabMessageView {

    private String TAG = "TabMessageFragment02";
    private LinearLayout msg_search_lin;
    private UnreadCountTextView conversation_notification_time_unread, conversation_matters_time_unread;
    private RelativeLayout cl_msg_sys;//系统通知
    private Button message_add_ql;//群聊按钮
    private RelativeLayout message_add_ql_rel;//群聊按钮背景
    private ImageView message_add;//发起群聊
    private View bottom_view;//底部
    public ConversationLayout conversationLayout;
    private UnreadCountTextView message_unread;//未读计数

    public TabMessageFragment02(View bottom_view, UnreadCountTextView message_unread) {
        this.bottom_view = bottom_view;
        this.message_unread = message_unread;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_message02;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_tab_message);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initWhenOnCreatedViews(View mRootView) {
        mRootView.findViewById(R.id.tv_right_click).setOnClickListener(this);
        msg_search_lin = mRootView.findViewById(R.id.msg_search_lin);
        cl_msg_sys = mRootView.findViewById(R.id.cl_msg_sys);
        conversation_notification_time_unread = mRootView.findViewById(R.id.conversation_notification_time_unread);
        conversation_matters_time_unread = mRootView.findViewById(R.id.conversation_matters_time_unread);
        message_add_ql = mRootView.findViewById(R.id.message_add_ql);

        conversationLayout = mRootView.findViewById(R.id.conversation_layout);
        message_add_ql.setOnClickListener(this);
        message_add_ql_rel = mRootView.findViewById(R.id.message_add_ql_rel);
        message_add_ql_rel.setOnClickListener(this);
        bottom_view.setOnClickListener(this);

//        message_add_ql.setOnTouchListener((view, motionEvent) -> {
//            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                //通知父控件不要干扰,即屏蔽父控件的该事件以及该事件之后的一切action
//                message_add_ql.getParent().requestDisallowInterceptTouchEvent(true);
//            }
//            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
//                //通知父控件不要干扰,即屏蔽父控件的该事件以及该事件之后的一切action
//                message_add_ql.getParent().requestDisallowInterceptTouchEvent(true);
//            }
//            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                //发起群聊
//                message_add_ql.setVisibility(View.GONE);
//                startActivity(new Intent(getActivity(), IMAddGroupChatActivity.class));
//                message_add_ql.getParent().requestDisallowInterceptTouchEvent(false);
//            }
//            return false;
//        });

        msg_search_lin.setOnClickListener(this);
        cl_msg_sys.setOnClickListener(this);
        mRootView.findViewById(R.id.cl_msg_recommend).setOnClickListener(this);
        message_add = mRootView.findViewById(R.id.message_add);
        message_add.setOnClickListener(this);

        UserInfoBean userInfoBean = UserInfoManager.getInstance().getUserInfo();
        if (userInfoBean.isFirmUser == 1) {
            cl_msg_sys.setVisibility(View.GONE);
            msg_search_lin.setVisibility(View.VISIBLE);
            message_add.setVisibility(View.VISIBLE);
        } else {
            //非猎必得账号，隐藏系统通知和输入框
            cl_msg_sys.setVisibility(View.GONE);
            msg_search_lin.setVisibility(View.GONE);
            message_add.setVisibility(View.GONE);
        }

        /*getChatList();*/
        //初始化聊天会话列表面板
        conversationLayout.initDefault();
        conversationLayout.getConversationList().setOnItemClickListener(new ConversationListLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, ConversationInfo messageInfo) {
                onItemClickListener(messageInfo);
            }
        });

        //新消息刷新
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
            @Override
            public boolean onNewMessages(List<TIMMessage> msgs) {
                Log.e(TAG, "收到消息>>>onNewMessages>>" + msgs.toString());
                conversationLayout.updateItem();
                //判断应用是否在前台，如果不在则发送通知
                String message = "";
                Log.e(TAG, "内容>>" + msgs.get(msgs.size() - 1).getConversation().getLastMsg().getElement(0).toString());

                TIMElem ele = msgs.get(msgs.size() - 1).getConversation().getLastMsg().getElement(0);
                TIMElemType eleType = ele.getType();
                // 用户资料修改通知，不需要在聊天界面展示，可以通过 TIMUserConfig 中的 setFriendshipListener 处理
                if (eleType == TIMElemType.Text) {
                    //文本
                    TIMTextElem txtEle = (TIMTextElem) ele;
                    message = txtEle.getText();
                }

                if (msgs.get(msgs.size() - 1).getConversation().getType() == TIMConversationType.C2C) {
                    //不加群名
                    message = msgs.get(msgs.size() - 1).getConversation().getLastMsg().getSenderNickname() + ":" + message;
                    isBackground(message, msgs.get(msgs.size() - 1).getConversation().getType(),
                            msgs.get(msgs.size() - 1).getConversation().getPeer(),
                            msgs.get(msgs.size() - 1).getConversation().getLastMsg().getSenderNickname(), "", "", false);
                } else if (msgs.get(msgs.size() - 1).getConversation().getType() == TIMConversationType.Group) {
                    //加群名
                    message = msgs.get(msgs.size() - 1).getConversation().getLastMsg().getSenderNickname() + "(" + msgs.get(msgs.size() - 1).getConversation().getGroupName() + "):" + message;
                    isBackground(message, msgs.get(msgs.size() - 1).getConversation().getType(),
                            msgs.get(msgs.size() - 1).getConversation().getPeer(),
                            msgs.get(msgs.size() - 1).getConversation().getGroupName(), "", "", false);
                }

                CommonUtils.setNotificationBadge(Configs.mUnreadTotal, getActivity());
                return false;
            }
        });

        //消息更新
        TIMManager.getInstance().addMessageUpdateListener(new TIMMessageUpdateListener() {
            @Override
            public boolean onMessagesUpdate(List<TIMMessage> list) {
                Log.e("herb", "收到消息>>>onMessagesUpdate");
                conversationLayout.updateItem();
                return false;
            }
        });

        new TIMUserConfig().setGroupEventListener(new TIMGroupEventListener() {
            @Override
            public void onGroupTipsEvent(TIMGroupTipsElem elem) {
                Log.e("herb", "收到消息>>>onGroupTipsEvent>>" + elem.getGroupId());
                //conversationLayout.removeConversationInfo(elem.getGroupId());
                conversationLayout.updateItem();

//                ConversationManager.getInstance().getConversation(TIMConversationType.C2C, idList.get(i)).sendMessage
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("herb", "TabMessageFragment02>>onActivityResult>>跳转回传>>");
        if (requestCode == 1000) {
            Log.e("herb", "TabMessageFragment02>>updateItem");
            conversationLayout.updateItem();
            //获取未读消息数量
            ConversationManagerKit.getInstance().loadConversation(new IUIKitCallBack() {
                @Override
                public void onSuccess(Object data) {
                    if (Configs.mUnreadTotal == 0) {
                        message_unread.setVisibility(View.GONE);
                    } else {
                        message_unread.setVisibility(View.VISIBLE);
                        if (Configs.mUnreadTotal > 999) {
                            message_unread.setText("···");
                        } else {
                            message_unread.setText(String.valueOf(Configs.mUnreadTotal));
                        }
                    }
                    CommonUtils.setNotificationBadge(Configs.mUnreadTotal, getContext());
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }
            });
        }
    }

    //获取聊天列表数据
    /*public void getChatList() {
        //2.获取IM sig：https://api.stg.bazhua.me/api/imcenter/
        IMDao.getSig((boolean success, IMSigBean bean, int errorCode, String errorMsg) -> {
            //IM登录返回
            Log.e("herb", "IM登录bean>>" + bean.toString());
            //3.腾讯登录
            WaitingView.getInstance().showWaitingView(getContext());
            TUIKit.login(Configs.unionId, bean.getData(), new IUIKitCallBack() {
                @Override
                public void onSuccess(Object data) {
                    WaitingView.getInstance().closeWaitingView();
                    Log.e("herb", "IM登录成功>>");

                    //初始化聊天会话列表面板
                    conversationLayout.initDefault();
                    conversationLayout.getConversationList().setOnItemClickListener(new ConversationListLayout.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, ConversationInfo messageInfo) {
                            onItemClickListener(messageInfo);
                        }
                    });
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    WaitingView.getInstance().closeWaitingView();
                    Log.e("herb", "IM登录失败>>errCode>>" + errCode + ">>errMsg>>" + errMsg);
                }
            });
        });
    }*/

    /**
     * 跳转到聊天详情页面
     */
    public void startConversation(TIMConversationType type, String id, String title, String company, String post, boolean isAll) {

        //设置传递聊天界面的信息为全局，这样在群聊点击添加好友时可以得知聊天ID
        InputLayout.type = type;
        InputLayout.id = id;

        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(type);//个人对话还是群组对话
        chatInfo.setId(id);//对话ID
        chatInfo.setChatName(title);//对话标题
        chatInfo.setCompany(company);
        chatInfo.setPost(post);
        chatInfo.setAll(isAll);

        Intent intent = new Intent(getActivity(), IMChatActivity.class);
        intent.putExtra(Constants.CHAT_INFO, chatInfo);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivityForResult(intent, 1000);
    }

    @Override
    protected void initWhenOnActivityCreated() {
    }

    @Override
    protected void changedUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    protected void onFragmentDeadForApp() {
        super.onFragmentDeadForApp();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.msg_search_lin:
                //搜索框
                startActivity(new Intent(getActivity(), IMSearchActivity.class));
                break;
            case R.id.cl_msg_sys:
                //系统通知
                //SystemMessageActivity.launch(getActivity(), ISystemView.MSG_SYSTEM);

                startConversation(TIMConversationType.C2C, "baza_im_admin", "系统通知", "", "", false);
                break;
            case R.id.cl_msg_recommend:
                //事项提醒
                RecommendActivity.launch(getActivity());
                break;
            case R.id.message_add:
                //加号展示发起群聊
                if (message_add_ql.getVisibility() == View.GONE) {
                    message_add_ql.setVisibility(View.VISIBLE);
                    message_add_ql_rel.setVisibility(View.VISIBLE);
                    bottom_view.setVisibility(View.VISIBLE);
                } else {
                    message_add_ql.setVisibility(View.GONE);
                    message_add_ql_rel.setVisibility(View.GONE);
                    bottom_view.setVisibility(View.GONE);
                }
                break;
            case R.id.message_add_ql:
                //发起群聊
                message_add_ql.setVisibility(View.GONE);
                message_add_ql_rel.setVisibility(View.GONE);
                bottom_view.setVisibility(View.GONE);
                startActivity(new Intent(getActivity(), IMAddGroupChatActivity.class));
                break;

            case R.id.message_add_ql_rel:
            case R.id.bottom_view:
                //发起群聊
                message_add_ql.setVisibility(View.GONE);
                message_add_ql_rel.setVisibility(View.GONE);
                bottom_view.setVisibility(View.GONE);
                break;

        }
    }

    @Override
    public void callCancelInnerProgress() {

    }

    @Override
    public void callShowInnerProgress(int msg) {

    }

    @Override
    public void callRefreshTargetRecentView(int targetPosition) {

    }

    @Override
    public void callOnLineStatusChanged(int statusCode) {

    }

    @Override
    public void callUpdateFriendRequestView(int requestCount) {

    }

    @Override
    public void callUpdateNoticeUnreadCountView(int systemMsgCount, int processCount) {
        if (mIFragmentEventsListener != null)
            mIFragmentEventsListener.onFragmentEventsArrival(AdapterEventIdConst.EVENT_MESSAGE_COUNT, systemMsgCount + processCount);
        if (systemMsgCount > 0) {
            conversation_notification_time_unread.setText(systemMsgCount > 99 ? "99+" : String.valueOf((systemMsgCount)));
            conversation_notification_time_unread.setVisibility(View.VISIBLE);
        } else
            conversation_notification_time_unread.setVisibility(View.GONE);
    }

    @Override
    public void callUpdateRecommendCountView(int unCompleteCount) {
        if (unCompleteCount > 0) {
            conversation_matters_time_unread.setText(unCompleteCount > 99 ? "99+" : String.valueOf((unCompleteCount)));
            conversation_matters_time_unread.setVisibility(View.VISIBLE);
        } else
            conversation_matters_time_unread.setVisibility(View.GONE);
    }

    //点击列表
    private void onItemClickListener(ConversationInfo messageInfo) {
        if (messageInfo.getId().equals("baza_im_admin")) {
            //系统通知
            startConversation(TIMConversationType.C2C, "baza_im_admin", "系统通知", "", "", false);
        } else {
            if (messageInfo.isGroup()) {
                if (messageInfo.isInner() && messageInfo.isCompanyResult()) {
                    startConversation(TIMConversationType.Group, messageInfo.getId(), messageInfo.getTitle(), "", "", true);
                } else {
                    startConversation(TIMConversationType.Group, messageInfo.getId(), messageInfo.getTitle(), "", "", false);
                }
            } else {
                if (messageInfo.getNameBean() == null) {
                    startConversation(TIMConversationType.C2C, messageInfo.getId(), messageInfo.getTitle(), "", "", false);
                } else {
                    startConversation(TIMConversationType.C2C, messageInfo.getId(), messageInfo.getTitle(), messageInfo.getNameBean().getCompany(), messageInfo.getNameBean().getTitle(), false);
                }
            }
        }
    }

    public boolean isBackground(String content, TIMConversationType type, String id, String title, String company, String post, boolean isAll) {
        if (getActivity() == null) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        Log.e(TAG, "appProcesses.size()>>" + appProcesses.size());

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            Log.e(TAG, "appProcess.processName>>" + appProcess.processName);
            if (appProcess.processName.equals(getActivity().getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.e(TAG, "处于后台" + appProcess.processName);
                    //发送通知
                    sendSimpleNotification(content, type, id, title, company, post, isAll);
                    return true;
                } else {
                    Log.e(TAG, "处于前台" + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    public void sendSimpleNotification(String content, TIMConversationType type, String id, String title, String company, String post, boolean isAll) {
        Log.e(TAG, "开始发送通知>>");

        Intent intent = new Intent(getContext(), IMChatActivity.class);
        // 注意：PendingIntent.FLAG_UPDATE_CURRENT
        InputLayout.type = type;
        InputLayout.id = id;

        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(type);//个人对话还是群组对话
        chatInfo.setId(id);//对话ID
        chatInfo.setChatName(title);//对话标题
        chatInfo.setCompany(company);
        chatInfo.setPost(post);
        chatInfo.setAll(isAll);
        intent.putExtra(Constants.CHAT_INFO, chatInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e(TAG, "开始发送通知>>1");

            // 1. 创建一个通知(必须设置channelId)
            Context context = getContext().getApplicationContext();
            String channelId = "ChannelId"; // 通知渠道
            Notification notification = new Notification.Builder(context)
                    .setChannelId(channelId)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
            // 2. 获取系统的通知管理器(必须设置channelId)
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "通知的渠道名称",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            // 3. 发送通知(Notification与NotificationManager的channelId必须对应)
            notificationManager.notify(1000, notification);
        } else {
            Log.e(TAG, "开始发送通知>>2");
            // 创建通知(标题、内容、图标)
            Notification notification = new Notification.Builder(getContext())
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(content)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            // 创建通知管理器
            NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            // 发送通知
            manager.notify(1000, notification);

        }
    }
}
