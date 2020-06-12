package com.baza.android.bzw.dao;

import android.text.TextUtils;

import com.baza.android.bzw.bean.CustomerHttpResultBean;
import com.baza.android.bzw.bean.common.OSSFileUploadResultBean;
import com.baza.android.bzw.bean.resume.AudioLinkBean;
import com.baza.android.bzw.bean.resume.RemarkAudioAttachmentMapBean;
import com.baza.android.bzw.bean.resumeelement.RemarkBean;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.CustomerHttpRequestUtil;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.UserInfoManager;
import com.slib.http.FileLoadTool;
import com.slib.http.IFileLoadObserve;
import com.slib.http.INetworkCallBack;
import com.slib.storage.database.DBWorker;
import com.slib.storage.database.listener.IDBReplyListener;
import com.slib.storage.file.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Vincent.Lei on 2019/8/5.
 * Title：
 * Note：
 */
public class AttachmentRemarkDao {
    public static final Integer NORMAL = 0;
    public static final Integer LOADING = 1;
    public static final Integer FAILED = 2;
    public static final Integer SUCCESS = 3;

    private static final String REMARK_AUDIO_PATH_NAME = "/audio/";
    private static List<IRemarkAudioLoadListener> loadListeners = new ArrayList<>();
    private static HashMap<String, Integer> mOnLoadingStatusSet = new HashMap<>();
    private static HashMap<String, String> mLocalAttachmentMap = new HashMap<>();
    private static HashMap<String, String> mRemarkAudioLinkBean2IdMap = new HashMap<>();
    private static final IFileLoadObserve fileLoadObserve = new IFileLoadObserve() {
        @Override
        public void onFileStartLoading(String fileUrl, String tagForSameUrl) {

        }

        @Override
        public void onFileLoadProgressChanged(String fileUrl, String tagForSameUrl, int progress) {
        }

        @Override
        public void onFileLoadSuccess(String fileUrl, String tagForSameUrl, File file) {
            String inquiryId = mRemarkAudioLinkBean2IdMap.remove(fileUrl);
            if (inquiryId == null)
                return;
            mLocalAttachmentMap.put(inquiryId, file.getAbsolutePath());
            mOnLoadingStatusSet.put(inquiryId, SUCCESS);
            saveAudioAttachmentMapRecord(inquiryId, file.getAbsolutePath());
            noticeResult(inquiryId, SUCCESS, file);
        }

        @Override
        public void onFileLoadFailed(String fileUrl, String tagForSameUrl, int errorCode, String errorMsg) {
            String inquiryId = mRemarkAudioLinkBean2IdMap.remove(fileUrl);
            if (inquiryId == null)
                return;
            noticeResult(inquiryId, FAILED, null);
        }
    };

    public interface IRemarkAudioLoadListener {
        void onRemarkAudioLoadFailed(String inquiryId);

        void onRemarkAudioLoadLoadSuccess(String inquiryId, File audioFile);
    }

    private static String getRemarkAudioPath() {
        return FileManager.getPrivateDir(REMARK_AUDIO_PATH_NAME, true);
    }

    public static File getRemarkAudioLocalFilePath(String inquiryId) {
        String filePath = mLocalAttachmentMap.get(inquiryId);
        if (filePath != null)
            return new File(filePath);
        return null;
    }


    public static void loadRemarkAudioSourceFile(final RemarkBean remarkBean) {
        if (TextUtils.isEmpty(remarkBean.inquiryId))
            return;
        mOnLoadingStatusSet.put(remarkBean.inquiryId, LOADING);
        HashMap<String, String> param = new HashMap<>();
        param.put("fileKey", remarkBean.getAttachmentKey());
        CustomerHttpRequestUtil.doHttpPost(URLConst.URL_GET_AUDIO_SOURCE_LOAD_URL, param, AudioLinkBean.class, new INetworkCallBack<CustomerHttpResultBean<AudioLinkBean>>() {
            @Override
            public void onSuccess(CustomerHttpResultBean<AudioLinkBean> audioLinkCustomerHttpResultBean) {
                if (audioLinkCustomerHttpResultBean.data == null || TextUtils.isEmpty(audioLinkCustomerHttpResultBean.data.link)) {
                    noticeResult(remarkBean.inquiryId, FAILED, null);
                    return;
                }
                remarkBean.setAttachmentLink(audioLinkCustomerHttpResultBean.data.link);
                mRemarkAudioLinkBean2IdMap.put(audioLinkCustomerHttpResultBean.data.link, remarkBean.inquiryId);
                FileLoadTool.getInstance().registerFileLoadObserve(fileLoadObserve);
                FileLoadTool.getInstance().downLoadFile(audioLinkCustomerHttpResultBean.data.link, remarkBean.isPhoneRemark() ? ".mp3" : ".aac", null, null, getRemarkAudioPath());
            }

            @Override
            public void onFailed(Object object) {
                noticeResult(remarkBean.inquiryId, FAILED, null);
            }
        });
    }

