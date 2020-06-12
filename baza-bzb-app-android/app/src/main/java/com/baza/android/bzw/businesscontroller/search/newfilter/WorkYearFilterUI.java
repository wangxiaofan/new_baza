package com.baza.android.bzw.businesscontroller.search.newfilter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.bean.searchfilterbean.WorkYearFilterBean;
import com.baza.android.bzw.extra.ICompanyFilterListener;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2018/6/13.
 * Title：
 * Note：
 */
public class WorkYearFilterUI implements View.OnClickListener {
    private Context mContext;
    private ICompanyFilterListener mFilterListener;
    private View view_root;
    private TextView tvOne, tvTwo, tvThree, tvFour;

    private String[] items;
    private WorkYearFilterBean[] workYearFilters;
    private WorkYearFilterBean mWorkYearFilter;
    private int currentPosition = -1;
    private WorkYearFilterBean defaultFilter;

    WorkYearFilterUI(Context context, ICompanyFilterListener filterListener) {
        this.mContext = context;
        this.mFilterListener = filterListener;
        init();
    }

    public View getView() {
        return view_root;
    }

    private void init() {
        view_root = LayoutInflater.from(mContext).inflate(R.layout.resume_search_layout_filter_work_year, null);
        tvOne = view_root.findViewById(R.id.tv_year_one);
        tvTwo = view_root.findViewById(R.id.tv_year_two);
        tvThree = view_root.findViewById(R.id.tv_year_three);
        tvFour = view_root.findViewById(R.id.tv_year_four);
        tvOne.setOnClickListener(this);
        tvTwo.setOnClickListener(this);
        tvThree.setOnClickListener(this);
        tvFour.setOnClickListener(this);
        items = mContext.getResources().getStringArray(R.array.advance_search_work_years_options);
        workYearFilters = new WorkYearFilterBean[items.length];
        for (int i = 0; i < items.length; i++) {
            workYearFilters[i] = WorkYearFilterBean.createForFilter(items[i], i);
        }
        defaultFilter = WorkYearFilterBean.createForFilter("工作年限", -1);
    }

    public void show(String name) {
        for (int i = 0; i < items.length; i++) {
            if (name.equals(items[i])) {
                setBackground(i);
                currentPosition = i;
            }
        }
        view_root.setVisibility(View.VISIBLE);
    }

    public void hide() {
        view_root.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_year_one:
                if (currentPosition == 0) {
                    setBackground(-1);
                    mWorkYearFilter = defaultFilter;
                } else {
                    setBackground(0);
                    mWorkYearFilter = workYearFilters[0];
                }
                break;
            case R.id.tv_year_two:
                if (currentPosition == 1) {
                    setBackground(-1);
                    mWorkYearFilter = defaultFilter;
                } else {
                    setBackground(1);
                    mWorkYearFilter = workYearFilters[1];
                }
                break;
            case R.id.tv_year_three:
                if (currentPosition == 2) {
                    setBackground(-1);
                    mWorkYearFilter = defaultFilter;
                } else {
                    setBackground(2);
                    mWorkYearFilter = workYearFilters[2];
                }
                break;
            case R.id.tv_year_four:
                if (currentPosition == 3) {
                    setBackground(-1);
                    mWorkYearFilter = defaultFilter;
                } else {
                    setBackground(3);
                    mWorkYearFilter = workYearFilters[3];
                }
                break;
        }

        if (mFilterListener != null)
            mFilterListener.onYearFilterSelected(mWorkYearFilter);
    }

    public void setBackground(int position) {
        currentPosition = position;
        tvOne.setBackgroundResource(R.drawable.shape_search_filter_normal);
        tvOne.setTextColor(Color.parseColor("#4E5968"));
        tvTwo.setBackgroundResource(R.drawable.shape_search_filter_normal);
        tvTwo.setTextColor(Color.parseColor("#4E5968"));
        tvThree.setBackgroundResource(R.drawable.shape_search_filter_normal);
        tvThree.setTextColor(Color.parseColor("#4E5968"));
        tvFour.setBackgroundResource(R.drawable.shape_search_filter_normal);
        tvFour.setTextColor(Color.parseColor("#4E5968"));

        if (position == 0) {
            tvOne.setBackgroundResource(R.drawable.shape_search_filter_checked);
            tvOne.setTextColor(Color.parseColor("#FFFFFF"));
        } else if (position == 1) {
            tvTwo.setBackgroundResource(R.drawable.shape_search_filter_checked);
            tvTwo.setTextColor(Color.parseColor("#FFFFFF"));
        } else if (position == 2) {
            tvThree.setBackgroundResource(R.drawable.shape_search_filter_checked);
            tvThree.setTextColor(Color.parseColor("#FFFFFF"));
        } else if (position == 3) {
            tvFour.setBackgroundResource(R.drawable.shape_search_filter_checked);
            tvFour.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }
}
