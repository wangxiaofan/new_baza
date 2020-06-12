package com.baza.android.bzw.businesscontroller.tracking;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.BubbleBean;
import com.baza.android.bzw.bean.resume.TrackingListBean;
import com.baza.android.bzw.businesscontroller.company.CompanyDetailActivity;
import com.baza.android.bzw.businesscontroller.company.presenter.CompanySearchPresenter;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.businesscontroller.search.EditSearchConfigActivity;
import com.baza.android.bzw.businesscontroller.search.TrackingSearchFilterUI;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IEditSearchConfigView;
import com.baza.android.bzw.businesscontroller.tracking.adapter.BubbleAdapter;
import com.baza.android.bzw.businesscontroller.tracking.adapter.TrackingListAdapter;
import com.baza.android.bzw.businesscontroller.tracking.presenter.TrackingSearchPresenter;
import com.baza.android.bzw.businesscontroller.tracking.viewinterface.ITrackingSearchView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.manager.UserInfoManager;
import com.baza.android.bzw.widget.LoadingView;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.slib.utils.AppUtil;
import com.slib.utils.ToastUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrackingSearchActivity extends BaseActivity implements ITrackingSearchView, PullToRefreshBase.OnRefreshListener2, BaseBZWAdapter.IAdapterEventsListener {

    @BindView(R.id.tv_do_search)
    TextView tvDoSearch;
    @BindView(R.id.tv_cancle)
    TextView tvCancle;
    @BindView(R.id.new_search_filter_ui)
    LinearLayout searchFilterUi;
    @BindView(R.id.view_title_bar)
    LinearLayout viewTitleBar;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.pull_to_refresh_listView)
    PullToRefreshListView pullToRefreshListView;
    @BindView(R.id.rv_bubble)
    RecyclerView rvBubble;

    TrackingSearchPresenter mPresenter;
    TrackingSearchFilterUI newSearchFilterUI;
    View view_noSearchResultHint;
    ListView listView;
    TrackingListAdapter mAdapter;
    HashMap<Integer, String> filterList = new HashMap<>();
    BubbleAdapter bubbleAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tracking_search;
    }

    @Override
    protected String getPageTitle() {
        return "企业人才库搜索结果";
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        mPresenter = new TrackingSearchPresenter(this, (Param) getIntent().getSerializableExtra("param"));
        initNoSearchResultView();
        listView = pullToRefreshListView.getRefreshableView();
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.doSearch(true);
            }
        });
        pullToRefreshListView.setHeadReboundInsteadRefresh(true);
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setOnRefreshListener(this);
        newSearchFilterUI = new TrackingSearchFilterUI(findViewById(R.id.new_search_filter_ui), this, mPresenter);
        newSearchFilterUI.init();
        String[] items = getResources().getStringArray(R.array.tracking_search_filter_type);
        for (int i = 0; i < items.length; i++) {
            filterList.put(i, items[i]);
        }
        rvBubble.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        bubbleAdapter = new BubbleAdapter(this, mPresenter);
        rvBubble.setAdapter(bubbleAdapter);
        mAdapter = new TrackingListAdapter(this, mPresenter.getResumeList(), this);
        listView.setAdapter(mAdapter);
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    public static void launch(Activity activity, Param param) {
        launch(activity, param, RequestCodeConst.INT_REQUEST_NONE);
    }

    public static void launch(Activity activity, Param param, int requestCode) {
        Intent intent = new Intent(activity, TrackingSearchActivity.class);
        if (param != null) {
            if (param.formComponentName == null)
                param.formComponentName = activity.getClass().getName();
            intent.putExtra("param", param);
        }
        if (requestCode > RequestCodeConst.INT_REQUEST_NONE)
            activity.startActivityForResult(intent, requestCode);
        else
            activity.startActivity(intent);
    }

    @Override
    public void callUpdateSearchResultHint(int resultCount, int extraResultCount, boolean showTopView, boolean isJobFinderEntranceEnable) {
        view_noSearchResultHint.setVisibility((resultCount > 0 ? View.GONE : View.VISIBLE));
    }

    @Override
    public void callSearch() {
        mPresenter.doSearch(true);
    }

    @Override
    public String callGetKeyWord() {
        return tvDoSearch.getText().toString().trim();
    }

    @Override
    public void callUpdateKeywordView(String keyWord) {
        tvDoSearch.setText(keyWord);
    }

    @Override
    public void callUpdateKeywordHint(int keyWordHint) {
        tvDoSearch.setHint(keyWordHint);
    }

    @Override
    public void callUpdateSearchFilterView() {
        newSearchFilterUI.makePreviousDataWhenFilterChangeOutside();
    }

    @Override
    public void callSetLabelLibrary() {
        newSearchFilterUI.setLabelLibs();
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
    public void callShowLoadingView(String msg) {
        loadingView.startLoading(msg);
    }

    @Override
    public void callRefreshListItems(int targetPosition, int selection) {
        if (targetPosition == CommonConst.LIST_POSITION_NONE) {
            mAdapter.notifyDataSetChanged();
            if (selection >= 0)
                listView.setSelection(selection);
            return;
        }
        View view = AppUtil.getTargetVisibleView(targetPosition, listView);
        if (view != null)
            mAdapter.refreshItem(view, targetPosition);
    }

    @Override
    public void callUpdateLoadAllDataView(boolean hasLoadAll) {
        pullToRefreshListView.setFootReboundInsteadLoad(hasLoadAll);
    }

    @Override
    public void callShowSpecialToastMsg(int type, String msg, int msgId) {
        switch (type) {
            case CompanySearchPresenter.SELF_TOAST_COLLECTION:
                ImageView imageView = new ImageView(mApplication);
                imageView.setImageResource((R.string.un_collection_success == msgId ? R.drawable.image_uncollection : R.drawable.image_collection));
                ToastUtil.selfToast(mApplication, imageView);
                break;
        }
    }

    @Override
    public void callShowBubbleData() {
        if (mPresenter.getBubbleList().size() > 0) {
            rvBubble.setVisibility(View.VISIBLE);
            for (BubbleBean bean : mPresenter.getBubbleList()) {
                bean.setFilterName(filterList.get(Integer.valueOf(bean.getFilterType())) + "(" + bean.getCount() + ")");
            }
            bubbleAdapter.notifyDataSetChanged();
        } else {
            rvBubble.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeConst.INT_REQUEST_SET_SEARCH_CONDITION) {
            if (resultCode == RESULT_OK && data != null)
                mPresenter.parseSearchCondition(data);
            else if (mPresenter.hasBackTagForNeverSearch())
                finish();
        }
    }

    /**
     * 初始化未搜索到结果视图
     */
    private void initNoSearchResultView() {
        view_noSearchResultHint = findViewById(R.id.view_no_result);
        //没有搜到结果提示语高亮显示(换个关键字或点开高级筛选试试吧！关键字、高级筛选高亮)
        SpannableStringBuilder ssb = new SpannableStringBuilder(mResources.getString(R.string.input_search_key_to_search));
        int colorHighLight = mResources.getColor(R.color.text_color_blue_53ABD5);
        ssb.setSpan(new ForegroundColorSpan(colorHighLight), 2, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new ForegroundColorSpan(colorHighLight), 8, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView) view_noSearchResultHint.findViewById(R.id.tv_sub_title)).setText(ssb);
    }

    @OnClick({R.id.tv_do_search, R.id.tv_cancle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancle:
                finish();
                break;

            case R.id.tv_do_search:
                newSearchFilterUI.closeFilterPane();
                EditSearchConfigActivity.launch(RequestCodeConst.INT_REQUEST_SET_SEARCH_CONDITION, this,
                        IEditSearchConfigView.MODE_TRACKING_LIST_HISTORY, mPresenter.getSearchMode(), mPresenter.getLastKeyword());
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.doSearch(false);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        TrackingListBean resumeBean = (TrackingListBean) input;
        switch (adapterEventId) {
            case AdapterEventIdConst.EVENT_ID_COMPANY_ITEM_CLICK:
                if (TextUtils.isEmpty(resumeBean.getFirmId())) {
                    ResumeDetailActivity.launch(this, new IResumeDetailView.IntentParam(resumeBean.getResumeId()).pageCode("TrackingListSearch"));
                } else {
                    CompanyDetailActivity.launch(this, new IResumeDetailView.IntentParam(resumeBean.getResumeId()).isCompany(true).pageCode("TrackingListSearch"));
                }
                UserInfoManager.getInstance().cacheReadResume(resumeBean.getResumeId());
                callRefreshListItems(position, 0);
                mPresenter.getResumeSearchLogger().sendSearchResultClickLog(this, resumeBean.getResumeId());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!newSearchFilterUI.shouldHideFilterPane())
            super.onBackPressed();
    }
}
