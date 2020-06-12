package com.baza.android.bzw.businesscontroller.account.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.slib.utils.AppUtil;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2018/12/5.
 * Title：
 * Note：
 */
public class InviteDialog extends Dialog implements View.OnClickListener, TextWatcher {

    public static final int STATE_NORMAL = 0;
    public static final int STATE_FAILED = 1;
    public static final int STATE_FAILED_YOURSELF_CODE = 2;
    public static final int STATE_SUCCESS = 3;


    public interface IInviteSetListener {
        void onInvitedCodeSubmit(String code);

        boolean checkInvitedCodeIsMine(String code);

        boolean checkIsInvitedCodeEnable(String code);
    }

    EditText editText;
    Button button_submit;
    TextView textView_title;
    TextView textView_subTitle;
    ImageView imageView_close;
    View view_line;
    Drawable drawable;
    IInviteSetListener listener;
    int state;
    int dp_15, dp_5, dp_30;

    public InviteDialog(@NonNull Context context, int state, IInviteSetListener listener) {
        super(context, R.style.customerDialog);
        this.state = state;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_invite);
        View mainView = findViewById(R.id.main_view);
        ViewGroup.LayoutParams lp = mainView.getLayoutParams();
        lp.width = (int) (ScreenUtil.screenWidth * 0.8f);
        mainView.setLayoutParams(lp);
        button_submit = findViewById(R.id.btn_submit);
        button_submit.setOnClickListener(this);
        imageView_close = findViewById(R.id.iv_close);
        imageView_close.setOnClickListener(this);
        editText = findViewById(R.id.et_input);
        editText.addTextChangedListener(this);
        textView_title = findViewById(R.id.tv_title);
        textView_subTitle = findViewById(R.id.tv_sub_title);
        view_line = findViewById(R.id.view_line);
        dp_15 = (int) getContext().getResources().getDimension(R.dimen.dp_15);
        dp_5 = (int) getContext().getResources().getDimension(R.dimen.dp_5);
        dp_30 = (int) getContext().getResources().getDimension(R.dimen.dp_30);
        textView_subTitle.setPadding(dp_15, dp_5, dp_15, dp_15);
        editText.setPadding(dp_5, 0, dp_5, 0);
        updateState(state);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.length()>0&&!button_submit.isEnabled()){
            button_submit.setEnabled(true);
            button_submit.setBackgroundResource(R.drawable.account_invite_dialog_submit_btn_bg);
        }else if(s.length()==0&&button_submit.isEnabled()){
            button_submit.setEnabled(false);
            button_submit.setBackgroundResource(R.drawable.account_invite_dialog_submit_btn_bg_unenable);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (listener != null) {
                    String code = editText.getText().toString().trim();
                    if (!listener.checkIsInvitedCodeEnable(code))
                        return;
                    if (listener.checkInvitedCodeIsMine(code)) {
                        updateState(STATE_FAILED_YOURSELF_CODE);
                        return;
                    }
                    dismiss();
                    listener.onInvitedCodeSubmit(code);
                    return;
                }
                dismiss();
                break;
            case R.id.iv_close:
                dismiss();
                break;
        }
    }

    private void updateState(int state) {
        switch (state) {
            case STATE_NORMAL:
                textView_title.setText(R.string.invite_title_normal);
                textView_subTitle.setText(R.string.invite_sub_title_normal);
                editText.setHint(R.string.invite_code_hint_normal);
                textView_title.setCompoundDrawables(null, null, null, null);
                textView_subTitle.setPadding(dp_15, dp_5, dp_15, dp_15);
                editText.setVisibility(View.VISIBLE);
                view_line.setVisibility(View.VISIBLE);
                button_submit.setVisibility(View.VISIBLE);
                break;
            case STATE_SUCCESS:
                textView_title.setText(R.string.invite_title_success);
                textView_subTitle.setText(R.string.invite_sub_title_success);
                drawable = AppUtil.drawableInit(R.drawable.account_invite_code_success, getContext().getResources());
                textView_title.setCompoundDrawables(drawable, null, null, null);
                textView_subTitle.setPadding(dp_15, dp_5, dp_15, dp_15);
                editText.setVisibility(View.GONE);
                view_line.setVisibility(View.GONE);
                button_submit.setVisibility(View.GONE);
                break;
            case STATE_FAILED:
            case STATE_FAILED_YOURSELF_CODE:
                textView_title.setText(R.string.invite_title_failed);
                textView_subTitle.setText(state == STATE_FAILED ? R.string.invite_sub_title_failed : R.string.invite_sub_title_failed_your_self_code);
                editText.setHint(state == STATE_FAILED ? R.string.invite_code_hint_normal : R.string.invite_code_hint_failed_your_self_code);
                drawable = AppUtil.drawableInit(R.drawable.account_invite_code_failed, getContext().getResources());
                textView_title.setCompoundDrawables(drawable, null, null, null);
                textView_subTitle.setPadding(dp_15, dp_30, dp_15, dp_5);
                editText.setVisibility(View.VISIBLE);
                view_line.setVisibility(View.VISIBLE);
                button_submit.setVisibility(View.VISIBLE);
                editText.requestFocus();
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (drawable != null)
            drawable.setCallback(null);
    }
}
