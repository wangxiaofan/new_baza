package com.baza.android.bzw.dao;

import android.os.Handler;

import com.baza.android.bzw.bean.operational.DialogActivityResultBean;
import com.slib.http.FileLoadTool;
import com.slib.http.IFileLoadObserve;
import com.slib.storage.database.listener.IDBReplyListener;

import java.io.File;
import java.util.List;

/**
 * Created by Vincent.Lei on 2018/2/11.
 * Title：
 * Note：
 */

public class ActivityPrepareDao {
    public interface IActivityPrepareListener {
        void onActivityPrepareOK(DialogConfig dialogConfig);
    }

    private List<DialogActivityResultBean.Data> mDataList;
    private Handler mHandler;
    private String mUnionId;
    private IActivityPrepareListener mListener;

    public ActivityPrepareDao(List<DialogActivityResultBean.Data> dataList, String unionId, Handler handler, IActivityPrepareListener listener) {
        this.mDataList = dataList;
        this.mHandler = handler;
        this.mListener = listener;
        this.mUnionId = unionId;
    }

    public void prepare() {
        if (mDataList == null || mDataList.isEmpty())
            return;
        DialogActivityResultBean.Data data;
        for (int i = 0, size = mDataList.size(); i < size; i++) {
            data = mDataList.get(i);
            if (data.isDataEnable())
                new DialogConfig(data, mUnionId, mListener, mHandler).prepare();
        }
    }

    public static class DialogConfig implements Runnable, IFileLoadObserve {
        private static final int BACKGROUND_LOAD = 1;
        private static final int BUTTON_LOAD = 1 << 1;
        private DialogActivityResultBean.Data data;
        private IActivityPrepareListener mListener;
        private Handler mHandler;
        private String mUnionId;

        public String mBackgroundPath;
        public String mButtonPath;
        public String mActivityUrl;

        private int mLoadStatus;

        DialogConfig(DialogActivityResultBean.Data data, String unionId, IActivityPrepareListener mListener, Handler mHandler) {
            this.data = data;
            this.mListener = mListener;
            this.mHandler = mHandler;
            this.mUnionId = unionId;
            this.mActivityUrl = data.buttonEventUrl;
        }

        public void updateTargetActivityDialogShowCount() {
            OperationalDao.updateTargetActivityDialogShowCount(mUnionId, data.id);
        }

        private void prepare() {
            if (data.showCount == DialogActivityResultBean.Data.SHOW_ALWAYS) {
                checkResource();
                return;
            }
            OperationalDao.queryTargetActivityDialogShowCount(mUnionId, data.id, new IDBReplyListener<Integer>() {
                @Override
                public void onDBReply(Integer integer) {
                    if (integer == null || integer < data.showCount)
                        checkResource();
                }
            });
        }

        private void checkResource() {
            File file = FileLoadTool.getInstance().getExistsDownLoadFileByUrl(data.backgroundImageUrl);
            if (file != null && file.exists()) {
                mBackgroundPath = file.getAbsolutePath();
                mLoadStatus |= BACKGROUND_LOAD;
            }
            file = FileLoadTool.getInstance().getExistsDownLoadFileByUrl(data.buttonImageUrl);
            if (file != null && file.exists()) {
                mButtonPath = file.getAbsolutePath();
                mLoadStatus |= BUTTON_LOAD;
            }
            if (mBackgroundPath != null && mButtonPath != null) {
                mHandler.post(this);
                return;
            }
            FileLoadTool.getInstance().registerFileLoadObserve(this);
            if (mBackgroundPath == null)
                FileLoadTool.getInstance().downLoadFile(data.backgroundImageUrl);
            if (mButtonPath == null)
                FileLoadTool.getInstance().downLoadFile(data.buttonImageUrl);
        }


        @Override
        public void run() {
            if (mListener != null)
                mListener.onActivityPrepareOK(this);
        }

        private void checkLoad(boolean success, String fileUrl, File file) {
            if (fileUrl.equals(data.backgroundImageUrl)) {
                mLoadStatus |= BACKGROUND_LOAD;
                if (success && file != null)
                    mBackgroundPath = file.getAbsolutePath();
            } else if (fileUrl.equals(data.buttonImageUrl)) {
                mLoadStatus |= BUTTON_LOAD;
                if (success && file != null)
                    mButtonPath = file.getAbsolutePath();
            }
            if ((mLoadStatus & BACKGROUND_LOAD) != 0 && (mLoadStatus & BUTTON_LOAD) != 0) {
                FileLoadTool.getInstance().unRegisterFileLoadObserve(this);
                if (mBackgroundPath != null && mButtonPath != null)
                    mHandler.post(this);
            }

        }

        @Override
        public void onFileStartLoading(String fileUrl, String tagForSameUrl) {

        }

        @Override
        public void onFileLoadProgressChanged(String fileUrl, String tagForSameUrl, int progress) {

        }

        @Override
        public void onFileLoadSuccess(String fileUrl, String tagForSameUrl, File file) {
            checkLoad(true, fileUrl, file);
        }

        @Override
        public void onFileLoadFailed(String fileUrl, String tagForSameUrl, int errorCode, String errorMsg) {
            checkLoad(false, fileUrl, null);
        }
    }

}
