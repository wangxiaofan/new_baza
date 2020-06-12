package com.baza.android.bzw.bean.friend;

import com.baza.android.bzw.bean.BaseHttpResultBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/9/21.
 * Title：
 * Note：
 */

public class FriendListResultBean extends BaseHttpResultBean {
    public List<FriendBean> data;

    public static class FriendBean implements Serializable {
        public static final int TYPE_NORMAL = 0;
        public static final int TYPE_AGREE = 3;

        public int id;
        public int location;
        public String nickName;
        public String trueName;
        public String neteaseId;
        public String unionId;
        public String title;
        public String company;
        public String mobile;
        public String avatar;

        //好友请求列表额外
        public int requestStatus;
        public int isFriend;
        public String message;
        public long contactTime;

        @Override
        public String toString() {
            return "nickName = " + nickName + "   trueName = " + trueName;
        }
    }
}
