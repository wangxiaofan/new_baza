package com.slib.http;

import java.io.File;

/**
 * Created by LW on 2016/icon_collect/8.
 * Title :
 * Note :
 */
public interface IFileLoadObserve {
    void onFileStartLoading(String fileUrl, String tagForSameUrl);

    void onFileLoadProgressChanged(String fileUrl, String tagForSameUrl, int progress);

    void onFileLoadSuccess(String fileUrl, String tagForSameUrl, File file);

    void onFileLoadFailed(String fileUrl, String tagForSameUrl, int errorCode, String errorMsg);

}
