package com.baza.android.bzw.bean.friend;

import com.baza.android.bzw.bean.BaseHttpResultBean;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/10/23.
 * Title：
 * Note：
 */

public class ListNearlyResultBean extends BaseHttpResultBean {
    public Data data;

    public static class NearlyPersonBean extends FriendListResultBean.FriendBean {
        public int distance;
    }

    public static class Data {
        public int total;
        public List<NearlyPersonBean> list;
    }
}
