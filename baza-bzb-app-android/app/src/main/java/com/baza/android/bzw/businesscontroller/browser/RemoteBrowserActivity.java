package com.baza.android.bzw.businesscontroller.browser;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.browser.jsinterface.JsInterfaceObj;
import com.baza.android.bzw.businesscontroller.browser.presenter.BZWUrlPresenter;
import com.baza.android.bzw.businesscontroller.browser.presenter.BZWWebEventPresenter;
import com.baza.android.bzw.businesscontroller.browser.setter.WebSetter;
import com.baza.android.bzw.businesscontroller.browser.viewinterface.IRemoteBrowserView;
import com.baza.android.bzw.businesscontroller.find.updateengine.ResumeEnableUpdateListActivity;
import com.baza.android.bzw.businesscontroller.find.updateengine.ResumeUpdatedRecordsActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.RPCConst;
import com.baza.android.bzw.manager.AppGlobalManager;
import com.bznet.android.rcbox.R;
import com.slib.multiprocesssimpleconnect.IRPCConnectionHandler;
import com.slib.multiprocesssimpleconnect.RPCConnectManager;
import com.slib.multiprocesssimpleconnect.RPCConnectWrapper;
import com.slib.progress.IndeterminateHorizontalProgressDrawable;
import com.slib.utils.ToastUtil;

import java.util.HashMap;

/**
 * Created by Vincent.Lei on 2019/7/5.
 * Title：
 * Note：这是一个运行在单独进程中的WebView，如果需要访问主进程或者其他进程信息，需要与对应进程建立连接
 * ，详见：buildRPCConnector()    destroyRPCConnector()
 */
public class RemoteBrowserActivity extends BaseActivity implements View.OnClickListener, IRemoteBrowserView {
    WebView webView;
    ProgressBar progressBar;
    TextView textView_title;
    View view_titleBar;

    private boolean mHasBindAppProcessBefore;
    private BZWUrlPresenter mUrlPresenter;
    private BZWWebEventPresenter mWebEventPresenter;
    private WebSetter mWebSetter = new WebSetter();
    private HashMap<String, Object> mCachedParam = new HashMap<>();

    private static final String P_TITLE = "title";
    private static final String P_LOAD_URL = "loadUrl";
    private static final String P_USE_TITLE_BAR = "useTitleBar";
    private static final String P_RSA_UNION_ID = "rsaUnionId";

