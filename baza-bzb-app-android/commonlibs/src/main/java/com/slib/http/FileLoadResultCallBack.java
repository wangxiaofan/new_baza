package com.slib.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Vincent.Lei on 2017/11/2.
 * Title：
 * Note：
 */
public abstract class FileLoadResultCallBack<T> extends BaseResultCallBack<T> {
    public static final int ERROR_NONE = 0;
    public static final int ERROR_SOURCE_IS_NOT_A_FILE = 1;
    public static final int ERROR_FILE_LOAD_SAVE_ERROR = 2;
    public static final int ERROR_FILE_LOAD_NETWORK_ERROR = 3;
    public static final int ERROR_FILE_LOAD_CANNOT_CREATE_FILE = 4;

    private static final int UI_TYPE_LOAD_FAILED = 1;
    private static final int UI_TYPE_LOAD_PROGRESS_CHANGED = 2;
    private static final int UI_TYPE_LOAD_SUCCESS = 3;

    private FileLoadParam mFileTask;
    private int mErrorCode;
    private int mProgress;
    private int mCurrentUiTaskType;
    private File mFileLoad;

    protected FileLoadResultCallBack(FileLoadParam fileTask, String mUrl, Class<T> mClass, HttpConfig mHttpConfig) {
        super(mUrl, mClass, mHttpConfig);
        this.mFileTask = fileTask;
    }

    @Override
    public void run() {
        switch (mCurrentUiTaskType) {
            case UI_TYPE_LOAD_SUCCESS:
                onFileLoadSuccess(mUrl, mFileTask.mTagForSameUrl, mFileLoad);
                break;
            case UI_TYPE_LOAD_PROGRESS_CHANGED:
                onFileLoadProcessChanged(mUrl, mFileTask.mTagForSameUrl, mProgress);
                break;
            case UI_TYPE_LOAD_FAILED:
                onFileLoadFailed(mUrl, mFileTask.mTagForSameUrl, mErrorCode, result);
                break;
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        mCurrentUiTaskType = UI_TYPE_LOAD_FAILED;
        mErrorCode = ERROR_FILE_LOAD_NETWORK_ERROR;
        mHandler.post(this);
    }

    @Override
    public void onResponse(Call call, Response response) {
        if (response == null || response.headers() == null || !response.isSuccessful()) {
            mCurrentUiTaskType = UI_TYPE_LOAD_FAILED;
            mErrorCode = ERROR_FILE_LOAD_NETWORK_ERROR;
            mHandler.post(this);
            return;
        }
        String contentType = response.header("content-type");
        if (mHttpConfig.mRequestAssistHandler != null && !mHttpConfig.mRequestAssistHandler.checkLoadSourceIsFile(mUrl, contentType)) {
            //下载文件出现参数错误或者其他问题
            //解析错误信息
            try {
                String resultStr = response.body().string();
                log(resultStr);
                if (!TextUtils.isEmpty(resultStr) && mClass != String.class) {
                    result = JSON.parseObject(resultStr, mClass);
                } else
                    result = (T) resultStr;
            } catch (Exception e) {
                logE("an error happened when parse http result to object target class is : " + mClass.getName());
            }
            mCurrentUiTaskType = UI_TYPE_LOAD_FAILED;
            mErrorCode = ERROR_SOURCE_IS_NOT_A_FILE;
            mHandler.post(this);
            return;
        }
        File tempFile = FileLoadTool.getTempFile(mFileTask.mFileName, true);
        if (tempFile == null) {
            mCurrentUiTaskType = UI_TYPE_LOAD_FAILED;
            mErrorCode = ERROR_FILE_LOAD_CANNOT_CREATE_FILE;
            mHandler.post(this);
            return;
        }
        if (!loadFileToLocal(tempFile, response))
            return;
        File fileSave = FileLoadTool.getDownFile(mFileTask.path, mFileTask.mFileName, true);
        if (fileSave == null || !copyFile(tempFile.getAbsolutePath(), fileSave.getAbsolutePath())) {
            if (fileSave != null) {
                log(fileSave.getAbsolutePath());
                fileSave.delete();
            }
            mCurrentUiTaskType = UI_TYPE_LOAD_FAILED;
            mErrorCode = ERROR_FILE_LOAD_CANNOT_CREATE_FILE;
            mHandler.post(this);
            return;
        }
        mFileLoad = fileSave;
        mCurrentUiTaskType = UI_TYPE_LOAD_SUCCESS;
        mErrorCode = ERROR_NONE;
        mHandler.post(this);
    }

    private boolean copyFile(String srcPath, String dstPath) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel fisChannel = null;
        FileChannel fosChannel = null;
        try {
            fis = new FileInputStream(srcPath);
            fos = new FileOutputStream(dstPath);
            fisChannel = fis.getChannel();
            fosChannel = fos.getChannel();
            fisChannel.transferTo(0, fisChannel.size(), fosChannel);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fisChannel != null) {
                try {
                    fisChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fosChannel != null) {
                try {
                    fosChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private boolean loadFileToLocal(File tempFile, Response response) {
        boolean isResultOk = true;
        int len;
        long current = 0;
        int progress;
        byte[] buf = new byte[2048];
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            long total = response.body().contentLength();
            is = response.body().byteStream();
            fos = new FileOutputStream(tempFile);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                current += len;
                progress = (int) (current * 1.0f / total * 100);
                if (progress - mProgress >= 3) {
                    mProgress = progress;
                    mCurrentUiTaskType = UI_TYPE_LOAD_PROGRESS_CHANGED;
                    mErrorCode = ERROR_NONE;
                    mHandler.post(this);
                }

            }
            fos.flush();
        } catch (Exception e) {
            isResultOk = false;
            if (tempFile != null && tempFile.exists())
                tempFile.delete();
            logE("download file failed url = " + mUrl);
            logE(e.getMessage());
            mCurrentUiTaskType = UI_TYPE_LOAD_FAILED;
            mErrorCode = ERROR_FILE_LOAD_SAVE_ERROR;
            mHandler.post(this);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e1) {
                    //ignore
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    //ignore
                }
            }
        }
        return isResultOk;
    }

    abstract void onFileLoadSuccess(String targetUrl, String tagForSameUrl, File file);

    abstract void onFileLoadProcessChanged(String targetUrl, String tagForSameUrl, int process);

    abstract void onFileLoadFailed(String targetUrl, String tagForSameUrl, int errorCode, T error);
}
