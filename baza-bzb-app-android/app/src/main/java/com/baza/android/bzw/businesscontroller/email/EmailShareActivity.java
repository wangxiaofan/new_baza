package com.baza.android.bzw.businesscontroller.email;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.email.AddresseeBean;
import com.baza.android.bzw.businesscontroller.email.adapter.AddresseeInputHintAdapter;
import com.baza.android.bzw.businesscontroller.email.presenter.EmailSharePresenter;
import com.baza.android.bzw.businesscontroller.email.viewinterface.IEmailShareView;
import com.baza.android.bzw.extra.AndroidBug5497Workaround;
import com.baza.android.bzw.extra.TextWatcherImpl;
import com.baza.android.bzw.widget.TopTipUI;
import com.slib.utils.DateUtil;
import com.slib.utils.KeyBoardHelper;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/5/31.
 * Title：
 * Note：
 */

public class EmailShareActivity extends BaseActivity implements IEmailShareView, View.OnClickListener {
    @BindView(R.id.ll_addresseeContainer)
    LinearLayout linearLayout_addresseeContainer;
    @BindView(R.id.ll_previousContainer)
    LinearLayout linearLayout_previousContainer;
    @BindView(R.id.et_addressee_input)
    AutoCompleteTextView autoCompleteTextView_addresseeInput;
    @BindView(R.id.et_live_msg)
    EditText editText_liveMsg;
    @BindView(R.id.tv_tips)
    TextView textView_tips;
    @BindView(R.id.rl_bottom)
    View view_bottom;
    @BindView(R.id.cb_email_link)
    CheckBox checkBox_emailLink;
    @BindView(R.id.cb_email_attachment)
    CheckBox checkBox_emailAttachment;
    TopTipUI addresseeTipUI;


