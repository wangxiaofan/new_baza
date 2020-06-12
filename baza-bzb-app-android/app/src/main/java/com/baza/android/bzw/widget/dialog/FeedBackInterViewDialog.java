package com.baza.android.bzw.widget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.bznet.android.rcbox.R;
import com.slib.utils.ToastUtil;
import com.slib.utils.string.StringUtil;

import baza.dialog.simpledialog.MaterialDialog;


/**
 * Created by LW on 2016/9/22.
 * Title : 安排面试
 * Note :
 */

public class FeedBackInterViewDialog {
    private Context mContext;
    private MaterialDialog mMaterialDialog;
    private IFeedBackListener mListener;
    private CheckBox cb_one, cb_two, cb_three, cb_four, cb_five, cb_six, cb_share;
    private EditText et_content;
    private String result = "0";
    private String type;

    public interface IFeedBackListener {
        void onFeedBack(String type, String result, String evaluation, String sharedResult);
    }

    public FeedBackInterViewDialog(Context context, IFeedBackListener listener) {
        this.mContext = context;
        this.mListener = listener;
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
                if (mListener != null) {
                    if (cb_one.isChecked()) {
                        type = "0";
                    } else if (cb_two.isChecked()) {
                        type = "1";
                    } else {
                        ToastUtil.showToast(mContext, "请选择面试类型");
                        return;
                    }

                    if (cb_three.isChecked()) {
                        result = "1";
                    } else if (cb_four.isChecked()) {
                        result = "2";
                    } else if (cb_five.isChecked()) {
                        result = "3";
                    } else if (cb_six.isChecked()) {
                        result = "4";
                    } else {
                        ToastUtil.showToast(mContext, "请选择面试结果");
                        return;
                    }

                    if (StringUtil.isEmpty(et_content.getText().toString())) {
                        ToastUtil.showToast(mContext, "请输入面试评价");
                        return;
                    }

                    mListener.onFeedBack(type, result, et_content.getText().toString(), cb_share.isChecked() ? "true" : "false");
                }
                mMaterialDialog.dismiss();
            }
        });
        mMaterialDialog.setCanceledOnTouchOutside(true);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_feedback_interview, null);
        cb_one = contentView.findViewById(R.id.cb_one);
        cb_two = contentView.findViewById(R.id.cb_two);
        cb_three = contentView.findViewById(R.id.cb_three);
        cb_four = contentView.findViewById(R.id.cb_four);
        cb_five = contentView.findViewById(R.id.cb_five);
        cb_six = contentView.findViewById(R.id.cb_six);
        cb_share = contentView.findViewById(R.id.cb_share);
        et_content = contentView.findViewById(R.id.et_content);

        cb_one.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) cb_two.setChecked(false);
            }
        });
        cb_two.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) cb_one.setChecked(false);
            }
        });
        cb_three.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    cb_four.setChecked(false);
                    cb_five.setChecked(false);
                    cb_six.setChecked(false);
                }
            }
        });
        cb_four.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb_three.setChecked(false);
                cb_five.setChecked(false);
                cb_six.setChecked(false);
            }
        });
        cb_five.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb_four.setChecked(false);
                cb_three.setChecked(false);
                cb_six.setChecked(false);
            }
        });
        cb_six.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb_four.setChecked(false);
                cb_five.setChecked(false);
                cb_three.setChecked(false);
            }
        });


        mMaterialDialog.setMessageView(contentView);
        mMaterialDialog.show();
    }
}
