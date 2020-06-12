package com.slib.storage.file;

import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;


/**
 * Created by Vincent.Lei on 2016/11/5.
 * Title : 存储跟目录设置
 * Note :
 */

public class RootStorage {
    private static String mDefineRootPath;
    private static Application mApplication;
    private static final String ROOT_PATH_IN_DATA = "rd";

    static void init(Application application, String rootPath) {
        mDefineRootPath = rootPath;
        mApplication = application;
        String mRootPath = getRootPath();
        createSubFolds(mRootPath);
    }


    static String getRootPath() {
        if (mApplication == null)
            throw new RuntimeException("please init first");
        String mRootPath = TextUtils.isEmpty(mDefineRootPath) ? (hasExtraStorage() ? (Environment.getExternalStorageDirectory().getPath() + File.separator + mApplication.getPackageName()) : (mApplication.getApplicationInfo().dataDir + File.separator + ROOT_PATH_IN_DATA)) : mDefineRootPath;
        if (!mRootPath.endsWith(File.separator))
            mRootPath = mRootPath + File.separator;
        File file = new File(mRootPath);
        if (!file.exists() && !file.mkdirs())
            Log.e("RootStorage", "can not make rootPath");
        Log.d("RootStorage", "mRootPath = " + mRootPath);
        return mRootPath;
    }

    /**
     * 该文件夹下存的是应用私有数据  只有获得root权限才可以看到
     */
    static String getPrivatePath(String pathName, boolean isCreateAuto) {
        File file = new File(mApplication.getApplicationInfo().dataDir + (pathName.startsWith(File.separator) ? pathName : (File.separator + pathName)));
        if (!file.exists()) {
            if (isCreateAuto && file.mkdirs()) {
                Log.d("createPrivatePath", file.getAbsolutePath());
                return file.getAbsolutePath();
            }
            return null;
        }
        return file.getAbsolutePath();
    }

    public static boolean hasExtraStorage() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private static void createSubFolds(String mRootPath) {
        if (TextUtils.isEmpty(mRootPath))
            return;
        createNoMediaFile(mRootPath);
        File filePath;
        for (StorageType storageType : StorageType.values()) {
            filePath = new File(mRootPath + storageType.getPath());
            if (!filePath.exists()) {
                if (filePath.mkdirs())
                    Log.d("createPath", filePath.getAbsolutePath());
            }
        }

    }

    static final String NO_MEDIA_FILE_NAME = ".nomedia";

    private static void createNoMediaFile(String path) {
        path = (path.endsWith(File.separator) ? path : (path + File.separator));
        File noMediaFile = new File(path + NO_MEDIA_FILE_NAME);
        try {
            if (!noMediaFile.exists()) {
                noMediaFile.createNewFile();
                Log.d("nomedia", noMediaFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
