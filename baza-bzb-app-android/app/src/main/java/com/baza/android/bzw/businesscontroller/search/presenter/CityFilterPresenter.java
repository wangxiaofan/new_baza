package com.baza.android.bzw.businesscontroller.search.presenter;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.SparseArray;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.common.LocalAreaBean;
import com.baza.android.bzw.bean.searchfilterbean.FilterCityBean;
import com.baza.android.bzw.businesscontroller.search.viewinterface.ICityView;
import com.baza.android.bzw.manager.AddressManager;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/23.
 * Title：
 * Note：
 */

public class CityFilterPresenter extends BasePresenter {
    private ICityView mCityView;

    private String[] mClassify;
    private Resources mResources;
    private List<FilterCityBean> filterCities;
    private boolean mIsIncludeCounty;

    public CityFilterPresenter(ICityView mCityView) {
        this(mCityView, true);
    }

    public CityFilterPresenter(ICityView mCityView, boolean isIncludeCounty) {
        this.mCityView = mCityView;
        this.mIsIncludeCounty = isIncludeCounty;
        this.mResources = mCityView.callGetContext().getResources();
        this.mClassify = mResources.getStringArray(R.array.city_classify);
    }

    @Override
    public void initialize() {
        new PrepareDataTask().execute();
    }

    public String[] getClassify() {
        return mClassify;
    }

    public int[] findTargetGroupAndIndex(int cityCode) {
        int[] location = {0, 0};
        if (mClassify == null || cityCode == LocalAreaBean.CODE_NONE)
            return location;
        FilterCityBean fb;
        for (int group = 0, size = filterCities.size(); group < size; group++) {
            fb = filterCities.get(group);
            for (int position = 0, index = fb.cities.size(); position < index; position++) {
                if (fb.cities.get(position).code == cityCode) {
                    location[0] = group;
                    location[1] = position;
                    return location;
                }
            }
        }
        return location;
    }

    public int findClassifyPosition(String letter) {
        if (letter == null || filterCities == null || filterCities.isEmpty())
            return Integer.MIN_VALUE;
        for (int index = 0, size = filterCities.size(); index < size; index++) {
            if (letter.equals(filterCities.get(index).title))
                return index;
        }

        return Integer.MIN_VALUE;
    }


    private class PrepareDataTask extends AsyncTask<Void, Void, List<FilterCityBean>> {

        @Override
        protected void onPostExecute(List<FilterCityBean> filters) {
            if (filters == null || filters.isEmpty())
                return;
            try {
                filterCities = filters;
                mCityView.callSetCities(filterCities);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected List<FilterCityBean> doInBackground(Void... params) {
            SparseArray<List<LocalAreaBean>> map = AddressManager.getInstance().getAllChineseCities();
            if (map == null || map.size() == 0)
                return null;
            List<LocalAreaBean> cityAll = new ArrayList<>(430);
            List<LocalAreaBean> each;
            for (int i = 0, size = map.size(); i < size; i++) {
                each = map.valueAt(i);
                cityAll.addAll(each);
            }
            return classify(cityAll);
        }


        private ArrayList<FilterCityBean> classify(List<LocalAreaBean> cityAll) {
            HashMap<String, List<LocalAreaBean>> map = new HashMap<>(26);
            LocalAreaBean cb;
            List<LocalAreaBean> each;
            String s;
            for (int index = 0, size = cityAll.size(); index < size; index++) {
                cb = cityAll.get(index);
                s = String.valueOf(cb.shortName.charAt(0)).toUpperCase();
                each = map.get(s);
                if (each == null) {
                    each = new ArrayList<>();
                    map.put(s, each);
                }
                each.add(cb);
            }

            ArrayList<FilterCityBean> filters = new ArrayList<>(mClassify.length);
            for (int i = 0; i < mClassify.length; i++) {
                if (i == 0) {
                    //热门城市
                    filters.add(setHotCities(cityAll));
                    continue;
                }
                s = mClassify[i];
                each = map.get(s);
                if (each == null || each.isEmpty())
                    continue;
                FilterCityBean filterCityBean = new FilterCityBean();
                filterCityBean.title = s;
                filterCityBean.cities = each;
                filters.add(filterCityBean);
            }
            return filters;
        }

        private FilterCityBean setHotCities(List<LocalAreaBean> cityAll) {
            FilterCityBean filterCityBean = new FilterCityBean();
            filterCityBean.title = "热门城市";
            filterCityBean.cities = new ArrayList<>(15);
            //构建全国
            LocalAreaBean cb;
            if (mIsIncludeCounty) {
                cb = new LocalAreaBean();
                cb.name = mResources.getString(R.string.city_all);
                cb.code = LocalAreaBean.CODE_NONE;
                filterCityBean.cities.add(cb);
            }
            String[] hotCityCodes = mResources.getStringArray(R.array.hot_city_codes);
            int code;
            for (int i = 0; i < hotCityCodes.length; i++) {
                code = Integer.parseInt(hotCityCodes[i]);
                for (int index = 0, size = cityAll.size(); index < size; index++) {
                    cb = cityAll.get(index);
                    if (code == cb.code) {
                        filterCityBean.cities.add(cb);
                        break;
                    }
                }
            }
            return filterCityBean;
        }
    }
}
