package com.baza.android.bzw.businesscontroller.resume.smartgroup;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.smartgroup.GroupIndexResultBean;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.adapter.SmartGroupIndexAdapter;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.presenter.SmartGroupIndexPresenter;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.viewinterface.ISmartGroupIndexView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.widget.LoadingView;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Vincent.Lei on 2018/11/27.
 * Title：
 * Note：
 */
public class SmartGroupIndexActivity extends BaseActivity implements ISmartGroupIndexView, View.OnClickListener, BaseBZWAdapter.IAdapterEventsListener {
    ListView listView;
    PullToRefreshListView pullToRefreshListView;
    LoadingView loadingView;
    private SmartGroupIndexAdapter mAdapter;
    private SmartGroupIndexPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.smart_group_activity_index;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_smart_group_index);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new SmartGroupIndexPresenter(this);
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.smart_group);
        loadingView = findViewById(R.id.loading_view);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                loadingView.startLoading(null);
                mPresenter.loadGroupCollectInfo();
            }
        });
        View viewEmpty = getLayoutInflater().inflate(R.layout.empty_view_no_result_default, null);
        ((TextView) viewEmpty.findViewById(R.id.tv_hint_no_result)).setText(R.string.no_resume_for_smart_group);
        pullToRefreshListView = findViewById(R.id.pull_to_refresh_listView);
        pullToRefreshListView.setEmptyView(viewEmpty);
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setHeadReboundInsteadRefresh(true);
        listView = pullToRefreshListView.getRefreshableView();
        mAdapter = new SmartGroupIndexAdapter(this, mPresenter.getDataList(), this);
        listView.setAdapter(mAdapter);
        mPresenter.initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
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
        if (!loadingView.isShownVisibility())
            return;
        if (success)
            loadingView.finishLoading();
        else
            loadingView.loadingFailed(errorCode, errorMsg);
    }

    @Override
    public void callRefreshListItems(int targetPosition) {
        if (targetPosition <= CommonConst.LIST_POSITION_NONE)
            mAdapter.notifyDataSetChanged();
        else {
            View view = AppUtil.getTargetVisibleView(targetPosition, listView);
            if (view != null)
                mAdapter.refreshItem(view, targetPosition);
        }
    }

    @Override
    public void callShowLoadingView(String msg) {
        loadingView.startLoading(null);
    }

    @Override
    protected void onActivityDeadForApp() {
        super.onActivityDeadForApp();
        if (mAdapter != null)
            mAdapter.onDestroy();
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, SmartGroupIndexActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        SmartGroupHomeActivity.launch(this, ((GroupIndexResultBean.GroupIndexBean) input).groupType);
    }

}