    public static void deleteRemarkAttachmentInfo(RemarkBean remarkBean) {
        String filePath = mLocalAttachmentMap.remove(remarkBean.inquiryId);
        File file = new File(filePath);
        if (file.exists())
            file.delete();
        mOnLoadingStatusSet.remove(remarkBean.inquiryId);
        if (mRemarkAudioLinkBean2IdMap.isEmpty())
            return;
        Iterator<Map.Entry<String, String>> iterator = mRemarkAudioLinkBean2IdMap.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue().equals(remarkBean.inquiryId))
                iterator.remove();
        }
    }

    private static void noticeResult(String inquiryId, Integer status, File file) {
        mOnLoadingStatusSet.put(inquiryId, status);
        if (!loadListeners.isEmpty()) {
            for (IRemarkAudioLoadListener listener : loadListeners) {
                if (status.equals(FAILED))
                    listener.onRemarkAudioLoadFailed(inquiryId);
                else
                    listener.onRemarkAudioLoadLoadSuccess(inquiryId, file);
            }
        }
    }

    public static void registerListeners(IRemarkAudioLoadListener loadListener) {
        if (loadListener == null)
            return;
        if (!loadListeners.contains(loadListener))
            loadListeners.add(loadListener);
    }

    public static void unRegisterListeners(IRemarkAudioLoadListener loadListener) {
        if (loadListener == null)
            return;
        loadListeners.remove(loadListener);
    }

    public static Integer getAudioSourceOnLoadingStatus(String inquiryId) {
        if (mOnLoadingStatusSet.containsKey(inquiryId))
            return mOnLoadingStatusSet.get(inquiryId);
        return NORMAL;
    }

    private static void saveAudioAttachmentMapRecord(String inquiryId, String filePath) {
        String unionId = UserInfoManager.getInstance().getUserInfo().unionId;
        if (TextUtils.isEmpty(unionId) || TextUtils.isEmpty(inquiryId) || TextUtils.isEmpty(filePath))
            return;
        RemarkAudioAttachmentMapBean mapBean = new RemarkAudioAttachmentMapBean();
        mapBean.inquiryId = inquiryId;
        mapBean.unionId = unionId;
        mapBean.filePath = filePath;
        DBWorker.saveSingle(mapBean, null, "inquiryId = ? and unionId = ?", new String[]{inquiryId, unionId}, null, false);
    }

    private static void deleteAudioAttachmentMapRecord(String inquiryId) {
        String unionId = UserInfoManager.getInstance().getUserInfo().unionId;
        if (TextUtils.isEmpty(unionId) || TextUtils.isEmpty(inquiryId))
            return;
        DBWorker.delete(RemarkAudioAttachmentMapBean.class, "inquiryId = ? and unionId = ?", new String[]{inquiryId, unionId});
    }

    public static void init() {
        String unionId = UserInfoManager.getInstance().getUserInfo().unionId;
        if (TextUtils.isEmpty(unionId))
            return;
        mLocalAttachmentMap.clear();
        mOnLoadingStatusSet.clear();
        LogUtil.d("AttachmentRemarkDao init");
        DBWorker.query(RemarkAudioAttachmentMapBean.class, "unionId = ?", new String[]{unionId}, new IDBReplyListener<List<RemarkAudioAttachmentMapBean>>() {
            @Override
            public void onDBReply(List<RemarkAudioAttachmentMapBean> list) {
                if (list != null && list.size() > 0) {
                    RemarkAudioAttachmentMapBean mapBean;
                    for (int i = 0, size = list.size(); i < size; i++) {
                        mapBean = list.get(i);
                        LogUtil.d(mapBean.toString());
                        if (!TextUtils.isEmpty(mapBean.filePath) && new File(mapBean.filePath).exists()) {
                            mLocalAttachmentMap.put(mapBean.inquiryId, mapBean.filePath);
                            mOnLoadingStatusSet.put(mapBean.inquiryId, SUCCESS);
                        } else {
                            deleteAudioAttachmentMapRecord(mapBean.inquiryId);
                        }

                    }
                }
            }
        });
    }

    public interface ICreateAttachmentRemarkListener {
        void onUploadAttachment();

        void onUploadAttachmentResult(boolean success, OSSFileUploadResultBean.Data data, int errorCode, String errorMsg);

        void onCreateRemark();

        void onCreateRemarkResult(boolean success, int errorCode, String errorMsg, OSSFileUploadResultBean.Data data, RemarkBean newRemarkBean);
    }

    private static void createAudioRemark(final String resumeId, final File audioFile, final int audioLength, final ICreateAttachmentRemarkListener listener) {
        listener.onUploadAttachment();
        OSSDao.uploadOSSFile(audioFile.getAbsolutePath(), OSSDao.TYPE_AUDIO_ATTACHMENT, new IDefaultRequestReplyListener<OSSFileUploadResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, final OSSFileUploadResultBean.Data data, int errorCode, String errorMsg) {
                success = (success && data != null);
                listener.onUploadAttachmentResult(success, data, errorCode, errorMsg);
                if (success) {
                    createAudioRemark(resumeId, audioFile, data, audioLength, listener);
                }
            }
        });
    }

    public static void createAudioRemark(final String resumeId, final File audioFile, final OSSFileUploadResultBean.Data oss, int audioLength, final ICreateAttachmentRemarkListener listener) {
        if (oss == null) {
            createAudioRemark(resumeId, audioFile, audioLength, listener);
            return;
        }
        listener.onCreateRemark();
        ResumeDao.addAudioAttachmentRemark(resumeId, oss.ossKey, audioLength, new IDefaultRequestReplyListener<RemarkBean>() {
            @Override
            public void onRequestReply(boolean success2, RemarkBean remarkBean, int errorCode, String errorMsg) {
                success2 = (success2 && remarkBean != null);
                if (success2) {
                    remarkBean.inquiryId = remarkBean.id;
                    remarkBean.updateTime = System.currentTimeMillis();
                    remarkBean.userId = UserInfoManager.getInstance().getUserInfo().userId;
                    remarkBean.isMyCreate = RemarkBean.INQUIRY_CREATE_BY_ME;
                    saveAudioAttachmentMapRecord(remarkBean.inquiryId, audioFile.getAbsolutePath());
                    mLocalAttachmentMap.put(remarkBean.inquiryId, audioFile.getAbsolutePath());
                    mOnLoadingStatusSet.put(remarkBean.inquiryId, SUCCESS);
                }
                listener.onCreateRemarkResult(success2, errorCode, errorMsg, oss, remarkBean);
            }
        });
    }
}
