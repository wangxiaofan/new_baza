package com.tencent.qcloud.tim.uikit.bean;

import java.io.Serializable;
import java.util.List;

public class IMGetNameResponseBean implements Serializable {

    private int errorCode;
    private String errorMessage;
    private boolean succeeded;
    private List<DataBean> data;

    @Override
    public String toString() {
        return "IMGetNameResponseBean{" +
                "errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", succeeded=" + succeeded +
                ", data=" + data +
                '}';
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

    public List<com.tencent.qcloud.tim.uikit.bean.IMGetNameResponseBean.DataBean> getData() {
        return data;
    }

    public void setData(List<com.tencent.qcloud.tim.uikit.bean.IMGetNameResponseBean.DataBean> data) {
        this.data = data;
    }

    public class DataBean {
        private String accountId;
        private String accountUserName;
        private String company;
        private String firmId;
        private String title;

        @Override
        public String toString() {
            return "DataBean{" +
                    "accountId='" + accountId + '\'' +
                    ", accountUserName='" + accountUserName + '\'' +
                    ", company='" + company + '\'' +
                    ", firmId='" + firmId + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getAccountUserName() {
            return accountUserName;
        }

        public void setAccountUserName(String accountUserName) {
            this.accountUserName = accountUserName;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getFirmId() {
            return firmId;
        }

        public void setFirmId(String firmId) {
            this.firmId = firmId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
