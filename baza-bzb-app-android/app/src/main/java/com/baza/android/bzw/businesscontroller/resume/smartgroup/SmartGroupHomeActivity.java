package com.baza.android.bzw.businesscontroller.resume.smartgroup;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.smartgroup.SmartGroupFoldersResultBean;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.adapter.SmartGroupFolderAdapter;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.dialog.AddGroupNameDialog;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.dialog.ConfirmDeleteGroupDialog;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.presenter.SmartGroupHomePresenter;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.viewinterface.ISmartGroupHomeView;
import com.baza.android.bzw.businesscontroller.search.EditSearchConfigActivity;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IEditSearchConfigView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.constant.SharedPreferenceConst;
import com.baza.android.bzw.widget.LoadingView;
import com.slib.storage.sharedpreference.SharedPreferenceManager;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Vincent.Lei on 2018/8/29.
 * Title：
 * Note：
 */
public class SmartGroupHomeActivity extends BaseActivity implements ISmartGroupHomeView, View.OnClickListener, PullToRefreshBase.OnRefreshListener2, BaseBZWAdapter.IAdapterEventsListener {
    private PullToRefreshListView pullToRefreshListView;
    private LoadingView loadingView;
    private TextView textView_title;
    private TextView textView_rightClick;
    private ListView listView;
    private TextView textView_search;

    private SmartGroupHomePresenter mPresenter;
    private SmartGroupFolderAdapter mAdapter;
    private int mGroupType;

