package com.baza.android.bzw.businesscontroller.publish.presenter;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.baza.android.bzw.manager.AppGlobalManager;
import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.businesscontroller.publish.viewinterface.ILauncherView;
import com.slib.permission.PermissionsResultAction;

/**
 * Created by Vincent.Lei on 2017/5/9.
 * Title：Launcher页面逻辑控制器
 * Note：
 */

public class LauncherPresenter extends BasePresenter {
    private static final int LAUNCHER_PAGE_KEEP_TIME = 1500;
    private ILauncherView mLauncherView;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public LauncherPresenter(ILauncherView launcherView, Intent intent) {
        this.mLauncherView = launcherView;
    }

    @Override
    public void initialize() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                requestStoragePermission();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 申请存储权限
     */
    private void requestStoragePermission() {
        mLauncherView.callGetBindActivity().requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, null, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                //GIO需要申请这个权限
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (!Settings.canDrawOverlays(mLauncherView.callGetBindActivity())) {
//                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                        intent.setData(Uri.parse("package:" + mLauncherView.callGetApplication().getPackageName()));
//                        mLauncherView.callGetBindActivity().startActivityForResult(intent, RequestCodeConst.INT_REQUEST_SYETEM_ALERT);
//                        return;
//                    }
//                }
                onPermissionGranted();
            }


            @Override
            public void onDenied(String permission) {
                mLauncherView.callShowStoragePermissionDenied();
            }
        });
    }

    public void onPermissionGranted() {
        //获取到了存储权限 执行下一步
        AppGlobalManager.getInstance().prepareDelayInitData(AppGlobalManager.DelayInitTask.TYPE_APP_START, new AppGlobalManager.IAppDelayInitTaskListener() {
            @Override
            public void onApplicationDelayInitComplete() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BZWApplication.continueLaunch(mLauncherView.callGetBindActivity());
                    }
                }, LAUNCHER_PAGE_KEEP_TIME);
            }
        });
    }

}
