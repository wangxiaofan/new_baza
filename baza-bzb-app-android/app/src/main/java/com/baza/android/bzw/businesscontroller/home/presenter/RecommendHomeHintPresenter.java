package com.baza.android.bzw.businesscontroller.home.presenter;

import android.os.Handler;
import android.os.Looper;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.recommend.RecommendBean;
import com.baza.android.bzw.businesscontroller.home.viewinterface.IHomeView;
import com.baza.android.bzw.manager.RecommendManager;
import com.bznet.android.rcbox.R;

import java.util.List;

/**
 * Created by Vincent.Lei on 2019/8/27.
 * Title：
 * Note：
 */
class RecommendHomeHintPresenter extends BasePresenter implements RecommendManager.IRecommendListener {
    private static final long LOAD_RECOMMEND_REPEAT_DISTANCE = 5 * 60 * 1000;
    private IHomeView mHomeView;
    private HomePresenter mHomePresenter;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mLoadRecommendRunnable;
    private Runnable mRecommendFloatRunnable;

    RecommendHomeHintPresenter(IHomeView homeView, HomePresenter homePresenter) {
        this.mHomeView = homeView;
        this.mHomePresenter = homePresenter;
    }


    @Override
    public void initialize() {
        RecommendManager.getInstance().registerListener(this);
        heartToLoadRecommend(0);
    }

    @Override
    public void onDestroy() {
        RecommendManager.getInstance().unRegisterListener(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onResume() {
        heartToLoadRecommend(0);
    }

    private void heartToLoadRecommend(long delayTime) {
        if (mLoadRecommendRunnable != null)
            mHandler.removeCallbacks(mLoadRecommendRunnable);
        if (mLoadRecommendRunnable == null)
            mLoadRecommendRunnable = new Runnable() {
                @Override
                public void run() {
                    RecommendManager.getInstance().refresh();
                    heartToLoadRecommend(LOAD_RECOMMEND_REPEAT_DISTANCE);
                }
            };
        mHandler.postDelayed(mLoadRecommendRunnable, delayTime);
    }

    @Override
    public void onUnCompletedRecommendOfTodayGet(List<RecommendBean> recommendList) {
        mHomePresenter.onRecommendUnProcessCountChanged(recommendList == null ? 0 : recommendList.size());
        if (recommendList == null || recommendList.isEmpty()) {
            mHomeView.callDismissRecommendFloattingView();
            return;
        }
        if (mRecommendFloatRunnable != null)
            mHandler.removeCallbacks(mRecommendFloatRunnable);
        if (mRecommendFloatRunnable == null)
            mRecommendFloatRunnable = new Runnable() {
                @Override
                public void run() {
                    mHomeView.callUpdateRecommendFloattingView(mHomeView.callGetResources().getString(R.string.recommend_float_hint));
                }
            };
        RecommendBean recommendBean;
        long timeDelay;
        long currentTime = System.currentTimeMillis();
        for (int i = 0, size = recommendList.size(); i < size; i++) {
            recommendBean = recommendList.get(i);
            timeDelay = recommendBean.getTimeMsec() - currentTime;
            if (timeDelay >= 0)
                mHandler.postDelayed(mRecommendFloatRunnable, timeDelay);
        }
    }
}
