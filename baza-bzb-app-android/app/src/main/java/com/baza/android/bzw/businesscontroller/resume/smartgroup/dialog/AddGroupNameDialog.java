package com.baza.android.bzw.businesscontroller.resume.smartgroup.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import com.slib.utils.AppUtil;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import baza.dialog.simpledialog.MaterialDialog;

/**
 * Created by Vincent.Lei on 2018/8/31.
 * Title：
 * Note：
 */
public class AddGroupNameDialog {
    private MaterialDialog materialDialog;
    private IGroupNameSetListener mListener;
    private EditText editText;

    public interface IGroupNameSetListener {
        void onGroupNameSet(String name);

        boolean isGroupNameEnable(String name);
    }

    public AddGroupNameDialog(Context context, String oldName, IGroupNameSetListener listener) {
        this.materialDialog = new MaterialDialog(context);
        this.mListener = listener;
        Resources resources = context.getResources();
        editText = new EditText(context);
        AppUtil.modifyEditTextCursorDrawable(editText, R.drawable.edit_text_default_cursor);
        editText.setTextSize(ScreenUtil.px2dip(resources.getDimension(R.dimen.text_size_14)));
        editText.setTextColor(resources.getColor(R.color.text_color_blue_0D315C));
        editText.setHintTextColor(resources.getColor(R.color.text_color_grey_94A1A5));
        editText.setHint(R.string.input_hint_group_name);
        editText.setMinHeight((int) resources.getDimension(R.dimen.dp_50));
        editText.setGravity(Gravity.LEFT | Gravity.TOP);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        editText.setBackgroundDrawable(null);
        if (!TextUtils.isEmpty(oldName)) {
            editText.setText(oldName);
            editText.setSelection(oldName.length());
        }
        materialDialog.setAutoDismissEnable(false);
        materialDialog.buildTitle(0).buildClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    String groupName = editText.getText().toString().trim();
                    if (mListener.isGroupNameEnable(groupName)) {
                        dismiss();
                        mListener.onGroupNameSet(groupName);
                    }
                    return;
                }
                dismiss();
            }
        });
        materialDialog.setMessageView(editText);
        materialDialog.setCancelable(false);
        materialDialog.show();
    }

    public void show() {
        try {
            materialDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        try {
            materialDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
