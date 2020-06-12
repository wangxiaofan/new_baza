package com.baza.android.bzw.dao;

import android.text.TextUtils;

import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.friend.DynamicListResultBean;
import com.baza.android.bzw.bean.friend.FriendAddResultBean;
import com.baza.android.bzw.bean.friend.FriendContactRecordBean;
import com.baza.android.bzw.bean.friend.FriendInfoResultBean;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.bean.friend.ListNearlyResultBean;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;
import com.slib.storage.database.DBWorker;
import com.slib.storage.database.listener.IDBReplyListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Vincent.Lei on 2017/9/26.
 * Title：
 * Note：
 */

public class FriendDao {
    private FriendDao() {
    }

    public static void getFriendList(final IDefaultRequestReplyListener<List<FriendListResultBean.FriendBean>> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_FRIEND_GET_LIST, null, FriendListResultBean.class, new INetworkCallBack<FriendListResultBean>() {
            @Override
            public void onSuccess(FriendListResultBean friendListResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, friendListResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void getFriendRequestList(final IDefaultRequestReplyListener<List<FriendListResultBean.FriendBean>> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_FRIEND_REQUEST_LIST, null, FriendListResultBean.class, new INetworkCallBack<FriendListResultBean>() {
            @Override
            public void onSuccess(FriendListResultBean friendListResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, friendListResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void addFriend(String toUserUnionId, String message, final IDefaultRequestReplyListener<FriendAddResultBean> listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("toUserUnionId", toUserUnionId);
        if (!TextUtils.isEmpty(message))
            map.put("message", message);
        HttpRequestUtil.doHttpPost(URLConst.URL_FRIEND_ADD, map, FriendAddResultBean.class, new INetworkCallBack<FriendAddResultBean>() {
            @Override
            public void onSuccess(FriendAddResultBean friendAddResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, friendAddResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void replyFriendRequest(String fromUserUnionId, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("fromUserUnionId", fromUserUnionId);
        map.put("requestStatus", "3");
        HttpRequestUtil.doHttpPost(URLConst.URL_FRIEND_REQUEST_REPLY, map, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void searchFriend(String keyword, final IDefaultRequestReplyListener<List<FriendListResultBean.FriendBean>> listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("keyword", keyword);
        HttpRequestUtil.doHttpPost(URLConst.URL_FRIEND_SEARCH, map, FriendListResultBean.class, new INetworkCallBack<FriendListResultBean>() {
            @Override
            public void onSuccess(FriendListResultBean friendListResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, friendListResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void getFriendInfo(String unionId, final IDefaultRequestReplyListener<FriendInfoResultBean.FriendInfoBean> listener) {
        getFriendInfo(unionId, null, listener);
    }

    public static void getFriendInfo(String unionId, String neteaseId, final IDefaultRequestReplyListener<FriendInfoResultBean.FriendInfoBean> listener) {
        HashMap<String, String> map = new HashMap<>();
        if (unionId != null)
            map.put("unionId", unionId);
        if (neteaseId != null)
            map.put("neteaseId", neteaseId);
        HttpRequestUtil.doHttpPost(URLConst.URL_FRIEND_INFO, map, FriendInfoResultBean.class, new INetworkCallBack<FriendInfoResultBean>() {
            @Override
            public void onSuccess(FriendInfoResultBean friendInfoResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, friendInfoResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void deleteFriend(String unionId, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("toUnionId", unionId);
        HttpRequestUtil.doHttpPost(URLConst.URL_FRIEND_DELETE, map, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadFriendDynamic(int pageNo, int pageSize, final IDefaultRequestReplyListener<DynamicListResultBean.Data> listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("pageNo", String.valueOf(pageNo));
        map.put("pageSize", String.valueOf(pageSize));
        HttpRequestUtil.doHttpPost(URLConst.URL_FRIEND_DYNAMIC, map, DynamicListResultBean.class, new INetworkCallBack<DynamicListResultBean>() {
            @Override
            public void onSuccess(DynamicListResultBean dynamicListResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, dynamicListResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadFriendDynamic(int pageNo, final IDefaultRequestReplyListener<DynamicListResultBean.Data> listener) {
        loadFriendDynamic(pageNo, CommonConst.DEFAULT_PAGE_SIZE, listener);
    }

    public static void getTargetFriendDynamic(String unionId, int pageNo, final IDefaultRequestReplyListener<DynamicListResultBean.Data> listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("pageNo", String.valueOf(pageNo));
        map.put("pageSize", String.valueOf(CommonConst.DEFAULT_PAGE_SIZE));
        map.put("unionId", unionId);
        HttpRequestUtil.doHttpPost(URLConst.URL_FRIEND_DYNAMIC_TARGET, map, DynamicListResultBean.class, new INetworkCallBack<DynamicListResultBean>() {
            @Override
            public void onSuccess(DynamicListResultBean dynamicListResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, dynamicListResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadSuggestFriend(Set<String> cacheIds, final IDefaultRequestReplyListener<List<FriendListResultBean.FriendBean>> listener) {
        HashMap<String, String> map = null;
        if (cacheIds != null && !cacheIds.isEmpty()) {
            Iterator<String> iterator = cacheIds.iterator();
            StringBuilder stringBuilder = new StringBuilder("[");
            while (iterator.hasNext()) {
                stringBuilder.append("\"").append(iterator.next()).append("\"").append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("]");
            map = new HashMap<>();
            map.put("haveRecommendedList", stringBuilder.toString());
        }
        HttpRequestUtil.doHttpPost(URLConst.URL_FRIEND_GET_SUGGEST, map, FriendListResultBean.class, new INetworkCallBack<FriendListResultBean>() {
            @Override
            public void onSuccess(FriendListResultBean friendListResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, friendListResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void deleteFriendRequest(int id, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(id));
        HttpRequestUtil.doHttpPost(URLConst.URL_DELETE_FRIEND_RECORD, map, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void listNearlyPerson(int pageNo, int pageSize, final IDefaultRequestReplyListener<ListNearlyResultBean.Data> listener) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put("pageNo", String.valueOf(pageNo));
        map.put("pageSize", String.valueOf(pageSize));
        HttpRequestUtil.doHttpPost(URLConst.URL_LIST_NEARLY_FRIEND, map, ListNearlyResultBean.class, new INetworkCallBack<ListNearlyResultBean>() {
            @Override
            public void onSuccess(ListNearlyResultBean listNearlyResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, listNearlyResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void listNearlyPerson(int pageNo, final IDefaultRequestReplyListener<ListNearlyResultBean.Data> listener) {
        listNearlyPerson(pageNo, CommonConst.DEFAULT_PAGE_SIZE, listener);
    }

    public static void getNearlyPersonCount(final IDefaultRequestReplyListener<ListNearlyResultBean.Data> listener) {
        listNearlyPerson(1, CommonConst.DEFAULT_PAGE_SIZE, listener);
    }

    public static void updateFriendContactRecords(List<FriendContactRecordBean> friendContactRecords) {
        if (friendContactRecords == null || friendContactRecords.isEmpty())
            return;
        List<String> whereClauseList = new ArrayList<>();
        List<String[]> whereArgsList = new ArrayList<>();
        String where = "unionId = ? and contactNeteaseId = ?";
        FriendContactRecordBean friendContactRecordBean;
        for (int i = 0, size = friendContactRecords.size(); i < size; i++) {
            friendContactRecordBean = friendContactRecords.get(i);
            whereClauseList.add(where);
            whereArgsList.add(new String[]{friendContactRecordBean.unionId, friendContactRecordBean.contactNeteaseId});
        }
        DBWorker.saveList(friendContactRecords, null, whereClauseList, whereArgsList, null, false);
    }

    public static void readFriendContactRecords(final String unionId, IDBReplyListener<List<FriendContactRecordBean>> dBReplyListener) {
        DBWorker.query(FriendContactRecordBean.class, "unionId = ?", new String[]{unionId}, dBReplyListener);
    }
}
