package com.baza.android.bzw.businesscontroller.resume.smartgroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.smartgroup.SmartGroupFoldersResultBean;
import com.baza.android.bzw.businesscontroller.find.updateengine.ResumeUpdateCardActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.adapter.SFGroupDetailAdapter;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.presenter.SFGroupDetailPresenter;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.viewinterface.ISFGroupDetailView;
import com.baza.android.bzw.businesscontroller.search.EditSearchConfigActivity;
import com.baza.android.bzw.businesscontroller.search.ResumeSearchActivity;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IEditSearchConfigView;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IResumeSearchView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.baza.android.bzw.widget.LoadingView;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Vincent.Lei on 2018/8/31.
 * Title：
 * Note：
 */
public class SFGroupDetailFragment extends BaseFragment implements ISFGroupDetailView, View.OnClickListener, PullToRefreshBase.OnRefreshListener2, BaseBZWAdapter.IAdapterEventsListener, CompoundButton.OnCheckedChangeListener {
    PullToRefreshListView pullToRefreshListView;
    ListView listView;
    LoadingView loadingView;
    TextView textView_search;
    TextView textView_right;
    View view_editArea;
    TextView textView_allSelected;
    TextView textView_countInfo;
    View view_empty;
    private SFGroupDetailPresenter mPresenter;
    private SFGroupDetailAdapter mAdapter;
    private SmartGroupFoldersResultBean.SmartGroupFolderBean mSmartGroupFolderBean;

