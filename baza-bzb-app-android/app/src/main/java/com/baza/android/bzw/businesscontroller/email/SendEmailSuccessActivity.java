package com.baza.android.bzw.businesscontroller.email;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/8/2.
 * Title：邮件已发送
 * Note：
 */

public class SendEmailSuccessActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_send_email_success;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_send_email_status);
    }

    @Override
    protected void initWhenCallOnCreate() {
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.title_send_email_status);
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                setBack(RESULT_OK);
                break;
            case R.id.btn_write_another:
                setBack(RequestCodeConst.INT_RESULT_CODE_WRITE_ANOTHER_EMAIL);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setBack(RESULT_OK);
        super.onBackPressed();
    }

    private void setBack(int resultCode) {
        setResult(resultCode);
        finish();
    }

    public static void launch(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SendEmailSuccessActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }
}
