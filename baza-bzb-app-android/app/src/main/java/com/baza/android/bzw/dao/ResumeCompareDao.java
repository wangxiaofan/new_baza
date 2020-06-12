package com.baza.android.bzw.dao;

import android.text.TextUtils;

import com.baza.android.bzw.bean.resume.ResumeDetailBean;
import com.baza.android.bzw.bean.resumeelement.EducationBean;
import com.baza.android.bzw.bean.resumeelement.ProjectExperienceBean;
import com.baza.android.bzw.bean.resumeelement.WorkExperienceBean;
import com.baza.android.bzw.bean.updateengine.UpdateResumeWrapperBean;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/9/7.
 * Title：
 * Note：
 */

public class ResumeCompareDao {
    public static final int CHANGE_REAL_NAME = 1;
    public static final int CHANGE_MOBILE = 1 << 1;
    public static final int CHANGE_EMAIL = 1 << 2;
    public static final int CHANGE_TITLE = 1 << 3;
    public static final int CHANGE_COMPANY = 1 << 4;
    public static final int CHANGE_MAJOR = 1 << 5;
    public static final int CHANGE_SCHOOL = 1 << 6;
    public static final int CHANGE_YEAR_EXPERIENCE = 1 << 7;
    public static final int CHANGE_DEGREE = 1 << 8;
    public static final int CHANGE_LOCATION = 1 << 9;
    public static final int CHANGE_SELF_EVALUATION = 1 << 10;
    public static final int CHANGE_INTENTION = 1 << 11;
    public static final int CHANGE_BIRTHDAY = 1 << 12;
    public static final int CHANGE_HUKOU = 1 << 13;
    public static final int CHANGE_MARRIAGE = 1 << 14;
    public static final int CHANGE_GENDER = 1 << 15;

    private int changeMode;
    private int newAddMode;
    private HashMap<Object, Object> mNewOldValueCache = new HashMap<>();

    public ResumeDetailBean getOldSMainInfoCache() {
        return mOldSMainInfoCache;
    }

    private ResumeDetailBean mOldSMainInfoCache = new ResumeDetailBean();

    public void buildNewContentToCurrentData(ResumeDetailBean currentData, UpdateResumeWrapperBean wrapperBean) {
        mOldSMainInfoCache.copyFromOld(currentData);
        buildMainInfo(currentData, wrapperBean);
        buildEducation(currentData, wrapperBean.eduList);
        buildWorkExperience(currentData, wrapperBean.workList);
        buildProjectExperience(currentData, wrapperBean.projectExperienceList);
        if (wrapperBean.intentions != null && !wrapperBean.intentions.isEmpty()) {
            currentData.intentions = wrapperBean.intentions;
            changeMode |= CHANGE_INTENTION;
        }
    }

    public void buildNewContentToCurrentData(ResumeDetailBean currentData, ResumeDetailBean newData) {
        mOldSMainInfoCache.copyFromOld(currentData);
        buildMainInfo(currentData, newData);
        buildEducation(currentData, newData.eduList);
        buildWorkExperience(currentData, newData.workList);
        buildProjectExperience(currentData, newData.projectExperienceList);
        boolean intentionChanged = ((currentData.intentions == null || currentData.intentions.isEmpty()) && (newData.intentions != null && !newData.intentions.isEmpty()));
        if (!intentionChanged)
            intentionChanged = (currentData.intentions != null && newData.intentions != null && currentData.intentions.size() != newData.intentions.size());
        if (intentionChanged) {
            currentData.intentions = newData.intentions;
            changeMode |= CHANGE_INTENTION;
        }
    }

    public boolean isTargetMainInfoChanged(int mode) {
        return ((changeMode & mode) > 0);
    }

    public boolean isTargetMainInfoAdd(int mode) {
        return ((newAddMode & mode) > 0);
    }

    public Object getOldValue(Object o) {
        return mNewOldValueCache.get(o);
    }

