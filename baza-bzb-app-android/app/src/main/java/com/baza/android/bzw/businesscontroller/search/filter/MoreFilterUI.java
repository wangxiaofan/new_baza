package com.baza.android.bzw.businesscontroller.search.filter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.baza.android.bzw.bean.searchfilterbean.DegreeFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SchoolFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SexFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SourceFromFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.WorkYearFilterBean;
import com.baza.android.bzw.businesscontroller.search.presenter.MoreFilterPresenter;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IMoreFilterView;
import com.baza.android.bzw.extra.IFilterListener;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.widget.LineBreakLayout;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;


/**
 * Created by Vincent.Lei on 2017/3/22.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class MoreFilterUI implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, IMoreFilterView {
    private static final int TYPE_WORK_YEAR = 1;
    private static final int TYPE_DEGREE = 2;
    private static final int TYPE_SCHOOL = 3;
    private static final int TYPE_SOURCE = 4;
    private static final int TYPE_SEX = 5;

    private View view_root;
    private Resources mResources;
    private LayoutInflater mLayoutInflater;
    private MoreFilterPresenter mPresenter;
    private IFilterListener mFilterListener;
    private LineBreakLayout lineBreakLayout_workYearsContainer;
    private LineBreakLayout lineBreakLayout_degreeContainer;
    private LineBreakLayout lineBreakLayout_schoolContainer;
    private LineBreakLayout lineBreakLayout_sourceContainer;
    private LineBreakLayout lineBreakLayout_sexContainer;
    private WorkYearFilterBean mWorkYearFilter, mTempWorkYearFilter;
    private DegreeFilterBean mDegreeFilter, mTempDegreeFilter;
    private SchoolFilterBean mSchoolFilter, mTempSchoolFilter;
    private SourceFromFilterBean mSourceFromFilter, mTempSourceFromFilter;
    private SexFilterBean mSexFilter, mTempSexFilter;
    //当点击切换了页面上的选项但是没有按确定，下次显示本页面判断是否要恢复到以前选择的选项，避免造成误解
    private boolean mayResetWorkYear;
    private boolean mayResetDegree;
    private boolean mayResetSchool;
    private boolean mayResetSourceFrom;
    private boolean mayResetSex;

    MoreFilterUI(Context mContext, IFilterListener mFilterListener) {
        this.mFilterListener = mFilterListener;
        this.mResources = mContext.getResources();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        init();
    }

    public View getView() {
        return view_root;
    }

    public void show(boolean mayReset, String mWorkYearOutSide, String mDegreeOutSide, String mSchoolOutSide, String mSourceOutSide, String mSexOutSide) {
        mayReset = (shouldReplyOutSide(mWorkYearOutSide, mDegreeOutSide, mSchoolOutSide, mSourceOutSide, mSexOutSide) || mayReset);
        if (mayReset)
            mayResetPrevious();
        view_root.setVisibility(View.VISIBLE);
    }

    public void hide() {
        view_root.setVisibility(View.GONE);
    }

    private void init() {
        mPresenter = new MoreFilterPresenter(this);
        mPresenter.initialize();
        view_root = mLayoutInflater.inflate(R.layout.layout_filter_more, null);
        lineBreakLayout_workYearsContainer = view_root.findViewById(R.id.lbl_workYears_container);
        lineBreakLayout_degreeContainer = view_root.findViewById(R.id.lbl_degree_container);
        lineBreakLayout_schoolContainer = view_root.findViewById(R.id.lbl_school_container);
        lineBreakLayout_sourceContainer = view_root.findViewById(R.id.lbl_source_container);
        lineBreakLayout_sexContainer = view_root.findViewById(R.id.lbl_sex_container);

        view_root.findViewById(R.id.tv_clear_filter).setOnClickListener(this);
        view_root.findViewById(R.id.tv_submit_filter).setOnClickListener(this);

        int itemWidth = (int) (int) ((ScreenUtil.screenWidth - mResources.getDimension(R.dimen.dp_60) - 4) / 4);
        int itemHeight = (int) mResources.getDimension(R.dimen.dp_25);
        addWorkYearsSelections(itemWidth, itemHeight);
        addDegreeSelections(itemWidth, itemHeight);
        addSchoolSelections(itemWidth, itemHeight);
        addSourceSelections(itemWidth, itemHeight);
        addSexSelections(itemWidth, itemHeight);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_clear_filter:
                restTargetSelectedFiltersUI(lineBreakLayout_workYearsContainer, -1);
                restTargetSelectedFiltersUI(lineBreakLayout_degreeContainer, -1);
                restTargetSelectedFiltersUI(lineBreakLayout_schoolContainer, -1);
                restTargetSelectedFiltersUI(lineBreakLayout_sourceContainer, -1);
                restTargetSelectedFiltersUI(lineBreakLayout_sexContainer, -1);
                mWorkYearFilter = mTempWorkYearFilter = null;
                mDegreeFilter = mTempDegreeFilter = null;
                mSchoolFilter = mTempSchoolFilter = null;
                mSourceFromFilter = mTempSourceFromFilter = null;
                mSexFilter = mTempSexFilter = null;
                mayResetWorkYear = mayResetDegree = mayResetSchool = mayResetSourceFrom = mayResetSex = false;
                if (mFilterListener != null)
                    mFilterListener.clearMoreFilter();
                break;
            case R.id.tv_submit_filter:
                mWorkYearFilter = mTempWorkYearFilter;
                mDegreeFilter = mTempDegreeFilter;
                mSchoolFilter = mTempSchoolFilter;
                mSourceFromFilter = mTempSourceFromFilter;
                mSexFilter = mTempSexFilter;
                if (mFilterListener != null)
                    mFilterListener.onMoreFilterSelected(mWorkYearFilter, mDegreeFilter, mSchoolFilter, mSourceFromFilter, mSexFilter);
                mayResetWorkYear = mayResetDegree = mayResetSchool = mayResetSourceFrom = mayResetSex = false;
                break;
        }
    }

    private void addWorkYearsSelections(int itemWidth, int itemHeight) {
        WorkYearFilterBean[] workYearFilters = mPresenter.getWorkYearFilters();
        CheckBox checkBox;
        WorkYearFilterBean workYearFilterBean;
        for (int i = 0; i < workYearFilters.length; i++) {
            workYearFilterBean = workYearFilters[i];
            checkBox = (CheckBox) mLayoutInflater.inflate(R.layout.layout_more_filter_item, null);
            checkBox.setLayoutParams(new ViewGroup.LayoutParams(itemWidth, itemHeight));
            checkBox.setText(workYearFilterBean.name);
            checkBox.setOnCheckedChangeListener(this);
            checkBox.setTag(TYPE_WORK_YEAR);
            checkBox.setTag(R.id.filter_id_tag, workYearFilterBean);
            lineBreakLayout_workYearsContainer.addView(checkBox);
        }
    }

    private void addDegreeSelections(int itemWidth, int itemHeight) {
        DegreeFilterBean[] degreeFilters = mPresenter.getDegreeFilters();
        DegreeFilterBean degreeFilterBean;
        CheckBox checkBox;
        for (int i = 0; i < degreeFilters.length; i++) {
            degreeFilterBean = degreeFilters[i];
            checkBox = (CheckBox) mLayoutInflater.inflate(R.layout.layout_more_filter_item, null);
            checkBox.setLayoutParams(new ViewGroup.LayoutParams(itemWidth, itemHeight));
            checkBox.setText(degreeFilterBean.name);
            checkBox.setOnCheckedChangeListener(this);
            checkBox.setTag(TYPE_DEGREE);
            checkBox.setTag(R.id.filter_id_tag, degreeFilterBean);
            lineBreakLayout_degreeContainer.addView(checkBox);
        }
    }

    private void addSchoolSelections(int itemWidth, int itemHeight) {
        SchoolFilterBean[] schoolFilters = mPresenter.getSchoolFilters();
        CheckBox checkBox;
        SchoolFilterBean schoolFilterBean;
        for (int i = 0; i < schoolFilters.length; i++) {
            schoolFilterBean = schoolFilters[i];
            checkBox = (CheckBox) mLayoutInflater.inflate(R.layout.layout_more_filter_item, null);
            checkBox.setLayoutParams(new ViewGroup.LayoutParams(itemWidth, itemHeight));
            checkBox.setText(schoolFilterBean.name);
            checkBox.setOnCheckedChangeListener(this);
            checkBox.setTag(TYPE_SCHOOL);
            checkBox.setTag(R.id.filter_id_tag, schoolFilterBean);
            lineBreakLayout_schoolContainer.addView(checkBox);
        }
    }

    private void addSourceSelections(int itemWidth, int itemHeight) {
        SourceFromFilterBean[] sourceFromFilters = mPresenter.getSourceFromFilters();
        SourceFromFilterBean sourceFromFilterBean;
        CheckBox checkBox;
        for (int i = 0; i < sourceFromFilters.length; i++) {
            sourceFromFilterBean = sourceFromFilters[i];
            checkBox = (CheckBox) mLayoutInflater.inflate(R.layout.layout_more_filter_item, null);
            checkBox.setLayoutParams(new ViewGroup.LayoutParams(itemWidth, itemHeight));
            checkBox.setText(sourceFromFilterBean.name);
            checkBox.setOnCheckedChangeListener(this);
            checkBox.setTag(TYPE_SOURCE);
            checkBox.setTag(R.id.filter_id_tag, sourceFromFilterBean);
            lineBreakLayout_sourceContainer.addView(checkBox);
        }
    }

    private void addSexSelections(int itemWidth, int itemHeight) {
        SexFilterBean[] sexFilters = mPresenter.getSexFilters();
        CheckBox checkBox;
        SexFilterBean sexFilterBean;
        for (int i = 0; i < sexFilters.length; i++) {
            sexFilterBean = sexFilters[i];
            checkBox = (CheckBox) mLayoutInflater.inflate(R.layout.layout_more_filter_item, null);
            checkBox.setLayoutParams(new ViewGroup.LayoutParams(itemWidth, itemHeight));
            checkBox.setText(sexFilterBean.name);
            checkBox.setOnCheckedChangeListener(this);
            checkBox.setTag(TYPE_SEX);
            checkBox.setTag(R.id.filter_id_tag, sexFilterBean);
            lineBreakLayout_sexContainer.addView(checkBox);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int type = (int) buttonView.getTag();
        LineBreakLayout lineBreakLayout_container = null;
        switch (type) {
            case TYPE_WORK_YEAR:
                lineBreakLayout_container = lineBreakLayout_workYearsContainer;
                break;
            case TYPE_DEGREE:
                lineBreakLayout_container = lineBreakLayout_degreeContainer;
                break;
            case TYPE_SCHOOL:
                lineBreakLayout_container = lineBreakLayout_schoolContainer;
                break;
            case TYPE_SOURCE:
                lineBreakLayout_container = lineBreakLayout_sourceContainer;
                break;
            case TYPE_SEX:
                lineBreakLayout_container = lineBreakLayout_sexContainer;
                break;
        }

        if (lineBreakLayout_container == null)
            return;
        CheckBox checkBox;
        for (int i = 0, count = lineBreakLayout_container.getChildCount(); i < count; i++) {
            checkBox = (CheckBox) lineBreakLayout_container.getChildAt(i);
            //避免onCheckedChanged重复调用
            checkBox.setOnCheckedChangeListener(null);
            if (buttonView != checkBox)
                checkBox.setChecked(false);
            else
                changedSelectedFilters(i, checkBox);
            checkBox.setOnCheckedChangeListener(this);
        }
    }

    private void changedSelectedFilters(int index, CheckBox checkBox) {
        int type = (int) checkBox.getTag();
        Object filter = checkBox.getTag(R.id.filter_id_tag);
        boolean isChecked = checkBox.isChecked();
        switch (type) {
            case TYPE_WORK_YEAR:
                mTempWorkYearFilter = (isChecked ? (WorkYearFilterBean) filter : null);
                if (mTempWorkYearFilter != null)
                    mTempWorkYearFilter.choseIndex = index;
                mayResetWorkYear = true;
                break;
            case TYPE_DEGREE:
                mTempDegreeFilter = (isChecked ? (DegreeFilterBean) filter : null);
                if (mTempDegreeFilter != null)
                    mTempDegreeFilter.choseIndex = index;
                mayResetDegree = true;
                break;
            case TYPE_SCHOOL:
                mTempSchoolFilter = (isChecked ? (SchoolFilterBean) filter : null);
                if (mTempSchoolFilter != null)
                    mTempSchoolFilter.choseIndex = index;
                mayResetSchool = true;
                break;
            case TYPE_SOURCE:
                mTempSourceFromFilter = (isChecked ? (SourceFromFilterBean) filter : null);
                mayResetSourceFrom = true;
                if (mTempSourceFromFilter != null)
                    mTempSourceFromFilter.choseIndex = index;
                break;
            case TYPE_SEX:
                mTempSexFilter = (isChecked ? (SexFilterBean) filter : null);
                mayResetSex = true;
                if (mTempSexFilter != null)
                    mTempSexFilter.choseIndex = index;
                break;

        }
    }

    private void restTargetSelectedFiltersUI(LineBreakLayout lineBreakLayout_container, int checkedIndex) {
        CheckBox checkBox;
        for (int i = 0, count = lineBreakLayout_container.getChildCount(); i < count; i++) {
            checkBox = (CheckBox) lineBreakLayout_container.getChildAt(i);
            //避免onCheckedChanged重复调用
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(checkedIndex == i);
            checkBox.setOnCheckedChangeListener(this);
        }
    }

    private void mayResetPrevious() {
        if (mayResetWorkYear) {
            LogUtil.d("reset lineBreakLayout_workYearsContainer");
            restTargetSelectedFiltersUI(lineBreakLayout_workYearsContainer, (mWorkYearFilter != null ? mWorkYearFilter.choseIndex : -1));
            mayResetWorkYear = false;
        }
        if (mayResetDegree) {
            LogUtil.d("reset lineBreakLayout_degreeContainer");
            restTargetSelectedFiltersUI(lineBreakLayout_degreeContainer, (mDegreeFilter != null ? mDegreeFilter.choseIndex : -1));
            mayResetDegree = false;
        }
        if (mayResetSchool) {
            LogUtil.d("reset lineBreakLayout_schoolContainer");
            restTargetSelectedFiltersUI(lineBreakLayout_schoolContainer, (mSchoolFilter != null ? mSchoolFilter.choseIndex : -1));
            mayResetSchool = false;
        }
        if (mayResetSourceFrom) {
            LogUtil.d("reset lineBreakLayout_sourceContainer");
            restTargetSelectedFiltersUI(lineBreakLayout_sourceContainer, (mSourceFromFilter != null ? mSourceFromFilter.choseIndex : -1));
            mayResetSourceFrom = false;
        }
        if (mayResetSex) {
            mayResetSex = false;
            LogUtil.d("reset lineBreakLayout_sexContainer");
            restTargetSelectedFiltersUI(lineBreakLayout_sexContainer, (mSexFilter != null ? mSexFilter.choseIndex : -1));
        }
    }

    private boolean shouldReplyOutSide(String mWorkYearOutSide, String mDegreeOutSide, String mSchoolOutSide, String mSourceOutSide, String mSexOutSide) {
        if (mWorkYearOutSide != null) {
            mayResetWorkYear = true;
            mTempWorkYearFilter = mWorkYearFilter = mPresenter.findWorkYearFilterByName(mWorkYearOutSide);
        }
        if (mDegreeOutSide != null) {
            mayResetDegree = true;
            mTempDegreeFilter = mDegreeFilter = mPresenter.findDegreeFilterByName(mDegreeOutSide);
        }

        if (mSchoolOutSide != null) {
            mayResetSchool = true;
            mTempSchoolFilter = mSchoolFilter = mPresenter.findSchoolFilterByName(mSchoolOutSide);
        }
        if (mSourceOutSide != null) {
            mayResetSourceFrom = true;
            mTempSourceFromFilter = mSourceFromFilter = mPresenter.findSourceFilterByName(mSourceOutSide);
        }
        if (mSexOutSide != null) {
            mayResetSex = true;
            mTempSexFilter = mSexFilter = mPresenter.findSexFilterByName(mSexOutSide);
        }
        return (mayResetWorkYear || mayResetDegree || mayResetSchool || mayResetSourceFrom || mayResetSex);
    }

    @Override
    public Resources callGetResources() {
        return mResources;
    }
}
