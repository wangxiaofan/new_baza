package com.baza.track.io.core;

import android.content.Context;
import android.text.TextUtils;


import com.baza.track.io.constant.TrackConst;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

public class TrackConfig {
    private SetClient mSetClient;

    String mAppVersion;
    String mProjectCode;
    String mProductCode;
    String mUserId;
    String mSessionId;
    String mIp;

    public static class SetClient {
        private String mProjectCode;
        private String mProductCode;
        private String mUserId;

        public SetClient projectCode(String projectCode) {
            this.mProjectCode = projectCode;
            return this;
        }

        public SetClient productCode(String productCode) {
            this.mProductCode = productCode;
            return this;
        }

        public SetClient bui(String bui) {
            this.mUserId = bui;
            return this;
        }
    }

    TrackConfig() {
    }

    void init(Context context, SetClient setClient) {
        if (context == null || setClient == null)
            return;
        mAppVersion = Util.getVersionName(context);
        mSetClient = setClient;
        mIp = Util.getIPAddress(context);
        initWithSetClient();
    }


    SetClient getSetClient() {
        return (mSetClient == null ? new SetClient() : mSetClient);
    }

    void setSetClient(SetClient setClient) {
        if (setClient != null) {
            mSetClient = setClient;
            initWithSetClient();
        }
    }

    private void initWithSetClient() {
        this.mProjectCode = mSetClient.mProjectCode;
        this.mProductCode = mSetClient.mProductCode;
        this.mUserId = mSetClient.mUserId;
        if (TextUtils.isEmpty(mProjectCode))
            throw new IllegalArgumentException("projectCode can not be null");
        if (TextUtils.isEmpty(mProductCode))
            mProductCode = mProjectCode + "-android";
        if (TextUtils.isEmpty(mUserId))
            mUserId = TrackConst.NULL;
        mSessionId = UUID.randomUUID().toString();
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private static String formatTime(long time) {
        return sdf.format(time);
    }

    private JSONObject getDefaultEventJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TrackConst.PROJECT_CODE, mProjectCode);
        jsonObject.put(TrackConst.PRODUCT_CODE, mProductCode);
        jsonObject.put(TrackConst.BUI, mUserId);
        jsonObject.put(TrackConst.IP, mIp);
        jsonObject.put(TrackConst.PRODUCT_VERSION, mAppVersion);
        jsonObject.put(TrackConst.SESSION_ID, mSessionId);
        jsonObject.put(TrackConst.TIME, formatTime(System.currentTimeMillis()));
        return jsonObject;
    }

    JSONObject getPageEventCommonJsonObject() throws JSONException {
        JSONObject jsonObject = getDefaultEventJsonObject();
        jsonObject.put(TrackConst.PTEC, TrackConst.NULL);
        jsonObject.put(TrackConst.PAGE_DATA, TrackConst.HEART_BEAT);
        jsonObject.put(TrackConst.PREVIOUS_PAGE_CODE, TrackConst.NULL);
        return jsonObject;
    }

    JSONObject getClickEventCommonJsonObject() throws JSONException {
        JSONObject jsonObject = getDefaultEventJsonObject();
        jsonObject.put(TrackConst.PAGE_VIEW_ID, TrackConst.NULL);
        jsonObject.put(TrackConst.TITLE, TrackConst.NULL);
        jsonObject.put(TrackConst.PAGE_CODE, TrackConst.NULL);
        jsonObject.put(TrackConst.EVENT_DATA, TrackConst.NULL);
        return jsonObject;
    }
}
