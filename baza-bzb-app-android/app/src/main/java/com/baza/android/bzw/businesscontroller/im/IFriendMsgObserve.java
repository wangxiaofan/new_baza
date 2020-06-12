package com.baza.android.bzw.businesscontroller.im;

/**
 * Created by Vincent.Lei on 2017/9/22.
 * Title：
 * Note：
 */

public interface IFriendMsgObserve {
    void onFriendNoticeMsgUnreadCountChanged(int unreadCount);

    void onFriendRequestGet(String account);

    void onFriendAgreeGet(String account);

//    void onFriendDeleted(List<String> deletedFriendAccounts);
}
