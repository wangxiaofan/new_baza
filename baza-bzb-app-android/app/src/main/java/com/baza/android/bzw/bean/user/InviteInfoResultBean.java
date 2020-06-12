package com.baza.android.bzw.bean.user;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/12/11.
 * Title：
 * Note：
 */
public class InviteInfoResultBean {
    public List<InviteInfoBean> list;
    public int inviteCount;

    public static class InviteInfoBean {
        public String inviteeUserName;
        public long created;
    }


}
