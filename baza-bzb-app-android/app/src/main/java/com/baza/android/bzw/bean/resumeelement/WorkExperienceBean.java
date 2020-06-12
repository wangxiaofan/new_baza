package com.baza.android.bzw.bean.resumeelement;

import android.text.TextUtils;

import com.baza.android.bzw.constant.CommonConst;

import java.io.Serializable;

/**
 * Created by Vincent.Lei on 2017/2/13.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class WorkExperienceBean implements Serializable {
    public long startDate = CommonConst.TIME_NO_GET;
    public long endDate = CommonConst.TIME_NO_GET;
    public String companyName;
    public String title;
    public String id;
    public String reportTo;
    public String responsibility;
    public int subordinateCount;
    public int salary;
    public int location;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof WorkExperienceBean))
            return false;
        WorkExperienceBean workExperience = (WorkExperienceBean) obj;
        if (this.startDate != workExperience.startDate || this.endDate != workExperience.endDate || this.subordinateCount != workExperience.subordinateCount || this.salary != workExperience.salary)
            return false;
        if (!TextUtils.equals(this.id, workExperience.id))
            return false;
        if (!TextUtils.equals(this.companyName, workExperience.companyName))
            return false;
        if (!TextUtils.equals(this.title, workExperience.title))
            return false;
        if (!TextUtils.equals(this.reportTo, workExperience.reportTo))
            return false;
        return TextUtils.equals(this.responsibility, workExperience.responsibility);
    }
}
