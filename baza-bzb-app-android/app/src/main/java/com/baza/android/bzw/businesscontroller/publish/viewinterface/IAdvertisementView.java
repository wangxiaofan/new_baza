package com.baza.android.bzw.businesscontroller.publish.viewinterface;

import com.baza.android.bzw.base.IBaseView;

import java.io.File;

/**
 * Created by Vincent.Lei on 2017/5/16.
 * Title：
 * Note：
 */

public interface IAdvertisementView extends IBaseView {
    void callShowAdvertisement(File fileAdv,boolean isGif);
}
