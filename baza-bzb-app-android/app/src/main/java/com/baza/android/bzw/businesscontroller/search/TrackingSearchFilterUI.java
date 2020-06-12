package com.baza.android.bzw.businesscontroller.search;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baza.android.bzw.bean.common.LocalAreaBean;
import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.searchfilterbean.DegreeFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SchoolFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SexFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SourceFromFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.TrackingSearchFilterInfoBean;
import com.baza.android.bzw.bean.searchfilterbean.WorkYearFilterBean;
import com.baza.android.bzw.businesscontroller.search.newfilter.TrackingFilterPopupWindow;
import com.baza.android.bzw.businesscontroller.tracking.presenter.TrackingSearchPresenter;
import com.baza.android.bzw.businesscontroller.tracking.viewinterface.ITrackingSearchView;
import com.baza.android.bzw.extra.ICompanyFilterListener;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.widget.dialog.SearchCompanyDialog;
import com.baza.android.bzw.widget.dialog.SearchMajorDialog;
import com.bznet.android.rcbox.R;
import com.slib.utils.AppUtil;
import com.slib.utils.string.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 搜索高级筛选UI-new
 */

public class TrackingSearchFilterUI implements ICompanyFilterListener {

    public static final int FILTER_CITY = 1;
    public static final int FILTER_ALL = 2;
    public static final int FILTER_YEARS = 3;
    public static final int FILTER_DEGREE = 4;
    public static final int FILTER_FLAG = 5;
    public static final int FILTER_MAJOR = 6;
    public static final int FILTER_COMPANY = 7;

    private static final String NONE = "none";

    protected View viewMain;
    @BindView(R.id.tv_filter_city)
    TextView tvFilterCity;
    @BindView(R.id.tv_filter_all)
    TextView tvFilterAll;
    @BindView(R.id.tv_filter_years)
    TextView tvFilterYears;
    @BindView(R.id.tv_filter_degree)
    TextView tvFilterDegree;
    @BindView(R.id.tv_filter_flag)
    TextView tvFilterFlag;
    @BindView(R.id.tv_filter_major)
    TextView tvFilterMajor;
    @BindView(R.id.tv_filter_company)
    TextView tvFilterCompany;
    @BindView(R.id.iv_point_one)
    ImageView ivPointOne;
    @BindView(R.id.iv_point_two)
    ImageView ivPointTwo;
    @BindView(R.id.hsv)
    HorizontalScrollView horizontalScrollView;

    private ITrackingSearchView mSearchView;
    private TrackingSearchPresenter mPresenter;
    private TrackingFilterPopupWindow mFilterPopupWindow;

    private int mFilterType = Integer.MIN_VALUE;
    //private int yOffset;
    private Drawable mDrawableChecked, mDrawableNormal;
    private int mTextColorChecked, mTextColorNormal;
    private String mCityNameOutSide, mWorkYearNameOutSide, mDegreeNameOutSide, mMajorOutSide, mCompanyOutSide, mSortOutSide;
    private int mCityCodeOutSide = LocalAreaBean.CODE_NONE;
    private ArrayList<Label> mLabelsOutSide;
    private boolean mIsLabelsOutSideEnable;
    private TrackingSearchFilterInfoBean mSearchFilterInfo;

    public TrackingSearchFilterUI(View viewMain, ITrackingSearchView mSearchView, TrackingSearchPresenter mPresenter) {
        this.mSearchView = mSearchView;
        this.mPresenter = mPresenter;
        this.viewMain = viewMain;
    }