    private EmailSharePresenter mPresenter;
    private boolean mShouldHideLiveMsg;
    private AddresseeInputHintAdapter mAddresseeInputHintAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_email_share;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_share_email);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        TextView textView_title = findViewById(R.id.tv_title);
        textView_title.setText(R.string.share_resume);
        findViewById(R.id.tv_right_click).setVisibility(View.GONE);
        checkBox_emailLink.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                textView_tips.setVisibility((isChecked ? View.INVISIBLE : View.VISIBLE));
            }
        });
        autoCompleteTextView_addresseeInput.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable s) {
                String email = autoCompleteTextView_addresseeInput.getText().toString();
                if (email.length() > 0 && email.endsWith(" ")) {
                    email = email.trim();
                    autoCompleteTextView_addresseeInput.setText(email);
                    autoCompleteTextView_addresseeInput.setSelection(email.length());
                    mPresenter.addNewAddressee(email, null);
                }
            }
        });
        autoCompleteTextView_addresseeInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    mPresenter.addNewAddressee(autoCompleteTextView_addresseeInput.getText().toString().trim(), null);
                    return true;
                }
                return false;
            }
        });
        AndroidBug5497Workaround.assistActivity(this, mStatusBarHeight);
        watchKeyBord();
        mPresenter = new EmailSharePresenter(this, getIntent());
        mPresenter.initialize();
    }

    @Override
    protected void onActivityDeadForApp() {
        mPresenter.onDestroy();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    protected boolean isHideInputMethodAutoWhenTouchWindow() {
        return false;
    }

    @Override
    public void onClick(View v) {
        hideSoftInput();
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.item_addressee_previous:
                int position = (int) v.getTag();
                mPresenter.addNewAddressee(null, mPresenter.getPreviousList().get(position));
                break;
            case R.id.btn_send:
                String emailOnInput = autoCompleteTextView_addresseeInput.getText().toString().trim();
                if (!TextUtils.isEmpty(emailOnInput)) {
                    if (mPresenter.addNewAddressee(emailOnInput, null))
                        mPresenter.sendEmails(checkBox_emailLink.isChecked(), checkBox_emailAttachment.isChecked(), editText_liveMsg.getText().toString());
                } else
                    mPresenter.sendEmails(checkBox_emailLink.isChecked(), checkBox_emailAttachment.isChecked(), editText_liveMsg.getText().toString());
                break;
            case R.id.addressee_item_id:
                showAddresseeAssignedTips(v);
                break;
        }
    }

    private void watchKeyBord() {
        autoCompleteTextView_addresseeInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mShouldHideLiveMsg = true;
                }
                return false;
            }
        });
        editText_liveMsg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mShouldHideLiveMsg = false;
                }
                return false;
            }
        });
        KeyBoardHelper.addKeyBoardOpenOrClosedListener(this, new KeyBoardHelper.IKeyBoardListener() {
            @Override
            public void onKeyBoardOpen() {
                if (mShouldHideLiveMsg && view_bottom.getVisibility() != View.GONE) {
                    view_bottom.setVisibility(View.INVISIBLE);
                    view_bottom.post(new Runnable() {
                        @Override
                        public void run() {
                            view_bottom.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onKeyBoardClosed() {
                mShouldHideLiveMsg = false;
                view_bottom.post(new Runnable() {
                    @Override
                    public void run() {
                        view_bottom.setVisibility(View.VISIBLE);
                    }
                });

            }
        });
    }

    private void showAddresseeAssignedTips(View v) {
        final int position = (int) v.getTag();
        if (addresseeTipUI == null) {
            addresseeTipUI = new TopTipUI(this, null);
            addresseeTipUI.updateMenus(new String[]{mResources.getString(R.string.delete)});
        }
        addresseeTipUI.setMenuClickListener(new TopTipUI.IMenuClickListener() {
            @Override
            public void onTipMenuClick(int menuIndex) {
                mPresenter.deleteTargetAssignedAddressee(position);
            }
        });
        addresseeTipUI.show(v);
    }

    public static void launch(Activity activity, String candidateId, boolean isShareContact, boolean isShareRemark, boolean hasOriginalFile) {
        Intent intent = new Intent(activity, EmailShareActivity.class);
        intent.putExtra("isShareContact", isShareContact);
        intent.putExtra("isShareRemark", isShareRemark);
        intent.putExtra("candidateId", candidateId);
        intent.putExtra("hasOriginalFile", hasOriginalFile);
        activity.startActivity(intent);
    }

    @Override
    public void callResetAddresseeView(boolean couldContinueAdd) {
        ArrayList<AddresseeBean> mAddresseeList = mPresenter.getAddresseeList();
        //注意最后一个是输入框
        linearLayout_addresseeContainer.removeViewAt(linearLayout_addresseeContainer.getChildCount() - 1);
        int currentCount = linearLayout_addresseeContainer.getChildCount();
        int needShowCount = mAddresseeList.size();
        boolean hasCache;
        TextView textView;
        for (int i = 0; i < needShowCount; i++) {
            hasCache = (i < currentCount);
            textView = (hasCache ? (TextView) linearLayout_addresseeContainer.getChildAt(i) : createAddresseeView());
            textView.setText(mAddresseeList.get(i).email);
            textView.setVisibility(View.VISIBLE);
            textView.setTag(i);
            textView.setOnClickListener(this);
            if (!hasCache)
                linearLayout_addresseeContainer.addView(textView);
        }

        if (currentCount > needShowCount) {
            for (; needShowCount < currentCount; needShowCount++)
                linearLayout_addresseeContainer.getChildAt(needShowCount).setVisibility(View.GONE);
        }
        //加上输入框
        autoCompleteTextView_addresseeInput.setText(null);
        linearLayout_addresseeContainer.addView(autoCompleteTextView_addresseeInput);
        if (couldContinueAdd) {
            autoCompleteTextView_addresseeInput.setVisibility(View.VISIBLE);
            autoCompleteTextView_addresseeInput.requestFocus();
        } else
            autoCompleteTextView_addresseeInput.setVisibility(View.GONE);
    }

    @Override
    public void callSetPreviousAddresseeView(int mMaxPreviousCount) {
        List<AddresseeBean> mPreviousList = mPresenter.getPreviousList();
        if (mPreviousList == null)
            return;
        View view_item;
        TextView textView;
        AddresseeBean addresseeBean;
        mMaxPreviousCount = (mMaxPreviousCount < mPreviousList.size() ? mMaxPreviousCount : mPreviousList.size());
        for (int i = 0; i < mMaxPreviousCount; i++) {
            addresseeBean = mPreviousList.get(i);
            view_item = getLayoutInflater().inflate(R.layout.layout_addressee_previous, null);
            view_item.setOnClickListener(this);
            view_item.setTag(i);
            view_item.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(30)));
            textView = view_item.findViewById(R.id.tv_email);
            textView.setText(addresseeBean.email);
            textView = view_item.findViewById(R.id.tv_createTime);
            textView.setText(DateUtil.longMillions2FormatDate(addresseeBean.createTime, DateUtil.SDF_YMD_HMS));
            linearLayout_previousContainer.addView(view_item);
        }
    }

    @Override
    public void callHideAttachmentShareSelection() {
        checkBox_emailAttachment.setChecked(false);
        checkBox_emailAttachment.setVisibility(View.GONE);
        checkBox_emailLink.setChecked(true);
        checkBox_emailLink.setEnabled(false);
    }

    @Override
    public void callSetAddresseeHint() {
        autoCompleteTextView_addresseeInput.post(new Runnable() {
            @Override
            public void run() {
                List<AddresseeBean> mPreviousList = mPresenter.getPreviousList();
                if (mPreviousList == null)
                    return;
                mAddresseeInputHintAdapter = new AddresseeInputHintAdapter(EmailShareActivity.this, mPreviousList, autoCompleteTextView_addresseeInput.getWidth(), autoCompleteTextView_addresseeInput.getHeight(), null);
                autoCompleteTextView_addresseeInput.setAdapter(mAddresseeInputHintAdapter);
                autoCompleteTextView_addresseeInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        autoCompleteTextView_addresseeInput.setText(null);
                        mPresenter.addNewAddressee(mAddresseeInputHintAdapter.getStrData(position), null);
                    }
                });
            }
        });
    }

    @Override
    public void callFinished() {
        finish();
    }


    private TextView createAddresseeView() {
        TextView textView = new TextView(this);
        textView.setId(R.id.addressee_item_id);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ScreenUtil.dip2px(25)));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTextColor(mResources.getColor(R.color.text_color_black_666666));
        textView.setBackgroundResource(R.drawable.shape_background_addressee);
        int padding = ScreenUtil.dip2px(10);
        textView.setPadding(padding, 0, padding, 0);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        return textView;
    }


}
