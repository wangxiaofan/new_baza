package com.baza.android.bzw.dao;

import android.text.TextUtils;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.bean.common.AdvertisementBean;
import com.baza.android.bzw.bean.common.AdvertisementResultBean;
import com.baza.android.bzw.bean.common.BannerResultBean;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.SharedPreferenceConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.manager.UserInfoManager;
import com.slib.http.FileLoadTool;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;
import com.slib.storage.sharedpreference.SharedPreferenceManager;
import com.slib.utils.ScreenUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/16.
 * Title：
 * Note：
 */

public class AdvertisementDao {
    private AdvertisementDao() {
    }

    public static File getAdvertisementResource() {
        String adv_url = SharedPreferenceManager.getString(SharedPreferenceConst.SP_ADVERTISEMENT_RESOURCE_URL);
        if (!TextUtils.isEmpty(adv_url)) {
            File file = FileLoadTool.getInstance().getExistsDownLoadFileByUrl(adv_url);
            if (file != null && file.exists())
                return file;
        }
        return null;
    }

    public static void checkToDownLoadNewAdvertisement() {
        BZWApplication mApplication = BZWApplication.getApplication();
        if (mApplication == null)
            return;
        HashMap<String, String> hearer = new HashMap<>();
        hearer.put(CommonConst.STR_AGENT_INFO, UserInfoManager.getInstance().getDeviceInfo());
        String url = URLConst.URL_ADVERTISEMENT + "?clientType=android&scale=" + getScaleForScreen();
        HttpRequestUtil.doHttpGet(url, hearer, AdvertisementResultBean.class, new INetworkCallBack<AdvertisementResultBean>() {
            @Override
            public void onSuccess(AdvertisementResultBean advertisementRequestBean) {
                if (advertisementRequestBean.data != null && !advertisementRequestBean.data.isEmpty()) {
                    AdvertisementBean advertisementBean = advertisementRequestBean.data.get(0);
                    SharedPreferenceManager.saveLong(SharedPreferenceConst.SP_ADVERTISEMENT_SHOW_TIME, advertisementBean.duration);
                    SharedPreferenceManager.saveString(SharedPreferenceConst.SP_ADVERTISEMENT_RESOURCE_URL, advertisementBean.picUrl);
                    SharedPreferenceManager.saveString(SharedPreferenceConst.SP_ADVERTISEMENT_CLICK_URL, advertisementBean.linkUrl);
                    if (TextUtils.isEmpty(advertisementBean.picUrl))
                        return;
                    File file = FileLoadTool.getInstance().getExistsDownLoadFileByUrl(advertisementBean.picUrl);
                    if (file != null && file.exists())
                        //广告已经下载
                        return;
                    //下载广告
                    FileLoadTool.getInstance().downLoadFile(advertisementBean.picUrl);
                } else
                    SharedPreferenceManager.saveString(SharedPreferenceConst.SP_ADVERTISEMENT_RESOURCE_URL, null);

            }

            @Override
            public void onFailed(Object object) {

            }
        });
    }

    public static void loadBanner(final IDefaultRequestReplyListener<List<BannerResultBean.Data>> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_BANNER, null, BannerResultBean.class, new INetworkCallBack<BannerResultBean>() {
            @Override
            public void onSuccess(BannerResultBean bannerResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, bannerResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    private static int getScaleForScreen() {
        //根据屏幕尺寸返回图片 scale
        if (ScreenUtil.screenWidth < 700)
            return SCALE_480;
        if (ScreenUtil.screenWidth < 1000)
            return SCALE_720;
        return SCALE_1080;
    }

    private static final int SCALE_480 = 1;
    private static final int SCALE_720 = 2;
    private static final int SCALE_1080 = 3;
}
