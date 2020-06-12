package com.tencent.qcloud.tim.uikit.modules.conversation.holder;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.bean.ElementBean;
import com.tencent.qcloud.tim.uikit.component.UnreadCountTextView;
import com.tencent.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationIconView;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfoUtil;
import com.tencent.qcloud.tim.uikit.utils.DateTimeUtil;
import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversationCommonHolder extends ConversationBaseHolder {

    private String TAG = "ConversationCommonHolder";
    //public ConversationIconView conversationIconView;
    public ImageView conversationIconView;
    protected RelativeLayout leftItemLayout;
    protected TextView titleText, conversation_title_label;
    protected TextView messageText;
    protected TextView timelineText;
    protected UnreadCountTextView unreadText;
    protected View conversation_unread_min;

    public ConversationCommonHolder(View itemView) {
        super(itemView);
        leftItemLayout = rootView.findViewById(R.id.item_left);
        conversationIconView = rootView.findViewById(R.id.conversation_icon);
        titleText = rootView.findViewById(R.id.conversation_title);
        conversation_title_label = rootView.findViewById(R.id.conversation_title_label);
        messageText = rootView.findViewById(R.id.conversation_last_msg);
        timelineText = rootView.findViewById(R.id.conversation_time);
        unreadText = rootView.findViewById(R.id.conversation_unread);
        conversation_unread_min = rootView.findViewById(R.id.conversation_unread_min);
    }

    public void layoutViews(ConversationInfo conversation, int position) {
        Log.e("herb", "conversation>>" + conversation.getId());

        //上条消息数据
        MessageInfo lastMsg = conversation.getLastMessage();
        if (lastMsg != null && lastMsg.getStatus() == MessageInfo.MSG_STATUS_REVOKE) {
            if (lastMsg.isSelf()) {
                lastMsg.setExtra("您撤回了一条消息");
            } else if (lastMsg.isGroup()) {
                String me = TextUtils.isEmpty(lastMsg.getGroupNameCard())
                        ? lastMsg.getFromUser()
                        : lastMsg.getGroupNameCard();
                String message = TUIKitConstants.covert2HTMLString(me);
                lastMsg.setExtra(Configs.nameIdsMap.get(me) + "撤回了一条消息");
            } else {
                lastMsg.setExtra("对方撤回了一条消息");
            }
        }

        //消息是否置顶
        if (conversation.isTop()) {
            leftItemLayout.setBackgroundColor(rootView.getResources().getColor(R.color.conversation_top_color));
        } else {
            leftItemLayout.setBackgroundColor(Color.WHITE);
        }

        //标题
        titleText.setText(conversation.getTitle());
        timelineText.setText("");

        if (lastMsg != null && lastMsg.getExtra() != null) {
            Log.e(TAG, "初始消息>>" + TUIKitConstants.covert2StringHTML(lastMsg.getExtra().toString()));
            messageText.setText(TUIKitConstants.covert2StringHTML(lastMsg.getExtra().toString()));
        }

        if (lastMsg != null && lastMsg.getElement() != null) {//lastMsg.getExtra() != null
            Log.e(TAG, "消息类型>>" + lastMsg.getElement().getType());
            if (lastMsg.getElement().getType() == TIMElemType.Custom) {
                if (!messageText.getText().equals("[自定义消息]")) {
                    messageText.setText("[" + messageText.getText() + "]");
                }
                TIMCustomElem customElem = (TIMCustomElem) lastMsg.getElement();
                if (customElem != null && customElem.getData() != null) {
                    try {
                        String json = new String(customElem.getExt(), "UTF-8");
                        json = json.replace("[", "");
                        json = json.replace("]", "");
                        ElementBean elementBean = new Gson().fromJson(json, ElementBean.class);
                        if (elementBean != null) {
                            Log.e(TAG, "elementBean>>" + elementBean.toString());
                            if (elementBean.getType().equals("kickedGroup")) {
                                messageText.setText("[群提示消息]");
                            } else if (elementBean.getType().equals("newRecommend")) {
                                messageText.setText("[简历推荐提醒]");
                            } else if (elementBean.getType().equals("recommendStatusUpdate")) {
                                messageText.setText("[推荐状态变更提醒]");
                            } else if (elementBean.getType().equals("newJob")) {
                                messageText.setText("[新职位提醒]");
                            } else if (elementBean.getType().equals("recommendNotProcess")) {
                                messageText.setText("[本月未处理推荐提醒]");
                            } else if (elementBean.getType().equals("jobShare")) {
                                //职位分享
                                messageText.setText("职位分享:" + "[" + elementBean.getData().getJobName() + "-" + elementBean.getData().getFirmShortName() + "]");
                            } else if (elementBean.getType().equals("candidateShare")) {
                                //候选人分享
                                messageText.setText("候选人分享:" + "[" + elementBean.getData().getCandidateName() + "-" + elementBean.getData().getTitle() + "]");
                            } else if (elementBean.getType().equals("atMessage")) {
                                //@消息
                                Log.e("herb", "我的名字>>" + Configs.nameIdsMap.get(Configs.unionId));
                                if (elementBean.getData().getMessage().contains(Configs.nameIdsMap.get(Configs.unionId))) {
                                    //有人@自己
                                    Log.e("herb", "elementBean.getData().getMessage()>>" + elementBean.getData().getMessage());
                                    String str = TUIKitConstants.covert2HTMLStringRed("[有人@我]");
                                    messageText.setText(Html.fromHtml(elementBean.getData().getMessage().replace("@" + Configs.nameIdsMap.get(Configs.unionId), str)));
                                } else {
                                    //其余@消息
                                    messageText.setText(elementBean.getData().getMessage());
                                }
                            } else {
                                //其他自定义消息
                                messageText.setText("[自定义消息]");
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            } else if (lastMsg.getElement().getType() == TIMElemType.GroupTips) {
                //群消息提示
                //lastMsg.getExtra()
                //<font color="#5B6B92">aa1b026a-3b25-4849-97dd-19bdab15ce9e</font>"加入群组
                Log.e(TAG, "群消息提醒>>getFromUser>>" + lastMsg.getFromUser());
                if (!messageText.getText().equals("[自定义消息]")) {
                    messageText.setText("[" + messageText.getText() + "]");
                }
            } else {
                //不是自定义消息，直接展示
                if (conversation.isGroup()) {
                    messageText.setText(lastMsg.getFromUser() + "：" + messageText.getText().toString());
                }
            }

            messageText.setTextColor(rootView.getResources().getColor(R.color.list_bottom_text_bg));
            //如果是群组，显示用户名：+内容
            //Log.e("herb", "群聊最后一条>>" + lastMsg.getExtra().toString());
            //Log.e("herb", "群聊最后一条01>>" + conversation.isGroup());
            //Log.e("herb", "群聊最后一条02>>" + lastMsg.getElement().getType());

            //((TIMCustomElem) lastMsg.getElement()).getData().equals(MessageInfoUtil.GROUP_CREATE)
            /*if (conversation.isGroup()) {
                if (lastMsg.getElement().getType() == TIMElemType.Custom) {
                    //群聊并且自定义
                    String result = messageText.getText().toString();
                    if (result.equals("[自定义消息]")) {
                        messageText.setText(result);
                    } else {
                        result = result.replace("\"", "");
                        if (result.equals("[群提示消息]") || result.equals("[简历推荐提醒]") || result.equals("[推荐状态变更提醒]") || result.equals("[新职位提醒]") || result.equals("[本月未处理推荐提醒]")) {
                            Log.e(TAG, "lastMsg>>" + lastMsg.getExtra().toString());
                            messageText.setText(Html.fromHtml(lastMsg.getExtra().toString()));
                        } else {
                            messageText.setText("[" + result + "]");
                        }
                    }
                } else {
                    //群聊不是自定义
                    messageText.setText(lastMsg.getFromUser() + "：" + messageText.getText().toString());
                }
            } else {
                //单聊
                messageText.setText(messageText.getText().toString());
            }*/

            timelineText.setText(DateTimeUtil.getTimeFormatText(new Date(lastMsg.getMsgTime() * 1000)));
        }

        //未读消息
        if (conversation.getUnRead() > 0) {
            unreadText.setVisibility(View.VISIBLE);
            if (conversation.getUnRead() > 99) {
                unreadText.setText("···");
            } else {
                unreadText.setText("" + conversation.getUnRead());
            }
        } else {
            unreadText.setVisibility(View.GONE);
        }

        //是否在线展示
        if (!conversation.isGroup() && conversation.getState() != null && conversation.getState().equals("Online")) {
            conversation_unread_min.setVisibility(View.VISIBLE);
        } else {
            conversation_unread_min.setVisibility(View.GONE);
        }

        //conversationIconView.setRadius(mAdapter.getItemAvatarRadius());
        //conversationIconView.setRadius(90);
        if (mAdapter.getItemDateTextSize() != 0) {
            timelineText.setTextSize(mAdapter.getItemDateTextSize());
        }
        if (mAdapter.getItemBottomTextSize() != 0) {
            messageText.setTextSize(mAdapter.getItemBottomTextSize());
        }
        if (mAdapter.getItemTopTextSize() != 0) {
            titleText.setTextSize(mAdapter.getItemTopTextSize());
        }
        if (!mAdapter.hasItemUnreadDot()) {
            unreadText.setVisibility(View.GONE);
        }

//        if (conversation.getIconUrlList() != null) {
//            conversationIconView.setConversation(conversation);
//        }
        //头像
        if (conversation.getId().equals("baza_im_admin")) {
            conversationIconView.setImageResource(R.drawable.icon_gongdanxiaoxi);
        } else {
            if (conversation.isGroup()) {
                Glide.with(conversationIconView.getContext()).load(URLConst.URL_ICON_GROUP + conversation.getId()).circleCrop().into(conversationIconView);
                //GlideEngine.loadCornerImage(conversationIconView, URLConst.URL_ICON_GROUP + conversation.getId(), null, 100);
            } else {
                Glide.with(conversationIconView.getContext()).load(URLConst.URL_ICON + conversation.getId()).circleCrop().into(conversationIconView);
                //GlideEngine.loadCornerImage(conversationIconView, URLConst.URL_ICON + conversation.getId(), null, 100);
            }
        }

        //是否是内部成员
        conversation_title_label.setVisibility(View.GONE);
        if (conversation.isGroup()) {
            if (conversation.isInner()) {
                if (conversation.isCompanyResult()) {
                    conversation_title_label.setText("全员");
                    conversation_title_label.setTextColor(itemView.getResources().getColor(R.color.chet_list_label_qy_color));
                    conversation_title_label.setBackgroundResource(R.drawable.chat_list_label_qy_bg);
                    conversation_title_label.setVisibility(View.VISIBLE);
                } else {
                    conversation_title_label.setText("内部");
                    conversation_title_label.setTextColor(itemView.getResources().getColor(R.color.chet_list_label_nb_color));
                    conversation_title_label.setBackgroundResource(R.drawable.chat_list_label_nb_bg);
                    conversation_title_label.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (conversation.isInner()) {
                conversation_title_label.setText("内部");
                conversation_title_label.setTextColor(itemView.getResources().getColor(R.color.chet_list_label_nb_color));
                conversation_title_label.setBackgroundResource(R.drawable.chat_list_label_nb_bg);
                conversation_title_label.setVisibility(View.VISIBLE);
            }
        }

        if (conversation.getId().equals("baza_im_admin")) {
            titleText.setText("系统通知");
        }

        // 由子类设置指定消息类型的views
        layoutVariableViews(conversation, position);
    }

    public void layoutVariableViews(ConversationInfo conversationInfo, int position) {

    }
}
