package com.baza.android.bzw.businesscontroller.friend;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.friend.DynamicListResultBean;
import com.baza.android.bzw.bean.friend.FriendInfoResultBean;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.businesscontroller.friend.adapter.FriendDynamicAdapter;
import com.baza.android.bzw.businesscontroller.friend.presenter.FriendHomePresenter;
import com.baza.android.bzw.businesscontroller.friend.viewinterface.IFriendHomeView;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.widget.ListPopupWindow;
import com.baza.android.bzw.widget.LoadingView;
import com.slib.utils.DialogUtil;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Vincent.Lei on 2017/9/26.
 * Title：好友主页
 * Note：
 */

public class FriendHomeActivity extends BaseActivity implements IFriendHomeView, View.OnClickListener, PullToRefreshBase.OnRefreshListener2, BaseBZWAdapter.IAdapterEventsListener {
    private FriendHomePresenter mPresenter;
    private LoadingView loadingView;
    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;
    private TextView textView_noDynamic;
    private ImageButton imageButton_rightClick;
    private HeadViewFriendHome mHeadViewFriendHome;
    private FriendDynamicAdapter mAdapter;
    private ListPopupWindow listPopupWindow_moreOperation;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend_main_page;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_friend_home);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new FriendHomePresenter(this, getIntent());
        imageButton_rightClick = findViewById(R.id.ibtn_right_click);
        imageButton_rightClick.setImageResource(R.drawable.icon_more_black);
        imageButton_rightClick.setVisibility(View.GONE);
        loadingView = findViewById(R.id.loading_view);
        pullToRefreshListView = findViewById(R.id.pull_to_refresh_listView);
        pullToRefreshListView.setHeadReboundInsteadRefresh(true);
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setOnRefreshListener(this);
        listView = pullToRefreshListView.getRefreshableView();
        View emptyView = getLayoutInflater().inflate(R.layout.empty_view_no_result_default, null);
        textView_noDynamic = emptyView.findViewById(R.id.tv_hint_no_result);
        textView_noDynamic.setCompoundDrawables(null, null, null, null);
        textView_noDynamic.setText(R.string.no_dynamic);
        int padding = ScreenUtil.dip2px(60);
        textView_noDynamic.setPadding(0, padding, 0, padding);
        listView.addFooterView(emptyView);

        mHeadViewFriendHome = new HeadViewFriendHome(this, mPresenter);
        listView.addHeaderView(mHeadViewFriendHome.getHeadView());
        mAdapter = new FriendDynamicAdapter(this, mPresenter.getDynamicData(), this);
        listView.setAdapter(mAdapter);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                loadingView.startLoading(null);
                mPresenter.getFriendInfo();
            }
        });
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    public static void launch(Activity activity, String unionId) {
        Intent intent = new Intent(activity, FriendHomeActivity.class);
        intent.putExtra("unionId", unionId);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.ibtn_right_click:
                showMoreEditMenu();
                break;
        }
    }

    private void showMoreEditMenu() {
        if (listPopupWindow_moreOperation == null)
            listPopupWindow_moreOperation = new ListPopupWindow(this, mPresenter.getMoreEditMenu(), new ListPopupWindow.IMenuClickListener() {
                @Override
                public void onMenuClick(int position) {
                    DialogUtil.doubleButtonShow(FriendHomeActivity.this, 0, R.string.delete_friend_confirm, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPresenter.deleteFriend();
                        }
                    }, null);
                }
            });
        listPopupWindow_moreOperation.show(imageButton_rightClick);
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
    public void callUpdateFriendInfoView() {
        FriendInfoResultBean.FriendInfoBean friendInfoBean = mPresenter.getFriendInfoData();
        imageButton_rightClick.setVisibility(((friendInfoBean.isFriend == FriendInfoResultBean.FriendInfoBean.FRIEND_YES) ? View.VISIBLE : View.GONE));
        mHeadViewFriendHome.updateFriendInfoView();
    }

    @Override
    public void callRefreshDynamicViews() {
        pullToRefreshListView.onRefreshComplete();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void callSetLoadMoreEnable(boolean enable) {
        pullToRefreshListView.setFootReboundInsteadLoad(!enable);
    }

    @Override
    public void callUpdateNoDataView(boolean hasData) {
        textView_noDynamic.setVisibility((hasData ? View.GONE : View.VISIBLE));
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.getTargetDynamicList(false);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.ADAPTER_EVENT_DYNAMIC_CLICK_PC_INFO:
                RemoteBrowserActivity.launch(this, null, false, CommonConst.STR_BZW_HOME_LINK);
                break;
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL:
//                DynamicListResultBean.DynamicBean dynamicBean = (DynamicListResultBean.DynamicBean) input;
//                if (dynamicBean.getShareRequestDynamic() == null)
//                    return;
//                ResumeDetailActivity.launch(this, new IResumeDetailView.IntentParam(dynamicBean.getShareRequestDynamic().candidateId));
                break;
            case AdapterEventIdConst.ADAPTER_EVENT_CLICK_ADDED_FRIEND:
                DynamicListResultBean.DynamicBean dynamicBean = (DynamicListResultBean.DynamicBean) input;
                if (dynamicBean.getAddFriendDynamic() == null || TextUtils.isEmpty(dynamicBean.getAddFriendDynamic().unionId))
                    return;
                FriendHomeActivity.launch(this, dynamicBean.getAddFriendDynamic().unionId);
                break;
        }
    }
}
