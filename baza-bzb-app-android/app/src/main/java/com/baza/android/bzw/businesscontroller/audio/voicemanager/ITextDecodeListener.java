package com.baza.android.bzw.businesscontroller.audio.voicemanager;

/**
 * Created by Vincent.Lei on 2017/2/16.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public interface ITextDecodeListener {
    int ERROR_CODE_NONE = 0;
    int ERROR_CODE_VOICE_NOT_EXIST = 1;
    int ERROR_CODE_FAILED = 3;
    int ERROR_CODE_BASE64_VOICE_FILE_FAILED = 4;

    void onDecodeToTextResult(int errorCode, String result);
}
