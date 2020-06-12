package com.baza.android.bzw.businesscontroller.publish.presenter;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.businesscontroller.publish.viewinterface.IAdvertisementView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.SharedPreferenceConst;
import com.baza.android.bzw.dao.AdvertisementDao;
import com.slib.storage.sharedpreference.SharedPreferenceManager;

import java.io.File;

/**
 * Created by Vincent.Lei on 2017/5/16.
 * Title：
 * Note：
 */

public class AdvertisementPresenter extends BasePresenter {
    private static final int DEFAULT_KEEP_TIME = 3000;
    private static final int MIN_KEEP_TIME = 800;

    private IAdvertisementView mAdvertisementView;
    private Handler mHandler = new Handler(Looper.myLooper());
    private int mPageKeepTime;
    private String mClickUrl;
    private boolean mHasPause;

    public AdvertisementPresenter(IAdvertisementView mAdvertisementView) {
        this.mAdvertisementView = mAdvertisementView;
    }

    @Override
    public void onResume() {
        if (mHasPause) {
            mHasPause = false;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    prepareToLeave();
                }
            }, 500);
        }
    }

    @Override
    public void onDestroy() {
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    @Override
    public void initialize() {
        mPageKeepTime = (int) SharedPreferenceManager.getLong(SharedPreferenceConst.SP_ADVERTISEMENT_SHOW_TIME);
        mClickUrl = SharedPreferenceManager.getString(SharedPreferenceConst.SP_ADVERTISEMENT_CLICK_URL);
        mPageKeepTime = (mPageKeepTime < MIN_KEEP_TIME ? DEFAULT_KEEP_TIME : mPageKeepTime);
        File fileAdvertisement = AdvertisementDao.getAdvertisementResource();
        if (fileAdvertisement != null)
            mAdvertisementView.callShowAdvertisement(fileAdvertisement, fileAdvertisement.getAbsolutePath().endsWith(CommonConst.STR_GIF_TAG));
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                prepareToLeave();
            }
        }, mPageKeepTime);
    }

    public void prepareToLeave() {
        mHandler.removeCallbacksAndMessages(null);
        BZWApplication.continueLaunch(mAdvertisementView.callGetBindActivity());
    }

    public void clickAdvertisement() {
        if (TextUtils.isEmpty(mClickUrl))
            return;
        mHandler.removeCallbacksAndMessages(null);
        mHasPause = true;
        RemoteBrowserActivity.launch(mAdvertisementView.callGetBindActivity(), null, false, mClickUrl);
    }
}
