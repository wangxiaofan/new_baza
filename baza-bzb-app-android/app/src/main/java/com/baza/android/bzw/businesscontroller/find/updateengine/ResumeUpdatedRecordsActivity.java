package com.baza.android.bzw.businesscontroller.find.updateengine;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.find.updateengine.adapter.UpdateResumeListAdapter;
import com.baza.android.bzw.businesscontroller.find.updateengine.presenter.ResumeUpdatedRecordsPresenter;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IResumeUpdatedRecordsView;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.ScrollShowImageView;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/9/11.
 * Title：已更新列表
 * Note：
 */

public class ResumeUpdatedRecordsActivity extends BaseActivity implements IResumeUpdatedRecordsView, View.OnClickListener, PullToRefreshBase.OnRefreshListener2, BaseBZWAdapter.IAdapterEventsListener {
    @BindView(R.id.pull_to_refresh_listView)
    PullToRefreshListView pullToRefreshListView;
    @BindView(R.id.scroll_show_image_view)
    ScrollShowImageView scrollShowImageView;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    ListView listView;
    TextView textView_allCount;

    private UpdateResumeListAdapter mAdapter;
    private ResumeUpdatedRecordsPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_seek;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_already_updated_list);
    }

    @Override
    protected void initWhenCallOnCreate() {
        TextView textView_title = findViewById(R.id.tv_title);
        textView_title.setText(R.string.title_already_updated_list);
        ButterKnife.bind(this);
        listView = pullToRefreshListView.getRefreshableView();
        textView_allCount = (TextView) getLayoutInflater().inflate(R.layout.name_list_received_top_head, null);
        listView.addHeaderView(textView_allCount);

        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setOnRefreshListener(this);
        View view_empty = getLayoutInflater().inflate(R.layout.empty_view_no_result_default, null);
        TextView textView = view_empty.findViewById(R.id.tv_hint_no_result);
        textView.setText(R.string.no_updated_resume);
        pullToRefreshListView.setEmptyView(view_empty);
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
                loadingView.startLoading(null);
                mPresenter.getAlreadyUpdatedList(true);
            }
        });

        mPresenter = new ResumeUpdatedRecordsPresenter(this);
        mAdapter = new UpdateResumeListAdapter(this, mPresenter.getData(), this);
        listView.setAdapter(mAdapter);
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, ResumeUpdatedRecordsActivity.class);
        activity.startActivity(intent);
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
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.getAlreadyUpdatedList(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.getAlreadyUpdatedList(false);
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
    public void callRefreshListItems() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void callUpdateLoadAllDataView(boolean hasLoadAll) {
        pullToRefreshListView.setFootReboundInsteadLoad(hasLoadAll);
    }

    @Override
    public void callUpdateAllCountView(int totalCount) {
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.resume_update_record_count_value, FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(totalCount, false, true)));
        spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_orange_FF7700)), 5, spannableString.length() - 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_allCount.setText(spannableString);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL:
                ResumeDetailActivity.launch(this, new IResumeDetailView.IntentParam(null).updateHistoryId(((ResumeBean) input).id));
                break;
        }
    }
}
