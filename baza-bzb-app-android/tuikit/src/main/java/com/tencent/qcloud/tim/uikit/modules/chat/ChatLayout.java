package com.tencent.qcloud.tim.uikit.modules.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.tencent.imsdk.TIMConversationType;
import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.base.ITitleBarLayout;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.base.AbsChatLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatManagerKit;
import com.tencent.qcloud.tim.uikit.modules.group.apply.GroupApplyInfo;
import com.tencent.qcloud.tim.uikit.modules.group.apply.GroupApplyManagerActivity;
import com.tencent.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.tencent.qcloud.tim.uikit.modules.group.info.GroupInfoActivity;
import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import java.util.List;


public class ChatLayout extends AbsChatLayout implements GroupChatManagerKit.GroupNotifyHandler {

    private GroupInfo mGroupInfo;
    private GroupChatManagerKit mGroupChatManager;
    private C2CChatManagerKit mC2CChatManager;
    private boolean isGroup;
    private ChatInfo currChatInfo;

    public ChatLayout(Context context) {
        super(context);
    }

    public ChatLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setChatInfo(final ChatInfo chatInfo) {
        super.setChatInfo(chatInfo);
        //设置聊天界面
        if (chatInfo == null) {
            return;
        }
        currChatInfo = chatInfo;

        if (chatInfo.getType() == TIMConversationType.Group) {
            isGroup = true;
            mGroupChatManager = GroupChatManagerKit.getInstance();
            mGroupChatManager.setGroupHandler(this);
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setId(chatInfo.getId());

            mGroupChatManager.setCurrentChatInfo(groupInfo);
            mGroupInfo = groupInfo;
            //groupInfo.setChatName(chatInfo.getChatName() + "(" + "0" + ")");
            getTitleBar().setTitle(chatInfo.getChatName() + "(" + "0" + ")", TitleBarLayout.POSITION.MIDDLE);
            loadChatMessages(null);
            loadApplyList();
            getTitleBar().getRightIcon().setImageResource(R.drawable.icon_more_black);
            getTitleBar().setOnRightClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mGroupInfo != null) {
                        Intent intent = new Intent(getContext(), GroupInfoActivity.class);
                        intent.putExtra(TUIKitConstants.Group.GROUP_ID, mGroupInfo.getId());
                        intent.putExtra("IS_ALL", chatInfo.isAll());
                        getContext().startActivity(intent);
                    } else {
                        ToastUtil.toastLongMessage("请稍后再试试~");
                    }
                }
            });
            mGroupApplyLayout.setOnNoticeClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), GroupApplyManagerActivity.class);
                    intent.putExtra(TUIKitConstants.Group.GROUP_INFO, mGroupInfo);
                    getContext().startActivity(intent);
                }
            });
        } else if (chatInfo.getType() == TIMConversationType.C2C) {
            isGroup = false;

            //getTitleBar().getRightIcon().setImageResource(R.drawable.chat_c2c);
            getTitleBar().getRightIcon().setVisibility(GONE);
            mC2CChatManager = C2CChatManagerKit.getInstance();
            mC2CChatManager.setCurrentChatInfo(chatInfo);
            loadChatMessages(null);

        } else if (chatInfo.getType() == TIMConversationType.System) {
            isGroup = false;

            //chatInfo.getId().equals("baza_im_admin")
            //系统通知
            getTitleBar().getRightIcon().setVisibility(GONE);
            mC2CChatManager = C2CChatManagerKit.getInstance();
            Log.e("herb", "chatInfo>>getId>>" + chatInfo.getId());
            Log.e("herb", "chatInfo>>getChatName>>" + chatInfo.getChatName());
            Log.e("herb", "chatInfo>>getType>>" + chatInfo.getType());

            mC2CChatManager.setCurrentChatInfo(chatInfo);
            loadChatMessages(null);
        }

        //系统消息不展示输入框
        if (chatInfo.getId().equals("baza_im_admin")) {
            getInputLayout().setVisibility(GONE);
        }
    }

    @Override
    public ChatManagerKit getChatManager() {
        if (isGroup) {
            return mGroupChatManager;
        } else {
            return mC2CChatManager;
        }
    }

    private void loadApplyList() {
        mGroupChatManager.getProvider().loadGroupApplies(new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                List<GroupApplyInfo> applies = (List<GroupApplyInfo>) data;
                if (applies != null && applies.size() > 0) {
                    mGroupApplyLayout.getContent().setText(getContext().getString(R.string.group_apply_tips, applies.size()));
                    mGroupApplyLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.toastLongMessage("loadApplyList onError: " + errMsg);
            }
        });
    }

    @Override
    public void onGroupForceExit() {
        if (getContext() instanceof Activity) {
            ((Activity) getContext()).finish();
        }
    }

    @Override
    public void onGroupNameChanged(String newName, String newNameMin) {
        if (currChatInfo.getType() == TIMConversationType.Group) {
            getTitleBar().setTitle(newName + "(" + mGroupInfo.getMemberCount() + ")", TitleBarLayout.POSITION.MIDDLE);
        } else {
            getTitleBar().setTitle(newName, TitleBarLayout.POSITION.MIDDLE);
        }
        getTitleBar().getmCenterTitleMin().setVisibility(GONE);
        getTitleBar().setTitle(newNameMin, TitleBarLayout.POSITION.SECOND);
    }

    @Override
    public void onApplied(int size) {
        if (size == 0) {
            mGroupApplyLayout.setVisibility(View.GONE);
        } else {
            mGroupApplyLayout.getContent().setText(getContext().getString(R.string.group_apply_tips, size));
            mGroupApplyLayout.setVisibility(View.VISIBLE);
        }
    }
}
