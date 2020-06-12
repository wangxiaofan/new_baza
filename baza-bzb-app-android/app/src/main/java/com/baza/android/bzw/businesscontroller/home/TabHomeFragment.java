package com.baza.android.bzw.businesscontroller.home;

import android.content.Intent;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.find.updateengine.ResumeUpdateCardActivity;
import com.baza.android.bzw.businesscontroller.find.updateengine.adapter.UpdateResumeListAdapter;
import com.baza.android.bzw.businesscontroller.home.presenter.TabHomePresenter;
import com.baza.android.bzw.businesscontroller.home.viewinterface.ITabHomeView;
import com.baza.android.bzw.businesscontroller.publish.util.QRCodeUtil;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.slib.utils.AppUtil;

/**
 * Created by Vincent.Lei on 2017/9/27.
 * Title：
 * Note：
 */

public class TabHomeFragment extends BaseFragment implements ITabHomeView, BaseBZWAdapter.IAdapterEventsListener, PullToRefreshBase.OnRefreshListener2 {
    PullToRefreshListView pullToRefreshListView;
    ListView listView;
    View view_statusBar;
    View view_empty;
    private TabHomePresenter mPresenter;

    private TabHomeHeadView mTabHomeHeadView;
    private UpdateResumeListAdapter mAdapter;
    private int[] mLocationOnScreen = new int[2];

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_home;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_home_tab);
    }

    @Override
    protected boolean isUseDefaultStatusBarBackground() {
        return false;
    }

    @Override
    protected void initWhenOnCreatedViews(View mRootView) {
        mPresenter = new TabHomePresenter(this);
        pullToRefreshListView = mRootView.findViewById(R.id.pull_to_refresh_listView);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        view_statusBar = mRootView.findViewById(R.id.view_status_bar);
        pullToRefreshListView.setHeadReboundInsteadRefresh(true);
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setOnRefreshListener(this);
        listView = pullToRefreshListView.getRefreshableView();
        mTabHomeHeadView = new TabHomeHeadView(this, mPresenter);
        listView.addHeaderView(mTabHomeHeadView.getRootView());
        mAdapter = new UpdateResumeListAdapter(getActivity(), mPresenter.getEnableUpdateList(), true, this);
        listView.setAdapter(mAdapter);
        pullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view_statusBar.getHeight() > 0) {
                    mTabHomeHeadView.viewPager_banner.getLocationOnScreen(mLocationOnScreen);
                    if (mLocationOnScreen[1] == 0) {
                        view_statusBar.setAlpha(view_statusBar.getAlpha() > 0.6f ? 1.0f : 0.0f);
                        return;
                    }
                    float alpha = Math.abs(mLocationOnScreen[1]) * 1.0f / mTabHomeHeadView.viewPager_banner.getMeasuredHeight();
                    alpha = (alpha > 0.9f ? 1.0f : (alpha < 0.1f ? 0.0f : alpha));
                    view_statusBar.setAlpha(alpha);
                }

            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mPresenter != null)
            mPresenter.onHiddenChanged(hidden);
        mTabHomeHeadView.onHiddenChanged(hidden);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onPause();
        mTabHomeHeadView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null)
            mPresenter.onResume();
        mTabHomeHeadView.onResume();
    }

    @Override
    protected void onFragmentDeadForApp() {
        super.onFragmentDeadForApp();
        if (mPresenter != null)
            mPresenter.onDestroy();
        mAdapter.onDestroy();
    }

    @Override
    protected void initWhenOnActivityCreated() {
        mPresenter.initialize();
    }

    @Override
    protected void changedUIToFitSDKReachKITKAT(int statusBarHeight) {
        ViewGroup.LayoutParams lp = view_statusBar.getLayoutParams();
        lp.height = statusBarHeight;
        view_statusBar.setLayoutParams(lp);
        view_statusBar.setVisibility(View.VISIBLE);
        if (mTabHomeHeadView != null)
            mTabHomeHeadView.changedUIToFitSDKReachKITKAT(statusBarHeight);
    }

    @Override
    public void callUpdateSuggestFriendView() {
        mTabHomeHeadView.updateSuggestFriendView();
    }

    @Override
    public void callRefreshListItems(int targetPosition) {
        pullToRefreshListView.onRefreshComplete();
        if (targetPosition == CommonConst.LIST_POSITION_NONE) {
            mAdapter.notifyDataSetChanged();
            return;
        }
        View view = AppUtil.getTargetVisibleView(targetPosition, listView);
        if (view != null)
            mAdapter.refreshItem(view, targetPosition);
    }

    @Override
    public void callUpdateBannerView() {
        mTabHomeHeadView.updateBannerView();
    }

    @Override
    public void callUpdateLoadMoreEnable(boolean enable) {
        pullToRefreshListView.setFootReboundInsteadLoad(!enable);
    }

    @Override
    public void callSetNoResultHintView(boolean noResult) {
        if (noResult) {
            if (view_empty == null) {
                view_empty = getLayoutInflater().inflate(R.layout.empty_view_mine_candidate, null);
//                view_empty.findViewById(R.id.tv_import_email_resume).setOnClickListener(this);
                TextView textView_pcHint = view_empty.findViewById(R.id.tv_pc_hint);
                SpannableString spannableString = new SpannableString(mResources.getString(R.string.hint_pc_import_resume));
                spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_blue_53ABD5)), 2, 15, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView_pcHint.setText(spannableString);
            }
            if (view_empty.getTag() == null) {
                listView.addFooterView(view_empty);
                view_empty.setTag(noResult);
            }
        } else if (view_empty != null) {
            listView.removeFooterView(view_empty);
            view_empty.setTag(null);
        }
    }

    @Override
    public void onFragmentResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == QRCodeUtil.INT_REQUEST_CODE_QR_CODE) {
            mPresenter.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    QRCodeUtil.parseQRCodeResult((BaseActivity) getActivity(), resultCode, data);
                }
            }, 500);
        }
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL:
                ResumeBean resumeBean = (ResumeBean) input;
                ResumeDetailActivity.launch(getActivity(), new IResumeDetailView.IntentParam(resumeBean.candidateId));
                UserInfoManager.getInstance().cacheReadResume(resumeBean.candidateId);
                callRefreshListItems(position);
                break;
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_COLLECTION:
                mPresenter.collection(position);
                break;
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_ADD_REMARK:
                resumeBean = (ResumeBean) input;
                ResumeDetailActivity.launch(getActivity(), new IResumeDetailView.IntentParam(resumeBean.candidateId).isAddRemarkMode(true));
                break;
            case AdapterEventIdConst.RESUME_LIST_SINGLE__UPDATE:
                ResumeUpdateCardActivity.launch(getActivity(), ((ResumeBean) input).candidateId, RequestCodeConst.INT_REQUEST_NONE);
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadResumeEnableUpdateList(false);
    }
}