    private void buildMainInfo(ResumeDetailBean currentData, UpdateResumeWrapperBean wrapperBean) {
        currentData.updateStatus = wrapperBean.updateStatus;
        currentData.currentCompletion = wrapperBean.currentCompletion;
        if (wrapperBean.selfEvaluation != null) {
            if (TextUtils.isEmpty(currentData.selfEvaluation))
                newAddMode |= CHANGE_SELF_EVALUATION;
            currentData.selfEvaluation = wrapperBean.selfEvaluation;
            changeMode |= CHANGE_SELF_EVALUATION;
        }
        if (wrapperBean.mainInfo != null) {
            if (wrapperBean.mainInfo.realName != null) {
                if (TextUtils.isEmpty(currentData.realName))
                    newAddMode |= CHANGE_REAL_NAME;
                currentData.realName = wrapperBean.mainInfo.realName;
                changeMode |= CHANGE_REAL_NAME;
            }
            if (wrapperBean.mainInfo.mobile != null) {
                if (TextUtils.isEmpty(currentData.mobile))
                    newAddMode |= CHANGE_MOBILE;
                currentData.mobile = wrapperBean.mainInfo.mobile;
                changeMode |= CHANGE_MOBILE;
            }
            if (wrapperBean.mainInfo.email != null) {
                if (TextUtils.isEmpty(currentData.email))
                    newAddMode |= CHANGE_EMAIL;
                currentData.email = wrapperBean.mainInfo.email;
                changeMode |= CHANGE_EMAIL;
            }
            if (wrapperBean.mainInfo.title != null) {
                if (TextUtils.isEmpty(currentData.title))
                    newAddMode |= CHANGE_TITLE;
                currentData.title = wrapperBean.mainInfo.title;
                changeMode |= CHANGE_TITLE;
            }
            if (wrapperBean.mainInfo.company != null) {
                if (TextUtils.isEmpty(currentData.company))
                    newAddMode |= CHANGE_COMPANY;
                currentData.company = wrapperBean.mainInfo.company;
                changeMode |= CHANGE_COMPANY;
            }
            if (wrapperBean.mainInfo.major != null) {
                if (TextUtils.isEmpty(currentData.major))
                    newAddMode |= CHANGE_MAJOR;
                currentData.major = wrapperBean.mainInfo.major;
                changeMode |= CHANGE_MAJOR;
            }
            if (wrapperBean.mainInfo.school != null) {
                if (TextUtils.isEmpty(currentData.school))
                    newAddMode |= CHANGE_SCHOOL;
                currentData.school = wrapperBean.mainInfo.school;
                changeMode |= CHANGE_SCHOOL;
            }
            if (wrapperBean.mainInfo.yearExpr >= 0) {
                if (currentData.yearExpr <= 0)
                    newAddMode |= CHANGE_YEAR_EXPERIENCE;
                currentData.yearExpr = wrapperBean.mainInfo.yearExpr;
                changeMode |= CHANGE_YEAR_EXPERIENCE;
            }
            if (wrapperBean.mainInfo.degree > 0) {
                if (currentData.degree <= 0)
                    newAddMode |= CHANGE_DEGREE;
                currentData.degree = wrapperBean.mainInfo.degree;
                changeMode |= CHANGE_DEGREE;
            }
            if (wrapperBean.mainInfo.gender > 0) {
                if (currentData.gender <= 0)
                    newAddMode |= CHANGE_GENDER;
                currentData.gender = wrapperBean.mainInfo.gender;
                changeMode |= CHANGE_GENDER;
            }
            if (wrapperBean.mainInfo.marriage > 0) {
                if (currentData.marriage <= 0)
                    newAddMode |= CHANGE_MARRIAGE;
                currentData.marriage = wrapperBean.mainInfo.marriage;
                changeMode |= CHANGE_MARRIAGE;
            }
            if (wrapperBean.mainInfo.huKou > 0) {
                if (currentData.huKou <= 0)
                    newAddMode |= CHANGE_HUKOU;
                currentData.huKou = wrapperBean.mainInfo.huKou;
                changeMode |= CHANGE_HUKOU;
            }

            if (Long.parseLong(wrapperBean.mainInfo.birthday) > 0) {
                if (Long.parseLong(currentData.birthday) <= 0)
                    newAddMode |= CHANGE_BIRTHDAY;
                currentData.birthday = wrapperBean.mainInfo.birthday;
                changeMode |= CHANGE_BIRTHDAY;
            }

            if (wrapperBean.mainInfo.location > 0) {
                if (currentData.location <= 0)
                    newAddMode |= CHANGE_LOCATION;
                currentData.location = wrapperBean.mainInfo.location;
                changeMode |= CHANGE_LOCATION;
            }
        }
    }

