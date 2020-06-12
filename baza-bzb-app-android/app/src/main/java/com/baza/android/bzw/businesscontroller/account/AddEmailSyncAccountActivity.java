package com.baza.android.bzw.businesscontroller.account;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.account.adapter.EmailInputHintAdapter;
import com.baza.android.bzw.businesscontroller.account.presenter.AddEmailSyncAccountPresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IAddEmailSyncAccountView;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.AndroidBug5497Workaround;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.TextWatcherImpl;
import com.baza.android.bzw.widget.ListPopupWindow;
import com.baza.android.bzw.widget.switchbutton.SwitchButton;
import com.slib.progress.IndeterminateProgressDrawable;
import com.slib.utils.AppUtil;
import com.slib.utils.DialogUtil;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/5/31.
 * Title：添加同步邮箱的账号
 * Note：
 */

public class AddEmailSyncAccountActivity extends BaseActivity implements IAddEmailSyncAccountView, View.OnClickListener {
    @BindView(R.id.mpb)
    ProgressBar progressBar_onSync;
    @BindView(R.id.tv_title)
    TextView textView_title;
    @BindView(R.id.tv_right_click)
    TextView textView_rightClick;
    @BindView(R.id.et_email)
    AutoCompleteTextView editText_email;
    @BindView(R.id.et_password)
    EditText editText_password;
    @BindView(R.id.et_emailHost)
    EditText editText_host;
    @BindView(R.id.et_port)
    EditText editText_port;
    @BindView(R.id.sb_ssl)
    SwitchButton switchButton_ssl;
    @BindView(R.id.item_host)
    View view_itemHost;
    @BindView(R.id.item_port)
    View view_itemPort;
    @BindView(R.id.item_ssl)
    View view_itemSSL;
    @BindView(R.id.tv_email_type)
    TextView textView_emailType;
    @BindView(R.id.tv_advance_set)
    TextView textView_advanceSet;
    @BindView(R.id.iv_password_show_hide)
    ImageView imageView_passwordShowOrHide;
    @BindView(R.id.btn_submit)
    Button button_submit;
    @BindView(R.id.tv_problem)
    TextView textView_problem;

