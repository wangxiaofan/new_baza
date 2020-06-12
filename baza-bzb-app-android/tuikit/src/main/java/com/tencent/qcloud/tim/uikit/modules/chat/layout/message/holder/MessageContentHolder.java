package com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.bean.ElementBean;
import com.tencent.qcloud.tim.uikit.component.gatherimage.UserIconView;
import com.tencent.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tim.uikit.config.TUIKitConfigs;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageTyping;
import com.tencent.qcloud.tim.uikit.utils.DateTimeUtil;
import com.tencent.qcloud.tim.uikit.utils.ShadowLayout;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 聊天界面
 */
public abstract class MessageContentHolder extends MessageEmptyHolder {

    private String TAG = "MessageContentHolder";
    public UserIconView leftUserIcon;
    public UserIconView rightUserIcon;
    public TextView usernameText;
    public LinearLayout msgContentLinear;
    public ProgressBar sendingProgress;
    public ImageView statusImage;
    public TextView isReadText;
    public TextView unreadAudioText;

    public ElementBean elementBean = null;
    public ShadowLayout system_sl;//建立推荐提醒
    public TextView system_title, system_content, system_time, system_content_min, system_content_status, system_job, system_button;
    public ImageView system_icon;
    public View system_line;

    public MessageContentHolder(View itemView) {
        super(itemView);
        //某一条聊天View
        rootView = itemView;

        leftUserIcon = itemView.findViewById(R.id.left_user_icon_view);
        rightUserIcon = itemView.findViewById(R.id.right_user_icon_view);
        usernameText = itemView.findViewById(R.id.user_name_tv);
        msgContentLinear = itemView.findViewById(R.id.msg_content_ll);
        statusImage = itemView.findViewById(R.id.message_status_iv);
        sendingProgress = itemView.findViewById(R.id.message_sending_pb);
        isReadText = itemView.findViewById(R.id.is_read_tv);
        unreadAudioText = itemView.findViewById(R.id.audio_unread);

        system_sl = itemView.findViewById(R.id.system_sl);
        system_title = itemView.findViewById(R.id.system_title);
        system_content = itemView.findViewById(R.id.system_content);
        system_time = itemView.findViewById(R.id.system_time);
        system_content_min = itemView.findViewById(R.id.system_content_min);
        system_content_status = itemView.findViewById(R.id.system_content_status);
        system_job = itemView.findViewById(R.id.system_job);
        system_button = itemView.findViewById(R.id.system_button);
        system_icon = itemView.findViewById(R.id.system_icon);
        system_line = itemView.findViewById(R.id.system_line);
    }

