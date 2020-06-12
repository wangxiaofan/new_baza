package com.baza.android.bzw.dao;

import android.database.Cursor;
import android.text.TextUtils;

import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.email.AddresseeBean;
import com.baza.android.bzw.bean.email.BindEmailResultBean;
import com.baza.android.bzw.bean.email.EmailAttachmentBean;
import com.baza.android.bzw.bean.email.EmailAttachmentUploadResultBean;
import com.baza.android.bzw.bean.email.ListSyncEmailResultBean;
import com.baza.android.bzw.bean.resumeelement.ShortAttachmentBean;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.UserInfoManager;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;
import com.slib.storage.database.DBWorker;
import com.slib.storage.database.core.DataBaseManager;
import com.slib.storage.database.handler.IDBControllerHandler;
import com.slib.storage.database.listener.IDBReplyListener;
import com.slib.utils.AppUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/31.
 * Title：
 * Note：
 */

public class EmailDao {
    private EmailDao() {
    }

    public static void getListSyncEmail(final IDefaultRequestReplyListener<ListSyncEmailResultBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_GET_SYNC_EMAIL_RESUME_ACCOUNT_LIST, null, ListSyncEmailResultBean.class, new INetworkCallBack<ListSyncEmailResultBean>() {
            @Override
            public void onSuccess(ListSyncEmailResultBean listSyncEmailRequestBean) {
                if (listener != null)
                    listener.onRequestReply(true, listSyncEmailRequestBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void saveSyncEmail(int unknowTypeId, String email, String password, String host, String port, boolean ssl, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("account", email);
        param.put("password", password);
        if (!TextUtils.isEmpty(host) && !TextUtils.isEmpty(port)) {
            param.put("emailServer", host);
            param.put("emailPort", port);
            param.put("enableSSL", String.valueOf(ssl ? 1 : 0));
            if (unknowTypeId > 0)
                param.put("emailType", String.valueOf(unknowTypeId));
        }
        HttpRequestUtil.doHttpPost(URLConst.URL_SAVE_SYNC_EMAIL_RESUME_ACCOUNT, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
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

    public static void startSyncEmailResume(String account, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("account", account);
        HttpRequestUtil.doHttpPost(URLConst.URL_START_SYNC_EMAIL_RESUME, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
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

    public static void deleteSyncedEmail(String account, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("account", account);
        HttpRequestUtil.doHttpPost(URLConst.URL_DELETE_SYNC_EMAIL, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
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

    public static void saveToLocal(final List<AddresseeBean> list) {
        if (list == null)
            return;
        List<String> whereClauseList = new ArrayList<>();
        List<String[]> whereArgsList = new ArrayList<>();
        String where = "uid = ? and email = ?";
        String userId = String.valueOf(UserInfoManager.getInstance().getUserInfo().userId);
        for (int i = 0, size = list.size(); i < size; i++) {
            whereClauseList.add(where);
            whereArgsList.add(new String[]{userId, list.get(i).email});
        }
        DBWorker.saveList(list, null, whereClauseList, whereArgsList, null, false);
    }

    public static void readLocalEmails(final IDBReplyListener<List<AddresseeBean>> listener) {
        DBWorker.query(AddresseeBean.class, "uid = ?", new String[]{String.valueOf(UserInfoManager.getInstance().getUserInfo().userId)}, listener);
    }


    public static void checkBindEmail(final IDefaultRequestReplyListener<BindEmailResultBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_GET_BIND_EMAIL, null, BindEmailResultBean.class, new INetworkCallBack<BindEmailResultBean>() {
            @Override
            public void onSuccess(BindEmailResultBean bindEmailRequestBean) {
                if (listener != null)
                    listener.onRequestReply(true, bindEmailRequestBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void bindEmail(String email, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);
        HttpRequestUtil.doHttpPost(URLConst.URL_SET_BIND_EMAIL, map, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
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

    public static void unBindEmail(final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_UN_BIND_EMAIL, null, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
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

    public static void emailShare(String candidateId, List<AddresseeBean> addresseeList, String des, boolean isShareStand, boolean isShareOriginal, boolean isShareContact, boolean isShareRemark, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("candidateId", candidateId);
        param.put("standard", String.valueOf((isShareStand ? 1 : 0)));
        param.put("original", String.valueOf((isShareOriginal ? 1 : 0)));
        param.put("allHide", String.valueOf((isShareContact ? 0 : 1)));
        param.put("remarkHide", String.valueOf((isShareRemark ? 0 : 1)));
        if (!TextUtils.isEmpty(des))
            param.put("content", des);
        String emailParam = appendEmailParam(addresseeList);
        LogUtil.d(emailParam);
        param.put("recipients", emailParam);
        HttpRequestUtil.doHttpPost(URLConst.URL_EMAIL_SHARE, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
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

    private static String appendEmailParam(List<AddresseeBean> mAddresseeList) {
        String dh = ",";
        String yh = "\"";
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0, size = mAddresseeList.size(); i < size; i++)
            stringBuilder.append(yh).append(mAddresseeList.get(i).email).append(yh).append(dh);
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public static void uploadEmailAttachment(File file, final IDefaultRequestReplyListener<EmailAttachmentBean> listener) {
        List<File> fileList = new ArrayList<>(1);
        fileList.add(file);
        HttpRequestUtil.doHttpWithFiles(URLConst.URL_UPLOAD_EMAIL_ATTACHMENT, fileList, null, null, EmailAttachmentUploadResultBean.class, new INetworkCallBack<EmailAttachmentUploadResultBean>() {
            @Override
            public void onSuccess(EmailAttachmentUploadResultBean emailAttachmentUploadResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, emailAttachmentUploadResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void sendEmailToTargetResume(String recipient, String title, String content, List<EmailAttachmentBean> attachmentBeanList, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("recipients", "[\"" + recipient + "\"]");
        param.put("title", title);
        param.put("content", content);
        String attachmentJson = getEmailAttachmentJson(attachmentBeanList);
        if (attachmentJson != null) {
            LogUtil.d(attachmentJson);
            param.put("attachments", attachmentJson);
        }
        HttpRequestUtil.doHttpPost(URLConst.URL_SEND_NORMAL_EMAIL, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
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

    private static String getEmailAttachmentJson(List<EmailAttachmentBean> attachmentBeanList) {
        if (attachmentBeanList == null || attachmentBeanList.isEmpty())
            return null;
        List<ShortAttachmentBean> list = new ArrayList<>(attachmentBeanList.size());
        EmailAttachmentBean e;
        for (int i = 0, size = attachmentBeanList.size(); i < size; i++) {
            e = attachmentBeanList.get(i);
            list.add(new ShortAttachmentBean(e.fileName, e.ossKey));
        }
        return AppUtil.objectToJson(list);
    }


}