    private void buildMainInfo(ResumeDetailBean currentData, ResumeDetailBean newData) {
        if (!TextUtils.equals(newData.selfEvaluation, currentData.selfEvaluation)) {
            if (TextUtils.isEmpty(currentData.selfEvaluation))
                newAddMode |= CHANGE_SELF_EVALUATION;
            currentData.selfEvaluation = newData.selfEvaluation;
            changeMode |= CHANGE_SELF_EVALUATION;
        }
        if (!TextUtils.equals(newData.realName, currentData.realName)) {
            if (TextUtils.isEmpty(currentData.realName))
                newAddMode |= CHANGE_REAL_NAME;
            currentData.realName = newData.realName;
            changeMode |= CHANGE_REAL_NAME;
        }
        if (!TextUtils.equals(newData.mobile, currentData.mobile)) {
            if (TextUtils.isEmpty(currentData.mobile))
                newAddMode |= CHANGE_MOBILE;
            currentData.mobile = newData.mobile;
            changeMode |= CHANGE_MOBILE;
        }
        if (!TextUtils.equals(newData.email, currentData.email)) {
            if (TextUtils.isEmpty(currentData.email))
                newAddMode |= CHANGE_EMAIL;
            currentData.email = newData.email;
            changeMode |= CHANGE_EMAIL;
        }
        if (!TextUtils.equals(newData.title, currentData.title)) {
            if (TextUtils.isEmpty(currentData.title))
                newAddMode |= CHANGE_TITLE;
            currentData.title = newData.title;
            changeMode |= CHANGE_TITLE;
        }
        if (!TextUtils.equals(newData.company, currentData.company)) {
            if (TextUtils.isEmpty(currentData.company))
                newAddMode |= CHANGE_COMPANY;
            currentData.company = newData.company;
            changeMode |= CHANGE_COMPANY;
        }
        if (!TextUtils.equals(newData.major, currentData.major)) {
            if (TextUtils.isEmpty(currentData.major))
                newAddMode |= CHANGE_MAJOR;
            currentData.major = newData.major;
            changeMode |= CHANGE_MAJOR;
        }
        if (!TextUtils.equals(newData.school, currentData.school)) {
            if (TextUtils.isEmpty(currentData.school))
                newAddMode |= CHANGE_SCHOOL;
            currentData.school = newData.school;
            changeMode |= CHANGE_SCHOOL;
        }
        if (newData.yearExpr >= 0 && newData.yearExpr != currentData.yearExpr) {
            if (currentData.yearExpr <= 0)
                newAddMode |= CHANGE_YEAR_EXPERIENCE;
            currentData.yearExpr = newData.yearExpr;
            changeMode |= CHANGE_YEAR_EXPERIENCE;
        }
        if (newData.degree > 0 && newData.degree != currentData.degree) {
            if (currentData.degree <= 0)
                newAddMode |= CHANGE_DEGREE;
            currentData.degree = newData.degree;
            changeMode |= CHANGE_DEGREE;
        }
        if (newData.gender > 0 && newData.gender != currentData.gender) {
            if (currentData.gender <= 0)
                newAddMode |= CHANGE_GENDER;
            currentData.gender = newData.gender;
            changeMode |= CHANGE_GENDER;
        }
        if (newData.marriage > 0 && newData.marriage != currentData.marriage) {
            if (currentData.marriage <= 0)
                newAddMode |= CHANGE_MARRIAGE;
            currentData.marriage = newData.marriage;
            changeMode |= CHANGE_MARRIAGE;
        }
        if (newData.huKou > 0 && newData.huKou != currentData.huKou) {
            if (currentData.huKou <= 0)
                newAddMode |= CHANGE_HUKOU;
            currentData.huKou = newData.huKou;
            changeMode |= CHANGE_HUKOU;
        }

        if (Long.parseLong(newData.birthday) > 0 && !newData.birthday.equals(currentData.birthday)) {
            if (Long.parseLong(currentData.birthday) <= 0)
                newAddMode |= CHANGE_BIRTHDAY;
            currentData.birthday = newData.birthday;
            changeMode |= CHANGE_BIRTHDAY;
        }

        if (newData.location > 0 && newData.location != currentData.location) {
            if (currentData.location <= 0)
                newAddMode |= CHANGE_LOCATION;
            currentData.location = newData.location;
            changeMode |= CHANGE_LOCATION;
        }
    }


