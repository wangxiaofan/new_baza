package com.baza.android.bzw.businesscontroller.company.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.bean.resume.ResumeDetailBean;
import com.baza.android.bzw.bean.resume.ResumeRecommend;
import com.baza.android.bzw.businesscontroller.company.adapter.RecommendListAdapter;
import com.baza.android.bzw.businesscontroller.floating.FloatingDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.presenter.ResumeDetailPresenter;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.bznet.android.rcbox.R;

/**
 * 推荐列表
 */
public class RecommendListFragment extends BaseFragment implements BaseBZWAdapter.IAdapterEventsListener {

    private ListView listView;

    private TextView textView;

    private RecommendListAdapter listAdapter;

    private ResumeDetailPresenter mPresenter;

    private IResumeDetailView mResumeDetailView;

    public RecommendListFragment(IResumeDetailView resumeDetailView, ResumeDetailPresenter presenter) {
        this.mResumeDetailView = resumeDetailView;
        this.mPresenter = presenter;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recommend_list;
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected void initWhenOnCreatedViews(View mRootView) {

    }

    @Override
    protected void initWhenOnActivityCreated() {
        textView = mRootView.findViewById(R.id.tv_nodata);
        listView = mRootView.findViewById(R.id.lv_recommend_list);
    }

    @Override
    protected void changedUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    protected boolean isUseDefaultStatusBarBackground() {
        return false;
    }

    public void setData() {
        ResumeDetailBean resumeDetailBean = mPresenter.getCurrentResumeData();
        if (resumeDetailBean.recommendDatas != null && resumeDetailBean.recommendDatas.size() > 0) {
            textView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            listAdapter = new RecommendListAdapter(getActivity(), resumeDetailBean.recommendDatas, this);
            listView.setAdapter(listAdapter);
        } else {
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        ResumeRecommend resumeBean = (ResumeRecommend) input;
//        FloatingDetailActivity.launch(getActivity(), resumeBean.recommendId);
        Intent intent = new Intent(getActivity(), FloatingDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("recommendId", resumeBean.recommendId);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
