package com.slib.http;

/**
 * Created by Vincent.Lei on 2017/10/27.
 * Title：
 * Note：
 */

public class HttpConfig {
    long mConnectTimeOut;
    long mReadTimeOut;
    long mWriteTimeOut;
    boolean mLogEnable;
    String mTrustHostName;
    String mLogTag = "httpRequest";

    public IRequestAssistHandler getRequestAssistHandler() {
        return mRequestAssistHandler;
    }

    IRequestAssistHandler mRequestAssistHandler;

    private HttpConfig(Builder builder) {
        this.mConnectTimeOut = builder.mConnectTimeOut;
        this.mReadTimeOut = builder.mReadTimeOut;
        this.mWriteTimeOut = builder.mWriteTimeOut;
        this.mLogTag = builder.mLogTag;
        this.mLogEnable = builder.mLogEnable;
        this.mRequestAssistHandler = builder.mRequestAssistHandler;
        this.mTrustHostName = builder.mTrustHostName;
    }


    public static class Builder {
        private long mConnectTimeOut;
        private long mReadTimeOut;
        private long mWriteTimeOut;
        private boolean mLogEnable;
        private String mLogTag = "NetWorkHelper";
        private IRequestAssistHandler mRequestAssistHandler;
        private String mTrustHostName;

        public Builder trustHostName(String trustHostName) {
            this.mTrustHostName = trustHostName;
            return this;
        }

        public Builder connectTimeOut(long connectTimeOut) {
            this.mConnectTimeOut = connectTimeOut;
            return this;
        }

        public Builder readTimeOut(long readTimeOut) {
            this.mReadTimeOut = readTimeOut;
            return this;
        }

        public Builder writeTimeOut(long writeTimeOut) {
            this.mWriteTimeOut = writeTimeOut;
            return this;
        }

        public Builder logTag(String logTag) {
            this.mLogTag = logTag;
            return this;
        }

        public Builder logEnable(boolean logEnable) {
            this.mLogEnable = logEnable;
            return this;
        }

        public Builder requestAssistHandler(IRequestAssistHandler requestAssistHandler) {
            this.mRequestAssistHandler = requestAssistHandler;
            return this;
        }

        public HttpConfig build() {
            return new HttpConfig(this);
        }
    }
}
