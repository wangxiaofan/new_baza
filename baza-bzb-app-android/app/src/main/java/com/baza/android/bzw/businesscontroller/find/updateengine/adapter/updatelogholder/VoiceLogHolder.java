package com.baza.android.bzw.businesscontroller.find.updateengine.adapter.updatelogholder;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baza.android.bzw.bean.resumeelement.LocalRemarkAttachment;
import com.baza.android.bzw.bean.resumeelement.RemarkBean;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.log.LogUtil;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/8/29.
 * Title：
 * Note：
 */

public class VoiceLogHolder extends BaseLogHolder implements View.OnClickListener {
    @BindView(R.id.tv_log_title)
    TextView textView_logType;
    @BindView(R.id.tv_pick_up)
    TextView textView_packUp;
    @BindView(R.id.tv_voice_time)
    TextView textView_voiceTime;
    @BindView(R.id.tv_remark_content)
    TextView textView_voiceContent;
    @BindView(R.id.iv_voice_animate)
    ImageView imageView_animate;
    @BindView(R.id.iv_voice_bg)
    ImageView imageView_bg;
    @BindView(R.id.rl_voice_layout)
    RelativeLayout frameLayout_voice;
    private int mVoiceViewExtraLength;
    private int mMinVoiceViewLength;
    private int mMaxVoiceViewLength;

    private static RemarkBean mRemarkBeanOnPlaying;
    private static ImageView imageView_animatingView;

    public VoiceLogHolder(Context context, View convertView, ILogAdapterListener listener) {
        super(context, convertView, listener);
        this.mMaxVoiceViewLength = ScreenUtil.screenWidth - ScreenUtil.dip2px(60);
        this.mMinVoiceViewLength = ScreenUtil.dip2px(30);
        this.mVoiceViewExtraLength = mMaxVoiceViewLength - mMinVoiceViewLength;
    }

    @Override
    int getItemLayoutId() {
        return R.layout.layout_update_log_voice;
    }

    @Override
    void initContentView() {
        ButterKnife.bind(this, view_contentRoot);
        textView_packUp.setOnClickListener(this);
    }

    @Override
    void setHolderData() {
        RemarkBean remarkBean = mLogData.getRemark();
        if (remarkBean == null)
            return;
        textView_logType.setText(getLogTypeMsg(mLogData.sceneId));
        boolean isUnPackUp = mListener.isCurrentUnPackUp(mLogData.getRemark().inquiryId);
        view_contentRoot.setBackgroundResource((isUnPackUp ? R.drawable.shape_background_update_log_normal : R.drawable.shape_background_update_log_pick_up));
        textView_packUp.setCompoundDrawables(null, null, (isUnPackUp ? mListener.getDrawableUnPackUp() : mListener.getDrawablePackUp()), null);
        if (!isUnPackUp) {
            frameLayout_voice.setVisibility(View.GONE);
            textView_voiceContent.setVisibility(View.GONE);
            return;
        }
        if (mLogData == textView_logType.getTag()) {
            frameLayout_voice.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(remarkBean.content))
                textView_voiceContent.setVisibility(View.VISIBLE);
            return;
        }
//        List<LocalRemarkAttachment> attachments = remarkBean.attachments;
        List<LocalRemarkAttachment> attachments = null;
        if (attachments == null || attachments.isEmpty())
            return;
        LocalRemarkAttachment attachment = attachments.get(0);
        int voiceTimeSecond = (int) (attachment.timeLength / 1000);
        textView_voiceTime.setText(voiceTimeSecond + "\"");
        if (!TextUtils.isEmpty(remarkBean.content)) {
            textView_voiceContent.setText(remarkBean.content);
            textView_voiceContent.setVisibility(View.VISIBLE);
        } else
            textView_voiceContent.setVisibility(View.GONE);

