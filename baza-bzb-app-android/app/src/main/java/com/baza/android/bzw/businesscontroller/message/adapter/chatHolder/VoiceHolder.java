package com.baza.android.bzw.businesscontroller.message.adapter.chatHolder;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.businesscontroller.im.BZWIMMessage;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.IMManager;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;

import java.io.File;

/**
 * Created by Vincent.Lei on 2017/7/10.
 * Title：
 * Note：
 */

public class VoiceHolder extends ChatViewHolder {
    private TextView textView_time;
    private ImageView imageView_animate;
    private View view_unread;
    private FrameLayout frame_contentContainer;
    private int maxWidth, minWidth;
    private static String mPlayingAudioMessageId;
    private static ImageView view_animating;

    public VoiceHolder(Context context, View convertView, IChatExtraMsgProvider mChatExtraMsgProvider) {
        super(context, convertView, mChatExtraMsgProvider);
        maxWidth = ScreenUtil.screenWidth - ScreenUtil.dip2px(130);
        minWidth = ScreenUtil.dip2px(50);
    }

    @Override
    public int getItemTypeViewId() {
        return R.layout.chat_item_voice;
    }

    @Override
    public void init(View viewContentView) {
        frame_contentContainer = viewContentView.findViewById(R.id.contentContainer);
        textView_time = viewContentView.findViewById(R.id.tv_time);
        imageView_animate = viewContentView.findViewById(R.id.iv_animate_view);
        view_unread = viewContentView.findViewById(R.id.view_has_unread);

        frame_contentContainer.setOnClickListener(this);
        frame_contentContainer.setOnLongClickListener(this);
    }

