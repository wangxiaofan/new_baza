package com.baza.android.bzw.businesscontroller.account;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.account.adapter.InvitedAdapter;
import com.baza.android.bzw.businesscontroller.account.dialog.InviteDialog;
import com.baza.android.bzw.businesscontroller.account.presenter.InvitedPresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IInvitedView;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.manager.UserInfoManager;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.dialog.ShareDialog;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Vincent.Lei on 2018/12/5.
 * Title：
 * Note：
 */
public class InvitedActivity extends BaseActivity implements IInvitedView, View.OnClickListener, PullToRefreshBase.OnRefreshListener2, InviteDialog.IInviteSetListener {
    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;
    private TextView textView_invitedSuccessCount;
    private TextView textView_invitedCode;
    private LoadingView loadingView;
    private InvitedPresenter mPresenter;
    private InvitedAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.account_activity_invited;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.account_item_text_invite);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new InvitedPresenter(this);
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.account_item_text_invite);
        loadingView = findViewById(R.id.loading_view);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                loadingView.startLoading(null);
                mPresenter.loadInviteCodeInfo();
            }
        });
        pullToRefreshListView = findViewById(R.id.pull_to_refresh_listView);
        listView = pullToRefreshListView.getRefreshableView();
        textView_invitedSuccessCount = findViewById(R.id.tv_invited_success_count);
        textView_invitedCode = findViewById(R.id.tv_my_invited_code);

        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setOnRefreshListener(this);

        View viewEmpty = getLayoutInflater().inflate(R.layout.empty_view_no_result_default, null);
        TextView textView_empty = viewEmpty.findViewById(R.id.tv_hint_no_result);
        textView_empty.setText(R.string.invited_list_empty);
        pullToRefreshListView.setEmptyView(viewEmpty);

        mAdapter = new InvitedAdapter(this, mPresenter.getInviteData(), null);
        listView.setAdapter(mAdapter);
        callUpdateInvitedSuccessCountView(0);

        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, InvitedActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.tv_input_invited_code:
                if (UserInfoManager.getInstance().checkIdentifyStatusAndVerifyIfNeed(this))
                    new InviteDialog(this, InviteDialog.STATE_NORMAL, this).show();
                break;
            case R.id.tv_des:
                RemoteBrowserActivity.launch(this, null, true, URLConst.LINK_INVITE);
                break;
            case R.id.btn_share:
                callShowShareMenu();
                break;
        }
    }

    @Override
    public void onInvitedCodeSubmit(String code) {
        mPresenter.inviteCodeExchange(code);
    }

    @Override
    public boolean checkInvitedCodeIsMine(String code) {
        return (code != null && code.equals(mPresenter.getInvitedCode()));
    }

    @Override
    public boolean checkIsInvitedCodeEnable(String code) {
        if (TextUtils.isEmpty(code)) {
            callShowToastMessage(null, R.string.input_invite_code);
            return false;
        }
        return true;
    }

    @Override
    public void callShowShareMenu() {
        new ShareDialog(this).addWeChatContactMenu().addWeChatFriendCircleMenu().addQQContactMenu().addQZoneMenu().addShareMenuSelectedListener(new ShareDialog.IShareMenuSelectedListener() {
            @Override
            public void onSharePlatformSelected(int platformType) {
                mPresenter.onSharePlatformSelectedForInvite(platformType);
            }
        }).show();
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
    public void callRefreshListItems() {
        pullToRefreshListView.onRefreshComplete();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void callShowLoadingView(String msg) {
        loadingView.startLoading(msg);
    }

    @Override
    public void callUpdateInviteCodeView() {
        textView_invitedCode.setText(mPresenter.getInvitedCode());
    }

    @Override
    public void callUpdateInviteCodeErrorView() {
        new InviteDialog(this, InviteDialog.STATE_FAILED, this).show();
    }

    @Override
    public void callUpdateInviteCodeSuccessView() {
        new InviteDialog(this, InviteDialog.STATE_SUCCESS, null).show();
    }

    @Override
    public void callUpdateEnableInputInviteCodeView(boolean enable) {
        findViewById(R.id.tv_input_invited_code).setVisibility(enable ? View.VISIBLE : View.GONE);
        findViewById(R.id.view_hint_invite_code).setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void callUpdateInvitedSuccessCountView(int count) {
        textView_invitedSuccessCount.setText(mResources.getString(R.string.invited_success_count_value, String.valueOf(count)));
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadInvitedList();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    protected void onActivityDeadForApp() {
        super.onActivityDeadForApp();
        mPresenter.onDestroy();
    }
}
