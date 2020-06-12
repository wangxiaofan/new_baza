package com.baza.android.bzw.businesscontroller.floating.filter;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.extra.IFloatingFilterListener;
import com.bznet.android.rcbox.R;
import com.slib.utils.ScreenUtil;

/**
 * Created by Vincent.Lei on 2018/6/13.
 * Title：
 * Note：
 */
public class FloatingSortFilterUI implements View.OnClickListener {
    private Context mContext;
    private IFloatingFilterListener mFilterListener;
    private int mColorNormal;
    private int mColorChecked;
    private int mSelectedPosition = 1;
    private String[] items;
    private View view_root;
    private LinearLayout linearLayout_container;

    FloatingSortFilterUI(Context context, IFloatingFilterListener filterListener) {
        this.mContext = context;
        this.mFilterListener = filterListener;
        init();
    }

    public View getView() {
        return view_root;
    }

    private void init() {
        mColorNormal = mContext.getResources().getColor(R.color.text_color_black_4E5968);
        mColorChecked = mContext.getResources().getColor(R.color.text_color_blue_53ABD5);
        view_root = LayoutInflater.from(mContext).inflate(R.layout.resyme_search_layout_filter_sort, null);
        linearLayout_container = view_root.findViewById(R.id.ll_container);
        items = mContext.getResources().getStringArray(R.array.floating_filter_sort);
        int itemHeight = (int) mContext.getResources().getDimension(R.dimen.dp_40);
        int textSize = ScreenUtil.px2dip(mContext.getResources().getDimension(R.dimen.text_size_12));
        TextView textView;
        for (int i = 0; i < items.length; i++) {
            textView = new TextView(mContext);
            textView.setTextColor((i == mSelectedPosition ? mColorChecked : mColorNormal));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            textView.setTag(i);
            textView.setText(items[i]);
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            textView.setPadding(50, 0, 0, 0);
            textView.setOnClickListener(this);
            linearLayout_container.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
        }
        changeStatus();
    }

    private void changeStatus() {
        TextView textView;
        for (int i = 0, count = linearLayout_container.getChildCount(); i < count; i++) {
            textView = (TextView) linearLayout_container.getChildAt(i);
            textView.setTextColor((i == mSelectedPosition ? mColorChecked : mColorNormal));
        }
    }

    public void show() {
        view_root.setVisibility(View.VISIBLE);
    }

    public void hide() {
        view_root.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (position == mSelectedPosition)
            return;
        mSelectedPosition = position;
        changeStatus();

        mFilterListener.onSortSelected(mSelectedPosition, items[mSelectedPosition]);
    }
}