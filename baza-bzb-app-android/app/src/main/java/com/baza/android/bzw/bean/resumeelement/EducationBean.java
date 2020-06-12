package com.baza.android.bzw.bean.resumeelement;

import android.text.TextUtils;

import com.baza.android.bzw.constant.CommonConst;

import java.io.Serializable;

/**
 * Created by Vincent.Lei on 2017/2/13.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class EducationBean implements Serializable {
    public long startDate = CommonConst.TIME_NO_GET;
    public long endDate = CommonConst.TIME_NO_GET;
    public String majorName;
    public String schoolName;
    public String id;
    public int degree;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof EducationBean))
            return false;
        EducationBean educationEqual = (EducationBean) obj;
        if (this.startDate != educationEqual.startDate || this.endDate != educationEqual.endDate || this.degree != educationEqual.degree)
            return false;
        if (!TextUtils.equals(this.id, educationEqual.id))
            return false;
        if (!TextUtils.equals(this.majorName, educationEqual.majorName))
            return false;
        return TextUtils.equals(this.schoolName, educationEqual.schoolName);
    }
}
