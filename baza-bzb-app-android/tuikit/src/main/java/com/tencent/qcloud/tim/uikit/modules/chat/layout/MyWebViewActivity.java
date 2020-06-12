package com.tencent.qcloud.tim.uikit.modules.chat.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tencent.qcloud.tim.uikit.R;

public class MyWebViewActivity extends Activity {

    private String TAG = "herb";
    private WebView webView;
    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        findViewById(R.id.layout_title_right_text).setVisibility(View.GONE);
        webView = findViewById(R.id.webView);
        url = getIntent().getStringExtra("url");
        ((TextView) findViewById(R.id.layout_title_text)).setText(url);
        findViewById(R.id.layout_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        loadWebView();
    }


    private void loadWebView() {


        //常用设置
        WebSettings webSettings = webView.getSettings();
        //支持Javascript交互:若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量），在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可
        webSettings.setJavaScriptEnabled(true);
        //支持插件
        webSettings.setPluginState(WebSettings.PluginState.ON);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        //支持超链接锚点
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);

        //多窗口
        webSettings.supportMultipleWindows();
        //提高渲染的优先级
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //支持内容重新布局
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //多媒体播放需要用户手动触发的改为false
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }

        // 特别注意：5.1以上默认禁止了https和http混用，以下方式是开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }

        webSettings.setAllowFileAccessFromFileURLs(true);

        //测试
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setGeolocationEnabled(true);

        // # 1 代码中实现
        //WebView webView = new WebView(this);
        //webView.loadUrl("http://www.baidu.com");
        //setContentView(webView);

        //使用当前应用打开（取消默认浏览器）
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();    //表示等待证书响应
                // handler.cancel();      //表示挂起连接，为默认方式
                // handler.handleMessage(null);    //可做其他处理
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //在这里执行你想调用的js函数
                Log.e(TAG, "页面加载完成>>>");
            }
        });

        //拦截webview事件
        webView.setWebChromeClient(new WebChromeClient() {

            //设置打开的进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
//                progress.setProgress(newProgress);
//
//                Log.e(TAG, "打开进度>>>" + progress);
//                if (newProgress == 100) {
//                    progress.setVisibility(View.GONE);
//                }
            }

            //警告框
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                Log.e(TAG, "警告框>>>");
                new AlertDialog.Builder(MyWebViewActivity.this)
                        .setTitle("JsAlert")
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                        .setCancelable(false)
                        .show();
                return true;
            }

            //确认框
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                Log.e(TAG, "确认框>>>");
                new AlertDialog.Builder(MyWebViewActivity.this)
                        .setTitle("JsConfirm")
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        })
                        .setCancelable(false)
                        .show();
                // 返回布尔值：判断点击时确认还是取消
                // true表示点击了确认；false表示点击了取消；
                return true;
            }

            //输入框
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {

                Log.e(TAG, "输入框>>>");

                final EditText et = new EditText(MyWebViewActivity.this);
                et.setText(defaultValue);
                new AlertDialog.Builder(MyWebViewActivity.this)
                        .setTitle(message)
                        .setView(et)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm(et.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        })
                        .setCancelable(false)
                        .show();

                return true;
            }
        });

        //获取触摸焦点
        webView.requestFocusFromTouch();

        //webView.loadUrl("http://ptah.kxjlcc.com:6991/screen-display/");
        webView.loadUrl(url);

//        Intent intent= new Intent();
//        intent.setAction("android.intent.action.VIEW");
//        Uri content_url = Uri.parse("http://ptah.kxjlcc.com:6991/cpic/#/index?callid=01KHBVL7HOF4LC89345H7B5AES1FMBV5");
//        intent.setData(content_url);
//        startActivity(intent);

    }

    /*@Override
    public void finish() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.finish();
        }
    }*/
}
