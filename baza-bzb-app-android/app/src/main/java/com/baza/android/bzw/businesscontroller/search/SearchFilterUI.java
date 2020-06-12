package com.baza.android.bzw.businesscontroller.search;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baza.android.bzw.bean.common.LocalAreaBean;
import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.searchfilterbean.DegreeFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SchoolFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SearchFilterInfoBean;
import com.baza.android.bzw.bean.searchfilterbean.SexFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SourceFromFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.WorkYearFilterBean;
import com.baza.android.bzw.businesscontroller.search.filter.FilterPopupWindow;
import com.baza.android.bzw.businesscontroller.search.presenter.ResumeSearchPresenter;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IResumeSearchView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.extra.IFilterListener;
import com.baza.android.bzw.log.LogUtil;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/5/22.
 * Title：搜索高级筛选UI
 * Note：
 */

public class SearchFilterUI implements View.OnClickListener, IFilterListener {
    public static final int FILTER_CITY = 1;
    public static final int FILTER_MORE = 2;
    public static final int FILTER_LABEL = 3;
    public static final int FILTER_SORT = 4;
    private static final String NONE = "none";

    @BindView(R.id.search_filter_ui)
    LinearLayout linearLayout_filterTitleBar;
    @BindView(R.id.tv_cityFilter)
    TextView textView_cityFilter;
    @BindView(R.id.tv_moreFilter)
    TextView textView_moreFilter;
    @BindView(R.id.tv_labelFilter)
    TextView textView_labelFilter;
    @BindView(R.id.tv_sortFilter)
    TextView textView_sortFilter;
    protected View viewMain;

    private IResumeSearchView mSearchView;
    private ResumeSearchPresenter mPresenter;
    private FilterPopupWindow mFilterPopupWindow;

    private int mFilterType = Integer.MIN_VALUE;
    //    private int yOffset;
    private Drawable mDrawableChecked, mDrawableNormal;
    private int mTextColorChecked, mTextColorNormal;
    private String mCityNameOutSide, mWorkYearNameOutSide, mDegreeNameOutSide, mSchoolNameOutSide, mSourceNameOutSide, mSexNameOutSide;
    private int mCityCodeOutSide = LocalAreaBean.CODE_NONE;
    private ArrayList<Label> mLabelsOutSide;
    private boolean mIsLabelsOutSideEnable;
    private SearchFilterInfoBean mSearchFilterInfo;


    public SearchFilterUI(View viewMain, IResumeSearchView mSearchView, ResumeSearchPresenter mPresenter) {
        this.mSearchView = mSearchView;
        this.mPresenter = mPresenter;
        this.viewMain = viewMain;
    }

