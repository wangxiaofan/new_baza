package com.baza.android.bzw.businesscontroller.message;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baza.android.bzw.businesscontroller.find.updateengine.ResumeEnableUpdateListActivity;
import com.bznet.android.rcbox.R;
import com.slib.utils.SnackbarUtil;

/**
 * Created by Vincent.Lei on 2018/1/16.
 * Title：
 * Note：
 */

public class MessageExtraUI {
    public static void showResumeEnableUpdateAmountChangeSnackMessage(final Activity activity, Resources resources, final int amount) {
        View add_view = activity.getLayoutInflater().inflate(R.layout.snackbar_layout_out_of_request_share_count, null);
        TextView textView = add_view.findViewById(R.id.tv_msg);
        textView.setText(resources.getString(R.string.resume_enable_update_amount_get_by_share, String.valueOf(amount)));
        Button button = add_view.findViewById(R.id.btn_get_more);
        button.setText(R.string.go_check_it_put);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResumeEnableUpdateListActivity.launch(activity);
            }
        });
        SnackbarUtil.indefiniteSdShow(activity, add_view, 5000);
    }

    public static void showTextSnackMessage(final Activity activity, String message) {
        View view = activity.getLayoutInflater().inflate(R.layout.snack_layout_resume_enable_update_amount_change, null);
        TextView textView = view.findViewById(R.id.tv);
        textView.setText(message);
        SnackbarUtil.shortSdShow(activity, view);
    }
}
