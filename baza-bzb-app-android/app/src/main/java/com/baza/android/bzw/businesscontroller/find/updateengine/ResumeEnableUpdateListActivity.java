package com.baza.android.bzw.businesscontroller.find.updateengine;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.ResumeUpdateTransformParamBean;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeEnableUpdateBaseInfoBean;
import com.baza.android.bzw.businesscontroller.account.ImportPlatformListActivity;
import com.baza.android.bzw.businesscontroller.account.RightCenterActivity;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.businesscontroller.find.updateengine.adapter.UpdateResumeListAdapter;
import com.baza.android.bzw.businesscontroller.find.updateengine.presenter.ResumeEnableUpdateListPresenter;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IResumeEnableUpdateListView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.ScrollShowImageView;
import com.baza.android.bzw.widget.dialog.UpdateByEngineFeedBackDialog;
import com.slib.utils.AppUtil;
import com.slib.utils.DialogUtil;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Vincent.Lei on 2017/8/22.
 * Title：简历更新列表
 * Note：
 */

public class ResumeEnableUpdateListActivity extends BaseActivity implements IResumeEnableUpdateListView, CompoundButton.OnCheckedChangeListener, View.OnClickListener, BaseBZWAdapter.IAdapterEventsListener, PullToRefreshBase.OnRefreshListener2 {
    PullToRefreshListView pullToRefreshListView;
    LoadingView loadingView;
    ListView listView;
    View view_footNoData;
    Button button_update;
    ScrollShowImageView scrollShowImageView;
    TextView textView_amountHint;
    TextView textView_allCount;

