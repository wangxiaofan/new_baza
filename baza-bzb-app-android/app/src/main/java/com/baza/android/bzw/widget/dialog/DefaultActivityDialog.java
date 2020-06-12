package com.baza.android.bzw.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2018/2/11.
 * Title：
 * Note：
 */

public class DefaultActivityDialog extends Dialog implements View.OnClickListener {

    public static class Param {
        public int backgroundImgId;
        public int buttonImgId;
        public String link;
        public int id;
        public boolean hideCloseBtn;
    }

    public interface IConfirmListener {
        void onClose(Param param);

        void onConfirm(Param param);
    }

    private IConfirmListener mListener;
    private Param mParam;

    public DefaultActivityDialog(@NonNull Context context, Param param, IConfirmListener listener) {
        super(context, R.style.customerDialog);
        this.mListener = listener;
        this.mParam = param;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_email_activity);
        ImageView imageView = findViewById(R.id.iv_background);
        imageView.setImageResource(mParam.backgroundImgId);
        View closeBtn = findViewById(R.id.iv_close);
        if (mParam.hideCloseBtn) {
            closeBtn.setVisibility(View.GONE);
        }
        closeBtn.setOnClickListener(this);
        imageView = findViewById(R.id.iv_button);
        imageView.setBackgroundResource(mParam.buttonImgId);
        imageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.iv_close:
                if (mListener != null)
                    mListener.onClose(mParam);
                break;
            case R.id.iv_button:
                if (mListener != null)
                    mListener.onConfirm(mParam);
                break;
        }
    }
}
