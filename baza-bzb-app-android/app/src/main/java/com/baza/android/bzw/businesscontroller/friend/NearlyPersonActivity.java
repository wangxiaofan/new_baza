package com.baza.android.bzw.businesscontroller.friend;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.businesscontroller.friend.adapter.FriendAdapter;
import com.baza.android.bzw.businesscontroller.friend.presenter.NearlyPersonPresenter;
import com.baza.android.bzw.businesscontroller.friend.viewinterface.INearlyPersonView;
import com.baza.android.bzw.businesscontroller.im.IMConst;
import com.baza.android.bzw.businesscontroller.message.ChatActivity;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.dialog.AddFriendDialog;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Vincent.Lei on 2017/9/21.
 * Title：好友搜索
 * Note：
 */

public class NearlyPersonActivity extends BaseActivity implements INearlyPersonView, View.OnClickListener, BaseBZWAdapter.IAdapterEventsListener, PullToRefreshBase.OnRefreshListener2 {
    private NearlyPersonPresenter mPresenter;
    private FriendAdapter mAdapter;
    private View view_noResult;
    ListView listView;
    LoadingView loadingView;
    PullToRefreshListView pullToRefreshListView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_nearly_person;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_nearly_person);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new NearlyPersonPresenter(this);
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.title_nearly_person);
        pullToRefreshListView = findViewById(R.id.pull_to_refresh_listView);
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setOnRefreshListener(this);
        listView = pullToRefreshListView.getRefreshableView();
        view_noResult = findViewById(R.id.fl_no_data);
        loadingView = findViewById(R.id.loading_view);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                loadingView.startLoading(null);
                mPresenter.listNearlyPerson(true);
            }
        });
        mAdapter = new FriendAdapter(this, mPresenter.getFriends(), FriendAdapter.TYPE_NEARLY_FRIEND, this);
        listView.setAdapter(mAdapter);
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    protected void onActivityDeadForApp() {
        if (mPresenter != null)
            mPresenter.onDestroy();
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, NearlyPersonActivity.class);
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
    public void callRefreshList() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void callShowLoadingView() {
        loadingView.startLoading(null);
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
    public void callUpdateNoDataView(boolean hasData) {
        view_noResult.setVisibility((hasData ? View.GONE : View.VISIBLE));
    }

    @Override
    public void callSetLoadDataStatus(boolean completed) {
        pullToRefreshListView.setFootReboundInsteadLoad(completed);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.ADAPTER_EVENT_FRIEND_SEND_MSG:
                FriendListResultBean.FriendBean friendBean = (FriendListResultBean.FriendBean) input;
                if (TextUtils.isEmpty(friendBean.neteaseId))
                    return;
                ChatActivity.launch(this, new ChatActivity.ChatParam(friendBean.neteaseId, IMConst.SESSION_TYPE_P2P));
                break;
            case AdapterEventIdConst.ADAPTER_EVENT_ADD_FRIEND:
                final FriendListResultBean.FriendBean friendBean2 = (FriendListResultBean.FriendBean) input;
                new AddFriendDialog(this, new AddFriendDialog.IAddFriendEditListener() {
                    @Override
                    public void onReadyAddFriend(String hello) {
                        mPresenter.addFriend(hello, friendBean2);
                    }
                });
                break;
            case AdapterEventIdConst.ADAPTER_EVENT_FRIEND_HOME:
                friendBean = (FriendListResultBean.FriendBean) input;
                FriendHomeActivity.launch(this, friendBean.unionId);
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.listNearlyPerson(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.listNearlyPerson(false);
    }
}
