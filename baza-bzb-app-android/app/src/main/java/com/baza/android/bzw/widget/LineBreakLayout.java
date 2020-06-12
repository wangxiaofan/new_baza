package com.baza.android.bzw.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.bznet.android.rcbox.R;


/**
 * Created by LW on 2016/10/27.
 * Title :
 * Note :
 */

public class LineBreakLayout extends ViewGroup {
    private int horizontalDepart;
    private int verticalDepart;
    private int lineLimit = Integer.MAX_VALUE;

    public LineBreakLayout(Context context) {
        this(context, null);
    }

    public LineBreakLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setHorizontalDepart(int horizontalDepart) {
        this.horizontalDepart = horizontalDepart;
        requestLayout();
    }

    public void setVerticalDepart(int verticalDepart) {
        this.verticalDepart = verticalDepart;
        requestLayout();
    }

    public LineBreakLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LineBreakLayout);
            horizontalDepart = (int) ta.getDimension(R.styleable.LineBreakLayout_horizontalDepart, 0);
            verticalDepart = (int) ta.getDimension(R.styleable.LineBreakLayout_verticalDepart, 0);
            lineLimit = ta.getInt(R.styleable.LineBreakLayout_lineLimit, lineLimit);
            ta.recycle();
        }
    }

    public void setLineLimit(int lineLimit) {
        this.lineLimit = lineLimit;
        requestLayout();
    }

    public int getLineLimit() {
        return lineLimit;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //为所有的标签childView计算宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        //获取高的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //建议的高度
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //布局的宽度采用建议宽度（match_parent或者size），如果设置wrap_content也是match_parent的效果
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            //如果高度模式为EXACTLY（match_perent或者size），则使用建议高度
            height = heightSize;
        } else {
            //其他情况下（AT_MOST、UNSPECIFIED）需要计算计算高度
            int childCount = getChildCount();
            if (childCount <= 0 || lineLimit == 0) {
                height = getPaddingTop() + getPaddingBottom();   //没有标签时，高度为0
            } else {
                int row = 1;  // 标签行数
                int childW;
                View view;
                int widthSpace = width - getPaddingRight() - getPaddingLeft();// 当前行右侧剩余的宽度
                for (int i = 0; i < childCount; i++) {
                    view = getChildAt(i);
                    if (view.getVisibility() == View.GONE)
                        continue;
                    //获取标签宽度
                    childW = view.getMeasuredWidth();
                    if (widthSpace >= childW) {
                        //如果剩余的宽度大于此标签的宽度，那就将此标签放到本行
                        widthSpace -= childW;
                    } else {
                        if (row >= lineLimit)
                            break;
                        row++;    //增加一行
                        //如果剩余的宽度不能摆放此标签，那就将此标签放入一行
                        widthSpace = width - childW - getPaddingRight() - getPaddingLeft();
                    }
                    //减去标签左右间距
                    widthSpace -= horizontalDepart;
                }
                //由于每个标签的高度是相同的，所以直接获取第一个标签的高度即可
                int childH = getChildAt(0).getMeasuredHeight();
                //最终布局的高度=标签高度*行数+行距*(行数-1)
                height = (childH * row) + verticalDepart * (row - 1) + getPaddingTop() + getPaddingBottom();
            }
        }
        //设置测量宽度和测量高度
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int row = 0;
        int right = 0;   // 标签相对于布局的右侧位置
        int bottom;       // 标签相对于布局的底部位置
        r = getMeasuredWidth() - getPaddingRight();
        right += getPaddingLeft();
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == View.GONE)
                continue;
            int childW = childView.getMeasuredWidth();
            int childH = childView.getMeasuredHeight();
            //右侧位置=本行已经占有的位置+当前标签的宽度
            right += childW;
            // 如果右侧位置已经超出布局右边缘，跳到下一行
            // if it can't drawing on a same line , skip to next line
            if (right > r) {
                if (row >= lineLimit)
                    break;
                row++;
                right = childW + getPaddingLeft();
            }
            bottom = row * verticalDepart + childH * (row + 1) + getPaddingTop();
            childView.layout(right - childW, bottom - childH, right, bottom);

            right += horizontalDepart;
        }
    }
}
