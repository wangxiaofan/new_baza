package com.baza.android.bzw.businesscontroller.publish;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/11/24.
 * Title：
 * Note：
 */

public class QRCodeLoginConfirmPopupWindow extends PopupWindow implements View.OnClickListener {
    public interface IQRCodeLoginConfirmListener {
        void onLoginConfirm(boolean enableLogin);
    }

    private IQRCodeLoginConfirmListener mLoginConfirmListener;

    public QRCodeLoginConfirmPopupWindow(@NonNull Context context, IQRCodeLoginConfirmListener loginConfirmListener) {
        super(context);
        this.mLoginConfirmListener = loginConfirmListener;
        View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_qr_code_login_confirm, null);
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.AnimBottomInBottomOut);
        ColorDrawable dw = new ColorDrawable(0xff000000);
        this.setBackgroundDrawable(dw);
        rootView.findViewById(R.id.iv_close).setOnClickListener(this);
        rootView.findViewById(R.id.tv_cancel_login).setOnClickListener(this);
        rootView.findViewById(R.id.tv_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.iv_close:
            case R.id.tv_cancel_login:
                if (mLoginConfirmListener != null)
                    mLoginConfirmListener.onLoginConfirm(false);
                break;
            case R.id.tv_login:
                if (mLoginConfirmListener != null)
                    mLoginConfirmListener.onLoginConfirm(true);
                break;
        }
    }

    public void showWithActivity(Activity activity) {
        showAtLocation(activity.getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);
    }
}
