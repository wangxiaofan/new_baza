package com.baza.android.bzw.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baza.android.bzw.dao.ActivityPrepareDao;
import com.slib.utils.LoadImageUtil;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2018/2/11.
 * Title：
 * Note：
 */

public class ConfigActivityDialog extends Dialog implements View.OnClickListener {
    private View.OnClickListener mClickListener;
    private ActivityPrepareDao.DialogConfig mDialogConfig;

    public ConfigActivityDialog(@NonNull Context context, ActivityPrepareDao.DialogConfig dialogConfig, View.OnClickListener clickListener) {
        super(context, R.style.customerDialog);
        this.mClickListener = clickListener;
        this.mDialogConfig = dialogConfig;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_config_activity);
        findViewById(R.id.iv_close).setOnClickListener(this);
        ImageView imageView_background = findViewById(R.id.iv_background);
        ImageView imageView_button = findViewById(R.id.iv_button);
        imageView_button.setOnClickListener(this);

        LoadImageUtil.loadImage(mDialogConfig.mBackgroundPath, imageView_background);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mDialogConfig.mButtonPath, options);
        ViewGroup.LayoutParams lp = imageView_button.getLayoutParams();
        lp.width = options.outWidth;
        int maxWidth = ScreenUtil.dip2px(240);
        if (lp.width > maxWidth)
            lp.width = maxWidth;
        imageView_button.setLayoutParams(lp);
        LoadImageUtil.loadImage(mDialogConfig.mButtonPath, imageView_button);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.iv_button:
                if (mClickListener != null)
                    mClickListener.onClick(null);
                break;
        }
    }
}
