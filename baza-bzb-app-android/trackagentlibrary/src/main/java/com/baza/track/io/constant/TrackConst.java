package com.baza.track.io.constant;

import com.baza.track.core.BuildConfig;

/**
 * Created by Vincent.Lei on 2018/4/17.
 * Title：
 * Note：
 */
public class TrackConst {
    public static final String NULL = "NULL";
    public static final String HEART_BEAT = "Heartbeat";

    public static final String PROJECT_CODE = "projectCode";
    public static final String PRODUCT_CODE = "productCode";
    public static final String PRODUCT_VERSION = "productVersion";
    public static final String BUI = "userId";
    public static final String SESSION_ID = "sessionId";
    public static final String IP = "ip";
    public static final String PTEC = "previousTriggerEventCode";
    public static final String TITLE = "title";
    public static final String PREVIOUS_PAGE_CODE = "previousPageCode";
    public static final String PAGE_CODE = "pageCode";
    public static final String PAGE_VIEW_ID = "pageViewId";
    public static final String TIME = "time";
    public static final String EVENT_CODE = "eventCode";
    public static final String EVENT_DATA = "eventData";
    public static final String PAGE_DATA = "pageData";

    public static String URL_PAGE = BuildConfig.HOST_UPA + "/Logs/PageViewInfos";
    public static String URL_CLICK = BuildConfig.HOST_UPA + "/Logs/EventInfos";


    public static class EventType {
        public static final String EVENT_TYPE_PAGE_REVIEW = "page";
        public static final String EVENT_TYPE_CLICK = "click";
    }

    public static class Db {
        public static String DB_PROCESS;
        public static final String DB_NAME = BuildConfig.LOG_DB_NAME;
        public static final int DB_VERSION = 1;
        public static final String TABLE_TRACK = "track";
        public static final String TRACK_COLUMN_ID = "id";
        public static final String TRACK_COLUMN_TYPE = "type";
        public static final String TRACK_COLUMN_VALUE = "value";
        public static final String TRACK_COLUMN_TIME = "time";
    }

}
