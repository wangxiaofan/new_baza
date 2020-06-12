package com.baza.android.bzw.businesscontroller.account;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.account.presenter.ChangeMobilePresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IChangeMobileView;
import com.baza.android.bzw.manager.UserInfoManager;
import com.slib.utils.AppUtil;
import com.slib.utils.ToastUtil;
import com.bznet.android.rcbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2018/1/4.
 * Title：
 * Note：
 */

public class ChangeMobileActivity extends BaseActivity implements IChangeMobileView, View.OnClickListener {
    @BindView(R.id.tv_title)
    TextView textView_title;
    @BindView(R.id.tv_change_mobile_des)
    TextView textView_des;
    @BindView(R.id.tv_old_mobile)
    TextView textView_currentMobile;
    @BindView(R.id.et_mobile)
    EditText editText_mobile;
    @BindView(R.id.et_sms_code)
    EditText editText_smsCode;
    @BindView(R.id.btn_get_sms_code)
    Button button_getSmsCode;

    private ChangeMobilePresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_mobile;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_change_mobile);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        textView_title.setText(R.string.title_check_mobile);
        String mobile = UserInfoManager.getInstance().getUserInfo().mobile;
        if (AppUtil.checkPhone(mobile))
            textView_currentMobile.setText(mobile.substring(0, 3) + "XXXX" + mobile.substring(7, 11));

        SpannableString spannableString = new SpannableString(mResources.getString(R.string.change_mobile_hint));
        spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_blue_53ABD5)), spannableString.length() - 6, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_des.setText(spannableString);

        mPresenter = new ChangeMobilePresenter(this, getIntent());
        mPresenter.initialize();
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
            case R.id.btn_submit:
                mPresenter.submit();
                break;
            case R.id.btn_get_sms_code:
                mPresenter.getSmSCode();
                break;
        }
    }

    @Override
    protected void onActivityDeadForApp() {
        if (mPresenter != null)
            mPresenter.onDestroy();
    }

    public static void launch(Activity activity) {

        launch(activity, 0);
    }

    public static void launch(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ChangeMobileActivity.class);
        if (requestCode > 0)
            activity.startActivityForResult(intent, requestCode);
        else
            activity.startActivity(intent);
    }

    @Override
    public String callGetMobile() {
        return editText_mobile.getText().toString().trim();
    }

    @Override
    public String callGetSmsCode() {
        return editText_smsCode.getText().toString().trim();
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
    public void callOnSendSmsCodeReply(boolean success, String errorMsg) {
        ImageView imageView = new ImageView(mApplication);
        imageView.setImageResource((success ? R.drawable.image_smscode_send_success : R.drawable.image_smscode_send_failed));
        ToastUtil.selfToast(this, imageView);
    }
}
