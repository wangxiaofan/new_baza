package com.baza.android.bzw.bean.friend;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.baza.android.bzw.bean.BaseHttpResultBean;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/9/27.
 * Title：
 * Note：
 */

public class DynamicListResultBean extends BaseHttpResultBean {
    public Data data;

    public static class Data {
        public int total;
        public List<DynamicBean> list;
    }

    public static class DynamicBean {
        public static final int DYNAMIC_TYPE_DOWNLOAD_PC = 10001;
        public static final int DYNAMIC_TYPE_SYNC_RESUME = 10002;
        public static final int DYNAMIC_TYPE_SHARE_REQUEST = 10003;
        public static final int DYNAMIC_TYPE_ADD_FRIEND = 10004;

        public String unionId;
        public String nickName;
        public String trueName;
        public String avatar;
        public int sceneId;
        public long updated;
        public String content;

        private SyncResumeDynamicInfo syncResumeInfo;
        private ShareRequestDynamicInfo shareRequestDynamicInfo;
        private AddFriendDynamicInfo addFriendDynamicInfo;

        public AddFriendDynamicInfo getAddFriendDynamic() {
            if (addFriendDynamicInfo != null)
                return addFriendDynamicInfo;
            if (TextUtils.isEmpty(content))
                return null;
            try {
                addFriendDynamicInfo = JSON.parseObject(content, AddFriendDynamicInfo.class);
            } catch (Exception e) {
                //ignore
            }
            return addFriendDynamicInfo;
        }

        public SyncResumeDynamicInfo getSyncResumeDynamic() {
            if (syncResumeInfo != null)
                return syncResumeInfo;
            if (TextUtils.isEmpty(content))
                return null;
            try {
                syncResumeInfo = JSON.parseObject(content, SyncResumeDynamicInfo.class);
            } catch (Exception e) {
                //ignore
            }
            return syncResumeInfo;
        }

        public ShareRequestDynamicInfo getShareRequestDynamic() {
            if (shareRequestDynamicInfo != null)
                return shareRequestDynamicInfo;
            if (TextUtils.isEmpty(content))
                return null;
            try {
                shareRequestDynamicInfo = JSON.parseObject(content, ShareRequestDynamicInfo.class);
            } catch (Exception e) {
                //ignore
            }
            return shareRequestDynamicInfo;
        }
    }

    public static class SyncResumeDynamicInfo {
        public String source;
        public int count;
    }

    public static class ShareRequestDynamicInfo {
        public int type;
        public String unionId;
        public String candidateId;
        public String nickName;
        public String trueName;
        public String realName;
        public String title;
    }

    public static class AddFriendDynamicInfo {
        public String unionId;
        public String nickName;
        public String trueName;
        public String title;
    }
}
