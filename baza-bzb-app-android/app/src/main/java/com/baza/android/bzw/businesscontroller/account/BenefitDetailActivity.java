package com.baza.android.bzw.businesscontroller.account;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.account.adapter.BenefitDetailAdapter;
import com.baza.android.bzw.businesscontroller.account.presenter.BenefitDetailPresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IBenefitDetailView;
import com.baza.android.bzw.widget.LoadingView;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Vincent.Lei on 2018/9/4.
 * Title：
 * Note：
 */
public class BenefitDetailActivity extends BaseActivity implements IBenefitDetailView, PullToRefreshBase.OnRefreshListener2, View.OnClickListener {
    PullToRefreshListView pullToRefreshListView;
    LoadingView loadingView;
    ListView listView;
    private BenefitDetailAdapter mAdapter;
    private BenefitDetailPresenter mPresenter;
    private int mType;

    @Override
    protected int getLayoutId() {
        return R.layout.account_activity_benefit_detail;
    }

    @Override
    protected void initOverAll() {
        mType = getIntent().getIntExtra("type", IBenefitDetailView.TYPE_IN);
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(mType == IBenefitDetailView.TYPE_IN ? R.string.right_center_get_record : R.string.exchange_amount_record);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new BenefitDetailPresenter(this, mType);
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(mType == IBenefitDetailView.TYPE_IN ? R.string.right_center_get_record : R.string.exchange_amount_record);
        textView = findViewById(R.id.tv_hint);
        if (mType == IBenefitDetailView.TYPE_IN)
            textView.setVisibility(View.GONE);
        else {
            SpannableString spannableString = new SpannableString(mResources.getString(R.string.benefit_exchange_detail_hint));
            spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_orange_FF7700)), 10, 12, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);
        }

        pullToRefreshListView = findViewById(R.id.pull_to_refresh_listView);
        pullToRefreshListView.setEmptyView(getLayoutInflater().inflate(R.layout.empty_view_no_result_default, null));
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        listView = pullToRefreshListView.getRefreshableView();
        loadingView = findViewById(R.id.loading_view);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.loadBenefitDetail(true);
            }
        });
        pullToRefreshListView.setFootReboundInsteadLoad(true);

        mAdapter = new BenefitDetailAdapter(this, mPresenter.getDataList(), mType == IBenefitDetailView.TYPE_IN, null);
        listView.setAdapter(mAdapter);

        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

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
    public void callRefreshListItems() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void callUpdateLoadAllDataView(boolean hasLoadAll) {
        pullToRefreshListView.setFootReboundInsteadLoad(hasLoadAll);
    }

    @Override
    public void callShowLoadingView(String msg) {
        loadingView.startLoading(msg);
    }

    public static void launch(Activity activity, int type) {
        Intent intent = new Intent(activity, BenefitDetailActivity.class);
        intent.putExtra("type", type);
        activity.startActivity(intent);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadBenefitDetail(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadBenefitDetail(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
        }
    }
}
