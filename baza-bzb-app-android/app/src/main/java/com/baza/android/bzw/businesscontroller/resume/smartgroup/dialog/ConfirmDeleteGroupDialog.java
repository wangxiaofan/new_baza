package com.baza.android.bzw.businesscontroller.resume.smartgroup.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.baza.android.bzw.constant.SharedPreferenceConst;
import com.slib.storage.sharedpreference.SharedPreferenceManager;
import com.bznet.android.rcbox.R;

import baza.dialog.simpledialog.MaterialDialog;

/**
 * Created by Vincent.Lei on 2018/8/31.
 * Title：
 * Note：
 */
public class ConfirmDeleteGroupDialog {
    private MaterialDialog materialDialog;
    private CheckBox checkBox;
    private View.OnClickListener onClickListener;

    public ConfirmDeleteGroupDialog(Context context, View.OnClickListener clickListener) {
        this.onClickListener = clickListener;
        this.materialDialog = new MaterialDialog(context);
        View messageView = LayoutInflater.from(context).inflate(R.layout.smart_group_dialog_delete_confirm, null);
        checkBox = messageView.findViewById(R.id.cb);
        materialDialog.buildTitle(0).buildClickListener(null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(null);
                if (checkBox.isChecked())
                    SharedPreferenceManager.saveBoolean(SharedPreferenceConst.SP_SMART_GROUP_DELETE_HINT_TOGGLE, true);
            }
        });
        materialDialog.setMessageView(messageView);
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
