package com.baza.android.bzw.businesscontroller.publish;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.im.BZWIMMessage;
import com.baza.android.bzw.businesscontroller.message.ChatActivity;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.IMManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.slib.permission.PermissionsResultAction;
import com.slib.storage.file.FileManager;
import com.slib.storage.file.StorageType;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Vincent.Lei on 2017/6/5.
 * Title：中间透明转发UI
 * Note：
 */

public class ForwardingActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_forwarding);
    }

    @Override
    protected boolean isStatusBarTintEnabledWhenSDKReachKITKAT() {
        return false;
    }

    @Override
    protected void initWhenCallOnCreate() {
        parseIntent();
        finish();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            Uri uri = getResumeImportUri(intent);
            if (uri != null) {
                openResumeImport(uri);
                return;
            }
            BZWIMMessage bzwimMessage = IMManager.getInstance(this).parseNotifyMessage(intent);
            if (bzwimMessage != null) {
                openIMMessage(bzwimMessage);
                return;
            }
            finish();
        }
    }

    private Uri getResumeImportUri(Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            Uri uri = null;
            if (Intent.ACTION_VIEW.equals(action))
                uri = getIntent().getData();
            else if (Intent.ACTION_SEND.equals(action))
                uri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
            if (uri == null || uri.getPath() == null) {
                return null;
            }
            return uri;
        }
        return null;
    }

    private void openResumeImport(final Uri uri) {
        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, null, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                final String path = getRealFilePath(ForwardingActivity.this, uri);
                LogUtil.d("path  =" + path);
                if (UserInfoManager.getInstance().isAppOpened()) {
                    //已经打开应用
                    ResumeImportActivity.launch(ForwardingActivity.this, path);
                } else {
                    mApplication.cacheTransformData(CommonConst.STR_TRANSFORM_RESUME_PATH, path);
                    //未打开应用
                    LauncherActivity.launch(ForwardingActivity.this);
                }
                finish();
            }

            @Override
            public void onDenied(String permission) {
                finish();
            }
        });

    }

    private void openIMMessage(BZWIMMessage bzwimMessage) {
        if (!UserInfoManager.getInstance().isAppOpened()) {
            //应用未打开，先打开应用,主页检查是否是点击消息状态栏
            mApplication.cacheTransformData(CommonConst.STR_TRANSFORM_IM_KEY, bzwimMessage);
            LauncherActivity.launch(this);

        } else if (UserInfoManager.getInstance().isUserSignIn()) {
            //已经登录直接打开聊天界面
            ChatActivity.launch(this, new ChatActivity.ChatParam(bzwimMessage.getSessionId(), bzwimMessage.getSessionType()));
        }
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String str = uri.toString();
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.SIZE, MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (cursor == null)
                return null;
            long fileSize = 0;
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE);
                if (index > -1) {
                    fileSize = cursor.getLong(index);
                }
                index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                if (index > -1) {
                    data = cursor.getString(index);
                }
            }
            cursor.close();
            if (data != null)
                return data;
            if (fileSize > (5 * 1024 * 1024))
                return null;
            int index = str.lastIndexOf("/");
            if (index <= 0)
                return null;
            data = writeFileProviderToLocal(context, uri, str.substring(index));
        }
        return data;
    }

    private static String writeFileProviderToLocal(Context context, Uri uri, String fileName) {
        ParcelFileDescriptor inputPFD;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        String path = null;
        try {
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //获取文件句柄
        try {
            inputPFD = context.getContentResolver().openFileDescriptor(uri, "r");
            if (inputPFD != null) {
                FileDescriptor fd = inputPFD.getFileDescriptor();
                if (fd != null) {
                    fis = new FileInputStream(fd);
                    File file = FileManager.getFile(fileName, StorageType.TYPE_CACHE, true);
                    if (file.exists()) {
                        fos = new FileOutputStream(file);
                        byte[] buff = new byte[2048];
                        int len;
                        while ((len = fis.read(buff)) != -1) {
                            fos.write(buff, 0, len);
                        }
                        fos.flush();
                        path = file.getAbsolutePath();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return path;
    }
}
