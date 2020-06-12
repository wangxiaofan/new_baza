package com.baza.android.bzw.businesscontroller.account;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.taskcard.TaskBean;
import com.baza.android.bzw.businesscontroller.account.adapter.TaskCardAdapter;
import com.baza.android.bzw.businesscontroller.account.presenter.TaskCardPresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.ITaskCardView;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.businesscontroller.find.updateengine.ResumeEnableUpdateListActivity;
import com.baza.android.bzw.businesscontroller.friend.FriendRequestActivity;
import com.baza.android.bzw.businesscontroller.home.HomeActivity;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.widget.LoadingView;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Vincent.Lei on 2018/11/28.
 * Title：
 * Note：
 */
public class TaskCardActivity extends BaseActivity implements ITaskCardView, View.OnClickListener, BaseBZWAdapter.IAdapterEventsListener {
    PullToRefreshListView pullToRefreshListView;
    LoadingView loadingView;
    ListView listView;

    private TaskCardPresenter mPresenter;
    private TaskCardAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.account_activity_task_card;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.task_card);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new TaskCardPresenter(this);
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.task_card);
        loadingView = findViewById(R.id.loading_view);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                loadingView.startLoading(null);
                mPresenter.loadMerculetTasks();
            }
        });
        pullToRefreshListView = findViewById(R.id.pull_to_refresh_listView);
        pullToRefreshListView.setHeadReboundInsteadRefresh(true);
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        listView = pullToRefreshListView.getRefreshableView();
        mAdapter = new TaskCardAdapter(this, mPresenter.getDataList(), this);
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

    public static void launch(Activity activity) {
        activity.startActivity(new Intent(activity, TaskCardActivity.class));
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
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void callShowLoadingView(String msg) {
        loadingView.startLoading(null);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        TaskBean taskBean = (TaskBean) input;
        switch (taskBean.eventType) {
            case CommonConst.MerculetEventType.EVENT_ADD_REMARK:
            case CommonConst.MerculetEventType.EVENT_RESUME_RECOMMEND:
                HomeActivity.launch(this, HomeActivity.TAB_TALENT);
                break;
            case CommonConst.MerculetEventType.EVENT_FRIEND_REQUEST:
                FriendRequestActivity.launch(this);
                break;
            case CommonConst.MerculetEventType.EVENT_RESUME_UPDATE:
                ResumeEnableUpdateListActivity.launch(this);
                break;
            case CommonConst.MerculetEventType.EVENT_RESUME_SYNC:
                ImportPlatformListActivity.launch(this);
                break;
            case CommonConst.MerculetEventType.EVENT_UPDATE_GRADE:
                AccountExperienceActivity.launch(this);
                break;
            case CommonConst.MerculetEventType.EVENT_INVITED:
                InvitedActivity.launch(this);
                break;
            default:
                if (!TextUtils.isEmpty(taskBean.linkUrl))
                    RemoteBrowserActivity.launch(this, null, true, taskBean.linkUrl);
                break;
        }
    }
}
