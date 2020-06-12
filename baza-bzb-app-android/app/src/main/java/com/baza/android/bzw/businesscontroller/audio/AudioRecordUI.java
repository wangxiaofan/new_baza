package com.baza.android.bzw.businesscontroller.audio;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.constant.CommonConst;
import com.bznet.android.rcbox.R;
import com.slib.utils.ToastUtil;

/**
 * Created by Vincent.Lei on 2019/8/12.
 * Title：
 * Note：
 */
public class AudioRecordUI implements View.OnTouchListener {
    private static final int TIME_JUDGE_RATE = 1000;//毫秒 记时间隔
    private static final int TIME_HINT_LEVEL = 10;//秒 剩余多长时间后要提醒用户

    public enum State {
        NONE, RECORDING, CANCELED
    }

    private TextView textView_touch;
    private View view_prompt;
    private TextView textView_hint, textView_timeCutDown;
    private ImageView imageView_hint;
    private long mLastFinishRecordTime;
    private boolean mReachMaxTime;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int mTimeSeconds;
    private AudioRecordUI.State state = AudioRecordUI.State.NONE;
    private AnimationDrawable mAnimationDrawable;
    private IAudioRecordUIListener mUIListener;

    public interface IAudioRecordUIListener {
        void onReadyToStartRecord();

        void onFinishRecord();

        void onCancelRecord();
    }

    public AudioRecordUI(TextView textView_touch, View view_prompt, IAudioRecordUIListener uiListener) {
        this.textView_touch = textView_touch;
        this.view_prompt = view_prompt;
        this.mUIListener = uiListener;
        init();
    }

    private void init() {
        textView_hint = view_prompt.findViewById(R.id.tv_text_hint);
        textView_timeCutDown = view_prompt.findViewById(R.id.tv_time_cut_down);
        imageView_hint = view_prompt.findViewById(R.id.iv_pic_hint);
        textView_touch.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown();
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                onTouchUp();
                break;
        }
        return true;
    }

    // 上滑取消录音判断
    private static boolean isCancelled(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        return event.getRawX() < location[0] || event.getRawX() > location[0] + view.getWidth()
                || event.getRawY() < location[1];

    }

    private void onTouchDown() {
        if (mLastFinishRecordTime > 0 && (System.currentTimeMillis() - mLastFinishRecordTime) < CommonConst.MIN_TWO_TIME_RECORD_DISTANCE) {
            ToastUtil.showToast(view_prompt.getContext(), R.string.audio_error_too_quickly);
            return;
        }
        if (state == AudioRecordUI.State.RECORDING)
            return;
        state = AudioRecordUI.State.RECORDING;
        imageView_hint.setVisibility(View.VISIBLE);
        textView_timeCutDown.setVisibility(View.GONE);
        view_prompt.setVisibility(View.VISIBLE);
        textView_hint.setText(R.string.up_move_to_cancel_send);
        textView_hint.setBackgroundDrawable(null);
        updateAnimateView(true, 0);
        textView_touch.setBackgroundResource(R.drawable.shape_background_chat_voice_pressed);
        textView_touch.setText(R.string.press_to_speak);
        mUIListener.onReadyToStartRecord();
        mReachMaxTime = false;
        startCalculateTask();
    }

    private void onTouchMove(MotionEvent event) {
        boolean isCancelled = isCancelled(textView_touch, event);
        if (isCancelled && state != AudioRecordUI.State.CANCELED) {
            state = AudioRecordUI.State.CANCELED;
            textView_hint.setText(R.string.release_to_cancel_send);
            updateAnimateView(false, R.drawable.icon_hint_cancel_recored);
        } else if (!isCancelled && state != AudioRecordUI.State.RECORDING) {
            state = AudioRecordUI.State.RECORDING;
            textView_hint.setText(R.string.up_move_to_cancel_send);
            textView_hint.setBackgroundDrawable(null);
            updateAnimateView(true, 0);
        }
    }

    private void onTouchUp() {
        if (mReachMaxTime) {
            state = AudioRecordUI.State.NONE;
            return;
        }
        view_prompt.setVisibility(View.GONE);
        textView_touch.setBackgroundResource(R.drawable.shape_background_chat_voice_nomal);
        if (state == AudioRecordUI.State.RECORDING)
            //通知保存
            mUIListener.onFinishRecord();
        else
            mUIListener.onCancelRecord();
        mHandler.removeCallbacks(runnableCalculateTime);
        state = AudioRecordUI.State.NONE;
        mLastFinishRecordTime = System.currentTimeMillis();
    }

    /**
     * 语音计时间
     */
    private void startCalculateTask() {
        mTimeSeconds = CommonConst.MAX_REMARK_AUDIO_TIME_SECONDS;
        mHandler.postDelayed(runnableCalculateTime, TIME_JUDGE_RATE);
    }

    private Runnable runnableCalculateTime = new Runnable() {
        @Override
        public void run() {
            mTimeSeconds--;
            if (mTimeSeconds < 0) {
                //录音时间达到最大值
                if (state != AudioRecordUI.State.NONE) {
                    onTouchUp();
                    mReachMaxTime = true;
//                    if (state != State.CANCELED)
//                        mChatView.callShowSpecialToastMsg(ResumeDetailPresenter.SELF_TOAST_RECORD, null, R.string.audio_error_time_overflow);
                }
                return;
            }
            if (mTimeSeconds <= TIME_HINT_LEVEL) {
                //UI提醒剩余时间
                updateAnimateView(false, 0);
                imageView_hint.setVisibility(View.GONE);
                if (textView_timeCutDown.getVisibility() != View.VISIBLE)
                    textView_timeCutDown.setVisibility(View.VISIBLE);
                textView_timeCutDown.setText(String.valueOf(mTimeSeconds));
            }
            mHandler.postDelayed(runnableCalculateTime, TIME_JUDGE_RATE);
        }
    };

    private void updateAnimateView(boolean animate, int resId) {
        if (animate) {
            imageView_hint.setImageDrawable(null);
            if (mAnimationDrawable == null) {
                imageView_hint.setBackgroundResource(R.drawable.record_animation);
                mAnimationDrawable = (AnimationDrawable) imageView_hint.getBackground();
            } else if (!(imageView_hint.getBackground() instanceof AnimationDrawable))
                imageView_hint.setBackgroundDrawable(mAnimationDrawable);

            if (!mAnimationDrawable.isRunning())
                mAnimationDrawable.start();
        } else {
            if (mAnimationDrawable != null && mAnimationDrawable.isRunning())
                mAnimationDrawable.stop();
            imageView_hint.setBackgroundDrawable(null);
            if (resId > 0)
                imageView_hint.setImageResource(resId);
        }
    }

    public void onDestroy() {
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
            mAnimationDrawable = null;
        }
    }
}
