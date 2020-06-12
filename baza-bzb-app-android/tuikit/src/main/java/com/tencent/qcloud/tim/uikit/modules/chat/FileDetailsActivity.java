package com.tencent.qcloud.tim.uikit.modules.chat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.utils.FileOpenHelper;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import java.io.File;

public class FileDetailsActivity extends Activity {

    String name, size, path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_details);

        name = getIntent().getStringExtra("name");
        size = getIntent().getStringExtra("size");
        path = getIntent().getStringExtra("path");

        findViewById(R.id.page_title_left_text).setVisibility(View.VISIBLE);
        findViewById(R.id.page_title_right_icon).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.page_title_left_text)).setText(name);
        ((TextView) findViewById(R.id.file_name_tv)).setText(name);
        ((TextView) findViewById(R.id.file_size_tv)).setText(size);

        findViewById(R.id.file_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发送Intent跳转到其他应用打开

                FileOpenHelper.openFile(FileDetailsActivity.this, new File(path, name));
                Log.e("herb", "文件路径>path>" + path);
                Log.e("herb", "文件路径>name>" + name);
                Log.e("herb", "文件路径>>" + getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));

                //openFileByPath(path + "/" + name);
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setDataAndType(Uri.fromFile(new File(path)), "*/*");
//                startActivity(intent);
            }
        });
    }


    /**
     * 根据路径打开文件
     *
     * @param path 文件路径
     */
    public void openFileByPath(String path) {
        if (path == null)
            return;
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //文件的类型
        String type = "";
        for (int i = 0; i < MATCH_ARRAY.length; i++) {
            //判断文件的格式
            if (path.toString().contains(MATCH_ARRAY[i][0].toString())) {
                type = MATCH_ARRAY[i][1];
                break;
            }
        }
        try {
            //设置intent的data和Type属性
            intent.setDataAndType(Uri.fromFile(new File(path)), type);
            //跳转
            startActivity(intent);
        } catch (Exception e) { //当系统没有携带文件打开软件，提示
            Log.e("herb", "打开文件异常>>" + e.getMessage());
            ToastUtil.toastShortMessage("无法打开该格式文件!");
            e.printStackTrace();
        }
    }

    //建立一个文件类型与文件后缀名的匹配表
    private static final String[][] MATCH_ARRAY = {
            //{后缀名，    文件类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };
}
