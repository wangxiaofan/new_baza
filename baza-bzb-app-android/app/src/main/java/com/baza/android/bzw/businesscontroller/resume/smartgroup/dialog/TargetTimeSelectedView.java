package com.baza.android.bzw.businesscontroller.resume.smartgroup.dialog;

import android.app.Activity;
import android.graphics.Color;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;

/**
 * Created by Vincent.Lei on 2018/9/3.
 * Title：
 * Note：
 */
public class TargetTimeSelectedView implements View.OnClickListener {

    public interface ITimeDataProvider {
        ArrayList<String> getYearSelector();

        ArrayList<String> getMonthSelectorByYear(String year);

        ArrayList<String> getDaySelectorByYearAndMonth(String year, String month);

        void onTimeSelectorSet(String year, String month, String day);
    }

    private FrameLayout view_root;
    private View view_main;
    private ITimeDataProvider mDataProvider;
    private WheelView wheelView_month;
    private WheelView wheelView_day;
    private WheelView wheelView_year;
    private ArrayList<String> mYearList;
    private ArrayList<String> mMonthList;
    private ArrayList<String> mDayList;

    private int mViewHeight;
    private boolean mAnimating;
    private boolean mShown;

    public TargetTimeSelectedView(Activity activity, ITimeDataProvider dataProvider) {
        this.mDataProvider = dataProvider;
        view_root = new FrameLayout(activity);
        view_root.setBackgroundColor(Color.parseColor("#7F000000"));
        view_root.setVisibility(View.GONE);
        FrameLayout frameLayout = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        frameLayout.addView(view_root, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view_main = LayoutInflater.from(activity).inflate(R.layout.smart_group_time_selected, null);
        view_main.setVisibility(View.INVISIBLE);
        view_main.findViewById(R.id.btn_cancel).setOnClickListener(this);
        view_main.findViewById(R.id.btn_sure).setOnClickListener(this);
        wheelView_year = view_main.findViewById(R.id.year);
        wheelView_year.setLabel("年");
        wheelView_month = view_main.findViewById(R.id.month);
        wheelView_month.setLabel("月");
        wheelView_day = view_main.findViewById(R.id.day);
        wheelView_day.setLabel("日");


        mYearList = mDataProvider.getYearSelector();
        wheelView_year.setAdapter(new ArrayWheelAdapter<>(mYearList));
        mMonthList = mDataProvider.getMonthSelectorByYear(mYearList.get(0));
        wheelView_month.setCurrentItem(0);
        wheelView_month.setAdapter(new ArrayWheelAdapter<>(mMonthList));
        mDayList = mDataProvider.getDaySelectorByYearAndMonth(mYearList.get(0), mMonthList.get(0));
        wheelView_day.setCurrentItem(0);
        wheelView_day.setAdapter(new ArrayWheelAdapter<>(mDayList));


//        wheelView_year.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(int index) {
//                mMonthList = mDataProvider.getMonthSelectorByYear(mYearList.get(index));
//                wheelView_month.setAdapter(new ArrayWheelAdapter<>(mMonthList));
//            }
//        });
        wheelView_month.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                mDayList = mDataProvider.getDaySelectorByYearAndMonth(mYearList.get(wheelView_year.getCurrentItem()), mMonthList.get(index));
                wheelView_day.setAdapter(new ArrayWheelAdapter<>(mDayList));
                wheelView_day.setCurrentItem(0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_sure:
                dismiss();
                mDataProvider.onTimeSelectorSet(mYearList.get(wheelView_year.getCurrentItem()), mMonthList.get(wheelView_month.getCurrentItem()),mDayList.get(wheelView_day.getCurrentItem()));
                break;
        }
    }

    public void dismiss() {
        if (mAnimating || !mShown)
            return;
        mAnimating = true;
        ViewCompat.animate(view_main).translationY(mViewHeight).setDuration(300).setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {

            }

            @Override
            public void onAnimationEnd(View view) {
                mShown = false;
                mAnimating = false;
                view_root.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(View view) {

            }
        }).start();
    }

    public void show() {
        if (mAnimating || mShown)
            return;
        mAnimating = true;
        view_root.setVisibility(View.VISIBLE);
        view_main.setVisibility(View.VISIBLE);
        if (view_main.getParent() == null) {
            FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            flp.gravity = Gravity.BOTTOM;
            view_root.addView(view_main, flp);
            view_root.post(new Runnable() {
                @Override
                public void run() {
                    mViewHeight = view_main.getHeight();
                    view_main.setTranslationY(mViewHeight);
                    startShow();
                }
            });
        } else
            startShow();
    }

    private void startShow() {
        ViewCompat.animate(view_main).translationY(0).setDuration(300).setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
                if (view_root.getVisibility() != View.VISIBLE)
                    view_root.setVisibility(View.VISIBLE);
                if (view_main.getVisibility() != View.VISIBLE)
                    view_main.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(View view) {
                mShown = true;
                mAnimating = false;
            }

            @Override
            public void onAnimationCancel(View view) {

            }
        }).start();
    }

}
