package com.baza.android.bzw.bean.user;

import java.io.Serializable;

/**
 * Created by Vincent.Lei on 2018/11/15.
 * Title：
 * Note：
 */
public class VerifyStatusBean implements Serializable {

    public static int ERROR_TYPE_ACCOUNT_ERROR = 1;
    public static int ERROR_TYPE_ACCOUNT_OVER_USED = 2;
    public int channelVerifyStatus;
    public int errorType;

    public VerifyStatusBean() {
    }

    public VerifyStatusBean(int status, int errorType) {
        this.channelVerifyStatus = status;
        this.errorType = errorType;
    }

    public void setChannelVerifyStatus(int channelVerifyStatus) {
        this.channelVerifyStatus = channelVerifyStatus;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }
}
