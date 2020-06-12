package com.baza.android.bzw.widget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bznet.android.rcbox.R;
import com.slib.utils.DateUtil;
import com.slib.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import baza.dialog.simpledialog.MaterialDialog;


/**
 * Created by LW on 2016/9/22.
 * Title : 安排面试
 * Note :
 */

public class OfferDialog implements CompoundButton.OnCheckedChangeListener {
    private Context mContext;
    private MaterialDialog mMaterialDialog;
    private IOfferListener mListener;
    private TextView tv_date;
    private LinearLayout llDate;
    private CheckBox cbOne, cbTwo, cbThree;
    private List<CheckBox> cb_list = new ArrayList<>();
    private String selectDate = "";
    private String selectMonth = "90";


    public interface IOfferListener {
        void onReadyOffer(String date, String month);
    }

    public OfferDialog(Context context, IOfferListener listener) {
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
                if (!cbOne.isChecked() && !cbTwo.isChecked() && !cbThree.isChecked()) {
                    ToastUtil.showToast(mContext, "请选择保证期");
                    return;
                }

                if (selectDate.equals("")) {
                    ToastUtil.showToast(mContext, "请选择入职时间");
                    return;
                }

                mMaterialDialog.dismiss();
                if (mListener != null) {
                    mListener.onReadyOffer(selectDate, selectMonth);
                }
            }
        });
        mMaterialDialog.setCanceledOnTouchOutside(false);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_offer, null);
        llDate = contentView.findViewById(R.id.ll_date);
        tv_date = contentView.findViewById(R.id.tv_date);
        cbOne = contentView.findViewById(R.id.cb_one);
        cbTwo = contentView.findViewById(R.id.cb_two);
        cbThree = contentView.findViewById(R.id.cb_three);
        cb_list.add(cbOne);
        cb_list.add(cbTwo);
        cb_list.add(cbThree);
        cbOne.setOnCheckedChangeListener(this);
        cbTwo.setOnCheckedChangeListener(this);
        cbThree.setOnCheckedChangeListener(this);

        llDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RecommendDateSetDialog(mContext, new Date(), new RecommendDateSetDialog.ITimeSelectedListener() {
                    @Override
                    public void onTimeSelected(int year, int month, int day, String hm) {
                        selectDate = DateUtil.formatTimeSelected(year, month, day, "");
                        tv_date.setText(selectDate);
                    }
                }).show();
            }
        });

        mMaterialDialog.setMessageView(contentView);
        mMaterialDialog.show();
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            for (int i = 0; i < cb_list.size(); i++) {
                //不等于当前选中的就变成false
                if (cb_list.get(i).getText().toString().equals(compoundButton.getText().toString())) {
                    cb_list.get(i).setChecked(true);
                    if (i == 0) {
                        selectMonth = "0";
                    } else if (i == 1) {
                        selectMonth = "90";
                    } else if (i == 2) {
                        selectMonth = "180";
                    }
                } else {
                    cb_list.get(i).setChecked(false);
                }
            }
        }
    }
}
