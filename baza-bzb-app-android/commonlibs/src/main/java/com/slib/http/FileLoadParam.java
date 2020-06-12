package com.slib.http;

/**
 * Created by Vincent.Lei on 2017/11/2.
 * Title：
 * Note：
 */

public class FileLoadParam {
    public String mUrl;
    public String mFileName;
    public String mTagForSameUrl;
    public String path;

    public FileLoadParam(String url, String fileName, String tagForSameUrl,String path) {
        this.mUrl = url;
        this.mFileName = fileName;
        this.mTagForSameUrl = tagForSameUrl;
        this.path = path;
    }
}
