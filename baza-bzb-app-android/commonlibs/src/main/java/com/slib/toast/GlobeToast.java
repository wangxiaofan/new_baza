package com.slib.toast;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baza.android.slib.R;


/**
 * Created by LW on 2016/5/25.
 * Title :
 * Note :
 */
public class GlobeToast {
    private TextView tv;
    private Toast mToast;

    public GlobeToast(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.zz_toast_layout, null);
        tv = view.findViewById(R.id.tv);
//		AppUtil.resetViewSize(tv, AppUtil.getDeviceScreenWidth(context) - AppUtil.dp2px(context, 20), tv.getLayoutParams().height);
        mToast = new Toast(context);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setView(view);
    }

    public void show(int msgId) {
        tv.setText(msgId);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

    public void show(String msg) {
        tv.setText(msg);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }


}
