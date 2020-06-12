package com.baza.android.bzw.businesscontroller.search.newfilter;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.extra.ICompanyFilterListener;
import com.bznet.android.rcbox.R;
import com.slib.utils.ScreenUtil;
import com.slib.utils.string.StringUtil;

/**
 * Created by Vincent.Lei on 2018/6/13.
 * Title：
 * Note：
 */
public class TrackingSortFilterUI implements View.OnClickListener {
    private Context mContext;
    private ICompanyFilterListener mFilterListener;
    private int mColorNormal;
    private int mColorChecked;
    private int mSelectedPosition;

    private View view_root;
    private LinearLayout linearLayout_container;

    TrackingSortFilterUI(Context context, ICompanyFilterListener filterListener) {
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
        String[] items = mContext.getResources().getStringArray(R.array.tracking_search_sort_mine);
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
    }

    private void changeStatus() {
        TextView textView;
        for (int i = 0, count = linearLayout_container.getChildCount(); i < count; i++) {
            textView = (TextView) linearLayout_container.getChildAt(i);
            textView.setTextColor((i == mSelectedPosition ? mColorChecked : mColorNormal));
        }
    }

    public void show(String mSortOutSide) {
        if (!StringUtil.isEmpty(mSortOutSide)) {
            switch (mSortOutSide) {
                case "OperationTime":
                    mSelectedPosition = 1;
                    break;
                case "AddTrackingListDate":
                    mSelectedPosition = 2;
                    break;
                default:
                    mSelectedPosition = 0;
                    break;
            }
            changeStatus();
        }
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
        if (mSelectedPosition == 0) {
            mFilterListener.onSortOrderSelected(0);
        } else if (mSelectedPosition == 1) {
            mFilterListener.onSortOrderSelected(1);
        } else if (mSelectedPosition == 2) {
            mFilterListener.onSortOrderSelected(2);
        }
    }
}
