package com.baza.android.bzw.businesscontroller.friend;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.businesscontroller.friend.adapter.FriendSelectedAdapter;
import com.baza.android.bzw.businesscontroller.friend.presenter.FriendSelectedPresenter;
import com.baza.android.bzw.businesscontroller.friend.viewinterface.IFriendSelectedView;
import com.baza.android.bzw.extra.TextWatcherImpl;
import com.baza.android.bzw.widget.LoadingView;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;

/**
 * Created by Vincent.Lei on 2018/1/11.
 * Title：
 * Note：
 */

public class FriendSelectedActivity extends BaseActivity implements IFriendSelectedView, View.OnClickListener {
    private LoadingView loadingView;
    private ListView listView;
    private TextView textView_noResult;

    private FriendSelectedPresenter mPresenter;
    private FriendSelectedAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_selected_friend;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_selected_friend);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new FriendSelectedPresenter(this);

        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.title_selected);
        textView = findViewById(R.id.tv_right_click);
        textView.setText(R.string.sure);
        loadingView = findViewById(R.id.loading_view);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                loadingView.startLoading(null);
                mPresenter.loadFriendList();
            }
        });
        textView_noResult = findViewById(R.id.tv_hint_no_result);
        listView = findViewById(R.id.list);
        View headView = getLayoutInflater().inflate(R.layout.headview_friend_selected, null);
        EditText editText = headView.findViewById(R.id.et_search_key);
        editText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.matchLocalFriend(s.toString());
            }
        });
        listView.addHeaderView(headView);
        mAdapter = new FriendSelectedAdapter(this, mPresenter.getFriends(), null);
        listView.setAdapter(mAdapter);
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
                ArrayList<FriendListResultBean.FriendBean> list = mPresenter.getSelectedList(mAdapter.getSelectedFriendIds());
                if (list == null || list.isEmpty()) {
                    callShowToastMessage(null, R.string.please_chose_friend);
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("friends", list);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityDeadForApp() {
        if (mPresenter != null)
            mPresenter.onDestroy();
    }

    public static void launch(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, FriendSelectedActivity.class);
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.push_bottom_in_slowly, R.anim.alpha_none);
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
        textView_noResult.setVisibility((hasData ? View.GONE : View.VISIBLE));
    }

    @Override
    public void callRefreshFriendsView() {
        mAdapter.refresh();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.push_bottom_out_slowly);
    }
}
