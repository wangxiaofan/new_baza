package com.baza.android.bzw.businesscontroller.find.updateengine;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2018/8/15.
 * Title：
 * Note：
 */
public class AmountLessDialog extends Dialog implements View.OnClickListener {
    public static final int TYPE_DAYLY = 1;
    public static final int TYPE_EXCHANGE = 2;

    public interface IAmountUpTypeListener {
        void onAmountUpTypeClick(int type);
    }

    private boolean mAmountEnough;
    private boolean mUpDaylyLimitEnable;
    private IAmountUpTypeListener mListener;

    public AmountLessDialog(@NonNull Context context, boolean amountEnough, boolean upDaylyLimitEnable, IAmountUpTypeListener listener) {
        super(context, R.style.customerDialog);
        this.mAmountEnough = amountEnough;
        this.mUpDaylyLimitEnable = upDaylyLimitEnable;
        this.mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_amount_less);
        View mainView = findViewById(R.id.main_view);
        ViewGroup.LayoutParams lp = mainView.getLayoutParams();
        lp.width = (int) (ScreenUtil.screenWidth*0.8f);
        mainView.setLayoutParams(lp);
        if (mAmountEnough)
            findViewById(R.id.tv_title).setVisibility(View.GONE);
        if (!mUpDaylyLimitEnable)
            findViewById(R.id.tv_dayly_up).setVisibility(View.GONE);
        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.tv_dayly_up).setOnClickListener(this);
        findViewById(R.id.tv_exchange_up).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        int id = v.getId();
        if (mListener != null && (id == R.id.tv_dayly_up || id == R.id.tv_exchange_up))
            mListener.onAmountUpTypeClick((id == R.id.tv_dayly_up ? TYPE_DAYLY : TYPE_EXCHANGE));
    }

}
