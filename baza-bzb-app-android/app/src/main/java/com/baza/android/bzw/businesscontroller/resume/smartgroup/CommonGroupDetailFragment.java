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
import com.baza.android.bzw.businesscontroller.resume.smartgroup.dialog.TargetTimeSelectedView;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.presenter.CommonGroupDetailPresenter;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.viewinterface.ICommonGroupDetailView;
import com.baza.android.bzw.businesscontroller.search.EditSearchConfigActivity;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IEditSearchConfigView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.baza.android.bzw.widget.LoadingView;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by Vincent.Lei on 2018/9/3.
 * Title：
 * Note：
 */
public class CommonGroupDetailFragment extends BaseFragment implements ICommonGroupDetailView, View.OnClickListener, PullToRefreshBase.OnRefreshListener2, CompoundButton.OnCheckedChangeListener, BaseBZWAdapter.IAdapterEventsListener {
    PullToRefreshListView pullToRefreshListView;
    ListView listView;
    LoadingView loadingView;
    TextView textView_search;
    TextView textView_countInfo;
    TextView textView_timeSelected;
    View view_empty;

    private SmartGroupFoldersResultBean.SmartGroupFolderBean mSmartGroupFolderBean;
    private int mType;
    private CommonGroupDetailPresenter mPresenter;
    private SFGroupDetailAdapter mAdapter;
    private TargetTimeSelectedView mTargetTimeSelectedView;

