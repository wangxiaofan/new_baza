package com.baza.android.bzw.widget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bznet.android.rcbox.R;

import baza.dialog.simpledialog.MaterialDialog;

/**
 * Created by Vincent.Lei on 2017/3/29.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class SimpleHintBDialog implements View.OnClickListener {
    private Context mContext;
    private MaterialDialog mMaterialDialog;
    private View.OnClickListener mOnClickListenerSure;

    public SimpleHintBDialog(Context context) {
        this.mContext = context;
    }


    public void show(String title, CharSequence msg, int drawableId, View.OnClickListener mOnClickListenerSure) {
        this.mOnClickListenerSure = mOnClickListenerSure;
        mMaterialDialog = new MaterialDialog(mContext);
        mMaterialDialog.setCancelable(true);
        mMaterialDialog.clearTitle()
                .buildButtonCount(0)
                .setWidthPercentOfScreen(0.8f)
                .clearLineBetweenButtonsAndMessage();
        View view_main = LayoutInflater.from(mContext).inflate(R.layout.dialog_simple_b_hint, null);
        TextView textView = view_main.findViewById(R.id.tv_title);
        textView.setText(title);
        textView = view_main.findViewById(R.id.tv_msg);
        textView.setText(msg);
        ImageView imageView = view_main.findViewById(R.id.iv_image);
        imageView.setImageResource(drawableId);
        textView = view_main.findViewById(R.id.tv_sure);
        textView.setOnClickListener(this);
        mMaterialDialog.setMessageView(view_main);
        mMaterialDialog.show();
    }

    @Override
    public void onClick(View v) {
        mMaterialDialog.dismiss();
        switch (v.getId()) {
            case R.id.tv_sure:
                if (mOnClickListenerSure != null)
                    mOnClickListenerSure.onClick(v);
                break;
        }
    }
}
