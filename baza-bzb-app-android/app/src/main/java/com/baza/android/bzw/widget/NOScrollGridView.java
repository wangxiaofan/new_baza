package com.baza.android.bzw.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Vincent.Lei on 2017/1/17.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class NOScrollGridView extends GridView {
    public NOScrollGridView(Context context) {
        this(context, null);
    }

    public NOScrollGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NOScrollGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
