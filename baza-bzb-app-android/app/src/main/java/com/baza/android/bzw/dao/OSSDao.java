package com.baza.android.bzw.dao;

import com.baza.android.bzw.bean.common.OSSFileUploadResultBean;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vincent.Lei on 2018/8/17.
 * Title：
 * Note：
 */
public class OSSDao {
    private OSSDao() {
    }

    public static final int TYPE_NAME_LIST = 1;
    //    public static final int TYPE_EMAIL = 2;
    public static final int TYPE_AUDIO_ATTACHMENT = 4;

    public static void uploadOSSFile(String filePath, int type, final IDefaultRequestReplyListener<OSSFileUploadResultBean.Data> listener) {
        HashMap<String, String> param = new HashMap<>(1);
        param.put("type", String.valueOf(type));
        File file = new File(filePath);
        List<File> fileList = new ArrayList<>(1);
        fileList.add(file);
        List<String> fileNameList = new ArrayList<>(1);
        fileNameList.add("file");
        HttpRequestUtil.doHttpWithFiles(URLConst.URL_UPLOAD_OSS_FILE, fileList, fileNameList, param, OSSFileUploadResultBean.class, new INetworkCallBack<OSSFileUploadResultBean>() {
            @Override
            public void onSuccess(OSSFileUploadResultBean ossFileUploadResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, ossFileUploadResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }
}
