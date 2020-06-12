package com.baza.android.bzw.bean.resume;

import java.io.Serializable;

public class FloatingListBean implements Serializable {

    /**
     * candidateName : 朱志恒
     * company : 思创数码科技股份有限公司
     * completion : 0.0
     * customer : 平安
     * flowStepName : Recommendation
     * id : ab760397-4240-484f-86a8-0d14c2937afb
     * interviewId :
     * isPublic : false
     * isPublisher : true
     * isRecommender : false
     * job : 职位申请测试关注人
     * jobOwnerRealName : 测试1
     * latestOperation : 撒哈拉推荐了候选人
     * major : 信息科学技术
     * recommendDate : 2020-03-06 16:44:04
     * recommenderRealName : 撒哈拉
     * school : 职业培训中心
     * status : 0
     * title : 软件工程师
     */

    private String candidateName;
    private String company;
    private String completion;
    private String customer;
    private String flowStepName;
    private String id;
    private String interviewId;
    private boolean isPublic;
    private boolean isPublisher;
    private boolean isRecommender;
    private String job;
    private String jobOwnerRealName;
    private String latestOperation;
    private String major;
    private String recommendDate;
    private String recommenderRealName;
    private String school;
    private int status;
    private String title;
    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompletion() {
        return completion;
    }

    public void setCompletion(String completion) {
        this.completion = completion;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getFlowStepName() {
        return flowStepName;
    }

    public void setFlowStepName(String flowStepName) {
        this.flowStepName = flowStepName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(String interviewId) {
        this.interviewId = interviewId;
    }

    public boolean isIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean isIsPublisher() {
        return isPublisher;
    }

    public void setIsPublisher(boolean isPublisher) {
        this.isPublisher = isPublisher;
    }

    public boolean isIsRecommender() {
        return isRecommender;
    }

    public void setIsRecommender(boolean isRecommender) {
        this.isRecommender = isRecommender;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJobOwnerRealName() {
        return jobOwnerRealName;
    }

    public void setJobOwnerRealName(String jobOwnerRealName) {
        this.jobOwnerRealName = jobOwnerRealName;
    }

    public String getLatestOperation() {
        return latestOperation;
    }

    public void setLatestOperation(String latestOperation) {
        this.latestOperation = latestOperation;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getRecommendDate() {
        return recommendDate;
    }

    public void setRecommendDate(String recommendDate) {
        this.recommendDate = recommendDate;
    }

    public String getRecommenderRealName() {
        return recommenderRealName;
    }

    public void setRecommenderRealName(String recommenderRealName) {
        this.recommenderRealName = recommenderRealName;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