    @Override
    protected int getLayoutId() {
        return R.layout.smart_group_fragment_sf_detail;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.smart_group_detail);
    }

    @Override
    protected void initWhenOnCreatedViews(View mRootView) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mSmartGroupFolderBean = (SmartGroupFoldersResultBean.SmartGroupFolderBean) bundle.getSerializable("groupFolder");
            TextView textView = mRootView.findViewById(R.id.tv_title);
            textView.setText(mSmartGroupFolderBean.groupName);
        }
        mPresenter = new SFGroupDetailPresenter(this, mSmartGroupFolderBean.id);
        mRootView.findViewById(R.id.ibtn_left_click).setOnClickListener(this);
        textView_right = mRootView.findViewById(R.id.tv_right_click);
        textView_right.setText(R.string.manager);
        textView_right.setOnClickListener(this);

        pullToRefreshListView = mRootView.findViewById(R.id.pull_to_refresh_listView);
        pullToRefreshListView.setOnRefreshListener(this);
        loadingView = mRootView.findViewById(R.id.loading_view);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.loadResumesInGroup(true, true);
            }
        });
        listView = pullToRefreshListView.getRefreshableView();
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        view_empty = mRootView.findViewById(R.id.view_empty_view);
        view_empty.findViewById(R.id.tv_add_resume_for_empty).setOnClickListener(this);

        textView_search = mRootView.findViewById(R.id.tv_do_search);
        view_editArea = mRootView.findViewById(R.id.cl_edit_area);
        textView_allSelected = mRootView.findViewById(R.id.tv_all_selected);
        textView_search.setOnClickListener(this);
        textView_allSelected.setOnClickListener(this);
        mRootView.findViewById(R.id.tv_move_out).setOnClickListener(this);
        mRootView.findViewById(R.id.tv_add_resume).setOnClickListener(this);
        callUpdateSelectedCountView(0);

        View headView = getLayoutInflater().inflate(R.layout.collection_head_action, null);
        listView.addHeaderView(headView);
        textView_countInfo = headView.findViewById(R.id.tv_count_info);
        ((CheckBox) headView.findViewById(R.id.cb_job_hunter)).setOnCheckedChangeListener(this);

        mAdapter = new SFGroupDetailAdapter(getActivity(), CommonConst.SmartGroupType.GROUP_TYPE_CUSTOMER, mPresenter.getDataList(), mPresenter.getSelectedResumes(), this);
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void initWhenOnActivityCreated() {
        mPresenter.initialize();
    }

    @Override
    protected void changedUIToFitSDKReachKITKAT(int statusBarHeight) {

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
        if (targetPosition == CommonConst.LIST_POSITION_NONE) {
            mAdapter.notifyDataSetChanged();
            return;
        }
        View view = AppUtil.getTargetVisibleView(targetPosition, listView);
        if (view != null)
            mAdapter.refreshItem(view, targetPosition);
    }

    @Override
    public void callUpdateLoadAllDataView(boolean hasLoadAll) {
        pullToRefreshListView.setFootReboundInsteadLoad(hasLoadAll);
    }

    @Override
    public void callUpdateLoadCountView(int totalCount, int extraCount) {
        view_empty.setVisibility(totalCount > 0 ? View.GONE : View.VISIBLE);
        String totalStr = FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(totalCount, false, true);
        String jobHunterStr = FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(extraCount, false, true);
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.search_result_count_mine_hint_value, totalStr, jobHunterStr));
        int color = mResources.getColor(R.color.text_color_orange_FF7700);
        spannableString.setSpan(new ForegroundColorSpan(color), 1, 1 + totalStr.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(color), spannableString.length() - 1 - jobHunterStr.length(), spannableString.length() - 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_countInfo.setText(spannableString);
    }

    @Override
    public void callUpdateSelectedCountView(int count) {
        textView_allSelected.setText(mResources.getString(R.string.group_add_all_selected_value, String.valueOf(count)));
    }

    @Override
    public void callShowLoadingView(String msg) {
        loadingView.startLoading(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                getActivity().finish();
                break;
            case R.id.tv_right_click:
                textView_right.setText(mAdapter.isOnEdit() ? R.string.manager : R.string.complete);
                view_editArea.setVisibility(mAdapter.isOnEdit() ? View.GONE : View.VISIBLE);
                textView_search.setVisibility(mAdapter.isOnEdit() ? View.VISIBLE : View.GONE);
                mPresenter.prepareToEdit(!mAdapter.isOnEdit());
                callUpdateSelectedCountView(0);
                mAdapter.onEditModeChanged();
                break;
            case R.id.tv_add_resume:
            case R.id.tv_add_resume_for_empty:
                ResumeSearchActivity.launch(getActivity(), new IResumeSearchView.Param().searchMode(CommonConst.INT_SEARCH_TYPE_GROUP_ADD).smartGroupId(mSmartGroupFolderBean.id), RequestCodeConst.INT_REQUEST_SELECT_RESUMES_FOR_GROUP_ADD);
                break;
            case R.id.tv_do_search:
                EditSearchConfigActivity.launch(RequestCodeConst.INT_REQUEST_SET_SEARCH_CONDITION, getActivity(), IEditSearchConfigView.MODE_DEFAULT_HISTORY, 0, mPresenter.getLastKeyword());
                break;
            case R.id.tv_move_out:
                mPresenter.removeAllResume();
                break;
            case R.id.tv_all_selected:
                mPresenter.selectedAll();
                break;
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodeConst.INT_REQUEST_SELECT_RESUMES_FOR_GROUP_ADD && resultCode == Activity.RESULT_OK) {
            mPresenter.delayToLoadResumesInGroup(true, true);
        } else if (requestCode == RequestCodeConst.INT_REQUEST_SET_SEARCH_CONDITION && resultCode == Activity.RESULT_OK && data != null) {
            String keyword = data.getStringExtra("keyword");
            textView_search.setText(keyword);
            mPresenter.onKeywordChanged(keyword);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadResumesInGroup(false, true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadResumesInGroup(false, false);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.ADAPTER_EVENT_REMOVE_RESUMNE_FROM_GROUP:
                mPresenter.removeResume((ResumeBean) input);
                break;
            case AdapterEventIdConst.ADAPTER_EVENT_ADD_RESUMNE_TO_GROUP:
                callUpdateSelectedCountView((int) input);
                break;
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL:
                ResumeDetailActivity.launch(getActivity(), new IResumeDetailView.IntentParam(((ResumeBean) input).candidateId));
                break;
            case AdapterEventIdConst.RESUME_LIST_SINGLE__UPDATE:
                ResumeUpdateCardActivity.launch(getActivity(), ((ResumeBean) input).candidateId, RequestCodeConst.INT_REQUEST_NONE);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPresenter.onOnlySearchJobFinderSet(isChecked);
        callUpdateSelectedCountView(0);
    }

    @Override
    protected void onFragmentDeadForApp() {
        mPresenter.onDestroy();
        mAdapter.onDestroy();
        super.onFragmentDeadForApp();
    }
}
