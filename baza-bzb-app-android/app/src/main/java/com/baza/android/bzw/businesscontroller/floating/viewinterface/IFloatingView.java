package com.baza.android.bzw.businesscontroller.floating.viewinterface;

import com.baza.android.bzw.base.IBaseView;
import com.baza.android.bzw.bean.resume.FloatingListDetailBean;

public interface IFloatingView extends IBaseView {

    void callShowLoadingView();

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callUpdateContent(FloatingListDetailBean beans);

    void callUpdatePage();

    void callShowDialog();
}