    private AddEmailSyncAccountPresenter mPresenter;
    private EmailInputHintAdapter mEmailInputHintAdapter;
    private ListPopupWindow mListPopupWindow;
    private String[] mEmailUnknowTypeTitleArray;
    private Drawable mDrawableAdvanceSetClose, mDrawableAdvanceSetOpen;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_email_sync_account;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_add_email_sync_account);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        mDrawableAdvanceSetClose = AppUtil.drawableInit(R.drawable.dropdown_arrow_black, mResources);
        mDrawableAdvanceSetOpen = AppUtil.drawableInit(R.drawable.dropdown_arrow_up_black, mResources);
        IndeterminateProgressDrawable indeterminateProgressDrawable = new IndeterminateProgressDrawable(mApplication);
        indeterminateProgressDrawable.setTint(mResources.getColor(R.color.text_color_blue_0D315C));
        progressBar_onSync.setIndeterminateDrawable(indeterminateProgressDrawable);
        textView_title.setText(R.string.import_resume);
        textView_rightClick.setText(R.string.email_help);
        mPresenter = new AddEmailSyncAccountPresenter(this, getIntent());
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {
        AndroidBug5497Workaround.assistActivity(this, statusBarHeight);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left_click:
                finish();
                break;
            case R.id.tv_advance_set:
                callSetDetailSettingViews((textView_advanceSet.getTag() == null));
                break;
            case R.id.btn_submit:
                mPresenter.syncEmailAccount(editText_email.getText().toString().trim(), editText_password.getText().toString().trim(), editText_host.getText().toString().trim(), editText_port.getText().toString().trim(), switchButton_ssl.isChecked());
                break;
            case R.id.tv_right_click:
                RemoteBrowserActivity.launch(this, mResources.getString(R.string.email_help), URLConst.LINK_EMIAL_HELP);
                break;
            case R.id.tv_email_type:
                if (mListPopupWindow == null) {
                    mEmailUnknowTypeTitleArray = mResources.getStringArray(R.array.email_unknow_type_list);
                    mListPopupWindow = new ListPopupWindow(this, 12, ScreenUtil.dip2px(30), mEmailUnknowTypeTitleArray, new ListPopupWindow.IMenuClickListener() {
                        @Override
                        public void onMenuClick(int position) {
                            textView_emailType.setText(mEmailUnknowTypeTitleArray[position]);
                            textView_emailType.setTag(R.id.hold_tag_id_one, position);
                        }
                    });
                }

                mListPopupWindow.show(textView_emailType);
                break;
            case R.id.iv_password_show_hide:
                boolean hide = (imageView_passwordShowOrHide.getTag() == null);
                imageView_passwordShowOrHide.setTag((hide ? R.id.iv_password_show_hide : null));
                imageView_passwordShowOrHide.setImageResource((hide ? R.drawable.password_ic_visibility : R.drawable.password_ic_visibility_off));
                editText_password.setTransformationMethod(hide ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
                int length = editText_password.getText().length();
                editText_password.setSelection(length);
                break;
            case R.id.tv_problem:
                RemoteBrowserActivity.launch(AddEmailSyncAccountActivity.this, mResources.getString(R.string.email_help), URLConst.LINK_EMIAL_SET);
                break;
        }
    }

    @Override
    public void callUpdateOnSyncingViews() {
        button_submit.setClickable(false);
        button_submit.setBackgroundResource(R.drawable.shape_rc_2_nostoken_solid_grey);
        progressBar_onSync.setVisibility(View.VISIBLE);
        textView_title.setText(R.string.on_verification);
    }

    @Override
    public void callCancelOnSyncingViews() {
        progressBar_onSync.setVisibility(View.GONE);
        textView_title.setText(R.string.sync_email_resume);
        button_submit.setClickable(true);
        button_submit.setBackgroundResource(R.drawable.login_login_btn_bg);
    }

    @Override
    public void callShowImportErrorDialog(final int errorCode) {
        if (errorCode == CustomerRequestAssistHandler.NET_REQUEST_EMAIL_PASSWORD_ERROR) {
            DialogUtil.doubleButtonShow(this, R.string.import_failed, R.string.tips_email_password_error, R.string.sure, R.string.how_to_open_pop3, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RemoteBrowserActivity.launch(AddEmailSyncAccountActivity.this, mResources.getString(R.string.email_help), URLConst.LINK_EMIAL_POP3);
                }
            }, null);

            return;
        }
        DialogUtil.singleButtonShow(this, R.string.import_failed, R.string.tips_check_email_set, R.string.sure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSetDetailSettingViews(true);
            }
        });
    }

    @Override
    public void callSetDetailSettingViews(boolean show) {
        textView_advanceSet.setTag(show ? R.id.tv_advance_set : null);
        textView_advanceSet.setCompoundDrawables(null, null, (show ? mDrawableAdvanceSetOpen : mDrawableAdvanceSetClose), null);
        view_itemHost.setVisibility(show ? View.VISIBLE : View.GONE);
        view_itemPort.setVisibility(show ? View.VISIBLE : View.GONE);
        view_itemSSL.setVisibility(show ? View.VISIBLE : View.GONE);
        textView_problem.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void callMayInModifyMode(String oldAccount) {
        if (!TextUtils.isEmpty(oldAccount)) {
            editText_email.setEnabled(false);
            editText_email.setClickable(false);
//        ((ClearEditText) editText_email).setDeleteIcon(0);
            editText_email.setText(oldAccount);
        } else {
            editText_email.post(new Runnable() {
                @Override
                public void run() {
                    mEmailInputHintAdapter = new EmailInputHintAdapter(AddEmailSyncAccountActivity.this, editText_email.getMeasuredWidth(), ScreenUtil.dip2px(30), mPresenter.getEmailHintList(), null);
                    editText_email.setAdapter(mEmailInputHintAdapter);
                    editText_email.addTextChangedListener(new TextWatcherImpl() {
                        @Override
                        public void afterTextChanged(Editable s) {
                            String filter = s.toString().trim();
                            mPresenter.filterEmailHint(filter);
                            textView_emailType.setVisibility((mPresenter.shouldHintUnknowEmailType(filter)) ? View.VISIBLE : View.GONE);
                        }
                    });
                }
            });
        }

    }

    @Override
    public void callFinished(boolean isAddSuccess) {
        if (isAddSuccess)
            setResult(RESULT_OK);
        finish();
    }

    @Override
    public void callUpdateEmailHintList() {
        if (mEmailInputHintAdapter != null)
            mEmailInputHintAdapter.notifyDataSetChanged();
    }

    @Override
    public int callGetUnknowEmailType() {
        if (textView_emailType.getVisibility() != View.VISIBLE || mEmailUnknowTypeTitleArray == null)
            return CommonConst.INT_ID_NONE;
        int position = (int) textView_emailType.getTag(R.id.hold_tag_id_one);
        if (position == (mEmailUnknowTypeTitleArray.length - 1))
            return CommonConst.INT_ID_NONE;
        return (position + 2);
    }

    public static void launch(Activity activity, int requestCode) {
        launch(activity, requestCode, null);
    }

    public static void launch(Activity activity, int requestCode, String account) {
        Intent intent = new Intent(activity, AddEmailSyncAccountActivity.class);
        if (account != null)
            intent.putExtra("account", account);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityDeadForApp() {
        super.onActivityDeadForApp();
        mPresenter.onDestroy();
        AppUtil.nonCallBackDrawable(mDrawableAdvanceSetClose);
        AppUtil.nonCallBackDrawable(mDrawableAdvanceSetOpen);
    }
}
