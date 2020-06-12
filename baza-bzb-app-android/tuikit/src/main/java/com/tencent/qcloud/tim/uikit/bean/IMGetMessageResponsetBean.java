package com.tencent.qcloud.tim.uikit.bean;

import java.util.List;

public class IMGetMessageResponsetBean {
    @Override
    public String toString() {
        return "IMGetMessageResponsetBean{" +
                "data=" + data +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", succeeded=" + succeeded +
                '}';
    }

    /**
     * data : {"isAfterCompleted":false,"isCompleted":false,"messageList":[{"data":{"atUserIds":["string"],"candidateFirmId":"string","candidateName":"string","cityName":"string","commissionValue":"string","company":"string","conversationID":"string","creatorAvatar":"string","creatorId":"string","creatorName":"string","customerName":"string","degree":"string","firmShortName":"string","flowStepName":"string","flowUpdateDate":"string","groupName":"string","isAtMe":false,"jobId":"string","jobName":"string","location":"string","message":"string","newJobNumber":0,"operatorId":"string","operatorName":"string","recommendDate":"string","recommendId":"string","recommendNotProcess":0,"recommenderAvatar":"string","recommenderId":"string","recommenderName":"string","resumeId":"string","salary":"string","school":"string","status":0,"title":"string","yearOfExperience":"string"},"fromAccountId":"string","fromAccountName":"string","msgContent":{"data":"string","desc":"string","downloadFlag":0,"ext":"string","fileName":"string","fileSize":0,"imageFormat":0,"imageInfoArray":[{"height":0,"size":0,"type":0,"url":"string","width":0}],"index":0,"latitude":0,"longitude":0,"second":0,"size":0,"sound":"string","text":"string","thumbDownloadFlag":0,"thumbFormat":"string","thumbHeight":0,"thumbSize":0,"thumbUrl":"string","thumbWidth":0,"url":"string","uuid":"string","videoDownloadFlag":0,"videoFormat":"string","videoSecond":0,"videoSize":0,"videoUrl":"string"},"msgSeq":0,"msgTime":0,"msgType":"string","toAccountId":"string","type":"string"}],"nextAfterReqMessageId":0,"nextAfterReqMessageTime":0,"nextReqMessageId":0,"nextReqMessageTime":0}
     * errorCode : 0
     * errorMessage : string
     * succeeded : true
     */

