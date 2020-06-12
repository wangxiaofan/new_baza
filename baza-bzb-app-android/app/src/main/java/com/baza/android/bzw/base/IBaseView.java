package com.baza.android.bzw.base;

import android.content.res.Resources;

import com.baza.android.bzw.application.BZWApplication;


/**
 * Created by Vincent.Lei on 2017/5/9.
 * Title：MVP View顶级接口
 * Note：
 */

public interface IBaseView {
    BaseActivity callGetBindActivity();

    Resources callGetResources();

    BZWApplication callGetApplication();

    void callShowProgress(String msg, boolean cancelable);

    void callCancelProgress();

    void callShowToastMessage(String msg, int id);
}
