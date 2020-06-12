package com.baza.android.bzw.manager;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebView;

import com.alibaba.wireless.security.jaq.JAQException;
import com.alibaba.wireless.security.jaq.SecurityInit;
import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.push.PushBean;
import com.baza.android.bzw.businesscontroller.appprocessservice.AppProcessService;
import com.baza.android.bzw.businesscontroller.im.BZWIMMessage;
import com.baza.android.bzw.businesscontroller.im.IMUserInfoProvider;
import com.baza.android.bzw.businesscontroller.login.LoginActivity;
import com.baza.android.bzw.businesscontroller.message.ChatActivity;
import com.baza.android.bzw.businesscontroller.publish.ResumeImportActivity;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.ConfigConst;
import com.baza.android.bzw.constant.SharedPreferenceConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.dao.AccountDao;
import com.baza.android.bzw.dao.AdvertisementDao;
import com.baza.android.bzw.dao.AttachmentRemarkDao;
import com.baza.android.bzw.extra.CustomerHttpRequestUtil;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.log.ReportAgent;
import com.bznet.android.rcbox.R;
import com.slib.http.HttpConfig;
import com.slib.permission.PermissionsResultAction;
import com.slib.storage.database.DBWorker;
import com.slib.storage.file.FileManager;
import com.slib.storage.sharedpreference.SharedPreferenceManager;
import com.slib.utils.AppUtil;
import com.slib.utils.DialogUtil;
import com.slib.utils.LoadImageUtil;
import com.slib.utils.ScreenUtil;

import baza.dialog.simpledialog.MaterialDialog;
import location.ILocationListener;
import location.LocationInfo;
import location.LocationManager;
import social.SocialHelper;

/**
 * Created by Vincent.Lei on 2019/7/1.
 * Title：
 * Note：
 */
public class AppGlobalManager {
    private AppGlobalManager() {
    }

    private static final String BROWSER_PROCESS_NAME = "com.bznet.android.rcbox:browser";
    private static AppGlobalManager mAppStarter = new AppGlobalManager();
    private static final long DEFAULT_DELAY_TIME = 1000;
    private AppOpenTask mAppOpenTask = new AppOpenTask();

    public static AppGlobalManager getInstance() {
        return mAppStarter;
    }

    private Application mApplication;
    private BaseActivity mMainActivity;
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public void attachApplication(Application application) {
        this.mApplication = application;
        onApplicationCreated();
    }

    public void attachMainActivity(BaseActivity mainActivity) {
        this.mMainActivity = mainActivity;
    }

