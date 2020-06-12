package com.baza.android.bzw.businesscontroller.search.presenter;

import android.content.res.Resources;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.searchfilterbean.DegreeFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SchoolFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SexFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SourceFromFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.WorkYearFilterBean;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IMoreFilterView;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/5/23.
 * Title：
 * Note：
 */

public class MoreFilterPresenter extends BasePresenter {
    private IMoreFilterView mMoreFilterView;
    private WorkYearFilterBean[] workYearFilters;
    private DegreeFilterBean[] degreeFilters;
    private SchoolFilterBean[] schoolFilters;
    private SourceFromFilterBean[] sourceFromFilters;
    private SexFilterBean[] sexFilters;


    public MoreFilterPresenter(IMoreFilterView mMoreFilterView) {
        this.mMoreFilterView = mMoreFilterView;
    }

    @Override
    public void initialize() {
        //工作经验
        Resources mResources = mMoreFilterView.callGetResources();
        String[] items = mResources.getStringArray(R.array.advance_search_work_years_options);
        workYearFilters = new WorkYearFilterBean[items.length];
        for (int i = 0; i < items.length; i++) {
            workYearFilters[i] = WorkYearFilterBean.createForFilter(items[i], i);
        }
        //学历
        items = mResources.getStringArray(R.array.degree_level);
        degreeFilters = new DegreeFilterBean[items.length];
        for (int i = 0; i < items.length; i++) {
            degreeFilters[i] = DegreeFilterBean.createForFilter(items[i], i);
        }
        //毕业院校
        items = mResources.getStringArray(R.array.advance_search_school_options);
        schoolFilters = new SchoolFilterBean[items.length];
        for (int i = 0; i < items.length; i++) {
            schoolFilters[i] = SchoolFilterBean.createForFilter(items[i], i);
        }
        //来源
        items = mResources.getStringArray(R.array.advance_search_source_options_normal);
        sourceFromFilters = new SourceFromFilterBean[items.length];
        for (int i = 0; i < items.length; i++) {
            sourceFromFilters[i] = SourceFromFilterBean.createForFilter(items[i], i);
        }
        //性别
        items = mResources.getStringArray(R.array.advance_degree_sex_options);
        sexFilters = new SexFilterBean[items.length];
        for (int i = 0; i < items.length; i++) {
            sexFilters[i] = SexFilterBean.createForFilter(items[i], i);
        }
    }

    public WorkYearFilterBean[] getWorkYearFilters() {
        return workYearFilters;
    }

    public DegreeFilterBean[] getDegreeFilters() {
        return degreeFilters;
    }

    public SchoolFilterBean[] getSchoolFilters() {
        return schoolFilters;
    }

    public SourceFromFilterBean[] getSourceFromFilters() {
        return sourceFromFilters;
    }

    public SexFilterBean[] getSexFilters() {
        return sexFilters;
    }

    public WorkYearFilterBean findWorkYearFilterByName(String name) {
        if (workYearFilters == null)
            return null;
        WorkYearFilterBean workYearFilterBean = null;
        for (int i = 0; i < workYearFilters.length; i++) {
            if (workYearFilters[i].name.equals(name)) {
                workYearFilterBean = workYearFilters[i];
                workYearFilterBean.choseIndex = i;
                break;
            }
        }
        return workYearFilterBean;
    }

    public DegreeFilterBean findDegreeFilterByName(String name) {
        if (degreeFilters == null)
            return null;
        DegreeFilterBean degreeFilterBean = null;
        for (int i = 0; i < degreeFilters.length; i++) {
            if (degreeFilters[i].name.equals(name)) {
                degreeFilterBean = degreeFilters[i];
                degreeFilterBean.choseIndex = i;
                break;
            }
        }
        return degreeFilterBean;
    }

    public SchoolFilterBean findSchoolFilterByName(String name) {
        if (schoolFilters == null)
            return null;
        SchoolFilterBean schoolFilterBean = null;
        for (int i = 0; i < schoolFilters.length; i++) {
            if (schoolFilters[i].name.equals(name)) {
                schoolFilterBean = schoolFilters[i];
                schoolFilterBean.choseIndex = i;
                break;
            }
        }
        return schoolFilterBean;
    }

    public SourceFromFilterBean findSourceFilterByName(String name) {
        if (sourceFromFilters == null)
            return null;
        SourceFromFilterBean sourceFromFilterBean = null;
        for (int i = 0; i < sourceFromFilters.length; i++) {
            if (sourceFromFilters[i].name.equals(name)) {
                sourceFromFilterBean = sourceFromFilters[i];
                sourceFromFilterBean.choseIndex = i;
                break;
            }
        }
        return sourceFromFilterBean;
    }

    public SexFilterBean findSexFilterByName(String name) {
        if (sexFilters == null)
            return null;
        SexFilterBean sexFilterBean = null;
        for (int i = 0; i < sexFilters.length; i++) {
            if (sexFilters[i].name.equals(name)) {
                sexFilterBean = sexFilters[i];
                sexFilterBean.choseIndex = i;
                break;
            }
        }
        return sexFilterBean;
    }
}
