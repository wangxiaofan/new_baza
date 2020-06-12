package com.tencent.qcloud.tim.uikit.bean;

import java.io.Serializable;
import java.util.List;

public class IMGetTeamsResponseBean implements Serializable {


    @Override
    public String toString() {
        return "IMGetTeamsResponseBean{" +
                "data=" + data +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", succeeded=" + succeeded +
                '}';
    }

    /**
     * data : {"childTeams":[{}],"level":0,"membersCount":0,"teamId":"string","teamName":"string"}
     * errorCode : 0
     * errorMessage : string
     * succeeded : true
     */

    private DataBean data;
    private int errorCode;
    private String errorMessage;
    private boolean succeeded;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
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

    public static class DataBean {
        /**
         * childTeams : [{}]
         * level : 0
         * membersCount : 0
         * teamId : string
         * teamName : string
         */

        private int level;
        private int membersCount;
        private String teamId;
        private String teamName;
        private List<DataBean> childTeams;
        private IMGetTeamMembersResponseBean bean;
        private boolean isOpen = false;//是否展开

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }

        public IMGetTeamMembersResponseBean getBean() {
            return bean;
        }

        public void setBean(IMGetTeamMembersResponseBean bean) {
            this.bean = bean;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getMembersCount() {
            return membersCount;
        }

        public void setMembersCount(int membersCount) {
            this.membersCount = membersCount;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public List<DataBean> getChildTeams() {
            return childTeams;
        }

        public void setChildTeams(List<DataBean> childTeams) {
            this.childTeams = childTeams;
        }

    }
}
