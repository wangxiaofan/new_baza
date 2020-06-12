package com.baza.android.bzw.businesscontroller.publish;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.publish.util.QRCodeUtil;
import com.bznet.android.rcbox.R;
import com.google.zxing.fragment.CaptureFragment;

/**
 * Created by Vincent.Lei on 2017/11/16.
 * Title：
 * Note：
 */

public class QRCodeActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_qrcode;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_qrcode);
    }

    @Override
    protected void initWhenCallOnCreate() {
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.scan_qrcode);
        CaptureFragment captureFragment = new CaptureFragment();
        captureFragment.setQRCodeResultListener(new CaptureFragment.IQRCodeResultListener() {
            @Override
            public void onQRCodeResult(String result) {
                if (!TextUtils.isEmpty(result)) {
                    Intent data = new Intent();
                    data.putExtra(QRCodeUtil.STR_SCAN_RESULT, result);
                    setResult(RESULT_OK, data);
                }
                finish();
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.fl_container, captureFragment).commit();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
        }
    }

    public static void launch(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, QRCodeActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }
}
