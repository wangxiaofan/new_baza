package com.slib.http;

import android.text.TextUtils;
import android.util.Log;

import com.slib.storage.file.FileManager;
import com.slib.storage.file.StorageType;
import com.slib.utils.string.MD5;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by LW on 2016/7/8.
 * Title : 文件下载
 * Note :
 */
public class FileLoadTool {
    private static FileLoadTool mInstance = new FileLoadTool();
    private ArrayList<IFileLoadObserve> mListeners = new ArrayList<>(3);
    private HashSet<String> mTaskSet = new HashSet<>();

    private FileLoadTool() {
    }

    public static FileLoadTool getInstance() {
        return mInstance;
    }

    public void registerFileLoadObserve(IFileLoadObserve fileLoadSubscriber) {
        if (fileLoadSubscriber == null)
            return;
        if (!mListeners.contains(fileLoadSubscriber))
            mListeners.add(fileLoadSubscriber);
    }

    public void unRegisterFileLoadObserve(IFileLoadObserve fileLoadSubscriber) {
        if (fileLoadSubscriber == null)
            return;
        mListeners.remove(fileLoadSubscriber);
    }

    public void downLoadFile(String url) {
        downLoadFile(url, null);
    }

    public void downLoadFile(String url, String extensionName) {
        downLoadFile(url, extensionName, null);
    }

    public void downLoadFile(String url, String extensionName, String tagForSameUrl) {
        downLoadFile(url, extensionName, tagForSameUrl, null);
    }

    public void downLoadFile(String url, String extensionName, String tagForSameUrl, HashMap<String, String> header) {
        downLoadFile(url, extensionName, tagForSameUrl, header, null);
    }

    public void downLoadFile(String url, String extensionName, String tagForSameUrl, HashMap<String, String> header, String path) {
        if (url == null)
            return;
        File fileAlreadyDownLoad = getExistsDownLoadFileByUrl(url, tagForSameUrl, path);
        if (fileAlreadyDownLoad != null && fileAlreadyDownLoad.exists()) {
            //文件已经下载
            processFileLoadSuccess(url, tagForSameUrl, fileAlreadyDownLoad);
            return;
        }
        String fileName = getFileNameByUrl(url, extensionName, tagForSameUrl);
        if (mTaskSet.contains(url)) {
            //任务已经在下载
            Log.d("file is on loading  url", url);
            return;
        }
        mTaskSet.add(url);
        processFileLoadStart(url, tagForSameUrl);
        HttpRequestUtil.downLoadFile(url, header, 120000, 30000, new FileLoadResultCallBack<String>(new FileLoadParam(url, fileName, tagForSameUrl, path), url, String.class, HttpRequestUtil.getHttpConfig()) {
            @Override
            void onFileLoadSuccess(String targetUrl, String tagForSameUrl, File file) {
                processFileLoadSuccess(targetUrl, tagForSameUrl, file);
            }

            @Override
            void onFileLoadProcessChanged(String targetUrl, String tagForSameUrl, int process) {
                processFileLoadProcessChanged(targetUrl, tagForSameUrl, process);
            }

            @Override
            void onFileLoadFailed(String targetUrl, String tagForSameUrl, int errorCode, String error) {
                processFileLoadFailed(targetUrl, tagForSameUrl, errorCode, error);
            }
        });
    }


    private void processFileLoadStart(String targetUrl, String tagForSameUrl) {
        if (!mListeners.isEmpty()) {
            for (int i = 0, size = mListeners.size(); i < size; i++) {
                mListeners.get(i).onFileStartLoading(targetUrl, tagForSameUrl);
            }
        }
    }

    private void processFileLoadSuccess(String targetUrl, String tagForSameUrl, File file) {
        if (!mListeners.isEmpty()) {
            for (int i = 0, size = mListeners.size(); i < size; i++) {
                mListeners.get(i).onFileLoadSuccess(targetUrl, tagForSameUrl, file);
            }
        }
        mTaskSet.remove(targetUrl);
    }

    private void processFileLoadProcessChanged(String targetUrl, String tagForSameUrl, int process) {
        if (!mListeners.isEmpty()) {
            for (int i = 0, size = mListeners.size(); i < size; i++) {
                mListeners.get(i).onFileLoadProgressChanged(targetUrl, tagForSameUrl, process);
            }
        }
    }

    private void processFileLoadFailed(String targetUrl, String tagForSameUrl, int errorCode, String error) {
        HttpConfig httpConfig = HttpRequestUtil.getHttpConfig();
        if (httpConfig.mRequestAssistHandler != null)
            error = httpConfig.mRequestAssistHandler.processFileLoadError(targetUrl, error);
        if (!mListeners.isEmpty()) {
            for (int i = 0, size = mListeners.size(); i < size; i++) {
                mListeners.get(i).onFileLoadFailed(targetUrl, tagForSameUrl, errorCode, error);
            }
        }
        mTaskSet.remove(targetUrl);
    }

    public File getExistsDownLoadFileByUrl(String url, String tagForSameUrl, String path) {
        String fileName = getFileNameByUrl(url, null, tagForSameUrl);
        if (!TextUtils.isEmpty(path)) {
            path = path.endsWith(File.separator) ? path : path + File.separator;
            return new File(path + fileName);
        }
        return getDownFile(fileName, false);
    }

    public File getExistsDownLoadFileByUrl(String url, String tagForSameUrl) {
        return getExistsDownLoadFileByUrl(url, tagForSameUrl, null);
    }

    public File getExistsDownLoadFileByUrl(String url) {
        return getExistsDownLoadFileByUrl(url, null);
    }

    /**
     * 获取下载的附件
     *
     * @param fileName     文件名称
     * @param isAutoCreate 文件不存在时是否自动创建
     * @return 下载的附件
     */
    public static File getDownFile(String fileName, boolean isAutoCreate) {
        return getDownFile(null, fileName, isAutoCreate);
    }

    public static File getDownFile(String path, String fileName, boolean isAutoCreate) {
        if (!TextUtils.isEmpty(path)) {
            path = path.endsWith(File.separator) ? path : path + File.separator;
            File file = new File(path + fileName);
            if (isAutoCreate && !file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return file;
        }
        return FileManager.getFile(fileName, StorageType.TYPE_DOWNLOAD, isAutoCreate);
    }

    /**
     * 临时的附件
     *
     * @param fileName     文件名称
     * @param isAutoCreate 文件不存在时是否自动创建
     * @return 临时的附件
     */
    public static File getTempFile(String fileName, boolean isAutoCreate) {
        return FileManager.getFile((fileName + ".temp"), StorageType.TYPE_TEMP, isAutoCreate);
    }

    /**
     * 根据url以及后缀名、相同URL的版本区分标志来确定文件名
     *
     * @param url           文件下载地址
     * @param extensionName 后缀名
     * @param tagForSameUrl 相同Url的版本名(某些URL下载链接不变但是资源可能在不同时期变化 tagForSameUrl区分不同时期，避免缓存旧的文件)
     * @return 文件名
     */

    public static String getFileNameByUrl(String url, String extensionName, String tagForSameUrl) {
        String md5Name = MD5.getStringMD5((!TextUtils.isEmpty(tagForSameUrl) ? (url + tagForSameUrl) : url));
        if (TextUtils.isEmpty(extensionName)) {
            //后缀名为空 根据下载链接判断后缀名
            int index = url.lastIndexOf(".");
            if (index >= 0)
                md5Name = md5Name + url.substring(index, url.length());
        } else
            md5Name = md5Name + (extensionName.startsWith(".") ? extensionName : ("." + extensionName));
        return md5Name;
    }
}
