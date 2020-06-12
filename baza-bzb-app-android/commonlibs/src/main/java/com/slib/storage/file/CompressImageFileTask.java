package com.slib.storage.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2016/11/7.
 * Title : 异步图片压缩任务
 * Note :
 */

public class CompressImageFileTask extends AsyncTask<Void, Void, Void> {

    private ImageManager.ICompressImageFileListener listener;
    private ImageManager.ICompressImageFileListListener listListener;
    private String shouldCompressedFilePath;
    private List<String> shouldCompressedFilePathList;
    private int reqWidth, reqHeight, maxKb;

    private String destFilePath;
    private ArrayList<String> destFilePathList;

    public CompressImageFileTask(Context context, ImageManager.ICompressImageFileListener listener, String shouldCompressedFilePath, int reqWidth, int reqHeight, int maxKb) {
        this.listener = listener;
        this.shouldCompressedFilePath = shouldCompressedFilePath;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
        this.maxKb = maxKb;
    }

    public CompressImageFileTask(Context context, ImageManager.ICompressImageFileListListener listListener, List<String> shouldCompressedFilePathList, int reqWidth, int reqHeight, int maxKb) {
        this.listListener = listListener;
        this.shouldCompressedFilePathList = shouldCompressedFilePathList;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
        this.maxKb = maxKb;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (shouldCompressedFilePath != null)
            destFilePath = doCompressTask(shouldCompressedFilePath);
        else {
            destFilePathList = new ArrayList<>(shouldCompressedFilePathList.size());
            for (int i = 0, j = shouldCompressedFilePathList.size(); i < j; i++) {
                String path = doCompressTask(shouldCompressedFilePathList.get(i));
                if (path != null)
                    destFilePathList.add(path);
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (shouldCompressedFilePath != null && listener != null)
            listener.completeCompressTask(destFilePath);
        else if (shouldCompressedFilePathList != null && listListener != null)
            listListener.completeListCompressTask(destFilePathList);
    }

    private String doCompressTask(String targetFile) {
        Bitmap bitmap = ImageManager.decodeBitmap(targetFile, reqWidth, reqHeight);
        if (bitmap == null)
            return null;
        byte[] buff = ImageManager.bitmapToByteArray(bitmap, maxKb, true);
        File file = FileManager.getFile(String.valueOf(System.currentTimeMillis()), StorageType.TYPE_CACHE, true);
        return ImageManager.saveBitmapByteArrayToLocal(buff, file.getAbsolutePath());
    }
}
