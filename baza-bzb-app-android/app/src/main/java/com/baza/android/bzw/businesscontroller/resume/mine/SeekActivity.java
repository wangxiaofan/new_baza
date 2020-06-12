package com.baza.android.bzw.businesscontroller.resume.mine;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.find.updateengine.ResumeUpdateCardActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.businesscontroller.resume.mine.adapter.MineResumeListAdapter;
import com.baza.android.bzw.businesscontroller.resume.mine.presenter.SeekPresenter;
import com.baza.android.bzw.businesscontroller.resume.mine.viewinterface.ISeekView;
import com.baza.android.bzw.businesscontroller.search.ResumeSearchActivity;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IResumeSearchView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.ScrollShowImageView;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/6/7.
 * Title：最近联系联系过的
 * Note：
 */

public class SeekActivity extends BaseActivity implements ISeekView, View.OnClickListener, BaseBZWAdapter.IAdapterEventsListener, PullToRefreshBase.OnRefreshListener2, CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.pull_to_refresh_listView)
    PullToRefreshListView pullToRefreshListView;
    @BindView(R.id.scroll_show_image_view)
    ScrollShowImageView scrollShowImageView;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.tv_title)
    TextView textView_title;

    TextView textView_countInfo;
    ListView listView;
    View view_emptyView;
    private SeekPresenter mPresenter;
    private MineResumeListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_seek;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_communicate_nearly);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        textView_title.setText(R.string.mine_seek);
        listView = pullToRefreshListView.getRefreshableView();
        View headView = getLayoutInflater().inflate(R.layout.head_view_inner_search, null);
        headView.findViewById(R.id.fl_search_entrance).setOnClickListener(this);
        textView_countInfo = headView.findViewById(R.id.tv_count_info);
        ((CheckBox) headView.findViewById(R.id.cb_job_hunter)).setOnCheckedChangeListener(this);

        listView.addHeaderView(headView);
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setOnRefreshListener(this);
        view_emptyView = findViewById(R.id.view_empty_view);
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
                mPresenter.getSeekList(true);
            }
        });

        mPresenter = new SeekPresenter(this);
        mAdapter = new MineResumeListAdapter(this, mPresenter.getDataList(), true, this);
        listView.setAdapter(mAdapter);

        mPresenter.initialize();
    }

    @Override
    public void callShowLoadingView(String msg) {
        loadingView.startLoading(null);
    }

    @Override
    protected void onActivityDeadForApp() {
        if (mPresenter != null)
            mPresenter.onDestroy();
        mAdapter.onDestroy();
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
                ResumeSearchActivity.launch(this, new IResumeSearchView.Param().searchMode(CommonConst.INT_SEARCH_TYPE_SEEK));
                break;
        }
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, SeekActivity.class);
        activity.startActivity(intent);
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
                break;
            case AdapterEventIdConst.RESUME_LIST_SINGLE__UPDATE:
                ResumeUpdateCardActivity.launch(this, ((ResumeBean) input).candidateId, RequestCodeConst.INT_REQUEST_NONE);
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
    public void callUpdateLoadAllDataView(boolean hasLoadAll) {
        pullToRefreshListView.setFootReboundInsteadLoad(hasLoadAll);
    }

    @Override
    public void callRefreshListItems(int targetPosition) {
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.seek_count_value, FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(mPresenter.getTotalCount(), false, true)));
        spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_orange_FF7700)), 1, spannableString.length() - 3, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_countInfo.setText(spannableString);

        view_emptyView.setVisibility((mPresenter.getTotalCount() > 0 ? View.GONE : View.VISIBLE));

        if (targetPosition == CommonConst.LIST_POSITION_NONE) {
            mAdapter.notifyDataSetChanged();
            return;
        }
        View view = AppUtil.getTargetVisibleView(targetPosition, listView);
        if (view != null)
            mAdapter.refreshItem(view, targetPosition);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.getSeekList(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.getSeekList(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPresenter.onJobHunterChanged(isChecked);
    }
}
