package com.baza.android.bzw.widget.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.bznet.android.rcbox.R;
import com.slib.utils.ToastUtil;
import com.slib.utils.string.StringUtil;

import baza.dialog.simpledialog.MaterialDialog;


/**
 * Created by LW on 2016/9/22.
 * Title : 添加或者修改备注的对话框
 * Note :
 */

public class SearchCompanyDialog {

    private Context mContext;
    private MaterialDialog mMaterialDialog;
    private EditText editText_content;
    private Resources mResources;
    private ISearchCompanyEditListener mListener;
    private String mDefaultHelloWord;

    public interface ISearchCompanyEditListener {
        void onReadySearchCompany(String hello);

        void onDismiss();
    }

    public SearchCompanyDialog(Context context, ISearchCompanyEditListener listener, String content) {
        this.mContext = context;
        this.mListener = listener;
        this.mResources = context.getResources();
        this.mDefaultHelloWord = content;
        makeDialog();
    }

    private void makeDialog() {
        mMaterialDialog = new MaterialDialog(mContext);
        mMaterialDialog.setCancelable(false);
        mMaterialDialog.buildTitle("公司").buildCancelButtonText(R.string.cancel).buildSureButtonText(R.string.sure).setAutoDismissEnable(false).buildClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
                if (mListener != null)
                    mListener.onDismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentHelloWord = editText_content.getText().toString().trim();
                mMaterialDialog.dismiss();
                if (mListener != null) {
                    mListener.onReadySearchCompany(currentHelloWord);
                }
            }
        });
        mMaterialDialog.setCanceledOnTouchOutside(false);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_input_company, null);
        editText_content = contentView.findViewById(R.id.et_content);
        if (!StringUtil.isEmpty(mDefaultHelloWord))
            editText_content.setText(mDefaultHelloWord);
        editText_content.setSelection(editText_content.getText().length());
        mMaterialDialog.setMessageView(contentView);
        mMaterialDialog.setNotShowTitle(false);
        mMaterialDialog.show();
    }
}
