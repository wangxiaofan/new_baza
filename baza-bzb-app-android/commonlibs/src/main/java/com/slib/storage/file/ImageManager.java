package com.slib.storage.file;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.text.TextUtils;
import android.webkit.WebView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2016/11/7.
 * Title :
 * Note : 图片处理
 */

public class ImageManager {

    private static final int DEFAULT_WH = 400;
    //大小为kb
    public static final int MAX_IMAGE_SIZE = 100;

    public interface ICompressImageFileListener {
        void completeCompressTask(String compressedFilePath);
    }

    public interface ICompressImageFileListListener {
        void completeListCompressTask(ArrayList<String> compressedFilePathList);
    }


    public static void compressImageFileAsync(Context context, String filePath, ICompressImageFileListener listener) {
        compressImageFileAsync(context, filePath, 0, 0, 0, listener);
    }

    public static void compressImageFileAsync(Context context, String filePath, int reqWidth, int reqHeight, int maxKb, ICompressImageFileListener listener) {
        new CompressImageFileTask(context, listener, filePath, reqWidth, reqHeight, (maxKb <= 0 ? MAX_IMAGE_SIZE : maxKb)).execute();
    }

    public static void compressImageFileListAsync(Context context, List<String> filePathList, ICompressImageFileListListener listener) {
        compressImageFileListAsync(context, filePathList, 0, 0, 0, listener);
    }

    public static void compressImageFileListAsync(Context context, List<String> filePathList, int reqWidth, int reqHeight, int maxKb, ICompressImageFileListListener listener) {
        new CompressImageFileTask(context, listener, filePathList, reqWidth, reqHeight, (maxKb <= 0 ? MAX_IMAGE_SIZE : maxKb)).execute();
    }

    /**
     * 计算图片尺寸应该缩小的inSampleSize
     */
    public static int calculateSampleSize(int width, int height, int reqWidth, int reqHeight) {
        // can't proceed
        if (width <= 0 || height <= 0)
            return 1;
        if (reqWidth <= 0 && reqHeight <= 0)
            return 1;

        if (reqWidth <= 0)
            reqWidth = (int) (width * reqHeight / (float) height + 0.5f);
        else if (reqHeight <= 0)
            reqHeight = (int) (height * reqWidth / (float) width + 0.5f);

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            if (inSampleSize == 0) {
                inSampleSize = 1;
            }
            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).
            final float totalPixels = width * height;
            // Anything more than 2x the requested pixels we'll sample down
            // further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeBitmap(String filePath, int reqWidth, int reqHeight) {
        if (TextUtils.isEmpty(filePath))
            return null;

        if (reqWidth <= 0 || reqHeight <= 0)
            reqWidth = reqHeight = DEFAULT_WH;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int[] getImageWH(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int[] wh = {options.outWidth, options.outHeight};
        return wh;
    }

    public static int[] getImageWH(Resources res, int resourceId) {
        if (resourceId <= 0)
            return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resourceId, options);
        int[] wh = {options.outWidth, options.outHeight};
        return wh;
    }


    public static byte[] bitmapToByteArray(Bitmap bitmap, int maxKb, boolean recycleOld) {
        if (bitmap == null)
            return null;
        maxKb *= 1024;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        byte[] buff = byteArrayOutputStream.toByteArray();
        if (maxKb > 0) {
            while (buff.length > maxKb) {
                byteArrayOutputStream.reset();
                quality -= 5;
                if (quality <= 0)
                    break;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
                buff = byteArrayOutputStream.toByteArray();
            }
        }
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (recycleOld)
            bitmap.recycle();
        return buff;
    }

    public static String saveBitmapToLocal(Bitmap bitmap, String filePath, boolean isRecycleSource) {
        if (bitmap == null)
            return null;
        File file = new File(filePath);
        if (!file.exists())
            try {
                if (!file.createNewFile())
                    return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        if (isRecycleSource)
            bitmap.recycle();
        return file.getAbsolutePath();
    }

    public static String saveBitmapByteArrayToLocal(byte[] bitmapByteArray, String filePath) {
        if (bitmapByteArray == null || bitmapByteArray.length == 0 || filePath == null)
            return null;
        File file = new File(filePath);
        if (!file.exists())
            try {
                if (!file.createNewFile())
                    return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bitmapByteArray, 0, bitmapByteArray.length);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            if (file.exists())
                file.delete();
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return file.getAbsolutePath();
    }

    /**
     * 截取webView快照(webView加载的整个内容的大小)
     *
     * @param webView
     * @return
     */
    public static Bitmap captureWebView(WebView webView) {
        Picture snapShot = webView.capturePicture();
        Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(), snapShot.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bmp);
        snapShot.draw(canvas);
        return bmp;
    }
}
