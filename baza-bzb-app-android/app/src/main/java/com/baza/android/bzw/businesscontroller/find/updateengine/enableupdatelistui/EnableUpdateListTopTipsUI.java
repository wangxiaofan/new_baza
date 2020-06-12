package com.baza.android.bzw.businesscontroller.find.updateengine.enableupdatelistui;

import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.bean.updateengine.SuggestEnableUpdateTipResultBean;
import com.baza.android.bzw.businesscontroller.find.updateengine.presenter.ResumeEnableUpdateListPresenter;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IResumeEnableUpdateListView;
import com.baza.android.bzw.widget.LineBreakLayout;
import com.slib.utils.AppUtil;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/11/10.
 * Title：
 * Note：
 */

public class EnableUpdateListTopTipsUI implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int ELEMENT_LINE_LIMIT = 2;
    private IResumeEnableUpdateListView mResumeEnableUpdateListView;
    private ResumeEnableUpdateListPresenter mPresenter;
    View mRootView;
    @BindView(R.id.tv_search_count)
    TextView textView_searchCount;
    @BindView(R.id.tv_easy_chose_title)
    TextView textView_easyChoseTitle;
    @BindView(R.id.view_label_tips)
    View view_labelTips;
    @BindView(R.id.view_job_tips)
    View view_JobTips;
    private LineBreakLayout lineBreakLayout_labelContainer;
    private LineBreakLayout lineBreakLayout_jobContainer;
    private ImageView imageView_labelCollapse;
    private ImageView imageView_jobCollapse;

    private boolean mEnableTipsSearch = true;
    private List<SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean> mSuggestJobList;
    private List<SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean> mSuggestLabelList;
    private SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean mLabelSelected;
    private SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean mJobSelected;

    public EnableUpdateListTopTipsUI(IResumeEnableUpdateListView resumeEnableUpdateListView, ResumeEnableUpdateListPresenter presenter) {
        this.mResumeEnableUpdateListView = resumeEnableUpdateListView;
        this.mPresenter = presenter;
        init();
    }

    private void init() {
        mRootView = mResumeEnableUpdateListView.callGetBindActivity().getLayoutInflater().inflate(R.layout.headview_enable_update_list_tips, null);
        ButterKnife.bind(this, mRootView);
        lineBreakLayout_labelContainer = view_labelTips.findViewById(R.id.lbl_group_container);
        lineBreakLayout_jobContainer = view_JobTips.findViewById(R.id.lbl_group_container);
        imageView_labelCollapse = view_labelTips.findViewById(R.id.iv_group_collapse);
        imageView_jobCollapse = view_JobTips.findViewById(R.id.iv_group_collapse);
        imageView_labelCollapse.setOnClickListener(this);
        imageView_labelCollapse.setTag(R.id.hold_tag_id_one, R.id.view_label_tips);
        imageView_jobCollapse.setOnClickListener(this);
        imageView_jobCollapse.setTag(R.id.hold_tag_id_one, R.id.view_job_tips);

        TextView textView = view_labelTips.findViewById(R.id.tv_group_title);
        textView.setText(R.string.group_name_label);
        textView = view_JobTips.findViewById(R.id.tv_group_title);
        textView.setText(R.string.group_name_job);
    }

    public View getRootView() {
        return mRootView;
    }

    public void updateSearchCount(int resultCount) {
        String hint = mResumeEnableUpdateListView.callGetResources().getString(R.string.enable_update_search_result_count_hint_value, AppUtil.formatTob(resultCount));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(hint);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(mResumeEnableUpdateListView.callGetResources().getColor(R.color.text_color_blue_53ABD5)), 2, hint.length() - 6, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_searchCount.setText(spannableStringBuilder);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_group_collapse:
                LineBreakLayout lineBreakLayout = (((int) v.getTag(R.id.hold_tag_id_one)) == R.id.view_label_tips ? lineBreakLayout_labelContainer : lineBreakLayout_jobContainer);
                boolean collapse = (lineBreakLayout.getLineLimit() == ELEMENT_LINE_LIMIT);
                ImageView imageView = (ImageView) v;
                imageView.setImageResource((collapse ? R.drawable.arrow_up_small : R.drawable.arrow_down_small));
                lineBreakLayout.setLineLimit((collapse ? Integer.MAX_VALUE : ELEMENT_LINE_LIMIT));
                break;
        }
    }

    public void updateSuggestEnableUpdateLabelsView(List<SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean> labels) {
        mSuggestLabelList = labels;
        if (mSuggestLabelList != null && !mSuggestLabelList.isEmpty()) {
            addItems(lineBreakLayout_labelContainer, R.id.view_label_tips, mSuggestLabelList);
            if (mEnableTipsSearch) {
                view_labelTips.setVisibility(View.VISIBLE);
                textView_easyChoseTitle.setVisibility(View.VISIBLE);
            }
        } else
            view_labelTips.setVisibility(View.GONE);
    }

    public void updateSuggestEnableUpdateTitleView(List<SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean> titles) {
        mSuggestJobList = titles;
        if (mSuggestJobList != null && !mSuggestJobList.isEmpty()) {
            addItems(lineBreakLayout_jobContainer, R.id.view_job_tips, mSuggestJobList);
            if (mEnableTipsSearch) {
                view_JobTips.setVisibility(View.VISIBLE);
                textView_easyChoseTitle.setVisibility(View.VISIBLE);
            }
        } else
            view_JobTips.setVisibility(View.GONE);
    }

    private void addItems(LineBreakLayout lineBreakLayout, int id, List<SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean> items) {
        CheckBox checkBox;
        int itemHeight = ScreenUtil.dip2px(25);
        int currentCount = lineBreakLayout.getChildCount();
        int needShowCount = (items != null ? items.size() : 0);
        boolean hasCacheView;
        SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean suggestEnableUpdateTip;
        LayoutInflater layoutInflater = mResumeEnableUpdateListView.callGetBindActivity().getLayoutInflater();
        for (int i = 0; i < needShowCount; i++) {
            hasCacheView = (i < currentCount);
            suggestEnableUpdateTip = items.get(i);
            if (hasCacheView)
                checkBox = (CheckBox) lineBreakLayout.getChildAt(i);
            else {
                checkBox = (CheckBox) layoutInflater.inflate(R.layout.layout_label_filter_item, null);
                checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            }
            checkBox.setText(suggestEnableUpdateTip.name);
            checkBox.setTag(R.id.hold_tag_id_one, id);
            checkBox.setTag(R.id.hold_tag_id_two, suggestEnableUpdateTip);
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked((id == R.id.view_label_tips && mLabelSelected != null && suggestEnableUpdateTip.name != null && suggestEnableUpdateTip.name.equals(mLabelSelected.name)) || (id == R.id.view_job_tips && mJobSelected != null && suggestEnableUpdateTip.name != null && suggestEnableUpdateTip.name.equals(mJobSelected.name)));
            checkBox.setOnCheckedChangeListener(this);
            checkBox.setVisibility(View.VISIBLE);
            if (!hasCacheView)
                lineBreakLayout.addView(checkBox, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, itemHeight));
        }
        int shouldHideCount = currentCount - needShowCount;
        if (shouldHideCount > 0)
            for (int index = needShowCount; index < currentCount; index++) {
                lineBreakLayout.getChildAt(index).setVisibility(View.GONE);
            }
    }

    private void unCheckAllSelections(LineBreakLayout lineBreakLayout, CheckBox currentChecked) {
        int count = lineBreakLayout.getChildCount();
        CheckBox checkBox;
        for (int i = 0; i < count; i++) {
            checkBox = (CheckBox) lineBreakLayout.getChildAt(i);
            if (checkBox == currentChecked || !checkBox.isChecked())
                continue;
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(false);
            checkBox.setOnCheckedChangeListener(this);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        unCheckAllSelections(lineBreakLayout_labelContainer, (CheckBox) buttonView);
        unCheckAllSelections(lineBreakLayout_jobContainer, (CheckBox) buttonView);
        int id = (int) buttonView.getTag(R.id.hold_tag_id_one);
        mLabelSelected = (id == R.id.view_label_tips && isChecked ? (SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean) buttonView.getTag(R.id.hold_tag_id_two) : null);
        mJobSelected = (id == R.id.view_job_tips && isChecked ? (SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean) buttonView.getTag(R.id.hold_tag_id_two) : null);
//        mPresenter.onJobHunterFilterSelected(mLabelSelected, mJobSelected);
    }

//    public void onSearchModeChanged(boolean enableTipsSearch) {
//        mEnableTipsSearch = enableTipsSearch;
//        boolean hasJob = (mSuggestJobList != null && !mSuggestJobList.isEmpty());
//        boolean hasTitle = (mSuggestLabelList != null && !mSuggestLabelList.isEmpty());
//        textView_easyChoseTitle.setVisibility(((enableTipsSearch && (hasTitle || hasJob)) ? View.VISIBLE : View.GONE));
//        view_JobTips.setVisibility(((enableTipsSearch && hasJob) ? View.VISIBLE : View.GONE));
//        view_labelTips.setVisibility(((enableTipsSearch && hasTitle) ? View.VISIBLE : View.GONE));
//        if (!enableTipsSearch) {
//            mLabelSelected = null;
//            mJobSelected = null;
//            unCheckAllSelections(lineBreakLayout_labelContainer, null);
//            unCheckAllSelections(lineBreakLayout_jobContainer, null);
//        }
//    }
}
