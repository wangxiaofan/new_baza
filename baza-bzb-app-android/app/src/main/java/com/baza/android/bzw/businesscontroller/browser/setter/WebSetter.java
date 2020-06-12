package com.baza.android.bzw.businesscontroller.browser.setter;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.baza.android.bzw.businesscontroller.browser.jsinterface.JsInterfaceObj;
import com.baza.android.bzw.log.LogUtil;

/**
 * Created by Vincent.Lei on 2019/7/5.
 * Title：
 * Note：
 */
public class WebSetter {

    private static final String INJECT_JS_CLIENT_NAME = "RCBPlatformClient";

    public interface IWebClientListener {
        void onUpdateLoadProgressEvent(int progress);

        void onLoadCompleteEvent();

        void onPageStartEvent();

        void onPageFinishedEvent();

        void onNewUrlEvent(String newUrl);
    }

    public void setWebView(WebView mWebView, String webCachePath, JsInterfaceObj jsObj, IWebClientListener webClientListener) {
        if (webClientListener == null)
            throw new IllegalArgumentException("webClientListener can not be null");
        if (TextUtils.isEmpty(webCachePath))
            throw new IllegalArgumentException("webCachePath can not be null");
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings webSettings = mWebView.getSettings();
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本
        webSettings.setBuiltInZoomControls(true); // 设置WebView可触摸放大缩小
        webSettings.setSupportZoom(true); // 支持缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);//双击变大，再双击后变小，当手动放大后，双击可以恢复到原始大小
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH); //提高渲染的优先级
//        webSettings.setBlockNetworkImage(true);//把图片加载放在最后来加载渲染
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        webSettings.setAppCacheEnabled(true);//H5的缓存打开
        webSettings.setAppCachePath(webCachePath);
        webSettings.setDomStorageEnabled(true);// 开启 DOM storage API 功能
        webSettings.setDatabaseEnabled(true); //开启 database storage API 功能
        webSettings.setSavePassword(true); //开启 database storage API 功能
        //不允许WebView使用File协议
        webSettings.setAllowFileAccess(false);
        //不保存密码
        webSettings.setSavePassword(false);
        //移除不安全的javascript调用
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.removeJavascriptInterface("accessibility");
        mWebView.removeJavascriptInterface("accessibilityTraversal");
        mWebView.setHorizontalScrollBarEnabled(false);

        addClient(mWebView, webClientListener);

        mWebView.addJavascriptInterface(jsObj, INJECT_JS_CLIENT_NAME);
    }

    private void addClient(WebView mWebView, final IWebClientListener webClientListener) {
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 90) {
                    webClientListener.onUpdateLoadProgressEvent(newProgress);
                    return;
                }
                webClientListener.onLoadCompleteEvent();
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtil.d("--------------onPageStarted--------------");
                webClientListener.onPageStartEvent();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtil.d("--------------onPageFinished--------------");
                webClientListener.onPageFinishedEvent();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request == null)
                    return true;
                String url = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (request.getUrl() != null)
                        url = request.getUrl().toString();
                } else
                    url = request.toString();
                if (url == null)
                    return true;
                /*
                 *WebView有一个getHitTestResult():返回的是一个HitTestResult，一般会根据打开的链接的类型，
                 * 返回一个extra的信息，如果打开链接不是一个url，或者打开的链接是JavaScript的url，
                 * 他的类型是UNKNOWN_TYPE，这个url就会通过requestFocusNodeHref(Message)异步重定向。
                 * 返回的extra为null，或者没有返回extra。根据此方法的返回值，判断是否为null，可以用于解决网页重定向问题。
                 */
                if (!TextUtils.isEmpty(url) && view.getHitTestResult() == null) {
                    LogUtil.d("shouldOverrideUrlLoading：" + url);
                    webClientListener.onNewUrlEvent(url);
                    return true;
                }

                return false;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError
                    error) {
                if (error.getPrimaryError() == SslError.SSL_DATE_INVALID
                        || error.getPrimaryError() == SslError.SSL_EXPIRED
                        || error.getPrimaryError() == SslError.SSL_INVALID
                        || error.getPrimaryError() == SslError.SSL_UNTRUSTED) {

                    handler.proceed();

                } else {
                    handler.cancel();
                }
            }
        });
    }

    public void destroyWebView(WebView webView) {
        try {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.removeAllViews();
            webView.clearHistory();
            webView.getSettings().setJavaScriptEnabled(false);
            //7.0以上缓存会引起白屏 暂时先清理
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                webView.clearCache(true);
            webView.destroy();
        } catch (Exception e) {
            //ignore
        }
    }

}
