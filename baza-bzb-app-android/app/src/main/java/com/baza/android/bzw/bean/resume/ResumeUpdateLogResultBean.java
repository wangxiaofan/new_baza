package com.baza.android.bzw.bean.resume;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.resumeelement.IntentionBean;
import com.baza.android.bzw.bean.resumeelement.IntentionUnion;
import com.baza.android.bzw.bean.resumeelement.ProjectExperienceBean;
import com.baza.android.bzw.bean.resumeelement.RemarkBean;
import com.baza.android.bzw.bean.user.UserInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/8/30.
 * Title：
 * Note：
 */

public class ResumeUpdateLogResultBean extends BaseHttpResultBean {
    public Data data;

    public static class Data {
        public int count;
        public List<LogData> data;
    }

    public static class LogData {
        public static final int TYPE_UPDATE_RESUME = 24;
        public static final int TYPE_EDIT_RESUME = 2;
        public static final int TYPE_ADD_LABEL = 3;
        public static final int TYPE_COLLECTION_RESUME = 5;
        public static final int TYPE_UN_COLLECTION_RESUME = 6;
        public static final int TYPE_ASK_RESUME = 1000;
        public static final int TYPE_MAKE_CALL = 25;
        public static final int TYPE_SEND_EMAIL = 26;
        public static final int TYPE_TEXT_REMARK = 4;
        public static final int TYPE_VOICE_REMARK = 10;
        public static final int TYPE_IMAGE_REMARK = 11;

        public long id;
        public int sceneId;
        public long created;
        public UserInfoBean user;
        public String content;//json
        private LogResumeContent logResumeContent;
        private List<String> logLabelContent;
        private RemarkBean remarkBean;

        public void setRemarkBean(RemarkBean remarkBean) {
            this.remarkBean = remarkBean;
        }

        public RemarkBean getRemark() {
            return remarkBean;
        }

        public List<String> getLogLabelContent() {
            if (logLabelContent != null)
                return logLabelContent;
            if (TextUtils.isEmpty(content))
                return null;
            try {
                logLabelContent = JSON.parseArray(content, String.class);
            } catch (Exception e) {
                //ignore
            }
            return logLabelContent;
        }

        public LogResumeContent getLogResumeContent() {
            if (logResumeContent != null)
                return logResumeContent;
            if (TextUtils.isEmpty(content))
                return null;
            try {
                logResumeContent = JSON.parseObject(content, LogResumeContent.class);
            } catch (Exception e) {
                //ignore
                e.printStackTrace();
            }
            return logResumeContent;
        }
    }

    public static class LogResumeContent {
        public int degree;
        public String realName;
        public String mobile;
        public String email;
        public String title;
        public String company;
        public int yearOfExperience;
        public int gender = -1;
        public String major;
        public String school;
        public String selfEvaluation;
        public int locationId;
        public LogEducationTop educations;
        public LogWorkExperienceTop workExperiences;
        public IntentionTop intentions;
        public LogProjectExperienceTop projectExperiences;

        private List<ProjectExperienceBean> allProjectExperienceList;//非api返回
        private IntentionUnion intentionUnion;//非api返回
        private List<IntentionBean> allIntentionList;//非api返回
        private List<LogEducation> allEducationList;//非api返回
        private List<LogWorkExperience> allWorkExperienceList;//非api返回

        public void setIntentionUnion(IntentionUnion intentionUnion) {
            this.intentionUnion = intentionUnion;
        }

        public IntentionUnion getIntentionUnion() {
            return intentionUnion;
        }

        public List<ProjectExperienceBean> getAllProjectExperienceList() {
            if (allProjectExperienceList == null && projectExperiences != null) {
                int count = 0;
                if (projectExperiences.create != null)
                    count += projectExperiences.create.size();
                if (projectExperiences.update != null)
                    count += projectExperiences.update.size();
                if (count > 0) {
                    allProjectExperienceList = new ArrayList<>(count);
                    if (projectExperiences.create != null)
                        allProjectExperienceList.addAll(projectExperiences.create);
                    if (projectExperiences.update != null)
                        allProjectExperienceList.addAll(projectExperiences.update);
                }
            }
            return allProjectExperienceList;
        }

        public List<IntentionBean> getAllIntentionList() {
            if (allIntentionList == null && intentions != null) {
                int count = 0;
                if (intentions.create != null)
                    count += intentions.create.size();
                if (intentions.update != null)
                    count += intentions.update.size();
                if (count > 0) {
                    allIntentionList = new ArrayList<>(count);
                    if (intentions.create != null)
                        allIntentionList.addAll(intentions.create);
                    if (intentions.update != null)
                        allIntentionList.addAll(intentions.update);
                }
            }
            return allIntentionList;
        }

        public List<LogEducation> getAllEducationList() {
            if (allEducationList == null && educations != null) {
                int count = 0;
                if (educations.create != null)
                    count += educations.create.size();
                if (educations.update != null)
                    count += educations.update.size();
                if (count > 0) {
                    allEducationList = new ArrayList<>(count);
                    if (educations.create != null)
                        allEducationList.addAll(educations.create);
                    if (educations.update != null)
                        allEducationList.addAll(educations.update);
                }
            }
            return allEducationList;
        }

        public List<LogWorkExperience> getAllWorkExperienceList() {
            if (allWorkExperienceList == null && workExperiences != null) {
                int count = 0;
                if (workExperiences.create != null)
                    count += workExperiences.create.size();
                if (workExperiences.update != null)
                    count += workExperiences.update.size();
                if (count > 0) {
                    allWorkExperienceList = new ArrayList<>(count);
                    if (workExperiences.create != null)
                        allWorkExperienceList.addAll(workExperiences.create);
                    if (workExperiences.update != null)
                        allWorkExperienceList.addAll(workExperiences.update);
                }
            }
            return allWorkExperienceList;
        }
    }

    public static class LogEducation {
        public String startDate;
        public String endDate;
        public String schoolName;
        public String majorName;
        public int degree;
    }

    public static class IntentionTop {
        public List<IntentionBean> create;
        public List<IntentionBean> update;
    }

    public static class LogEducationTop {
        public List<LogEducation> create;
        public List<LogEducation> update;
    }

    public static class LogWorkExperienceTop {
        public List<LogWorkExperience> create;
        public List<LogWorkExperience> update;
    }

    public static class LogProjectExperienceTop {
        public List<ProjectExperienceBean> create;
        public List<ProjectExperienceBean> update;
    }

    public static class LogWorkExperience {
        public String startDate;
        public String endDate;
        public String companyName;
        public String title;
        public String responsibility;
        public String reportTo;
        public int subordinateCount;
        public int salary;
        public int locationId;
    }
}
