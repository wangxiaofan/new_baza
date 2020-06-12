package com.baza.android.bzw.businesscontroller.company;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.company.adapter.CompanyResumeListAdapter;
import com.baza.android.bzw.businesscontroller.company.presenter.CompanyTalentPresenter;
import com.baza.android.bzw.businesscontroller.company.viewinterface.ICompanySearchView;
import com.baza.android.bzw.businesscontroller.company.viewinterface.ICompanyTalentView;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.log.logger.FirmTalentLogger;
import com.baza.android.bzw.manager.UserInfoManager;
import com.baza.android.bzw.widget.LoadingView;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.slib.utils.AppUtil;
import com.slib.utils.ToastUtil;

public class CompanyTalentActivity extends BaseActivity implements View.OnClickListener, ICompanyTalentView, PullToRefreshBase.OnRefreshListener2, BaseBZWAdapter.IAdapterEventsListener {

    private PullToRefreshListView pullToRefreshListView;

    private LoadingView loadingView;

    private ListView listView;

    private View view_empty;

    private CompanyTalentPresenter mPresenter;

    private CompanyResumeListAdapter mAdapter;

    private FirmTalentLogger talentLogger = new FirmTalentLogger();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_company_talent;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_company_talent);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new CompanyTalentPresenter(this);
        talentLogger.setPageCode(this, "FirmTalentList");
        TextView textView_title = findViewById(R.id.tv_title);
        textView_title.setText(R.string.talent_company_talent);
        loadingView = findViewById(R.id.loading_view);
        pullToRefreshListView = findViewById(R.id.pull_to_refresh_listView);
        listView = pullToRefreshListView.getRefreshableView();
        View view_emptyView = findViewById(R.id.view_empty_view);
        View headView = getLayoutInflater().inflate(R.layout.head_view_inner_company_talent, null);
        listView.addHeaderView(headView);
        headView.findViewById(R.id.fl_search_entrance).setOnClickListener(this);
        ((TextView) headView.findViewById(R.id.tv_do_search)).setHint(R.string.company_talent_search_hint);

        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setEmptyView(view_emptyView);
        pullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.loadMyResumeList(true);
            }
        });

        mAdapter = new CompanyResumeListAdapter(this, mPresenter.getDataList(), false, true, this);
        listView.setAdapter(mAdapter);
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, CompanyTalentActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.fl_search_entrance:
                CompanySearchActivity.launch(this, new ICompanySearchView.Param().searchMode(CommonConst.INT_SEARCH_TYPE_COMPANY));
                break;
        }
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
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        ResumeBean resumeBean = (ResumeBean) input;
        switch (adapterEventId) {
            case AdapterEventIdConst.EVENT_ID_COMPANY_ITEM_CLICK:
                CompanyDetailActivity.launch(this, new IResumeDetailView.IntentParam(resumeBean.candidateId).isCompany(true).pageCode("FirmTalent"));
                UserInfoManager.getInstance().cacheReadResume(resumeBean.candidateId);
                callRefreshListItems(position);
                break;

            case AdapterEventIdConst.EVENT_ID_COMPANY_ITEM_COLLECTION:
                mPresenter.collection(position, resumeBean);
                if (resumeBean.collectStatus == 0) {
                    talentLogger.collecEvent(this, "Collect", resumeBean.candidateId, resumeBean.firmId);
                } else {
                    talentLogger.collecEvent(this, "CancelCollect", resumeBean.candidateId, resumeBean.firmId);
                }
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
    public void callShowSpecialToastMsg(int type, String msg, int msgId) {
        switch (type) {
            case CompanyTalentPresenter.SELF_TOAST_COLLECTION:
                ImageView imageView = new ImageView(mApplication);
                imageView.setImageResource((R.string.un_collection_success == msgId ? R.drawable.image_uncollection : R.drawable.image_collection));
                ToastUtil.selfToast(mApplication, imageView);
                break;
        }
    }
}
