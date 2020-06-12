package com.baza.android.bzw.businesscontroller.floating.filter;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.baza.android.bzw.bean.resume.ResumeStatus;
import com.baza.android.bzw.businesscontroller.floating.FloatingSearchFilterUI;
import com.baza.android.bzw.extra.IFloatingFilterListener;
import com.bznet.android.rcbox.R;
import com.slib.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/3/21.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class FloatingPopupWindow extends PopupWindow implements IFloatingFilterListener {
    private FrameLayout frameLayout_container;
    private int mFilterType;
    private Context mContext;
    private FloatingSortFilterUI mSortFilterUI;
    private StatusFilterUI mStatusFilterUI;
    private JobFilterUI mJobFilterUI;
    private IFloatingFilterListener mListener;
    private List<String> jobList = new ArrayList<>();
    private String currentJob;
    private String type;

    public FloatingPopupWindow(Context context, IFloatingFilterListener mListener, String type) {
        super(context, null, R.style.customerDialog);
        this.mContext = context;
        this.mListener = mListener;
        this.type = type;
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setAnimationStyle(R.style.filterUIAnimation);
        frameLayout_container = new FrameLayout(context);
        this.setContentView(frameLayout_container);
//        this.setOutsideTouchable(true);
//        this.setBackgroundDrawable(new BitmapDrawable());
    }

    public void setJobs(List<String> jobs, String job) {
        this.jobList.clear();
        this.jobList.addAll(jobs);
        this.currentJob = job;
    }

    public void showWindow(View v, int mFilterType) {
        this.mFilterType = mFilterType;
        showSelectedUI();
        showAsDropDown(v);
    }

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
            case FloatingSearchFilterUI.FILTER_SORT:
                showSort();
                break;
            case FloatingSearchFilterUI.FILTER_STATUS:
                showStatus();
                break;
            case FloatingSearchFilterUI.FILTER_MAJOR:
                showJob();
                break;
        }
    }

    private void showStatus() {
        if (mStatusFilterUI == null) {
            mStatusFilterUI = new StatusFilterUI(mContext, this, type);
            frameLayout_container.addView(mStatusFilterUI.getView());
        }
        if (mSortFilterUI != null)
            mSortFilterUI.hide();
        if (mJobFilterUI != null)
            mJobFilterUI.hide();
        mStatusFilterUI.show();
    }

    private void showSort() {
        if (mSortFilterUI == null) {
            mSortFilterUI = new FloatingSortFilterUI(mContext, this);
            frameLayout_container.addView(mSortFilterUI.getView());
        }
        if (mStatusFilterUI != null)
            mStatusFilterUI.hide();
        if (mJobFilterUI != null)
            mJobFilterUI.hide();
        mSortFilterUI.show();
    }

    private void showJob() {
        if (mJobFilterUI == null) {
            mJobFilterUI = new JobFilterUI(mContext, this);
            frameLayout_container.addView(mJobFilterUI.getView());
        }
        if (mStatusFilterUI != null)
            mStatusFilterUI.hide();
        if (mSortFilterUI != null)
            mSortFilterUI.hide();
        mJobFilterUI.show(jobList, currentJob);
    }

    @Override
    public void onSortSelected(int sort, String name) {
        mListener.onSortSelected(sort, name);
    }

    @Override
    public void onStatusSelected(ResumeStatus status) {
        mListener.onStatusSelected(status);
    }

    @Override
    public void onJobSelected(String job) {
        mListener.onJobSelected(job);
    }
}
