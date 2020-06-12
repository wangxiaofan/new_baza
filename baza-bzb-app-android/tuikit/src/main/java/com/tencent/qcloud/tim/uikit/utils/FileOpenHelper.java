package com.tencent.qcloud.tim.uikit.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.HashMap;

/**
 * Created by LW on 2016/icon_collect/12.
 * Title :
 * Note :
 */
public class FileOpenHelper {


    private static HashMap<String, String> mine_mapTable = new HashMap<>();

    static {
        mine_mapTable.put(".apk", "application/vnd.android.package-archive");
        mine_mapTable.put(".doc", "application/msword");
        mine_mapTable.put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mine_mapTable.put(".xml", "text/plain");
        mine_mapTable.put(".zip", "application/x-zip-compressed");
        mine_mapTable.put(".pdf", "application/pdf");
        mine_mapTable.put(".ppt", "application/vnd.ms-powerpoint");
        mine_mapTable.put(".jpeg", "image/jpeg");
        mine_mapTable.put(".jpg", "image/jpeg");
        mine_mapTable.put(".png", "image/png");
        mine_mapTable.put(".mp4", "video/mp4");
        mine_mapTable.put(".3gp", "video/3gp");
        mine_mapTable.put(".mp3", "audio/x-mpeg");
        mine_mapTable.put(".conf", "text/plain");
        mine_mapTable.put(".txt", "text/plain");
        mine_mapTable.put(".wav", "audio/x-wav");
        mine_mapTable.put(".wma", "audio/x-ms-wma");
        mine_mapTable.put(".wmv", "audio/x-ms-wmv");
        mine_mapTable.put(".htm", "text/html");
        mine_mapTable.put(".html", "text/html");
    }

    //    private static final String[][] mine_mapTable = {
//            //{后缀名，MIME类型}
////            {".3gp", "video/3gpp"},
//            {".apk", "application/vnd.android.package-archive"},
////            {".asf", "video/x-ms-asf"},
////            {".avi", "video/x-msvideo"},
////            {".bin", "application/octet-stream"},
////            {".bmp", "image/bmp"},
////            {".c", "text/plain"},
////            {".class", "application/octet-stream"},
////            {".conf", "text/plain"},
////            {".cpp", "text/plain"},
//            {".doc", "application/msword"},
//            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
////            {".xls", "application/vnd.ms-excel"},
////            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
////            {".exe", "application/octet-stream"},
////            {".gif", "image/gif"},
////            {".gtar", "application/x-gtar"},
////            {".gz", "application/x-gzip"},
////            {".h", "text/plain"},
////            {".htm", "text/html"},
////            {".html", "text/html"},
////            {".jar", "application/java-archive"},
////            {".java", "text/plain"},
////            {".jpeg", "image/jpeg"},
////            {".jpg", "image/jpeg"},
////            {".js", "application/x-javascript"},
////            {".log", "text/plain"},
////            {".m3u", "audio/x-mpegurl"},
////            {".m4a", "audio/mp4a-latm"},
////            {".m4b", "audio/mp4a-latm"},
////            {".m4p", "audio/mp4a-latm"},
////            {".m4u", "video/vnd.mpegurl"},
////            {".m4v", "video/x-m4v"},
////            {".mov", "video/quicktime"},
////            {".mp2", "audio/x-mpeg"},
////            {".mp3", "audio/x-mpeg"},
////            {".mp4", "video/mp4"},
////            {".mpc", "application/vnd.mpohun.certificate"},
////            {".mpe", "video/mpeg"},
////            {".mpeg", "video/mpeg"},
////            {".mpg", "video/mpeg"},
////            {".mpg4", "video/mp4"},
////            {".mpga", "audio/mpeg"},
////            {".msg", "application/vnd.ms-outlook"},
////            {".ogg", "audio/ogg"},
////            {".pdf", "application/pdf"},
////            {".png", "image/png"},
////            {".pps", "application/vnd.ms-powerpoint"},
////            {".ppt", "application/vnd.ms-powerpoint"},
////            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
////            {".prop", "text/plain"},
////            {".rc", "text/plain"},
////            {".rmvb", "audio/x-pn-realaudio"},
////            {".rtf", "application/rtf"},
////            {".sh", "text/plain"},
////            {".tar", "application/x-tar"},
////            {".tgz", "application/x-compressed"},
////            {".txt", "text/plain"},
////            {".wav", "audio/x-wav"},
////            {".wma", "audio/x-ms-wma"},
////            {".wmv", "audio/x-ms-wmv"},
//            {".wps", "application/vnd.ms-works"},
//            {".xml", "text/plain"},
////            {".z", "application/x-compress"},
//            {".zip", "application/x-zip-compressed"},
//            {"", "*/*"}
//    };
    public static String getMIMEType(String fileName) {
        String type = "text/html";
        if (TextUtils.isEmpty(fileName))
            return type;
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名*/
        String end = fileName.substring(dotIndex, fileName.length()).toLowerCase();
        if (TextUtils.isEmpty(end))
            return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        String find = mine_mapTable.get(end);

        return find == null ? type : find;
    }

    private static String getMIMEType(File file) {
        return getMIMEType(file.getName());
    }

    public static void openFile(Context context, File file) {
        openFile(context, file, null);
    }

    public static void openFile(Context context, File file, String mineType) {
        if (TextUtils.isEmpty(mineType))
            mineType = getMIMEType(file);
        Log.e("herb", "file.getAbsolutePath>>" + file.getAbsolutePath());
        Log.e("herb", "file.mineType>>" + mineType);

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, mineType);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("herb", ">>打开文件错误>>" + e.getMessage());
            ToastUtil.toastShortMessage("打开文件错误");
        }
    }

    public static void openFile(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists())
            return;
        openFile(context, file);
    }
}
