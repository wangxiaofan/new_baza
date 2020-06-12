package com.baza.android.bzw.businesscontroller.find.updateengine.cardcontentui;

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
import com.baza.android.bzw.bean.resumeelement.EducationBean;
import com.baza.android.bzw.bean.resumeelement.EducationUnion;
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

public class UpdateCardEducationUI implements CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.tv_part_title)
    TextView textView_partTitle;
    @BindView(R.id.cb_check_all)
    CheckBox checkBox_all;
    @BindView(R.id.ll_item_container)
    LinearLayout linearLayout_itemContainer;
    @BindView(R.id.tv_no_data)
    TextView textView_noData;
    View mRootView;

    private int[] mCurrentViewIds = {R.id.tv_learn_time_current, R.id.tv_school_current, R.id.tv_degree_and_major_current};
    private int[] mTargetViewIds = {R.id.tv_learn_time_updated, R.id.tv_school_updated, R.id.tv_degree_and_major_updated};
    private int mAllEnableUpdateCount;
    private int mSelectedCount;
    private IUpdateContentCardView mUpdateContentCardView;
    private UpdateContentCardPresenter mPresenter;
    private boolean mHasInit;


    public UpdateCardEducationUI(IUpdateContentCardView updateContentCardView, UpdateContentCardPresenter presenter) {
        this.mUpdateContentCardView = updateContentCardView;
        this.mPresenter = presenter;
        init();
    }

    public View getView() {
        return mRootView;
    }

    private void init() {
        mRootView = getBindActivity().getLayoutInflater().inflate(R.layout.layout_candidate_update_content_middle_ui, null);
        ButterKnife.bind(this, mRootView);
        textView_partTitle.setText(R.string.text_candidate_online_lable_education);
        checkBox_all.setOnCheckedChangeListener(this);
    }

    private BaseActivity getBindActivity() {
        return mUpdateContentCardView.callGetBindActivity();
    }

    private ResumeUpdatedContentResultBean.Data getData() {
        return mPresenter.getEnableUpdateContentData();
    }

    public void updateViews(List<EducationUnion> educationUnions) {
        calculateAllEnableUpdateCount(educationUnions);
        boolean isJustShowEnableUpdateContentMode = mPresenter.isJustShowEnableUpdateContentMode();
        if (educationUnions == null || educationUnions.isEmpty() || (mAllEnableUpdateCount == 0 && isJustShowEnableUpdateContentMode)) {
            if (!isJustShowEnableUpdateContentMode) {
                textView_noData.setVisibility(View.VISIBLE);
                linearLayout_itemContainer.setVisibility(View.GONE);
                checkBox_all.setVisibility(View.GONE);
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
        EducationUnion educationUnion;
        List<View> cacheVisibleItems = new ArrayList<>(educationUnions.size());
        View lastVisibleItem = null;
        int currentCount = linearLayout_itemContainer.getChildCount();
        boolean hasCache;

        for (int i = 0, size = educationUnions.size(); i < size; i++) {
            hasCache = (i < currentCount);
            view = (hasCache ? linearLayout_itemContainer.getChildAt(i) : layoutInflater.inflate(R.layout.layout_candidate_update_education_info_item, null));
            if (!hasCache)
                linearLayout_itemContainer.addView(view);
            checkBox = view.findViewById(R.id.cb_check);
            checkBox.setOnCheckedChangeListener(this);
            educationUnion = educationUnions.get(i);
            if (educationUnion.target == null && isJustShowEnableUpdateContentMode) {
                view.setVisibility(View.GONE);
                continue;
            }
            lastVisibleItem = view;
            cacheVisibleItems.add(view);
            if (educationUnion.current != null)
                updateEducationItem(view, mCurrentViewIds, educationUnion.current);
            else
                hideViews(view, mCurrentViewIds);
            if (educationUnion.target != null) {
                updateEducationItem(view, mTargetViewIds, educationUnion.target);
                textView = view.findViewById(R.id.tv_add_or_update);
                textView.setText((educationUnion.current == null ? R.string.add_content : R.string.update_item_to_2));
                textView.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);
            } else {
                hideViews(view, mTargetViewIds);
                view.findViewById(R.id.tv_add_or_update).setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
            }
            checkBox.setTag(educationUnion.target);
            view.setVisibility(View.VISIBLE);

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

    private void calculateAllEnableUpdateCount(List<EducationUnion> educationUnions) {
        if (mAllEnableUpdateCount != 0)
            return;
        if (educationUnions != null && !educationUnions.isEmpty()) {
            for (int i = 0, size = educationUnions.size(); i < size; i++) {
                if (educationUnions.get(i).target != null)
                    mAllEnableUpdateCount++;
            }
        }
    }

    private void updateEducationItem(View parent, int[] childIds, EducationBean educationBean) {
        TextView textView = parent.findViewById(childIds[0]);
        textView.setText(FriendlyShowInfoManager.getInstance().getWorkExperienceFormattedTime(educationBean.startDate, educationBean.endDate));
        textView = parent.findViewById(childIds[1]);
        textView.setText(educationBean.schoolName);
        textView = parent.findViewById(childIds[2]);
        textView.setText(FriendlyShowInfoManager.getInstance().getDegree(educationBean.degree) + " " + educationBean.majorName);

    }

    private void hideViews(View parent, int[] ids) {
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

    public List<EducationBean> getUpdateContentParam() {
        if (mSelectedCount == 0)
            return null;
        List<EducationBean> list;
        if (checkBox_all.isChecked()) {
            ResumeUpdatedContentResultBean.Data data = getData();
            if (data == null || data.target == null || data.target.eduList == null)
                return null;
            list = new ArrayList<>(mSelectedCount);
            list.addAll(data.target.eduList);
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
                list.add((EducationBean) checkBox.getTag());
        }
        return list;
    }
}
