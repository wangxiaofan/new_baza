package com.baza.android.bzw.widget.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.bznet.android.rcbox.R;
import com.slib.utils.string.StringUtil;

import baza.dialog.simpledialog.MaterialDialog;


/**
 * Created by LW on 2016/9/22.
 * Title : 添加或者修改备注的对话框
 * Note :
 */

public class SearchMajorDialog {

    private Context mContext;
    private MaterialDialog mMaterialDialog;
    private EditText editText_content;
    private Resources mResources;
    private ISearchMajorEditListener mListener;
    private String mDefaultHelloWord;

    public interface ISearchMajorEditListener {
        void onReadySearchMajor(String hello);

        void onDismiss();
    }

    public SearchMajorDialog(Context context, ISearchMajorEditListener listener, String content) {
        this.mContext = context;
        this.mListener = listener;
        this.mResources = context.getResources();
        mDefaultHelloWord = content;
        makeDialog();
    }

    private void makeDialog() {
        mMaterialDialog = new MaterialDialog(mContext);
        mMaterialDialog.setCancelable(false);
        mMaterialDialog.buildTitle("职位").buildCancelButtonText(R.string.cancel).buildSureButtonText(R.string.sure).setAutoDismissEnable(false).buildClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
                if (mListener != null) {
                    mListener.onDismiss();
                }
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
                if (mListener != null) {
                    String currentHelloWord = editText_content.getText().toString().trim();
                    mListener.onReadySearchMajor(currentHelloWord);
                }
            }
        });
        mMaterialDialog.setCanceledOnTouchOutside(false);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_input_major, null);
        editText_content = contentView.findViewById(R.id.et_content);
        if (!StringUtil.isEmpty(mDefaultHelloWord))
            editText_content.setText(mDefaultHelloWord);
        editText_content.setSelection(editText_content.getText().length());
        mMaterialDialog.setMessageView(contentView);
        mMaterialDialog.setNotShowTitle(false);
        mMaterialDialog.show();
    }
}
