package com.baza.android.bzw.constant;

import com.bznet.android.rcbox.BuildConfig;

/**
 * Created by Vincent.Lei on 2017/10/13.
 * Title：
 * Note：
 */

public class ConfigConst {
    private ConfigConst() {
    }

    public static final int INT_LOG_LEVEL = BuildConfig.LOG_LEVEL;
    //是否处理崩溃信息
    public static final boolean IS_PROCESS_CRASH_MSG = BuildConfig.IS_PROCESS_CRASH_MSG;
    public static final boolean IS_DEBUG_BUILD = BuildConfig.IS_DEBUG_BUILD;

    //网易云信
    public static final String NIM_KEY = BuildConfig.NIM_KEY;
    //QQ平台
    public static final String QQ_APP_ID = "1105731521";
    //微信平台
    public static final String WECHAT_APP_ID = "wx887c997a3533786a";
    //百度语音
//        public static final String BD_APP_ID = "8856771";
    public static final String BD_API_KEY = "VcEFukMTdPNO67svYIYbRd72zmc13Tfp";
    public static final String BD_SECRET_KEY = "M3cN3KsvKSe5kYsGHU6lbMa4tiGLO5w3";
    //数据文件
//    public static final String API_CTR_ASSET_NAME = "api.rchezi.com.crt";
//    public static final String USER_AGREEMENT_LINK = "file:///android_asset/rcbox_user_agreement.html";
    public static final String USER_AGREEMENT_LINK = "http://dl.rchezi.com/user-agreement/user_agreement.html";
    public static final String USER_PRIVATE_LINK = "https://dl.rchezi.com/user-agreement/privacy.html";
    public static final String USER_RC_INDEX = "https://www.bazhua.me";
    //数据库
    public static final int DB_VERSION = 51;
    public static final String DB_NAME = "bzw_app.db";
    public static final String API_PUBLIC_SIGN_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCp3lUHHn9uRnfO2bcQTt+Wltm1++kWJUHw5zA3\n" +
            "HN0ulRHg4Xu4hRyIckD0HZ+y3JCu3VW7LH/FfV207o/XIe8dbHkJkfHeoJDOMS36QVmAlc730p1n\n" +
            "+phCwtdkT8XIrV7yvm2styjodpW9Nkn8OMxSV9kg/5UQ3cg2BKmRTWMYIQIDAQAB";
}
