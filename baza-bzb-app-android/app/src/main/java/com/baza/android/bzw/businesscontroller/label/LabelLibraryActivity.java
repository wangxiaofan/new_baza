package com.baza.android.bzw.businesscontroller.label;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.searchfilterbean.SearchFilterInfoBean;
import com.baza.android.bzw.businesscontroller.label.adapter.LabelLibsAdapter;
import com.baza.android.bzw.businesscontroller.label.presenter.LabelLibraryPresenter;
import com.baza.android.bzw.businesscontroller.label.viewinterface.ILabelLibraryView;
import com.baza.android.bzw.businesscontroller.search.ResumeSearchActivity;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IResumeSearchView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.dialog.AddLabelDialog;
import com.baza.android.bzw.widget.dialog.SimpleHintDialog;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/5/25.
 * Title：
 * Note：
 */

public class LabelLibraryActivity extends BaseActivity implements ILabelLibraryView, View.OnClickListener, BaseBZWAdapter.IAdapterEventsListener {
    @BindView(R.id.tv_title)
    TextView textView_title;
    @BindView(R.id.tv_right_click)
    TextView textView_rightClick;
    @BindView(R.id.tv_no_data_hint)
    TextView textView_noDataHint;
    @BindView(R.id.lv)
    ListView listView;
    @BindView(R.id.loading_view)
    LoadingView loadingView;

    private LabelLibraryPresenter mPresenter;
    private LabelLibsAdapter mAdapter;
    private SearchFilterInfoBean mSearchFilterInfoBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_label_library;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_label_library);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        textView_title.setText(R.string.label_group);
        textView_rightClick.setOnClickListener(this);
        textView_rightClick.setVisibility(View.GONE);
        textView_rightClick.setText(R.string.new_add);
        textView_noDataHint.setText(getNoDataHintMsg());
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.loadLabelLibrary();
            }
        });
        mPresenter = new LabelLibraryPresenter(this);

        mAdapter = new LabelLibsAdapter(this, mPresenter.getLabelList(), this);
        listView.setAdapter(mAdapter);
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onActivityDeadForApp() {
        mPresenter.onDestroy();
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, LabelLibraryActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.tv_right_click:
                new AddLabelDialog(this, new AddLabelDialog.IAddLabelListener() {
                    @Override
                    public void onSubmit(String labelText) {
                        mPresenter.createNewTag(labelText);
                    }

                    @Override
                    public boolean onCheck(String labelText) {
                        return mPresenter.checkEnableToCreateNewTag(labelText);
                    }
                });
                break;
        }
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.ADAPTER_EVENT_LABEL_DEL_TARGET:
                showMarkDeletedDialog((Label) input);
                break;
            case AdapterEventIdConst.ADAPTER_EVENT_LABEL_DO_SEARCH:
                if (mSearchFilterInfoBean == null) {
                    mSearchFilterInfoBean = new SearchFilterInfoBean();
                    mSearchFilterInfoBean.labelList = new ArrayList<>(1);
                }
                mSearchFilterInfoBean.labelList.clear();
                mSearchFilterInfoBean.labelList.add((Label) input);
                ResumeSearchActivity.launch(this, new IResumeSearchView.Param().searchMode(CommonConst.INT_SEARCH_TYPE_MINE).searchFilterInfoPrevious(mSearchFilterInfoBean));
                break;
        }
    }

    private void showMarkDeletedDialog(final Label label) {
        new SimpleHintDialog(this).show(mResources.getString(R.string.confirm_del_mark), R.drawable.icon_warn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.deleteLabel(label);
            }
        }, null);
    }

    @Override
    public void callCancelLoadingView(boolean success, int errorCode, String errorMsg) {
        if (success)
            loadingView.finishLoading();
        else
            loadingView.loadingFailed(errorCode, errorMsg);
    }

    @Override
    public void callShowLoadingView() {
        if (!loadingView.isShown())
            loadingView.startLoading(null);
    }

    @Override
    public void callShowNoDataView(boolean noData) {
        textView_noDataHint.setVisibility((noData ? View.VISIBLE : View.GONE));
    }

    @Override
    public void callRefreshLabelsView() {
        mAdapter.notifyDataSetChanged();
        textView_rightClick.setVisibility(View.VISIBLE);
        textView_title.setText(mResources.getString(R.string.label_group) + "（" + mPresenter.getLabelList().size() + "）");
    }

    private SpannableStringBuilder getNoDataHintMsg() {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(mResources.getString(R.string.no_label_library_hint));
        spannableStringBuilder.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_blue_53ABD5)), 11, spannableStringBuilder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(14, true), 11, spannableStringBuilder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }
}
