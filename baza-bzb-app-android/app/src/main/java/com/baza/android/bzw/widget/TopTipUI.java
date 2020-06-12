package com.baza.android.bzw.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;


/**
 * Created by Vincent.Lei on 2017/3/27.
 * Title :
 * Note :
 */

public class TopTipUI extends PopupWindow implements View.OnClickListener {
    public interface IMenuClickListener {
        void onTipMenuClick(int menuIndex);
    }

    private final int[] mLocation = new int[2];

    public void setMenuClickListener(IMenuClickListener mMenuClickListener) {
        this.mMenuClickListener = mMenuClickListener;
    }

    private IMenuClickListener mMenuClickListener;
    private Context mContext;
    private LinearLayout linearLayout_menuContainer;

    public TopTipUI(Context context, IMenuClickListener mMenuClickListener) {
        super(context);
        this.mContext = context;
        this.mMenuClickListener = mMenuClickListener;
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        this.setAnimationStyle(R.style.tipAnimation);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        linearLayout_menuContainer = new LinearLayout(context);
        linearLayout_menuContainer.setBackgroundResource(R.drawable.bg_tip);

        setContentView(linearLayout_menuContainer);
    }

    public void show(View v) {
        v.getLocationOnScreen(mLocation);
        showAtLocation(v, Gravity.NO_GRAVITY, mLocation[0], mLocation[1] - 60);
    }

    public void show(View v, int xOffset, int yOffset) {
        v.getLocationOnScreen(mLocation);
        showAtLocation(v, Gravity.NO_GRAVITY, mLocation[0] + xOffset, mLocation[1] + yOffset);
    }

    public void updateMenus(String[] menuItem) {
        if (menuItem == null || menuItem.length == 0)
            return;
        int currentChildCount = linearLayout_menuContainer.getChildCount();
        int needShowCount = menuItem.length;
        boolean hasCache;
        for (int i = 0; i < needShowCount; i++) {
            hasCache = i < currentChildCount;
            TextView tv = (TextView) (hasCache ? linearLayout_menuContainer.getChildAt(i) : createMenuView());
            tv.setText(menuItem[i]);
            tv.setOnClickListener(this);
            tv.setTag(i);
            ViewGroup.LayoutParams lp = tv.getLayoutParams();
            if (needShowCount == 1) {
                if (lp == null)
                    lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.width = ScreenUtil.dip2px(60);
            } else if (lp != null) {
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            if (lp != null)
                tv.setLayoutParams(lp);
            tv.setVisibility(View.VISIBLE);
            if (!hasCache)
                linearLayout_menuContainer.addView(tv);
        }
        //多余的缓存控件要隐藏
        int needBeHided = currentChildCount - needShowCount;
        if (needBeHided > 0) {
            for (int i = needShowCount; i < currentChildCount; i++)
                linearLayout_menuContainer.getChildAt(i).setVisibility(View.GONE);
        }
    }

    private TextView createMenuView() {
        TextView textView = new TextView(mContext);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setText(R.string.delete);
        textView.setPadding(10, 0, 10, 0);
        return textView;
    }

    public void updateMenuClick(IMenuClickListener mMenuClickListener){
        this.mMenuClickListener = mMenuClickListener;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (mMenuClickListener != null)
            mMenuClickListener.onTipMenuClick((int) v.getTag());
    }
}
