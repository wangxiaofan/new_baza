package com.baza.android.bzw.bean.email;

/**
 * Created by Vincent.Lei on 2017/5/2.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class ListSyncEmailBean {
    /**
     * "account": "tester201704@126.com",
     * "createTime": 1493704491298,
     * "emailPort": 995,
     * "emailServer": "pop3.126.com",
     * "enableSSL": 1,
     * "pkId": 1,
     * "resumeCount": 0, //同步的简历数量
     * "status": 0, //0-未完成任务 1-已完成任务 2-密码错误
     * "updateTime": 1493704491298,
     * "userId": 1
     */
    public static final int STATUS_SYNCING = 0;
    public static final int STATUS_COMPLETE = 1;
    public static final int STATUS_ERROR = 2;
    public String account;
    public int resumeCount;
    public int status;
    public long updateTime;
}
