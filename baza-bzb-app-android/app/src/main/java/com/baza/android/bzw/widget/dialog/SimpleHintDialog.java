package com.baza.android.bzw.widget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bznet.android.rcbox.R;

import baza.dialog.simpledialog.MaterialDialog;

/**
 * Created by Vincent.Lei on 2017/3/29.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class SimpleHintDialog implements View.OnClickListener {
    private Context mContext;
    private MaterialDialog mMaterialDialog;
    private View.OnClickListener mOnClickListenerSure, mOnClickListenerCancel;

    public SimpleHintDialog(Context context) {
        this.mContext = context;
    }

    public void show(String title, int drawableId, View.OnClickListener mOnClickListenerSure, View.OnClickListener mOnClickListenerCancel) {
        show(title, null, drawableId, 0, 0, mOnClickListenerSure, mOnClickListenerCancel);
    }

    public void show(String title, int drawableId, int mSureButtonTextId, int mCancelButtonTextId, View.OnClickListener mOnClickListenerSure, View.OnClickListener mOnClickListenerCancel) {
        show(title, null, drawableId, mSureButtonTextId, mCancelButtonTextId, mOnClickListenerSure, mOnClickListenerCancel);
    }

    public void show(String title, CharSequence msg, int drawableId, int mSureButtonTextId, int mCancelButtonTextId, View.OnClickListener mOnClickListenerSure, View.OnClickListener mOnClickListenerCancel) {
        this.mOnClickListenerSure = mOnClickListenerSure;
        this.mOnClickListenerCancel = mOnClickListenerCancel;
        mMaterialDialog = new MaterialDialog(mContext);
        mMaterialDialog.setCancelable(true);
        mMaterialDialog.clearTitle()
                .buildButtonCount(0)
                .setWidthPercentOfScreen(0.8f)
                .clearLineBetweenButtonsAndMessage();
        View view_main = LayoutInflater.from(mContext).inflate(R.layout.dialog_simple_hint, null);
        TextView textView = view_main.findViewById(R.id.tv_title);
        if (title != null)
            textView.setText(title);
        else
            textView.setVisibility(View.GONE);
        if (msg != null) {
            textView = view_main.findViewById(R.id.tv_msg);
            textView.setText(msg);
            textView.setVisibility(View.VISIBLE);
        }
        ImageView imageView = view_main.findViewById(R.id.iv_image);
        if (drawableId > 0) {
            imageView.setImageResource(drawableId);
        } else
            imageView.setVisibility(View.GONE);
        Button button = view_main.findViewById(R.id.btn_cancel);
        button.setOnClickListener(this);
        if (mCancelButtonTextId > 0)
            button.setText(mCancelButtonTextId);

        button = view_main.findViewById(R.id.btn_sure);
        button.setOnClickListener(this);
        if (mSureButtonTextId > 0)
            button.setText(mSureButtonTextId);
        mMaterialDialog.setMessageView(view_main);
        try {
            mMaterialDialog.show();
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        mMaterialDialog.dismiss();
        switch (v.getId()) {
            case R.id.btn_sure:
                if (mOnClickListenerSure != null)
                    mOnClickListenerSure.onClick(v);
                break;
            case R.id.btn_cancel:
                if (mOnClickListenerCancel != null)
                    mOnClickListenerCancel.onClick(v);
                break;
        }
    }
}
