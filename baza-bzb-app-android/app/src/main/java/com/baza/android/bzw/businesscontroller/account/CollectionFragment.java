package com.baza.android.bzw.businesscontroller.account;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.account.presenter.CollectionTypePresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.ICollectionTypeView;
import com.baza.android.bzw.businesscontroller.company.CompanyDetailActivity;
import com.baza.android.bzw.businesscontroller.company.CompanySearchActivity;
import com.baza.android.bzw.businesscontroller.company.adapter.CompanyResumeListAdapter;
import com.baza.android.bzw.businesscontroller.company.viewinterface.ICompanySearchView;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.ScrollShowImageView;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.slib.utils.AppUtil;

/**
 * Created by Vincent.Lei on 2018/8/22.
 * Title：
 * Note：
 */
public class CollectionFragment extends BaseFragment implements ICollectionTypeView, PullToRefreshBase.OnRefreshListener2, CompoundButton.OnCheckedChangeListener, View.OnClickListener, BaseBZWAdapter.IAdapterEventsListener {
    PullToRefreshListView pullToRefreshListView;
    ScrollShowImageView scrollShowImageView;
    LoadingView loadingView;
    TextView textView_countInfo;
    ListView listView;

    private CollectionTypePresenter mPresenter;
    private CompanyResumeListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.collection_fragment_type;
    }

    @Override
    protected String getPageTitle() {
        return "我的收藏";
    }

    @Override
    protected void initWhenOnCreatedViews(View mRootView) {
        mPresenter = new CollectionTypePresenter(this);
        loadingView = mRootView.findViewById(R.id.loading_view);
        pullToRefreshListView = mRootView.findViewById(R.id.pull_to_refresh_listView);
        listView = pullToRefreshListView.getRefreshableView();
        scrollShowImageView = mRootView.findViewById(R.id.scroll_show_image_view);
        View view_emptyView = mRootView.findViewById(R.id.view_empty_view);
        scrollShowImageView.setOnClickListener(this);

        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setEmptyView(view_emptyView);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                scrollShowImageView.onScroll(firstVisibleItem, visibleItemCount);
            }
        });
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.loadCollectionList(true, true);
            }
        });

        View headView = getLayoutInflater().inflate(R.layout.head_view_inner_search, null);
        listView.addHeaderView(headView);
        textView_countInfo = headView.findViewById(R.id.tv_count_info);
        ((CheckBox) headView.findViewById(R.id.cb_job_hunter)).setOnCheckedChangeListener(this);
        headView.findViewById(R.id.fl_search_entrance).setOnClickListener(this);
        ((TextView) headView.findViewById(R.id.tv_do_search)).setHint(R.string.collection_search_hint);

        mAdapter = new CompanyResumeListAdapter(getActivity(), mPresenter.getDataList(), true, false, this);
        listView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    protected void initWhenOnActivityCreated() {
        mPresenter.initialize();
    }

    @Override
    protected void changedUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    protected boolean isStatusBarTintEnabledWhenSDKReachKITKAT() {
        return false;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadCollectionList(true, false);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadCollectionList(false, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scroll_show_image_view:
                listView.setSelection(0);
                break;
            case R.id.fl_search_entrance:
                CompanySearchActivity.launch(getActivity(), new ICompanySearchView.Param().searchMode(CommonConst.INT_SEARCH_TYPE_COLLECTION));
                break;
        }
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
    public void callRefreshListViews(int targetPosition, int totalCount) {
        if (targetPosition == CommonConst.LIST_POSITION_NONE)
            mAdapter.notifyDataSetChanged();
        else {
            View view = AppUtil.getTargetVisibleView(targetPosition, listView);
            if (view != null)
                mAdapter.refreshItem(view, targetPosition);
        }
    }

    @Override
    public void callUpdateLoadAllDataView(boolean hasLoadAll) {
        pullToRefreshListView.setFootReboundInsteadLoad(hasLoadAll);
    }

    @Override
    public void callShowLoadingView(String msg) {
        loadingView.startLoading(null);
    }

    @Override
    public void callUpdateSearchCountView(int totalCount, int extraCount) {
        String totalStr = FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(totalCount, false, true);
        String jobHunterStr = FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(extraCount, false, true);
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.search_result_count_mine_hint_value, totalStr, jobHunterStr));
        int color = mResources.getColor(R.color.text_color_orange_FF7700);
        spannableString.setSpan(new ForegroundColorSpan(color), 1, 1 + totalStr.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(color), spannableString.length() - 1 - jobHunterStr.length(), spannableString.length() - 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_countInfo.setText(spannableString);
    }

    //    @Override
//    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
//        switch (adapterEventId) {
//            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL:
//                ResumeBean resumeBean = (ResumeBean) input;
//                ResumeDetailActivity.launch(getActivity(), new IResumeDetailView.IntentParam(resumeBean.candidateId));
//                UserInfoManager.getInstance().cacheReadResume(resumeBean.candidateId);
//                callRefreshListViews(position, CommonConst.LIST_POSITION_NONE);
//                break;
//            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_COLLECTION:
//                mPresenter.collection(position);
//                break;
//            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_ADD_REMARK:
//                resumeBean = (ResumeBean) input;
//                ResumeDetailActivity.launch(getActivity(), new IResumeDetailView.IntentParam(resumeBean.candidateId).isAddRemarkMode(true));
//                break;
//
//            case AdapterEventIdConst.RESUME_LIST_SINGLE__UPDATE:
//                ResumeUpdateCardActivity.launch(getActivity(), ((ResumeBean) input).candidateId, RequestCodeConst.INT_REQUEST_NONE);
//                break;
//        }
//    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        ResumeBean resumeBean = (ResumeBean) input;
        switch (adapterEventId) {
            case AdapterEventIdConst.EVENT_ID_COMPANY_ITEM_CLICK:
                if (TextUtils.isEmpty(resumeBean.firmId)) {
                    ResumeDetailActivity.launch(getActivity(), new IResumeDetailView.IntentParam(resumeBean.candidateId).pageCode("Collection"));
                } else {
                    CompanyDetailActivity.launch(getActivity(), new IResumeDetailView.IntentParam(resumeBean.candidateId).isCompany(true).pageCode("Collection"));
                }
                UserInfoManager.getInstance().cacheReadResume(resumeBean.candidateId);
                callRefreshListViews(position, 0);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPresenter.onJobHunterChanged(isChecked);
    }

    @Override
    protected void onFragmentDeadForApp() {
        super.onFragmentDeadForApp();
        mAdapter.onDestroy();
    }
}
