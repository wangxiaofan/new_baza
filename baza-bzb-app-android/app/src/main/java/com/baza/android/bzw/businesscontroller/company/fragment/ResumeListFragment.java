package com.baza.android.bzw.businesscontroller.company.fragment;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.businesscontroller.resume.detail.FootViewExtraInfoUI;
import com.baza.android.bzw.businesscontroller.resume.detail.HeadViewDetailInfoUI;
import com.baza.android.bzw.businesscontroller.resume.detail.presenter.ResumeDetailPresenter;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.bznet.android.rcbox.R;

/**
 * 简历列表
 */
public class ResumeListFragment extends BaseFragment {

    private HeadViewDetailInfoUI mHeadViewDetailInfoUI;

//    private FootViewExtraInfoUI mFootViewExtraInfoUI;

    private ResumeDetailPresenter mPresenter;

    private IResumeDetailView mResumeDetailView;

    private LinearLayout flContent;

    public ResumeListFragment(IResumeDetailView resumeDetailView, ResumeDetailPresenter presenter) {
        this.mResumeDetailView = resumeDetailView;
        this.mPresenter = presenter;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_resume_list;
    }


    @Override
    protected String getPageTitle() {
        return "";
    }

    @Override
    protected void initWhenOnCreatedViews(View mRootView) {
        flContent = mRootView.findViewById(R.id.fl_content);
        //主要信息headView
        mHeadViewDetailInfoUI = new HeadViewDetailInfoUI(mResumeDetailView, mPresenter);
        flContent.addView(mHeadViewDetailInfoUI.getHeadView());
        //简历原文 添加备注按钮
//        mFootViewExtraInfoUI = new FootViewExtraInfoUI(mResumeDetailView, mPresenter);
//        flContent.addView(mFootViewExtraInfoUI.getFootView());
    }

    @Override
    protected void initWhenOnActivityCreated() {

    }

    @Override
    protected void changedUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    protected boolean isUseDefaultStatusBarBackground() {
        return false;
    }

    public void setData() {
        mHeadViewDetailInfoUI.setData();
//        mFootViewExtraInfoUI.setData();
    }
}
