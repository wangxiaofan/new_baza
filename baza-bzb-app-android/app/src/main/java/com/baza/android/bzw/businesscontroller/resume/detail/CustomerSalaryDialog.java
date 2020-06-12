package com.baza.android.bzw.businesscontroller.resume.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bznet.android.rcbox.R;

import baza.dialog.simpledialog.MaterialDialog;

public class CustomerSalaryDialog {
    public interface ICustomerSalaryListener {
        void onSubmit(String salary);

        boolean onCheck(String salary);

        void onDismiss();
    }

    private Context mContext;
    private MaterialDialog mMaterialDialog;
    private EditText editText_content;
    private ICustomerSalaryListener mCustomerSalaryListener;
    private InputMethodManager mImm;
    private String mOldCustomerSalary;

    public CustomerSalaryDialog(Context context, String customerSalary, ICustomerSalaryListener customerSalaryListener) {
        this.mCustomerSalaryListener = customerSalaryListener;
        this.mContext = context;
        this.mOldCustomerSalary = customerSalary;
        this.mImm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        makeDialog();
    }

    private void makeDialog() {
        mMaterialDialog = new MaterialDialog(mContext);
        mMaterialDialog.setCancelable(true);
        mMaterialDialog.clearTitle().buildCancelButtonText(R.string.cancel).buildSureButtonText(R.string.sure).setAutoDismissEnable(false).buildClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                mMaterialDialog.dismiss();
                if (mCustomerSalaryListener != null)
                    mCustomerSalaryListener.onDismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomerSalaryListener != null) {
                    String content = editText_content.getText().toString();
                    boolean passChecked = mCustomerSalaryListener.onCheck(content);
                    if (passChecked) {
                        hideSoftInput();
                        mMaterialDialog.dismiss();
                        mCustomerSalaryListener.onSubmit(content);
                        mCustomerSalaryListener.onDismiss();
                    }
                    return;
                }
                mMaterialDialog.dismiss();
            }
        });
        mMaterialDialog.setCanceledOnTouchOutside(false);
        mMaterialDialog.setOutSideClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
            }
        });
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_customer_salary, null);
        editText_content = contentView.findViewById(R.id.et_content);
        if (!TextUtils.isEmpty(mOldCustomerSalary))
            editText_content.setText(mOldCustomerSalary);
        mMaterialDialog.setMessageView(contentView);
    }

    private void hideSoftInput() {
        try {
            editText_content.clearFocus();
            if (mImm.isActive() && editText_content.getWindowToken() != null)
                mImm.hideSoftInputFromWindow(editText_content.getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    public void show() {
        mMaterialDialog.show();
        editText_content.requestFocus();
    }

}
