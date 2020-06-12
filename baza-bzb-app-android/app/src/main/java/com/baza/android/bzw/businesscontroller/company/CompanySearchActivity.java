package com.baza.android.bzw.businesscontroller.company;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.company.adapter.CompanyResumeListAdapter;
import com.baza.android.bzw.businesscontroller.company.presenter.CompanySearchPresenter;
import com.baza.android.bzw.businesscontroller.company.viewinterface.ICompanySearchView;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.businesscontroller.search.EditSearchConfigActivity;
import com.baza.android.bzw.businesscontroller.search.NewSearchFilterUI;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IEditSearchConfigView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CompanySearchActivity extends BaseActivity implements ICompanySearchView, PullToRefreshBase.OnRefreshListener2, BaseBZWAdapter.IAdapterEventsListener {

    @BindView(R.id.tv_do_search)
    TextView tvDoSearch;
    @BindView(R.id.tv_cancle)
    TextView tvCancle;
    @BindView(R.id.new_search_filter_ui)
    LinearLayout searchFilterUi;
    @BindView(R.id.view_title_bar)
    LinearLayout viewTitleBar;
    @BindView(R.id.tv_no_look)
    CheckBox tvNoLook;
    @BindView(R.id.tv_empty_num)
    CheckBox tvEmptyNum;
    @BindView(R.id.tv_find_job)
    CheckBox tvFindJob;
    @BindView(R.id.tv_good_school)
    CheckBox tvGoodSchool;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.pull_to_refresh_listView)
    PullToRefreshListView pullToRefreshListView;
    @BindView(R.id.ll_company_filter)
    LinearLayout llCompanyFilter;

    CompanySearchPresenter mPresenter;
    NewSearchFilterUI newSearchFilterUI;
    View view_noSearchResultHint;
    ListView listView;
    CompanyResumeListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_company_search;
    }

    @Override
    protected String getPageTitle() {
        return "企业人才库搜索结果";
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        mPresenter = new CompanySearchPresenter(this, (Param) getIntent().getSerializableExtra("param"));
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
        newSearchFilterUI = new NewSearchFilterUI(findViewById(R.id.new_search_filter_ui), this, mPresenter);
        newSearchFilterUI.init();

        if (mPresenter.getSearchMode() == CommonConst.INT_SEARCH_TYPE_COMPANY) {
            llCompanyFilter.setVisibility(View.VISIBLE);
            mAdapter = new CompanyResumeListAdapter(this, mPresenter.getResumeList(), false, true, this);
            mPresenter.getResumeSearchLogger().setPageCode(this, "FirmTalentSearh");
        } else if (mPresenter.getSearchMode() == CommonConst.INT_SEARCH_TYPE_COLLECTION) {
            mAdapter = new CompanyResumeListAdapter(this, mPresenter.getResumeList(), true, false, this);
            mPresenter.getResumeSearchLogger().setPageCode(this, "CollectionSearh");
        }
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
        Intent intent = new Intent(activity, CompanySearchActivity.class);
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
        if (mPresenter.getSearchFilterInfo().schoolParameter == 3) {
            tvGoodSchool.setChecked(true);
        } else {
            tvGoodSchool.setChecked(false);
        }
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
    public void callIsJobHunting(boolean isJobHunting) {
        tvFindJob.setChecked(isJobHunting);
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

    @OnClick({R.id.tv_do_search, R.id.tv_cancle, R.id.tv_no_look, R.id.tv_empty_num, R.id.tv_find_job, R.id.tv_good_school})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancle:
                finish();
                break;

            case R.id.tv_no_look:
                mPresenter.notScan(true);
                break;

            case R.id.tv_empty_num:
                mPresenter.isExcludeEmptyMobile(true);
                break;

            case R.id.tv_find_job:
                mPresenter.isJobHunting();
                break;

            case R.id.tv_good_school:
                mPresenter.goodSchool();
                break;

            case R.id.tv_do_search:
                newSearchFilterUI.closeFilterPane();
                EditSearchConfigActivity.launch(RequestCodeConst.INT_REQUEST_SET_SEARCH_CONDITION, this,
                        IEditSearchConfigView.MODE_COMPANY_SEARCH_HISTORY, mPresenter.getSearchMode(), mPresenter.getLastKeyword());
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
        ResumeBean resumeBean = (ResumeBean) input;
        switch (adapterEventId) {
            case AdapterEventIdConst.EVENT_ID_COMPANY_ITEM_CLICK:
                if (TextUtils.isEmpty(resumeBean.firmId)) {
                    ResumeDetailActivity.launch(this, new IResumeDetailView.IntentParam(resumeBean.candidateId)
                            .pageCode(mPresenter.getSearchMode() == CommonConst.INT_SEARCH_TYPE_COMPANY ? "FirmTalentSearch" : "CollectionSearch"));
                } else {
                    CompanyDetailActivity.launch(this, new IResumeDetailView.IntentParam(resumeBean.candidateId).isCompany(true)
                            .pageCode(mPresenter.getSearchMode() == CommonConst.INT_SEARCH_TYPE_COMPANY ? "FirmTalentSearch" : "CollectionSearch"));
                }
                UserInfoManager.getInstance().cacheReadResume(resumeBean.candidateId);
                callRefreshListItems(position, 0);
                mPresenter.getResumeSearchLogger().sendSearchResultClickLog(CompanySearchActivity.this, resumeBean.candidateId);
                break;

            case AdapterEventIdConst.EVENT_ID_COMPANY_ITEM_COLLECTION:
                mPresenter.collection(position, resumeBean);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!newSearchFilterUI.shouldHideFilterPane())
            super.onBackPressed();
    }
}
