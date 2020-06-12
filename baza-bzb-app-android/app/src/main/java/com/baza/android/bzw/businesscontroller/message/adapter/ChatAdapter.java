package com.baza.android.bzw.businesscontroller.message.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.message.ExtraMessageBean;
import com.baza.android.bzw.businesscontroller.im.BZWIMMessage;
import com.baza.android.bzw.businesscontroller.message.adapter.chatHolder.ChatViewHolder;
import com.baza.android.bzw.businesscontroller.message.adapter.chatHolder.FileHolder;
import com.baza.android.bzw.businesscontroller.message.adapter.chatHolder.ImageHolder;
import com.baza.android.bzw.businesscontroller.message.adapter.chatHolder.TextHolder;
import com.baza.android.bzw.businesscontroller.message.adapter.chatHolder.VoiceHolder;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;

import java.util.List;
import java.util.Set;

/**
 * Created by Vincent.Lei on 2017/6/2.
 * Title：
 * Note：
 */

public class ChatAdapter extends BaseBZWAdapter implements ChatViewHolder.IChatExtraMsgProvider {
    private static final int TYPE_COUNT = 6;
    private static final int TYPE_TEXT = 0;
    private static final int TYPE_CUSTOMER_UNKNOWN = 1;
    private static final int TYPE_VOICE = 2;
    private static final int TYPE_FRIEND_AGREE_NOTIFY = 3;
    private static final int TYPE_IMAGE = 4;
    private static final int TYPE_FILE = 5;

    private Context mContext;
    private List<BZWIMMessage> mMessages;
    private Set<String> mFlagTimeSet;
    private Set<String> mPassiveShareProcessedTagSet;

    public ChatAdapter(Context mContext, List<BZWIMMessage> mMessages, Set<String> flagTimeSet, Set<String> passiveShareProcessedTagSet, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = mContext;
        this.mFlagTimeSet = flagTimeSet;
        this.mPassiveShareProcessedTagSet = passiveShareProcessedTagSet;
        this.mMessages = mMessages;
    }

    @Override
    public int getCount() {
        return (mMessages == null ? 0 : mMessages.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_chat, null);
            convertView.setTag(getViewHolder(convertView, position));
        }
        refreshTargetItemView(convertView, position);
        return convertView;
    }

    public void refreshTargetItemView(View convertView, int position) {
        ChatViewHolder chatViewHolder = (ChatViewHolder) convertView.getTag();
        chatViewHolder.refresh(mMessages.get(position), position);
    }

    private ChatViewHolder getViewHolder(View convertView, int position) {
        ChatViewHolder chatViewHolder = null;
        switch (getItemViewType(position)) {
            case TYPE_TEXT:
                chatViewHolder = new TextHolder(mContext, convertView, this);
                break;
            case TYPE_CUSTOMER_UNKNOWN:
                chatViewHolder = new TextHolder(mContext, convertView, this, TextHolder.TYPE_CUSTOMER_UN_KNOWN);
                break;
            case TYPE_VOICE:
                chatViewHolder = new VoiceHolder(mContext, convertView, this);
                break;
            case TYPE_FRIEND_AGREE_NOTIFY:
                chatViewHolder = new TextHolder(mContext, convertView, this, TextHolder.TYPE_FRIEND_TIPS);
                break;
            case TYPE_IMAGE:
                chatViewHolder = new ImageHolder(mContext, convertView, this);
                break;
            case TYPE_FILE:
                chatViewHolder = new FileHolder(mContext, convertView, this);
                break;
        }
        return chatViewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        BZWIMMessage bzwimMessage = mMessages.get(position);
        ExtraMessageBean extraMessageBean = bzwimMessage.getExtraMessage();
        if (extraMessageBean != null)
            return getCustomMsgType(extraMessageBean);
        if (bzwimMessage.imMessage.getMsgType() == MsgTypeEnum.audio)
            return TYPE_VOICE;
        if (bzwimMessage.imMessage.getMsgType() == MsgTypeEnum.image)
            return TYPE_IMAGE;
        if (bzwimMessage.imMessage.getMsgType() == MsgTypeEnum.file)
            return TYPE_FILE;
        return TYPE_TEXT;
    }

    private int getCustomMsgType(ExtraMessageBean extraMessageBean) {
        int type = TYPE_CUSTOMER_UNKNOWN;
        switch (extraMessageBean.type) {
            case ExtraMessageBean.TYPE_FRIEND_AGREE_NOTIFY:
                type = TYPE_FRIEND_AGREE_NOTIFY;
                break;
        }
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public void onDestroy() {
        VoiceHolder.animateStop();
    }

    public void finishPlayVoice() {
        VoiceHolder.resetAnimatingView(true);
    }

    public void playTargetVoice(ListView listView, BZWIMMessage bzwimMessage) {
        if (bzwimMessage == null || bzwimMessage.getUuid() == null)
            return;
        int targetPosition = -1;
        for (int i = 0, size = mMessages.size(); i < size; i++) {
            if (bzwimMessage.getUuid().equals(mMessages.get(i).getUuid())) {
                targetPosition = i;
                break;
            }
        }
        if (targetPosition != -1) {
            View view = AppUtil.getTargetVisibleView(targetPosition, listView);
            if (view == null || !(view.getTag() instanceof VoiceHolder))
                return;
            VoiceHolder holder = (VoiceHolder) view.getTag();
            holder.playVoiceByChangeMode(bzwimMessage, targetPosition);
        }
    }

    @Override
    public Set<String> getFlagTimeSet() {
        return mFlagTimeSet;
    }

    @Override
    public Set<String> getPassiveShareProcessedTagSet() {
        return mPassiveShareProcessedTagSet;
    }

    @Override
    public IAdapterEventsListener getAdapterEventsListener() {
        return mIAdapterEventsListener;
    }

    @Override
    public boolean checkPassiveShareProcessed(String id) {
        return mPassiveShareProcessedTagSet != null && mPassiveShareProcessedTagSet.contains(id);
    }
}
