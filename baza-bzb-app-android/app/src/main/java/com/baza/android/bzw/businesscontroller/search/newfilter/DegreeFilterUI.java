package com.baza.android.bzw.businesscontroller.search.newfilter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.bean.searchfilterbean.DegreeFilterBean;
import com.baza.android.bzw.extra.ICompanyFilterListener;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2018/6/13.
 * Title：
 * Note：
 */
public class DegreeFilterUI implements View.OnClickListener {
    private Context mContext;
    private ICompanyFilterListener mFilterListener;
    private View view_root;
    private TextView tvOne, tvTwo, tvThree, tvFour, tvFive, tvSix;
    private String[] items;
    private DegreeFilterBean[] degreeFilters;
    private DegreeFilterBean mDegreeFilter;
    private int currentPosition = -1;
    private DegreeFilterBean defaultFilter;

    DegreeFilterUI(Context context, ICompanyFilterListener filterListener) {
        this.mContext = context;
        this.mFilterListener = filterListener;
        init();
    }

    public View getView() {
        return view_root;
    }

    private void init() {
        view_root = LayoutInflater.from(mContext).inflate(R.layout.resume_search_layout_filter_degree, null);
        tvOne = view_root.findViewById(R.id.tv_degree_one);
        tvTwo = view_root.findViewById(R.id.tv_degree_two);
        tvThree = view_root.findViewById(R.id.tv_degree_three);
        tvFour = view_root.findViewById(R.id.tv_degree_four);
        tvFive = view_root.findViewById(R.id.tv_degree_five);
        tvSix = view_root.findViewById(R.id.tv_degree_six);
        tvOne.setOnClickListener(this);
        tvTwo.setOnClickListener(this);
        tvThree.setOnClickListener(this);
        tvFour.setOnClickListener(this);
        tvFive.setOnClickListener(this);
        tvSix.setOnClickListener(this);
        //学历
        items = mContext.getResources().getStringArray(R.array.degree_level);
        degreeFilters = new DegreeFilterBean[items.length];
        for (int i = 0; i < items.length; i++) {
            degreeFilters[i] = DegreeFilterBean.createForFilter(items[i], i);
        }
        defaultFilter = DegreeFilterBean.createForFilter("学历", -1);
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
            case R.id.tv_degree_one:
                if (currentPosition == 0) {
                    setBackground(-1);
                    mDegreeFilter = defaultFilter;
                } else {
                    setBackground(0);
                    mDegreeFilter = degreeFilters[0];
                }
                break;
            case R.id.tv_degree_two:
                if (currentPosition == 1) {
                    setBackground(-1);
                    mDegreeFilter = defaultFilter;
                } else {
                    setBackground(1);
                    mDegreeFilter = degreeFilters[1];
                }
                break;
            case R.id.tv_degree_three:
                if (currentPosition == 2) {
                    setBackground(-1);
                    mDegreeFilter = defaultFilter;
                } else {
                    setBackground(2);
                    mDegreeFilter = degreeFilters[2];
                }
                break;
            case R.id.tv_degree_four:
                if (currentPosition == 3) {
                    setBackground(-1);
                    mDegreeFilter = defaultFilter;
                } else {
                    setBackground(3);
                    mDegreeFilter = degreeFilters[3];
                }
                break;
            case R.id.tv_degree_five:
                if (currentPosition == 4) {
                    setBackground(-1);
                    mDegreeFilter = defaultFilter;
                } else {
                    setBackground(4);
                    mDegreeFilter = degreeFilters[4];
                }
                break;
            case R.id.tv_degree_six:
                if (currentPosition == 5) {
                    setBackground(-1);
                    mDegreeFilter = defaultFilter;
                } else {
                    setBackground(5);
                    mDegreeFilter = degreeFilters[5];
                }
                break;
        }
        if (mFilterListener != null)
            mFilterListener.onMoreFilterSelected(null, mDegreeFilter, null, null, null);
    }

    private void setBackground(int position) {
        currentPosition = position;
        tvOne.setBackgroundResource(R.drawable.shape_search_filter_normal);
        tvOne.setTextColor(Color.parseColor("#4E5968"));
        tvTwo.setBackgroundResource(R.drawable.shape_search_filter_normal);
        tvTwo.setTextColor(Color.parseColor("#4E5968"));
        tvThree.setBackgroundResource(R.drawable.shape_search_filter_normal);
        tvThree.setTextColor(Color.parseColor("#4E5968"));
        tvFour.setBackgroundResource(R.drawable.shape_search_filter_normal);
        tvFour.setTextColor(Color.parseColor("#4E5968"));
        tvFive.setBackgroundResource(R.drawable.shape_search_filter_normal);
        tvFive.setTextColor(Color.parseColor("#4E5968"));
        tvSix.setBackgroundResource(R.drawable.shape_search_filter_normal);
        tvSix.setTextColor(Color.parseColor("#4E5968"));

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
        } else if (position == 4) {
            tvFive.setBackgroundResource(R.drawable.shape_search_filter_checked);
            tvFive.setTextColor(Color.parseColor("#FFFFFF"));
        } else if (position == 5) {
            tvSix.setBackgroundResource(R.drawable.shape_search_filter_checked);
            tvSix.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }
}
