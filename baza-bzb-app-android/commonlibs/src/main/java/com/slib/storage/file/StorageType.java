package com.slib.storage.file;

/**
 * Created by Vincent.Lei on 2016/11/5.
 * Title : 存储类型分类
 * Note :
 */

public enum StorageType {

    TYPE_DATA("data/"),
    TYPE_CACHE("cache/"),
    TYPE_IMAGE("image/"),
    TYPE_AUDIO("audio/"),
    TYPE_LOG("log/"),
    TYPE_TEMP("temp/"),
    TYPE_DOWNLOAD("download/"),
    TYPE_ATTACHMENT("attachment/"),
    TYPE_OTHER("other/");


    private String path;

    public String getPath() {
        return path;
    }

    StorageType(String path) {
        this.path = path;
    }
}
