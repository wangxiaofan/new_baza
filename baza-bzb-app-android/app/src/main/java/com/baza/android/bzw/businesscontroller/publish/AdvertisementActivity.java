package com.baza.android.bzw.businesscontroller.publish;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.publish.presenter.AdvertisementPresenter;
import com.baza.android.bzw.businesscontroller.publish.viewinterface.IAdvertisementView;
import com.bznet.android.rcbox.R;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Vincent.Lei on 2017/5/16.
 * Title：
 * Note：
 */

public class AdvertisementActivity extends BaseActivity implements IAdvertisementView, View.OnClickListener {
    @BindView(R.id.gifImageView)
    GifImageView gifImageView;
    @BindView(R.id.btn_skip)
    Button button_skip;

    private AdvertisementPresenter mPresenter;
    private GifDrawable mGifDrawable;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_advertisement;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_advertisement);
    }

    @Override
    protected boolean isUseDefaultStatusBarBackground() {
        return false;
    }

    @Override
    protected boolean isStatusBarTintEnabledWhenSDKReachKITKAT() {
        return true;
    }

    @Override
    protected void initOverAll() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { // lower api
                View v = this.getWindow().getDecorView();
                v.setSystemUiVisibility(View.GONE);
            } else {
                //for new api versions.
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                getWindow().setAttributes(params);
            }
        }
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        button_skip.setOnClickListener(this);
        mPresenter = new AdvertisementPresenter(this);
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_skip:
                mPresenter.prepareToLeave();
                break;
            case R.id.gifImageView:
                mPresenter.clickAdvertisement();
                break;
        }
    }

    @Override
    public void callShowAdvertisement(File fileAdv, boolean isGif) {
        if (isGif) {
            try {
                mGifDrawable = new GifDrawable(fileAdv);
                gifImageView.setImageDrawable(mGifDrawable);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        //显示静态图
        gifImageView.setImageURI(Uri.fromFile(fileAdv));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null)
            mPresenter.onResume();
    }

    @Override
    protected void onActivityDeadForApp() {
        if (mPresenter != null)
            mPresenter.onDestroy();
        if (mGifDrawable != null) {
            mGifDrawable.stop();
            mGifDrawable.recycle();
        }
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, AdvertisementActivity.class);
        activity.startActivity(intent);
    }
}