    private DataBeanX data;
    private int errorCode;
    private String errorMessage;
    private boolean succeeded;

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public static class DataBeanX {
        @Override
        public String toString() {
            return "DataBeanX{" +
                    "isAfterCompleted=" + isAfterCompleted +
                    ", isCompleted=" + isCompleted +
                    ", nextAfterReqMessageId=" + nextAfterReqMessageId +
                    ", nextAfterReqMessageTime=" + nextAfterReqMessageTime +
                    ", nextReqMessageId=" + nextReqMessageId +
                    ", nextReqMessageTime=" + nextReqMessageTime +
                    ", messageList=" + messageList +
                    '}';
        }

        /**
         * isAfterCompleted : false
         * isCompleted : false
         * messageList : [{"data":{"atUserIds":["string"],"candidateFirmId":"string","candidateName":"string","cityName":"string","commissionValue":"string","company":"string","conversationID":"string","creatorAvatar":"string","creatorId":"string","creatorName":"string","customerName":"string","degree":"string","firmShortName":"string","flowStepName":"string","flowUpdateDate":"string","groupName":"string","isAtMe":false,"jobId":"string","jobName":"string","location":"string","message":"string","newJobNumber":0,"operatorId":"string","operatorName":"string","recommendDate":"string","recommendId":"string","recommendNotProcess":0,"recommenderAvatar":"string","recommenderId":"string","recommenderName":"string","resumeId":"string","salary":"string","school":"string","status":0,"title":"string","yearOfExperience":"string"},"fromAccountId":"string","fromAccountName":"string","msgContent":{"data":"string","desc":"string","downloadFlag":0,"ext":"string","fileName":"string","fileSize":0,"imageFormat":0,"imageInfoArray":[{"height":0,"size":0,"type":0,"url":"string","width":0}],"index":0,"latitude":0,"longitude":0,"second":0,"size":0,"sound":"string","text":"string","thumbDownloadFlag":0,"thumbFormat":"string","thumbHeight":0,"thumbSize":0,"thumbUrl":"string","thumbWidth":0,"url":"string","uuid":"string","videoDownloadFlag":0,"videoFormat":"string","videoSecond":0,"videoSize":0,"videoUrl":"string"},"msgSeq":0,"msgTime":0,"msgType":"string","toAccountId":"string","type":"string"}]
         * nextAfterReqMessageId : 0
         * nextAfterReqMessageTime : 0
         * nextReqMessageId : 0
         * nextReqMessageTime : 0
         */

        private boolean isAfterCompleted;
        private boolean isCompleted;
        private int nextAfterReqMessageId;
        private int nextAfterReqMessageTime;
        private int nextReqMessageId;
        private int nextReqMessageTime;
        private List<MessageListBean> messageList;

        public boolean isIsAfterCompleted() {
            return isAfterCompleted;
        }

        public void setIsAfterCompleted(boolean isAfterCompleted) {
            this.isAfterCompleted = isAfterCompleted;
        }

        public boolean isIsCompleted() {
            return isCompleted;
        }

        public void setIsCompleted(boolean isCompleted) {
            this.isCompleted = isCompleted;
        }

        public int getNextAfterReqMessageId() {
            return nextAfterReqMessageId;
        }

        public void setNextAfterReqMessageId(int nextAfterReqMessageId) {
            this.nextAfterReqMessageId = nextAfterReqMessageId;
        }

        public int getNextAfterReqMessageTime() {
            return nextAfterReqMessageTime;
        }

        public void setNextAfterReqMessageTime(int nextAfterReqMessageTime) {
            this.nextAfterReqMessageTime = nextAfterReqMessageTime;
        }

        public int getNextReqMessageId() {
            return nextReqMessageId;
        }

        public void setNextReqMessageId(int nextReqMessageId) {
            this.nextReqMessageId = nextReqMessageId;
        }

        public int getNextReqMessageTime() {
            return nextReqMessageTime;
        }

        public void setNextReqMessageTime(int nextReqMessageTime) {
            this.nextReqMessageTime = nextReqMessageTime;
        }

        public List<MessageListBean> getMessageList() {
            return messageList;
        }

        public void setMessageList(List<MessageListBean> messageList) {
            this.messageList = messageList;
        }

        public static class MessageListBean {
            @Override
            public String toString() {
                return "MessageListBean{" +
                        "data=" + data +
                        ", fromAccountId='" + fromAccountId + '\'' +
                        ", fromAccountName='" + fromAccountName + '\'' +
                        ", msgContent=" + msgContent +
                        ", msgSeq=" + msgSeq +
                        ", msgTime=" + msgTime +
                        ", msgType='" + msgType + '\'' +
                        ", toAccountId='" + toAccountId + '\'' +
                        ", type='" + type + '\'' +
                        '}';
            }

            /**
             * data : {"atUserIds":["string"],"candidateFirmId":"string","candidateName":"string","cityName":"string","commissionValue":"string","company":"string","conversationID":"string","creatorAvatar":"string","creatorId":"string","creatorName":"string","customerName":"string","degree":"string","firmShortName":"string","flowStepName":"string","flowUpdateDate":"string","groupName":"string","isAtMe":false,"jobId":"string","jobName":"string","location":"string","message":"string","newJobNumber":0,"operatorId":"string","operatorName":"string","recommendDate":"string","recommendId":"string","recommendNotProcess":0,"recommenderAvatar":"string","recommenderId":"string","recommenderName":"string","resumeId":"string","salary":"string","school":"string","status":0,"title":"string","yearOfExperience":"string"}
             * fromAccountId : string
             * fromAccountName : string
             * msgContent : {"data":"string","desc":"string","downloadFlag":0,"ext":"string","fileName":"string","fileSize":0,"imageFormat":0,"imageInfoArray":[{"height":0,"size":0,"type":0,"url":"string","width":0}],"index":0,"latitude":0,"longitude":0,"second":0,"size":0,"sound":"string","text":"string","thumbDownloadFlag":0,"thumbFormat":"string","thumbHeight":0,"thumbSize":0,"thumbUrl":"string","thumbWidth":0,"url":"string","uuid":"string","videoDownloadFlag":0,"videoFormat":"string","videoSecond":0,"videoSize":0,"videoUrl":"string"}
             * msgSeq : 0
             * msgTime : 0
             * msgType : string
             * toAccountId : string
             * type : string
             */

            private DataBean data;
            private String fromAccountId;
            private String fromAccountName;
            private MsgContentBean msgContent;
            private int msgSeq;
            private int msgTime;
            private String msgType;
            private String toAccountId;
            private String type;

            public DataBean getData() {
                return data;
            }

            public void setData(DataBean data) {
                this.data = data;
            }

            public String getFromAccountId() {
                return fromAccountId;
            }

            public void setFromAccountId(String fromAccountId) {
                this.fromAccountId = fromAccountId;
            }

            public String getFromAccountName() {
                return fromAccountName;
            }

            public void setFromAccountName(String fromAccountName) {
                this.fromAccountName = fromAccountName;
            }

            public MsgContentBean getMsgContent() {
                return msgContent;
            }

            public void setMsgContent(MsgContentBean msgContent) {
                this.msgContent = msgContent;
            }

            public int getMsgSeq() {
                return msgSeq;
            }

            public void setMsgSeq(int msgSeq) {
                this.msgSeq = msgSeq;
            }

            public int getMsgTime() {
                return msgTime;
            }

            public void setMsgTime(int msgTime) {
                this.msgTime = msgTime;
            }

            public String getMsgType() {
                return msgType;
            }

            public void setMsgType(String msgType) {
                this.msgType = msgType;
            }

            public String getToAccountId() {
                return toAccountId;
            }

            public void setToAccountId(String toAccountId) {
                this.toAccountId = toAccountId;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public static class DataBean {
                @Override
                public String toString() {
                    return "DataBean{" +
                            "candidateFirmId='" + candidateFirmId + '\'' +
                            ", candidateName='" + candidateName + '\'' +
                            ", cityName='" + cityName + '\'' +
                            ", commissionValue='" + commissionValue + '\'' +
                            ", company='" + company + '\'' +
                            ", conversationID='" + conversationID + '\'' +
                            ", creatorAvatar='" + creatorAvatar + '\'' +
                            ", creatorId='" + creatorId + '\'' +
                            ", creatorName='" + creatorName + '\'' +
                            ", customerName='" + customerName + '\'' +
                            ", degree='" + degree + '\'' +
                            ", firmShortName='" + firmShortName + '\'' +
                            ", flowStepName='" + flowStepName + '\'' +
                            ", flowUpdateDate='" + flowUpdateDate + '\'' +
                            ", groupName='" + groupName + '\'' +
                            ", isAtMe=" + isAtMe +
                            ", jobId='" + jobId + '\'' +
                            ", jobName='" + jobName + '\'' +
                            ", location='" + location + '\'' +
                            ", message='" + message + '\'' +
                            ", newJobNumber=" + newJobNumber +
                            ", operatorId='" + operatorId + '\'' +
                            ", operatorName='" + operatorName + '\'' +
                            ", recommendDate='" + recommendDate + '\'' +
                            ", recommendId='" + recommendId + '\'' +
                            ", recommendNotProcess=" + recommendNotProcess +
                            ", recommenderAvatar='" + recommenderAvatar + '\'' +
                            ", recommenderId='" + recommenderId + '\'' +
                            ", recommenderName='" + recommenderName + '\'' +
                            ", resumeId='" + resumeId + '\'' +
                            ", salary='" + salary + '\'' +
                            ", school='" + school + '\'' +
                            ", status=" + status +
                            ", title='" + title + '\'' +
                            ", yearOfExperience='" + yearOfExperience + '\'' +
                            ", atUserIds=" + atUserIds +
                            '}';
                }

                /**
                 * atUserIds : ["string"]
                 * candidateFirmId : string
                 * candidateName : string
                 * cityName : string
                 * commissionValue : string
                 * company : string
                 * conversationID : string
                 * creatorAvatar : string
                 * creatorId : string
                 * creatorName : string
                 * customerName : string
                 * degree : string
                 * firmShortName : string
                 * flowStepName : string
                 * flowUpdateDate : string
                 * groupName : string
                 * isAtMe : false
                 * jobId : string
                 * jobName : string
                 * location : string
                 * message : string
                 * newJobNumber : 0
                 * operatorId : string
                 * operatorName : string
                 * recommendDate : string
                 * recommendId : string
                 * recommendNotProcess : 0
                 * recommenderAvatar : string
                 * recommenderId : string
                 * recommenderName : string
                 * resumeId : string
                 * salary : string
                 * school : string
                 * status : 0
                 * title : string
                 * yearOfExperience : string
                 */

                private String candidateFirmId;
                private String candidateName;
                private String cityName;
                private String commissionValue;
                private String company;
                private String conversationID;
                private String creatorAvatar;
                private String creatorId;
                private String creatorName;
                private String customerName;
                private String degree;
                private String firmShortName;
                private String flowStepName;
                private String flowUpdateDate;
                private String groupName;
                private boolean isAtMe;
                private String jobId;
                private String jobName;
                private String location;
                private String message;
                private int newJobNumber;
                private String operatorId;
                private String operatorName;
                private String recommendDate;
                private String recommendId;
                private int recommendNotProcess;
                private String recommenderAvatar;
                private String recommenderId;
                private String recommenderName;
                private String resumeId;
                private String salary;
                private String school;
                private int status;
                private String title;
                private String yearOfExperience;
                private List<String> atUserIds;

                public String getCandidateFirmId() {
                    return candidateFirmId;
                }

                public void setCandidateFirmId(String candidateFirmId) {
                    this.candidateFirmId = candidateFirmId;
                }

                public String getCandidateName() {
                    return candidateName;
                }

                public void setCandidateName(String candidateName) {
                    this.candidateName = candidateName;
                }

                public String getCityName() {
                    return cityName;
                }

                public void setCityName(String cityName) {
                    this.cityName = cityName;
                }

                public String getCommissionValue() {
                    return commissionValue;
                }

                public void setCommissionValue(String commissionValue) {
                    this.commissionValue = commissionValue;
                }

                public String getCompany() {
                    return company;
                }

                public void setCompany(String company) {
                    this.company = company;
                }

                public String getConversationID() {
                    return conversationID;
                }

                public void setConversationID(String conversationID) {
                    this.conversationID = conversationID;
                }

                public String getCreatorAvatar() {
                    return creatorAvatar;
                }

                public void setCreatorAvatar(String creatorAvatar) {
                    this.creatorAvatar = creatorAvatar;
                }

                public String getCreatorId() {
                    return creatorId;
                }

                public void setCreatorId(String creatorId) {
                    this.creatorId = creatorId;
                }

                public String getCreatorName() {
                    return creatorName;
                }

                public void setCreatorName(String creatorName) {
                    this.creatorName = creatorName;
                }

                public String getCustomerName() {
                    return customerName;
                }

                public void setCustomerName(String customerName) {
                    this.customerName = customerName;
                }

                public String getDegree() {
                    return degree;
                }

                public void setDegree(String degree) {
                    this.degree = degree;
                }

                public String getFirmShortName() {
                    return firmShortName;
                }

                public void setFirmShortName(String firmShortName) {
                    this.firmShortName = firmShortName;
                }

                public String getFlowStepName() {
                    return flowStepName;
                }

                public void setFlowStepName(String flowStepName) {
                    this.flowStepName = flowStepName;
                }

                public String getFlowUpdateDate() {
                    return flowUpdateDate;
                }

                public void setFlowUpdateDate(String flowUpdateDate) {
                    this.flowUpdateDate = flowUpdateDate;
                }

                public String getGroupName() {
                    return groupName;
                }

                public void setGroupName(String groupName) {
                    this.groupName = groupName;
                }

                public boolean isIsAtMe() {
                    return isAtMe;
                }

                public void setIsAtMe(boolean isAtMe) {
                    this.isAtMe = isAtMe;
                }

                public String getJobId() {
                    return jobId;
                }

                public void setJobId(String jobId) {
                    this.jobId = jobId;
                }

                public String getJobName() {
                    return jobName;
                }

                public void setJobName(String jobName) {
                    this.jobName = jobName;
                }

                public String getLocation() {
                    return location;
                }

                public void setLocation(String location) {
                    this.location = location;
                }

                public String getMessage() {
                    return message;
                }

                public void setMessage(String message) {
                    this.message = message;
                }

                public int getNewJobNumber() {
                    return newJobNumber;
                }

                public void setNewJobNumber(int newJobNumber) {
                    this.newJobNumber = newJobNumber;
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

                public String getRecommendDate() {
                    return recommendDate;
                }

                public void setRecommendDate(String recommendDate) {
                    this.recommendDate = recommendDate;
                }

                public String getRecommendId() {
                    return recommendId;
                }

                public void setRecommendId(String recommendId) {
                    this.recommendId = recommendId;
                }

                public int getRecommendNotProcess() {
                    return recommendNotProcess;
                }

                public void setRecommendNotProcess(int recommendNotProcess) {
                    this.recommendNotProcess = recommendNotProcess;
                }

                public String getRecommenderAvatar() {
                    return recommenderAvatar;
                }

                public void setRecommenderAvatar(String recommenderAvatar) {
                    this.recommenderAvatar = recommenderAvatar;
                }

                public String getRecommenderId() {
                    return recommenderId;
                }

                public void setRecommenderId(String recommenderId) {
                    this.recommenderId = recommenderId;
                }

                public String getRecommenderName() {
                    return recommenderName;
                }

                public void setRecommenderName(String recommenderName) {
                    this.recommenderName = recommenderName;
                }

                public String getResumeId() {
                    return resumeId;
                }

                public void setResumeId(String resumeId) {
                    this.resumeId = resumeId;
                }

                public String getSalary() {
                    return salary;
                }

                public void setSalary(String salary) {
                    this.salary = salary;
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

                public String getYearOfExperience() {
                    return yearOfExperience;
                }

                public void setYearOfExperience(String yearOfExperience) {
                    this.yearOfExperience = yearOfExperience;
                }

                public List<String> getAtUserIds() {
                    return atUserIds;
                }

                public void setAtUserIds(List<String> atUserIds) {
                    this.atUserIds = atUserIds;
                }
            }

            public static class MsgContentBean {
                /**
                 * data : string
                 * desc : string
                 * downloadFlag : 0
                 * ext : string
                 * fileName : string
                 * fileSize : 0
                 * imageFormat : 0
                 * imageInfoArray : [{"height":0,"size":0,"type":0,"url":"string","width":0}]
                 * index : 0
                 * latitude : 0
                 * longitude : 0
                 * second : 0
                 * size : 0
                 * sound : string
                 * text : string
                 * thumbDownloadFlag : 0
                 * thumbFormat : string
                 * thumbHeight : 0
                 * thumbSize : 0
                 * thumbUrl : string
                 * thumbWidth : 0
                 * url : string
                 * uuid : string
                 * videoDownloadFlag : 0
                 * videoFormat : string
                 * videoSecond : 0
                 * videoSize : 0
                 * videoUrl : string
                 */

                private String data;
                private String desc;
                private int downloadFlag;
                private String ext;
                private String fileName;
                private int fileSize;
                private int imageFormat;
                private int index;
                private int latitude;
                private int longitude;
                private int second;
                private int size;
                private String sound;
                private String text;
                private int thumbDownloadFlag;
                private String thumbFormat;
                private int thumbHeight;
                private int thumbSize;
                private String thumbUrl;
                private int thumbWidth;
                private String url;
                private String uuid;
                private int videoDownloadFlag;
                private String videoFormat;
                private int videoSecond;
                private int videoSize;
                private String videoUrl;
                private List<ImageInfoArrayBean> imageInfoArray;

                public String getData() {
                    return data;
                }

                public void setData(String data) {
                    this.data = data;
                }

                public String getDesc() {
                    return desc;
                }

                public void setDesc(String desc) {
                    this.desc = desc;
                }

                public int getDownloadFlag() {
                    return downloadFlag;
                }

                public void setDownloadFlag(int downloadFlag) {
                    this.downloadFlag = downloadFlag;
                }

                public String getExt() {
                    return ext;
                }

                public void setExt(String ext) {
                    this.ext = ext;
                }

                public String getFileName() {
                    return fileName;
                }

                public void setFileName(String fileName) {
                    this.fileName = fileName;
                }

                public int getFileSize() {
                    return fileSize;
                }

                public void setFileSize(int fileSize) {
                    this.fileSize = fileSize;
                }

                public int getImageFormat() {
                    return imageFormat;
                }

                public void setImageFormat(int imageFormat) {
                    this.imageFormat = imageFormat;
                }

                public int getIndex() {
                    return index;
                }

                public void setIndex(int index) {
                    this.index = index;
                }

                public int getLatitude() {
                    return latitude;
                }

                public void setLatitude(int latitude) {
                    this.latitude = latitude;
                }

                public int getLongitude() {
                    return longitude;
                }

                public void setLongitude(int longitude) {
                    this.longitude = longitude;
                }

                public int getSecond() {
                    return second;
                }

                public void setSecond(int second) {
                    this.second = second;
                }

                public int getSize() {
                    return size;
                }

                public void setSize(int size) {
                    this.size = size;
                }

                public String getSound() {
                    return sound;
                }

                public void setSound(String sound) {
                    this.sound = sound;
                }

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public int getThumbDownloadFlag() {
                    return thumbDownloadFlag;
                }

                public void setThumbDownloadFlag(int thumbDownloadFlag) {
                    this.thumbDownloadFlag = thumbDownloadFlag;
                }

                public String getThumbFormat() {
                    return thumbFormat;
                }

                public void setThumbFormat(String thumbFormat) {
                    this.thumbFormat = thumbFormat;
                }

                public int getThumbHeight() {
                    return thumbHeight;
                }

                public void setThumbHeight(int thumbHeight) {
                    this.thumbHeight = thumbHeight;
                }

                public int getThumbSize() {
                    return thumbSize;
                }

                public void setThumbSize(int thumbSize) {
                    this.thumbSize = thumbSize;
                }

                public String getThumbUrl() {
                    return thumbUrl;
                }

                public void setThumbUrl(String thumbUrl) {
                    this.thumbUrl = thumbUrl;
                }

                public int getThumbWidth() {
                    return thumbWidth;
                }

                public void setThumbWidth(int thumbWidth) {
                    this.thumbWidth = thumbWidth;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getUuid() {
                    return uuid;
                }

                public void setUuid(String uuid) {
                    this.uuid = uuid;
                }

                public int getVideoDownloadFlag() {
                    return videoDownloadFlag;
                }

                public void setVideoDownloadFlag(int videoDownloadFlag) {
                    this.videoDownloadFlag = videoDownloadFlag;
                }

                public String getVideoFormat() {
                    return videoFormat;
                }

                public void setVideoFormat(String videoFormat) {
                    this.videoFormat = videoFormat;
                }

                public int getVideoSecond() {
                    return videoSecond;
                }

                public void setVideoSecond(int videoSecond) {
                    this.videoSecond = videoSecond;
                }

                public int getVideoSize() {
                    return videoSize;
                }

                public void setVideoSize(int videoSize) {
                    this.videoSize = videoSize;
                }

                public String getVideoUrl() {
                    return videoUrl;
                }

                public void setVideoUrl(String videoUrl) {
                    this.videoUrl = videoUrl;
                }

                public List<ImageInfoArrayBean> getImageInfoArray() {
                    return imageInfoArray;
                }

                public void setImageInfoArray(List<ImageInfoArrayBean> imageInfoArray) {
                    this.imageInfoArray = imageInfoArray;
                }

                public static class ImageInfoArrayBean {
                    /**
                     * height : 0
                     * size : 0
                     * type : 0
                     * url : string
                     * width : 0
                     */

                    private int height;
                    private int size;
                    private int type;
                    private String url;
                    private int width;

                    public int getHeight() {
                        return height;
                    }

                    public void setHeight(int height) {
                        this.height = height;
                    }

                    public int getSize() {
                        return size;
                    }

                    public void setSize(int size) {
                        this.size = size;
                    }

                    public int getType() {
                        return type;
                    }

                    public void setType(int type) {
                        this.type = type;
                    }

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }

                    public int getWidth() {
                        return width;
                    }

                    public void setWidth(int width) {
                        this.width = width;
                    }
                }
            }
        }
    }
}
