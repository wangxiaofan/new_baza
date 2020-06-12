package com.baza.android.bzw.businesscontroller.resume.detail.adapter.remark;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resumeelement.RemarkBean;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.dao.AttachmentRemarkDao;
import com.baza.android.bzw.log.LogUtil;
import com.bznet.android.rcbox.R;
import com.slib.utils.DateUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Vincent.Lei on 2017/5/24.
 * Title：
 * Note：
 */

public class RemarkListAdapter extends BaseBZWAdapter implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int TYPE_VOICE = 0;
    private static final int TYPE_TEXT = 1;

    private Context mContext;
    private Resources mResources;
    private ViewHolder.VoiceHolder mAnimatingVoiceHolder;
    private ArrayList<RemarkBean> mRemarkList;
    private boolean mIsResumeFromOthers;
    private View view_cacheShowEditMenuClickView;
    private Handler mHandler;
    private AudioRemarkStateProvider mAudioRemarkStateProvider = new AudioRemarkStateProvider();

    public RemarkListAdapter(Context context, boolean mIsResumeFromOthers, ArrayList<RemarkBean> remarkList, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = context;
        this.mIsResumeFromOthers = mIsResumeFromOthers;
        this.mResources = context.getResources();
        this.mRemarkList = remarkList;
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int getCount() {
        return mRemarkList == null ? 0 : mRemarkList.size();
    }

    public boolean isCurrentVoiceIsPlaying(RemarkBean data) {
        return mAudioRemarkStateProvider.isCurrentVoiceIsPlaying(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemType = getItemViewType(position);
        RemarkBean remarkBean = mRemarkList.get(position);
        boolean init = false;
        ViewHolder viewHolder;
        if (convertView == null) {
            init = true;
            convertView = LayoutInflater.from(mContext).inflate(getItemLayoutId(itemType), null);
            viewHolder = new ViewHolder(convertView, itemType, mHandler);
            convertView.setTag(viewHolder);
            convertView.setOnClickListener(this);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        switch (itemType) {
            case TYPE_TEXT:
                //设置文字备注
                setTextRemarkView(viewHolder, remarkBean, position);
                break;
            case TYPE_VOICE:
                //设置语音备注
                setVoiceRemarkView(viewHolder, remarkBean, position);
                break;
        }
        if (init) {
            //首次初始化后ConstraintLayout中部分控件GONE属性可能会导致空占位
            //requestLayout重新绘制一次
            final View finalConvertView = convertView;
            convertView.post(new Runnable() {
                @Override
                public void run() {
                    finalConvertView.requestLayout();
                }
            });
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        RemarkBean remarkBean = mRemarkList.get(position);
        return remarkBean.isVoice() ? TYPE_VOICE : TYPE_TEXT;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    private int getItemLayoutId(int itemType) {
        int layoutId = 0;
        switch (itemType) {
            case TYPE_TEXT:
                layoutId = R.layout.adapter_item_for_text_remark;
                break;
            case TYPE_VOICE:
                layoutId = R.layout.adapter_item_for_voice_remark;
                break;
        }
        return layoutId;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_voice_animate:
                int position = (int) v.getTag();
                //播放语音
                RemarkBean localRemarkBean = mRemarkList.get(position);
                playAudioAnimate((ViewHolder.VoiceHolder) v.getTag(R.id.id_animate_view), localRemarkBean, position);
                break;
            case R.id.iv_edit:
                onEditButtonClick(v);
                break;
        }
    }

    private void onEditButtonClick(View v) {
        int type = (int) v.getTag(R.id.hold_tag_id_one);
        switch (type) {
            case TYPE_TEXT:
            case TYPE_VOICE:
                int position = (int) v.getTag();
                if (type == TYPE_VOICE) {
                    view_cacheShowEditMenuClickView = v;
                }
                sendAdapterEvent(AdapterEventIdConst.EVENT_ID_SHOW_EDIT_REMARK_TIPS_MENU, position, v, mRemarkList.get(position));
                break;
        }
    }


    public void refreshTargetView(int dataIndex, View view, boolean isResumeFromOthers) {
        this.mIsResumeFromOthers = isResumeFromOthers;
        if (view == null || dataIndex < 0) {
            notifyDataSetChanged();
            return;
        }
        int itemType = getItemViewType(dataIndex);
        switch (itemType) {
            case TYPE_TEXT:
                //设置文字备注
                setTextRemarkView((ViewHolder) view.getTag(), mRemarkList.get(dataIndex), dataIndex);
                break;
            case TYPE_VOICE:
                //设置语音备注
                setVoiceRemarkView((ViewHolder) view.getTag(), mRemarkList.get(dataIndex), dataIndex);
                break;
        }
    }

    private void setTextRemarkView(ViewHolder viewHolder, RemarkBean data, int position) {
        viewHolder.mTextHolder.textView_remark_time.setText(DateUtil.longMillions2FormatDate(data.updateTime, DateUtil.SDF_YMD_HM));
        viewHolder.mTextHolder.textView_content.setText(data.content);
        if (!TextUtils.isEmpty(data.jobHoppingOccasion)) {
            viewHolder.mTextHolder.textView_jobHoping.setText(mResources.getString(R.string.remark_title_of_job_change, data.jobHoppingOccasion));
            viewHolder.mTextHolder.textView_jobHoping.setVisibility(View.VISIBLE);
        } else
            viewHolder.mTextHolder.textView_jobHoping.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(data.employerInfo)) {
            viewHolder.mTextHolder.textView_hirerDes.setText(mResources.getString(R.string.remark_title_of_hirer_des, data.employerInfo));
            viewHolder.mTextHolder.textView_hirerDes.setVisibility(View.VISIBLE);
        } else
            viewHolder.mTextHolder.textView_hirerDes.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(data.expectSalary)) {
            viewHolder.mTextHolder.textView_expectSalary.setText(mResources.getString(R.string.remark_title_of_expert_salary, data.expectSalary));
            viewHolder.mTextHolder.textView_expectSalary.setVisibility(View.VISIBLE);
        } else
            viewHolder.mTextHolder.textView_expectSalary.setVisibility(View.GONE);
        viewHolder.mTextHolder.view_depart.setVisibility((position == mRemarkList.size() - 1 ? View.GONE : View.VISIBLE));
        if (data.isMyCreate != RemarkBean.INQUIRY_CREATE_BY_ME || mIsResumeFromOthers) {
            //备注来自其他人或者这份简历来自其他人分享的  文字备注不能编辑
            if (data.isMyCreate != RemarkBean.INQUIRY_CREATE_BY_ME && !TextUtils.isEmpty(data.creatorName)) {
                viewHolder.mTextHolder.textView_fromUser.setText(mResources.getString(R.string.remark_from_user, data.creatorName));
                viewHolder.mTextHolder.textView_fromUser.setVisibility(View.VISIBLE);
            } else
                viewHolder.mTextHolder.textView_fromUser.setVisibility(View.GONE);
            viewHolder.mTextHolder.imageView_edit.setVisibility(View.GONE);
            return;
        }
        viewHolder.mTextHolder.textView_fromUser.setVisibility(View.GONE);
        viewHolder.mTextHolder.imageView_edit.setVisibility(data.isSelfCreated() ? View.VISIBLE : View.GONE);
        if (data.isSelfCreated()) {
            viewHolder.mTextHolder.imageView_edit.setOnClickListener(this);
            viewHolder.mTextHolder.imageView_edit.setTag(position);
            viewHolder.mTextHolder.imageView_edit.setTag(R.id.hold_tag_id_two, viewHolder.mTextHolder);
            viewHolder.mTextHolder.imageView_edit.setTag(R.id.hold_tag_id_one, TYPE_TEXT);
        }
    }

    private void setVoiceRemarkView(ViewHolder viewHolder, RemarkBean data, int position) {
        if (viewHolder == null)
            return;
        viewHolder.mVoiceHolder.textView_remark_time.setText(DateUtil.longMillions2FormatDate(data.updateTime, DateUtil.SDF_YMD_HM));
        if (!TextUtils.isEmpty(data.content)) {
            viewHolder.mVoiceHolder.textView_content.setText(data.content);
            viewHolder.mVoiceHolder.textView_content.setVisibility(View.VISIBLE);
        } else
            viewHolder.mVoiceHolder.textView_content.setVisibility(View.GONE);
        Integer loadingStatus = AttachmentRemarkDao.getAudioSourceOnLoadingStatus(data.inquiryId);
        viewHolder.mVoiceHolder.textView_on_loading.setVisibility(loadingStatus.equals(AttachmentRemarkDao.LOADING) || loadingStatus.equals(AttachmentRemarkDao.FAILED) ? View.VISIBLE : View.GONE);
        if (viewHolder.mVoiceHolder.textView_on_loading.getVisibility() == View.VISIBLE) {
            viewHolder.mVoiceHolder.textView_on_loading.setText(loadingStatus.equals(AttachmentRemarkDao.LOADING) ? R.string.attachment_loading : R.string.attachment_load_failed);
        }
        viewHolder.mVoiceHolder.textView_voice_time.setText(mAudioRemarkStateProvider.getShowDurationMsg(data));
        resetAudioAnimate(viewHolder.mVoiceHolder, data);
        if (!TextUtils.isEmpty(data.jobHoppingOccasion)) {
            viewHolder.mVoiceHolder.textView_jobHoping.setText(mResources.getString(R.string.remark_title_of_job_change, data.jobHoppingOccasion));
            viewHolder.mVoiceHolder.textView_jobHoping.setVisibility(View.VISIBLE);
        } else
            viewHolder.mVoiceHolder.textView_jobHoping.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(data.employerInfo)) {
            viewHolder.mVoiceHolder.textView_hirerDes.setText(mResources.getString(R.string.remark_title_of_hirer_des, data.employerInfo));
            viewHolder.mVoiceHolder.textView_hirerDes.setVisibility(View.VISIBLE);
        } else
            viewHolder.mVoiceHolder.textView_hirerDes.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(data.expectSalary)) {
            viewHolder.mVoiceHolder.textView_expectSalary.setText(mResources.getString(R.string.remark_title_of_expert_salary, data.expectSalary));
            viewHolder.mVoiceHolder.textView_expectSalary.setVisibility(View.VISIBLE);
        } else
            viewHolder.mVoiceHolder.textView_expectSalary.setVisibility(View.GONE);
        viewHolder.mVoiceHolder.imageView_animate.setOnClickListener(this);
        viewHolder.mVoiceHolder.imageView_animate.setTag(position);
        viewHolder.mVoiceHolder.imageView_animate.setTag(R.id.id_animate_view, viewHolder.mVoiceHolder);
        viewHolder.mVoiceHolder.view_depart.setVisibility((position == mRemarkList.size() - 1 ? View.GONE : View.VISIBLE));
        viewHolder.mVoiceHolder.imageView_edit.setOnClickListener(this);
        viewHolder.mVoiceHolder.imageView_edit.setTag(R.id.hold_tag_id_one, TYPE_VOICE);
        viewHolder.mVoiceHolder.imageView_edit.setTag(R.id.id_animate_view, viewHolder.mVoiceHolder);
        viewHolder.mVoiceHolder.imageView_edit.setTag(position);
        viewHolder.mVoiceHolder.seekBar.setOnSeekBarChangeListener(this);
        viewHolder.mVoiceHolder.seekBar.setTag(position);
    }


    private void sendAdapterEvent(int eventId, int position, View v, Object data) {
        if (mIAdapterEventsListener != null)
            mIAdapterEventsListener.onAdapterEventsArrival(eventId, position, v, data);
    }

    public void onDestroy() {
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
    }

    public void stopAudioAnimate() {
        if (mAnimatingVoiceHolder == null)
            return;
        mAnimatingVoiceHolder.imageView_animate.setImageResource(R.drawable.icon_play);
        mAnimatingVoiceHolder.seekBar.setProgress(0);
        mAudioRemarkStateProvider.clear();
        int position = (int) mAnimatingVoiceHolder.imageView_animate.getTag();
        RemarkBean remarkBean = mRemarkList.get(position);
        mAnimatingVoiceHolder.textView_voice_time.setText(mAudioRemarkStateProvider.getShowDurationMsg(remarkBean));
        mAnimatingVoiceHolder = null;
    }

    private void resetAudioAnimate(ViewHolder.VoiceHolder mVoiceHolder, RemarkBean currentData) {
        boolean isCurrentIsPlaying = mAudioRemarkStateProvider.isCurrentVoiceIsPlaying(currentData);
        mVoiceHolder.imageView_animate.setImageResource((isCurrentIsPlaying && !mAudioRemarkStateProvider.isPaused) ? R.drawable.ln_player_stop_btn : R.drawable.icon_play);
        mVoiceHolder.seekBar.setProgress(mAudioRemarkStateProvider.getProgress(currentData));
        if (isCurrentIsPlaying) {
            mAnimatingVoiceHolder = mVoiceHolder;
            mAudioRemarkStateProvider.remarkOnPlaying = currentData;
        }
    }

    private void playAudioAnimate(ViewHolder.VoiceHolder mVoiceHolder, RemarkBean currentData, int position) {
        playAudioAnimate(mVoiceHolder, currentData, position, false);
    }

    private void playAudioAnimate(ViewHolder.VoiceHolder mVoiceHolder, RemarkBean currentData, int position, boolean changePlayMode) {
        boolean isCurrentIsPlaying = mAudioRemarkStateProvider.isCurrentVoiceIsPlaying(currentData);
        if (mAudioRemarkStateProvider.hasPlayingAudio()) {
            if (!changePlayMode && isCurrentIsPlaying) {
                //暂停
                mVoiceHolder.imageView_animate.setImageResource(mAudioRemarkStateProvider.isPaused ? R.drawable.ln_player_stop_btn : R.drawable.icon_play);
                sendAdapterEvent(mAudioRemarkStateProvider.isPaused ? AdapterEventIdConst.EVENT_ID_CONTINUE_PLAY_RECORD : AdapterEventIdConst.EVENT_ID_PAUSE_PLAY_RECORD, position, null, null);
                mAudioRemarkStateProvider.isPaused = !mAudioRemarkStateProvider.isPaused;
                return;
            }
            stopAudioAnimate();
            //停止语音播放
            sendAdapterEvent(AdapterEventIdConst.EVENT_ID_STOP_PLAY_RECORD, position, null, null);
        }
        if (!isCurrentIsPlaying || changePlayMode) {
            File fileSource = AttachmentRemarkDao.getRemarkAudioLocalFilePath(currentData.inquiryId);
            if (fileSource != null && fileSource.exists() && fileSource.length() > 0) {
                LogUtil.d(fileSource.getAbsolutePath());
                mVoiceHolder.imageView_animate.setImageResource(R.drawable.ln_player_stop_btn);
                sendAdapterEvent((changePlayMode ? AdapterEventIdConst.EVENT_ID_PLAY_RECORD_WITH_NEW_MODE : AdapterEventIdConst.EVENT_ID_PLAY_RECORD), position, null, fileSource);
                mAudioRemarkStateProvider.remarkOnPlaying = currentData;
                mAnimatingVoiceHolder = mVoiceHolder;
            } else
                sendAdapterEvent(AdapterEventIdConst.EVENT_ID_REMARK_AUDIO_LOCAL_SOURCE_NOT_EXIST, position, null, currentData);
        }
    }

    public void performChangeAudioPlayMode() {
        if (view_cacheShowEditMenuClickView == null)
            return;
        LogUtil.d("performChangeAudioPlayMode");
        int position = (int) view_cacheShowEditMenuClickView.getTag();
        //播放语音
        RemarkBean localRemarkBean = mRemarkList.get(position);
        playAudioAnimate((ViewHolder.VoiceHolder) view_cacheShowEditMenuClickView.getTag(R.id.id_animate_view), localRemarkBean, position, true);
    }

    public void updateDurationChanged(int curPosition) {
        if (mAnimatingVoiceHolder == null)
            return;
        mAudioRemarkStateProvider.playingDuration = curPosition;
        mAnimatingVoiceHolder.seekBar.setProgress(mAudioRemarkStateProvider.getProgress());
        mAnimatingVoiceHolder.textView_voice_time.setText(mAudioRemarkStateProvider.getShowDurationMsg());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        int position = (int) seekBar.getTag();
        RemarkBean remark = mRemarkList.get(position);
        if (mAudioRemarkStateProvider.isCurrentVoiceIsPlaying(remark)) {
            int timeSecond = (int) (progress * 1.0f / 100 * remark.getAudioTimeLength());
            sendAdapterEvent(AdapterEventIdConst.EVENT_ID_SEEK_PLAY_RECORD, position, null, timeSecond);
        }
    }

    private static class ViewHolder {

        static class TextHolder {
            TextView textView_content, textView_remark_time, textView_fromUser, textView_jobHoping, textView_hirerDes, textView_expectSalary;
            ImageView imageView_edit;
            View view_depart;

            private TextHolder(View convertView) {
                textView_content = convertView.findViewById(R.id.tv_remark_content);
                textView_remark_time = convertView.findViewById(R.id.tv_remark_time);
                textView_fromUser = convertView.findViewById(R.id.tv_fromUser);
                imageView_edit = convertView.findViewById(R.id.iv_edit);
                view_depart = convertView.findViewById(R.id.view_depart);
                textView_jobHoping = convertView.findViewById(R.id.tv_job_hoping);
                textView_hirerDes = convertView.findViewById(R.id.tv_hirer_des);
                textView_expectSalary = convertView.findViewById(R.id.tv_expect_salary);
            }
        }

        static class VoiceHolder {
            ImageView imageView_bg, imageView_animate;
            ImageView imageView_edit;
            TextView textView_content, textView_remark_time, textView_voice_time, textView_on_loading, textView_jobHoping, textView_hirerDes, textView_expectSalary;
            View voice_layout;
            View view_depart;
            SeekBar seekBar;

            private VoiceHolder(View convertView) {
                imageView_bg = convertView.findViewById(R.id.iv_voice_bg);
                imageView_animate = convertView.findViewById(R.id.iv_voice_animate);
                imageView_edit = convertView.findViewById(R.id.iv_edit);
                textView_content = convertView.findViewById(R.id.tv_remark_content);
                textView_remark_time = convertView.findViewById(R.id.tv_remark_time);
                textView_voice_time = convertView.findViewById(R.id.tv_voice_time);
                voice_layout = convertView.findViewById(R.id.rl_voice_layout);
                view_depart = convertView.findViewById(R.id.view_depart);
                textView_on_loading = convertView.findViewById(R.id.tv_on_loading);
                seekBar = convertView.findViewById(R.id.seek_bar);
                textView_jobHoping = convertView.findViewById(R.id.tv_job_hoping);
                textView_hirerDes = convertView.findViewById(R.id.tv_hirer_des);
                textView_expectSalary = convertView.findViewById(R.id.tv_expect_salary);
            }
        }


        TextHolder mTextHolder;
        VoiceHolder mVoiceHolder;

        ViewHolder(View convertView, int itemType, Handler mHandler) {

            switch (itemType) {
                case TYPE_TEXT:
                    mTextHolder = new TextHolder(convertView);
                    break;
                case TYPE_VOICE:
                    mVoiceHolder = new VoiceHolder(convertView);
                    break;
            }
        }
    }
}
