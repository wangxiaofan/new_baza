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

public class DefaultDoubleBtnActivityDialog extends Dialog implements View.OnClickListener {
    public static class Param {
        public int backgroundImgId;
        public int leftButtonImgId;
        public int rightButtonImgId;
        public String link;
        public int id;
    }


    public interface IConfirmListener {
        void onClose(Param param);

        void onLeftButtonClick(Param param);

        void onRightButtonClick(Param param);
    }

    private Param mParam;
    private IConfirmListener mListener;

    public DefaultDoubleBtnActivityDialog(@NonNull Context context, Param param, IConfirmListener listener) {
        super(context, R.style.customerDialog);
        this.mListener = listener;
        this.mParam = param;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sign_in_activity);
        ImageView imageView = findViewById(R.id.iv_background);
        imageView.setImageResource(mParam.backgroundImgId);
        findViewById(R.id.iv_close).setOnClickListener(this);
        imageView = findViewById(R.id.iv_button_left);
        imageView.setBackgroundResource(mParam.leftButtonImgId);
        imageView.setOnClickListener(this);
        imageView = findViewById(R.id.iv_button_right);
        imageView.setBackgroundResource(mParam.rightButtonImgId);
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
            case R.id.iv_button_left:
                if (mListener != null)
                    mListener.onLeftButtonClick(mParam);
                break;
            case R.id.iv_button_right:
                if (mListener != null)
                    mListener.onRightButtonClick(mParam);
                break;
        }
    }
}