        int viewWidth = (int) (mVoiceViewExtraLength * 1.0f * voiceTimeSecond / CommonConst.MAX_VOICE_TIME_SECONDS) + mMinVoiceViewLength;
        viewWidth = viewWidth > mMaxVoiceViewLength ? mMaxVoiceViewLength : viewWidth;
        ViewGroup.LayoutParams lp = imageView_bg.getLayoutParams();
        lp.width = viewWidth;
        resetAudioAnimate(imageView_animate, remarkBean);
        imageView_bg.setLayoutParams(lp);
        imageView_bg.setOnClickListener(this);
        frameLayout_voice.setVisibility(View.VISIBLE);
        textView_logType.setTag(mLogData);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pick_up:
                if (mLogData.getRemark() == null)
                    return;
                boolean isUnPickUp = mListener.isCurrentUnPackUp(mLogData.getRemark().inquiryId);
                if (isUnPickUp)
                    mListener.removeUnPackUp(mLogData.getRemark().inquiryId);
                else
                    mListener.cacheInUnPackUp(mLogData.getRemark().inquiryId);
                setHolderData();
                break;
            case R.id.iv_voice_bg:
                RemarkBean remarkBean = mLogData.getRemark();
                if (remarkBean == null)
                    return;
                playAudioAnimate(imageView_animate, remarkBean, mPosition, false);
                break;
        }
    }

    private void resetAudioAnimate(ImageView animationView, RemarkBean currentData) {
        boolean isCurrentIsPlaying = isCurrentVoiceIsPlaying(currentData);
        if (isCurrentIsPlaying)
            stopAudioAnimate();
        animationView.setBackgroundDrawable(null);
        animationView.setBackgroundResource(R.drawable.audio_animation_list_left);
        AnimationDrawable animation = (AnimationDrawable) animationView.getBackground();
        if (isCurrentIsPlaying) {
            animation.start();
            imageView_animatingView = animationView;
            mRemarkBeanOnPlaying = currentData;
        } else {
            animation.stop();
            animation.selectDrawable(2);
        }

    }

    private boolean isCurrentVoiceIsPlaying(RemarkBean data) {
        return mRemarkBeanOnPlaying != null && mRemarkBeanOnPlaying.inquiryId != null && data != null && mRemarkBeanOnPlaying.inquiryId.equals(data.inquiryId);
    }

//    private void playAudioAnimate(ImageView animationView, RemarkBean currentData, int position) {
//        playAudioAnimate(animationView, currentData, position, false);
//    }

    private void playAudioAnimate(ImageView animationView, RemarkBean currentData, int position, boolean changePlayMode) {
        boolean isCurrentIsPlaying = isCurrentVoiceIsPlaying(currentData);
        stopAudioAnimate();
        //停止语音播放
        sendAdapterEvent(AdapterEventIdConst.EVENT_ID_STOP_PLAY_RECORD, position, null, null);
//        if ((!isCurrentIsPlaying || changePlayMode) && animationView.getBackground() instanceof AnimationDrawable) {
//            AnimationDrawable animation = (AnimationDrawable) animationView.getBackground();
//            LocalRemarkAttachment attachment = currentData.attachments.get(0);
//            File fileSource = new File(attachment.filePath);
//            if (fileSource.exists() && fileSource.length() > 0) {
//                LogUtil.d(fileSource.getAbsolutePath());
//                sendAdapterEvent((changePlayMode ? AdapterEventIdConst.EVENT_ID_PLAY_RECORD_WITH_NEW_MODE : AdapterEventIdConst.EVENT_ID_PLAY_RECORD), position, null, fileSource);
//                animation.start();
//                mRemarkBeanOnPlaying = currentData;
//                imageView_animatingView = animationView;
//            } else
//                sendAdapterEvent(AdapterEventIdConst.EVENT_ID_RECORD_IS_NOT_EXIST, position, null, fileSource);
//        }
    }

    public static void stopAudioAnimate() {
        if (imageView_animatingView == null)
            return;
        if (imageView_animatingView.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) imageView_animatingView.getBackground();
            if (animation.isRunning()) {
                animation.stop();
                animation.selectDrawable(2);
                mRemarkBeanOnPlaying = null;
                imageView_animatingView = null;
            }
        }
    }

    public static void onDestroy() {
        imageView_animatingView = null;
        mRemarkBeanOnPlaying = null;
    }
}
