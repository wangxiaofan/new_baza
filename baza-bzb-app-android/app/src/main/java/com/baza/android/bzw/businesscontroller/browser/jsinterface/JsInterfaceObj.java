package com.baza.android.bzw.businesscontroller.browser.jsinterface;

import android.webkit.JavascriptInterface;

/**
 * Created by Vincent.Lei on 2019/7/8.
 * Title：
 * Note：
 */
public class JsInterfaceObj {
    public interface IJsCallBack {
        void popBack();

        void emailImport();

        void openCandidateDetail(String candidateId);

        void share(String url, int type, String data);

        void jumpListMatch();

        void scanEnableUpdateResumes();

        void openResumeUpdateList();

        void onJsCall(int eventId, String jsonData);
    }

    private IJsCallBack jsCallBack;

    public JsInterfaceObj(IJsCallBack jsCallBack) {
        this.jsCallBack = jsCallBack;
        if (jsCallBack == null)
            throw new IllegalArgumentException("jsCallBack is null");
    }

    @JavascriptInterface
    public void share(String url, int type, String data) {
        jsCallBack.share(url, type, data);
    }

    @JavascriptInterface
    public void jumpListMatch() {
        jsCallBack.jumpListMatch();
    }

    @JavascriptInterface
    public void popBack() {
        jsCallBack.popBack();
    }

    @JavascriptInterface
    public void emailImport() {
        jsCallBack.emailImport();
    }

    @JavascriptInterface
    public void openCandidateDetail(String candidateId) {
        jsCallBack.openCandidateDetail(candidateId);
    }

    @JavascriptInterface
    public void scanEnableUpdateResumes() {
        jsCallBack.scanEnableUpdateResumes();
    }

    @JavascriptInterface
    public void openResumeUpdateList() {
        jsCallBack.openResumeUpdateList();
    }

    @JavascriptInterface
    public void onJsCall(int eventId, String jsonData) {
        jsCallBack.onJsCall(eventId, jsonData);
    }

}
