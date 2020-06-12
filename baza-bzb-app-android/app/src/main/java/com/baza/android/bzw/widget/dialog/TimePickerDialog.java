package com.baza.android.bzw.widget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.bznet.android.rcbox.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.slib.utils.AppUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TimePickerDialog extends BottomSheetDialog implements View.OnClickListener {

    public interface ITimeSelectedListener {
        void onTimeSelected(int year, int month, int day);
    }

    WheelView wheelView_year;
    WheelView wheelView_month;
    WheelView wheelView_day;
    ArrayList<Integer> yearList = new ArrayList<>(10);
    ArrayList<Integer> monthList = new ArrayList<>(12);
    ArrayList<Integer> dayList = new ArrayList<>(31);
    int yearCurrent, monthCurrent, dayCurrent;
    int yearSelected, monthSelected, daySelected;
    TimePickerDialog.ITimeSelectedListener timeSelectedListener;

    public TimePickerDialog(@NonNull Context context, Date target, TimePickerDialog.ITimeSelectedListener timeSelectedListener) {
        super(context);
        this.timeSelectedListener = timeSelectedListener;
        initData(target);
        init(context);
    }

    private void initData(Date target) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        yearSelected = yearCurrent = calendar.get(Calendar.YEAR);
        monthSelected = monthCurrent = calendar.get(Calendar.MONTH) + 1;
        daySelected = dayCurrent = calendar.get(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < 10; i++) {
            yearList.add(yearCurrent + i);
        }
        if (target != null) {
            calendar.setTime(target);
            yearSelected = calendar.get(Calendar.YEAR);
            monthSelected = calendar.get(Calendar.MONTH) + 1;
            daySelected = calendar.get(Calendar.DAY_OF_MONTH);
        }

        setMonths(yearSelected);
        setDays(yearSelected, monthSelected);
    }

    private void setMonths(int year) {
        monthList.clear();
        for (int i = year == yearCurrent ? monthCurrent : 1; i <= 12; i++) {
            monthList.add(i);
        }
    }

    private void setDays(int year, int month) {
        int dayCount = AppUtil.getDayCountOfMonth(year, month);
        dayList.clear();
        for (int i = year == yearCurrent && month == monthCurrent
                ? dayCurrent : 1; i <= dayCount; i++) {
            dayList.add(i);
        }
    }

    private int findIndexInHMOrigin(int hour, int minute, List<String> hmList) {
        String hourStr = hour >= 10 ? String.valueOf(hour) : ("0" + hour);
        String minuteStr = minute >= 10 ? String.valueOf(minute) : ("0" + minute);
        for (int i = 0, size = hmList.size(); i < size; i++) {
            if (hmList.get(i).startsWith(hourStr) && hmList.get(i).endsWith(minuteStr))
                return i;
        }
        return 0;
    }

    private void init(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.resume_recommend_date_pick_ui, null);
        rootView.post(new Runnable() {
            @Override
            public void run() {
                BottomSheetBehavior behavior = BottomSheetBehavior.from(TimePickerDialog.this.findViewById(R.id.design_bottom_sheet));
                //此处设置表示禁止BottomSheetBehavior的执行
                behavior.setHideable(false);
            }
        });
        wheelView_year = rootView.findViewById(R.id.options1);
        wheelView_month = rootView.findViewById(R.id.options2);
        wheelView_day = rootView.findViewById(R.id.options3);
        rootView.findViewById(R.id.tv_save).setOnClickListener(this);
        rootView.findViewById(R.id.tv_cancel).setOnClickListener(this);
        wheelView_year.setAdapter(new ArrayWheelAdapter(yearList));
        wheelView_month.setAdapter(new ArrayWheelAdapter(monthList));
        wheelView_day.setAdapter(new ArrayWheelAdapter(dayList));
        wheelView_year.setCurrentItem(yearSelected - yearList.get(0));
        wheelView_month.setCurrentItem(monthSelected - monthList.get(0));
        wheelView_day.setCurrentItem(daySelected - dayList.get(0));
        wheelView_year.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                yearSelected = yearList.get(index);

                setMonths(yearSelected);
                wheelView_month.setAdapter(new ArrayWheelAdapter(monthList));
                wheelView_month.setCurrentItem(0);

                monthSelected = monthList.get(0);
                setDays(yearSelected, monthSelected);
                wheelView_day.setAdapter(new ArrayWheelAdapter(dayList));
                wheelView_day.setCurrentItem(0);

                daySelected = dayList.get(0);
            }
        });

        wheelView_month.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                monthSelected = monthList.get(index);
                setDays(yearSelected, monthSelected);
                wheelView_day.setAdapter(new ArrayWheelAdapter(dayList));
                wheelView_day.setCurrentItem(0);

                daySelected = dayList.get(0);
            }
        });
        wheelView_day.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                daySelected = dayList.get(index);
            }
        });
        setContentView(rootView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_save:
                int daySelected = wheelView_day.getCurrentItem();
                if (daySelected >= dayList.size())
                    return;
                dismiss();
                if (timeSelectedListener != null)
                    timeSelectedListener.onTimeSelected(yearSelected, monthSelected, dayList.get(daySelected));
                break;
        }
    }
}
