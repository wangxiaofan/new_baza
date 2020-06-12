package com.baza.android.bzw.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.baza.android.bzw.log.LogUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/5/22.
 * Title：
 * Note：
 */

public class ScrollShowImageView extends FloatingActionButton {
    private int mReadyToShowCount;
    private int mRightDistance;
    private ValueAnimator mValueAnimatorTopViewIn, mValueAnimatorTopViewOut;
    private boolean mAnimationInPrepareShowing, mAnimationOutPrepareShowing;
    private ViewGroup.MarginLayoutParams mMarginLayoutParams;

    public ScrollShowImageView(Context context) {
        this(context, null);
    }

    public ScrollShowImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollShowImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = attrs == null ? null : getContext().obtainStyledAttributes(attrs, R.styleable.ScrollShowImageView);
        if (ta != null) {
            mReadyToShowCount = ta.getInt(R.styleable.ScrollShowImageView_readyShowCount, 10);
            mRightDistance = (int) ta.getDimension(R.styleable.ScrollShowImageView_rightDistance, 0);
            ta.recycle();
        }
        post(new Runnable() {
            @Override
            public void run() {
                mMarginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                mMarginLayoutParams.rightMargin = -getMeasuredWidth();
                setLayoutParams(mMarginLayoutParams);
                setVisibility(View.GONE);
            }
        });
    }

    public void onScroll(int firstVisibleItem, int visibleItemCount) {
        if (mMarginLayoutParams == null)
            return;
        if (firstVisibleItem + visibleItemCount >= mReadyToShowCount) {
            if (mAnimationInPrepareShowing || getVisibility() == View.VISIBLE || (mValueAnimatorTopViewIn != null && mValueAnimatorTopViewIn.isRunning()))
                return;
            LogUtil.d("mAnimationInPrepareShowing");
            mAnimationInPrepareShowing = true;
            mAnimationOutPrepareShowing = false;
            if (mValueAnimatorTopViewOut != null && mValueAnimatorTopViewOut.isRunning())
                mValueAnimatorTopViewOut.cancel();
            if (mValueAnimatorTopViewIn == null) {
                mValueAnimatorTopViewIn = ValueAnimator.ofInt(-getMeasuredWidth(), mRightDistance);
                mValueAnimatorTopViewIn.setDuration(200);
                mValueAnimatorTopViewIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mMarginLayoutParams.rightMargin = (int) valueAnimator.getAnimatedValue();
                        setLayoutParams(mMarginLayoutParams);
                        if (ScrollShowImageView.this.getVisibility() != View.VISIBLE)
                            ScrollShowImageView.this.setVisibility(View.VISIBLE);
                    }
                });
            }
            mValueAnimatorTopViewIn.start();
        } else {
            if (mAnimationOutPrepareShowing || getVisibility() == View.GONE || (mValueAnimatorTopViewOut != null && mValueAnimatorTopViewOut.isRunning()))
                return;
            mAnimationOutPrepareShowing = true;
            mAnimationInPrepareShowing = false;
            if (mValueAnimatorTopViewIn != null && mValueAnimatorTopViewIn.isRunning())
                mValueAnimatorTopViewIn.cancel();
            LogUtil.d("mAnimationOutPrepareShowing");
            if (mValueAnimatorTopViewOut == null) {
                mValueAnimatorTopViewOut = ValueAnimator.ofInt(mRightDistance, -getMeasuredWidth());
                mValueAnimatorTopViewOut.setDuration(200);
                mValueAnimatorTopViewOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mMarginLayoutParams.rightMargin = (int) valueAnimator.getAnimatedValue();
                        setLayoutParams(mMarginLayoutParams);
                        if (mMarginLayoutParams.rightMargin <= -getMeasuredWidth())
                            ScrollShowImageView.this.setVisibility(View.GONE);
                    }
                });
            }
            mValueAnimatorTopViewOut.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mValueAnimatorTopViewIn != null)
            mValueAnimatorTopViewIn.cancel();
        mValueAnimatorTopViewIn = null;
        if (mValueAnimatorTopViewOut != null)
            mValueAnimatorTopViewOut.cancel();
        mValueAnimatorTopViewOut = null;
    }
}
