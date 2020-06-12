package com.baza.android.bzw.extra;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import com.google.android.material.tabs.TabLayout;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2018/8/22.
 * Title：
 * Note：
 */
public class TabLayoutTextTabWrapper {
    private TabLayout mTabLayout;
    private String[] mPageTitles;
    private int mSelectedPosition;
    private int mColorNormal;
    private int mColorSelected;
    private static Drawable mDrawableSelected;
    private static Drawable mDrawableNormal;

    public TabLayoutTextTabWrapper(TabLayout tabLayout, String[] mPageTitles) {
        this.mTabLayout = tabLayout;
        this.mPageTitles = mPageTitles;
        Resources resources = mTabLayout.getContext().getResources();
        this.mColorNormal = resources.getColor(R.color.text_color_black_4E5968);
        this.mColorSelected = resources.getColor(R.color.text_color_blue_53ABD5);
        if (mDrawableSelected == null) {
            mDrawableSelected = resources.getDrawable(R.drawable.indicator_selected);
            mDrawableSelected.setBounds(0, 0, mDrawableSelected.getIntrinsicWidth(), mDrawableSelected.getIntrinsicHeight());
            mDrawableNormal = resources.getDrawable(R.drawable.tab_indicator_trans);
            mDrawableNormal.setBounds(0, 0, mDrawableNormal.getIntrinsicWidth(), mDrawableNormal.getIntrinsicHeight());
        }

        setUpTabs();
    }

    private void setUpTabs() {
        int count = mTabLayout.getTabCount();
        TabLayout.Tab tab;
        TextView textView;
        LayoutInflater layoutInflater = LayoutInflater.from(mTabLayout.getContext());
        for (int i = 0; i < count; i++) {
            tab = mTabLayout.getTabAt(i);
            textView = (TextView) layoutInflater.inflate(R.layout.tablayout_text_tab, null);
            textView.setText(mPageTitles[i]);
            textView.setTextColor((i == mSelectedPosition) ? mColorSelected : mColorNormal);
            textView.setCompoundDrawables(null, null, null, (i == mSelectedPosition ? mDrawableSelected : mDrawableNormal));
            tab.setCustomView(textView);
        }
    }

    public void setSelection(int position) {
        if (position == mSelectedPosition)
            return;
        mSelectedPosition = position;
        int count = mTabLayout.getTabCount();
        TextView textView;
        for (int i = 0; i < count; i++) {
            textView = (TextView) mTabLayout.getTabAt(i).getCustomView();
            textView.setText(mPageTitles[i]);
            textView.setTextColor((i == mSelectedPosition) ? mColorSelected : mColorNormal);
            textView.setCompoundDrawables(null, null, null, (i == mSelectedPosition ? mDrawableSelected : mDrawableNormal));
        }
    }

    public void updateText(int position) {
        TextView textView = (TextView) mTabLayout.getTabAt(position).getCustomView();
        textView.setText(mPageTitles[position]);
    }
}
