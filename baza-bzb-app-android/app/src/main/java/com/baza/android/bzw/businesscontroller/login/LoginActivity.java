package com.baza.android.bzw.businesscontroller.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.verificationsdk.ui.IActivityCallback;
import com.alibaba.verificationsdk.ui.VerifyActivity;
import com.alibaba.verificationsdk.ui.VerifyType;
import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.user.LoginResultBean;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.businesscontroller.login.presenter.LoginPresenter;
import com.baza.android.bzw.businesscontroller.login.viewinterface.ILoginView;
import com.baza.android.bzw.constant.ConfigConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.widget.ClearEditText;
import com.bznet.android.rcbox.BuildConfig;
import com.bznet.android.rcbox.R;
import com.slib.utils.AppUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/5/9.
 * Title：登录页
 * Note：
 */

public class LoginActivity extends BaseActivity implements ILoginView, View.OnClickListener, ClearEditText.IClearEditTextCheckOkStatusListener {
    @BindView(R.id.et_username)
    ClearEditText editText_userName;
    @BindView(R.id.et_sms_code)
    ClearEditText editText_smsCode;
    @BindView(R.id.tv_send_sms_code)
    TextView textView_getSmsCode;
    @BindView(R.id.cl_user_name)
    View view_parentUserName;
    @BindView(R.id.tv_agreement)
    TextView textView_agreement;
    @BindView(R.id.tv_private)
    TextView tv_private;
    @BindView(R.id.tv_user_name_error)
    TextView textView_errorUserName;
    @BindView(R.id.tv_sms_code_error)
    TextView textView_errorSmsCode;
    private LoginPresenter mPresenter;
    private Integer mTagWaitingSmsCode = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_login);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        mPresenter = new LoginPresenter(this);
        editText_userName.setCheckOkStatusListener(this);
        editText_smsCode.setCheckOkStatusListener(this);
        editText_userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                view_parentUserName.setBackgroundResource((hasFocus ? R.drawable.login_et_bg_focus : R.drawable.login_et_bg_un_focus));
            }
        });
        editText_smsCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editText_smsCode.setBackgroundResource((hasFocus ? R.drawable.login_et_bg_focus : R.drawable.login_et_bg_un_focus));
            }
        });