    public void init() {
        ButterKnife.bind(this, viewMain);
        Resources res = mSearchView.callGetResources();
        mDrawableNormal = AppUtil.drawableInit(R.drawable.name_list_ic_menu_drop_down, res);
        mDrawableChecked = AppUtil.drawableInit(R.drawable.name_list_ic_menu_drop_up, res);
        mTextColorChecked = res.getColor(R.color.text_color_blue_53ABD5);
        mTextColorNormal = res.getColor(R.color.text_color_grey_94A1A5);
        makePreviousDataWhenFilterChangeOutside();
        horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        break;
                    case MotionEvent.ACTION_MOVE:
                        View firstView = ((HorizontalScrollView) horizontalScrollView).getChildAt(0);
                        if (firstView.getMeasuredWidth() <= horizontalScrollView.getScrollX() + horizontalScrollView.getWidth()) {
                            ivPointOne.setBackgroundResource(R.drawable.filter_bottom_point_short);
                            ivPointTwo.setBackgroundResource(R.drawable.filter_bottom_point_long);
                        } else if (horizontalScrollView.getScrollX() <= 10) {
                            ivPointOne.setBackgroundResource(R.drawable.filter_bottom_point_long);
                            ivPointTwo.setBackgroundResource(R.drawable.filter_bottom_point_short);
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
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

            case FILTER_ALL:
            case FILTER_DEGREE:
            case FILTER_YEARS:
                mFilterPopupWindow.setOutSideMoreInfo(mWorkYearNameOutSide, mDegreeNameOutSide, mSortOutSide);
                break;

            case FILTER_COMPANY:

                break;

            case FILTER_MAJOR:

                break;

            case FILTER_FLAG:
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
        mFilterPopupWindow = new TrackingFilterPopupWindow(mSearchView.callGetBindActivity(), this);
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
        tvFilterCity.setTextColor((mFilterType == FILTER_CITY ? mTextColorChecked : mTextColorNormal));
        tvFilterAll.setTextColor((mFilterType == FILTER_ALL ? mTextColorChecked : mTextColorNormal));
        tvFilterYears.setTextColor((mFilterType == FILTER_YEARS ? mTextColorChecked : mTextColorNormal));
        tvFilterDegree.setTextColor((mFilterType == FILTER_DEGREE ? mTextColorChecked : mTextColorNormal));
        tvFilterFlag.setTextColor((mFilterType == FILTER_FLAG ? mTextColorChecked : mTextColorNormal));
        tvFilterCompany.setTextColor((mFilterType == FILTER_COMPANY ? mTextColorChecked : mTextColorNormal));
        tvFilterMajor.setTextColor((mFilterType == FILTER_MAJOR ? mTextColorChecked : mTextColorNormal));

        tvFilterCity.setCompoundDrawables(null, null, (mFilterType == FILTER_CITY ? mDrawableChecked : mDrawableNormal), null);
        tvFilterAll.setCompoundDrawables(null, null, (mFilterType == FILTER_ALL ? mDrawableChecked : mDrawableNormal), null);
        tvFilterYears.setCompoundDrawables(null, null, (mFilterType == FILTER_YEARS ? mDrawableChecked : mDrawableNormal), null);
        tvFilterDegree.setCompoundDrawables(null, null, (mFilterType == FILTER_DEGREE ? mDrawableChecked : mDrawableNormal), null);
        tvFilterFlag.setCompoundDrawables(null, null, (mFilterType == FILTER_FLAG ? mDrawableChecked : mDrawableNormal), null);
        tvFilterCompany.setCompoundDrawables(null, null, (mFilterType == FILTER_COMPANY ? mDrawableChecked : mDrawableNormal), null);
        tvFilterMajor.setCompoundDrawables(null, null, (mFilterType == FILTER_MAJOR ? mDrawableChecked : mDrawableNormal), null);
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
        tvFilterCity.setText(cityBean.name);
        mSearchFilterInfo.attachCityFilter(cityBean);
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

    @Override
    public void onYearFilterSelected(WorkYearFilterBean mWorkYearFilter) {
        if (mSearchFilterInfo == null)
            return;
        mSearchFilterInfo.attachWorkYearFilter(mWorkYearFilter);
        mSearchFilterInfo.attachKeyWord(mSearchView.callGetKeyWord());
        closeFilterPane();
        mSearchView.callSearch();
        mWorkYearNameOutSide = mWorkYearFilter.name;
        tvFilterYears.setText(mWorkYearNameOutSide);
    }

    @Override
    public void onMoreFilterSelected(WorkYearFilterBean workYearFilter, DegreeFilterBean degreeFilter, SchoolFilterBean schoolFilter, SourceFromFilterBean sourceFromFilter, SexFilterBean sexFilter) {
        if (mSearchFilterInfo == null)
            return;
        mSearchFilterInfo.attachMoreFilter(workYearFilter, degreeFilter, schoolFilter, sourceFromFilter, sexFilter);
        mSearchFilterInfo.attachKeyWord(mSearchView.callGetKeyWord());
        closeFilterPane();
        mSearchView.callSearch();
        mDegreeNameOutSide = degreeFilter.name;
        tvFilterDegree.setText(mDegreeNameOutSide);
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
    public void onSortOrderSelected(int type) {
        if (mSearchFilterInfo == null)
            return;
        String sortType = "Score";
        switch (type) {
            case 0:
                tvFilterAll.setText("综合排序");
                sortType = "Score";
                break;
            case 1:
                tvFilterAll.setText("操作时间");
                sortType = "OperationTime";
                break;
            case 2:
                tvFilterAll.setText("加入时间");
                sortType = "AddTrackingListDate";
                break;
        }
        mSearchFilterInfo.attachSort(sortType);
        mSearchFilterInfo.attachKeyWord(mSearchView.callGetKeyWord());
        closeFilterPane();
        mSearchView.callSearch();
    }

    /**
     * 请确保当前的SearchFilterInfoBean与SearchPresenter是引用同一个对象
     * 所有的搜索条件的建立都是基于SearchPresenter中的SearchFilterInfoBean
     * NONE值是确保当由外部设置了筛选条件时确保筛选UI与条件保持一致 至少做一次刷新
     * 只有调用了makePreviousDataWhenFilterChangeOutside才可以认为由外部设置了筛选条件
     */
    protected TrackingSearchFilterInfoBean getSearchFilterInfo() {
        return mPresenter.getSearchFilterInfo();
    }

    public void makePreviousDataWhenFilterChangeOutside() {
        TrackingSearchFilterInfoBean searchFilterCurrent = getSearchFilterInfo();
        boolean needRefresh = (mSearchFilterInfo == null || mSearchFilterInfo.getId() == null || !mSearchFilterInfo.getId().equals(searchFilterCurrent.getId()));
        mSearchFilterInfo = searchFilterCurrent;
        if (needRefresh) {
            LogUtil.d("makePreviousDataWhenFilterChangeOutside");
            mCityNameOutSide = mSearchFilterInfo.cityName;
            mCityCodeOutSide = mSearchFilterInfo.cityCode;
            if (mCityNameOutSide != null && !StringUtil.isEmpty(mCityNameOutSide))
                tvFilterCity.setText(mCityNameOutSide);
            else
                tvFilterCity.setText("全国");

            mWorkYearNameOutSide = mSearchFilterInfo.workYearName == null ? "" : mSearchFilterInfo.workYearName;
            if (mWorkYearNameOutSide != null && !StringUtil.isEmpty(mWorkYearNameOutSide)) {
                tvFilterYears.setText(mWorkYearNameOutSide);
            } else
                tvFilterYears.setText("工作年限");

            mDegreeNameOutSide = (mSearchFilterInfo.degreeName == null ? "" : mSearchFilterInfo.degreeName);
            if (mDegreeNameOutSide != null && !StringUtil.isEmpty(mDegreeNameOutSide)) {
                tvFilterDegree.setText(mDegreeNameOutSide);
            } else {
                tvFilterDegree.setText("学历");
            }

            mLabelsOutSide = mSearchFilterInfo.labelList;
            mIsLabelsOutSideEnable = true;

            mMajorOutSide = mSearchFilterInfo.title;
            if (mMajorOutSide != null && !StringUtil.isEmpty(mMajorOutSide)) {
                tvFilterMajor.setText(mMajorOutSide);
            } else {
                tvFilterMajor.setText("职位名称");
            }

            mCompanyOutSide = mSearchFilterInfo.company;
            if (mCompanyOutSide != null && !StringUtil.isEmpty(mCompanyOutSide)) {
                tvFilterCompany.setText(mCompanyOutSide);
            } else {
                tvFilterCompany.setText("公司");
            }

            mSortOutSide = mSearchFilterInfo.sort;
            if (mSortOutSide != null && !StringUtil.isEmpty(mSortOutSide)) {
                switch (mSortOutSide) {
                    case "Score":
                        tvFilterAll.setText("综合排序");
                        break;
                    case "OperationTime":
                        tvFilterAll.setText("操作时间");
                        break;
                    case "AddTrackingListDate":
                        tvFilterAll.setText("加入时间");
                        break;
                }
            } else {
                mSearchFilterInfo.attachSort("Score");
                tvFilterAll.setText("综合排序");
            }
        }
    }

    @OnClick({R.id.tv_filter_city, R.id.tv_filter_all, R.id.tv_filter_years, R.id.tv_filter_degree, R.id.tv_filter_flag, R.id.tv_filter_major, R.id.tv_filter_company})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_filter_city:
                showFilterWindow(FILTER_CITY);
                break;

            case R.id.tv_filter_all:
                showFilterWindow(FILTER_ALL);
                break;

            case R.id.tv_filter_years:
                showFilterWindow(FILTER_YEARS);
                break;

            case R.id.tv_filter_degree:
                showFilterWindow(FILTER_DEGREE);
                break;

            case R.id.tv_filter_flag:
                showFilterWindow(FILTER_FLAG);
                break;

            case R.id.tv_filter_major:
                mFilterType = FILTER_MAJOR;
                changeLabelUI();
                closeFilterPane();
                new SearchMajorDialog(mSearchView.callGetBindActivity(), new SearchMajorDialog.ISearchMajorEditListener() {
                    @Override
                    public void onReadySearchMajor(String hello) {
                        if (mSearchFilterInfo == null)
                            return;
                        mSearchFilterInfo.attachTitleFilter(hello);
                        mSearchFilterInfo.attachKeyWord(mSearchView.callGetKeyWord());
                        mSearchView.callSearch();
                        mFilterType = Integer.MIN_VALUE;
                        changeLabelUI();
                        if (StringUtil.isEmpty(hello)) {
                            tvFilterMajor.setText("职位名称");
                        } else {
                            tvFilterMajor.setText(hello);
                        }
                    }

                    @Override
                    public void onDismiss() {
                        mFilterType = Integer.MIN_VALUE;
                        changeLabelUI();
                    }
                }, tvFilterMajor.getText().toString().equals("职位名称") ? "" : mSearchFilterInfo.title);
                break;

            case R.id.tv_filter_company:
                mFilterType = FILTER_COMPANY;
                changeLabelUI();
                closeFilterPane();
                new SearchCompanyDialog(mSearchView.callGetBindActivity(), new SearchCompanyDialog.ISearchCompanyEditListener() {
                    @Override
                    public void onReadySearchCompany(String hello) {
                        if (mSearchFilterInfo == null)
                            return;
                        mSearchFilterInfo.attachCompanyFilter(hello);
                        mSearchFilterInfo.attachKeyWord(mSearchView.callGetKeyWord());
                        mSearchView.callSearch();
                        mFilterType = Integer.MIN_VALUE;
                        changeLabelUI();
                        if (StringUtil.isEmpty(hello)) {
                            tvFilterCompany.setText("公司");
                        } else {
                            tvFilterCompany.setText(hello);
                        }
                    }

                    @Override
                    public void onDismiss() {
                        mFilterType = Integer.MIN_VALUE;
                        changeLabelUI();
                    }
                }, tvFilterCompany.getText().toString().equals("公司") ? "" : mSearchFilterInfo.company);
                break;
        }
    }
}
