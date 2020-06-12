package com.baza.android.bzw.businesscontroller.resume.detail;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bznet.android.rcbox.R;
import com.slib.utils.ScreenUtil;


/**
 * Created by Vincent.Lei on 2017/1/9.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class RemarkTipsPopupWindow extends PopupWindow implements View.OnClickListener {

    public interface ITipsMenuClickListener {
        void onTipClick(int menuPosition);
    }

    private Context mContext;

    public void setMenuClickListener(ITipsMenuClickListener mListener) {
        this.mListener = mListener;
    }

    private ITipsMenuClickListener mListener;
    private View mainView;
    private LinearLayout linearLayout_container;
    private final int[] mLocation = new int[2];
    private int mItemWidth, mWidth;

    public RemarkTipsPopupWindow(Context context, ITipsMenuClickListener listener) {
        super(context);
        this.mContext = context;
        this.mListener = listener;
        mainView = LayoutInflater.from(context).inflate(R.layout.layout_remark_tip_popupwindow, null);
        linearLayout_container = mainView.findViewById(R.id.ll_menu_container);
        this.setContentView(mainView);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mItemWidth = ScreenUtil.dip2px(80);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setAnimationStyle(R.style.tipAnimation);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
    }

    public void show(View v) {
        v.getLocationOnScreen(mLocation);
        showAtLocation(v, Gravity.NO_GRAVITY, mLocation[0] - mWidth - 20, mLocation[1]);
    }

    public void setUpMenus(String[] items) {
        if (items == null || items.length == 0)
            return;
        int currentMenuCount = linearLayout_container.getChildCount();
        int needShowCount = items.length;
        int lastShowPosition = needShowCount - 1;
        FrameLayout linearLayout_menuItem;
        boolean hasCacheChild;
        for (int i = 0; i < needShowCount; i++) {
            hasCacheChild = i < currentMenuCount;
            linearLayout_menuItem = (FrameLayout) (hasCacheChild ? linearLayout_container.getChildAt(i) : LayoutInflater.from(mContext).inflate(R.layout.layout_tip_menu_item, null));
            ((TextView) linearLayout_menuItem.getChildAt(0)).setText(items[i]);
            linearLayout_menuItem.getChildAt(1).setVisibility(i == lastShowPosition ? View.GONE : View.VISIBLE);
            linearLayout_menuItem.setOnClickListener(this);
            linearLayout_menuItem.setTag(i);
            linearLayout_menuItem.setVisibility(View.VISIBLE);
            if (!hasCacheChild)
                linearLayout_container.addView(linearLayout_menuItem, new FrameLayout.LayoutParams(mItemWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        if (currentMenuCount > needShowCount) {
            for (int i = needShowCount; i < currentMenuCount; i++) {
                linearLayout_container.getChildAt(i).setVisibility(View.GONE);
            }
        }
        mWidth = mItemWidth * needShowCount;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (mListener != null)
            mListener.onTipClick((int) v.getTag());
    }
}
