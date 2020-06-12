package com.baza.android.bzw.businesscontroller.find.updateengine;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.businesscontroller.find.updateengine.adapter.UpdateLogAdapter;
import com.baza.android.bzw.businesscontroller.find.updateengine.presenter.UpdateLogPresenter;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IUpdateLogView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.widget.LoadingView;
import com.slib.utils.ToastUtil;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/8/29.
 * Title：简历更新日志
 * Note：
 */

public class UpdateLogActivity extends BaseActivity implements IUpdateLogView, View.OnClickListener, PullToRefreshBase.OnRefreshListener2, BaseBZWAdapter.IAdapterEventsListener {
    @BindView(R.id.tv_title)
    TextView textView_title;
    @BindView(R.id.pull_to_refresh_listView)
    PullToRefreshListView pullToRefreshListView;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    ListView listView;
    private UpdateLogPresenter mPresenter;
    private UpdateLogAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_update_log;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_update_log);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        mPresenter = new UpdateLogPresenter(this, getIntent());
        listView = pullToRefreshListView.getRefreshableView();
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setEmptyView(getLayoutInflater().inflate(R.layout.empty_view_no_result_default, null));
        mAdapter = new UpdateLogAdapter(this, mPresenter.getLogDataList(), mPresenter.getResumeUserName(), this);
        listView.setAdapter(mAdapter);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                loadingView.startLoading(null);
                mPresenter.getLogs(true);
            }
        });

        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    protected void onActivityDeadForApp() {
        if (mAdapter != null)
            mAdapter.onDestroy();
        if (mPresenter != null)
            mPresenter.onDestroy();
    }

    public static void launch(Activity activity, String ownerName, String candidateId) {
        Intent intent = new Intent(activity, UpdateLogActivity.class);
        if (ownerName != null)
            intent.putExtra("ownerName", ownerName);
        if (candidateId != null)
            intent.putExtra("candidateId", candidateId);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
        }
    }

    @Override
    public void callSetTitle(String title) {
        textView_title.setText(title);
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
    public void callSetLoadMoreEnable(boolean enable) {
        pullToRefreshListView.setFootReboundInsteadLoad(!enable);
    }

    @Override
    public void callRefreshListView() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void callFinishPlayVoice() {
        mAdapter.stopAudioAnimate();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.getLogs(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.getLogs(false);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.EVENT_ID_PLAY_RECORD:
            case AdapterEventIdConst.EVENT_ID_PLAY_RECORD_WITH_NEW_MODE:
                mPresenter.playVoice((File) input, adapterEventId == AdapterEventIdConst.EVENT_ID_PLAY_RECORD_WITH_NEW_MODE);
                break;
            case AdapterEventIdConst.EVENT_ID_RECORD_IS_NOT_EXIST:
                ToastUtil.showToast(this, R.string.remark_file_is_not_exist_is_delete);
                break;
            case AdapterEventIdConst.EVENT_ID_STOP_PLAY_RECORD:
                mPresenter.stopPlayRecord();
                break;
        }
    }
}
