package com.baza.android.bzw.businesscontroller.account;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.email.ListSyncEmailBean;
import com.baza.android.bzw.businesscontroller.account.presenter.EmailSyncedListPresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IEmailSyncedListView;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.businesscontroller.email.adapter.EmailSyncedAdapter;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.TopTipUI;
import com.baza.android.bzw.widget.dialog.SimpleHintDialog;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/5/27.
 * Title：
 * Note：
 */

public class EmailSyncedListActivity extends BaseActivity implements IEmailSyncedListView, View.OnClickListener, PullToRefreshBase.OnRefreshListener2, BaseBZWAdapter.IAdapterEventsListener {
    @BindView(R.id.tv_title)
    TextView textView_title;
    @BindView(R.id.tv_right_click)
    TextView textView_rightClick;
    @BindView(R.id.pull_to_refresh_listView)
    PullToRefreshListView pullToRefreshListView;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    private TopTipUI emailTipUI;
    private ListView listView;

    private EmailSyncedListPresenter mPresenter;
    private EmailSyncedAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_email_synced_list;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_email_sync_list);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        textView_title.setText(R.string.email_synced);
        textView_rightClick.setText(R.string.email_help);
        listView = pullToRefreshListView.getRefreshableView();
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setEmptyView(getLayoutInflater().inflate(R.layout.empty_view_no_result_default, null));
        mPresenter = new EmailSyncedListPresenter(this);
        mAdapter = new EmailSyncedAdapter(this, mPresenter.getEmailList(), this);
        listView.setAdapter(mAdapter);

        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                loadingView.startLoading(null);
                mPresenter.loadSyncEmailList();
            }
        });

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
            case R.id.tv_right_click:
                RemoteBrowserActivity.launch(this, mResources.getString(R.string.email_help), URLConst.LINK_EMIAL_HELP);
                break;
            default:
                mApplication.cacheTransformData(CommonConst.STR_TRANSFORM_EMAIL_LIST, mPresenter.getEmailList());
                AddEmailSyncAccountActivity.launch(this, RequestCodeConst.INT_REQUEST_ADD_SYNC_EMAIL_ACCOUNT);
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
    public void callRefreshEmailListView(int targetPosition) {
        if (targetPosition == -1) {
            mAdapter.notifyDataSetChanged();
            return;
        }
        View view = AppUtil.getTargetVisibleView(targetPosition, listView);
        if (view != null)
            mAdapter.refreshTargetItemView(targetPosition, view, false);
    }


    @Override
    public void onAdapterEventsArrival(int adapterEventId, final int position, View v, Object input) {
        switch (adapterEventId) {
            case EmailSyncedAdapter.EVENT_ID_LONG_CLICK:
                if (emailTipUI == null) {
                    emailTipUI = new TopTipUI(this, null);
                    emailTipUI.updateMenus(mPresenter.getMenuItem());
                }
                emailTipUI.setMenuClickListener(new TopTipUI.IMenuClickListener() {
                    @Override
                    public void onTipMenuClick(int menuIndex) {
                        showDeleteEmailDialog(position);
                    }
                });
                emailTipUI.show(v, v.getWidth() / 3, -60);
                break;
            case EmailSyncedAdapter.EVENT_ID_SYNC_NOW:
                mPresenter.startSyncEmailResume((ListSyncEmailBean) input);
                break;
            case EmailSyncedAdapter.EVENT_ID_RESET_ACCOUNT:
                AddEmailSyncAccountActivity.launch(this, RequestCodeConst.INT_REQUEST_ADD_SYNC_EMAIL_ACCOUNT, ((ListSyncEmailBean) input).account);
                break;
        }

    }

    private void showDeleteEmailDialog(final int position) {
        new SimpleHintDialog(this).show(mResources.getString(R.string.confirm_del_email), mResources.getString(R.string.hint_del_synced_email), R.drawable.icon_warn, 0, 0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.deleteEmail(position);
            }
        }, null);
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, EmailSyncedListActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodeConst.INT_REQUEST_ADD_SYNC_EMAIL_ACCOUNT && resultCode == RESULT_OK) {
            loadingView.startLoading(null);
            mPresenter.loadSyncEmailList();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadSyncEmailList();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }
}