    @Override
    protected int getLayoutId() {
        return R.layout.smart_group_fragment_common_detail;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.smart_group_detail);
    }

    @Override
    protected void initWhenOnCreatedViews(View mRootView) {
        textView_search = mRootView.findViewById(R.id.tv_do_search);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mSmartGroupFolderBean = (SmartGroupFoldersResultBean.SmartGroupFolderBean) bundle.getSerializable("groupFolder");
            mType = bundle.getInt("type");
            TextView textView = mRootView.findViewById(R.id.tv_title);
            textView.setText(mSmartGroupFolderBean.groupName);
            if (mType == CommonConst.SmartGroupType.GROUP_TYPE_WORK_EXPERIENCE || mType == CommonConst.SmartGroupType.GROUP_TYPE_DEGREE)
                textView_search.setHint(mResources.getString(R.string.smart_group_search_hint, mSmartGroupFolderBean.groupName));
        }
        mPresenter = new CommonGroupDetailPresenter(this, mType, mSmartGroupFolderBean);

        mRootView.findViewById(R.id.ibtn_left_click).setOnClickListener(this);
        mRootView.findViewById(R.id.tv_do_search).setOnClickListener(this);
        view_empty = mRootView.findViewById(R.id.view_empty_view);
        pullToRefreshListView = mRootView.findViewById(R.id.pull_to_refresh_listView);
        pullToRefreshListView.setOnRefreshListener(this);
        loadingView = mRootView.findViewById(R.id.loading_view);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.loadInitData(true, true);
            }
        });
        listView = pullToRefreshListView.getRefreshableView();
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        if (mType == CommonConst.SmartGroupType.GROUP_TYPE_TIME) {
            textView_countInfo = mRootView.findViewById(R.id.tv_time_group_count_info);
            textView_timeSelected = mRootView.findViewById(R.id.tv_time_select);
            String year = (mSmartGroupFolderBean.groupName != null && mSmartGroupFolderBean.groupName.length() > 4 ? mSmartGroupFolderBean.groupName.substring(0, 4) : null);
            textView_timeSelected.setText(year + "年全部");
            textView_timeSelected.setOnClickListener(this);
            mRootView.findViewById(R.id.cl_time_operate).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.tv_do_search).setVisibility(View.GONE);
            mPresenter.onTimeSelected(year, null, null, false);
        } else {
            View headView = getLayoutInflater().inflate(R.layout.collection_head_action, null);
            listView.addHeaderView(headView);
            textView_countInfo = headView.findViewById(R.id.tv_count_info);
            ((CheckBox) headView.findViewById(R.id.cb_job_hunter)).setOnCheckedChangeListener(this);
        }
        mAdapter = new SFGroupDetailAdapter(getActivity(), mType, mPresenter.getDataList(), null, this);
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
        view_empty.setVisibility((totalCount > 0 ? View.GONE : View.VISIBLE));
        if (mType == CommonConst.SmartGroupType.GROUP_TYPE_TIME) {
            textView_countInfo.setText(mResources.getString(R.string.smart_group_folder_inner_value, FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(totalCount, false, true)));
            return;
        }
        String totalStr = FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(totalCount, false, true);
        String jobHunterStr = FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(extraCount, false, true);
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.search_result_count_mine_hint_value, totalStr, jobHunterStr));
        int color = mResources.getColor(R.color.text_color_orange_FF7700);
        spannableString.setSpan(new ForegroundColorSpan(color), 1, 1 + totalStr.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(color), spannableString.length() - 1 - jobHunterStr.length(), spannableString.length() - 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_countInfo.setText(spannableString);
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
            case R.id.tv_do_search:
                EditSearchConfigActivity.launch(RequestCodeConst.INT_REQUEST_SET_SEARCH_CONDITION, getActivity(), IEditSearchConfigView.MODE_DEFAULT_HISTORY, 0, mPresenter.getLastKeyword());
                break;
            case R.id.tv_time_select:
                if (mTargetTimeSelectedView == null)
                    mTargetTimeSelectedView = new TargetTimeSelectedView(getActivity(), new TargetTimeSelectedView.ITimeDataProvider() {
                        @Override
                        public ArrayList<String> getYearSelector() {
                            return mPresenter.getTimeSelector().yearList;
                        }

                        @Override
                        public ArrayList<String> getMonthSelectorByYear(String year) {
                            return mPresenter.getTimeSelector().monthList;
                        }

                        @Override
                        public ArrayList<String> getDaySelectorByYearAndMonth(String year, String month) {
                            return mPresenter.getTimeSelector().monthDaySet.get(month);
                        }

                        @Override
                        public void onTimeSelectorSet(String year, String month, String day) {
                            if ("全部".equals(month)) {
                                textView_timeSelected.setText(year + "年全部");
                                mPresenter.onTimeSelected(year, null, null, true);
                            } else if ("全部".equals(day)) {
                                textView_timeSelected.setText(year + "年" + month + "月");
                                mPresenter.onTimeSelected(year, month, null, true);
                            } else {
                                textView_timeSelected.setText(year + "年" + month + "月" + day + "日");
                                mPresenter.onTimeSelected(year, month, day, true);
                            }

                        }
                    });
                mTargetTimeSelectedView.show();
                break;
        }
    }

    @Override
    protected void onFragmentDeadForApp() {
        mPresenter.onDestroy();
        mAdapter.onDestroy();
        super.onFragmentDeadForApp();
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodeConst.INT_REQUEST_SET_SEARCH_CONDITION && resultCode == Activity.RESULT_OK && data != null) {
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPresenter.onOnlySearchJobFinderSet(isChecked);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL:
                ResumeBean resumeBean = (ResumeBean) input;
                ResumeDetailActivity.launch(getActivity(), new IResumeDetailView.IntentParam(resumeBean.candidateId));
                UserInfoManager.getInstance().cacheReadResume(resumeBean.candidateId);
                callRefreshListItems(position);
                break;
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_COLLECTION:
                mPresenter.collection(position);
                break;
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_ADD_REMARK:
                resumeBean = (ResumeBean) input;
                ResumeDetailActivity.launch(getActivity(), new IResumeDetailView.IntentParam(resumeBean.candidateId).isAddRemarkMode(true));
                break;
            case AdapterEventIdConst.RESUME_LIST_SINGLE__UPDATE:
                ResumeUpdateCardActivity.launch(getActivity(), ((ResumeBean) input).candidateId, RequestCodeConst.INT_REQUEST_NONE);
                break;
        }
    }
}
