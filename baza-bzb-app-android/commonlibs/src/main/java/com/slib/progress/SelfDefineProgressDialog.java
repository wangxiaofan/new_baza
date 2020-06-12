package com.slib.progress;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baza.android.slib.R;


public class SelfDefineProgressDialog {
    private Dialog mDialog;
    private TextView tv;

    public SelfDefineProgressDialog(Context context) {
        mDialog = new Dialog(context, R.style.customerDialog);
        View localView = LayoutInflater.from(context).inflate(R.layout.zz_progress_dialog, null);
        ProgressBar mProgressBar = localView.findViewById(R.id.mpb);
        tv = localView.findViewById(R.id.tv);
        IndeterminateProgressDrawable indeterminateProgressDrawable = new IndeterminateProgressDrawable(context);
        indeterminateProgressDrawable.setTint(context.getResources().getColor(R.color.main_progress));
        mProgressBar.setIndeterminateDrawable(indeterminateProgressDrawable);
        mDialog.setContentView(localView);
        mDialog.setCanceledOnTouchOutside(false);
    }


    public void setCancelable(boolean cancelAble) {
        if (mDialog != null) {
            mDialog.setCancelable(cancelAble);
        }
    }

    public void show(String msg) {
        if (!isShowing()) {
            try {
                if (msg != null)
                    tv.setText(msg);
                mDialog.show();
            } catch (Exception e) {
                //ignore
            }

        }
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public void dismiss() {
        if (isShowing()) {
            try {
                mDialog.dismiss();
                //菊花延迟500毫秒消失
//                tv.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }, 500);

            } catch (Exception e) {
            }

        }
    }
}
