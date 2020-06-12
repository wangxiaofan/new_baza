package com.baza.android.bzw.businesscontroller.friend;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.friend.DynamicListResultBean;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.businesscontroller.friend.adapter.FriendDynamicAdapter;
import com.baza.android.bzw.businesscontroller.friend.presenter.DynamicPresenter;
import com.baza.android.bzw.businesscontroller.friend.viewinterface.IDynamicView;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.widget.LoadingView;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Vincent.Lei on 2017/9/28.
 * Title：好友动态
 * Note：
 */

public class DynamicActivity extends BaseActivity implements IDynamicView, View.OnClickListener, PullToRefreshBase.OnRefreshListener2, BaseBZWAdapter.IAdapterEventsListener {
    PullToRefreshListView pullToRefreshListView;
    LoadingView loadingView;
    ListView listView;
    private DynamicPresenter mPresenter;
    private FriendDynamicAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dynamic;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_friend_dynamic);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new DynamicPresenter(this);
        TextView textView_title = findViewById(R.id.tv_title);
        textView_title.setText(R.string.friend_dynamic);
        pullToRefreshListView = findViewById(R.id.pull_to_refresh_listView);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setEmptyView(getLayoutInflater().inflate(R.layout.empty_view_no_result_default, null));
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        loadingView = findViewById(R.id.loading_view);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                loadingView.startLoading(null);
                mPresenter.getFriendDynamicList(true);
            }
        });
        listView = pullToRefreshListView.getRefreshableView();
        mAdapter = new FriendDynamicAdapter(this, mPresenter.getDynamicData(), this);
        listView.setAdapter(mAdapter);
        mPresenter.initialize();
    }

    @Override
    protected void onActivityDeadForApp() {
        if (mPresenter != null)
            mPresenter.onDestroy();
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
    public void callRefreshListItems() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void callUpdateLoadAllDataView(boolean hasLoadAll) {
        pullToRefreshListView.setFootReboundInsteadLoad(hasLoadAll);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.getFriendDynamicList(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.getFriendDynamicList(false);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.ADAPTER_EVENT_DYNAMIC_CLICK_PC_INFO:
                RemoteBrowserActivity.launch(this, null, false, CommonConst.STR_BZW_HOME_LINK);
                break;
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL:
                DynamicListResultBean.DynamicBean dynamicBean = (DynamicListResultBean.DynamicBean) input;
                if (dynamicBean.getShareRequestDynamic() == null)
                    return;
                ResumeDetailActivity.launch(this, new IResumeDetailView.IntentParam(dynamicBean.getShareRequestDynamic().candidateId));
                break;
            case AdapterEventIdConst.ADAPTER_EVENT_CLICK_TO_FRIEND_HOME:
                dynamicBean = (DynamicListResultBean.DynamicBean) input;
                if (TextUtils.isEmpty(dynamicBean.unionId))
                    return;
                FriendHomeActivity.launch(this, dynamicBean.unionId);
                break;
            case AdapterEventIdConst.ADAPTER_EVENT_CLICK_ADDED_FRIEND:
                dynamicBean = (DynamicListResultBean.DynamicBean) input;
                if (dynamicBean.getAddFriendDynamic() == null || TextUtils.isEmpty(dynamicBean.getAddFriendDynamic().unionId))
                    return;
                FriendHomeActivity.launch(this, dynamicBean.getAddFriendDynamic().unionId);
                break;
        }
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, DynamicActivity.class);
        activity.startActivity(intent);
    }
}
