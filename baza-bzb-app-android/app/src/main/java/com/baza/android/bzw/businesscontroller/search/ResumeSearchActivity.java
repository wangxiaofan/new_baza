package com.baza.android.bzw.businesscontroller.search;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter.IAdapterEventsListener;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.find.updateengine.ResumeUpdateCardActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.businesscontroller.search.adapter.SearchResumeListAdapter;
import com.baza.android.bzw.businesscontroller.search.presenter.ResumeSearchPresenter;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IResumeSearchView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.manager.UserInfoManager;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.ScrollShowImageView;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.slib.utils.AppUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/5/22.
 * Title：简历搜索
 * Note：
 */

public class ResumeSearchActivity extends BaseActivity implements IResumeSearchView, CompoundButton.OnCheckedChangeListener, View.OnClickListener, IAdapterEventsListener, PullToRefreshBase.OnRefreshListener2 {
    @BindView(R.id.pull_to_refresh_listView)
    PullToRefreshListView pullToRefreshListView;
    @BindView(R.id.scroll_show_image_view)
    ScrollShowImageView scrollShowImageView;
    @BindView(R.id.tv_do_search)
    TextView textView_search;
    @BindView(R.id.tv_all_selected)
    TextView textView_allSelected;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    ListView listView;
    View view_searchFilterOperate;
    TextView textView_searchCount;
    CheckBox checkBox_onlySearchJobFinder;
    //    CheckBox checkBox_nameListReceive;
    View view_noSearchResultHint;

    private SearchResumeListAdapter mAdapter;
    private ResumeSearchPresenter mPresenter;
    private SearchFilterUI mSearchFilterUI;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_candidate_search;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_candidate_search);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new ResumeSearchPresenter(this, (Param) getIntent().getSerializableExtra("param"));
        ButterKnife.bind(this);
        initNoSearchResultView();
        listView = pullToRefreshListView.getRefreshableView();
        //搜索结果以及条件操作view
        if (mPresenter.getSearchMode() == CommonConst.INT_SEARCH_TYPE_GROUP_ADD) {
            findViewById(R.id.ll_group_add).setVisibility(View.VISIBLE);
            updateGroupAddAllSelectedCount(0);
        }
        view_searchFilterOperate = getLayoutInflater().inflate(R.layout.head_view_filter_result_operate, null);
        textView_searchCount = view_searchFilterOperate.findViewById(R.id.tv_search_count);
        checkBox_onlySearchJobFinder = view_searchFilterOperate.findViewById(R.id.cb_job_hunter);
//        checkBox_nameListReceive = view_searchFilterOperate.findViewById(R.id.cb_name_List);
//        checkBox_nameListReceive.setVisibility((mPresenter.isMineSearch() || !UserInfoManager.getInstance().hasCFTag()) ? View.GONE : View.VISIBLE);
        checkBox_onlySearchJobFinder.setOnCheckedChangeListener(this);
