package com.baza.android.bzw.businesscontroller.resume.jobfind;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.businesscontroller.resume.jobfind.adapter.JobHunterPredictionAdapter;
import com.baza.android.bzw.businesscontroller.resume.jobfind.presenter.JobHunterPredictionPresenter;
import com.baza.android.bzw.businesscontroller.resume.jobfind.viewinterface.IJobHunterPredictionView;
import com.baza.android.bzw.businesscontroller.search.ResumeSearchActivity;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IResumeSearchView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.log.logger.JobPredictionLogger;
import com.baza.android.bzw.manager.UserInfoManager;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.ScrollShowImageView;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Vincent.Lei on 2018/8/23.
 * Title：
 * Note：
 */
public class JobHunterPredictionActivity extends BaseActivity implements IJobHunterPredictionView, View.OnClickListener, PullToRefreshBase.OnRefreshListener2, BaseBZWAdapter.IAdapterEventsListener {
    PullToRefreshListView pullToRefreshListView;
    ScrollShowImageView scrollShowImageView;
    LoadingView loadingView;
    ListView listView;
    View view_empty;
    private JobHunterPredictionPresenter mPresenter;
    private JobHunterPredictionAdapter mAdapter;
    private JobPredictionLogger mJobPredictionLogger = new JobPredictionLogger();

    @Override
    protected int getLayoutId() {
        return R.layout.job_hunter_activity_prediction;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.job_prediction);
    }

    @Override
    protected void initWhenCallOnCreate() {
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.job_prediction);
        mPresenter = new JobHunterPredictionPresenter(this);
        view_empty = findViewById(R.id.view_empty);
        pullToRefreshListView = findViewById(R.id.pull_to_refresh_listView);
        pullToRefreshListView.setAllLoadTagEnabled(false);
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setOnRefreshListener(this);
        listView = pullToRefreshListView.getRefreshableView();
        scrollShowImageView = findViewById(R.id.scroll_show_image_view);
        pullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                scrollShowImageView.onScroll(firstVisibleItem, visibleItemCount);
            }
        });
        loadingView = findViewById(R.id.loading_view);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.loadNowPredictionList(true, false);
            }
        });

        mAdapter = new JobHunterPredictionAdapter(this, mPresenter.getDataList(), 0, 0, this);
        listView.setAdapter(mAdapter);
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.scroll_show_image_view:
                listView.setSelection(0);
                break;
            case R.id.fl_search_entrance:
                ResumeSearchActivity.launch(this, new IResumeSearchView.Param().searchMode(CommonConst.INT_SEARCH_TYPE_MINE_JOB_HUNTER_PREDICTION));
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
    public void callRefreshListItems(int targetPosition) {
        if (targetPosition == CommonConst.LIST_POSITION_NONE) {
            mAdapter.refresh(mPresenter.getNowCount(), mPresenter.getPreviousCount(), null, CommonConst.LIST_POSITION_NONE);
            return;
        }
        View view = AppUtil.getTargetVisibleView(targetPosition, listView);
        if (view != null)
            mAdapter.refresh(mPresenter.getNowCount(), mPresenter.getPreviousCount(), view, CommonConst.LIST_POSITION_NONE);
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
    public void callShowEmptyView(boolean hasData) {
        view_empty.setVisibility((hasData ? View.GONE : View.VISIBLE));
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, JobHunterPredictionActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadPreviousPredictionList(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadPreviousPredictionList(false);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL:
                ResumeBean resumeBean = (ResumeBean) input;
                ResumeDetailActivity.launch(this, new IResumeDetailView.IntentParam(resumeBean.candidateId));
                UserInfoManager.getInstance().cacheReadResume(resumeBean.candidateId);
                callRefreshListItems(position);
                break;
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_COLLECTION:
                mPresenter.collection(position);
                break;
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_ADD_REMARK:
                resumeBean = (ResumeBean) input;
                ResumeDetailActivity.launch(this, new IResumeDetailView.IntentParam(resumeBean.candidateId).isAddRemarkMode(true));
                mJobPredictionLogger.sendAddRemarkLog(this, resumeBean.candidateId);
                break;
            case AdapterEventIdConst.ADAPTER_EVENT_REFRESH_NOW_PREDICTION:
                mPresenter.loadNowPredictionList(false, true);
                mJobPredictionLogger.sendClickRefreshLog(this);
                break;
        }
    }

    @Override
    protected void onActivityDeadForApp() {
        super.onActivityDeadForApp();
        mPresenter.onDestroy();
        mAdapter.onDestroy();
    }
}
