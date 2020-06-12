package com.baza.android.bzw.businesscontroller.friend;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.businesscontroller.friend.adapter.FriendAdapter;
import com.baza.android.bzw.businesscontroller.friend.constant.FriendConst;
import com.baza.android.bzw.businesscontroller.friend.presenter.SearchFriendPresenter;
import com.baza.android.bzw.businesscontroller.friend.viewinterface.ISearchFriendView;
import com.baza.android.bzw.businesscontroller.im.IMConst;
import com.baza.android.bzw.businesscontroller.message.ChatActivity;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.extra.TextWatcherImpl;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.dialog.AddFriendDialog;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/9/21.
 * Title：好友搜索
 * Note：
 */

public class SearchFriendActivity extends BaseActivity implements ISearchFriendView, View.OnClickListener, BaseBZWAdapter.IAdapterEventsListener {
    private SearchFriendPresenter mPresenter;
    private FriendAdapter mAdapter;
    private View view_noResult;
    EditText editText_search;
    ListView listView;
    LoadingView loadingView;
    TextView textView_nearlyPersonCount;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_friend;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_friend_search);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new SearchFriendPresenter(this, getIntent());
        editText_search = findViewById(R.id.et_keyword);
        listView = findViewById(R.id.list);
        view_noResult = findViewById(R.id.fl_no_data);
        loadingView = findViewById(R.id.loading_view);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.prepareSearch(editText_search.getText().toString().trim());
            }
        });
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

    public static void launch(Activity activity, int type) {
        Intent intent = new Intent(activity, SearchFriendActivity.class);
        intent.putExtra("type", type);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.view_near_by:
                NearlyPersonActivity.launch(this);
                break;
        }
    }

    @Override
    public void callSetMode(int mode) {
        if (mode == FriendConst.TYPE_SEARCH_TO_ADD_FRIEND) {
            //云端搜索添加附近的人入口
            View view_nearly = getLayoutInflater().inflate(R.layout.layout_nearly_entrance, null);
            textView_nearlyPersonCount = view_nearly.findViewById(R.id.tv__nearly_person_count);
            textView_nearlyPersonCount.setText(mResources.getString(R.string.nearly_person_count_around_you, 0));
            view_nearly.setOnClickListener(this);
            listView.addHeaderView(view_nearly);
        }
        editText_search.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.prepareSearch(editText_search.getText().toString().trim());
            }
        });
        editText_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftInput();
                    //搜索
                    mPresenter.prepareSearch(editText_search.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        mAdapter = new FriendAdapter(this, mPresenter.getFriends(), ((mode == FriendConst.TYPE_SEARCH_FRIEND_LOCAL) ? FriendAdapter.TYPE_LOCAL_SEARCH_AND_ADD_CLOUD : FriendAdapter.TYPE_ADD_FRIEND), this);
        listView.setAdapter(mAdapter);
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
    public void callUpdateNearlyPersonCountView(int count) {
        textView_nearlyPersonCount.setText(mResources.getString(R.string.nearly_person_count_around_you, count));
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
}
