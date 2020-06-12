package com.tencent.qcloud.tim.uikit.bean;

import java.io.Serializable;

public class IMSigBean implements Serializable {

    String data;//": "string",
    int errorCode;//": 0,
    String errorMessage;//": "string",
    boolean succeeded;//": true

    public String getData() {
        return data;
    }

    public void setData(String data) {
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

    @Override
    public String toString() {
        return "IMSigBean{" +
                "data='" + data + '\'' +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", succeeded=" + succeeded +
                '}';
    }
}
