package com.baza.android.bzw.bean.taskcard;

/**
 * Created by Vincent.Lei on 2018/11/29.
 * Title：
 * Note：
 */
public class TaskBean {
    public static final int STATUS_UNENABLE = -1;
    public static final int STATUS_DONE = 1;
    public static final int STATUS_NORMAL = 0;

    public String eventTitle;
    public String notYetButtonName;
    public String alreadyButtonName;
    public String linkUrl;
    public String eventSubTitle;
    public String finishedSubTitle;
    public int taskType;
    public int eventStatus;
    public int eventType;
}
