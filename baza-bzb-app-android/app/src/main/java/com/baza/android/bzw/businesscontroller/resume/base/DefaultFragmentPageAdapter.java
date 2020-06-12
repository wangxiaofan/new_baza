package com.baza.android.bzw.businesscontroller.resume.base;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/3/9.
 * Title：
 * Note：
 */

public class DefaultFragmentPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;
    private String[] mPageTitles;

    public DefaultFragmentPageAdapter(List<Fragment> fragmentList, String[] pageTitles, FragmentManager fm) {
        super(fm);
        this.mFragmentList = fragmentList;
        this.mPageTitles = pageTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return (mFragmentList != null ? mFragmentList.size() : 0);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return (mPageTitles != null ? mPageTitles[position] : null);
    }
}
