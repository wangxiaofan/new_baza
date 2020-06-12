package com.baza.android.bzw.businesscontroller.publish;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.businesscontroller.login.adapter.CompanyLibraryAdapter;
import com.baza.android.bzw.businesscontroller.login.presenter.FillUserInfoPresenter;
import com.baza.android.bzw.businesscontroller.login.viewinterface.IFillUserInfoView;
import com.baza.android.bzw.extra.TextWatcherImpl;
import com.baza.android.bzw.widget.ClearAutoCompleteTextView;
import com.slib.utils.AppUtil;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/27.
 * Title：编辑部分信息
 * Note：
 */

public class EditValueActivity extends BaseActivity implements IFillUserInfoView, View.OnClickListener, TextWatcher {
    public static final int MSG_TYPE_DEFAULT = 2;
    public static final int MSG_TYPE_NICKNAME = 3;
    public static final int MSG_TYPE_REAL_NAME = 4;
    public static final int MSG_TYPE_JOB = 5;
    public static final int MSG_TYPE_EMAIL = 6;
    public static final int MSG_TYPE_COMPANY = 7;
    public static final int MSG_TYPE_BUSINESS = 8;

    public static final int INPUT_TYPE_TEXT = 0;
    public static final int INPUT_TYPE_NUM = 1;

    private ClearAutoCompleteTextView clearEditText_value;
    private TextView textView_sure;