    public void layoutViews(final MessageInfo msg, final int position) {
        super.layoutViews(msg, position);

        elementBean = null;
        Log.e(TAG, "加载具体消息>>msg>>" + msg.toString());

        if (msg.getTIMMessage() != null && msg.getTIMMessage().getElementCount() > 0) {
            Log.e(TAG, "加载具体消息>>msg.getId()>>" + msg.getId());

            //系统消息
            if (msg.getFromUser().equals("baza_im_admin")) {
                Log.e(TAG, "系统消息>>");
                if (msg.getTIMMessage().getElement(0) != null &&
                        msg.getTIMMessage().getElement(0).getType() == TIMElemType.Custom
                    //&&((TIMCustomElem) msg.getTIMMessage().getElement(0)).getData() != null
                ) {

                    //自定义消息转化为对应子类
                    TIMCustomElem customElem = (TIMCustomElem) msg.getTIMMessage().getElement(0);

                    try {
                        elementBean = new Gson().fromJson(new String(customElem.getExt(), "UTF-8"), ElementBean.class);
                        Log.e(TAG, "msg>>elementBean>>" + elementBean.toString());

                        if (elementBean != null) {
                            system_sl.setVisibility(View.VISIBLE);
                            if (elementBean.getType().equals("newRecommend")) {
                                system_title.setText("简历推荐提醒");
                                system_content_status.setVisibility(View.GONE);
                                Glide.with(itemView.getContext()).load(URLConst.URL_ICON + elementBean.getData().getRecommenderUnionId()).circleCrop().into(system_icon);
                                //GlideEngine.loadCornerImage(system_icon, URLConst.URL_ICON + elementBean.getData().getRecommenderUnionId(), null, 90);
                                String name = elementBean.getData().getOperatorName();
                                if (name.length() > 4) {
                                    name = name.substring(0, 4) + "…";
                                }
                                system_content.setText(name + "给你推荐了一份简历");
                                system_time.setText(DateTimeUtil.getTimeFormatText(elementBean.getData().getFlowUpdateDate()));
                                system_content_min.setText("候选人：" + elementBean.getData().getCandidateName() + "[" + elementBean.getData().getTitle() + "]");
                                system_button.setText("查看详情");
                                system_job.setText("推荐职位：" + elementBean.getData().getJobName() + "[" + elementBean.getData().getCustomerName() + "]");

                            } else if (elementBean.getType().equals("recommendStatusUpdate")) {
                                system_title.setText("推荐状态变更提醒");
                                Glide.with(itemView.getContext()).load(URLConst.URL_ICON + elementBean.getData().getOperatorId()).circleCrop().into(system_icon);
                                //GlideEngine.loadCornerImage(system_icon, URLConst.URL_ICON + elementBean.getData().getRecommenderUnionId(), null, 90);
                                String name = elementBean.getData().getOperatorName();
                                if (name.length() > 4) {
                                    name = name.substring(0, 4) + "…";
                                }
                                system_content.setText(name + "反馈了你的推荐");
                                system_time.setText(DateTimeUtil.getTimeFormatText(elementBean.getData().getFlowUpdateDate()));
                                system_content_min.setText("候选人：" + elementBean.getData().getCandidateName() + "[" + elementBean.getData().getTitle() + "]");
                                if (elementBean.getData().getStatus() == 0) {
                                    system_content_status.setText("推荐状态：待反馈");
                                } else if (elementBean.getData().getStatus() == 1) {
                                    system_content_status.setText("推荐状态：已接受");
                                } else if (elementBean.getData().getStatus() == 2) {
                                    system_content_status.setText("推荐状态：拒绝");
                                } else if (elementBean.getData().getStatus() == 4 || elementBean.getData().getStatus() == 5 || elementBean.getData().getStatus() == 7) {
                                    system_content_status.setText("推荐状态：进入面试阶段");
                                } else if (elementBean.getData().getStatus() == 8) {
                                    system_content_status.setText("推荐状态：已Offer");
                                } else if (elementBean.getData().getStatus() == 16) {
                                    system_content_status.setText("推荐状态：已入职");
                                } else if (elementBean.getData().getStatus() == 32) {
                                    system_content_status.setText("推荐状态：已离职");
                                } else if (elementBean.getData().getStatus() == 1024) {
                                    system_content_status.setText("推荐状态：候选人已淘汰");
                                }
                                system_button.setText("查看详情");
                                system_job.setText("推荐职位：" + elementBean.getData().getJobName() + "[" + elementBean.getData().getCustomerName() + "]");

                            } else if (elementBean.getType().equals("newJob")) {
                                system_title.setText("新职位提醒");

                                system_icon.setVisibility(View.GONE);
                                system_content.setVisibility(View.GONE);
                                system_time.setVisibility(View.GONE);
                                system_content_min.setVisibility(View.GONE);
                                system_content_status.setVisibility(View.GONE);
                                system_button.setText("APP暂不支持查看此消息，请在网页查看");
                                system_button.setTextColor(system_job.getContext().getResources().getColor(R.color.app_not_support_text_color));
                                system_job.setText("昨日系统新增" + elementBean.getData().getNewJobNumber() + "个新职位");
                            } else if (elementBean.getType().equals("recommendNotProcess")) {
                                system_title.setText("未处理推荐提醒");

                                system_icon.setVisibility(View.GONE);
                                system_content.setVisibility(View.GONE);
                                system_time.setVisibility(View.GONE);
                                system_content_min.setVisibility(View.GONE);
                                system_content_status.setVisibility(View.GONE);
                                system_button.setText("立即处理");
                                system_job.setText("本月有" + elementBean.getData().getRecommendNotProcess() + "位推荐给你的候选人还未处理");
                            } else if (elementBean.getType().equals("jobShare")) {
                                //职位分享卡片消息
                                system_title.setText("职位分享卡片消息");

                                system_icon.setVisibility(View.GONE);
                                system_content.setVisibility(View.GONE);
                                system_time.setVisibility(View.GONE);
                                system_content_min.setVisibility(View.GONE);
                                system_content_status.setVisibility(View.GONE);
                                system_line.setVisibility(View.GONE);
                                system_button.setVisibility(View.GONE);
                                //system_job.setText("你被群主踢出群聊 " + elementBean.getData().getGroupName());
                            } else if (elementBean.getType().equals("candidateShare")) {
                                //候选人分享卡片消息
                                system_title.setText("候选人分享卡片消息");

                                system_icon.setVisibility(View.GONE);
                                system_content.setVisibility(View.GONE);
                                system_time.setVisibility(View.GONE);
                                system_content_min.setVisibility(View.GONE);
                                system_content_status.setVisibility(View.GONE);
                                system_line.setVisibility(View.GONE);
                                system_button.setVisibility(View.GONE);
                                //system_job.setText("你被群主踢出群聊 " + elementBean.getData().getGroupName());
                            } else if (elementBean.getType().equals("atMessage")) {
                                //@消息
                                system_title.setText("@消息");

                                system_icon.setVisibility(View.GONE);
                                system_content.setVisibility(View.GONE);
                                system_time.setVisibility(View.GONE);
                                system_content_min.setVisibility(View.GONE);
                                system_content_status.setVisibility(View.GONE);
                                system_line.setVisibility(View.GONE);
                                system_button.setVisibility(View.GONE);
                                //system_job.setText("你被群主踢出群聊 " + elementBean.getData().getGroupName());
                            } else if (elementBean.getType().equals("kickedGroup")) {
                                //踢出群聊消息
                                system_title.setText("群提醒消息");

                                system_icon.setVisibility(View.GONE);
                                system_content.setVisibility(View.GONE);
                                system_time.setVisibility(View.GONE);
                                system_content_min.setVisibility(View.GONE);
                                system_content_status.setVisibility(View.GONE);
                                system_line.setVisibility(View.GONE);
                                system_button.setVisibility(View.GONE);
                                system_job.setText("你被群主踢出群聊 " + elementBean.getData().getGroupName());
                            } else {
                                system_sl.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception e) {
                        system_sl.setVisibility(View.GONE);
                        Log.e(TAG, "职位Json转换异常");
                        e.printStackTrace();
                    }

                    system_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (elementBean.getType().equals("newRecommend")) {
                                //简历推荐提醒 跳转到 "首页 -> 流程管理 -> 我收到的 -> 推荐详情 FloatingDetailActivity"
                                String url = "scheme://baza/FloatingDetailActivity";//这个就是刚刚前面在AndroidManManifest中设置的，?后面是需要传去的参数，但是不要太长
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                Bundle bundle = new Bundle();
                                bundle.putString("recommendId", elementBean.getData().getRecommenderId());
                                bundle.putString("type", "1");
                                intent.putExtras(bundle);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                itemView.getContext().startActivity(intent);

                            } else if (elementBean.getType().equals("recommendStatusUpdate")) {
                                //推荐状态反馈 跳转到 "首页 -> 流程管理 -> 我收到的 -> 推荐详情 FloatingDetailActivity"
                                String url = "scheme://baza/FloatingDetailActivity";//这个就是刚刚前面在AndroidManManifest中设置的，?后面是需要传去的参数，但是不要太长
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                Bundle bundle = new Bundle();
                                bundle.putString("recommendId", elementBean.getData().getRecommendId());
                                bundle.putString("type", "1");
                                intent.putExtras(bundle);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                itemView.getContext().startActivity(intent);

                            } else if (elementBean.getType().equals("newJob")) {
                                //新职位提醒

                            } else if (elementBean.getType().equals("recommendNotProcess")) {
                                //未处理推荐提醒 跳转到 "首页 -> 流程管理 -> 我收到的 FloatingActivity"（筛选出 【最近一个月】 且 【未处理】状态的简历）
                                String url = "scheme://baza/FloatingActivity";
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                intent.putExtra("FROM", "1000");
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                itemView.getContext().startActivity(intent);
                            }
                        }
                    });

                    //展示系统消息
                    leftUserIcon.setVisibility(View.GONE);
                    rightUserIcon.setVisibility(View.GONE);
                    usernameText.setVisibility(View.GONE);
                    msgContentLinear.setVisibility(View.GONE);
                }
            } else {
                //非系统消息
                system_sl.setVisibility(View.GONE);

                Log.e(TAG, "msg.getTIMMessage().getElement(0)>>" + msg.getTIMMessage().getElement(0));
                Log.e(TAG, "msg.getTIMMessage().getElement(0).getType()>>" + msg.getTIMMessage().getElement(0).getType());

                if (msg.getTIMMessage().getElement(0) != null &&
                        msg.getTIMMessage().getElement(0).getType() == TIMElemType.Custom) {

                    //自定义消息转化为对应子类
                    TIMCustomElem customElem = (TIMCustomElem) msg.getTIMMessage().getElement(0);
                    Log.e(TAG, "customElem>>" + customElem);

                    try {
                        Log.e(TAG, "customElem.getExt()>>" + new String(customElem.getExt(), "UTF-8"));

                        String json = new String(customElem.getExt(), "UTF-8");
                        json = json.replace("[", "");
                        json = json.replace("]", "");
                        elementBean = new Gson().fromJson(json, ElementBean.class);
                        Log.e(TAG, "elementBean01>>" + elementBean);

                        if (elementBean != null && elementBean.getType() != null) {
                            Log.e(TAG, "elementBean02>>" + elementBean.toString());

                            if (elementBean.getType().equals("atMessage")) {
                                //@消息
                            } else if (elementBean.getType().equals("jobShare")) {
                                if (elementBean.getData() != null) {
                                    elementBean.getData().setMessage("暂不支持查看职位分享，请前往Web端查看");
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }

                TIMElem timElem = msg.getTIMMessage().getElement(0);
                TIMElemType type = timElem.getType();
                if (type == TIMElemType.GroupTips) {
                    //群组提醒
                    Log.e(TAG, "群组提醒>>");

                } else {
                    Log.e(TAG, "非群组提醒>>");
                }
                // 头像设置
                if (msg.isSelf()) {
                    leftUserIcon.setVisibility(View.GONE);
                    rightUserIcon.setVisibility(View.VISIBLE);
                } else {
                    leftUserIcon.setVisibility(View.VISIBLE);
                    rightUserIcon.setVisibility(View.GONE);
                }
                if (properties.getAvatar() != 0) {
                    leftUserIcon.setDefaultImageResId(properties.getAvatar());
                    rightUserIcon.setDefaultImageResId(properties.getAvatar());
                } else {
                    leftUserIcon.setDefaultImageResId(R.drawable.default_head);
                    rightUserIcon.setDefaultImageResId(R.drawable.default_head);
                }
                if (properties.getAvatarRadius() != 0) {
                    leftUserIcon.setRadius(properties.getAvatarRadius());
                    rightUserIcon.setRadius(properties.getAvatarRadius());
                } else {
                    leftUserIcon.setRadius(5);
                    rightUserIcon.setRadius(5);
                }

                //设置聊天头像角度
                leftUserIcon.setRadius(90);
                rightUserIcon.setRadius(90);

                if (properties.getAvatarSize() != null && properties.getAvatarSize().length == 2) {
                    ViewGroup.LayoutParams params = leftUserIcon.getLayoutParams();
                    params.width = properties.getAvatarSize()[0];
                    params.height = properties.getAvatarSize()[1];
                    leftUserIcon.setLayoutParams(params);

                    params = rightUserIcon.getLayoutParams();
                    params.width = properties.getAvatarSize()[0];
                    params.height = properties.getAvatarSize()[1];
                    rightUserIcon.setLayoutParams(params);
                }
                leftUserIcon.invokeInformation(msg);
                rightUserIcon.invokeInformation(msg);

                // 用户昵称设置
                if (msg.isSelf()) { // 默认不显示自己的昵称
                    if (properties.getRightNameVisibility() == 0) {
                        usernameText.setVisibility(View.GONE);
                    } else {
                        usernameText.setVisibility(properties.getRightNameVisibility());
                    }
                } else {
                    if (properties.getLeftNameVisibility() == 0) {
                        if (msg.isGroup()) { // 群聊默认显示对方的昵称
                            usernameText.setVisibility(View.VISIBLE);
                        } else { // 单聊默认不显示对方昵称
                            usernameText.setVisibility(View.GONE);
                        }
                    } else {
                        usernameText.setVisibility(properties.getLeftNameVisibility());
                    }
                }
                if (properties.getNameFontColor() != 0) {
                    usernameText.setTextColor(properties.getNameFontColor());
                }
                if (properties.getNameFontSize() != 0) {
                    usernameText.setTextSize(properties.getNameFontSize());
                }

                // 聊天界面设置头像和昵称
                TIMUserProfile profile = TIMFriendshipManager.getInstance().queryUserProfile(msg.getFromUser());
                if (profile == null) {
                    usernameText.setText(msg.getFromUser());
                } else {
                    if (TextUtils.isEmpty(msg.getGroupNameCard())) {
                        usernameText.setText(!TextUtils.isEmpty(profile.getNickName()) ? profile.getNickName() : msg.getFromUser());
                    } else {
                        usernameText.setText(msg.getGroupNameCard());
                    }
                    if (!TextUtils.isEmpty(profile.getFaceUrl()) && !msg.isSelf()) {
                        List<Object> urllist = new ArrayList<>();
                        urllist.add(profile.getFaceUrl());
                        leftUserIcon.setIconUrls(urllist);
                        urllist.clear();
                    }
                }
                TIMUserProfile selfInfo = TIMFriendshipManager.getInstance().queryUserProfile(TIMManager.getInstance().getLoginUser());
                if (selfInfo != null && msg.isSelf()) {
                    if (!TextUtils.isEmpty(selfInfo.getFaceUrl())) {
                        List<Object> urllist = new ArrayList<>();
                        urllist.add(selfInfo.getFaceUrl());
                        rightUserIcon.setIconUrls(urllist);
                        urllist.clear();
                    }
                }

                if (msg.isSelf()) {
                    if (msg.getStatus() == MessageInfo.MSG_STATUS_SEND_FAIL
                            || msg.getStatus() == MessageInfo.MSG_STATUS_SEND_SUCCESS
                            || msg.isPeerRead()) {
                        sendingProgress.setVisibility(View.GONE);
                    } else {
                        sendingProgress.setVisibility(View.VISIBLE);
                    }
                } else {
                    sendingProgress.setVisibility(View.GONE);
                }

                // 聊天气泡设置
                if (msg.isSelf()) {
                    if (properties.getRightBubble() != null && properties.getRightBubble().getConstantState() != null) {
                        msgContentFrame.setBackground(properties.getRightBubble().getConstantState().newDrawable());
                    } else {
                        msgContentFrame.setBackgroundResource(R.drawable.chat_bubble_myself);
                    }
                    //设置新的聊天气泡
                    msgContentFrame.setBackgroundResource(R.drawable.chat_bubble_myself_new);
                } else {
                    if (properties.getLeftBubble() != null && properties.getLeftBubble().getConstantState() != null) {
                        msgContentFrame.setBackground(properties.getLeftBubble().getConstantState().newDrawable());
                        msgContentFrame.setLayoutParams(msgContentFrame.getLayoutParams());
                    } else {
                        msgContentFrame.setBackgroundResource(R.drawable.chat_other_bg);
                    }
                    //设置对方聊天气泡
                    msgContentFrame.setBackgroundResource(R.drawable.chat_bubble_other_new);
                }

                // 聊天气泡的点击事件处理
                if (onItemClickListener != null) {
                    msgContentFrame.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            onItemClickListener.onMessageLongClick(v, position, msg);
                            return true;
                        }
                    });
                    leftUserIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onItemClickListener.onUserIconClick(view, position, msg);
                        }
                    });
                    rightUserIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onItemClickListener.onUserIconClick(view, position, msg);
                        }
                    });
                }

                // 发送状态的设置
                if (msg.getStatus() == MessageInfo.MSG_STATUS_SEND_FAIL) {
                    statusImage.setVisibility(View.VISIBLE);
                    msgContentFrame.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (onItemClickListener != null) {
                                onItemClickListener.onMessageLongClick(msgContentFrame, position, msg);
                            }
                        }
                    });
                } else {
                    msgContentFrame.setOnClickListener(null);
                    statusImage.setVisibility(View.GONE);
                }

                // 左右边的消息需要调整一下内容的位置
                if (msg.isSelf()) {
                    msgContentLinear.removeView(msgContentFrame);
                    msgContentLinear.addView(msgContentFrame);
                } else {
                    msgContentLinear.removeView(msgContentFrame);
                    msgContentLinear.addView(msgContentFrame, 0);
                }
                msgContentLinear.setVisibility(View.VISIBLE);

                // 对方已读标识的设置
                if (TUIKitConfigs.getConfigs().getGeneralConfig().isShowRead()) {
                    if (msg.isSelf()) {
                        if (msg.isGroup()) {
                            isReadText.setVisibility(View.GONE);
                        } else {
                            isReadText.setVisibility(View.VISIBLE);
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) isReadText.getLayoutParams();
                            params.gravity = Gravity.CENTER_VERTICAL;
                            isReadText.setLayoutParams(params);
                            if (msg.isPeerRead()) {
                                isReadText.setText(R.string.has_read);
                            } else {
                                isReadText.setText(R.string.unread);
                            }
                        }
                    } else {
                        isReadText.setVisibility(View.GONE);
                    }
                }

                // 音频已读
                unreadAudioText.setVisibility(View.GONE);

                //自己的头像
                List<Object> url = new ArrayList<>();
                url.add(URLConst.URL_ICON + Configs.unionId);
                rightUserIcon.setIconUrls(url);

                // 由子类设置指定消息类型的views
                layoutVariableViews(msg, position, elementBean);
            }
        }
    }

    public abstract void layoutVariableViews(final MessageInfo msg, final int position, final ElementBean elementBean);
}
