package com.baza.android.bzw.bean.friend;

import com.baza.android.bzw.bean.BaseHttpResultBean;

/**
 * Created by Vincent.Lei on 2017/9/26.
 * Title：
 * Note：
 */

public class FriendInfoResultBean extends BaseHttpResultBean {
    public FriendInfoBean data;

    public static class FriendInfoBean {
        /**
         * "nickName":"Switch",
         * "trueName":"Switch",
         * "avatar":"http://bazapic.oss-cn-shenzhen.aliyuncs.com/user-avatar-dev/361_1505100038245.jpg",
         * "company":"深圳市八爪网络科技有限公司",
         * "location"432,
         * "title":"Java工程师",
         * "unionId":"a7e703e3-431b-4aa3-9e3b-d65606ef01f9",
         * "neteaseId":"15115111511",
         * "candidateCount":10000,
         * "grade":6,
         * "isFriend":1,
         */
        public static final int FRIEND_YES = 1;

        public String title;
        public String unionId;
        public String neteaseId;
        public String nickName;
        public String trueName;
        public String avatar;
        public String company;
        public int location;
        public int candidateCount;
        public int isFriend;
        public int grade;

    }
}
