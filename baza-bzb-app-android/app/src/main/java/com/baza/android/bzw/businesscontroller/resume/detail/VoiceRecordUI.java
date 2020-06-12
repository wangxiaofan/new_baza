package com.baza.android.bzw.businesscontroller.resume.detail;

import android.animation.ValueAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.businesscontroller.resume.detail.presenter.ResumeDetailPresenter;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.CommonConst;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/1/11.
 * Title : 语音录音控件控制
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class VoiceRecordUI implements ValueAnimator.AnimatorUpdateListener, View.OnTouchListener {


    private static final int TIME_JUDGE_RATE = 1000;//毫秒 记时间隔
    private static final int TIME_HINT_LEVEL = 10;//秒 剩余多长时间后要提醒用户

    private View view_touch, view_prompt;
    private ResumeDetailPresenter mPresenter;
    private IResumeDetailView mResumeView;
    private TextView textView_hint, textView_timeCutDown;
    private ImageView imageView_hint;
    private View view_press;

    private boolean mIsPaneShown;
    private int mAnimateHeight;
    private ValueAnimator mValueAnimatorShow, mValueAnimatorHide;
    private long mLastFinishRecordTime;
    //    private int mAmplitudeDrawableId = R.drawable.voice_amplitude1;
    private int mTimeSeconds;
    private boolean mReachMaxTime;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private AnimationDrawable mAnimationDrawable;

    public enum State {
        NONE, RECORDING, CANCELED
    }

    private State state = State.NONE;

    public VoiceRecordUI(View viewTouch, View viewPrompt, IResumeDetailView mResumeView, ResumeDetailPresenter mPresenter) {
        this.view_touch = viewTouch;
        this.view_prompt = viewPrompt;
        this.mPresenter = mPresenter;
        this.mResumeView = mResumeView;

        init();
    }

    private void init() {
        textView_hint = view_prompt.findViewById(R.id.tv_text_hint);
        textView_timeCutDown = view_prompt.findViewById(R.id.tv_time_cut_down);
        imageView_hint = view_prompt.findViewById(R.id.iv_pic_hint);
        view_press = view_touch.findViewById(R.id.fl_press_to_record);
        mAnimateHeight = mResumeView.callGetResources().getDimensionPixelSize(R.dimen.voice_record_touch_ui_height);
        view_press.setOnTouchListener(this);
    }

    public void openRecordPane() {
        if (mValueAnimatorShow == null) {
            mValueAnimatorShow = ValueAnimator.ofInt(mAnimateHeight, 0);
            mValueAnimatorShow.setDuration(200);
            mValueAnimatorShow.addUpdateListener(this);
        }
        if (mIsPaneShown)
            return;
        if (mValueAnimatorShow != null && mValueAnimatorShow.isRunning())
            return;
        if (mValueAnimatorHide != null && mValueAnimatorHide.isRunning())
            return;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mValueAnimatorShow.start();
            }
        }, 50);
        mIsPaneShown = true;
    }

    public void closeRecordPane() {
        if (mValueAnimatorHide == null) {
            mValueAnimatorHide = ValueAnimator.ofInt(0, mAnimateHeight);
            mValueAnimatorHide.setDuration(200);
            mValueAnimatorHide.addUpdateListener(this);
        }
        if (!mIsPaneShown)
            return;
        if (mValueAnimatorShow != null && mValueAnimatorShow.isRunning())
            return;
        if (mValueAnimatorHide != null && mValueAnimatorHide.isRunning())
            return;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mValueAnimatorHide.start();
            }
        }, 50);
        mIsPaneShown = false;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int height = (int) valueAnimator.getAnimatedValue();
        if (view_touch.getVisibility() != View.VISIBLE)
            view_touch.setVisibility(View.VISIBLE);
        view_touch.setTranslationY(height);
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

    public boolean checkHideWhenTouchWindow(MotionEvent motionEvent) {
        //当录音面板展示时 点击屏幕上录音面板上方区域关闭面板
        if (mIsPaneShown && isCancelled(view_touch, motionEvent)) {
            closeRecordPane();
            return true;
        }
        return false;
    }

    /**
     * 录音振幅变化
     */
//    public void onAmplitudeChanged(int value) {
//    }
    private void onTouchDown() {
        if (mLastFinishRecordTime > 0 && (System.currentTimeMillis() - mLastFinishRecordTime) < CommonConst.MIN_TWO_TIME_RECORD_DISTANCE) {
            mResumeView.callShowToastMessage(null, R.string.audio_error_too_quickly);
            return;
        }
        if (state == State.RECORDING)
            return;
        state = State.RECORDING;
        imageView_hint.setVisibility(View.VISIBLE);
        textView_timeCutDown.setVisibility(View.GONE);
        view_prompt.setVisibility(View.VISIBLE);
        textView_hint.setText(R.string.up_move_to_cancel_send);
        updateAnimateView(true, 0);
        view_press.setBackgroundResource(R.drawable.shape_background_voice_button_pressed);
        mPresenter.startRecord();
        mReachMaxTime = false;
        startCalculateTask();
    }

    private void onTouchMove(MotionEvent event) {
        boolean isCancelled = isCancelled(view_press, event);
        if (isCancelled && state != State.CANCELED) {
            state = State.CANCELED;
            textView_hint.setText(R.string.release_to_cancel_send);
            updateAnimateView(false, R.drawable.icon_hint_cancel_recored);
        } else if (!isCancelled && state != State.RECORDING) {
            state = State.RECORDING;
            textView_hint.setText(R.string.up_move_to_cancel_send);
            textView_hint.setBackgroundDrawable(null);
            updateAnimateView(true, 0);
        }
    }

    private void onTouchUp() {
        if (mReachMaxTime) {
            state = State.NONE;
            return;
        }
        view_prompt.setVisibility(View.GONE);
        view_press.setBackgroundResource(R.drawable.shape_background_voice_button_normal);
        closeRecordPane();
        if (state == State.RECORDING)
            //通知保存
            mPresenter.finishRecord();
        else
            mPresenter.cancelRecord();
        mHandler.removeCallbacks(runnableCalculateTime);
        state = State.NONE;
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
                if (state != State.NONE) {
                    onTouchUp();
                    mReachMaxTime = true;
                    if (state != State.CANCELED)
                        mResumeView.callShowSpecialToastMsg(ResumeDetailPresenter.SELF_TOAST_RECORD, null, R.string.audio_error_time_overflow);
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
