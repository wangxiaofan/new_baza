package com.baza.android.bzw.businesscontroller.message.adapter.chatHolder;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.businesscontroller.im.BZWIMMessage;
import com.baza.android.bzw.businesscontroller.im.IMConst;
import com.baza.android.bzw.businesscontroller.im.IMUserInfoProvider;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.slib.utils.DateUtil;
import com.slib.utils.LoadImageUtil;
import com.bznet.android.rcbox.R;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/6/2.
 * Title：
 * Note：
 */

public abstract class ChatViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public interface IChatExtraMsgProvider {
        Set<String> getFlagTimeSet();

        Set<String> getPassiveShareProcessedTagSet();

        BaseBZWAdapter.IAdapterEventsListener getAdapterEventsListener();

        boolean checkPassiveShareProcessed(String id);
    }

    @BindView(R.id.fl_avatar_left)
    View view_avatarLeft;
    @BindView(R.id.fl_avatar_right)
    View view_avatarRight;
    @BindView(R.id.tv_avatar_left)
    TextView textView_avatarLeft;
    @BindView(R.id.tv_avatar_right)
    TextView textView_avatarRight;
    @BindView(R.id.tv_chat_depart_time)
    TextView textView_chatDepartTime;
    @BindView(R.id.civ_avatar_left)
    ImageView imageView_avatarLeft;
    @BindView(R.id.civ_avatar_right)
    ImageView imageView_avatarRight;
    @BindView(R.id.message_item_progress)
    ProgressBar progressBar_send;
    @BindView(R.id.message_item_alert)
    ImageView imageView_sendAlert;
    @BindView(R.id.message_item_content)
    LinearLayout linearLayout_content;
    @BindView(R.id.ll_message_item)
    LinearLayout linearLayout_messageItem;
    @BindView(R.id.fl_center_content)
    LinearLayout linearLayout_mainItem;
    View viewContentView;
    private IMUserInfoProvider mUserInfoProvider;
    protected Resources mResources;
    protected Context mContext;
    protected IChatExtraMsgProvider mChatExtraMsgProvider;

    public ChatViewHolder(Context context, View convertView, IChatExtraMsgProvider mChatExtraMsgProvider) {
        this.mChatExtraMsgProvider = mChatExtraMsgProvider;
        this.mContext = context;
        this.mResources = context.getResources();
        this.mUserInfoProvider = IMUserInfoProvider.getInstance(context);
        ButterKnife.bind(this, convertView);
        view_avatarLeft.setOnClickListener(this);
        view_avatarRight.setOnClickListener(this);
        viewContentView = LayoutInflater.from(context).inflate(getItemTypeViewId(), null);
        linearLayout_content.addView(viewContentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView_sendAlert.setOnClickListener(this);
        init(viewContentView);
    }

    public abstract int getItemTypeViewId();

    public abstract void init(View viewContentView);

    public void refresh(BZWIMMessage bzwimMessage, int position) {
        Set<String> flagTimeSet = mChatExtraMsgProvider.getFlagTimeSet();
        if (flagTimeSet != null && flagTimeSet.contains(bzwimMessage.getUuid())) {
            textView_chatDepartTime.setText(DateUtil.longMillions2FormatDate(bzwimMessage.imMessage.getTime(), DateUtil.SDF_YMD_HM));
            textView_chatDepartTime.setVisibility(View.VISIBLE);
        } else
            textView_chatDepartTime.setVisibility(View.GONE);

        boolean isReceivedMessage = bzwimMessage.isReceivedMessage();
        int index = (isReceivedMessage ? 0 : 2);
        if (linearLayout_messageItem.getChildAt(index) != linearLayout_content) {
            linearLayout_messageItem.removeView(linearLayout_content);
            linearLayout_messageItem.addView(linearLayout_content, index);
        }
        UserInfoBean userInfoBean = mUserInfoProvider.getBZWUserInfo(bzwimMessage.getFromAccount());
        String name = null;
        String avatar = null;
        if (userInfoBean != null) {
            avatar = TextUtils.isEmpty(userInfoBean.avatar) ? null : userInfoBean.avatar;
            name = (TextUtils.isEmpty(userInfoBean.nickName) ? userInfoBean.trueName : userInfoBean.nickName);
        }
        if (TextUtils.isEmpty(name))
            name = bzwimMessage.getFromAccount();
        if (name == null)
            name = CommonConst.STR_DEFAULT_USER_NAME_SX;

        ViewGroup.LayoutParams lp = linearLayout_messageItem.getLayoutParams();
        if (shouldStrengthMessageWidth())
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        else
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        linearLayout_messageItem.setLayoutParams(lp);

        if (isReceivedMessage) {
            linearLayout_mainItem.setGravity(Gravity.LEFT);
            view_avatarRight.setVisibility(View.INVISIBLE);
            if (isUseDefaultBubble())
                linearLayout_content.setBackgroundResource(R.drawable.selector_background_chat_left);
            else if (getBubbleLeftDrawable() > 0)
                linearLayout_content.setBackgroundResource(getBubbleLeftDrawable());
            if (avatar != null) {
                LoadImageUtil.loadImage(avatar, R.drawable.avatar_def, imageView_avatarLeft);
                imageView_avatarLeft.setVisibility(View.VISIBLE);
                textView_avatarLeft.setVisibility(View.GONE);
            } else {
                imageView_avatarLeft.setVisibility(View.GONE);
                textView_avatarLeft.setVisibility(View.VISIBLE);
                textView_avatarLeft.setText(name.substring(0, 1));
            }
            view_avatarLeft.setVisibility(View.VISIBLE);
        } else {
            linearLayout_mainItem.setGravity(Gravity.RIGHT);
            view_avatarLeft.setVisibility(View.INVISIBLE);

            if (avatar != null) {
                LoadImageUtil.loadImage(avatar, R.drawable.avatar_def, imageView_avatarRight);
                imageView_avatarRight.setVisibility(View.VISIBLE);
                textView_avatarRight.setVisibility(View.GONE);
            } else {
                imageView_avatarRight.setVisibility(View.GONE);
                textView_avatarRight.setVisibility(View.VISIBLE);
                textView_avatarRight.setText(name.substring(0, 1));
            }
            view_avatarRight.setVisibility(View.VISIBLE);
            if (isUseDefaultBubble())
                linearLayout_content.setBackgroundResource(R.drawable.selector_background_chat_right);
            else if (getBubbleRightDrawable() > 0)
                linearLayout_content.setBackgroundResource(getBubbleRightDrawable());
        }
        setStatus(bzwimMessage.getStatus());
        imageView_sendAlert.setTag(bzwimMessage);
        view_avatarLeft.setTag(bzwimMessage);
        view_avatarRight.setTag(bzwimMessage);
        refreshView(bzwimMessage, position);
    }

    /**
     * 设置消息发送状态
     */
    private void setStatus(int status) {
        switch (status) {
            case IMConst.IM_MESSAGE_STATUS_FAILED:
                progressBar_send.setVisibility(View.GONE);
                imageView_sendAlert.setVisibility(View.VISIBLE);
                break;
            case IMConst.IM_MESSAGE_STATUS_SENDING:
                progressBar_send.setVisibility(View.VISIBLE);
                imageView_sendAlert.setVisibility(View.GONE);
                break;
            default:
                progressBar_send.setVisibility(View.GONE);
                imageView_sendAlert.setVisibility(View.GONE);
                break;
        }
    }

    public abstract void refreshView(BZWIMMessage bzwimMessage, int position);

    public boolean shouldStrengthMessageWidth() {
        return false;
    }

    public boolean isUseDefaultBubble() {
        return true;
    }

    public int getBubbleLeftDrawable() {
        return 0;
    }

    public int getBubbleRightDrawable() {
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_item_alert:
                mChatExtraMsgProvider.getAdapterEventsListener().onAdapterEventsArrival(AdapterEventIdConst.IM_MESSAGE_ADAPTER_EVENT_RESEND, 0, null, v.getTag());
                break;
            case R.id.fl_avatar_left:
            case R.id.fl_avatar_right:
                mChatExtraMsgProvider.getAdapterEventsListener().onAdapterEventsArrival(AdapterEventIdConst.ADAPTER_EVENT_FRIEND_HOME, 0, null, v.getTag());
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
