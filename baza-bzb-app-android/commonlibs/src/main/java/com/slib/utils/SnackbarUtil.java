package com.slib.utils;

import android.app.Activity;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Vincent.Lei on 2018/1/12.
 * Title：
 * Note：
 */

public class SnackbarUtil {
    public static Snackbar shortSdShow(Activity activity, View view) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.removeAllViews();
        snackbarLayout.setPadding(0, 0, 0, 0);
        snackbarLayout.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        snackbar.show();
        return snackbar;
    }

    public static Snackbar indefiniteSdShow(Activity activity, View view, long duration) {
        final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), "", Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.removeAllViews();
        snackbarLayout.setPadding(0, 0, 0, 0);
        snackbarLayout.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        snackbar.show();
        activity.getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
            }
        }, duration);
        return snackbar;
    }

}
