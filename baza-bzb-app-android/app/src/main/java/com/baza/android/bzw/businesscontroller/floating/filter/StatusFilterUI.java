package com.baza.android.bzw.businesscontroller.floating.filter;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.bean.resume.ResumeStatus;
import com.baza.android.bzw.extra.IFloatingFilterListener;
import com.bznet.android.rcbox.R;
import com.slib.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2018/6/13.
 * Title：
 * Note：
 */
public class StatusFilterUI implements View.OnClickListener {
    private Context mContext;
    private IFloatingFilterListener mFilterListener;
    private View view_root;
    private String[] codes;
    private String[] names;
    private List<ResumeStatus> statusList = new ArrayList<>();
    private int mSelectedPosition = 0;
    private int mColorNormal;
    private int mColorChecked;
    private int itemHeight;
    private int textSize;
    private LinearLayout linearLayout_container;
    private String type;

    StatusFilterUI(Context context, IFloatingFilterListener filterListener, String type) {
        this.mContext = context;
        this.mFilterListener = filterListener;
        this.type = type;
        init();
    }

    public View getView() {
        return view_root;
    }

    private void init() {
        view_root = LayoutInflater.from(mContext).inflate(R.layout.resume_search_layout_filter_status, null);
        linearLayout_container = view_root.findViewById(R.id.ll_container);
        mColorNormal = mContext.getResources().getColor(R.color.text_color_black_4E5968);
        mColorChecked = mContext.getResources().getColor(R.color.text_color_blue_53ABD5);
        itemHeight = (int) mContext.getResources().getDimension(R.dimen.dp_40);
        textSize = ScreenUtil.px2dip(mContext.getResources().getDimension(R.dimen.text_size_12));
        //学历
        codes = mContext.getResources().getStringArray(R.array.floating_status_code);
        if (type.equals("1")) {
            names = mContext.getResources().getStringArray(R.array.floating_status_name_1);
        } else {
            names = mContext.getResources().getStringArray(R.array.floating_status_name);
        }
        for (int i = 0; i < codes.length; i++) {
            statusList.add(new ResumeStatus(Integer.valueOf(codes[i]), names[i]));
        }
        addResumStatus();
    }

    private void addResumStatus() {
        TextView textView;
        for (int i = 0; i < codes.length; i++) {
            textView = new TextView(mContext);
            textView.setTextColor((i == mSelectedPosition ? mColorChecked : mColorNormal));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            textView.setTag(i);
            textView.setText(names[i]);
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            textView.setPadding(50, 0, 0, 0);
            textView.setOnClickListener(this);
            linearLayout_container.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
        }
        changeStatus();
    }

    public void show() {
        view_root.setVisibility(View.VISIBLE);
    }

    public void hide() {
        view_root.setVisibility(View.GONE);
    }

    private void changeStatus() {
        TextView textView;
        for (int i = 0, count = linearLayout_container.getChildCount(); i < count; i++) {
            textView = (TextView) linearLayout_container.getChildAt(i);
            textView.setTextColor((i == mSelectedPosition ? mColorChecked : mColorNormal));
        }
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        if (position == mSelectedPosition)
            return;
        mSelectedPosition = position;
        changeStatus();
        mFilterListener.onStatusSelected(statusList.get(position));
    }
}