    private ResumeEnableUpdateListPresenter mPresenter;
    private UpdateResumeListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_resume_enable_update_list;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_candidate_enable_update_list);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new ResumeEnableUpdateListPresenter(this, getIntent());

        TextView textView_title = findViewById(R.id.tv_title);
        textView_title.setText(R.string.candidate_update);
        TextView textView_rightClick = findViewById(R.id.tv_right_click);
        textView_rightClick.setText(R.string.title_already_updated_list);

        button_update = findViewById(R.id.btn_update);
        pullToRefreshListView = findViewById(R.id.pull_to_refresh_listView);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        pullToRefreshListView.setOnRefreshListener(this);
        listView = pullToRefreshListView.getRefreshableView();

        View headView = getLayoutInflater().inflate(R.layout.resume_update_hone_head_action, null);
        textView_amountHint = headView.findViewById(R.id.tv_amount_info);
        textView_allCount = headView.findViewById(R.id.tv_count_info);
        headView.findViewById(R.id.tv_get_amount).setOnClickListener(this);
        headView.findViewById(R.id.fl_search_entrance).setOnClickListener(this);
        ((CheckBox) headView.findViewById(R.id.cb_job_hunter)).setOnCheckedChangeListener(this);
        listView.addHeaderView(headView);

        FrameLayout view_empty = (FrameLayout) getLayoutInflater().inflate(R.layout.empty_view_no_update_list, null);
        view_footNoData = view_empty.getChildAt(0);
        view_footNoData.setVisibility(View.GONE);
        view_footNoData.findViewById(R.id.tv_import_resume).setOnClickListener(this);
        listView.addFooterView(view_empty);

        mAdapter = new UpdateResumeListAdapter(this, mPresenter.getEnableUpdateList(), true, this);
        listView.setAdapter(mAdapter);

        loadingView = findViewById(R.id.loading_view);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.loadInitData();
            }
        });

        scrollShowImageView = findViewById(R.id.scroll_show_image_view);
        pullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                scrollShowImageView.onScroll(firstVisibleItem, visibleItemCount);
            }
        });
        mPresenter.initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null)
            mPresenter.onResume();
    }


    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {
    }

    @Override
    protected void onActivityDeadForApp() {
        if (mPresenter != null)
            mPresenter.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!mPresenter.isEnableGoBack())
            return;
        super.onBackPressed();
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, ResumeEnableUpdateListActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                onBackPressed();
                break;
            case R.id.tv_right_click:
                ResumeUpdatedRecordsActivity.launch(this);
                break;
            case R.id.tv_import_resume:
                ImportPlatformListActivity.launch(this);
                break;
            case R.id.btn_update:
                if (!mPresenter.isOneKeyUpdateEnable()) {
                    mPresenter.shareUpdateEngineAdv();
                    return;
                }
                DialogUtil.doubleButtonShow(this, 0, R.string.confirm_one_key_update, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.updateResumeList();
                    }
                }, null);
                break;
            case R.id.scroll_show_image_view:
                listView.setSelection(0);
                break;
            case R.id.tv_get_amount:
                new AmountLessDialog(this, true, true, new AmountLessDialog.IAmountUpTypeListener() {
                    @Override
                    public void onAmountUpTypeClick(int type) {
                        if (type == AmountLessDialog.TYPE_DAYLY)
                            ImportPlatformListActivity.launch(ResumeEnableUpdateListActivity.this);
                        else
                            RightCenterActivity.launch(ResumeEnableUpdateListActivity.this);
                    }
                }).show();
                break;
            case R.id.fl_search_entrance:
                EnableUpdateResumeSearchActivity.launch(this);
                break;
        }
    }


    @Override
    public void callRefreshListItems(int targetPosition) {
        if (pullToRefreshListView.getVisibility() != View.VISIBLE)
            pullToRefreshListView.setVisibility(View.VISIBLE);
        if (targetPosition == CommonConst.LIST_POSITION_NONE) {
            mAdapter.notifyDataSetChanged();
            return;
        }
        View view = AppUtil.getTargetVisibleView(targetPosition, listView);
        if (view != null)
            mAdapter.refreshItem(view, targetPosition);

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
    public void callUpdateLoadMoreEnable(boolean enable) {
        pullToRefreshListView.setFootReboundInsteadLoad(!enable);
    }


    @Override
    public void callUpdateNoDataView(boolean hasData) {
        view_footNoData.setVisibility((hasData ? View.GONE : View.VISIBLE));
    }

    @Override
    public void callUpdateOneKeyUpdateView(int amount, int listCount, int searchCount) {
        if (listCount <= 0) {
            button_update.setVisibility(View.GONE);
            return;
        }
        button_update.setText((amount > 0 ? mResources.getString(R.string.one_key_to_update_resume_list, String.valueOf((amount > searchCount ? searchCount : amount))) : mResources.getString(R.string.share_to_get_three_amount)));
        button_update.setVisibility(View.VISIBLE);
    }

    @Override
    public void callUpdateAmountInfo() {
        ResumeEnableUpdateBaseInfoBean baseInfoBean = mPresenter.getAmountInfo();
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.amount_hint, String.valueOf(baseInfoBean.limit), String.valueOf(baseInfoBean.totalLimit)));
        spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_orange_FF7700)), 6, spannableString.length() - 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_amountHint.setText(spannableString);
    }

    @Override
    public void callShowFeedBackDialog() {
        new UpdateByEngineFeedBackDialog(this, new UpdateByEngineFeedBackDialog.IUpdateByEngineFeedBackListener() {
            @Override
            public void onFeedBack(int commentType, String msg) {
                mPresenter.feedBack(commentType, msg);
            }

            @Override
            public void onNotHint() {
                mPresenter.setFeedBackDialogNotShowAnymore();
                finish();
            }

            @Override
            public void onNormalClosed() {
                finish();
            }
        }).show();
    }

    @Override
    public void callUpdateSearchCount(int totalCount) {
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.resume_enable_update_count_value, FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(totalCount, false, true)));
        spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_orange_FF7700)), 1, spannableString.length() - 4, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_allCount.setText(spannableString);
    }

    @Override
    public void callUpdateAmountNotEnoughView() {
        new AmountLessDialog(this, false, true, new AmountLessDialog.IAmountUpTypeListener() {
            @Override
            public void onAmountUpTypeClick(int type) {
                if (type == AmountLessDialog.TYPE_DAYLY)
                    RemoteBrowserActivity.launch(ResumeEnableUpdateListActivity.this, null, false, URLConst.LINK_H5_GRADE);
                else
                    RightCenterActivity.launch(ResumeEnableUpdateListActivity.this);
            }
        }).show();
    }

    @Override
    public void callUpdateShareToMuchView() {
        DialogUtil.doubleButtonShow(this, 0, R.string.share_to_much_for_get_update_quality, R.string.cancel, R.string.to_exchange, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RightCenterActivity.launch(ResumeEnableUpdateListActivity.this);
            }
        }, null);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL:
                ResumeBean resumeBean = (ResumeBean) input;
                if (resumeBean.isUpdating()) {
                    callShowToastMessage(null, R.string.on_one_day_update);
                    return;
                }
                ResumeUpdateTransformParamBean mResumeUpdateTransformParam = new ResumeUpdateTransformParamBean(position, mPresenter.getTotalEnableUpdateCount(), mPresenter.getEnableUpdateList());
                mApplication.cacheTransformData(CommonConst.STR_TRANSFORM_UPDATE_RESUME_PARAM, mResumeUpdateTransformParam);
                ResumeUpdateCardActivity.launch(this);
                break;
            case AdapterEventIdConst.RESUME_LIST_SINGLE__UPDATE:
                mPresenter.updateSingleResume((ResumeBean) input);
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadResumeEnableUpdateList(true, false, false);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadResumeEnableUpdateList(false, false, false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPresenter.onJobHunterFilterSelected(isChecked);
    }
}
