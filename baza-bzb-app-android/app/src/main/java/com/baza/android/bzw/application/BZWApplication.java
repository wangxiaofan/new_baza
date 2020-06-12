package com.baza.android.bzw.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import androidx.multidex.MultiDex;
import android.view.View;

import com.baza.android.bzw.bean.user.VersionBean;
import com.baza.android.bzw.businesscontroller.home.HomeActivity;
import com.baza.android.bzw.businesscontroller.login.LoginActivity;
import com.baza.android.bzw.businesscontroller.publish.AdvertisementActivity;
import com.baza.android.bzw.businesscontroller.publish.FirstOpenAppGuideActivity;
import com.baza.android.bzw.businesscontroller.publish.LauncherActivity;
import com.baza.android.bzw.constant.SharedPreferenceConst;
import com.baza.android.bzw.dao.AdvertisementDao;
import com.baza.android.bzw.dao.CheckUpdateDao;
import com.baza.android.bzw.manager.AppGlobalManager;
import com.baza.android.bzw.manager.NFManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.slib.storage.file.FileManager;
import com.slib.storage.sharedpreference.SharedPreferenceManager;
import com.slib.utils.DialogUtil;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.config.CustomFaceConfig;
import com.tencent.qcloud.tim.uikit.config.GeneralConfig;
import com.tencent.qcloud.tim.uikit.config.TUIKitConfigs;

import java.io.File;
import java.util.HashMap;

import baza.dialog.simpledialog.MaterialDialog;

/**
 * Created by Vincent.Lei on 2017/5/8.
 * Title：全局Application
 * Note：
 */

public class BZWApplication extends Application {
    private static BZWApplication mApplication;
    private HashMap<String, Object> mCacheData = new HashMap<>();

    public static final int SDKAPPID = 1400335702; // 腾讯IM SDKAppID

    public static BZWApplication getApplication() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        AppGlobalManager.getInstance().attachApplication(this);

        //腾讯IM初始化
        // 配置 Config，请按需配置
        TUIKitConfigs configs = TUIKit.getConfigs();
        configs.setSdkConfig(new TIMSdkConfig(SDKAPPID));
        configs.setCustomFaceConfig(new CustomFaceConfig());
        configs.setGeneralConfig(new GeneralConfig());
        TUIKit.init(this, SDKAPPID, configs);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public void cacheTransformData(String transformKey, Object data) {
        mCacheData.put(transformKey, data);
    }

    public Object getCachedTransformData(String transformKey) {
        return mCacheData.remove(transformKey);
    }

    @Override
    public void onTerminate() {
        NFManager.cancelAllNotification();
        super.onTerminate();
    }



    public void showNewVersionDialog(Activity activity, VersionBean data) {
        showNewVersionDialog(activity, data, null);
    }

    public void showNewVersionDialog(Activity activity, VersionBean data, DialogInterface.OnDismissListener dismissListener) {
        Resources resources = getResources();
        String info = resources.getString(R.string.update_version_info, data.description, FileManager.formatFileSize(data.appSize));
        if (data.isNotForceUpdate()) {
            MaterialDialog materialDialog = DialogUtil.doubleButtonShow(activity, resources.getString(R.string.title_new_version), info, resources.getString(R.string.update_later), resources.getString(R.string.update_now), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckUpdateDao.getInstance().downLoadNewVersionApk();
                }
            }, null, false);
            materialDialog.setNotShowTitle(false);
            materialDialog.show();
            if (dismissListener != null)
                materialDialog.setOnDismissListener(dismissListener);
            return;
        }
        MaterialDialog materialDialog = DialogUtil.singleButtonShow(activity, resources.getString(R.string.title_new_version), info, resources.getString(R.string.update_now), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckUpdateDao.getInstance().downLoadNewVersionApk();
            }
        }, false);
        materialDialog.setNotShowTitle(false);
        materialDialog.setCancelable(false);
        materialDialog.show();
        if (dismissListener != null)
            materialDialog.setOnDismissListener(dismissListener);
    }

    public static void continueLaunch(Activity activity) {
        if ((activity instanceof LauncherActivity)) {
            File fileAdv = AdvertisementDao.getAdvertisementResource();
            if (fileAdv != null) {
                AdvertisementActivity.launch(activity);
                activity.finish();
                return;
            }
        }
        if (!SharedPreferenceManager.getBoolean(SharedPreferenceConst.SP_IS_NOY_FIRST_IN_APP)) {
            FirstOpenAppGuideActivity.launch(activity);
            activity.finish();
            return;
        }
        if (!UserInfoManager.getInstance().isUserSignIn()) {
            //未登录
            LoginActivity.launch(activity);
            activity.finish();
            return;
        }
        HomeActivity.launch(activity);
        activity.finish();
    }
}
