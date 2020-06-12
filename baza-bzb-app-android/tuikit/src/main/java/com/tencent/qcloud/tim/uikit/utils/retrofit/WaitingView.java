package com.tencent.qcloud.tim.uikit.utils.retrofit;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tencent.qcloud.tim.uikit.R;

public class WaitingView {

    private static WaitingView waitingView;
    private Dialog dialog;

    private WaitingView() {
    }

    public static WaitingView getInstance() {
        if (waitingView == null) {
            waitingView = new WaitingView();
        }
        return waitingView;
    }

    public void showWaitingView(Context context) {

        if (dialog == null) {
            dialog = new Dialog(context, R.style.WaitingDialogStyle);
            View view = LayoutInflater.from(context).inflate(R.layout.view_waiting, null);
            ProgressBar waiting_progress = view.findViewById(R.id.waiting_progress);
            waiting_progress.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_animation));
            dialog.setContentView(view);
        }

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    public void showWaitingView(Context context, String showText, boolean isShowBtn) {

        if (dialog == null) {
            dialog = new Dialog(context, R.style.WaitingDialogStyle);
            View view = LayoutInflater.from(context).inflate(R.layout.view_waiting, null);
            ProgressBar waiting_progress = view.findViewById(R.id.waiting_progress);
            TextView tv_cancel = view.findViewById(R.id.tv_cancel);
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    if (dialog != null) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                }
            });
            if (isShowBtn) {
                tv_cancel.setVisibility(View.VISIBLE);
            } else {
                tv_cancel.setVisibility(View.GONE);
            }
            TextView tipTextView = view.findViewById(R.id.tipTextView);
            tipTextView.setText(showText);
            waiting_progress.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_animation));
            dialog.setContentView(view);
        }

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    public void closeWaitingView() {
        if (dialog != null) {
            dialog.cancel();
            dialog.dismiss();
            dialog = null;
        }
    }
}
