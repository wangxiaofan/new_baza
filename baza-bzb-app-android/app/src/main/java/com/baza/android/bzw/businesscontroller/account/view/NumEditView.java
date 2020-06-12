package com.baza.android.bzw.businesscontroller.account.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2018/9/4.
 * Title：
 * Note：
 */
public class NumEditView extends FrameLayout implements View.OnClickListener {
    public interface INumEditListener {
        boolean isNextAddEnable(NumEditView numEditView, int currentValue, int nextValue);

        boolean isNextMinusEnable(NumEditView numEditView, int currentValue, int nextValue);

        void onNumChanged(int currentValue);
    }

    private TextView textView_countValue;
    ImageView imageView_add;
    ImageView imageView_minus;
    private int mCurrentValue;
    private int mStepCount = 1;
    private INumEditListener mNumEditListener;

    public void setNumEditListener(INumEditListener numEditListener) {
        this.mNumEditListener = numEditListener;
    }

    public int getCurrentValue() {
        return mCurrentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.mCurrentValue = currentValue;
        textView_countValue.setText(String.valueOf(mCurrentValue));
    }

    public int getStepCount() {
        return mStepCount;
    }

    public void setStepCount(int stepCount) {
        this.mStepCount = stepCount;
    }


    public NumEditView(@NonNull Context context) {
        this(context, null);
    }

    public NumEditView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumEditView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view_main = LayoutInflater.from(context).inflate(R.layout.view_inner_num_edit, this);
        textView_countValue = view_main.findViewById(R.id.tv_count_value);
        imageView_add = findViewById(R.id.iv_add);
        imageView_minus = findViewById(R.id.iv_minus);
        imageView_add.setOnClickListener(this);
        imageView_minus.setOnClickListener(this);
    }

    public void setEditEnable(boolean enable) {
        imageView_add.setEnabled(enable);
        imageView_add.setClickable(enable);
        imageView_minus.setEnabled(enable);
        imageView_minus.setClickable(enable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                if (mNumEditListener == null || mNumEditListener.isNextAddEnable(this, mCurrentValue, mCurrentValue + mStepCount)) {
                    mCurrentValue += mStepCount;
                    textView_countValue.setText(String.valueOf(mCurrentValue));
                }
                break;
            case R.id.iv_minus:
                if (mNumEditListener == null || mNumEditListener.isNextMinusEnable(this, mCurrentValue, mCurrentValue - mStepCount)) {
                    mCurrentValue -= mStepCount;
                    textView_countValue.setText(String.valueOf(mCurrentValue));
                }
                break;
        }
        if (mNumEditListener != null)
            mNumEditListener.onNumChanged(mCurrentValue);
    }
}
