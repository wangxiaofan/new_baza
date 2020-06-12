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
import com.baza.android.bzw.businesscontroller.friend.constant.FriendConst;
import com.baza.android.bzw.businesscontroller.friend.presenter.FriendListPresenter;
import com.baza.android.bzw.businesscontroller.friend.viewinterface.IFriendListView;
import com.baza.android.bzw.businesscontroller.im.IMConst;
import com.baza.android.bzw.businesscontroller.message.ChatActivity;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.widget.LoadingView;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/9/21.
 * Title：好友列表
 * Note：
 */

public class FriendListActivity extends BaseActivity implements View.OnClickListener, IFriendListView, BaseBZWAdapter.IAdapterEventsListener {
    private LoadingView loadingView;
    private ListView listView;
    private View view_unread;
    private TextView textView_noResult;
    private FriendListPresenter mPresenter;
    private FriendAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend_list;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_friend_list);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new FriendListPresenter(this, getIntent());
        loadingView = findViewById(R.id.loading_view);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.loadFriendList();
            }
        });
        textView_noResult = findViewById(R.id.tv_hint_no_result);
        listView = findViewById(R.id.list);
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.title_friend_list);
        textView = findViewById(R.id.tv_right_click);
        textView.setText(R.string.add);
        View headView = getLayoutInflater().inflate(R.layout.head_view_for_friend_list, null);
        view_unread = headView.findViewById(R.id.view_new_hint);
        headView.findViewById(R.id.fl_search).setOnClickListener(this);
        headView.findViewById(R.id.fl_new_friend).setOnClickListener(this);
        listView.addHeaderView(headView);
        mAdapter = new FriendAdapter(this, mPresenter.getFriends(), FriendAdapter.TYPE_FRIEND_LIST, this);
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
            case R.id.tv_right_click:
                SearchFriendActivity.launch(this, FriendConst.TYPE_SEARCH_TO_ADD_FRIEND);
                break;
            case R.id.fl_search:
                mApplication.cacheTransformData(CommonConst.STR_TRANSFORM_LOACL_FRIEND_LIST, mPresenter.getFriends());
                SearchFriendActivity.launch(this, FriendConst.TYPE_SEARCH_FRIEND_LOCAL);
                break;
            case R.id.fl_new_friend:
                FriendRequestActivity.launch(this);
                break;
        }
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, FriendListActivity.class);
        activity.startActivity(intent);
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
    public void callFriendNoticeUnReadCountUpdate(int unreadCount) {
        view_unread.setVisibility((unreadCount > 0 ? View.VISIBLE : View.GONE));
    }

    @Override
    public void callUpdateNoDataView(boolean hasData) {
        textView_noResult.setVisibility((hasData ? View.GONE : View.VISIBLE));
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
            case AdapterEventIdConst.ADAPTER_EVENT_FRIEND_HOME:
                friendBean = (FriendListResultBean.FriendBean) input;
                FriendHomeActivity.launch(this, friendBean.unionId);
                break;
        }
    }
}
