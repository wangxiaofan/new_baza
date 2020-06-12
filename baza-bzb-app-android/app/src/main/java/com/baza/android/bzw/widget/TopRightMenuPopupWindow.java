package com.baza.android.bzw.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/5/19.
 * Title：
 * Note：
 */

public class TopRightMenuPopupWindow extends PopupWindow implements View.OnClickListener {
    public interface IMenuSelectedListener {
        void onMenuClicked(int position);
    }

    private LinearLayout linearLayout_menuContainer;
    private Context mContext;
    private IMenuSelectedListener mListener;
    private int mMenuColor;
    private int xOffset = -1, yOffset;

    public TopRightMenuPopupWindow(Context context, IMenuSelectedListener mListener) {
        this(context, 0, 0, mListener);
    }

    public TopRightMenuPopupWindow(Context context, int backgroundDrawableId, int menuColor, IMenuSelectedListener mListener) {
        super(context);
        this.mListener = mListener;
        this.mContext = context;
        this.mMenuColor = menuColor;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View mainView = layoutInflater.inflate(R.layout.popupwindow_top_right_menu, null);
        this.setContentView(mainView);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.AnimScaleRightTopInAndOut);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        if (backgroundDrawableId > 0)
            mainView.setBackgroundResource(backgroundDrawableId);
        linearLayout_menuContainer = mainView.findViewById(R.id.ll_container);
    }


    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            default:
                if (mListener != null)
                    mListener.onMenuClicked((int) v.getTag());
                break;
        }
    }

    public void show(View anchor, String[] menus) {
        if (anchor == null)
            return;
        if (xOffset == -1) {
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            xOffset = location[0] - ScreenUtil.dip2px(120) + anchor.getWidth() / 2 + 10;
            yOffset = location[1] + anchor.getHeight();
        }
        updateMenu(menus);
        showAtLocation(anchor, Gravity.NO_GRAVITY, xOffset, yOffset);
    }

    private void updateMenu(String[] menus) {
        if (menus == null || menus.length == 0) {
            linearLayout_menuContainer.removeAllViews();
            return;
        }
        int needShowCount = menus.length;
        int currentCount = linearLayout_menuContainer.getChildCount();
        TextView textView;
        boolean hasCache;
        for (int i = 0; i < needShowCount; i++) {
            hasCache = (i < currentCount);
            textView = (hasCache ? (TextView) linearLayout_menuContainer.getChildAt(i) : createMenu());
            textView.setText(menus[i]);
            textView.setTag(i);
            textView.setOnClickListener(this);
            textView.setVisibility(View.VISIBLE);
            if (!hasCache)
                linearLayout_menuContainer.addView(textView);
        }
        if (needShowCount < currentCount) {
            for (; needShowCount < currentCount; needShowCount++) {
                linearLayout_menuContainer.getChildAt(needShowCount).setVisibility(View.GONE);
            }
        }
    }

    private TextView createMenu() {
        TextView textView = new TextView(mContext);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setGravity(Gravity.CENTER);
        if (mMenuColor != 0)
            textView.setTextColor(mMenuColor);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ScreenUtil.dip2px(80), ScreenUtil.dip2px(30)));
        return textView;
    }
}
