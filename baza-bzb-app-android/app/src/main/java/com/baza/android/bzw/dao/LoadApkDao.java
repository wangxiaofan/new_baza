package com.baza.android.bzw.dao;

import android.app.Notification;
import android.content.Context;
import android.text.TextUtils;

import com.baza.android.bzw.manager.NFManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.slib.http.FileLoadTool;
import com.slib.http.FileOpenHelper;
import com.slib.http.IFileLoadObserve;
import com.slib.utils.ToastUtil;
import com.bznet.android.rcbox.R;

import java.io.File;


/**
 * Created by LW on 2016/9/14.
 * Title : 新版本下载
 * Note :
 */
public class LoadApkDao implements IFileLoadObserve {

    private String loadUrl;
    private Context context;
    private Notification mNotification;
    private static LoadApkDao loadApkBean;
    private boolean isOnLoadingNewApk;

    private LoadApkDao(Context context) {
        this.context = context;
        FileLoadTool.getInstance().registerFileLoadObserve(this);
    }

    private void destroy() {
        isOnLoadingNewApk = false;
        context = null;
        mNotification = null;
        loadApkBean = null;
    }

    public static LoadApkDao getInstance(Context context) {
        if (loadApkBean == null) {
            synchronized (LoadApkDao.class) {
                if (loadApkBean == null)
                    loadApkBean = new LoadApkDao(context.getApplicationContext());
            }
        }
        return loadApkBean;
    }

    public void loadNewApk(String loadUrl, int version) {
        if (TextUtils.isEmpty(loadUrl))
            return;
        isOnLoadingNewApk = true;
        this.loadUrl = loadUrl;
        FileLoadTool.getInstance().downLoadFile(loadUrl, null, "?versionId = " + version, UserInfoManager.getInstance().getDefaultPassportHeader());
    }

    public boolean isOnLoadingNewApk() {
        return isOnLoadingNewApk;
    }

    @Override
    public void onFileStartLoading(String fileUrl, String tagForSameUrl) {
        if (!fileUrl.equals(loadUrl))
            return;
        ToastUtil.showToast(context, R.string.starting_load_new_apk);
        mNotification = NFManager.buildLoadApkNotification(context);
    }

    @Override
    public void onFileLoadProgressChanged(String fileUrl, String tagForSameUrl, int progress) {
        if (fileUrl.equals(loadUrl))
            NFManager.updateLoadApkNotification(mNotification, progress);

    }

    @Override
    public void onFileLoadSuccess(String fileUrl, String tagForSameUrl, File file) {
        if (!fileUrl.equals(loadUrl))
            return;
        FileLoadTool.getInstance().unRegisterFileLoadObserve(this);
        NFManager.cancelLoadApkNotification();
        FileOpenHelper.openFile(context, file);
        destroy();
    }

    @Override
    public void onFileLoadFailed(String fileUrl, String tagForSameUrl, int errorCode, String errorMsg) {
        if (!fileUrl.equals(loadUrl))
            return;
        FileLoadTool.getInstance().unRegisterFileLoadObserve(this);
        NFManager.cancelLoadApkNotification();
        ToastUtil.showToast(context, R.string.text_failed_download);
        destroy();
    }

}
