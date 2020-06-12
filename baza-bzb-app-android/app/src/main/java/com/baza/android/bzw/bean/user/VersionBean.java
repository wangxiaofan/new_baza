package com.baza.android.bzw.bean.user;

/**
 * Created by LW on 2016/icon_collect/14.
 * Title :
 * Note :
 */
public class VersionBean {
    /**
     * {"code":0,"msg":"成功","data":{"versionId":"10","clientType":"android","releaseDate":1468482113000,
     * "downloadUrl":"http://rcbox.liebide.com/downloads/app/rcbox_1.1.2.apk","description":"versionNo 1.1.2"}}
     */

    public String downloadUrl;
    public String description;
    public int versionId;
    public int forceUpdate;
    public String versionNo;
    public long appSize;//Kb

    public boolean isNotForceUpdate() {
        return forceUpdate == 0;
    }

}
