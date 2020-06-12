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
 * Created by Vincent.Lei on 2016/11/21.
 * Title : 简历详情查看更多悬浮菜单
 * Note :
 */

public class ListPopupWindow extends PopupWindow implements View.OnClickListener {

    private LinearLayout linearLayout_menu_container;
    private int xOffset = -1, yOffset;
    private Context context;
    private IMenuClickListener menuClickListener;
    private int textSize;
    private int textColor = R.color.text_color_blue_0D315C;
    private int itemHeight;

    public interface IMenuClickListener {
        void onMenuClick(int position);
    }

    private ListPopupWindow(Context context) {
        super(context);
    }

    public ListPopupWindow(Context context, int textSize, int itemHeight, String[] menuItem, IMenuClickListener menuClickListener) {
        this(context);
        this.context = context;
        this.menuClickListener = menuClickListener;
        this.textSize = textSize;
        this.itemHeight = itemHeight;
        this.textSize = textSize;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View mainView = layoutInflater.inflate(R.layout.layout_list_popupwindow, null);
        mainView.setOnClickListener(this);
        linearLayout_menu_container = mainView.findViewById(R.id.ll_menu_container);
        updateMenus(menuItem);

        this.setContentView(mainView);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.AnimScaleRightTopInAndOut);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
    }

    public ListPopupWindow(Context context, String[] menuItem, IMenuClickListener menuClickListener) {
        this(context, ScreenUtil.px2dip(context.getResources().getDimension(R.dimen.text_size_14)), (int) context.getResources().getDimension(R.dimen.dp_50), menuItem, menuClickListener);
    }

    public void updateMenus(String[] menuItem) {
        if (menuItem == null || menuItem.length == 0)
            return;
        int currentChildCount = linearLayout_menu_container.getChildCount();
        int needShowCount = menuItem.length;
        boolean hasCache;
        for (int i = 0; i < needShowCount; i++) {
            hasCache = i < currentChildCount;
            TextView tv = (TextView) (hasCache ? linearLayout_menu_container.getChildAt(i) : createMenu());
            tv.setText(menuItem[i]);
            tv.setOnClickListener(this);
            tv.setTag(i);
            if (!hasCache)
                linearLayout_menu_container.addView(tv);
        }
        //多余的缓存控件要隐藏
        int needBeHided = currentChildCount - needShowCount;
        if (needBeHided > 0) {
            for (int i = needShowCount; i < currentChildCount; i++)
                linearLayout_menu_container.getChildAt(i).setVisibility(View.GONE);
        }
    }

    private TextView createMenu() {
        TextView textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView.setTextColor(context.getResources().getColor(textColor));
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.default_title_line_background);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
        return textView;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (menuClickListener != null)
            menuClickListener.onMenuClick((int) v.getTag());
    }

    public void show(View anchor) {
        if (anchor == null)
            return;
        if (xOffset == -1) {
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            xOffset = location[0];
            yOffset = location[1] + anchor.getHeight();
        }
        showAtLocation(anchor, Gravity.NO_GRAVITY, xOffset, yOffset);
    }
}