    private void onApplicationCreated() {
        SharedPreferenceManager.init(mApplication, SharedPreferenceConst.SP_FILE_NAME);
        IMManager.getInstance(mApplication).init(SharedPreferenceManager.getString(SharedPreferenceConst.SP_IM_USER_NAME), SharedPreferenceConst.SP_IM_USER_TOKEN);
        String processName = AppUtil.getProcessName(mApplication);
        if (BROWSER_PROCESS_NAME.equals(processName) || inMainProcess(processName)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                WebView.setDataDirectorySuffix(processName);
            }
            ReportAgent.initScenarioType(mApplication, processName);
            ScreenUtil.init(mApplication);
            initNetworkRequest();
            LoadImageUtil.init(mApplication);
            UserInfoManager.getInstance().init(mApplication);
            DBWorker.initDataBase(mApplication, ConfigConst.DB_NAME, ConfigConst.DB_VERSION);
        }
        if (inMainProcess(processName)) {
            LogUtil.d("inMainProcess");
            ActivitiesManager.getInstance().init(mApplication);
            IMManager.getInstance(mApplication).registerCustomAttachmentParser();
            AppProcessService.start(mApplication);
            try {
                SecurityInit.Initialize(mApplication);
            } catch (JAQException e) {
                e.printStackTrace();
            }
        }
    }

    public void onMainActivityResume() {
        UserInfoManager.getInstance().setAppOpenStatus(true);
        mAppOpenTask.start();
    }

    public void onMainActivityPause() {

    }

    public void onMainActivityDestroy(BaseActivity main) {
        if (main == null || main != mMainActivity)
            return;
        UserInfoManager.getInstance().setAppOpenStatus(false);
        mAppOpenTask.stop();
        mMainActivity = null;
    }

    /**
     * 检测到 账号过期或者在其他地方登陆
     */
    public void forceLogoutForSinglePlatform() {
        if (!inMainProcess())
            return;
        final Activity from = ActivitiesManager.getInstance().finishAllExceptStackTopActivityForSinglePlatform();
        if (from == null)
            return;
        MaterialDialog materialDialog = DialogUtil.singleButtonShow(from, 0, R.string.platform_changed_you_need_reLogin, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.launch(from);
                from.finish();
                ActivitiesManager.getInstance().reset();
            }
        });
        materialDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                UserInfoManager.getInstance().clearCurrentUserInfoWhenSignOut();
            }
        });
    }

    public void forceIdentify() {
        Activity from = ActivitiesManager.getInstance().getTopStackActivity();
        UserInfoManager.getInstance().checkIdentifyStatusAndVerifyIfNeed(from, false);
    }

    public void logOut(BaseActivity currentActivity) {
        UserInfoManager.getInstance().clearCurrentUserInfoWhenSignOut();
        ActivitiesManager.getInstance().finishAllActivitiesButCurrent(currentActivity);
        LoginActivity.launch(currentActivity);
        currentActivity.finish();
    }

    /**
     * 判断是否是当前进程
     */
    boolean inMainProcess() {
        return inMainProcess(AppUtil.getProcessName(mApplication));
    }

    private boolean inMainProcess(String processName) {
        String packageName = mApplication.getPackageName();
        return packageName.equals(processName);
    }

    /**
     * 初始化网络请求
     */
    private static void initNetworkRequest() {
        HttpConfig httpConfig = new HttpConfig.Builder().trustHostName(URLConst.HOST_NAME.startsWith("https") ? URLConst.HOST_NAME : null).logEnable(ConfigConst.INT_LOG_LEVEL > 0).logTag(LogUtil.TAG).connectTimeOut(CustomerRequestAssistHandler.CONNECT_TIME_OUT).readTimeOut(CustomerRequestAssistHandler.READ_TIME_OUT).writeTimeOut(CustomerRequestAssistHandler.WRITE_TIME_OUT).requestAssistHandler(new CustomerRequestAssistHandler()).build();
        CustomerHttpRequestUtil.init(httpConfig);
    }

    /**
     * 注意该方法需在获取到存储权限时才能调用
     */
    private static void initFileSystem(Application application) {
        FileManager.initFileSystem(application, null);
    }

    /**
     * 启动后需要延迟初始化的任务启动
     * 只能有一个地方调用  建议在启动页调用
     */

    public void prepareDelayInitData(int type, IAppDelayInitTaskListener listener) {
        new DelayInitTask(mApplication, type, listener).execute();
    }

    /*
     *延迟初始化任务
     **************************************************************************************************************************************************************************************
     */
    public interface IAppDelayInitTaskListener {
        void onApplicationDelayInitComplete();
    }

    public static class DelayInitTask extends AsyncTask<Void, Void, Void> {
        public static final int TYPE_APP_START = 0;
        public static final int TYPE_USER_LOGIN = 1;
        public static final int TYPE_BROWSER_PROCESS_INIT = 2;
        private IAppDelayInitTaskListener mDelayInitTaskListener;
        private Application mApplication;
        private int type;

        private DelayInitTask(Application application, int type, IAppDelayInitTaskListener delayInitTaskListener) {
            this.mDelayInitTaskListener = delayInitTaskListener;
            this.mApplication = application;
            this.type = type;
        }

        private DelayInitTask(Application application, IAppDelayInitTaskListener delayInitTaskListener) {
            this(application, TYPE_APP_START, delayInitTaskListener);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (type == TYPE_APP_START) {
                LogUtil.d("-------DelayInitTask(TYPE_APP_START) start --------");
                initFileSystem(mApplication);
                SocialHelper.getInstance().init(mApplication, R.drawable.icon_bzw_share_default_b).qqAppId(ConfigConst.QQ_APP_ID).weChatAppId(ConfigConst.WECHAT_APP_ID);
                AddressManager.getInstance().init(mApplication);
                IMUserInfoProvider.getInstance(mApplication).initLocalInfo();
                AttachmentRemarkDao.init();
            } else if (type == TYPE_USER_LOGIN) {
                LogUtil.d("-------DelayInitTask(TYPE_USER_LOGIN) start --------");
                IMUserInfoProvider.getInstance(mApplication).initLocalInfo();
                AttachmentRemarkDao.init();
            } else if (type == TYPE_BROWSER_PROCESS_INIT) {
                initFileSystem(mApplication);
                SocialHelper.getInstance().init(mApplication, R.drawable.icon_bzw_share_default_b).qqAppId(ConfigConst.QQ_APP_ID).weChatAppId(ConfigConst.WECHAT_APP_ID);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mDelayInitTaskListener != null)
                mDelayInitTaskListener.onApplicationDelayInitComplete();
        }
    }

    private class AppOpenTask {
        private ILocationListener mLocationListener;

        private void start() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    IMManager.getInstance(mApplication).registerLoginSyncDataStatus();
                    //检测打开消息
                    BZWIMMessage bzwimMessage = (BZWIMMessage) BZWApplication.getApplication().getCachedTransformData(CommonConst.STR_TRANSFORM_IM_KEY);
                    if (bzwimMessage != null)
                        ChatActivity.launch(mMainActivity, new ChatActivity.ChatParam(bzwimMessage.getSessionId(), bzwimMessage.getSessionType()));
                    //检测打开推送
                    PushBean pushBean = (PushBean) BZWApplication.getApplication().getCachedTransformData(CommonConst.STR_TRANSFORM_PUSH_KEY);
                    if (pushBean != null)
                        PushManager.getInstance().openPushMessage(mMainActivity, pushBean, true);
                    //检测打开文件导入
                    String path = (String) BZWApplication.getApplication().getCachedTransformData(CommonConst.STR_TRANSFORM_RESUME_PATH);
                    if (path != null)
                        ResumeImportActivity.launch(mMainActivity, path);
                }
            });
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    UserInfoManager.getInstance().updateUserInfo();
                    UserInfoManager.getInstance().updateUserIllegalWord();
                    registerLocationListener(true);
                    doLocation(true);
                }
            });
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //启动推送
                    PushManager.getInstance().startPush(mApplication);
                    //检查广告
                    AdvertisementDao.checkToDownLoadNewAdvertisement();

                }
            }, DEFAULT_DELAY_TIME * 2);
        }

        private void stop() {
            registerLocationListener(false);
            doLocation(false);
        }


        private void registerLocationListener(boolean register) {
            if (register) {
                if (mLocationListener == null)
                    mLocationListener = new ILocationListener() {
                        @Override
                        public void onReceiveLastLocation(LocationInfo locationInfo) {
                        }

                        @Override
                        public void onReceiveCurrentLocation(LocationInfo locationInfo) {
                            AccountDao.reportUserLocation(locationInfo);
                            LocationManager.getInstance(mMainActivity).stopLocation();
                        }
                    };
                LocationManager.getInstance(mMainActivity).registerLocationListener(mLocationListener, true);
            } else if (mLocationListener != null)
                LocationManager.getInstance(mMainActivity).registerLocationListener(mLocationListener, false);
        }

        private void doLocation(boolean start) {
            if (start)
                mMainActivity.requestPermission(null, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        LogUtil.d("start to get location");
                        LocationManager.getInstance(mMainActivity).startLocation();
                    }

                    @Override
                    public void onDenied(String permission) {
                    }
                });
            else
                LocationManager.getInstance(mMainActivity).stopLocation();
        }

    }
}
