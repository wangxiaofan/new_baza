package com.baza.android.bzw.dao;

import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.CustomerHttpResultBean;
import com.baza.android.bzw.bean.smartgroup.GroupFolderResultBean;
import com.baza.android.bzw.bean.smartgroup.GroupIndexResultBean;
import com.baza.android.bzw.bean.smartgroup.GroupTimeSelectorResultBean;
import com.baza.android.bzw.bean.smartgroup.SmartGroupFoldersResultBean;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.CustomerHttpRequestUtil;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Vincent.Lei on 2018/8/30.
 * Title：
 * Note：
 */
public class SmartGroupDao {
    private SmartGroupDao() {
    }

    public static void loadSmartGroups(int offset, boolean selfDefine, String countType, String keyword, final IDefaultRequestReplyListener<SmartGroupFoldersResultBean.Data> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("offset", String.valueOf(offset));
        param.put("pageSize", String.valueOf(CommonConst.DEFAULT_PAGE_SIZE));
        if (countType != null)
            param.put("countType", countType);
        if (keyword != null)
            param.put("keyword", keyword);
        HttpRequestUtil.doHttpPost(selfDefine ? URLConst.URL_SMART_GROUP_LIST_SELF_DEFINE : URLConst.URL_SMART_GROUP_LIST_DEFAULT, param, SmartGroupFoldersResultBean.class, new INetworkCallBack<SmartGroupFoldersResultBean>() {
            @Override
            public void onSuccess(SmartGroupFoldersResultBean smartGroupFoldersResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, smartGroupFoldersResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void createSelfDefineGroup(String groupName, final IDefaultRequestReplyListener<SmartGroupFoldersResultBean.SmartGroupFolderBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("groupName", groupName);
        HttpRequestUtil.doHttpPost(URLConst.URL_CREATE_SELF_DEFINE_GROUP, param, GroupFolderResultBean.class, new INetworkCallBack<GroupFolderResultBean>() {
            @Override
            public void onSuccess(GroupFolderResultBean groupFolderResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, groupFolderResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void updateSelfDefineGroup(String groupId, String groupName, final IDefaultRequestReplyListener<SmartGroupFoldersResultBean.SmartGroupFolderBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("groupId", groupId);
        param.put("groupName", groupName);
        HttpRequestUtil.doHttpPost(URLConst.URL_UPDATE_SELF_DEFINE_GROUP, param, GroupFolderResultBean.class, new INetworkCallBack<GroupFolderResultBean>() {
            @Override
            public void onSuccess(GroupFolderResultBean groupFolderResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, groupFolderResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void deleteSelfDefineGroup(String groupId, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("groupId", groupId);
        HttpRequestUtil.doHttpPost(URLConst.URL_DELETE_SELF_DEFINE_GROUP, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
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

    public static void addResumesToGroup(String groupId, Set<String> idSet, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        if (idSet == null || idSet.size() == 0)
            return;
        HashMap<String, String> param = new HashMap<>();
        param.put("groupId", groupId);
        StringBuilder stringBuilder = new StringBuilder("[");
        Iterator<String> ite = idSet.iterator();
        while (ite.hasNext())
            stringBuilder.append("\"").append(ite.next()).append("\"").append(",");
        stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("]");
        param.put("resumeIds", stringBuilder.toString());
        HttpRequestUtil.doHttpPost(URLConst.URL_ADD_RESUMES_TO_GROUP, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
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

    public static void removeResumesFromGroup(String groupId, List<String> list, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("groupId", groupId);
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0, size = list.size(); i < size; i++) {
            stringBuilder.append("\"").append(list.get(i)).append("\"").append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("]");
        param.put("resumeIds", stringBuilder.toString());
        HttpRequestUtil.doHttpPost(URLConst.URL_REMOVE_RESUMES_FROM_GROUP, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
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

    public static void loadTimeSelectorFormGroup(String year, final IDefaultRequestReplyListener<GroupTimeSelectorResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("year", year);
        HttpRequestUtil.doHttpPost(URLConst.URL_TIME_SELECTOR_FROM_GROUP, param, GroupTimeSelectorResultBean.class, new INetworkCallBack<GroupTimeSelectorResultBean>() {
            @Override
            public void onSuccess(GroupTimeSelectorResultBean groupTimeSelectorResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, groupTimeSelectorResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadGroupCollectInfo(final IDefaultRequestReplyListener<GroupIndexResultBean> listener) {
        CustomerHttpRequestUtil.doHttpPost(URLConst.URL_SMART_GROUP_COLLECT_INFO, null, GroupIndexResultBean.class, new INetworkCallBack<CustomerHttpResultBean<GroupIndexResultBean>>() {
            @Override
            public void onSuccess(CustomerHttpResultBean<GroupIndexResultBean> customerHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, customerHttpResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }
}