    @Override
    protected void initOverAll() {
        Intent intent = getIntent();
        mCachedParam.put(P_TITLE, intent.getStringExtra(P_TITLE));
        mCachedParam.put(P_LOAD_URL, intent.getStringExtra(P_LOAD_URL));
        mCachedParam.put(P_USE_TITLE_BAR, intent.getBooleanExtra(P_USE_TITLE_BAR, true));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_browser;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_browser);
    }

    @Override
    protected void initWhenCallOnCreate() {
        progressBar = findViewById(R.id.progressBar);
        textView_title = findViewById(R.id.tv_title);
        view_titleBar = findViewById(R.id.real_view_title_bar);
        webView = findViewById(R.id.wv);

        ImageButton imageButton_right_click = findViewById(R.id.ibtn_right_click);
        String title = (String) mCachedParam.get(P_TITLE);
        if (mResources.getString(R.string.rank).equals(title)) {
            //当前是排行榜
            imageButton_right_click.setImageResource(R.drawable.icon_share_out);
            imageButton_right_click.setOnClickListener(this);
            imageButton_right_click.setVisibility(View.VISIBLE);
        } else
            imageButton_right_click.setVisibility(View.GONE);
        IndeterminateHorizontalProgressDrawable indeterminateProgressDrawable = new IndeterminateHorizontalProgressDrawable(getApplicationContext());
        indeterminateProgressDrawable.setTint(mResources.getColor(R.color.main_progress));
        indeterminateProgressDrawable.setUseIntrinsicPadding(false);
        progressBar.setIndeterminateDrawable(indeterminateProgressDrawable);
        callSetTitleBar((boolean) mCachedParam.get(P_USE_TITLE_BAR), (String) mCachedParam.get(P_TITLE));

        initPresenter();

        attachWebSetter();

        processInit();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected boolean isUseDefaultStatusBarBackground() {
        return false;
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {
        View child = findViewById(R.id.view_status_bar);
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        lp.height = mStatusBarHeight;
        child.setLayoutParams(lp);
        child.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                onBackPressed();
                break;
            case R.id.ibtn_right_click:
                mWebEventPresenter.shareRank();
                break;
        }
    }

    public static void launch(Activity activity, String title, String loadUrl) {
        launch(activity, title, true, loadUrl);
    }

    public static void launch(Activity activity, String title, boolean useTitleBar, String loadUrl) {
        launch(activity, title, useTitleBar, loadUrl, 0);
    }

    public static void launch(Activity activity, String title, boolean useTitleBar, String loadUrl, int requestCode) {
        Intent intent = new Intent(activity, com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity.class);
        if (title != null)
            intent.putExtra(P_TITLE, title);
        intent.putExtra(P_USE_TITLE_BAR, useTitleBar);
        if (loadUrl != null)
            intent.putExtra(P_LOAD_URL, loadUrl);
        if (requestCode <= 0)
            activity.startActivity(intent);
        else
            activity.startActivityForResult(intent, requestCode);
    }


    @Override
    public void callUpdateProgress(int newProgress) {
    }

    @Override
    public void callLoadNewUrl(String url) {
        webView.loadUrl(mUrlPresenter.getWrappedUrl(url));
    }

    @Override
    public void callSetTitleBar(boolean enable, String title) {
        if (enable) {
            textView_title.setText((TextUtils.isEmpty(title) ? mResources.getString(R.string.app_name) : title));
            view_titleBar.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.view_status_bar).setVisibility(View.VISIBLE);
            view_titleBar.setVisibility(View.GONE);
        }
    }

    @Override
    public String getRsaUnionId() {
        String rsaUnionId = (String) mCachedParam.get(P_RSA_UNION_ID);
        if (rsaUnionId == null) {
            rsaUnionId = RPCConnectManager.getInstance().doRPCRequireSync(RPCConst.RPCProcessId.ID_MAIN, RPCConst.ACTION_NAME_FAST_GET_SIMPLE_DATA, RPCConst.DATA_NAME_RSA_UNION_ID);
            mCachedParam.put(P_RSA_UNION_ID, rsaUnionId);
        }
        return rsaUnionId;
    }


    @Override
    public void callShowToastMessage(String msg, int id) {
        if (id > 0)
            ToastUtil.showToast(getApplication(), id);
        else if (!TextUtils.isEmpty(msg))
            ToastUtil.showToast(getApplication(), msg);
    }


    @Override
    protected void onActivityDeadForApp() {
        destroyRPCConnector();
        mWebSetter.destroyWebView(webView);
        mWebEventPresenter.onDestroy();
        //销毁当前进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private void initPresenter() {
        mUrlPresenter = new BZWUrlPresenter(new BZWUrlPresenter.IURLEventListener() {
            @Override
            public String getRsaUnionId() {
                return RemoteBrowserActivity.this.getRsaUnionId();
            }
        });

        mWebEventPresenter = new BZWWebEventPresenter(mUrlPresenter, this);
    }

    private void processInit() {
        AppGlobalManager.getInstance().prepareDelayInitData(AppGlobalManager.DelayInitTask.TYPE_BROWSER_PROCESS_INIT, new AppGlobalManager.IAppDelayInitTaskListener() {
            @Override
            public void onApplicationDelayInitComplete() {
                buildRPCConnector();
            }
        });
    }

    private void buildRPCConnector() {
        RPCConnectManager.getInstance().connect(this, RPCConst.RPCProcessId.ID_BROWSER, new IRPCConnectionHandler() {
            @Override
            public void onRPCRouterServiceConnected() {
                if (!mHasBindAppProcessBefore)
                    callLoadNewUrl((String) mCachedParam.get(P_LOAD_URL));
                mHasBindAppProcessBefore = true;
            }

            @Override
            public void onRPCRouterServiceDisConnected() {

            }

            @Override
            public boolean shouldRPCEventCostByUIThread(String requireId, String action) {
                return false;
            }

            @Override
            public String onRPCConnectionAction(RPCConnectWrapper.RPCRequireHolder holder) {
                return null;
            }
        });
    }

    private void destroyRPCConnector() {
        RPCConnectManager.getInstance().disConnect(this);
    }

    private void attachWebSetter() {
        mWebSetter.setWebView(webView, getCacheDir().getAbsolutePath() + "/webViewCache", new JsInterfaceObj(new JsInterfaceObj.IJsCallBack() {
            @Override
            public void popBack() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                });
            }

            @Override
            public void emailImport() {
            }

            @Override
            public void openCandidateDetail(String candidateId) {
                ResumeDetailActivity.launch(RemoteBrowserActivity.this, new IResumeDetailView.IntentParam(candidateId));
            }

            @Override
            public void share(String url, int type, String data) {
                mWebEventPresenter.share(url, type, data);
            }

            @Override
            public void jumpListMatch() {
            }

            @Override
            public void scanEnableUpdateResumes() {
                ResumeEnableUpdateListActivity.launch(RemoteBrowserActivity.this);
            }

            @Override
            public void openResumeUpdateList() {
                ResumeUpdatedRecordsActivity.launch(RemoteBrowserActivity.this);
            }

            @Override
            public void onJsCall(int eventId, String jsonData) {
                mWebEventPresenter.onJsCall(eventId, jsonData);
            }
        }), new WebSetter.IWebClientListener() {
            @Override
            public void onUpdateLoadProgressEvent(int progress) {

            }

            @Override
            public void onLoadCompleteEvent() {
                if (progressBar.getVisibility() == View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStartEvent() {
                if (progressBar.getVisibility() != View.VISIBLE)
                    progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinishedEvent() {
                if (progressBar.getVisibility() == View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onNewUrlEvent(String newUrl) {
                callLoadNewUrl(newUrl);
            }
        });
    }

}
