package com.baza.android.bzw.bean.resume;

import com.baza.android.bzw.bean.resumeelement.CertificationBean;
import com.baza.android.bzw.bean.resumeelement.EducationBean;
import com.baza.android.bzw.bean.resumeelement.IntentionBean;
import com.baza.android.bzw.bean.resumeelement.LanguageBean;
import com.baza.android.bzw.bean.resumeelement.ProjectExperienceBean;
import com.baza.android.bzw.bean.resumeelement.RemarkBean;
import com.baza.android.bzw.bean.resumeelement.SkillBean;
import com.baza.android.bzw.bean.resumeelement.TrainingBean;
import com.baza.android.bzw.bean.resumeelement.WorkExperienceBean;
import com.baza.android.bzw.constant.CommonConst;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/6/13.
 * Title：
 * Note：
 */

public class ResumeDetailBean extends ResumeBean {

    public static final int ORIGINAL_FILE_EXIST = 1;//简历有附件

    public List<RemarkBean> inquiryList;
    public List<WorkExperienceBean> workList;//工作经历
    public List<EducationBean> eduList;//教育经历
    public List<IntentionBean> intentions;//期望
    public List<ProjectExperienceBean> projectExperienceList;//项目经历
    public List<CertificationBean> certifications;//证书
    public List<LanguageBean> languages;//证书
    public List<SkillBean> skills;//技能
    public List<TrainingBean> trainings;//培训
    public List<ResumeAttachment> attachments;//附件
    public String selfEvaluation;//自我评价
    public String ownerId;
    public long sourceCreateTime;
    public int isHasOriginalFile;//是否有简历附件
    public String fileName;//源文件名称
    public String fileUrl;//源文件下载地址
    public float completion;//评分更新引擎返回的评分
    public ResumeContactValidBean resumeContactValid;
    public List<ResumeRecommend> recommendDatas;//推荐列表


    @Override
    public void copyFromOld(ResumeBean resumeBean) {
        super.copyFromOld(resumeBean);
        ResumeDetailBean candidateDetailBean = (ResumeDetailBean) resumeBean;
        selfEvaluation = candidateDetailBean.selfEvaluation;
        inquiryList = candidateDetailBean.inquiryList;  //在线简历地址
        ownerId = candidateDetailBean.ownerId;
        workList = candidateDetailBean.workList;
        isHasOriginalFile = candidateDetailBean.isHasOriginalFile;
        eduList = candidateDetailBean.eduList;
        sourceCreateTime = candidateDetailBean.sourceCreateTime;
        fileName = candidateDetailBean.fileName;
        fileUrl = candidateDetailBean.fileUrl;
        intentions = candidateDetailBean.intentions;
        completion = candidateDetailBean.completion;
        certifications = candidateDetailBean.certifications;
        languages = candidateDetailBean.languages;
        projectExperienceList = candidateDetailBean.projectExperienceList;
        skills = candidateDetailBean.skills;
        trainings = candidateDetailBean.trainings;
        resumeContactValid = candidateDetailBean.resumeContactValid;
        attachments = candidateDetailBean.attachments;
    }

    public boolean isEmailValid() {
        return resumeContactValid == null || resumeContactValid.emailValid;
    }

    public boolean isMobileValid() {
        return resumeContactValid == null || resumeContactValid.mobileValid;
    }

    public void markEmailValid(boolean valid) {
        if (resumeContactValid == null) {
            resumeContactValid = new ResumeContactValidBean();
            resumeContactValid.mobileValid = true;
        }
        resumeContactValid.emailValid = valid;
    }

    public void markMobileValid(boolean valid) {
        if (resumeContactValid == null) {
            resumeContactValid = new ResumeContactValidBean();
            resumeContactValid.emailValid = true;
        }
        resumeContactValid.mobileValid = valid;
    }
}
