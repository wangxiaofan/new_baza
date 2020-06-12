package com.baza.android.bzw.extra;

import android.webkit.JavascriptInterface;

/**
 * Created by Vincent.Lei on 2017/6/7.
 * Title：
 * Note：
 */

public class BaseJSInterface {

    public static final int JS_CALL_EVENT_SHARE = 1;
    public static final int JS_CALL_EVENT_SHARE_INVITE = 2;

    public interface IJSCallNativeInterface {

        void share(int type, String url, String data);

        void emailImport();

        void popBack();

        void openResumeDetail(String candidateId);

        void scanEnableUpdateResumes();

        void openResumeUpdatedList();

        void jumpListMatch();

        void onJsCall(int eventId, String jsonData);
    }

    private IJSCallNativeInterface jsCallNativeInterface;

    public BaseJSInterface(IJSCallNativeInterface jsCallNativeInterface) {
        this.jsCallNativeInterface = jsCallNativeInterface;
    }

    @JavascriptInterface
    public void share(String url, int type, String data) {
        jsCallNativeInterface.share(type, url, data);
    }

    @JavascriptInterface
    public void jumpListMatch() {
        jsCallNativeInterface.jumpListMatch();
    }

    @JavascriptInterface
    public void popBack() {
        jsCallNativeInterface.popBack();
    }

    @JavascriptInterface
    public void emailImport() {
        jsCallNativeInterface.emailImport();
    }

    @JavascriptInterface
    public void openCandidateDetail(String candidateId) {
        jsCallNativeInterface.openResumeDetail(candidateId);
    }

    @JavascriptInterface
    public void scanEnableUpdateResumes() {
        jsCallNativeInterface.scanEnableUpdateResumes();
    }

    @JavascriptInterface
    public void openResumeUpdateList() {
        jsCallNativeInterface.openResumeUpdatedList();
    }

    @JavascriptInterface
    public void onJsCall(int eventId, String jsonData) {
        jsCallNativeInterface.onJsCall(eventId, jsonData);
    }

}
