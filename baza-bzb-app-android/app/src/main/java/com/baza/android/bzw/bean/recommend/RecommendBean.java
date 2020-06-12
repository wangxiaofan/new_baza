package com.baza.android.bzw.bean.recommend;

import com.slib.utils.DateUtil;

/**
 * Created by Vincent.Lei on 2019/8/15.
 * Title：
 * Note：
 */
public class RecommendBean {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_COMPLETE = 1;
    public static final int STATE_DELAY = -1;
    public String content;
    public String realName;
    public String remindTime;
    public String resumeId;
    public String id;
    public int status;
    public boolean isFirm;

    private long timeMsec;

//    public int getStateCompareValue() {
//        return status == STATE_NORMAL ? 3 : (status == STATE_COMPLETE ? 1 : 2);
//    }

    public int getStateCompareValue(String today) {
        return status == STATE_COMPLETE ? 1 : (remindTime != null && remindTime.startsWith(today) ? 4 : status == STATE_DELAY ? 2 : 3);
    }

    public long getTimeMsec() {
        if (timeMsec == 0) {
            timeMsec = DateUtil.parseLongDate(remindTime, DateUtil.DEFAULT_API_FORMAT);
        }
        return timeMsec;
    }

}
