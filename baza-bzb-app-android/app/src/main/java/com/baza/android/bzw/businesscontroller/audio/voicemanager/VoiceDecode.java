package com.baza.android.bzw.businesscontroller.audio.voicemanager;

import android.content.Context;

import java.io.File;

/**
 * Created by Vincent.Lei on 2017/2/14.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class VoiceDecode {
    public static void decodeToText(Context mContext, File sourceFile, ITextDecodeListener textDecodeListener) {
        new BaiDuVoiceToTextHelper(mContext, sourceFile, textDecodeListener).decode();
    }


}
