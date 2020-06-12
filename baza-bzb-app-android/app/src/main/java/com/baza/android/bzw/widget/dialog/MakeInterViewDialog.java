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
import com.slib.utils.DateUtil;
import com.slib.utils.ToastUtil;
import com.slib.utils.string.StringUtil;

import java.util.Date;

import baza.dialog.simpledialog.MaterialDialog;


/**
 * Created by LW on 2016/9/22.
 * Title : 安排面试
 * Note :
 */

public class MakeInterViewDialog implements View.OnClickListener {
    private Context mContext;
    private MaterialDialog mMaterialDialog;
    private IReceiveListener mListener;
    private TextView tv_name, tv_major, tv_date, tv_contact_name;
    private CheckBox cb_one, cb_two, cb_send_msg;
    private LinearLayout ll_date;
    private EditText et_address, et_remark;
    private String userName, contactName, major;
    private String interviewType = "";
    private String interviewTime = "";

    public interface IReceiveListener {
        void onReadyReceive(String interviewType, String interviewTime, String interviewAddress, String remark, String shouldSendNotification);
    }

    public MakeInterViewDialog(Context context, IReceiveListener listener, String userName, String contactName, String major) {
        this.mContext = context;
        this.mListener = listener;
        this.userName = userName;
        this.contactName = contactName;
        this.major = major;
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
                    if (!cb_one.isChecked() && !cb_two.isChecked()) {
                        ToastUtil.showToast(mContext, "请选择面试方式");
                        return;
                    }
                    if (cb_one.isChecked()) {
                        interviewType = "0";
                    } else if (cb_two.isChecked()) {
                        interviewType = "1";
                    }

                    if (StringUtil.isEmpty(interviewTime)) {
                        ToastUtil.showToast(mContext, "请选择面试时间");
                        return;
                    }

                    if (StringUtil.isEmpty(et_address.getText().toString())) {
                        ToastUtil.showToast(mContext, "请填写面试地点");
                        return;
                    }

                    mListener.onReadyReceive(interviewType, tv_date.getText().toString(), et_address.getText().toString(),
                            et_remark.getText().toString(), cb_send_msg.isChecked() ? "true" : "false");
                }
                mMaterialDialog.dismiss();
            }
        });
        mMaterialDialog.setCanceledOnTouchOutside(true);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_make_interview, null);
        tv_name = contentView.findViewById(R.id.tv_name);
        tv_major = contentView.findViewById(R.id.tv_major);
        tv_date = contentView.findViewById(R.id.tv_date);
        tv_contact_name = contentView.findViewById(R.id.tv_contact_name);
        cb_one = contentView.findViewById(R.id.cb_one);
        cb_two = contentView.findViewById(R.id.cb_two);
        cb_send_msg = contentView.findViewById(R.id.cb_send_msg);
        ll_date = contentView.findViewById(R.id.ll_date);
        et_address = contentView.findViewById(R.id.et_address);
        et_remark = contentView.findViewById(R.id.et_remark);

        tv_name.setText(userName);
        tv_contact_name.setText(contactName);
        tv_major.setText(major);
        ll_date.setOnClickListener(this);
        cb_one.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    cb_two.setChecked(false);
            }
        });
        cb_two.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    cb_one.setChecked(false);
            }
        });


        mMaterialDialog.setMessageView(contentView);
        mMaterialDialog.show();
    }

    @Override
    public void onClick(View view) {
        new RecommendDateSetDialog(mContext, new Date(), new RecommendDateSetDialog.ITimeSelectedListener() {
            @Override
            public void onTimeSelected(int year, int month, int day, String hm) {
                interviewTime = DateUtil.formatTimeSelected(year, month, day, hm) + ":00";
                tv_date.setText(interviewTime);
            }
        }).show();
    }
}
