package com.baza.android.bzw.businesscontroller.browser.presenter;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.log.LogUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * Created by Vincent.Lei on 2019/7/8.
 * Title：
 * Note：
 */
public class BZWUrlPresenter extends BasePresenter {
    private static final String STR_PAGE = "/page/";
    private static final String STR_PAGE_REPLACE = "/pagenew/";
    private static final String BZW_URL_REGEX = "(\\S*(bazhua|rchezi|ibole)\\S*)|(http://192.168.22.94:8083\\S*)";
    private static final String PROTOCOL_REGEX = "(http|https|file)://\\S*";
    private static final String PROTOCOL_HTTPS = "https://";

    public interface IURLEventListener {
        String getRsaUnionId();
    }


    private IURLEventListener mUrlEventListener;
    private HashMap<String, String> mCacheAfterWrapperMap = new HashMap<>();

    public BZWUrlPresenter(IURLEventListener urlEventListener) {
        this.mUrlEventListener = urlEventListener;
        if (mUrlEventListener == null)
            throw new IllegalArgumentException("urlEventListener is null");
    }

    @Override
    public void initialize() {

    }

    String getCompatibleForOldVersionUrl(String url) {
        if (isBZWLink(url) && url.contains(STR_PAGE))
            return url.replace(STR_PAGE, STR_PAGE_REPLACE);
        return url;
    }

    private boolean isBZWLink(String url) {
        return (url != null && url.matches(BZW_URL_REGEX));
    }

    public String getWrappedUrl(String url) {
        return getWrappedUrl(true, url);
    }

    public String getWrappedUrl(boolean isInRCB, String urlOriginal) {
        String url = urlOriginal;
        if (url != null) {
            url = decodeAndFillUrl(url);
            if (!isBZWLink(url)) {
                LogUtil.d(urlOriginal);
                return urlOriginal;
            }
            url = getCompatibleForOldVersionUrl(url);
            return wrapperAndAppendParamForBZWUrl(isInRCB, url);
        }
        return null;
    }

    private String wrapperAndAppendParamForBZWUrl(boolean isInRCB, String url) {
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
        if (!url.contains("unionIdnew="))
            stringBuilder.append((containWH ? "&unionIdnew=" : "?unionIdnew=")).append(mUrlEventListener.getRsaUnionId());

        if (index != -1)
            stringBuilder.append(url, index, ((whPosition != -1 && whPosition > index) ? whPosition : url.length()));
        url = stringBuilder.toString();
        LogUtil.d("url after wrapper  = :" + url);
        mCacheAfterWrapperMap.put(url, url);
        return url;
    }

    private String decodeAndFillUrl(String urlOriginal) {
        try {
            urlOriginal = URLDecoder.decode(urlOriginal, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (!urlOriginal.matches(PROTOCOL_REGEX))
            urlOriginal = PROTOCOL_HTTPS + urlOriginal;
        return urlOriginal;
    }

}
