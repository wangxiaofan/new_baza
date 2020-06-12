package com.baza.android.bzw.businesscontroller.find.updateengine.cardcontentui;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.resume.ResumeUpdatedContentResultBean;
import com.baza.android.bzw.bean.resumeelement.WorkExperienceBean;
import com.baza.android.bzw.bean.resumeelement.WorkExperienceUnion;
import com.baza.android.bzw.businesscontroller.find.updateengine.presenter.UpdateContentCardPresenter;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IUpdateContentCardView;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/8/25.
 * Title：
 * Note：
 */

public class UpdateCardWorkExperienceUI implements CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.tv_part_title)
    TextView textView_partTitle;
    @BindView(R.id.cb_check_all)
    CheckBox checkBox_all;
    @BindView(R.id.ll_item_container)
    LinearLayout linearLayout_itemContainer;
    @BindView(R.id.tv_no_data)
    TextView textView_noData;

    private int mAllEnableUpdateCount;
    private int mSelectedCount;
    protected View mRootView;
    private IUpdateContentCardView mUpdateContentCardView;
    private UpdateContentCardPresenter mPresenter;
    private boolean mHasInit;
    private int[] mCurrentViewIds = {R.id.tv_work_time_current, R.id.tv_company_current, R.id.tv_job_title_current, R.id.tv_report_to_current, R.id.tv_subordinate_count_current, R.id.tv_salary_current, R.id.tv_job_content_current};
    private int[] mTargetViewIds = {R.id.tv_work_time_updated, R.id.tv_company_updated, R.id.tv_job_title_updated, R.id.tv_report_to_updated, R.id.tv_subordinate_count_updated, R.id.tv_salary_updated, R.id.tv_job_content_updated};

    public UpdateCardWorkExperienceUI(IUpdateContentCardView updateContentCardView, UpdateContentCardPresenter presenter) {
        this.mUpdateContentCardView = updateContentCardView;
        this.mPresenter = presenter;
        init();
    }

    public View getView() {
        return mRootView;
    }

    public void init() {
        mRootView = getBindActivity().getLayoutInflater().inflate(R.layout.layout_candidate_update_content_middle_ui, null);
        ButterKnife.bind(this, mRootView);
        textView_partTitle.setText(R.string.text_candidate_work_experience);
        checkBox_all.setOnCheckedChangeListener(this);
    }

    protected BaseActivity getBindActivity() {
        return mUpdateContentCardView.callGetBindActivity();
    }

    protected ResumeUpdatedContentResultBean.Data getData() {
        return mPresenter.getEnableUpdateContentData();
    }

    public void updateViews(List<WorkExperienceUnion> workExperienceUnions) {
        calculateAllEnableUpdateCount(workExperienceUnions);
        boolean isJustShowEnableUpdateContentMode = mPresenter.isJustShowEnableUpdateContentMode();
        if (workExperienceUnions == null || workExperienceUnions.isEmpty() || (mAllEnableUpdateCount == 0 && isJustShowEnableUpdateContentMode)) {
            if (!isJustShowEnableUpdateContentMode) {
                textView_noData.setVisibility(View.VISIBLE);
                linearLayout_itemContainer.setVisibility(View.GONE);
                mRootView.setVisibility(View.VISIBLE);
                checkBox_all.setVisibility(View.GONE);
                return;
            }
            mRootView.setVisibility(View.GONE);
            return;
        }
        LayoutInflater layoutInflater = getBindActivity().getLayoutInflater();
        View view;
        TextView textView;
        CheckBox checkBox;
        List<View> cacheVisibleItems = new ArrayList<>(workExperienceUnions.size());
        View lastVisibleItem = null;

        Resources resources = getBindActivity().callGetResources();
        WorkExperienceUnion workExperienceUnion;
        int currentCount = linearLayout_itemContainer.getChildCount();
        boolean hasCache;
        for (int i = 0, size = workExperienceUnions.size(); i < size; i++) {
            hasCache = (i < currentCount);
            view = (hasCache ? linearLayout_itemContainer.getChildAt(i) : layoutInflater.inflate(R.layout.layout_candidate_update_work_experience_info_item, null));
            if (!hasCache)
                linearLayout_itemContainer.addView(view);
            checkBox = view.findViewById(R.id.cb_check);
            checkBox.setOnCheckedChangeListener(this);
            workExperienceUnion = workExperienceUnions.get(i);
            if (workExperienceUnion.target == null && isJustShowEnableUpdateContentMode) {
                view.setVisibility(View.GONE);
                continue;
            }
            lastVisibleItem = view;
            cacheVisibleItems.add(view);
            if (workExperienceUnion.current != null && (!isJustShowEnableUpdateContentMode || workExperienceUnion.target != null))
                updateWorkItemView(view, mCurrentViewIds, resources, workExperienceUnion.current);
            else
                hideViews(view, mCurrentViewIds);

            if (workExperienceUnion.target != null) {
                updateWorkItemView(view, mTargetViewIds, resources, workExperienceUnion.target);
                textView = view.findViewById(R.id.tv_add_or_update);
                textView.setText((workExperienceUnion.current == null ? R.string.add_content : R.string.update_item_to_2));
                textView.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);
            } else {
                hideViews(view, mTargetViewIds);
                view.findViewById(R.id.tv_add_or_update).setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
            }
            view.setVisibility(View.VISIBLE);
            checkBox.setTag(workExperienceUnion.target);
        }
        resetTimeLine(lastVisibleItem, cacheVisibleItems);
        if (!mHasInit) {
            mHasInit = true;
            checkBox_all.setChecked(true);
        }
        checkBox_all.setVisibility((mAllEnableUpdateCount > 0 ? View.VISIBLE : View.GONE));
        textView_noData.setVisibility(View.GONE);
        linearLayout_itemContainer.setVisibility(View.VISIBLE);
        mRootView.setVisibility(View.VISIBLE);
    }

    private void resetTimeLine(View lastVisibleItem, List<View> cacheVisibleItems) {
        if (cacheVisibleItems == null || cacheVisibleItems.isEmpty())
            return;
        View view;
        ImageView imageView;
        ViewGroup.LayoutParams lpp;
        for (int i = 0, size = cacheVisibleItems.size(); i < size; i++) {
            view = cacheVisibleItems.get(i);
            imageView = view.findViewById(R.id.iv_time_line);
            lpp = imageView.getLayoutParams();
            if (view == lastVisibleItem) {
                lpp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                imageView.setLayoutParams(lpp);
                imageView.setImageResource(R.drawable.time_line_single);
            } else {
                lpp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                imageView.setLayoutParams(lpp);
                imageView.setImageResource(R.drawable.time_line_normal);
            }
        }
    }

    private void calculateAllEnableUpdateCount(List<WorkExperienceUnion> workExperienceUnions) {
        if (mAllEnableUpdateCount != 0)
            return;
        if (workExperienceUnions != null && !workExperienceUnions.isEmpty()) {
            for (int i = 0, size = workExperienceUnions.size(); i < size; i++) {
                if (workExperienceUnions.get(i).target != null)
                    mAllEnableUpdateCount++;
            }
        }
    }

    protected void updateWorkItemView(View parent, int[] childIds, Resources resources, WorkExperienceBean workExperienceBean) {
        TextView textView = parent.findViewById(childIds[0]);
        textView.setText( FriendlyShowInfoManager.getInstance().getWorkExperienceFormattedTime(workExperienceBean.startDate, workExperienceBean.endDate));
        textView.setVisibility(View.VISIBLE);
        textView = parent.findViewById(childIds[1]);
        textView.setText(workExperienceBean.companyName);
        textView.setVisibility(View.VISIBLE);
        textView = parent.findViewById(childIds[2]);
        textView.setText(workExperienceBean.title);
        textView.setVisibility(View.VISIBLE);
        textView = parent.findViewById(childIds[3]);
        textView.setText(resources.getString(R.string.report_to_with_value, workExperienceBean.reportTo));
        textView.setVisibility(View.VISIBLE);
        textView = parent.findViewById(childIds[4]);
        textView.setText(resources.getString(R.string.subordinateCount_value, String.valueOf(workExperienceBean.subordinateCount)));
        textView.setVisibility(View.VISIBLE);
        textView = parent.findViewById(childIds[5]);
        textView.setText(resources.getString(R.string.salary_with_value, String.valueOf(workExperienceBean.salary)));
        textView.setVisibility(View.VISIBLE);
        textView = parent.findViewById(childIds[6]);
        textView.setText(resources.getString(R.string.main_job_with_value, workExperienceBean.responsibility));
        textView.setVisibility(View.VISIBLE);
    }

    protected void hideViews(View parent, int[] ids) {
        for (int i = 0; i < ids.length; i++) {
            parent.findViewById(ids[i]).setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == checkBox_all) {
            mSelectedCount = (isChecked ? mAllEnableUpdateCount : 0);
            int childCount = linearLayout_itemContainer.getChildCount();
            View viewItem;
            CheckBox checkBox;
            for (int i = 0; i < childCount; i++) {
                viewItem = linearLayout_itemContainer.getChildAt(i);
                checkBox = viewItem.findViewById(R.id.cb_check);
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(isChecked);
                checkBox.setOnCheckedChangeListener(this);
            }
        } else {
            mSelectedCount += (isChecked ? 1 : -1);
            checkBox_all.setOnCheckedChangeListener(null);
            checkBox_all.setChecked((mSelectedCount == mAllEnableUpdateCount));
            checkBox_all.setOnCheckedChangeListener(this);
        }
    }

    public List<WorkExperienceBean> getUpdateContentParam() {
        if (mSelectedCount == 0)
            return null;
        List<WorkExperienceBean> list;
        if (checkBox_all.isChecked()) {
            ResumeUpdatedContentResultBean.Data data = getData();
            if (data == null || data.target == null || data.target.workList == null)
                return null;
            list = new ArrayList<>(mSelectedCount);
            list.addAll(data.target.workList);
            return list;
        }
        list = new ArrayList<>(mSelectedCount);
        int childCount = linearLayout_itemContainer.getChildCount();
        View viewItem;
        CheckBox checkBox;
        for (int i = 0; i < childCount; i++) {
            viewItem = linearLayout_itemContainer.getChildAt(i);
            checkBox = viewItem.findViewById(R.id.cb_check);
            if (checkBox.isChecked() && checkBox.getTag() != null)
                list.add((WorkExperienceBean) checkBox.getTag());
        }
        return list;
    }

}
