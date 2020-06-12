package com.baza.android.bzw.businesscontroller.resume.tab;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.ListPopupWindow;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.find.updateengine.ResumeUpdateCardActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.businesscontroller.resume.jobfind.JobHunterPredictionActivity;
import com.baza.android.bzw.businesscontroller.resume.mine.SeekActivity;
import com.baza.android.bzw.businesscontroller.resume.mine.adapter.MineResumeListAdapter;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.SmartGroupIndexActivity;
import com.baza.android.bzw.businesscontroller.resume.tab.presenter.TabMineResumePresenter;
import com.baza.android.bzw.businesscontroller.resume.tab.viewinterface.ITabMineResumeView;
import com.baza.android.bzw.businesscontroller.search.ResumeSearchActivity;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IResumeSearchView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.log.logger.JobPredictionLogger;
import com.baza.android.bzw.log.logger.ResumeDetailLogger;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.slib.utils.AppUtil;

/**
 * Created by Vincent.Lei on 2017/5/18.
 * Title：
 * Note：
 */

public class MineResumeFragment extends BaseFragment implements ITabMineResumeView, PullToRefreshBase.OnRefreshListener2, BaseBZWAdapter.IAdapterEventsListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    PullToRefreshListView pullToRefreshListView;
    ListView listView;
    View view_empty;
    TextView textView_countInfo;
    View view_predictionHint;
    TextView textView_enableUpdateCount;
    private TabMineResumePresenter mPresenter;
    private MineResumeListAdapter mAdapter;
    private JobPredictionLogger mJobPredictionLogger;
    private ListPopupWindow mPopupWindowMenu;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_candidate;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_mine_candidate);
    }

    @Override
    protected void initWhenOnCreatedViews(View mRootView) {
        mPresenter = new TabMineResumePresenter(this, getArguments());
        pullToRefreshListView = mRootView.findViewById(R.id.pull_to_refresh_listView);
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setOnRefreshListener(this);
        listView = pullToRefreshListView.getRefreshableView();

        View headView = getLayoutInflater().inflate(R.layout.talent_tab_mine_resume_head_action, null);
        headView.findViewById(R.id.fl_search_entrance).setOnClickListener(this);
        view_predictionHint = headView.findViewById(R.id.view_prediction_hint);
        textView_enableUpdateCount = headView.findViewById(R.id.tv_resume_enable_update_count);
        textView_countInfo = headView.findViewById(R.id.tv_count_info);
        ((CheckBox) headView.findViewById(R.id.cb_job_hunter)).setOnCheckedChangeListener(this);

        headView.findViewById(R.id.cl_job_hunter_prediction).setOnClickListener(this);
        headView.findViewById(R.id.cl_resume_update).setOnClickListener(this);
        headView.findViewById(R.id.cl_collection).setOnClickListener(this);
        callUpdateSearchCountView(0, 0);
        listView.addHeaderView(headView);


        mAdapter = new MineResumeListAdapter(getActivity(), mPresenter.getDataList(), this);
        listView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mPresenter.onHiddenChanged(hidden);
    }

    @Override
    protected void onFragmentDeadForApp() {
        super.onFragmentDeadForApp();
        mAdapter.onDestroy();
        if (mPresenter != null)
            mPresenter.onDestroy();
    }

    @Override
    protected void initWhenOnActivityCreated() {
        mPresenter.initialize();
    }

    @Override
    protected void changedUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, final int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL:
                ResumeBean resumeBean = (ResumeBean) input;
                ResumeDetailActivity.launch(getActivity(), new IResumeDetailView.IntentParam(resumeBean.candidateId).pageCode("MyTalent"));
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


    @Override
    public void callUpdateLoadAllDataView(boolean hasLoadAll) {
        pullToRefreshListView.setFootReboundInsteadLoad(hasLoadAll);
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
    public void callUpdateAllResumeCountView(int allCount) {
    }

    @Override
    public void callSetNoResultHintView(boolean noResult) {
        if (noResult) {
            if (view_empty == null) {
                view_empty = getLayoutInflater().inflate(R.layout.empty_view_mine_candidate, null);
//                view_empty.findViewById(R.id.tv_import_email_resume).setOnClickListener(this);
                TextView textView_pcHint = view_empty.findViewById(R.id.tv_pc_hint);
                SpannableString spannableString = new SpannableString(mResources.getString(R.string.hint_pc_import_resume));
                spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_blue_53ABD5)), 2, 15, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView_pcHint.setText(spannableString);
            }
            if (view_empty.getTag() == null) {
                listView.addFooterView(view_empty);
                view_empty.setTag(noResult);
            }
        } else if (view_empty != null) {
            listView.removeFooterView(view_empty);
            view_empty.setTag(null);
        }
    }

    @Override
    public void callUpdatePredictionHintView(boolean shouldHint) {
        view_predictionHint.setVisibility(shouldHint ? View.VISIBLE : View.GONE);
    }

    @Override
    public void callCancelLoadingView(boolean success, int errorCode, String errorMsg) {
        pullToRefreshListView.onRefreshComplete();
    }

    @Override
    public void callUpdateResumeUpdateView(int count) {
//        if (count > 0) {
//            textView_enableUpdateCount.setText(getEnableUpdateCountShowInfo(count));
//            textView_enableUpdateCount.setVisibility(View.VISIBLE);
//        } else
        textView_enableUpdateCount.setVisibility(View.GONE);
    }

    private String getEnableUpdateCountShowInfo(int value) {
        return value < CommonConst.MAX_COUNT_VALUE_LEVEL ? String.valueOf(value) : mResources.getString(R.string.friend_count_value, String.valueOf(value / CommonConst.MAX_COUNT_VALUE_LEVEL));
    }

    @Override
    public void callUpdateSearchCountView(int totalCount, int jobHunterCount) {
        String totalStr = FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(totalCount, false, true);
        String jobHunterStr = FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(jobHunterCount, false, true);
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.search_result_count_mine_hint_value, totalStr, jobHunterStr));
        int color = mResources.getColor(R.color.text_color_orange_FF7700);
        spannableString.setSpan(new ForegroundColorSpan(color), 1, 1 + totalStr.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(color), spannableString.length() - 1 - jobHunterStr.length(), spannableString.length() - 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_countInfo.setText(spannableString);
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadMyResumeList(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadMyResumeList(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.ibtn_left_click:
//                getActivity().finish();
//                break;
            case R.id.cl_resume_update:
                SeekActivity.launch(getActivity());
                break;
            case R.id.cl_job_hunter_prediction:
                JobHunterPredictionActivity.launch(getActivity());
                if (mJobPredictionLogger == null)
                    mJobPredictionLogger = new JobPredictionLogger();
                mJobPredictionLogger.sendClickEntranceLog(this);
                break;
            case R.id.cl_collection:
                SmartGroupIndexActivity.launch(getActivity());
                break;
            case R.id.fl_search_entrance:
                ResumeSearchActivity.launch(getActivity(), new IResumeSearchView.Param().formComponentName(this.getClass().getName()).searchMode(CommonConst.INT_SEARCH_TYPE_MINE));
                break;
//            case R.id.ibtn_right_click:
//                if (mPopupWindowMenu == null) {
//                    List<String> list = new ArrayList<>(2);
////                    list.add(mResources.getString(R.string.mine_collection));
//                    list.add(mResources.getString(R.string.mine_seek));
//                    list.add(mResources.getString(R.string.label_group));
//                    mPopupWindowMenu = new ListPopupWindow(getActivity());
//                    mPopupWindowMenu.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.list_pop_defaylt_menu_item, list));
//                    mPopupWindowMenu.setWidth(ScreenUtil.screenWidth / 3);
//                    mPopupWindowMenu.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//                    mPopupWindowMenu.setHorizontalOffset((int) (-v.getWidth() * 1.5f));
//                    mPopupWindowMenu.setModal(true);
//                }
//                mPopupWindowMenu.setAnchorView(v);
//                mPopupWindowMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        mPopupWindowMenu.dismiss();
//                        if (position == 0)
//                            SeekActivity.launch(getActivity());
//                        else LabelLibraryActivity.launch(getActivity());
//                    }
//                });
//                mPopupWindowMenu.show();
//                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPresenter.onJobHunterChanged(isChecked);
    }

    @Override
    protected boolean isUseDefaultStatusBarBackground() {
        return false;
    }
}
