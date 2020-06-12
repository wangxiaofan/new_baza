package com.slib.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.slib.toast.GlobeToast;


/**
 * Created by LW on 2016/5/25.
 * Title :
 * Note :
 */
public class ToastUtil {
    private static GlobeToast globeToast;

    public static void showToast(Context context, String message) {

        if (TextUtils.isEmpty(message))
            return;
        if (globeToast == null) {
            synchronized (ToastUtil.class) {
                if (globeToast == null) {
                    globeToast = new GlobeToast(context.getApplicationContext());
                }
            }
        }
        globeToast.show(message);
    }

    public static void showToast(Context context, int resId) {
        if (resId <= 0)
            return;
        if (globeToast == null) {
            synchronized (ToastUtil.class) {
                if (globeToast == null) {
                    globeToast = new GlobeToast(context.getApplicationContext());
                }
            }
        }
        globeToast.show(resId);
    }


    public static void selfToast(Context context, View view) {
        if (view == null)
            return;
        Toast selfViewToast = new Toast(context.getApplicationContext());
        selfViewToast.setGravity(Gravity.CENTER, 0, 0);
        if (view != selfViewToast.getView())
            selfViewToast.setView(view);
        selfViewToast.show();
    }
}