    private int mMaxLength, mMinLength, mMaxLine, mInputType, mMsgType;
    private String mOldValue;
    private CompanyLibraryAdapter mCompanyLibraryAdapter;
    private FillUserInfoPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_value;
    }

    @Override
    protected String getPageTitle() {
        return mMsgType == MSG_TYPE_NICKNAME ? mResources.getString(R.string.page_edit_nick_name) : "EditValueActivity";
    }

    @Override
    protected void initWhenCallOnCreate() {
        Intent intent = getIntent();
        mMaxLength = intent.getIntExtra("mMaxLength", -1);
        mMinLength = intent.getIntExtra("mMinLength", -1);
        mMaxLine = intent.getIntExtra("mMaxLine", -1);
        mInputType = intent.getIntExtra("mInputType", INPUT_TYPE_TEXT);
        mMsgType = intent.getIntExtra("msgType", MSG_TYPE_DEFAULT);
        mOldValue = intent.getStringExtra("mOldValue");

        TextView textView = findViewById(R.id.tv_title);
        textView.setText(getTitleInfo());

        textView_sure = findViewById(R.id.tv_right_click);
        textView_sure.setVisibility(View.VISIBLE);
        textView_sure.setText(R.string.sure);
        updateSureBtnStatus(false);

        //////////////
        clearEditText_value = findViewById(R.id.et_value);

        if (mMaxLine > 1)
            clearEditText_value.setMaxLines(mMaxLength);
        else
            clearEditText_value.setSingleLine();


        clearEditText_value.setHint(getHint());
        clearEditText_value.addTextChangedListener(this);

        if (mMaxLength > 0)
            clearEditText_value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mMaxLength)});

        switch (mInputType) {
            case INPUT_TYPE_NUM:
                clearEditText_value.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }

        clearEditText_value.setText(mOldValue);
        if (mOldValue != null)
            clearEditText_value.setSelection(mMaxLength > 0 ? (mOldValue.length() >= mMaxLength ? mMaxLength : mOldValue.length()) : mOldValue.length());

        int inputLimitHint = getLimit();
        if (inputLimitHint > 0)
            ((TextView) findViewById(R.id.tv_input_limit_hint)).setText(inputLimitHint);
        if (mMsgType == MSG_TYPE_COMPANY) {
            mPresenter = new FillUserInfoPresenter(this);
            clearEditText_value.post(new Runnable() {
                @Override
                public void run() {
                    mCompanyLibraryAdapter = new CompanyLibraryAdapter(EditValueActivity.this, clearEditText_value.getMeasuredWidth(), ScreenUtil.dip2px(30), null);
                    clearEditText_value.setAdapter(mCompanyLibraryAdapter);
                    clearEditText_value.addTextChangedListener(new TextWatcherImpl() {
                        @Override
                        public void afterTextChanged(Editable s) {
                            mPresenter.getAllCompany(s.toString());
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onActivityDeadForApp() {
        if (mPresenter != null)
            mPresenter.onDestroy();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    private void updateSureBtnStatus(boolean enable) {
        textView_sure.setTextColor(mResources.getColor(enable ? R.color.text_color_blue_0D315C : R.color.text_color_grey_94A1A5));
        textView_sure.setClickable(enable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.tv_right_click:
                String value = clearEditText_value.getText().toString().trim();
                Intent intentData = new Intent();
                intentData.putExtra("value", value);
                setResult(RESULT_OK, intentData);
                finish();
                break;

        }
    }

    public static String parseResultValue(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            return data.getStringExtra("value");
        }
        return null;
    }

    private int getHint() {
        int hint = R.string.input_msg;
        switch (mMsgType) {
            case MSG_TYPE_NICKNAME:
                hint = R.string.input_nick_name;
                break;
            case MSG_TYPE_REAL_NAME:
                hint = R.string.user_verify_hint_real_name;
                break;
            case MSG_TYPE_JOB:
                hint = R.string.user_verify_hint_title;
                break;
            case MSG_TYPE_EMAIL:
                hint = R.string.input_email;
                break;
            case MSG_TYPE_COMPANY:
                hint = R.string.user_verify_hint_company;
                break;
            case MSG_TYPE_BUSINESS:
                hint = R.string.user_verify_hint_business;
                break;
        }

        return hint;
    }

    private int getTitleInfo() {
        int hint = R.string.input_msg;
        switch (mMsgType) {
            case MSG_TYPE_NICKNAME:
                hint = R.string.title_edit_nick_name;
                break;
            case MSG_TYPE_REAL_NAME:
                hint = R.string.title_edit_true_name;
                break;
            case MSG_TYPE_JOB:
                hint = R.string.title_edit_job;
                break;
            case MSG_TYPE_EMAIL:
                hint = R.string.title_edit_email;
                break;
            case MSG_TYPE_COMPANY:
                hint = R.string.title_edit_company;
                break;
            case MSG_TYPE_BUSINESS:
                hint = R.string.title_edit_business;
                break;
        }

        return hint;
    }

    private int getLimit() {
        int hint = 0;
        switch (mMsgType) {
            case MSG_TYPE_NICKNAME:
                hint = R.string.nick_name_edit_limit;
                break;
        }
        return hint;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        checkValues(s.toString().trim());
    }

    private void checkValues(String value) {
        boolean enable = doCheckEnable(value);
        updateSureBtnStatus(enable);
    }

    private boolean doCheckEnable(String value) {
        if (TextUtils.isEmpty(value))
            return false;
        if (mMsgType == MSG_TYPE_EMAIL && !AppUtil.checkEmail(value))
            return false;
        if (mMinLength > 0) {
            int chineseCharCount = AppUtil.getChineseCharCount(value);
            return chineseCharCount * 2 + value.length() - chineseCharCount >= mMinLength;

        }
        return true;

    }


    public static void launch(Activity activity, int requestCode, int msgType, int maxLine, int maxLength, int minLength, int inputType, String oldValue) {
        Intent intent = new Intent(activity, EditValueActivity.class);
        intent.putExtra("mMaxLength", maxLength);
        intent.putExtra("mMinLength", minLength);
        intent.putExtra("mMaxLine", maxLine);
        intent.putExtra("mInputType", inputType);
        intent.putExtra("msgType", msgType);
        if (oldValue != null)
            intent.putExtra("mOldValue", oldValue);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void launch(Activity activity, int requestCode, int msgType, String oldValue) {
        launch(activity, requestCode, msgType, -1, -1, -1, -1, oldValue);
    }

    @Override
    public String callGetUserName() {
        return null;
    }

    @Override
    public String callGetEmail() {
        return null;
    }

    @Override
    public String callGetCompany() {
        return null;
    }

    @Override
    public String callGetJob() {
        return null;
    }

    @Override
    public void callSetCompanyAutoCompleteLib(List<String> companyList) {
        mCompanyLibraryAdapter.refresh(companyList);
    }

    @Override
    public void callSetOldData(UserInfoBean userInfoBean) {

    }
}
