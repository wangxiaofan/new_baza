package com.baza.android.bzw.businesscontroller.publish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.common.LocalAreaBean;
import com.baza.android.bzw.bean.searchfilterbean.FilterCityBean;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.businesscontroller.publish.adapter.CityListAdapter;
import com.baza.android.bzw.businesscontroller.search.presenter.CityFilterPresenter;
import com.baza.android.bzw.businesscontroller.search.viewinterface.ICityView;
import com.baza.android.bzw.widget.SideBar;
import com.bznet.android.rcbox.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/7/11.
 * Title：
 * Note：
 */

public class CitySelectedActivity extends BaseActivity implements View.OnClickListener, ICityView, SideBar.OnTouchingLetterChangedListener, BaseBZWAdapter.IAdapterEventsListener {
    @BindView(R.id.sideBar)
    SideBar sideBar;
    @BindView(R.id.lv)
    ListView listView;
    @BindView(R.id.tv_right_click)
    TextView textView_sure;
    @BindView(R.id.tv_title)
    TextView textView_title;
    private CityListAdapter mCityListAdapter;
    private CityFilterPresenter mPresenter;
    private int mCityCodeOutSide = LocalAreaBean.CODE_NONE;
    private LocalAreaBean mCityBeanSelected;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_city_selected;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_city_selected);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        String code = getIntent().getStringExtra("oldCityCode");
        if (!TextUtils.isEmpty(code))
            mCityCodeOutSide = Integer.parseInt(code);

        textView_title.setText(R.string.title_city_selected);
        textView_sure.setText(R.string.save);
        textView_sure.setVisibility(View.GONE);
        mPresenter = new CityFilterPresenter(this, false);
        sideBar.setClassifyValue(mResources.getColor(R.color.text_color_blue_0D315C), mResources.getColor(R.color.text_color_blue_53ABD5), mPresenter.getClassify());
        sideBar.setClassifyTextSize((int) mResources.getDimension(R.dimen.text_size_10));
        sideBar.setTextView((TextView) findViewById(R.id.tv_classify_hint));
        sideBar.setOnTouchingLetterChangedListener(this);
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.tv_right_click:
                if (mCityBeanSelected != null) {
                    Intent intent = new Intent();
                    intent.putExtra("city", mCityBeanSelected);
                    setResult(RESULT_OK, intent);
                }
                finish();
                break;
        }
    }

    public static void launch(Activity activity, String oldCityCode, int requestCode) {
        Intent intent = new Intent(activity, CitySelectedActivity.class);
        if (oldCityCode != null)
            intent.putExtra("oldCityCode", oldCityCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void callSetCities(List<FilterCityBean> filters) {
        if (mCityCodeOutSide != LocalAreaBean.CODE_NONE) {
            int[] location = mPresenter.findTargetGroupAndIndex(mCityCodeOutSide);
            mCityListAdapter = new CityListAdapter(this, filters, location[0], location[1], this);
            mCityCodeOutSide = LocalAreaBean.CODE_NONE;
            mCityBeanSelected = filters.get(location[0]).cities.get(location[1]);
            textView_sure.setVisibility((mCityBeanSelected != null ? View.VISIBLE : View.GONE));
        } else
            mCityListAdapter = new CityListAdapter(this, filters, 0, 0, this);
        listView.setAdapter(mCityListAdapter);
        listView.setSelection(0);
    }

    @Override
    public Context callGetContext() {
        return this;
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
        mCityBeanSelected = (LocalAreaBean) input;
        textView_sure.setVisibility((mCityBeanSelected != null ? View.VISIBLE : View.GONE));
    }
}
