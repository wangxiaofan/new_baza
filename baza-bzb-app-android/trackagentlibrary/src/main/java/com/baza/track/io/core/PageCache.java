package com.baza.track.io.core;

import android.text.TextUtils;


import com.baza.track.io.constant.TrackConst;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Vincent.Lei on 2018/4/17.
 * Title：
 * Note：
 */
class PageCache {
    private HashMap<String, String> mUIPageNames = new HashMap<>();
    private HashMap<Object, String> mPageViewIdMap = new HashMap<>();
    private HashMap<Object, String> mPageCodeMap = new HashMap<>();

    void setPageName(Object o, String pageName) {
        if (o == null)
            return;
        if (!TextUtils.isEmpty(pageName))
            mUIPageNames.put(o.getClass().getName(), pageName);
    }

    void setPageCode(Object o, String pageCode) {
        if (o == null)
            return;
        if (!TextUtils.isEmpty(pageCode))
            mPageCodeMap.put(o, pageCode);
    }

    String getPageName(Object o) {
        if (o == null)
            return TrackConst.NULL;
        String pageName = mUIPageNames.get(o.getClass().getName());
        return (pageName == null ? o.getClass().getName() : pageName);
    }

    String getPageViewId(Object o) {
        String id = mPageViewIdMap.get(o);
        return id == null ? TrackConst.NULL : id;
    }

    void onPageResume(Object o) {
        if (o != null) {
            if (!mPageViewIdMap.containsKey(o))
                mPageViewIdMap.put(o, UUID.randomUUID().toString());
        }
    }

//    void onPagePause(Object o) {
//
//    }

    void onPageDestroy(Object o) {
        if (o == null)
            return;
        mPageViewIdMap.remove(o);
        mPageCodeMap.remove(o);
    }

    String getPageCode(Object o) {
        if (o == null)
            return TrackConst.NULL;
        String code = mPageCodeMap.get(o);
        return code == null ? o.getClass().getName() : code;
    }
}
