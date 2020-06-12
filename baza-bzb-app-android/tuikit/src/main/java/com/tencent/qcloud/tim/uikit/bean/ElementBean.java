package com.tencent.qcloud.tim.uikit.bean;

public class ElementBean {

    @Override
    public String toString() {
        return "ElementBean{" +
                "type='" + type + '\'' +
                ", data=" + data +
                '}';
    }

    /**
     * type : newJob
     * data : {"newJobNumber":2,"recommendNotProcess":0}
     */

    private String type;
    private DataBean data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        @Override
        public String toString() {
            return "DataBean{" +
                    "newJobNumber=" + newJobNumber +
                    ", recommendNotProcess=" + recommendNotProcess +
                    ", recommenderUnionId='" + recommenderUnionId + '\'' +
                    ", recommenderId='" + recommenderId + '\'' +
                    ", operatorId='" + operatorId + '\'' +
                    ", operatorName='" + operatorName + '\'' +
                    ", recommenderName='" + recommenderName + '\'' +
                    ", recommendDate='" + recommendDate + '\'' +
                    ", candidateId='" + candidateId + '\'' +
                    ", candidateName='" + candidateName + '\'' +
                    ", candidateFirmId='" + candidateFirmId + '\'' +
                    ", title='" + title + '\'' +
                    ", recommendId='" + recommendId + '\'' +
                    ", jobName='" + jobName + '\'' +
                    ", customerName='" + customerName + '\'' +
                    ", flowUpdateDate='" + flowUpdateDate + '\'' +
                    ", status=" + status +
                    ", groupName='" + groupName + '\'' +
                    ", firmShortName='" + firmShortName + '\'' +
                    ", message='" + message + '\'' +
                    ", atUserIds='" + atUserIds + '\'' +
                    '}';
        }

        /**
         * newJobNumber : 2
         * recommendNotProcess : 0
         */

        private int newJobNumber;
        private int recommendNotProcess;

        private String recommenderUnionId;//: "",  //推荐人UnionId
        private String recommenderId;//: "",  //推荐人LBDUserId
        private String operatorId;//:"",  //操作者的UnionId
        private String operatorName;//: "", //操作者姓名
        private String recommenderName;//: "",  //推荐人姓名
        private String recommendDate;//: "",  //推荐时间
        private String candidateId;//:"", //候选人Id
        private String candidateName;//: "",  //候选人姓名
        private String candidateFirmId;//:"",  //候选人简历所属公司
        private String title;//: "",  //候选人当前职位
        private String recommendId;//: "",  //推荐Id
        private String jobName;//: "",  //推荐职位
        private String customerName;//: ""  //推荐公司
        private String flowUpdateDate;//: "",  //状态变更时间
        private int status;//: 0,  //推荐流程状态
        private String groupName;//踢出群聊名称
        private String firmShortName;// "",  //客户公司

        private String message;//@自定义消息 | @跳槽13900180000@tiao13900180000 test
        private String atUserIds;//发送@的id | ["aa1b026a-3b25-4849-97dd-19bdab15ce9e"]
        //private boolean isAtMe;//是否是@自己

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getAtUserIds() {
            return atUserIds;
        }

        public void setAtUserIds(String atUserIds) {
            this.atUserIds = atUserIds;
        }

//        public boolean isAtMe() {
//            return isAtMe;
//        }
//
//        public void setAtMe(boolean atMe) {
//            isAtMe = atMe;
//        }

        public String getFirmShortName() {
            return firmShortName;
        }

        public void setFirmShortName(String firmShortName) {
            this.firmShortName = firmShortName;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getRecommenderUnionId() {
            return recommenderUnionId;
        }

        public void setRecommenderUnionId(String recommenderUnionId) {
            this.recommenderUnionId = recommenderUnionId;
        }

        public String getRecommenderId() {
            return recommenderId;
        }

        public void setRecommenderId(String recommenderId) {
            this.recommenderId = recommenderId;
        }

        public String getOperatorId() {
            return operatorId;
        }

        public void setOperatorId(String operatorId) {
            this.operatorId = operatorId;
        }

        public String getOperatorName() {
            return operatorName;
        }

        public void setOperatorName(String operatorName) {
            this.operatorName = operatorName;
        }

        public String getRecommenderName() {
            return recommenderName;
        }

        public void setRecommenderName(String recommenderName) {
            this.recommenderName = recommenderName;
        }

        public String getRecommendDate() {
            return recommendDate;
        }

        public void setRecommendDate(String recommendDate) {
            this.recommendDate = recommendDate;
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

        public String getCandidateFirmId() {
            return candidateFirmId;
        }

        public void setCandidateFirmId(String candidateFirmId) {
            this.candidateFirmId = candidateFirmId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getRecommendId() {
            return recommendId;
        }

        public void setRecommendId(String recommendId) {
            this.recommendId = recommendId;
        }

        public String getJobName() {
            return jobName;
        }

        public void setJobName(String jobName) {
            this.jobName = jobName;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getFlowUpdateDate() {
            return flowUpdateDate;
        }

        public void setFlowUpdateDate(String flowUpdateDate) {
            this.flowUpdateDate = flowUpdateDate;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getNewJobNumber() {
            return newJobNumber;
        }

        public void setNewJobNumber(int newJobNumber) {
            this.newJobNumber = newJobNumber;
        }

        public int getRecommendNotProcess() {
            return recommendNotProcess;
        }

        public void setRecommendNotProcess(int recommendNotProcess) {
            this.recommendNotProcess = recommendNotProcess;
        }
    }
}
