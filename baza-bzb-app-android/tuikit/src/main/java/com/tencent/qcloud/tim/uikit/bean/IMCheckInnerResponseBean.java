package com.tencent.qcloud.tim.uikit.bean;

import java.io.Serializable;
import java.util.List;

public class IMCheckInnerResponseBean implements Serializable {

    @Override
    public String toString() {
        return "IMCheckInnerResponseBean{" +
                "errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", succeeded=" + succeeded +
                ", data=" + data +
                '}';
    }

    /**
     * data : [{"accountId":"12345678910","checkResult":true,"companyResult":true}]
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
         * accountId : 12345678910
         * checkResult : true
         * companyResult : true
         */

        private String accountId;
        private boolean checkResult;
        private boolean companyResult;

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public boolean isCheckResult() {
            return checkResult;
        }

        public void setCheckResult(boolean checkResult) {
            this.checkResult = checkResult;
        }

        public boolean isCompanyResult() {
            return companyResult;
        }

        public void setCompanyResult(boolean companyResult) {
            this.companyResult = companyResult;
        }
    }
}
