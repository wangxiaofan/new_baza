package com.baza.android.bzw.businesscontroller.find.updateengine.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

/**
 * Created by Vincent.Lei on 2017/11/7.
 * Title：
 * Note：
 */

public class UpdateCardListAdapter extends FragmentStatePagerAdapter {
    public interface IDataAndViewProvider {
        int getCount();

        Fragment instantiateItem(int position);

        void destroyItem(int position, Fragment outOfDate);
    }

    private IDataAndViewProvider mDataAndViewProvider;

    public UpdateCardListAdapter(FragmentManager fm, IDataAndViewProvider dataAndViewProvider) {
        super(fm);
        this.mDataAndViewProvider = dataAndViewProvider;
    }

    @Override
    public Fragment getItem(int position) {
        return mDataAndViewProvider.instantiateItem(position);
    }

    @Override
    public int getCount() {
        return mDataAndViewProvider.getCount();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mDataAndViewProvider.destroyItem(position, (Fragment) object);
    }
}
