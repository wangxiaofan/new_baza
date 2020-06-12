package com.baza.android.bzw.bean.smartgroup;

import com.baza.android.bzw.bean.BaseHttpResultBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Vincent.Lei on 2018/8/29.
 * Title：
 * Note：
 */
public class SmartGroupFoldersResultBean extends BaseHttpResultBean {
    public Data data;

    public static class Data {
        public int totalCount;
        public List<SmartGroupFolderBean> recordList;
    }

    public static class SmartGroupFolderBean implements Serializable{
        public String id;
        public String key;
        public String groupName;
        public int resumeCount;
    }
}