    public void init() {
        ButterKnife.bind(this, viewMain);
        viewMain.findViewById(R.id.fl_city_filter).setOnClickListener(this);
        viewMain.findViewById(R.id.fl_more_filter).setOnClickListener(this);
        viewMain.findViewById(R.id.fl_label_filter).setOnClickListener(this);
        viewMain.findViewById(R.id.fl_sort_filter).setOnClickListener(this);
        Resources res = mSearchView.callGetResources();
        mDrawableNormal = AppUtil.drawableInit(R.drawable.name_list_ic_menu_drop_down, res);
        mDrawableChecked = AppUtil.drawableInit(R.drawable.name_list_ic_menu_drop_up, res);
        mTextColorChecked = res.getColor(R.color.text_color_blue_53ABD5);
        mTextColorNormal = res.getColor(R.color.text_color_grey_94A1A5);
        makePreviousDataWhenFilterChangeOutside();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_city_filter:
                showFilterWindow(FILTER_CITY);
                break;
            case R.id.fl_more_filter:
                showFilterWindow(FILTER_MORE);
                break;
            case R.id.fl_label_filter:
                showFilterWindow(FILTER_LABEL);
                break;
            case R.id.fl_sort_filter:
                showFilterWindow(FILTER_SORT);
                break;
        }
    }

    private void showFilterWindow(int type) {
        mSearchView.callGetBindActivity().hideSoftInput();
        if (mFilterType == type) {
            choseSameType();
            return;
        }
        mFilterType = type;
        changeLabelUI();
        if (mFilterPopupWindow == null)
            initFilterPopupWindow();
        switch (type) {
            case FILTER_CITY:
                mFilterPopupWindow.setOutSideCity(mCityCodeOutSide);
                mCityCodeOutSide = LocalAreaBean.CODE_NONE;
                break;
            case FILTER_MORE:
                mFilterPopupWindow.setOutSideMoreInfo(mWorkYearNameOutSide, mDegreeNameOutSide, mSchoolNameOutSide, mSourceNameOutSide, mSexNameOutSide);
                mWorkYearNameOutSide = mDegreeNameOutSide = mSchoolNameOutSide = mSourceNameOutSide = mSexNameOutSide = null;
                break;
            case FILTER_LABEL:
                prepareLabels();
                mFilterPopupWindow.setOutSideLabels(mLabelsOutSide, mIsLabelsOutSideEnable);
                mFilterPopupWindow.setLabelLibs(getLabelLibrary());
                mLabelsOutSide = null;
                mIsLabelsOutSideEnable = false;
                break;
        }
        mFilterPopupWindow.showWindow(viewMain, mFilterType);
    }

    private void initFilterPopupWindow() {
        mFilterPopupWindow = new FilterPopupWindow(mSearchView.callGetBindActivity(), this);
        mFilterPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mFilterPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFilterType = Integer.MIN_VALUE;
                changeLabelUI();
            }
        });
    }

    private void choseSameType() {
        if (mFilterPopupWindow != null && mFilterPopupWindow.isShowing())
            mFilterPopupWindow.dismiss();
        mFilterType = Integer.MIN_VALUE;
    }

    public void closeFilterPane() {
        if (mFilterPopupWindow != null && mFilterPopupWindow.isShowing())
            mFilterPopupWindow.dismiss();
    }

    private void changeLabelUI() {
        textView_cityFilter.setTextColor((mFilterType == FILTER_CITY ? mTextColorChecked : mTextColorNormal));
        textView_moreFilter.setTextColor((mFilterType == FILTER_MORE ? mTextColorChecked : mTextColorNormal));
        textView_labelFilter.setTextColor((mFilterType == FILTER_LABEL ? mTextColorChecked : mTextColorNormal));
        textView_sortFilter.setTextColor((mFilterType == FILTER_SORT ? mTextColorChecked : mTextColorNormal));

        textView_cityFilter.setCompoundDrawables(null, null, (mFilterType == FILTER_CITY ? mDrawableChecked : mDrawableNormal), null);
        textView_moreFilter.setCompoundDrawables(null, null, (mFilterType == FILTER_MORE ? mDrawableChecked : mDrawableNormal), null);
        textView_labelFilter.setCompoundDrawables(null, null, (mFilterType == FILTER_LABEL ? mDrawableChecked : mDrawableNormal), null);
        textView_sortFilter.setCompoundDrawables(null, null, (mFilterType == FILTER_SORT ? mDrawableChecked : mDrawableNormal), null);
    }

    public boolean shouldHideFilterPane() {
        if (mFilterPopupWindow != null && mFilterPopupWindow.isShowing()) {
            mFilterPopupWindow.dismiss();
            return true;
        }
        return false;
    }

    public void setLabelLibs() {
        if (mFilterPopupWindow != null)
            mFilterPopupWindow.setLabelLibs(getLabelLibrary());
    }

    protected void prepareLabels() {
        mPresenter.prepareLabels();
    }

    public ArrayList<Label> getLabelLibrary() {
        return mPresenter.getLabelLibrary();
    }

    @Override
    public void onCityFilterSelected(LocalAreaBean cityBean) {
        if (mSearchFilterInfo == null)
            return;
        textView_cityFilter.setText(cityBean.name);
        mSearchFilterInfo.attachCityFilter(cityBean);
        mSearchFilterInfo.attachKeyWord(mSearchView.callGetKeyWord());
        closeFilterPane();
        mSearchView.callSearch();
    }

    @Override
    public void onMoreFilterSelected(WorkYearFilterBean workYearFilter, DegreeFilterBean degreeFilter, SchoolFilterBean schoolFilter, SourceFromFilterBean sourceFromFilter, SexFilterBean sexFilter) {
        if (mSearchFilterInfo == null)
            return;
        mSearchFilterInfo.attachMoreFilter(workYearFilter, degreeFilter, schoolFilter, sourceFromFilter, sexFilter);
        mSearchFilterInfo.attachKeyWord(mSearchView.callGetKeyWord());
        closeFilterPane();
        mSearchView.callSearch();
    }

    @Override
    public void onLabelSelected(HashMap<String, Label> mLabelSelected) {
        if (mSearchFilterInfo == null)
            return;
        mSearchFilterInfo.attachLabelsFilter(mLabelSelected);
        mSearchFilterInfo.attachKeyWord(mSearchView.callGetKeyWord());
        closeFilterPane();
        mSearchView.callSearch();
    }

    @Override
    public void onSortOrderSelected(int sortType) {
        if (mSearchFilterInfo == null)
            return;
        switch (sortType) {
            case CommonConst.RESUME_SEARCH_SORT_ORDER_COMPLETION:
                textView_sortFilter.setText(R.string.sort_completion);
                break;
            case CommonConst.RESUME_SEARCH_SORT_ORDER_CREATE_TIME:
                textView_sortFilter.setText(R.string.sort_create_time);
                break;
            case CommonConst.RESUME_SEARCH_SORT_ORDER_UPDATE_TIME:
                textView_sortFilter.setText(R.string.sort_update_time);
                break;
            case CommonConst.RESUME_SEARCH_SORT_ORDER_NONE:
                textView_sortFilter.setText(R.string.sort);
                break;
        }
        mSearchFilterInfo.setMandatorySort(sortType);
        mSearchFilterInfo.attachKeyWord(mSearchView.callGetKeyWord());
        closeFilterPane();
        mSearchView.callSearch();
    }

    @Override
    public void clearMoreFilter() {
        if (mSearchFilterInfo == null)
            return;
        mSearchFilterInfo.clearMoreFilter();
    }

    @Override
    public void clearLabelsFilter() {
        if (mSearchFilterInfo == null)
            return;
        mSearchFilterInfo.clearLabelsFilter();
    }

    /**
     * 请确保当前的SearchFilterInfoBean与SearchPresenter是引用同一个对象
     * 所有的搜索条件的建立都是基于SearchPresenter中的SearchFilterInfoBean
     * NONE值是确保当由外部设置了筛选条件时确保筛选UI与条件保持一致 至少做一次刷新
     * 只有调用了makePreviousDataWhenFilterChangeOutside才可以认为由外部设置了筛选条件
     */
    protected SearchFilterInfoBean getSearchFilterInfo() {
        return mPresenter.getSearchFilterInfo();
    }

    public void makePreviousDataWhenFilterChangeOutside() {
        SearchFilterInfoBean searchFilterCurrent = getSearchFilterInfo();
        boolean needRefresh = (mSearchFilterInfo == null || mSearchFilterInfo.getId() == null || !mSearchFilterInfo.getId().equals(searchFilterCurrent.getId()));
        mSearchFilterInfo = searchFilterCurrent;
        if (needRefresh) {
            LogUtil.d("makePreviousDataWhenFilterChangeOutside");
            mCityNameOutSide = mSearchFilterInfo.cityName;
            mCityCodeOutSide = mSearchFilterInfo.cityCode;
            if (mCityNameOutSide != null)
                textView_cityFilter.setText(mCityNameOutSide);
            mWorkYearNameOutSide = (mSearchFilterInfo.workYearName == null ? NONE : mSearchFilterInfo.workYearName);
            mDegreeNameOutSide = (mSearchFilterInfo.degreeName == null ? NONE : mSearchFilterInfo.degreeName);
            mSchoolNameOutSide = (mSearchFilterInfo.schoolName == null ? NONE : mSearchFilterInfo.schoolName);
            mSourceNameOutSide = (mSearchFilterInfo.sourceFromName == null ? NONE : mSearchFilterInfo.sourceFromName);
            mSexNameOutSide = (mSearchFilterInfo.sexName == null ? NONE : mSearchFilterInfo.sexName);
            mLabelsOutSide = mSearchFilterInfo.labelList;
            mIsLabelsOutSideEnable = true;
        }
    }
}
