package com.baza.android.bzw.businesscontroller.account;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ListPopupWindow;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.businesscontroller.account.adapter.ResumeClassifyAdapter;
import com.baza.android.bzw.businesscontroller.account.presenter.ResumeClassifyPresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IResumeClassifyView;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.baza.android.bzw.widget.LoadingView;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Vincent.Lei on 2018/8/24.
 * Title：
 * Note：
 */
public class ResumeClassifyFragment extends BaseFragment implements IResumeClassifyView, PullToRefreshBase.OnRefreshListener2, View.OnClickListener, BaseBZWAdapter.IAdapterEventsListener {
    PullToRefreshListView pullToRefreshListView;
    ListView listView;
    LoadingView loadingView;
    TextView textView_allCount;
    TextView textView_filterType;
    View emptyView;

    private ResumeClassifyAdapter mAdapter;
    private int mType;
    private String mTitle;
    private ResumeClassifyPresenter mPresenter;
    private ListPopupWindow mListPopupWindow;
    private String[] mFilterMenuItem;
    private String[] mFilterSourcePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt("type");
        mTitle = getArguments().getString("title", "");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.account_fragmet_resume_classify;
    }

    @Override
    protected String getPageTitle() {
        return mTitle;
    }

    @Override
    protected void initWhenOnCreatedViews(View mRootView) {
        mPresenter = new ResumeClassifyPresenter(this, mType);
        pullToRefreshListView = mRootView.findViewById(R.id.pull_to_refresh_listView);
        loadingView = mRootView.findViewById(R.id.loading_view);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.loadClassifyList(false, true);
            }
        });
        listView = pullToRefreshListView.getRefreshableView();
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setOnRefreshListener(this);
        View headView = getLayoutInflater().inflate(R.layout.account_resume_classify_head_action, null);
        textView_allCount = headView.findViewById(R.id.tv_count_info);
        textView_filterType = headView.findViewById(R.id.tv_filter_type);
        if (mType == IResumeClassifyView.IMPORT_FROM_LOCAL || mType == IResumeClassifyView.IMPORT_FROM_OTHER_PLATFORM) {
            textView_filterType.setVisibility(View.VISIBLE);
            textView_filterType.setOnClickListener(this);
        }
        listView.addHeaderView(headView);
        mAdapter = new ResumeClassifyAdapter(getActivity(), mType, mPresenter.getDataList(), this);
        listView.setAdapter(mAdapter);
        emptyView = mRootView.findViewById(R.id.view_empty_view);
    }

    @Override
    protected void initWhenOnActivityCreated() {
        mPresenter.initialize();
    }

    @Override
    protected void changedUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    protected boolean isStatusBarTintEnabledWhenSDKReachKITKAT() {
        return false;
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
    public void callShowLoadingView(String msg) {
        loadingView.startLoading(msg);
    }

    @Override
    public void callRefreshListItems() {
        mAdapter.notifyDataSetChanged();
        emptyView.setVisibility((mPresenter.getDataList().size() > 0 ? View.GONE : View.VISIBLE));
    }

    @Override
    public void callUpdateLoadAllDataView(boolean hasLoadAll) {
        pullToRefreshListView.setFootReboundInsteadLoad(hasLoadAll);
    }

    @Override
    public void callUpdateAllCountView(int totalCount) {
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.resume_classify_total_count_value, FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(totalCount, false, true)));
        spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_orange_FF7700)), 2, spannableString.length() - 3, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_allCount.setText(spannableString);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadClassifyList(true, false);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.loadClassifyList(false, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_filter_type:
                if (mListPopupWindow == null) {
                    mFilterMenuItem = mResources.getStringArray(mType == IResumeClassifyView.IMPORT_FROM_LOCAL ? R.array.account_resume_sub_classify_menu_local : R.array.account_resume_sub_classify_menu_platform);
                    mFilterSourcePath = mResources.getStringArray(mType == IResumeClassifyView.IMPORT_FROM_LOCAL ? R.array.account_resume_sub_classify_menu_local_source_path : R.array.account_resume_sub_classify_menu_platform_source_path);
                    mListPopupWindow = new ListPopupWindow(getActivity());
                    mListPopupWindow.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.list_pop_defaylt_menu_item, mFilterMenuItem));
                    mListPopupWindow.setWidth(ScreenUtil.screenWidth / 3);
                    mListPopupWindow.setHorizontalOffset(-v.getWidth());
                    mListPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    mListPopupWindow.setModal(true);
                    mListPopupWindow.setAnchorView(v);
                    mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mListPopupWindow.dismiss();
                            textView_filterType.setText(mFilterMenuItem[position]);
                            mPresenter.setSourcePath(mFilterSourcePath[position]);
                        }
                    });
                }
                mListPopupWindow.show();
                break;
        }
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL:
                ResumeDetailActivity.launch(getActivity(), new IResumeDetailView.IntentParam((String) input));
                break;
        }
    }
}
