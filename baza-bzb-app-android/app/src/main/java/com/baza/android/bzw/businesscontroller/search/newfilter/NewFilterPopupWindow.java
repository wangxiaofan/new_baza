package com.baza.android.bzw.businesscontroller.search.newfilter;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.baza.android.bzw.bean.common.LocalAreaBean;
import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.searchfilterbean.DegreeFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SchoolFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SexFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.SourceFromFilterBean;
import com.baza.android.bzw.bean.searchfilterbean.WorkYearFilterBean;
import com.baza.android.bzw.businesscontroller.search.NewSearchFilterUI;
import com.baza.android.bzw.extra.ICompanyFilterListener;
import com.bznet.android.rcbox.R;
import com.slib.utils.AppUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Vincent.Lei on 2017/3/21.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class NewFilterPopupWindow extends PopupWindow implements ICompanyFilterListener {
    private FrameLayout frameLayout_container;
    private int mFilterType;
    private Context mContext;
    private NewCityFilterUI mCityFilterUI;
    private NewLabelFilterUI mLabelFilterUI;
    private NewSortFilterUI mSortFilterUI;
    private WorkYearFilterUI mWorkYearFilterUI;
    private DegreeFilterUI mDegreeFilterUI;
    private ICompanyFilterListener mListener;
    private ArrayList<Label> mLabelLibrary;
    private int mCityCodeOutSide = LocalAreaBean.CODE_NONE;
    private String mWorkYearOutSide, mDegreeOutSide;
    private ArrayList<Label> mLabelsOutSide;
    private boolean mIsLabelsOutSideEnable;

    public NewFilterPopupWindow(Context context, ICompanyFilterListener mListener) {
        super(context, null, R.style.customerDialog);
        this.mContext = context;
        this.mListener = mListener;
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setAnimationStyle(R.style.filterUIAnimation);
        frameLayout_container = new FrameLayout(context);
        this.setContentView(frameLayout_container);
    }

    public void setOutSideCity(int cityCode) {
        this.mCityCodeOutSide = cityCode;
    }

    public void setOutSideMoreInfo(String mWorkYearOutSide, String mDegreeOutSide) {
        this.mWorkYearOutSide = mWorkYearOutSide;
        this.mDegreeOutSide = mDegreeOutSide;
    }

    public void showWindow(View v, int mFilterType) {
        this.mFilterType = mFilterType;
        showSelectedUI();
//        showAtLocation(v, Gravity.NO_GRAVITY, 0, yOffset);
        showAsDropDown(v);
    }

    /**
     * 在android7.0上，如果不主动约束PopuWindow的大小，比如，设置布局大小为 MATCH_PARENT,那么PopuWindow会变得尽可能大，
     * 以至于 view下方无空间完全显示PopuWindow，而且view又无法向上滚动，此时PopuWindow会主动上移位置，直到可以显示完全。
     * 　解决办法：主动约束PopuWindow的内容大小，重写showAsDropDown方法：
     *
     * @param anchor
     */

    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Rect visibleFrame = new Rect();
            anchor.getGlobalVisibleRect(visibleFrame);
            setHeight(AppUtil.getFitScreenHeight(mContext) - visibleFrame.bottom);
        }
        super.showAsDropDown(anchor);
    }

    private void showSelectedUI() {
        switch (mFilterType) {
            case NewSearchFilterUI.FILTER_CITY:
                showCity();
                break;
            case NewSearchFilterUI.FILTER_YEARS:
                showYear();
                break;
            case NewSearchFilterUI.FILTER_DEGREE:
                showDegree();
                break;
            case NewSearchFilterUI.FILTER_FLAG:
                showLabel();
                break;
            case NewSearchFilterUI.FILTER_ALL:
                showSort();
                break;
            case NewSearchFilterUI.FILTER_COMPANY:

                break;
            case NewSearchFilterUI.FILTER_MAJOR:

                break;
        }
    }

    private void showCity() {
        if (mCityFilterUI == null) {
            mCityFilterUI = new NewCityFilterUI(mContext, this);
            frameLayout_container.addView(mCityFilterUI.getView());
        }
        if (mLabelFilterUI != null)
            mLabelFilterUI.hide();
        if (mSortFilterUI != null)
            mSortFilterUI.hide();
        if (mWorkYearFilterUI != null)
            mWorkYearFilterUI.hide();
        if (mDegreeFilterUI != null)
            mDegreeFilterUI.hide();
        mCityFilterUI.show(mCityCodeOutSide);
        mCityCodeOutSide = LocalAreaBean.CODE_NONE;
    }

    private void showLabel() {
        boolean mayReset = true;
        if (mLabelFilterUI == null) {
            mayReset = false;
            mLabelFilterUI = new NewLabelFilterUI(mContext, this, mLabelLibrary);
            frameLayout_container.addView(mLabelFilterUI.getView());
        }
        if (mCityFilterUI != null)
            mCityFilterUI.hide();
        if (mWorkYearFilterUI != null)
            mWorkYearFilterUI.hide();
        if (mSortFilterUI != null)
            mSortFilterUI.hide();
        if (mDegreeFilterUI != null)
            mDegreeFilterUI.hide();
        mLabelFilterUI.show(mayReset, mLabelsOutSide, mIsLabelsOutSideEnable);
        mLabelsOutSide = null;
    }

    private void showSort() {
        if (mSortFilterUI == null) {
            mSortFilterUI = new NewSortFilterUI(mContext, this);
            frameLayout_container.addView(mSortFilterUI.getView());
        }
        if (mCityFilterUI != null)
            mCityFilterUI.hide();
        if (mWorkYearFilterUI != null)
            mWorkYearFilterUI.hide();
        if (mDegreeFilterUI != null)
            mDegreeFilterUI.hide();
        if (mLabelFilterUI != null)
            mLabelFilterUI.hide();
        mSortFilterUI.show();
    }

    public void setLabelLibs(ArrayList<Label> labelLibs) {
        this.mLabelLibrary = labelLibs;
        if (mLabelFilterUI != null)
            mLabelFilterUI.refreshLabel(mLabelLibrary);
    }

    private void showYear() {
        if (mWorkYearFilterUI == null) {
            mWorkYearFilterUI = new WorkYearFilterUI(mContext, this);
            frameLayout_container.addView(mWorkYearFilterUI.getView());
        }
        if (mCityFilterUI != null)
            mCityFilterUI.hide();
        if (mLabelFilterUI != null)
            mLabelFilterUI.hide();
        if (mSortFilterUI != null)
            mSortFilterUI.hide();
        if (mDegreeFilterUI != null)
            mDegreeFilterUI.hide();
        mWorkYearFilterUI.show(mWorkYearOutSide);
    }

    private void showDegree() {
        if (mDegreeFilterUI == null) {
            mDegreeFilterUI = new DegreeFilterUI(mContext, this);
            frameLayout_container.addView(mDegreeFilterUI.getView());
        }
        if (mCityFilterUI != null)
            mCityFilterUI.hide();
        if (mLabelFilterUI != null)
            mLabelFilterUI.hide();
        if (mSortFilterUI != null)
            mSortFilterUI.hide();
        if (mWorkYearFilterUI != null)
            mWorkYearFilterUI.hide();
        mDegreeFilterUI.show(mDegreeOutSide);
    }

    public void setOutSideLabels(ArrayList<Label> labelsOutSide, boolean mIsLabelsOutSideEnable) {
        this.mLabelsOutSide = labelsOutSide;
        this.mIsLabelsOutSideEnable = mIsLabelsOutSideEnable;
    }

    @Override
    public void onCityFilterSelected(LocalAreaBean cityBean) {
        if (mListener != null)
            mListener.onCityFilterSelected(cityBean);
    }

    @Override
    public void clearMoreFilter() {
        if (mListener != null)
            mListener.clearMoreFilter();
    }

    @Override
    public void clearLabelsFilter() {
        if (mListener != null)
            mListener.clearLabelsFilter();
    }

    @Override
    public void onYearFilterSelected(WorkYearFilterBean bean) {
        if (mListener != null)
            mListener.onYearFilterSelected(bean);
    }

    @Override
    public void onMoreFilterSelected(WorkYearFilterBean workYearFilter, DegreeFilterBean degreeFilter, SchoolFilterBean schoolFilter, SourceFromFilterBean sourceFromFilter, SexFilterBean sexFilter) {
        if (mListener != null)
            mListener.onMoreFilterSelected(workYearFilter, degreeFilter, schoolFilter, sourceFromFilter, sexFilter);
    }

    @Override
    public void onLabelSelected(HashMap<String, Label> mLabelSelected) {
        if (mListener != null)
            mListener.onLabelSelected(mLabelSelected);
    }

    @Override
    public void onSortOrderSelected(int sortType) {
        if (mListener != null)
            mListener.onSortOrderSelected(sortType);
    }
}
