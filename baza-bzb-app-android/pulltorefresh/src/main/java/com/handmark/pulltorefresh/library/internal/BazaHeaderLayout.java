package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.handmark.pulltorefresh.library.LoadingLayoutBase;
import com.handmark.pulltorefresh.library.R;

/**
 * Created by zhouwk on 2015/12/21 0021.
 */
public class BazaHeaderLayout extends LoadingLayoutBase {

    private boolean isRebound;
    private FrameLayout mInnerLayout;
    private ImageView mBabyImage;

    private AnimationDrawable animBabyShow;
    private AnimationDrawable animBabyShake;

    public BazaHeaderLayout(Context context, int gravity) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.rcbox_header_loadinglayout, this);
        mInnerLayout = findViewById(R.id.fl_inner);
        mBabyImage = mInnerLayout.findViewById(R.id.pull_to_refresh_baby);

        LayoutParams lp = (LayoutParams) mInnerLayout.getLayoutParams();
        lp.gravity = gravity == 0 ? Gravity.BOTTOM : Gravity.TOP;

        reset();
    }

    // 获取"加载头部"高度
    @Override
    public int getContentSize() {
        return mInnerLayout.getHeight();
    }

    // 开始下拉时的回调
    @Override
    public void pullToRefresh() {
        mBabyImage.setImageResource(R.drawable.pull_image);
    }

    // "加载头部"完全显示时的回调
    @Override
    public void releaseToRefresh() {
        if (isRebound)
            return;
        mBabyImage.setImageResource(R.drawable.progress_rcbox_pull_to_refresh_second_anim);
        if (animBabyShow == null) {
            animBabyShow = (AnimationDrawable) mBabyImage.getDrawable();
        }
        animBabyShow.start();
    }

    // 下拉拖动时的回调
    @Override
    public void onPull(float scaleOfLayout) {
        if (isRebound)
            return;
        scaleOfLayout = scaleOfLayout > 1.0f ? 1.0f : scaleOfLayout;

        //缩放动画
        mBabyImage.setPivotY(mBabyImage.getMeasuredHeight());   // 设置中心点
        mBabyImage.setPivotX(mBabyImage.getMeasuredWidth() / 2);
//        ObjectAnimator animPX = ObjectAnimator.ofFloat(mBabyImage, "scaleX", 0, 1).setDuration(300);
//        animPX.setCurrentPlayTime((long) (scaleOfLayout * 300));
//        ObjectAnimator animPY = ObjectAnimator.ofFloat(mBabyImage, "scaleY", 0, 1).setDuration(300);
//        animPY.setCurrentPlayTime((long) (scaleOfLayout * 300));

        mBabyImage.setScaleX(scaleOfLayout);
        mBabyImage.setScaleY(scaleOfLayout);
    }

    // 释放后刷新时的回调
    @Override
    public void refreshing() {
        if (isRebound)
            return;
        if (null != animBabyShow) {
            animBabyShow.stop();
        }
        mBabyImage.setImageResource(R.drawable.progress_rcbox_pull_to_refresh_third_anim);
        if (animBabyShake == null) {
            animBabyShake = (AnimationDrawable) mBabyImage.getDrawable();
        }
        animBabyShake.start();
    }

    @Override
    public void reset() {
        if (isRebound)
            return;
        if (animBabyShow != null) {
            animBabyShow.stop();
            animBabyShow = null;
        }
        if (animBabyShake != null) {
            animBabyShake.stop();
            animBabyShake = null;
        }
    }

    @Override
    public void setPullLabel(CharSequence pullLabel) {

    }

    @Override
    public void setRefreshingLabel(CharSequence refreshingLabel) {

    }

    @Override
    public void setReleaseLabel(CharSequence releaseLabel) {

    }

    @Override
    protected void onDetachedFromWindow() {

        if (animBabyShake != null) {
            try {
                if (animBabyShake.isRunning())
                    animBabyShake.stop();
                animBabyShake = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (animBabyShow != null) {
            try {
                if (animBabyShow.isRunning())
                    animBabyShow.stop();
                animBabyShow = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void reboundState(boolean isRebound) {
        this.isRebound = isRebound;
        mBabyImage.setVisibility(isRebound ? View.GONE : View.VISIBLE);
    }
}
