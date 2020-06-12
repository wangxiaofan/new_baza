package com.baza.android.bzw.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.label.CreateLabelResultBean;
import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.label.LabelListResultBean;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/25.
 * Title：
 * Note：
 */

public class LabelDao {
    private LabelDao() {
    }

    public static int findTargetLabelPosition(Label label, List<Label> mAllLabels) {
        if (label == null || mAllLabels == null)
            return -1;
        for (int i = 0, size = mAllLabels.size(); i < size; i++) {
            if (label.tag.equals(mAllLabels.get(i).tag)) {
                return i;
            }
        }
        return -1;
    }

    public static void loadLabelLibrary(final IDefaultRequestReplyListener<List<Label>> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_ALL_LABELS, null, LabelListResultBean.class, new INetworkCallBack<LabelListResultBean>() {
            @Override
            public void onSuccess(LabelListResultBean labelListResultBean) {
                if (listener != null) {
                    if (labelListResultBean.data != null && !labelListResultBean.data.isEmpty()) {
                        Comparator<Label> comparator = new Comparator<Label>() {
                            @Override
                            public int compare(Label o1, Label o2) {
                                return (int) (o2.createTime - o1.createTime);
                            }
                        };
                        Collections.sort(labelListResultBean.data, comparator);
                    }
                    listener.onRequestReply(true, labelListResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
                }
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void createNewTag(String tag, final IDefaultRequestReplyListener<Label> listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tagName", tag);
        HttpRequestUtil.doHttpPost(URLConst.URL_CREATE_LABEL, map, CreateLabelResultBean.class, new INetworkCallBack<CreateLabelResultBean>() {
            @Override
            public void onSuccess(CreateLabelResultBean createLabelRequestBean) {
                boolean success = createLabelRequestBean.data != null;
                if (listener != null)
                    listener.onRequestReply(true, (success ? createLabelRequestBean.data : null), (success ? CustomerRequestAssistHandler.NET_REQUEST_OK : CustomerRequestAssistHandler.getErrorCode(createLabelRequestBean)), (success ? null : CustomerRequestAssistHandler.getErrorMsg(createLabelRequestBean)));
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void deleteTag(String tag, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tag", String.valueOf(tag));
        HttpRequestUtil.doHttpPost(URLConst.URL_DELETE_LABEL, map, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean createLabelRequestBean) {
                if (listener != null)
                    listener.onRequestReply(true, createLabelRequestBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void saveLabelForResume(String candidateId, List<Label> list, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("candidateId", candidateId);
        boolean clearTags = (list == null || list.isEmpty());
        JSONArray jsonArray = new JSONArray();
        if (!clearTags) {
            for (int i = 0, size = list.size(); i < size; i++) {
                jsonArray.add(list.get(i).tag);
            }
            map.put("tags", JSON.toJSONString(jsonArray));
        }

        HttpRequestUtil.doHttpPost((clearTags ? URLConst.URL_RESUME_CLEAR_LABELS : URLConst.URL_RESUME_BING_LABELS), map, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean createLabelRequestBean) {
                if (listener != null)
                    listener.onRequestReply(true, createLabelRequestBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }
}
