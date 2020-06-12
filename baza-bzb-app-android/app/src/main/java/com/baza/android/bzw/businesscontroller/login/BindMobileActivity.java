package com.baza.android.bzw.businesscontroller.login;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.user.LoginResultBean;
import com.baza.android.bzw.businesscontroller.login.presenter.BindMobilePresenter;
import com.baza.android.bzw.businesscontroller.login.viewinterface.IBindMobileView;
import com.bznet.android.rcbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/8/7.
 * Title：验证信息
 * Note：
 */

public class BindMobileActivity extends BaseActivity implements IBindMobileView, View.OnClickListener {
    @BindView(R.id.et_username)
    EditText editText_mobile;
    @BindView(R.id.et_sms_code)
    EditText editText_smsCode;
    @BindView(R.id.btn_get_sms_code)
    Button button_getSmsCode;
    @BindView(R.id.tv_title)
    TextView textView_title;

    private BindMobilePresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bind_mobile;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_bind_mobile);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        textView_title.setText(R.string.title_bind_mobile);
        mPresenter = new BindMobilePresenter(this, getIntent());
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
            case R.id.btn_get_sms_code:
                mPresenter.getSmSCode();
                break;
            case R.id.btn_do_check:
                mPresenter.bindMobile();
                break;
        }
    }

    @Override
    public void callUpdateSendSmsCodeTextViewStatus(int amountSeconds) {
        if (amountSeconds >= 0) {
            button_getSmsCode.setText(getString(R.string.send_after_seconds_ith_value, String.valueOf(amountSeconds)));
            if (button_getSmsCode.isClickable()) {
                button_getSmsCode.setClickable(false);
                button_getSmsCode.setTextColor(mResources.getColor(R.color.text_color_grey_9E9E9E));
            }

        } else {
            button_getSmsCode.setText(R.string.get_sms_code);
            button_getSmsCode.setClickable(true);
            button_getSmsCode.setTextColor(mResources.getColor(R.color.text_color_blue_53ABD5));
        }
    }

    @Override
    public String callGetUserName() {
        return editText_mobile.getText().toString().trim();
    }

    @Override
    public String callGetSmsCode() {
        return editText_smsCode.getText().toString().trim();
    }

    @Override
    public void callSetBack(LoginResultBean loginResultBean) {
        Intent intent = new Intent();
        intent.putExtra("loginResult", loginResultBean);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityDeadForApp() {
        if (mPresenter != null)
            mPresenter.onDestroy();
    }

    public static void launch(Activity activity, String unionid, int requestCode) {
        Intent intent = new Intent(activity, BindMobileActivity.class);
        intent.putExtra("unionid", unionid);
        activity.startActivityForResult(intent, requestCode);
    }
}
