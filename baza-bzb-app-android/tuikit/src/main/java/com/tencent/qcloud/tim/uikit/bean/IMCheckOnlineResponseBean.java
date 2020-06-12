package com.tencent.qcloud.tim.uikit.bean;

import java.io.Serializable;
import java.util.List;

public class IMCheckOnlineResponseBean implements Serializable {


    /**
     * data : [{"accountId":"string","details":[{"platform":"string","status":"string"}],"state":"Online"}]
     * errorCode : 0
     * errorMessage : string
     * succeeded : true
     */

    private int errorCode;
    private String errorMessage;
    private boolean succeeded;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * accountId : string
         * details : [{"platform":"string","status":"string"}]
         * state : Online
         */

        private String accountId;
        private String state;
        private List<DetailsBean> details;

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public List<DetailsBean> getDetails() {
            return details;
        }

        public void setDetails(List<DetailsBean> details) {
            this.details = details;
        }

        public static class DetailsBean {
            /**
             * platform : string
             * status : string
             */

            private String platform;
            private String status;

            public String getPlatform() {
                return platform;
            }

            public void setPlatform(String platform) {
                this.platform = platform;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }
    }
}
