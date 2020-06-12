package com.baza.android.bzw.businesscontroller.friend;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.businesscontroller.friend.adapter.FriendAdapter;
import com.baza.android.bzw.businesscontroller.friend.presenter.FriendRequestPresenter;
import com.baza.android.bzw.businesscontroller.friend.viewinterface.IFriendRequestView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.dialog.ListMenuDialog;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/9/22.
 * Title：好友请求列表
 * Note：
 */

public class FriendRequestActivity extends BaseActivity implements IFriendRequestView, View.OnClickListener, BaseBZWAdapter.IAdapterEventsListener {
    private LoadingView loadingView;
    private ListView listView;
    private View view_noResult;
    private FriendRequestPresenter mPresenter;
    private FriendAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend_require_list;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_friend_request_list);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new FriendRequestPresenter(this);
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.new_friend);
        loadingView = findViewById(R.id.loading_view);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.getFriendRequestList();
            }
        });
        view_noResult = findViewById(R.id.fl_no_data);

        listView = findViewById(R.id.list);
        mAdapter = new FriendAdapter(this, mPresenter.getFriends(), FriendAdapter.TYPE_FRIEND_REQUIRE, this);
        listView.setAdapter(mAdapter);
        mPresenter.initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null)
            mPresenter.onResume();
    }

    @Override
    protected void onActivityDeadForApp() {
        if (mPresenter != null)
            mPresenter.onDestroy();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, FriendRequestActivity.class);
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
    public void callRefreshFriendsView() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void callShowLoadingView() {
        loadingView.startLoading(null);
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
    public void callUpdateNoDataView(boolean hasData) {
        view_noResult.setVisibility((hasData ? View.GONE : View.VISIBLE));
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, final Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.ADAPTER_EVENT_AGREE_FRIEND:
                mPresenter.agreeFriend((FriendListResultBean.FriendBean) input);
                break;
            case AdapterEventIdConst.ADAPTER_EVENT_FRIEND_HOME:
                FriendListResultBean.FriendBean friendBean = (FriendListResultBean.FriendBean) input;
                FriendHomeActivity.launch(this, friendBean.unionId);
                break;
            case AdapterEventIdConst.ADAPTER_EVENT_DELETE_FRIEND_REQUEST:
                ListMenuDialog.showNewInstance(this, null, mPresenter.getListOperateMenu(), true, new ListMenuDialog.IOnChoseItemClickListener() {
                    @Override
                    public void onChoseItemClick(int position) {
                        mPresenter.deleteRequestRecord((FriendListResultBean.FriendBean) input);
                    }
                });
                break;
        }
    }
}
