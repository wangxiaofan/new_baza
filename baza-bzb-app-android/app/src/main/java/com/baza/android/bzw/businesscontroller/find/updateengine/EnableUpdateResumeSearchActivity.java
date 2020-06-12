package com.baza.android.bzw.businesscontroller.find.updateengine;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.ResumeUpdateTransformParamBean;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.account.RightCenterActivity;
import com.baza.android.bzw.businesscontroller.find.updateengine.adapter.UpdateResumeListAdapter;
import com.baza.android.bzw.businesscontroller.find.updateengine.enableupdatelistui.EnableUpdatedSearchUI;
import com.baza.android.bzw.businesscontroller.find.updateengine.presenter.EnableUpdateResumeSearchPresenter;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IEnableUpdateResumeSearchView;
import com.baza.android.bzw.businesscontroller.search.EditSearchConfigActivity;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IEditSearchConfigView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.ScrollShowImageView;
import com.slib.utils.AppUtil;
import com.slib.utils.DialogUtil;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2018/6/8.
 * Title：
 * Note：
 */
public class EnableUpdateResumeSearchActivity extends BaseActivity implements IEnableUpdateResumeSearchView, CompoundButton.OnCheckedChangeListener, View.OnClickListener, PullToRefreshBase.OnRefreshListener2, BaseBZWAdapter.IAdapterEventsListener {
    @BindView(R.id.pull_to_refresh_listView)
    PullToRefreshListView pullToRefreshListView;
    @BindView(R.id.scroll_show_image_view)
    ScrollShowImageView scrollShowImageView;
    @BindView(R.id.tv_do_search)
    TextView textView_search;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.btn_update)
    Button button_update;
    ListView listView;
    View view_searchFilterOperate;
    TextView textView_searchCount;
    View view_noSearchResultHint;

    private EnableUpdateResumeSearchPresenter mPresenter;
    private EnableUpdatedSearchUI mSearchFilterUI;
    private UpdateResumeListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.resume_update_activity_search;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_enable_update_search);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null)
            mPresenter.onResume();
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        initNoSearchResultView();
        listView = pullToRefreshListView.getRefreshableView();
        //搜索结果以及条件操作view
        view_searchFilterOperate = getLayoutInflater().inflate(R.layout.head_view_filter_result_operate, null);
        textView_searchCount = view_searchFilterOperate.findViewById(R.id.tv_search_count);
        CheckBox checkBox_onlySearchJobFinder = view_searchFilterOperate.findViewById(R.id.cb_job_hunter);
        checkBox_onlySearchJobFinder.setOnCheckedChangeListener(this);
        callUpdateSearchResultHint(0, 0, false, false);

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.addView(view_searchFilterOperate, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        listView.addHeaderView(frameLayout);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.loadInitData();
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
        mPresenter = new EnableUpdateResumeSearchPresenter(this);
        mSearchFilterUI = new EnableUpdatedSearchUI(findViewById(R.id.search_filter_ui), this, mPresenter);
        mSearchFilterUI.init();
        mAdapter = new UpdateResumeListAdapter(this, mPresenter.getEnableUpdateList(), true, this);
        listView.setAdapter(mAdapter);
        mPresenter.initialize();
        EditSearchConfigActivity.launch(RequestCodeConst.INT_REQUEST_SET_SEARCH_CONDITION, this, IEditSearchConfigView.MODE_RESUME_SEARCH_HISTORY, 0, textView_search.getText().toString().trim());
    }

    @Override
    protected void onActivityDeadForApp() {
        if (mPresenter != null)
            mPresenter.onDestroy();
//        if (mAdapter != null)
//            mAdapter.onDestroy();
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

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, EnableUpdateResumeSearchActivity.class);
        activity.startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.tv_do_search:
                mSearchFilterUI.closeFilterPane();
                EditSearchConfigActivity.launch(RequestCodeConst.INT_REQUEST_SET_SEARCH_CONDITION, this, IEditSearchConfigView.MODE_RESUME_SEARCH_HISTORY, 0, textView_search.getText().toString().trim());
                break;
            case R.id.scroll_show_image_view:
                listView.setSelection(0);
                break;
            case R.id.btn_update:
                if (!mPresenter.isOneKeyUpdateEnable()) {
                    mPresenter.shareUpdateEngineAdv();
                    return;
                }
                DialogUtil.doubleButtonShow(this, 0, R.string.confirm_one_key_update, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.updateResumeList();
                    }
                }, null);
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
                ResumeBean resumeBean = (ResumeBean) input;
                if (resumeBean.isUpdating()) {
                    callShowToastMessage(null, R.string.on_one_day_update);
                    return;
                }
                ResumeUpdateTransformParamBean mResumeUpdateTransformParam = new ResumeUpdateTransformParamBean(position, mPresenter.getTotalEnableUpdateCount(), mPresenter.getEnableUpdateList());
                mResumeUpdateTransformParam.setAdvanceSearchFilter(mPresenter.getSearchFilterInfo());
                BZWApplication.getApplication().cacheTransformData(CommonConst.STR_TRANSFORM_UPDATE_RESUME_PARAM, mResumeUpdateTransformParam);
                ResumeUpdateCardActivity.launch(this);
                break;
            case AdapterEventIdConst.RESUME_LIST_SINGLE__UPDATE:
                mPresenter.updateSingleResume((ResumeBean) input);
                break;
        }
    }

    /**
     * 更新搜索到的简历数提醒
     */
    @Override
    public void callUpdateSearchResultHint(int resultCount, int extraCount, boolean showTopView, boolean isJobFinderEntranceEnable) {
        String hint = mResources.getString(R.string.search_result_count_hint_value, AppUtil.formatTob(resultCount));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(hint);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_orange_FF7700)), 3, hint.length() - 3, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_searchCount.setText(spannableStringBuilder);
        view_searchFilterOperate.setVisibility((showTopView ? View.VISIBLE : View.GONE));
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
            mAdapter.notifyDataSetChanged();
        }
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
    public void callUpdateOneKeyUpdateView(int amount, int listCount, int searchCount) {
        if (listCount <= 0) {
            button_update.setVisibility(View.GONE);
            return;
        }
        button_update.setText((amount > 0 ? mResources.getString(R.string.one_key_to_update_resume_list, String.valueOf((amount > searchCount ? searchCount : amount))) : mResources.getString(R.string.share_to_get_three_amount)));
        button_update.setVisibility(View.VISIBLE);
    }

    @Override
    public void callUpdateShareToMuchView() {
        DialogUtil.doubleButtonShow(this, 0, R.string.share_to_much_for_get_update_quality, R.string.cancel, R.string.to_exchange, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RightCenterActivity.launch(EnableUpdateResumeSearchActivity.this);
            }
        }, null);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPresenter.onJobHunterFilterSelected(isChecked);
    }
}