    @Override
    public void refreshView(BZWIMMessage bzwimMessage, int position) {
        final AudioAttachment msgAttachment = (AudioAttachment) bzwimMessage.imMessage.getAttachment();
        int time = (int) (msgAttachment.getDuration() / 1000);
        if ((msgAttachment.getDuration() - time * 1000) >= 500)
            time++;
        ViewGroup.LayoutParams lp = frame_contentContainer.getLayoutParams();
        lp.width = minWidth + ((time * (maxWidth - minWidth)) / CommonConst.MAX_VOICE_TIME_SECONDS);
        frame_contentContainer.setLayoutParams(lp);

        FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) imageView_animate.getLayoutParams();
        FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) textView_time.getLayoutParams();

        int drawableRes = R.drawable.audio_animation_list_left;
        if (bzwimMessage.isReceivedMessage()) {
            params1.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            imageView_animate.setLayoutParams(params1);
            params2.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            textView_time.setLayoutParams(params2);
            frame_contentContainer.setBackgroundResource(R.drawable.selector_background_chat_left);
        } else {
            drawableRes = R.drawable.audio_animation_list_right;
            params1.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            imageView_animate.setLayoutParams(params1);
            params2.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            textView_time.setLayoutParams(params2);
            frame_contentContainer.setBackgroundResource(R.drawable.selector_background_chat_right);
        }
        textView_time.setText(time + "\"");
        boolean isCurrentPlaying = (mPlayingAudioMessageId != null && mPlayingAudioMessageId.equals(bzwimMessage.getUuid()));
        AnimationDrawable animationDrawable;
        if (imageView_animate.getBackground() != null && imageView_animate.getBackground() instanceof AnimationDrawable) {
            animationDrawable = (AnimationDrawable) imageView_animate.getBackground();
            animationDrawable.stop();
        }
        imageView_animate.setBackgroundResource(drawableRes);
        animationDrawable = (AnimationDrawable) imageView_animate.getBackground();
        if (isCurrentPlaying) {
            animationDrawable.start();
            mPlayingAudioMessageId = bzwimMessage.getUuid();
            view_animating = imageView_animate;
        } else
            animationDrawable.selectDrawable(2);
        AttachStatusEnum attachStatus = bzwimMessage.imMessage.getAttachStatus();
        MsgStatusEnum status = bzwimMessage.imMessage.getStatus();
        if (bzwimMessage.isReceivedMessage() && attachStatus == AttachStatusEnum.transferred && status != MsgStatusEnum.read) {
            view_unread.setVisibility(View.VISIBLE);
        } else {
            view_unread.setVisibility(View.GONE);
        }
        frame_contentContainer.setTag(bzwimMessage);
        frame_contentContainer.setTag(R.id.hold_tag_id_one, position);
    }

    @Override
    public boolean isUseDefaultBubble() {
        return false;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.contentContainer:
                BZWIMMessage bzwimMessage = (BZWIMMessage) v.getTag();
                playVoice(bzwimMessage, (int) v.getTag(R.id.hold_tag_id_one), false);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        sendAdapterEvent(AdapterEventIdConst.EVENT_ID_SHOW_EDIT_VOICE_REMARK_MENU_DIALOG, (int) v.getTag(R.id.hold_tag_id_one), v, v.getTag());
        return true;
    }

    private void playVoice(BZWIMMessage bzwimMessage, int position, boolean changePlayMode) {
        if (bzwimMessage.imMessage.getDirect() == MsgDirectionEnum.In && bzwimMessage.imMessage.getAttachStatus() != AttachStatusEnum.transferred) {
            return;
        }
        boolean isCurrentPlaying = (mPlayingAudioMessageId != null && mPlayingAudioMessageId.equals(bzwimMessage.getUuid()));
        resetAnimatingView(view_animating);
        if (isCurrentPlaying && !changePlayMode) {
            //当前正在播放
            sendAdapterEvent(AdapterEventIdConst.EVENT_ID_STOP_PLAY_RECORD, position, null, null);
            resetAnimatingView(true);
            return;
        }
        if (bzwimMessage.imMessage.getStatus() != MsgStatusEnum.read) {
            // 将未读标识去掉,更新数据库
            view_unread.setVisibility(View.GONE);
            bzwimMessage.setHasRead();
            IMManager.getInstance(BZWApplication.getApplication()).updateIMMessageStatus(bzwimMessage);

        }
        final AudioAttachment msgAttachment = (AudioAttachment) bzwimMessage.imMessage.getAttachment();
        if (msgAttachment == null || msgAttachment.getPath() == null) {
            sendAdapterEvent(AdapterEventIdConst.EVENT_ID_RECORD_IS_NOT_EXIST, position, null, null);
            return;
        }
        File fileSource = new File(msgAttachment.getPath());
        if (!fileSource.exists()) {
            sendAdapterEvent(AdapterEventIdConst.EVENT_ID_RECORD_IS_NOT_EXIST, position, null, null);
            return;
        }
        mPlayingAudioMessageId = bzwimMessage.getUuid();
        if (imageView_animate.getBackground() != null && imageView_animate.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) imageView_animate.getBackground();
            animationDrawable.start();
            view_animating = imageView_animate;
        }
        sendAdapterEvent((changePlayMode ? AdapterEventIdConst.EVENT_ID_PLAY_RECORD_WITH_NEW_MODE : AdapterEventIdConst.EVENT_ID_PLAY_RECORD), position, null, fileSource);
    }

    public static void resetAnimatingView(boolean destroyAnimating) {
        LogUtil.d("==destroyAnimating==");
        resetAnimatingView(view_animating);
        if (destroyAnimating) {
            view_animating = null;
            mPlayingAudioMessageId = null;
        }
    }

    private static void resetAnimatingView(View viewAnimating) {
        if (viewAnimating == null) return;
        if (viewAnimating.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) viewAnimating.getBackground();
            animationDrawable.stop();
            animationDrawable.selectDrawable(2);
        }

    }

    public static void animateStop() {
        if (view_animating == null) return;
        if (view_animating.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) view_animating.getBackground();
            if (animationDrawable.isRunning())
                animationDrawable.stop();
        }
        view_animating = null;
        mPlayingAudioMessageId = null;
    }

    private void sendAdapterEvent(int adapterEventId, int position, View v, Object input) {
        mChatExtraMsgProvider.getAdapterEventsListener().onAdapterEventsArrival(adapterEventId, position, v, input);
    }

    public void playVoiceByChangeMode(BZWIMMessage bzwimMessage, int position) {
        playVoice(bzwimMessage, position, true);
    }
}
