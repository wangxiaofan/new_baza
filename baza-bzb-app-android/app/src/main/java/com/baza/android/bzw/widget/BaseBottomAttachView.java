package com.baza.android.bzw.widget;

import android.app.Activity;
import android.graphics.Color;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Vincent.Lei on 2019/8/14.
 * Title：
 * Note：
 */
public abstract class BaseBottomAttachView {
    protected Activity mActivity;
    protected View mContentView;
    protected FrameLayout mRootView;
    protected boolean mAnimating;
    protected boolean mShown;
    protected int mViewHeight;

    public BaseBottomAttachView(Activity activity) {
        this.mActivity = activity;
        mRootView = new FrameLayout(activity);
        mRootView.setBackgroundColor(Color.parseColor("#7F000000"));
        mRootView.setVisibility(View.GONE);
        mContentView = initContentView();

        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.gravity = Gravity.BOTTOM;
        mRootView.addView(mContentView, flp);
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        FrameLayout frameLayout = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        frameLayout.addView(mRootView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mViewHeight = frameLayout.getHeight();
        mRootView.setTranslationY(mViewHeight);
    }

    public abstract View initContentView();

    public void show() {
        if (mAnimating || mShown)
            return;
        mAnimating = true;
        ViewCompat.animate(mRootView).translationY(0).setDuration(300).setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
                if (mRootView.getVisibility() != View.VISIBLE)
                    mRootView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(View view) {
                mShown = true;
                mAnimating = false;
            }

            @Override
            public void onAnimationCancel(View view) {

            }
        }).start();
    }

    public void dismiss() {
        if (mAnimating || !mShown)
            return;
        mAnimating = true;
        ViewCompat.animate(mRootView).translationY(mViewHeight).setDuration(300).setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {

            }

            @Override
            public void onAnimationEnd(View view) {
                mShown = false;
                mAnimating = false;
                mRootView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(View view) {

            }
        }).start();
    }
}
