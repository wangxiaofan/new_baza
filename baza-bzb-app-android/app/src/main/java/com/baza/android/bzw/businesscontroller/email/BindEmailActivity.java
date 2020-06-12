package com.baza.android.bzw.businesscontroller.email;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.email.presenter.BindEmailPresenter;
import com.baza.android.bzw.businesscontroller.email.viewinterface.IBindEmailView;
import com.baza.android.bzw.extra.TextWatcherImpl;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/5/31.
 * Title：
 * Note：
 */

public class BindEmailActivity extends BaseActivity implements IBindEmailView, View.OnClickListener {
    @BindView(R.id.tv_title)
    TextView textView_title;
    @BindView(R.id.tv_right_click)
    TextView textView_rightClick;
    @BindView(R.id.tv_no_bind_hint)
    TextView textView_noBindHint;
    @BindView(R.id.tv_email_item_title)
    TextView textView_emailItemTitle;
    @BindView(R.id.et_input_email)
    EditText editText_emailInput;
    @BindView(R.id.btn_bind)
    Button button_toBindNow;

    private BindEmailPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_share_bind_email;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_share_bind_email);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        mPresenter = new BindEmailPresenter(this, getIntent());
        textView_rightClick.setVisibility(View.GONE);
        textView_rightClick.setText(R.string.sure);
        editText_emailInput.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable s) {
                updateSureButtonStatus(AppUtil.checkEmail(s.toString()));
            }
        });
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    protected void onActivityDeadForApp() {
        if (mPresenter != null)
            mPresenter.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.tv_right_click:
                mPresenter.bindEmail(editText_emailInput.getText().toString().trim());
                break;
            case R.id.btn_bind:
                textView_noBindHint.setVisibility(View.GONE);
                button_toBindNow.setVisibility(View.GONE);
                textView_emailItemTitle.setVisibility(View.VISIBLE);
                editText_emailInput.setVisibility(View.VISIBLE);
                textView_rightClick.setVisibility(View.VISIBLE);
                editText_emailInput.requestFocus();
                updateSureButtonStatus(AppUtil.checkEmail(editText_emailInput.getText().toString()));
                break;
        }
    }

    private void updateSureButtonStatus(boolean clickEnable) {
        if (clickEnable && !textView_rightClick.isClickable()) {
            textView_rightClick.setClickable(true);
            textView_rightClick.setTextColor(mResources.getColorStateList(R.drawable.text_color_selector_white_gray));
        } else if (!clickEnable && textView_rightClick.isClickable()) {
            textView_rightClick.setClickable(false);
            textView_rightClick.setTextColor(mResources.getColor(R.color.text_color_grey_D3D3D3));
        }
    }

    public static void launch(Activity activity) {
        launch(activity, false);
    }

    public static void launch(Activity activity, boolean isBindForSendEmailForTargetResume) {
        Intent intent = new Intent(activity, BindEmailActivity.class);
        intent.putExtra("isBindForSendEmailForTargetResume", isBindForSendEmailForTargetResume);
        activity.startActivity(intent);
    }

    @Override
    public void callFinished() {
        finish();
    }

    @Override
    public void callSetPreviousEmail(String email) {
        editText_emailInput.setText(email);
        if (!TextUtils.isEmpty(email))
            editText_emailInput.setSelection(email.length());
    }

    @Override
    public void callSetTitleAndHint(String title, String hint) {
        textView_title.setText(title);
        textView_noBindHint.setText(hint);
    }
}