//        checkBox_nameListReceive.setOnCheckedChangeListener(this);
        callUpdateSearchResultHint(0, 0, false, false);

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.addView(view_searchFilterOperate, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        listView.addHeaderView(frameLayout);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.doSearch(true);
            }
        });
        pullToRefreshListView.setHeadReboundInsteadRefresh(true);
        pullToRefreshListView.setFootReboundInsteadLoad(true);
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

        mSearchFilterUI = new SearchFilterUI(findViewById(R.id.search_filter_ui), this, mPresenter);
        mSearchFilterUI.init();
        mAdapter = new SearchResumeListAdapter(this, mPresenter.getSearchMode(), mPresenter.isSortByCreateTime(), mPresenter.getResumeList(), mPresenter.getSelectedIdSetForGroupAdd(), this);
        listView.setAdapter(mAdapter);

        mPresenter.initialize();
    }

    @Override
    protected void onActivityDeadForApp() {
        if (mPresenter != null)
            mPresenter.onDestroy();
        if (mAdapter != null)
            mAdapter.onDestroy();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

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

    public static void launch(Activity activity, Param param) {
        launch(activity, param, RequestCodeConst.INT_REQUEST_NONE);
    }

    public static void launch(Activity activity, Param param, int requestCode) {
        Intent intent = new Intent(activity, ResumeSearchActivity.class);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                onBackPressed();
                break;
            case R.id.tv_do_search:
                mSearchFilterUI.closeFilterPane();
                EditSearchConfigActivity.launch(RequestCodeConst.INT_REQUEST_SET_SEARCH_CONDITION, this, mPresenter.getSearchMode(), mPresenter.getLastKeyword());
                break;
            case R.id.scroll_show_image_view:
                listView.setSelection(0);
                break;
            case R.id.tv_all_selected:
                mPresenter.addCurrentAllResumesToGroup();
                updateGroupAddAllSelectedCount(mPresenter.getSelectedIdSetForGroupAdd().size());
                break;
            case R.id.tv_add_resume:
                mPresenter.submitResumesToGroup();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodeConst.INT_REQUEST_SET_SEARCH_CONDITION) {
            if (resultCode == RESULT_OK && data != null)
                mPresenter.parseSearchCondition(data);
            else if (mPresenter.hasBackTagForNeverSearch())
                finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mSearchFilterUI.shouldHideFilterPane())
            super.onBackPressed();
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, final Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL:
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_REQUEST_SHARE:
                ResumeBean resumeBean = (ResumeBean) input;
                ResumeDetailActivity.launch(this, new IResumeDetailView.IntentParam(resumeBean.candidateId).pageCode("MyTalentSearch"));
                UserInfoManager.getInstance().cacheReadResume(resumeBean.candidateId);
                callRefreshListItems(position, CommonConst.LIST_POSITION_NONE);
                mPresenter.getResumeSearchLogger().sendSearchResultClickLog(this, position, CommonConst.DEFAULT_PAGE_SIZE, resumeBean.candidateId);
                break;
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_COLLECTION:
                mPresenter.collection(position);
                break;
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_ADD_REMARK:
                resumeBean = (ResumeBean) input;
                ResumeDetailActivity.launch(this, new IResumeDetailView.IntentParam(resumeBean.candidateId).isAddRemarkMode(true));
                break;

            case AdapterEventIdConst.RESUME_LIST_SINGLE__UPDATE:
                ResumeUpdateCardActivity.launch(this, ((ResumeBean) input).candidateId, RequestCodeConst.INT_REQUEST_NONE);
                break;
            case AdapterEventIdConst.ADAPTER_EVENT_ADD_RESUMNE_TO_GROUP:
                updateGroupAddAllSelectedCount((int) input);
                break;
        }
    }

    /**
     * 更新搜索到的简历数提醒
     */
    @Override
    public void callUpdateSearchResultHint(int resultCount, int extraResultCount, boolean showTopView, boolean isJobFinderEntranceEnable) {
        String countStr1 = AppUtil.formatTob(resultCount);
        int highLightColor = mResources.getColor(R.color.text_color_orange_FF7700);
        String countStr2 = AppUtil.formatTob(extraResultCount);
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.search_result_count_mine_hint_value, countStr1, countStr2));
        spannableString.setSpan(new ForegroundColorSpan(highLightColor), 1, 1 + countStr1.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(highLightColor), 8 + countStr1.length(), 8 + countStr1.length() + countStr2.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView_searchCount.setText(spannableString);
//        checkBox_showMatchDetail.setVisibility((isMatchDetailEntranceEnable ? View.VISIBLE : View.GONE));
        view_searchFilterOperate.setVisibility((showTopView ? View.VISIBLE : View.GONE));
        checkBox_onlySearchJobFinder.setVisibility((isJobFinderEntranceEnable ? View.VISIBLE : View.GONE));
        view_noSearchResultHint.setVisibility((resultCount > 0 ? View.GONE : View.VISIBLE));
    }

    @Override
    public void callSearch() {
        mPresenter.doSearch(true);
    }

    @Override
    public String callGetKeyWord() {
        return textView_search.getText().toString().trim();
    }

    @Override
    public void callUpdateKeywordView(String keyWord) {
        textView_search.setText(keyWord);
    }

    @Override
    public void callUpdateKeywordHint(int keyWordHint) {
        textView_search.setHint(keyWordHint);
    }

    @Override
    public void callUpdateSearchFilterView() {
        mSearchFilterUI.makePreviousDataWhenFilterChangeOutside();
    }

    @Override
    public void callSetLabelLibrary() {
        mSearchFilterUI.setLabelLibs();
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
            mAdapter.refresh(mPresenter.isSortByCreateTime());
        } else {
            View view = AppUtil.getTargetVisibleView(targetPosition, listView);
            if (view != null)
                mAdapter.refreshItem(view, targetPosition);
        }
        if (selection >= 0)
            listView.setSelection(selection);
    }

    @Override
    public void callUpdateLoadAllDataView(boolean hasLoadAll) {
        pullToRefreshListView.setFootReboundInsteadLoad(hasLoadAll);
    }

    @Override
    public void callShowSpecialToastMsg(int type, String msg, int msgId) {

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.doSearch(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        if (buttonView.getId() == R.id.cb_name_List) {
//            mPresenter.onNameListFilterSet(isChecked);
//            return;
//        }
        mPresenter.onOnlySearchJobFinderSet(isChecked);
    }

    private void updateGroupAddAllSelectedCount(int count) {
        textView_allSelected.setText(mResources.getString(R.string.group_add_all_selected_value, String.valueOf(count)));
    }
}
