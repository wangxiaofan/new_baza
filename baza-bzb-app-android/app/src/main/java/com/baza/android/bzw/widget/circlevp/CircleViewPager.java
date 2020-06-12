package com.baza.android.bzw.widget.circlevp;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.bznet.android.rcbox.R;


public class CircleViewPager extends ViewPager {
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private long mDelay;
    private boolean mEnableAutoNext;
    private boolean mPaused;
    private OnPageChangeListener pageChangeListener;
    private Runnable mRunnableAutoNext = new Runnable() {
        @Override
        public void run() {
            toNextPage();
        }
    };

    public CircleViewPager(@NonNull Context context) {
        this(context, null);
    }

    public CircleViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleViewPager);
            if (typedArray != null) {
                mEnableAutoNext = typedArray.getBoolean(R.styleable.CircleViewPager_enableAutoNext, true);
                mDelay = typedArray.getInt(R.styleable.CircleViewPager_nextFrameDistanceSec, 3) * 1000L;
                if (mDelay <= 0)
                    mDelay = 5000L;
                typedArray.recycle();
            }

        }
        super.addOnPageChangeListener(onPageChangeListener);
    }

    @Override
    public final void setAdapter(@Nullable PagerAdapter adapter) {
        if (!(adapter instanceof CirclePagerAdapter))
            throw new IllegalArgumentException("adapter should be CirclePagerAdapter");
        super.setAdapter(adapter);
        setCurrentItem(0);
    }

    @Override
    public void setCurrentItem(int item) {
        CirclePagerAdapter circlePagerAdapter = (CirclePagerAdapter) getAdapter();
        if (circlePagerAdapter == null)
            return;
        super.setCurrentItem(circlePagerAdapter.getRelativePositionByPosition(item));
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        CirclePagerAdapter circlePagerAdapter = (CirclePagerAdapter) getAdapter();
        if (circlePagerAdapter == null)
            return;
        super.setCurrentItem(circlePagerAdapter.getRelativePositionByPosition(item), smoothScroll);
    }

    @Override
    public final void addOnPageChangeListener(@NonNull OnPageChangeListener listener) {
        this.pageChangeListener = listener;
    }

    private int mSelectedPosition;
    private ViewPager.OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (pageChangeListener != null) {
                CirclePagerAdapter circlePagerAdapter = (CirclePagerAdapter) getAdapter();
                if (circlePagerAdapter == null)
                    return;
                pageChangeListener.onPageScrolled(circlePagerAdapter.getRealPositionByRelative(position), positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            mSelectedPosition = position;
            CirclePagerAdapter circlePagerAdapter = (CirclePagerAdapter) getAdapter();
            if (circlePagerAdapter == null)
                return;
            if (pageChangeListener != null) {
                pageChangeListener.onPageSelected(circlePagerAdapter.getRealPositionByRelative(position));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == SCROLL_STATE_IDLE) {
                CirclePagerAdapter circlePagerAdapter = (CirclePagerAdapter) getAdapter();
                if (circlePagerAdapter == null)
                    return;
                int totalCount = circlePagerAdapter.getCount();
                if (mSelectedPosition == 0 || mSelectedPosition == totalCount - 1) {
                    CircleViewPager.super.removeOnPageChangeListener(onPageChangeListener);
                    CircleViewPager.super.setCurrentItem(mSelectedPosition == 0 ? (totalCount - 2) : 1, false);
                    CircleViewPager.super.addOnPageChangeListener(onPageChangeListener);
                }
            }
            if (pageChangeListener != null) {
                pageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (MotionEvent.ACTION_DOWN == ev.getAction()) {
            pauseAutoNext();
        } else if ((MotionEvent.ACTION_UP == ev.getAction() || MotionEvent.ACTION_CANCEL == ev.getAction())) {
            resumeAutoNext();
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (MotionEvent.ACTION_UP == ev.getAction()) {
            resumeAutoNext();
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        pauseAutoNext();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        resumeAutoNext();
    }

    private void pauseAutoNext() {
        mPaused = true;
        mHandler.removeCallbacksAndMessages(null);
    }

    private void resumeAutoNext() {
        mPaused = false;
        startToAutoDelay();
    }

    private void startToAutoDelay() {
        if (!mEnableAutoNext)
            return;
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(mRunnableAutoNext, mDelay);
    }

    private void toNextPage() {
        if (mPaused || !mEnableAutoNext)
            return;
        CirclePagerAdapter circlePagerAdapter = (CirclePagerAdapter) getAdapter();
        if (circlePagerAdapter != null) {
            int currentIndex = getCurrentItem();
            currentIndex++;
            if (currentIndex < circlePagerAdapter.getCount())
                super.setCurrentItem(currentIndex);
        }
        mHandler.postDelayed(mRunnableAutoNext, mDelay);
    }

    public void onPause() {
        pauseAutoNext();
    }

    public void onResume() {
        resumeAutoNext();
    }
}
