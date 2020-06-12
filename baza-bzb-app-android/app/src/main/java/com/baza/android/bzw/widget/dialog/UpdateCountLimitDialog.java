package com.baza.android.bzw.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;


/**
 * Created by LW on 2016/9/22.
 * Title : 更新反馈的对话框
 * Note :
 */

public class UpdateCountLimitDialog extends Dialog implements View.OnClickListener {
    public interface IUpdateCountLimitListener {
        void onLearnMoreClick();
    }

    private IUpdateCountLimitListener mLimitListener;

    public UpdateCountLimitDialog(Context context, IUpdateCountLimitListener limitListener) {
        super(context, R.style.customerDialog);
        this.mLimitListener = limitListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout_update_count_limit);
        findViewById(R.id.iv_closed).setOnClickListener(this);
        findViewById(R.id.btn_update_grade).setOnClickListener(this);
        View mainView = findViewById(R.id.main_view);
        ViewGroup.LayoutParams lp = mainView.getLayoutParams();
        lp.width = (int) (ScreenUtil.screenWidth * 0.8f);
        mainView.setLayoutParams(lp);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.btn_update_grade:
                if (mLimitListener != null)
                    mLimitListener.onLearnMoreClick();
                break;
        }
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            //ignore
        }
    }
}
