package com.baza.android.bzw.businesscontroller.message;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.push.PushBean;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.businesscontroller.message.adapter.SystemMessageAdapter;
import com.baza.android.bzw.businesscontroller.message.presenter.SystemMessagePresenter;
import com.baza.android.bzw.businesscontroller.message.viewinterface.ISystemView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.ScrollShowImageView;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.baza.android.bzw.manager.PushManager;

/**
 * Created by Vincent.Lei on 2017/6/21.
 * Title：
 * Note：
 */

public class SystemMessageActivity extends BaseActivity implements ISystemView, View.OnClickListener, BaseBZWAdapter.IAdapterEventsListener, PullToRefreshBase.OnRefreshListener2 {
    @BindView(R.id.pull_to_refresh_listView)
    PullToRefreshListView pullToRefreshListView;
    @BindView(R.id.tv_title)
    TextView textView_title;
    @BindView(R.id.scroll_show_image_view)
    ScrollShowImageView scrollShowImageView;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    ListView listView;
    private SystemMessageAdapter mAdapter;

    private SystemMessagePresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_system_message;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_system);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        int type = getIntent().getIntExtra("type", ISystemView.MSG_SYSTEM);
        textView_title.setText(type == ISystemView.MSG_BAZA_HELPER ? R.string.baza_helper : R.string.sys_notice);
        mPresenter = new SystemMessagePresenter(this, type);
        listView = pullToRefreshListView.getRefreshableView();
        mAdapter = new SystemMessageAdapter(this, mPresenter.getDataList(), this);
        listView.setAdapter(mAdapter);
        pullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                scrollShowImageView.onScroll(firstVisibleItem, visibleItemCount);
            }
        });
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setEmptyView(getLayoutInflater().inflate(R.layout.empty_view_no_result_default, null));
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                loadingView.startLoading(null);
                mPresenter.loadInitData(true);
            }
        });

        mPresenter.initialize();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mPresenter.loadInitData(true);
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    public void callCancelLoadingView(boolean success, int errorCode, String errorMsg) {
        if (!loadingView.isShownVisibility())
            return;
        if (success)
            loadingView.finishLoading();
        else
            loadingView.loadingFailed(errorCode, errorMsg);
    }

    @Override
    public void callRefreshMessageViews(int targetPosition) {
        pullToRefreshListView.onRefreshComplete();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void callUpdateLoadMoreView(boolean enable) {
        pullToRefreshListView.setFootReboundInsteadLoad(!enable);
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

    public static void launch(Activity activity, int type) {
        Intent intent = new Intent(activity, SystemMessageActivity.class);
        intent.putExtra("type", type);
        activity.startActivity(intent);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.SYSTEM_MESSAGE_ADAPTER_EVENT_OPEN:
                PushManager.getInstance().openPushMessage(this, (PushBean) input, false);
                break;
            case AdapterEventIdConst.ADAPTER_EVENT_CLICK_LINK_TEXT:
                if (input != null)
                    RemoteBrowserActivity.launch(this, null, false, input.toString());
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadInitData(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadInitData(false);
    }
}
