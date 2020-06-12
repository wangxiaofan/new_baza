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
import com.baza.android.bzw.bean.resumeelement.ProjectExperienceBean;
import com.baza.android.bzw.bean.resumeelement.ProjectUnion;
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

public class UpdateCardProjectExperienceUI implements CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.tv_part_title)
    TextView textView_partTitle;
    @BindView(R.id.cb_check_all)
    CheckBox checkBox_all;
    @BindView(R.id.ll_item_container)
    LinearLayout linearLayout_itemContainer;
    @BindView(R.id.tv_no_data)
    TextView textView_noData;
    View mRootView;

    private int mAllEnableUpdateCount;
    private int mSelectedCount;
    private IUpdateContentCardView mUpdateContentCardView;
    private UpdateContentCardPresenter mPresenter;
    private boolean mHasInit;
    private int[] mCurrentViewIds = {R.id.tv_project_time, R.id.tv_project_name, R.id.tv_project_role, R.id.tv_project_content, R.id.tv_project_responsibility};
    private int[] mTargetViewIds = {R.id.tv_project_time_updated, R.id.tv_project_name_updated, R.id.tv_project_role_updated, R.id.tv_project_content_updated, R.id.tv_project_responsibility_updated};

    public UpdateCardProjectExperienceUI(IUpdateContentCardView updateContentCardView, UpdateContentCardPresenter presenter) {
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
        textView_partTitle.setText(R.string.project_experience);
        checkBox_all.setOnCheckedChangeListener(this);
    }

    protected BaseActivity getBindActivity() {
        return mUpdateContentCardView.callGetBindActivity();
    }

    protected ResumeUpdatedContentResultBean.Data getData() {
        return mPresenter.getEnableUpdateContentData();
    }

    public void updateViews(List<ProjectUnion> projectUnions) {
        calculateAllEnableUpdateCount(projectUnions);
        boolean isJustShowEnableUpdateContentMode = mPresenter.isJustShowEnableUpdateContentMode();
        if (projectUnions == null || projectUnions.isEmpty() || (mAllEnableUpdateCount == 0 && isJustShowEnableUpdateContentMode)) {
            if (!isJustShowEnableUpdateContentMode) {
                linearLayout_itemContainer.setVisibility(View.GONE);
                checkBox_all.setVisibility(View.GONE);
                textView_noData.setVisibility(View.VISIBLE);
                mRootView.setVisibility(View.VISIBLE);
                return;
            }
            mRootView.setVisibility(View.GONE);
            return;
        }
        LayoutInflater layoutInflater = getBindActivity().getLayoutInflater();
        View view;
        TextView textView;
        CheckBox checkBox;
        Resources resources = getBindActivity().callGetResources();
        View lastVisibleItem = null;
        int currentCount = linearLayout_itemContainer.getChildCount();
        boolean hasCache;
        ProjectUnion projectUnion;
        List<View> cacheVisibleItems = new ArrayList<>(projectUnions.size());

        for (int i = 0, size = projectUnions.size(); i < size; i++) {
            hasCache = (i < currentCount);
            view = (hasCache ? linearLayout_itemContainer.getChildAt(i) : layoutInflater.inflate(R.layout.layout_candidate_update_project_experiemce_info_item, null));
            if (!hasCache)
                linearLayout_itemContainer.addView(view);
            checkBox = view.findViewById(R.id.cb_check);
            checkBox.setOnCheckedChangeListener(this);
            projectUnion = projectUnions.get(i);
            if (projectUnion.target == null && isJustShowEnableUpdateContentMode) {
                view.setVisibility(View.GONE);
                continue;
            }
            lastVisibleItem = view;
            cacheVisibleItems.add(view);
            if (projectUnion.current != null && (!isJustShowEnableUpdateContentMode || projectUnion.target != null))
                updateWorkItemView(view, mCurrentViewIds, resources, projectUnion.current);
            else
                hideViews(view, mCurrentViewIds);

            if (projectUnion.target != null) {
                updateWorkItemView(view, mTargetViewIds, resources, projectUnion.target);
                textView = view.findViewById(R.id.tv_add_or_update);
                textView.setText((projectUnion.current == null ? R.string.add_content : R.string.update_item_to_2));
                textView.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);
            } else {
                hideViews(view, mTargetViewIds);
                view.findViewById(R.id.tv_add_or_update).setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
            }
            view.setVisibility(View.VISIBLE);
            checkBox.setTag(projectUnion.target);
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

    protected void updateWorkItemView(View parent, int[] childIds, Resources resources, ProjectExperienceBean projectExperienceBean) {
        TextView textView = parent.findViewById(childIds[0]);
        textView.setText(FriendlyShowInfoManager.getInstance().getWorkExperienceFormattedTime(projectExperienceBean.startDate, projectExperienceBean.endDate));
        textView.setVisibility(View.VISIBLE);
        textView = parent.findViewById(childIds[1]);
        textView.setText(projectExperienceBean.projectName);
        textView.setVisibility(View.VISIBLE);
        textView = parent.findViewById(childIds[2]);
        textView.setText(resources.getString(R.string.project_role_value, projectExperienceBean.projectRole));
        textView.setVisibility(View.VISIBLE);
        textView = parent.findViewById(childIds[3]);
        textView.setText(resources.getString(R.string.project_content_value, projectExperienceBean.projectDescription));
        textView.setVisibility(View.VISIBLE);
        textView = parent.findViewById(childIds[4]);
        textView.setText(resources.getString(R.string.project_responsibility_value, projectExperienceBean.responsibility));
        textView.setVisibility(View.VISIBLE);
    }

    protected void hideViews(View parent, int[] ids) {
        for (int i = 0; i < ids.length; i++) {
            parent.findViewById(ids[i]).setVisibility(View.GONE);
        }
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

    private void calculateAllEnableUpdateCount(List<ProjectUnion> projectUnions) {
        if (mAllEnableUpdateCount != 0)
            return;
        if (projectUnions != null && !projectUnions.isEmpty()) {
            for (int i = 0, size = projectUnions.size(); i < size; i++) {
                if (projectUnions.get(i).target != null)
                    mAllEnableUpdateCount++;
            }
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

    protected ResumeUpdatedContentResultBean.Data getEnableUpdateContentData() {
        return mPresenter.getEnableUpdateContentData();
    }

    public List<ProjectExperienceBean> getUpdateContentParam() {
        if (mSelectedCount == 0)
            return null;
        List<ProjectExperienceBean> list;
        if (checkBox_all.isChecked()) {
            ResumeUpdatedContentResultBean.Data data = getEnableUpdateContentData();
            if (data == null || data.target == null || data.target.projectExperienceList == null)
                return null;
            list = new ArrayList<>(mSelectedCount);
            list.addAll(data.target.projectExperienceList);
            return list;
        }
        list = new ArrayList<>(mSelectedCount);
        int childCount = linearLayout_itemContainer.getChildCount();
        View viewItem;
        CheckBox checkBox;
        for (int i = 0; i < childCount; i++) {
            viewItem = linearLayout_itemContainer.getChildAt(i);
            checkBox = viewItem.findViewById(R.id.cb_check);
            if (checkBox.isChecked())
                list.add((ProjectExperienceBean) checkBox.getTag());
        }
        return list;
    }
}
