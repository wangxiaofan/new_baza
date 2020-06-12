package com.baza.android.bzw.businesscontroller.floating;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baza.android.bzw.bean.resume.ResumeStatus;
import com.baza.android.bzw.businesscontroller.floating.filter.FloatingPopupWindow;
import com.baza.android.bzw.businesscontroller.floating.viewinterface.IFloatingListView;
import com.baza.android.bzw.extra.IFloatingFilterListener;
import com.baza.android.bzw.widget.dialog.SearchNameDialog;
import com.bznet.android.rcbox.R;
import com.slib.utils.AppUtil;
import com.slib.utils.string.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 搜索高级筛选UI-new
 */

public class FloatingSearchFilterUI implements IFloatingFilterListener {

    public static final int FILTER_SORT = 1;
    public static final int FILTER_MAJOR = 2;
    public static final int FILTER_STATUS = 3;
    public static final int FILTER_NAME = 4;

    protected View viewMain;
    @BindView(R.id.tv_filter_sort)
    TextView tvFilterSort;
    @BindView(R.id.tv_filter_major)
    TextView tvFilterMajor;
    @BindView(R.id.tv_filter_status)
    TextView tvFilterStatus;
    @BindView(R.id.tv_filter_name)
    TextView tvFilterName;

    private FloatingPopupWindow mFilterPopupWindow;
    private int mFilterType = Integer.MIN_VALUE;
    private Drawable mDrawableChecked, mDrawableNormal;
    private int mTextColorChecked, mTextColorNormal;
    private IFloatingListView iFloatingView;
    private String currentName = "";
    private String type;

    public FloatingSearchFilterUI(IFloatingListView iFloatingView, View viewMain, String type) {
        this.iFloatingView = iFloatingView;
        this.viewMain = viewMain;
        this.type = type;
    }

    public void init() {
        ButterKnife.bind(this, viewMain);
        Resources res = iFloatingView.callGetResources();
        mDrawableNormal = AppUtil.drawableInit(R.drawable.name_list_ic_menu_drop_down, res);
        mDrawableChecked = AppUtil.drawableInit(R.drawable.name_list_ic_menu_drop_up, res);
        mTextColorChecked = res.getColor(R.color.text_color_blue_53ABD5);
        mTextColorNormal = res.getColor(R.color.text_color_grey_94A1A5);
    }

    private void showFilterWindow(int type) {
        if (mFilterType == type) {
            choseSameType();
            return;
        }
        mFilterType = type;
        changeLabelUI();
        if (mFilterPopupWindow == null)
            initFilterPopupWindow();
        if (mFilterType == FloatingSearchFilterUI.FILTER_MAJOR) {
            mFilterPopupWindow.setJobs(iFloatingView.getJobList(), iFloatingView.getjob());
        }
        mFilterPopupWindow.showWindow(viewMain, mFilterType);
    }

    private void initFilterPopupWindow() {
        mFilterPopupWindow = new FloatingPopupWindow(iFloatingView.callGetBindActivity(), this, type);
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
        tvFilterSort.setTextColor((mFilterType == FILTER_SORT ? mTextColorChecked : mTextColorNormal));
        tvFilterMajor.setTextColor((mFilterType == FILTER_MAJOR ? mTextColorChecked : mTextColorNormal));
        tvFilterStatus.setTextColor((mFilterType == FILTER_STATUS ? mTextColorChecked : mTextColorNormal));
        tvFilterName.setTextColor((mFilterType == FILTER_NAME ? mTextColorChecked : mTextColorNormal));

        tvFilterSort.setCompoundDrawables(null, null, (mFilterType == FILTER_SORT ? mDrawableChecked : mDrawableNormal), null);
        tvFilterMajor.setCompoundDrawables(null, null, (mFilterType == FILTER_MAJOR ? mDrawableChecked : mDrawableNormal), null);
        tvFilterStatus.setCompoundDrawables(null, null, (mFilterType == FILTER_STATUS ? mDrawableChecked : mDrawableNormal), null);
        tvFilterName.setCompoundDrawables(null, null, (mFilterType == FILTER_NAME ? mDrawableChecked : mDrawableNormal), null);
    }

    public boolean shouldHideFilterPane() {
        if (mFilterPopupWindow != null && mFilterPopupWindow.isShowing()) {
            mFilterPopupWindow.dismiss();
            return true;
        }
        return false;
    }


    @OnClick({R.id.tv_filter_sort, R.id.tv_filter_major, R.id.tv_filter_status, R.id.tv_filter_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_filter_sort:
                showFilterWindow(FILTER_SORT);
                break;

            case R.id.tv_filter_major:
                showFilterWindow(FILTER_MAJOR);
                break;

            case R.id.tv_filter_status:
                showFilterWindow(FILTER_STATUS);
                break;

            case R.id.tv_filter_name:
                closeFilterPane();
                new SearchNameDialog(iFloatingView.callGetBindActivity(), new SearchNameDialog.ISearchCompanyEditListener() {
                    @Override
                    public void onReadySearchCompany(String hello) {
                        currentName = hello;
                        tvFilterName.setText(StringUtil.isEmpty(hello) ? "人选姓名" : hello);
                        iFloatingView.callRefreshDataByName(hello);
                    }

                    @Override
                    public void onDismiss() {

                    }
                }, currentName);
                break;
        }
    }

    @Override
    public void onSortSelected(int sort, String name) {
        Log.e("herb", "时间选择>>" + name);
        tvFilterSort.setText(name);
        closeFilterPane();
        iFloatingView.callRefreshData(sort);
    }

    @Override
    public void onStatusSelected(ResumeStatus status) {
        tvFilterStatus.setText("全部".equals(status.getName()) ? "全部状态" : status.getName());
        closeFilterPane();
        iFloatingView.callRefreshDataByStatus(status.getStatus());
    }

    @Override
    public void onJobSelected(String job) {
        tvFilterMajor.setText("全部".equals(job) ? "全部职位" : job);
        closeFilterPane();
        iFloatingView.callRefreshDataByJob("全部".equals(job) ? "" : job);
    }
}
