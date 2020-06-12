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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2018/6/13.
 * Title：
 * Note：
 */
public class JobFilterUI implements View.OnClickListener {
    private Context mContext;
    private IFloatingFilterListener mFilterListener;
    private View view_root;
    private List<String> jobList = new ArrayList<>();
    private int mSelectedPosition = 0;
    private int mColorNormal;
    private int mColorChecked;
    private int itemHeight;
    private int textSize;
    private LinearLayout linearLayout_container;
    private String currentJob;

    JobFilterUI(Context context, IFloatingFilterListener filterListener) {
        this.mContext = context;
        this.mFilterListener = filterListener;
        init();
    }

    public View getView() {
        return view_root;
    }

    private void init() {
        view_root = LayoutInflater.from(mContext).inflate(R.layout.resume_search_layout_filter_job, null);
        linearLayout_container = view_root.findViewById(R.id.ll_container);
        mColorNormal = mContext.getResources().getColor(R.color.text_color_black_4E5968);
        mColorChecked = mContext.getResources().getColor(R.color.text_color_blue_53ABD5);
        itemHeight = (int) mContext.getResources().getDimension(R.dimen.dp_40);
        textSize = ScreenUtil.px2dip(mContext.getResources().getDimension(R.dimen.text_size_12));
    }

    private void addResumStatus() {
        TextView textView;
        for (int i = 0; i < jobList.size(); i++) {
            if (currentJob.equals(jobList.get(i))) {
                mSelectedPosition = i;
            }
            textView = new TextView(mContext);
            textView.setTextColor((i == mSelectedPosition ? mColorChecked : mColorNormal));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            textView.setTag(i);
            textView.setText(jobList.get(i));
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            textView.setPadding(50, 0, 0, 0);
            textView.setOnClickListener(this);
            linearLayout_container.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
        }
    }

    public void show(List<String> jobs, String job) {
        this.jobList = jobs;
        this.currentJob = job;
        linearLayout_container.removeAllViews();
        addResumStatus();
        changeStatus();
        view_root.setVisibility(View.VISIBLE);
    }

    private void changeStatus() {
        TextView textView;
        for (int i = 0, count = linearLayout_container.getChildCount(); i < count; i++) {
            textView = (TextView) linearLayout_container.getChildAt(i);
            textView.setTextColor((i == mSelectedPosition ? mColorChecked : mColorNormal));
        }
    }

    public void hide() {
        view_root.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        if (position == mSelectedPosition)
            return;
        mSelectedPosition = position;
        changeStatus();
        mFilterListener.onJobSelected(jobList.get(position));
    }
}
