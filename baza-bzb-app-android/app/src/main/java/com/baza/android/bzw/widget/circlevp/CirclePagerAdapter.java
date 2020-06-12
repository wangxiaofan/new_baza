package com.baza.android.bzw.widget.circlevp;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

public abstract class CirclePagerAdapter<T> extends PagerAdapter {
    private List<T> mDataList;

    public CirclePagerAdapter(List<T> bindDataList) {
        updateBindDataList(bindDataList);
    }

    private void updateBindDataList(List<T> bindDataList) {
        if (bindDataList == null || bindDataList.isEmpty())
            return;
        if (mDataList == null)
            mDataList = new ArrayList<>(bindDataList.size() + 2);
        else
            mDataList.clear();
        mDataList.addAll(bindDataList);
        mDataList.add(bindDataList.get(0));
        mDataList.add(0, bindDataList.get(bindDataList.size() - 1));
    }

    @Override
    public final void notifyDataSetChanged() {
//        super.notifyDataSetChanged();
    }

    public void updateDataSetChanged(List<T> bindDataList) {
        updateBindDataList(bindDataList);
        super.notifyDataSetChanged();
    }

    @Override
    public final int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public final boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public final Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = instantiateItemView(getRealPositionByRelative(position));
        container.addView(view);
        return view;
    }

    @Override
    public final void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        destroyItemView((View) object);
    }

    int getRealPositionByRelative(int position) {
        int totalCount = getCount();
        if (position == 0)
            position = totalCount - 3;
        else if (position == totalCount - 1)
            position = 0;
        else
            position--;

        return position;
    }

    int getRelativePositionByPosition(int position) {
        position++;
        return position;
    }

    protected abstract View instantiateItemView(int position);

    protected abstract void destroyItemView(View view);
}