    private void buildEducation(ResumeDetailBean currentData, List<EducationBean> eduListTarget) {
        if (currentData.eduList == null || currentData.eduList.isEmpty()) {
            currentData.eduList = eduListTarget;
            return;
        }
        EducationBean current;
        EducationBean target;
        int indexTarget, sizeTarget;
        boolean noTarget = (eduListTarget == null || eduListTarget.isEmpty());
        for (int indexCurrent = 0, sizeCurrent = currentData.eduList.size(); indexCurrent < sizeCurrent; indexCurrent++) {
            current = currentData.eduList.get(indexCurrent);
            mNewOldValueCache.put(current, current);
            if (noTarget) continue;
            for (indexTarget = 0, sizeTarget = eduListTarget.size(); indexTarget < sizeTarget; indexTarget++) {
                target = eduListTarget.get(indexTarget);
                if (!TextUtils.isEmpty(current.id) && current.id.equals(target.id)) {
                    currentData.eduList.set(indexCurrent, target);
                    eduListTarget.remove(indexTarget);
                    mNewOldValueCache.put(target, current);
                    break;
                }
            }
        }
        if (!noTarget && !eduListTarget.isEmpty())
            currentData.eduList.addAll(eduListTarget);
    }

    private void buildWorkExperience(ResumeDetailBean currentData, List<WorkExperienceBean> WorkExperienceTarget) {
        if (currentData.workList == null || currentData.workList.isEmpty()) {
            currentData.workList = WorkExperienceTarget;
            return;
        }
        WorkExperienceBean current;
        WorkExperienceBean target;
        int indexTarget, sizeTarget;
        boolean noTarget = (WorkExperienceTarget == null || WorkExperienceTarget.isEmpty());

        for (int indexCurrent = 0, sizeCurrent = currentData.workList.size(); indexCurrent < sizeCurrent; indexCurrent++) {
            current = currentData.workList.get(indexCurrent);
            mNewOldValueCache.put(current, current);
            if (noTarget)
                continue;
            for (indexTarget = 0, sizeTarget = WorkExperienceTarget.size(); indexTarget < sizeTarget; indexTarget++) {
                target = WorkExperienceTarget.get(indexTarget);
                if (!TextUtils.isEmpty(current.id) && current.id.equals(target.id)) {
                    currentData.workList.set(indexCurrent, target);
                    WorkExperienceTarget.remove(indexTarget);
                    mNewOldValueCache.put(target, current);
                    break;
                }
            }
        }
        if (!noTarget && !WorkExperienceTarget.isEmpty())
            currentData.workList.addAll(WorkExperienceTarget);
    }

    private void buildProjectExperience(ResumeDetailBean currentData, List<ProjectExperienceBean> projectExperienceTarget) {
        if (currentData.projectExperienceList == null || currentData.projectExperienceList.isEmpty()) {
            currentData.projectExperienceList = projectExperienceTarget;
            return;
        }
        ProjectExperienceBean current;
        ProjectExperienceBean target;
        int indexTarget, sizeTarget;
        boolean noTarget = (projectExperienceTarget == null || projectExperienceTarget.isEmpty());
        for (int indexCurrent = 0, sizeCurrent = currentData.projectExperienceList.size(); indexCurrent < sizeCurrent; indexCurrent++) {
            current = currentData.projectExperienceList.get(indexCurrent);
            mNewOldValueCache.put(current, current);
            if (noTarget)
                continue;
            for (indexTarget = 0, sizeTarget = projectExperienceTarget.size(); indexTarget < sizeTarget; indexTarget++) {
                target = projectExperienceTarget.get(indexTarget);
                if (!TextUtils.isEmpty(current.id) && current.id.equals(target.id)) {
                    currentData.projectExperienceList.set(indexCurrent, target);
                    projectExperienceTarget.remove(indexTarget);
                    mNewOldValueCache.put(target, current);
                    break;
                }
            }

        }
        if (!noTarget && !projectExperienceTarget.isEmpty())
            currentData.projectExperienceList.addAll(projectExperienceTarget);
    }
}
