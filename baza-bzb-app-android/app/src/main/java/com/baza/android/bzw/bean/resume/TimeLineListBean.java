package com.baza.android.bzw.bean.resume;

import java.io.Serializable;

public class TimeLineListBean implements Serializable {

    /**
     * created : 2020-03-17 17:42:39
     * createdString : 2020-03-17 17:42:39
     * extensionProperties : {"candidateId":"a9ce0423-17f5-49ce-8355-852a437937f8","candidateName":"夏旺蓝","evaluation":"","includePickUpService":"","interviewAddress":"","interviewId":"","interviewTime":"","interviewType":"","recommendReason":"","result":""}
     * guaranteeDays : null
     * offerHiredDate :
     * offerMonthSalary :
     * offerMonths :
     * offerRemarks :
     * onboardDate :
     * processType : 4
     * processTypeText : 接受
     * recommenderName : 撒哈拉
     */

    private String created;
    private String createdString;
    private ExtensionPropertiesBean extensionProperties;
    private int guaranteeDays;
    private String offerHiredDate;
    private String offerMonthSalary;
    private String offerMonths;
    private String offerRemarks;
    private String onboardDate;
    private int processType;
    private String processTypeText;
    private String recommenderName;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreatedString() {
        return createdString;
    }

    public void setCreatedString(String createdString) {
        this.createdString = createdString;
    }

    public ExtensionPropertiesBean getExtensionProperties() {
        return extensionProperties;
    }

    public void setExtensionProperties(ExtensionPropertiesBean extensionProperties) {
        this.extensionProperties = extensionProperties;
    }

    public int getGuaranteeDays() {
        return guaranteeDays;
    }

    public void setGuaranteeDays(int guaranteeDays) {
        this.guaranteeDays = guaranteeDays;
    }

    public String getOfferHiredDate() {
        return offerHiredDate;
    }

    public void setOfferHiredDate(String offerHiredDate) {
        this.offerHiredDate = offerHiredDate;
    }

    public String getOfferMonthSalary() {
        return offerMonthSalary;
    }

    public void setOfferMonthSalary(String offerMonthSalary) {
        this.offerMonthSalary = offerMonthSalary;
    }

    public String getOfferMonths() {
        return offerMonths;
    }

    public void setOfferMonths(String offerMonths) {
        this.offerMonths = offerMonths;
    }

    public String getOfferRemarks() {
        return offerRemarks;
    }

    public void setOfferRemarks(String offerRemarks) {
        this.offerRemarks = offerRemarks;
    }

    public String getOnboardDate() {
        return onboardDate;
    }

    public void setOnboardDate(String onboardDate) {
        this.onboardDate = onboardDate;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public String getProcessTypeText() {
        return processTypeText;
    }

    public void setProcessTypeText(String processTypeText) {
        this.processTypeText = processTypeText;
    }

    public String getRecommenderName() {
        return recommenderName;
    }

    public void setRecommenderName(String recommenderName) {
        this.recommenderName = recommenderName;
    }

    public static class ExtensionPropertiesBean {
        /**
         * candidateId : a9ce0423-17f5-49ce-8355-852a437937f8
         * candidateName : 夏旺蓝
         * evaluation :
         * includePickUpService :
         * interviewAddress :
         * interviewId :
         * interviewTime :
         * interviewType :
         * recommendReason :
         * result :
         */

        private String candidateId;
        private String candidateName;
        private String evaluation;
        private String includePickUpService;
        private String interviewAddress;
        private String interviewId;
        private String interviewTime;
        private String interviewType;
        private String recommendReason;
        private String result;
        private String rejectedReason;
        private String rejectedDetail;
        private String obsoletedReason;
        private String obsoletedDetail;
        private String amount;
        private String recommenderRealName;
        private String recommenderNickname;
        private String remark;
        private String lastStatusName;

        public String getLastStatusName() {
            return lastStatusName;
        }

        public void setLastStatusName(String lastStatusName) {
            this.lastStatusName = lastStatusName;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getRecommenderRealName() {
            return recommenderRealName;
        }

        public void setRecommenderRealName(String recommenderRealName) {
            this.recommenderRealName = recommenderRealName;
        }

        public String getRecommenderNickname() {
            return recommenderNickname;
        }

        public void setRecommenderNickname(String recommenderNickname) {
            this.recommenderNickname = recommenderNickname;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getObsoletedReason() {
            return obsoletedReason;
        }

        public void setObsoletedReason(String obsoletedReason) {
            this.obsoletedReason = obsoletedReason;
        }

        public String getObsoletedDetail() {
            return obsoletedDetail;
        }

        public void setObsoletedDetail(String obsoletedDetail) {
            this.obsoletedDetail = obsoletedDetail;
        }

        public String getRejectedReason() {
            return rejectedReason;
        }

        public void setRejectedReason(String rejectedReason) {
            this.rejectedReason = rejectedReason;
        }

        public String getRejectedDetail() {
            return rejectedDetail;
        }

        public void setRejectedDetail(String rejectedDetail) {
            this.rejectedDetail = rejectedDetail;
        }

        public String getCandidateId() {
            return candidateId;
        }

        public void setCandidateId(String candidateId) {
            this.candidateId = candidateId;
        }

        public String getCandidateName() {
            return candidateName;
        }

        public void setCandidateName(String candidateName) {
            this.candidateName = candidateName;
        }

        public String getEvaluation() {
            return evaluation;
        }

        public void setEvaluation(String evaluation) {
            this.evaluation = evaluation;
        }

        public String getIncludePickUpService() {
            return includePickUpService;
        }

        public void setIncludePickUpService(String includePickUpService) {
            this.includePickUpService = includePickUpService;
        }

        public String getInterviewAddress() {
            return interviewAddress;
        }

        public void setInterviewAddress(String interviewAddress) {
            this.interviewAddress = interviewAddress;
        }

        public String getInterviewId() {
            return interviewId;
        }

        public void setInterviewId(String interviewId) {
            this.interviewId = interviewId;
        }

        public String getInterviewTime() {
            return interviewTime;
        }

        public void setInterviewTime(String interviewTime) {
            this.interviewTime = interviewTime;
        }

        public String getInterviewType() {
            return interviewType;
        }

        public void setInterviewType(String interviewType) {
            this.interviewType = interviewType;
        }

        public String getRecommendReason() {
            return recommendReason;
        }

        public void setRecommendReason(String recommendReason) {
            this.recommendReason = recommendReason;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }
}
