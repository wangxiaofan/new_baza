package com.slib.http;

import java.util.HashMap;

/**
 * Created by Vincent.Lei on 2017/3/2.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public interface IRequestAssistHandler {
    boolean isResultOK(Object result);

    HashMap<String, String> getDefaultHeader();

    String processFileLoadError(String url, String error);

    boolean checkLoadSourceIsFile(String targetUrl, String contentType);
}
