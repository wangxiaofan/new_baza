package com.baza.android.bzw.businesscontroller.tracking;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.TrackingListBean;
import com.baza.android.bzw.businesscontroller.company.CompanyDetailActivity;
import com.baza.android.bzw.businesscontroller.company.presenter.CompanyTalentPresenter;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.businesscontroller.tracking.adapter.TrackingListAdapter;
import com.baza.android.bzw.businesscontroller.tracking.presenter.TrackingPresenter;
import com.baza.android.bzw.businesscontroller.tracking.viewinterface.ITrackingSearchView;
import com.baza.android.bzw.businesscontroller.tracking.viewinterface.ITrackingView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.manager.UserInfoManager;
import com.baza.android.bzw.widget.LoadingView;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.slib.utils.AppUtil;
import com.slib.utils.ToastUtil;

public class TrackingActivity extends BaseActivity implements View.OnClickListener, ITrackingView, BaseBZWAdapter.IAdapterEventsListener, PullToRefreshBase.OnRefreshListener2 {

    private PullToRefreshListView pullToRefreshListView;

    private LoadingView loadingView;

    private ListView listView;

    private View view_empty;

    private TrackingPresenter mPresenter;

    private TrackingListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tracking;
    }

    @Override
    protected String getPageTitle() {
        return "tracking";
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new TrackingPresenter(this);
        TextView textView_title = findViewById(R.id.tv_title);
        textView_title.setText(R.string.title_trackinglist);
        loadingView = findViewById(R.id.loading_view);
        pullToRefreshListView = findViewById(R.id.pull_to_refresh_listView);
        listView = pullToRefreshListView.getRefreshableView();
        View view_emptyView = findViewById(R.id.view_empty_view);
        View headView = getLayoutInflater().inflate(R.layout.head_view_inner_company_talent, null);
        listView.addHeaderView(headView);
        headView.findViewById(R.id.fl_search_entrance).setOnClickListener(this);
        ((TextView) headView.findViewById(R.id.tv_do_search)).setHint("搜索TrackingList");

        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setEmptyView(view_emptyView);
        pullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.loadMyResumeList(true);
            }
        });

        mAdapter = new TrackingListAdapter(this, mPresenter.getDataList(), this);
        listView.setAdapter(mAdapter);
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, TrackingActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.fl_search_entrance:
                TrackingSearchActivity.launch(this, new ITrackingSearchView.Param().searchMode(CommonConst.INT_SEARCH_TYPE_TRACKING));
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadMyResumeList(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadMyResumeList(false);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        TrackingListBean resumeBean = (TrackingListBean) input;
        switch (adapterEventId) {
            case AdapterEventIdConst.EVENT_ID_COMPANY_ITEM_CLICK:
                if (TextUtils.isEmpty(resumeBean.getFirmId())) {
                    ResumeDetailActivity.launch(this, new IResumeDetailView.IntentParam(resumeBean.getResumeId()).pageCode("TrackingList"));
                } else {
                    CompanyDetailActivity.launch(this, new IResumeDetailView.IntentParam(resumeBean.getResumeId()).isCompany(true).pageCode("TrackingList"));
                }
                UserInfoManager.getInstance().cacheReadResume(resumeBean.getResumeId());
                callRefreshListItems(position);
                break;
        }
    }

    @Override
    public void callUpdateLoadAllDataView(boolean hasLoadAll) {
        pullToRefreshListView.setFootReboundInsteadLoad(hasLoadAll);
    }

    @Override
    public void callRefreshListItems(int targetPosition) {
        if (targetPosition == CommonConst.LIST_POSITION_NONE) {
            mAdapter.notifyDataSetChanged();
            return;
        }
        View view = AppUtil.getTargetVisibleView(targetPosition, listView);
        if (view != null)
            mAdapter.refreshItem(view, targetPosition);
    }

    @Override
    public void callCancelLoadingView(boolean success, int errorCode, String errorMsg) {
        pullToRefreshListView.onRefreshComplete();
        if (!loadingView.isShownVisibility())
            return;
        if (success)
            loadingView.finishLoading();
        else
            loadingView.loadingFailed(errorCode, errorMsg);
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
    public void callShowSpecialToastMsg(int type, String msg, int msgId) {
        switch (type) {
            case CompanyTalentPresenter.SELF_TOAST_COLLECTION:
                ImageView imageView = new ImageView(mApplication);
                imageView.setImageResource((R.string.un_collection_success == msgId ? R.drawable.image_uncollection : R.drawable.image_collection));
                ToastUtil.selfToast(mApplication, imageView);
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