//        SpannableString spannableString = new SpannableString(mResources.getString(R.string.login_text_user_agreement));
//        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
//        textView_agreement.setText(spannableString);

        mPresenter.initialize();
        editText_userName.postDelayed(new Runnable() {
            @Override
            public void run() {
                editText_userName.requestFocus();
                showSoftInput(editText_userName);
            }
        }, 500);
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {
        ImageView imageView = findViewById(R.id.iv_back);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
        lp.topMargin += statusBarHeight;
        imageView.setLayoutParams(lp);
    }

    @Override
    protected boolean isUseDefaultStatusBarBackground() {
        return false;
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) finishAfterTransition();
        else super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_send_sms_code:
                //获取验证码
                mPresenter.getSmSCode();
                break;
            case R.id.btn_login:
                if (!mPresenter.checkIsEnableLogin())
                    return;
                if (BuildConfig.IS_DEBUG_BUILD) {
                    mPresenter.securitySuccessAndLogin(null);
                    return;
                }
                VerifyActivity.startSimpleVerifyUI(this, VerifyType.NOCAPTCHA, "0335", null, new IActivityCallback() {
                    @Override
                    public void onNotifyBackPressed() {

                    }

                    @Override
                    public void onResult(int retInt, Map<String, String> code) {
                        switch (retInt) {
                            case VerifyActivity.VERIFY_SUCC:
                                String sessionID = code.get("sessionID");
                                mPresenter.securitySuccessAndLogin(sessionID);
                                break;
                            case VerifyActivity.VERIFY_FAILED:
                                callShowToastMessage(null, R.string.security_verify_failed);
                            default:
                                break;
                        }
                    }
                });
                break;
            case R.id.tv_agreement:
                //用户协议
                RemoteBrowserActivity.launch(this, mResources.getString(R.string.user_agreement), ConfigConst.USER_AGREEMENT_LINK);
                break;
            case R.id.tv_private:
                //用户协议
                RemoteBrowserActivity.launch(this, "隐私协议", ConfigConst.USER_PRIVATE_LINK);
                break;
            case R.id.iv_login_with_wechat:
                mPresenter.doWeChatLogin();
                break;
        }
    }

    /**
     * 响应下次发送验证码倒计时
     *
     * @param amountSeconds 剩余时间
     */
    @Override
    public void callUpdateSendSmsCodeTextViewStatus(int amountSeconds) {
        if (amountSeconds >= 0) {
            textView_getSmsCode.setTag(R.id.hold_tag_id_one, mTagWaitingSmsCode);
            textView_getSmsCode.setText(getString(R.string.send_after_seconds_ith_value, String.valueOf(amountSeconds)));
            if (textView_getSmsCode.isClickable()) {
                textView_getSmsCode.setClickable(false);
                textView_getSmsCode.setTextColor(mResources.getColor(R.color.edit_text_hint_color_D3DFEF));
            }

        } else {
            textView_getSmsCode.setTag(R.id.hold_tag_id_one, null);
            textView_getSmsCode.setText(R.string.get_sms_code);
            boolean enable = AppUtil.checkPhone(editText_userName.getText().toString());
            textView_getSmsCode.setClickable(enable);
            textView_getSmsCode.setTextColor(mResources.getColor(enable ? R.color.text_color_blue_53ABD5 : R.color.edit_text_hint_color_D3DFEF));
        }
    }

    @Override
    public String callGetUserName() {
        return editText_userName.getText().toString().trim();
    }

    @Override
    public String callGetSmsCode() {
        return editText_smsCode.getText().toString().trim();
    }

    @Override
    public void callPreviousUserView(String phone) {
        //phone = "15814498877";//16611110006//15814490022
        editText_userName.setText(phone);
        if (!TextUtils.isEmpty(phone))
            editText_userName.setSelection(phone.length());
    }

    @Override
    public void callOnSendSmsCodeReply(boolean success, String errorMsg) {
        editText_userName.clearFocus();
        editText_smsCode.requestFocus();
        editText_smsCode.postDelayed(new Runnable() {
            @Override
            public void run() {
                showSoftInput(editText_smsCode);
            }
        }, 100);
    }

    @Override
    protected void onActivityDeadForApp() {
        mPresenter.onDestroy();
    }

    @Override
    public void callBindMobile(String uunionId) {
        BindMobileActivity.launch(this, uunionId, RequestCodeConst.INT_REQUEST_BIND_MOBILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodeConst.INT_REQUEST_BIND_MOBILE && resultCode == RESULT_OK) {
            LoginResultBean loginResultBean = (LoginResultBean) data.getSerializableExtra("loginResult");
            mPresenter.afterLogin(true, loginResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
        }
    }

    @Override
    public boolean shouldEnterOkStatus(ClearEditText clearEditText, Editable s) {
        if (clearEditText == editText_userName) {
            if (AppUtil.checkPhone(s.toString())) {
                callUpdateUserNameErrorView(false);
                if (textView_getSmsCode.getTag(R.id.hold_tag_id_one) == null && !textView_getSmsCode.isClickable()) {
                    textView_getSmsCode.setClickable(true);
                    textView_getSmsCode.setTextColor(mResources.getColor(R.color.text_color_blue_53ABD5));
                }
                return true;
            }
            if (textView_getSmsCode.getTag(R.id.hold_tag_id_one) == null && textView_getSmsCode.isClickable()) {
                textView_getSmsCode.setClickable(false);
                textView_getSmsCode.setTextColor(mResources.getColor(R.color.edit_text_hint_color_D3DFEF));
            }
        } else if (clearEditText == editText_smsCode && s.length() == 6) {
            callUpdateSmsCodeErrorView(false);
            return true;
        }
        return false;
    }

    @Override
    public void callUpdateUserNameErrorView(boolean hasError) {
        textView_errorUserName.setVisibility((hasError ? View.VISIBLE : View.INVISIBLE));
        view_parentUserName.setBackgroundResource(hasError ? R.drawable.login_et_bg_error : R.drawable.login_et_bg_focus);


    }

    @Override
    public void callUpdateSmsCodeErrorView(boolean hasError) {
        textView_errorSmsCode.setVisibility((hasError ? View.VISIBLE : View.INVISIBLE));
        editText_smsCode.setBackgroundResource(hasError ? R.drawable.login_et_bg_error : R.drawable.login_et_bg_focus);
    }
}
