package com.baza.android.bzw.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SideBar extends View {
    // 触摸事件
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    private int choose = -1;// 选中
    private Paint paint = new Paint();
    private String[] mItems;
    private TextView mTextDialog;
    private int mTextColorNormal, mTextColorTouched;
    private int mTextSize = 30;

    /**
     * 为SideBar设置显示字母的TextView
     */
    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public void setClassifyValue(int mTextColorNormal, int mTextColorTouched, String[] mItems) {
        this.mItems = mItems;
        this.mTextColorNormal = mTextColorNormal;
        this.mTextColorTouched = mTextColorTouched;
        invalidate();
    }

    public void setClassifyTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBar(Context context) {
        super(context);
    }

    /**
     * 重写这个方法
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mItems == null || mItems.length == 0)
            return;
        // 获取焦点改变背景颜色.
        int height = getHeight();// 获取对应高度
        int width = getWidth(); // 获取对应宽度
        int singleHeight = height / mItems.length;// 获取每一个字母的高度

        for (int i = 0; i < mItems.length; i++) {
            paint.setColor(mTextColorNormal);
            // paint.setColor(Color.WHITE);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(mTextSize);
            // 选中的状态
            if (i == choose) {
                paint.setColor(mTextColorTouched);
                paint.setFakeBoldText(true);
            }
            // x坐标等于中间-字符串宽度的一半.
            float xPos = width / 2 - paint.measureText(mItems[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(mItems[i], xPos, yPos, paint);
            paint.reset();// 重置画笔
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击y坐标
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * mItems.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

        switch (action) {
            case MotionEvent.ACTION_UP:
//                setBackgroundColor(Color.parseColor("0x00000000"));
                choose = -1;//
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;

            default:
//                setBackgroundResource(R.drawable.sidebar_background);
                if (oldChoose != c) {
                    if (c >= 0 && c < mItems.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(mItems[c]);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(mItems[c]);
                            mTextDialog.setVisibility(View.VISIBLE);
                        }

                        choose = c;
                        invalidate();
                    }
                }

                break;
        }
        return true;
    }

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }

}