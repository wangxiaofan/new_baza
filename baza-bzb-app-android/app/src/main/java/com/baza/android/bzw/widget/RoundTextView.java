package com.baza.android.bzw.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/5/24.
 * Title：
 * Note：
 */

public class RoundTextView extends TextView {
    private int mBackgroundColor = Color.TRANSPARENT;
    private int mBorderColor = Color.WHITE;
    private int mBorderWidth;
    private Paint mPaint;

    public RoundTextView(Context context) {
        this(context, null);
    }

    public RoundTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = attrs == null ? null : getContext().obtainStyledAttributes(attrs, R.styleable.RoundTextView);
        if (ta != null) {
            mBackgroundColor = ta.getColor(R.styleable.RoundTextView_rbColor, mBackgroundColor);
            mBorderColor = ta.getColor(R.styleable.RoundTextView_rb_borderColor, mBorderColor);
            mBorderWidth = (int) ta.getDimension(R.styleable.RoundTextView_rb_borderWidth, 0.0f);
            ta.recycle();
        }
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int r = (width > height ? height : width) / 2;
        if (mBorderWidth > 0) {
            mPaint.setColor(mBorderColor);
            canvas.drawCircle(width / 2, height / 2, r, mPaint);
        }
        mPaint.setColor(mBackgroundColor);
        canvas.drawCircle(width / 2, height / 2, r - mBorderWidth, mPaint);
        super.onDraw(canvas);
    }
}
