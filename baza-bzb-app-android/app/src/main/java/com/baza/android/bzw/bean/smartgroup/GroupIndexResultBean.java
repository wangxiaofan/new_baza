package com.baza.android.bzw.bean.smartgroup;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/11/28.
 * Title：
 * Note：
 */
public class GroupIndexResultBean {
    public List<GroupIndexBean> recordList;

    public static class GroupIndexBean {
        public String groupName;
        public int groupType;
        public int resumeCount;
        public int secondLevelGroupCount;
        public long groupTime;
    }
}
