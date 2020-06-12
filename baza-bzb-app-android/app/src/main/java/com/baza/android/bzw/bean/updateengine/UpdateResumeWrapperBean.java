package com.baza.android.bzw.bean.updateengine;

import android.text.TextUtils;

import com.baza.android.bzw.bean.resumeelement.EducationBean;
import com.baza.android.bzw.bean.resumeelement.IntentionBean;
import com.baza.android.bzw.bean.resumeelement.ProjectExperienceBean;
import com.baza.android.bzw.bean.resumeelement.WorkExperienceBean;
import com.baza.android.bzw.constant.CommonConst;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/8/28.
 * Title：
 * Note：
 */

public class UpdateResumeWrapperBean {
    public String candidateId;
    public int updateStatus;//是否可更新
    public float currentCompletion;//评分
    public MainInfo mainInfo;
    public String selfEvaluation;
    public List<WorkExperienceBean> workList;//工作经历
    public List<EducationBean> eduList;//教育经历
    public List<IntentionBean> intentions;//期望
    public List<ProjectExperienceBean> projectExperienceList;//项目经历


    public static class MainInfo {
        public String realName;
        public String mobile;
        public String email;
        public String title;
        public String company;
        public String major;
        public String school;
        public int yearExpr = CommonConst.TIME_NO_GET;
        public int degree;
        public int location;
        public int gender;
        public int huKou;
        public int marriage;
        public String birthday = CommonConst.TIME_NO_GET_BIRTHDAY;
    }

    public boolean isEnableUpdate() {
        if (mainInfo != null)
            return true;
        if (!TextUtils.isEmpty(selfEvaluation))
            return true;
        return workList != null || eduList != null || intentions != null || projectExperienceList != null;
    }
}
