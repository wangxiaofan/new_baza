package com.baza.android.bzw.widget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bznet.android.rcbox.R;

import baza.dialog.simpledialog.MaterialDialog;

public class AddLabelDialog {
    public interface IAddLabelListener {
        void onSubmit(String labelText);

        boolean onCheck(String labelText);
    }

    private Context mContext;
    private MaterialDialog mMaterialDialog;
    private EditText editText_content;
    private IAddLabelListener mAddLabelListener;
    private InputMethodManager mImm;


    public AddLabelDialog(Context context, IAddLabelListener addLabelListener) {
        this.mAddLabelListener = addLabelListener;
        this.mContext = context;
        this.mImm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        makeDialog();
    }

    private void makeDialog() {
        mMaterialDialog = new MaterialDialog(mContext);
        mMaterialDialog.setCancelable(true);
        mMaterialDialog.clearTitle().buildCancelButtonText(R.string.cancel).buildSureButtonText(R.string.sure).setAutoDismissEnable(false).buildClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAddLabelListener != null) {
                    String content = editText_content.getText().toString();
                    boolean passChecked = mAddLabelListener.onCheck(content);
                    if (passChecked) {
                        mMaterialDialog.dismiss();
                        mAddLabelListener.onSubmit(content);
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
                editText_content.clearFocus();

                try {
                    if (mImm.isActive() && editText_content.getWindowToken() != null)
                        mImm.hideSoftInputFromWindow(editText_content.getWindowToken(), 0);
                } catch (Exception e) {
                }
            }
        });
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_add_label, null);
        editText_content = contentView.findViewById(R.id.et_content);
        mMaterialDialog.setMessageView(contentView);
        mMaterialDialog.show();
        editText_content.requestFocus();
    }
}
