package com.tencent.qcloud.tim.uikit.utils;

import android.widget.ListView;

/**
 * * @Description: scrollview 中内嵌 listview 的简单实现
 * * @File: ScrollViewWithListView.java
 * * * @Version
 */
public class ScrollViewWithListView extends ListView {

    private boolean isOnMeasure;

    public boolean isMeasure() {
        return isOnMeasure;
    }


    public ScrollViewWithListView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Integer.MAX_VALUE >> 2,如果不设置，系统默认设置是显示两条
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        isOnMeasure = true;
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        isOnMeasure = false;
        super.onLayout(changed, l, t, r, b);
    }
}