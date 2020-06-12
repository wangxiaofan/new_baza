package com.baza.android.bzw.dao;

import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.UserInfoManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * Created by Vincent.Lei on 2017/9/15.
 * Title：
 * Note：
 */

public class BZWUrlDao {
    private HashMap<String, String> mCacheAfterWrapperMap = new HashMap<>();

    /**
     * 将连接中page路径替换成pagenew
     * 注意：这是为了兼容旧版  以后新的连接都需要测试这个问题,避免替换成pagenew后导致连接不可用
     */
    public String getCorrectUrl(String url) {
        if (isBZWLink(url) && url.contains("/page/"))
            return url.replace("/page/", "/pagenew/");
        return url;
    }

    private boolean isBZWLink(String url) {
        return (url != null && (url.contains("bazhua") || url.contains("rchezi") || url.contains("ibole")));
    }

    public String wrapperUrl(String url) {
        return wrapperUrl(true, url);
    }

    public String wrapperUrl(boolean isInRCB, String url) {
        if (url != null) {
            try {
                url = URLDecoder.decode(url, "utf-8");
                if (!url.contains("://"))
                    url = "http://" + url;
                if (!isBZWLink(url)) {
                    LogUtil.d(url);
                    return url;
                }
                url = getCorrectUrl(url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String tempUrl = url.replace("/?", "?");
            if (mCacheAfterWrapperMap.get(url) != null || mCacheAfterWrapperMap.get(tempUrl) != null) {
                LogUtil.d("url wrapper cache  =" + url);
                return url;
            }
            LogUtil.d("url before wrapper  =" + url);
            StringBuilder stringBuilder = new StringBuilder();
            String oldParam = null;
            int index = -1;
            int whPosition = -1;
            if (url.contains("#")) {
                index = url.lastIndexOf("#");
                whPosition = url.indexOf("?");
                stringBuilder.append(url, 0, index);
                if (whPosition != -1) {
                    oldParam = url.substring(whPosition, url.length());
                }
            } else
                stringBuilder.append(url);
            if (oldParam != null)
                stringBuilder.append(oldParam);
            boolean containWH = url.contains("?");
            if (isInRCB && !url.contains("from=RCB")) {
                stringBuilder.append((containWH ? "&from=RCB" : "?from=RCB"));
                containWH = true;
            }
            if (!url.contains("unionIdnew=")) {
                stringBuilder.append((containWH ? "&unionIdnew=" : "?unionIdnew=")).append(UserInfoManager.getInstance().getRsaUnionId());
//                containWH = true;
            }
//            if (!url.contains("authorization")) {
//                stringBuilder.append((containWH ? "&authorization=" : "?authorization="))
//                        .append(BZWApplication.getApplication().getUserInfoManager().getAuthorization());
////                containWH = true;
//            }
            if (index != -1)
                stringBuilder.append(url, index, ((whPosition != -1 && whPosition > index) ? whPosition : url.length()));
            url = stringBuilder.toString();
            LogUtil.d("url after wrapper  = :" + url);
            mCacheAfterWrapperMap.put(url, url);
            return url;
        }
        return null;
    }

}
