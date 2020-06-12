package com.baza.android.bzw.businesscontroller.resume.recommend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.recommend.RecommendBean;
import com.baza.android.bzw.businesscontroller.company.CompanyDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.businesscontroller.resume.recommend.adapter.RecommendAdapter;
import com.baza.android.bzw.businesscontroller.resume.recommend.presenter.RecommendPresenter;
import com.baza.android.bzw.businesscontroller.resume.recommend.viewinterface.IRecommendView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.widget.LoadingView;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2019/8/15.
 * Title：
 * Note：
 */
public class RecommendActivity extends BaseActivity implements IRecommendView, View.OnClickListener, PullToRefreshBase.OnRefreshListener2, BaseBZWAdapter.IAdapterEventsListener, CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.pull_to_refresh_listView)
    PullToRefreshListView pullToRefreshListView;
    ListView listView;
    @BindView(R.id.tv_date_show)
    TextView textView_dateShown;
    @BindView(R.id.cb_date_delay_count)
    CheckBox checkBox_delayCount;
    @BindView(R.id.cb_complete_count)
    CheckBox checkBox_completeCount;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    private RecommendAdapter mAdapter;

    private RecommendPresenter mPresenter;
    private int mCurrentYear, mCurrentMonth;

    @Override
    protected int getLayoutId() {
        return R.layout.resume_activity_recommend;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.recommend_title);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        mCurrentYear = calendar.get(Calendar.YEAR);
        mCurrentMonth = calendar.get(Calendar.MONTH) + 1;
        textView_dateShown.setText(mResources.getString(R.string.recommend_date_append, String.valueOf(mCurrentMonth >= 10 ? mCurrentMonth : "0" + mCurrentMonth), String.valueOf(mCurrentYear)));
        mPresenter = new RecommendPresenter(this, mCurrentYear, mCurrentMonth);
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.recommend_title);
        pullToRefreshListView = findViewById(R.id.pull_to_refresh_listView);
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setEmptyView(getLayoutInflater().inflate(R.layout.empty_view_no_result_recommend, null));
        listView = pullToRefreshListView.getRefreshableView();
        mAdapter = new RecommendAdapter(this, mPresenter.getDataList(), this);
        listView.setAdapter(mAdapter);
        updateStateCountView(0, 0);
        textView_dateShown.setClickable(true);
        textView_dateShown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getX() < textView_dateShown.getWidth() / 5) {
                        updateDateSelected(-1);
                        event.setAction(MotionEvent.ACTION_CANCEL);
                        return true;
                    }
                    if (event.getX() > textView_dateShown.getWidth() * 4 / 5) {
                        updateDateSelected(1);
                        event.setAction(MotionEvent.ACTION_CANCEL);
                        return true;
                    }

                }
                return false;
            }
        });
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                loadingView.startLoading(null);
                mPresenter.loadRecommendList();
            }
        });
        checkBox_delayCount.setOnCheckedChangeListener(this);
        checkBox_completeCount.setOnCheckedChangeListener(this);
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    private void updateDateSelected(int value) {
        mCurrentMonth += value;
        if (mCurrentMonth > 12) {
            mCurrentMonth = 1;
            mCurrentYear++;
        } else if (mCurrentMonth == 0) {
            mCurrentMonth = 12;
            mCurrentYear--;
        }
        textView_dateShown.setText(mResources.getString(R.string.recommend_date_append, String.valueOf(mCurrentMonth >= 10 ? mCurrentMonth : "0" + mCurrentMonth), String.valueOf(mCurrentYear)));
        mPresenter.setDate(mCurrentYear, mCurrentMonth);
        loadingView.startLoading(null);
        mPresenter.loadRecommendList();
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, RecommendActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadRecommendList();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void callRefreshListItems(int targetPosition) {
        pullToRefreshListView.onRefreshComplete();
        mAdapter.refreshTargetItemView(listView, targetPosition);
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
    public void updateStateCountView(int delayCount, int completeCount) {
        checkBox_delayCount.setText(mResources.getString(R.string.recommend_delay_count_info, String.valueOf(delayCount)));
        checkBox_completeCount.setText(mResources.getString(R.string.recommend_complete_count_info, String.valueOf(completeCount)));
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.EVENT_ID_RECOMMEND_COMPLETE:
                mPresenter.setRecommendComplete((RecommendBean) input);
                break;
            case AdapterEventIdConst.EVENT_ID_RECOMMEND_DELETE:
                mPresenter.deleteRecommend((RecommendBean) input);
                break;
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL:
                RecommendBean bean = (RecommendBean) input;
                if (bean.isFirm) {
                    CompanyDetailActivity.launch(this, new IResumeDetailView.IntentParam(bean.resumeId).isCompany(true).pageCode("Recommend"));
                } else {
                    ResumeDetailActivity.launch(this, new IResumeDetailView.IntentParam(bean.resumeId));
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == checkBox_delayCount && isChecked) {
            checkBox_completeCount.setOnCheckedChangeListener(null);
            checkBox_completeCount.setChecked(false);
            checkBox_completeCount.setOnCheckedChangeListener(this);
            mPresenter.selectRecommendByState(RecommendBean.STATE_DELAY);
        } else if (buttonView == checkBox_completeCount && isChecked) {
            checkBox_delayCount.setOnCheckedChangeListener(null);
            checkBox_delayCount.setChecked(false);
            checkBox_delayCount.setOnCheckedChangeListener(this);
            mPresenter.selectRecommendByState(RecommendBean.STATE_COMPLETE);
        } else
            mPresenter.clearSelectRecommendState();
    }

}
