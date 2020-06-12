package com.tencent.qcloud.tim.uikit.bean;

public class IMSendManySystemMessageResponseBean {

    /**
     * data : {}
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
    }
}