    @Override
    protected int getLayoutId() {
        return R.layout.smart_group_activity_home;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.smart_group);
    }

    @Override
    protected void initOverAll() {
        mGroupType = getIntent().getIntExtra("groupType", CommonConst.SmartGroupType.GROUP_TYPE_TIME);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new SmartGroupHomePresenter(this, mGroupType);

        textView_search = findViewById(R.id.tv_do_search);
        updateSearchView();
        textView_title = findViewById(R.id.tv_title);
        textView_title.setText(getTitleIdByGroupType(mGroupType));
        textView_rightClick = findViewById(R.id.tv_right_click);
        textView_rightClick.setText(R.string.add_smart_group);
        textView_rightClick.setVisibility((mGroupType == CommonConst.SmartGroupType.GROUP_TYPE_CUSTOMER) ? View.VISIBLE : View.GONE);

        loadingView = findViewById(R.id.loading_view);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.loadSmartGroupFolders(true, true);
            }
        });
        pullToRefreshListView = findViewById(R.id.pull_to_refresh_listView);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setEmptyView(getLayoutInflater().inflate(R.layout.empty_view_no_result_default, null));
        listView = pullToRefreshListView.getRefreshableView();
        mAdapter = new SmartGroupFolderAdapter(this, mGroupType, mPresenter.getDataList(), this);
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
            case R.id.tv_right_click:
                showGroupAddDialog(null);
                break;
            case R.id.tv_do_search:
                EditSearchConfigActivity.launch(RequestCodeConst.INT_REQUEST_SET_SEARCH_CONDITION, this, mGroupType == CommonConst.SmartGroupType.GROUP_TYPE_COMPANY ? IEditSearchConfigView.MODE_SG_COMPANY_HISTORY : IEditSearchConfigView.MODE_SG_TITLE_HISTORY, 0, mPresenter.getLastKeyword());
                break;
        }
    }

    private void updateSearchView() {
        textView_search.setVisibility((mGroupType == CommonConst.SmartGroupType.GROUP_TYPE_COMPANY || mGroupType == CommonConst.SmartGroupType.GROUP_TYPE_TITLE) ? View.VISIBLE : View.GONE);
        if (textView_search.getVisibility() == View.VISIBLE) {
            textView_search.setHint((mGroupType == CommonConst.SmartGroupType.GROUP_TYPE_COMPANY ? R.string.group_folder_search_hint_company : R.string.group_folder_search_hint_title));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodeConst.INT_REQUEST_SET_SEARCH_CONDITION && resultCode == Activity.RESULT_OK && data != null) {
            String keyword = data.getStringExtra("keyword");
            textView_search.setText(keyword);
            mPresenter.onKeywordChanged(keyword);
        }
    }

    private void showGroupAddDialog(final SmartGroupFoldersResultBean.SmartGroupFolderBean smartGroup) {
        new AddGroupNameDialog(this, smartGroup != null ? smartGroup.groupName : null, new AddGroupNameDialog.IGroupNameSetListener() {
            @Override
            public void onGroupNameSet(String name) {
                if (smartGroup == null)
                    mPresenter.createGroup(name);
                else
                    mPresenter.updateGroupName(smartGroup, name);
            }

            @Override
            public boolean isGroupNameEnable(String name) {
                if (TextUtils.isEmpty(name)) {
                    callShowToastMessage(null, R.string.input_hint_group_name);
                    return false;
                }
                if (mPresenter.isGroupNameExist(name)) {
                    callShowToastMessage(null, R.string.exist_group_name);
                    return false;
                }
                return true;
            }
        }).show();
    }


    private int getTitleIdByGroupType(int type) {
        int id = R.string.sg_index_time;
        switch (type) {
            case CommonConst.SmartGroupType.GROUP_TYPE_DEGREE:
                id = R.string.sg_index_degree;
                break;
            case CommonConst.SmartGroupType.GROUP_TYPE_TITLE:
                id = R.string.sg_index_title;
                break;
            case CommonConst.SmartGroupType.GROUP_TYPE_COMPANY:
                id = R.string.sg_index_company;
                break;
            case CommonConst.SmartGroupType.GROUP_TYPE_WORK_EXPERIENCE:
                id = R.string.sg_index_work_experience;
                break;
            case CommonConst.SmartGroupType.GROUP_TYPE_CUSTOMER:
                id = R.string.sg_index_customer;
                break;
        }
        return id;
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
    public void callRefreshListItems(int targetPosition) {
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        if (targetPosition == CommonConst.LIST_POSITION_NONE) {
            mAdapter.refresh(mGroupType, null, CommonConst.LIST_POSITION_NONE);
            return;
        }
        View view = AppUtil.getTargetVisibleView(targetPosition, listView);
        if (view != null)
            mAdapter.refresh(mGroupType, view, targetPosition);
    }

    @Override
    public void callUpdateLoadAllDataView(boolean hasLoadAll) {
        pullToRefreshListView.setFootReboundInsteadLoad(hasLoadAll);
    }

    @Override
    public void callShowLoadingView(String msg) {
        loadingView.startLoading(null);
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, SmartGroupHomeActivity.class);
        activity.startActivity(intent);
    }

    public static void launch(Activity activity, int groupType) {
        Intent intent = new Intent(activity, SmartGroupHomeActivity.class);
        intent.putExtra("groupType", groupType);
        activity.startActivity(intent);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadSmartGroupFolders(true, false);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, final Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.ADAPTER_EVENT_MODIFY_SMART_GROUP_NAME:
                showGroupAddDialog((SmartGroupFoldersResultBean.SmartGroupFolderBean) input);
                break;
            case AdapterEventIdConst.ADAPTER_EVENT_DELETE_SMART_GROUP:
                if (SharedPreferenceManager.getBoolean(SharedPreferenceConst.SP_SMART_GROUP_DELETE_HINT_TOGGLE)) {
                    mPresenter.deleteGroup((SmartGroupFoldersResultBean.SmartGroupFolderBean) input);
                    return;
                }
                new ConfirmDeleteGroupDialog(this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.deleteGroup((SmartGroupFoldersResultBean.SmartGroupFolderBean) input);
                    }
                }).show();
                break;
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL:
                GroupDetailActivity.launch(this, ((SmartGroupFoldersResultBean.SmartGroupFolderBean) input), mGroupType, RequestCodeConst.INT_REQUEST_SMART_GROUP_DETAIL);
                break;
        }
    }
}
