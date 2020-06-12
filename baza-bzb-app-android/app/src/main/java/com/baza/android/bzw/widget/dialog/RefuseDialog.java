package com.baza.android.bzw.widget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bznet.android.rcbox.R;
import com.slib.utils.ToastUtil;
import com.slib.utils.string.StringUtil;

import java.util.ArrayList;
import java.util.List;

import baza.dialog.simpledialog.MaterialDialog;


/**
 * Created by LW on 2016/9/22.
 * Title : 添加或者修改备注的对话框
 * Note :
 */

public class RefuseDialog implements CompoundButton.OnCheckedChangeListener {
    private Context mContext;
    private MaterialDialog mMaterialDialog;
    private IRefuseListener mListener;
    private EditText editText_content;
    private LinearLayout llContent;
    private TextView tv_title;
    private List<CheckBox> cb_list = new ArrayList<>();
    private List<String> contentList = new ArrayList<>();
    private String title;
    private boolean canAllSelect;

    public interface IRefuseListener {
        void onReadyRefuse(String rejectReason, String detail);
    }

    public RefuseDialog(Context context, IRefuseListener listener, List<String> list, String title, boolean canAllSelect) {
        this.mContext = context;
        this.mListener = listener;
        this.contentList = list;
        this.title = title;
        this.canAllSelect = canAllSelect;
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
                    StringBuilder stringBuilder = new StringBuilder();
                    for (CheckBox cb : cb_list) {
                        if (cb.isChecked()) {
                            stringBuilder.append(cb.getText()).append(",");
                        }
                    }
                    if (StringUtil.isEmpty(stringBuilder.toString())) {
                        if (title.contains("淘汰")) {
                            ToastUtil.showToast(mContext, "请选择淘汰理由");
                        } else {
                            ToastUtil.showToast(mContext, "请选择拒绝理由");
                        }
                        return;
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);

                    if (StringUtil.isEmpty(editText_content.getText().toString())) {
                        if (title.contains("淘汰")) {
                            ToastUtil.showToast(mContext, "请填写淘汰原因");
                        } else {
                            ToastUtil.showToast(mContext, "请填写拒绝原因");
                        }
                        return;
                    }
                    if (editText_content.getText().toString().trim().length() < 4) {
                        if (title.contains("淘汰")) {
                            ToastUtil.showToast(mContext, "请填写4-200字淘汰原因");
                        } else {
                            ToastUtil.showToast(mContext, "请填写4-200字拒绝原因");
                        }
                        return;
                    }

                    mListener.onReadyRefuse(stringBuilder.toString(), editText_content.getText().toString());
                }
                mMaterialDialog.dismiss();
            }
        });
        mMaterialDialog.setCanceledOnTouchOutside(true);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_refuse, null);
        editText_content = contentView.findViewById(R.id.et_content);
        editText_content.setHint("填写更多原因");
        editText_content.setSelection(editText_content.getText().length());
        llContent = contentView.findViewById(R.id.ll_conent);
        tv_title = contentView.findViewById(R.id.tv_title);
        tv_title.setText(title);
        addCheckBox();

        mMaterialDialog.setMessageView(contentView);
        mMaterialDialog.show();
    }

    private void addCheckBox() {
        for (String str : contentList) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_check_layout_refuse, null);
            CheckBox cb = view.findViewById(R.id.cb_content);
            cb.setText(str);
            cb_list.add(cb);
            if (!canAllSelect) {
                cb.setOnCheckedChangeListener(this);
            }
            llContent.addView(view);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            for (int i = 0; i < cb_list.size(); i++) {
                //不等于当前选中的就变成false
                if (cb_list.get(i).getText().toString().equals(compoundButton.getText().toString())) {
                    cb_list.get(i).setChecked(true);
                } else {
                    cb_list.get(i).setChecked(false);
                }
            }
        }
    }
}
