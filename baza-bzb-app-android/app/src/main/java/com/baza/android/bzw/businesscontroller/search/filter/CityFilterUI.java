package com.baza.android.bzw.businesscontroller.search.filter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.bean.common.LocalAreaBean;
import com.baza.android.bzw.bean.searchfilterbean.FilterCityBean;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.businesscontroller.publish.adapter.CityListAdapter;
import com.baza.android.bzw.businesscontroller.search.presenter.CityFilterPresenter;
import com.baza.android.bzw.businesscontroller.search.viewinterface.ICityView;
import com.baza.android.bzw.extra.IFilterListener;
import com.baza.android.bzw.widget.SideBar;
import com.bznet.android.rcbox.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Vincent.Lei on 2017/3/21.
 * Title : 筛选城市选择
 * <p>
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class CityFilterUI implements ICityView, BaseBZWAdapter.IAdapterEventsListener, SideBar.OnTouchingLetterChangedListener {
    private Context mContext;
    private View view_root;
    @BindView(R.id.sideBar)
    SideBar sideBar;
    @BindView(R.id.lv)
    ListView listView;

    private CityListAdapter mCityListAdapter;
    private CityFilterPresenter mPresenter;
    private IFilterListener mFilterListener;
    private int mCityCodeOutSide;

    public CityFilterUI(Context mContext, IFilterListener mFilterListener) {
        this.mContext = mContext;
        this.mFilterListener = mFilterListener;
        init();
    }

    private void init() {
        this.view_root = LayoutInflater.from(mContext).inflate(R.layout.layout_filter_city, null);
        ButterKnife.bind(this, view_root);

        this.mPresenter = new CityFilterPresenter(this);
        Resources resources = mContext.getResources();
        this.sideBar.setClassifyValue(resources.getColor(R.color.text_color_blue_0D315C), resources.getColor(R.color.text_color_blue_53ABD5), mPresenter.getClassify());
        this.sideBar.setClassifyTextSize((int) mContext.getResources().getDimension(R.dimen.text_size_10));
        this.sideBar.setTextView((TextView) view_root.findViewById(R.id.tv_classify_hint));
        this.sideBar.setOnTouchingLetterChangedListener(this);
        mPresenter.initialize();
    }

    public View getView() {
        return view_root;
    }

    public void show(int mCityCodeOutSide) {
        if (mCityCodeOutSide != LocalAreaBean.CODE_NONE && mCityListAdapter != null && mCityListAdapter.getCount() > 0) {
            int[] location = mPresenter.findTargetGroupAndIndex(mCityCodeOutSide);
            mCityListAdapter.resetGroupAndIndex(location[0], location[1]);
        } else
            this.mCityCodeOutSide = mCityCodeOutSide;
        view_root.setVisibility(View.VISIBLE);
    }

    public void hide() {
        view_root.setVisibility(View.GONE);
    }

    @Override
    public void callSetCities(List<FilterCityBean> filters) {
        if (mCityCodeOutSide != LocalAreaBean.CODE_NONE) {
            int[] location = mPresenter.findTargetGroupAndIndex(mCityCodeOutSide);
            mCityListAdapter = new CityListAdapter(mContext, filters, location[0], location[1], this);
            mCityCodeOutSide = LocalAreaBean.CODE_NONE;
        } else
            mCityListAdapter = new CityListAdapter(mContext, filters, 0, 0, this);
        listView.setAdapter(mCityListAdapter);
        listView.setSelection(0);
    }

    @Override
    public Context callGetContext() {
        return mContext;
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        if ("#".equals(s)) {
            listView.setSelection(0);
            return;
        }
        int position = mPresenter.findClassifyPosition(s);
        if (position < 0)
            return;
        listView.setSelection(position);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        if (mFilterListener != null) {
            LocalAreaBean cityBean = (LocalAreaBean) input;
            mFilterListener.onCityFilterSelected(cityBean);
        }
    }
}
