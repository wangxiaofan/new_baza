package com.baza.android.bzw.businesscontroller.publish;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.publish.presenter.LauncherPresenter;
import com.baza.android.bzw.businesscontroller.publish.viewinterface.ILauncherView;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.slib.utils.DialogUtil;
import com.bznet.android.rcbox.R;

import baza.dialog.simpledialog.MaterialDialog;

/**
 * Created by Vincent.Lei on 2017/5/8.
 * Title：启动页
 * Note：
 */

public class LauncherActivity extends BaseActivity implements ILauncherView, OnClickListener {
    private LauncherPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_launcher);
    }

    @Override
    protected void initOverAll() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { // lower api
                View v = this.getWindow().getDecorView();
                v.setSystemUiVisibility(View.GONE);
            } else {
                //for new api versions.
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                getWindow().setAttributes(params);
            }
        }
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new LauncherPresenter(this, getIntent());
        mPresenter.initialize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodeConst.INT_REQUEST_SYETEM_ALERT)
            mPresenter.onPermissionGranted();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    protected boolean isStatusBarTintEnabledWhenSDKReachKITKAT() {
        return true;
    }

    @Override
    protected boolean isUseDefaultStatusBarBackground() {
        return false;
    }

    @Override
    public void callShowStoragePermissionDenied() {
        MaterialDialog materialDialog = DialogUtil.singleButtonShow(this, 0, R.string.require_write_storage_denied, null);
        materialDialog.setCancelable(true);
        materialDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, LauncherActivity.class);
        activity.startActivity(intent);
    }
}
