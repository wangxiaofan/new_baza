package com.bigkoo.pickerview.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.TimePickerView.Type;
import com.bigkoo.pickerview.adapter.NumericWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class WheelTime {
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private View view;
    private WheelView wv_year;
    private WheelView wv_month;
    private WheelView wv_day;
    private WheelView wv_hours;
    private WheelView wv_mins;

    private Type type;
    public static final int DEFULT_START_YEAR = 1990;
    public static final int DEFULT_END_YEAR = 2100;
    private int startYear = DEFULT_START_YEAR;
    private int endYear = DEFULT_END_YEAR;
    private Calendar mCalendarLimit;
    private Resources mResources;
    private int mYearSelected;
    private int mMonthSelected;
    private int mDaySelected;
    private int mHourSelected;
    private int mMinuteSelected;

    public WheelTime(View view, Type type) {
        super();
        this.view = view;
        this.type = type;
        setView(view);
    }

    public void setPicker(int year, int month, int day) {
        this.setPicker(year, month, day, 0, 0);
    }

    public void setTime(Calendar calendar) {
        this.mCalendarLimit = calendar;
    }

    /**
     * @Description: TODO 弹出日期时间选择器
     */
    public void setPicker(int year, int month, int day, int h, int m) {
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        Context context = view.getContext();
        // 年
        wv_year.setAdapter(new NumericWheelAdapter(startYear, endYear));// 设置"年"的显示数据
        wv_year.setLabel(context.getString(R.string.pickerview_year));// 添加文字
        wv_year.setCurrentItem(year - startYear);// 初始化时显示的数据
        // 月
        wv_month.setAdapter(new NumericWheelAdapter(1, 12));
        wv_month.setLabel(context.getString(R.string.pickerview_month));
        wv_month.setCurrentItem(month);

        // 日

        // 判断大小月及是否闰年,用来确定"日"的数据
        if (list_big.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumericWheelAdapter(1, 31));
        } else if (list_little.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumericWheelAdapter(1, 30));
        } else {
            // 闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                wv_day.setAdapter(new NumericWheelAdapter(1, 29));
            else
                wv_day.setAdapter(new NumericWheelAdapter(1, 28));
        }
        wv_day.setLabel(context.getString(R.string.pickerview_day));
        wv_day.setCurrentItem(day - 1);


        wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
        wv_hours.setLabel(context.getString(R.string.pickerview_hours));// 添加文字
        wv_hours.setCurrentItem(h);


        wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
        wv_mins.setLabel(context.getString(R.string.pickerview_minutes));// 添加文字
        wv_mins.setCurrentItem(m);

        // 添加"年"监听
        OnItemSelectedListener wheelListener_year = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int year_num = index + startYear;
                // 判断大小月及是否闰年,用来确定"日"的数据
                int maxItem = 30;
                if (list_big
                        .contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 31));
                    maxItem = 31;
                } else if (list_little.contains(String.valueOf(wv_month
                        .getCurrentItem() + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 30));
                    maxItem = 30;
                } else {
                    if ((year_num % 4 == 0 && year_num % 100 != 0)
                            || year_num % 400 == 0) {
                        wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                        maxItem = 29;
                    } else {
                        wv_day.setAdapter(new NumericWheelAdapter(1, 28));
                        maxItem = 28;
                    }
                }
                if (wv_day.getCurrentItem() > maxItem - 1) {
                    wv_day.setCurrentItem(maxItem - 1);
                }
            }
        };
        // 添加"月"监听
        OnItemSelectedListener wheelListener_month = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int month_num = index + 1;
                int maxItem;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String.valueOf(month_num))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 31));
                    maxItem = 31;
                } else if (list_little.contains(String.valueOf(month_num))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 30));
                    maxItem = 30;
                } else {
                    if (((wv_year.getCurrentItem() + startYear) % 4 == 0 && (wv_year
                            .getCurrentItem() + startYear) % 100 != 0)
                            || (wv_year.getCurrentItem() + startYear) % 400 == 0) {
                        wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                        maxItem = 29;
                    } else {
                        wv_day.setAdapter(new NumericWheelAdapter(1, 28));
                        maxItem = 28;
                    }
                }
                if (wv_day.getCurrentItem() > maxItem - 1) {
                    wv_day.setCurrentItem(maxItem - 1);
                }

            }
        };
        wv_year.setOnItemSelectedListener(wheelListener_year);
        wv_month.setOnItemSelectedListener(wheelListener_month);


    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic
     */
    public void setCyclic(boolean cyclic) {
        wv_year.setCyclic(cyclic);
        wv_month.setCyclic(cyclic);
        wv_day.setCyclic(cyclic);
        wv_hours.setCyclic(cyclic);
        wv_mins.setCyclic(cyclic);
    }

    public String getTime() {
        StringBuffer sb = new StringBuffer();
        if (mCalendarLimit != null) {
            sb.append(mYearSelected).append("-").append(mMonthSelected).append("-").append(mDaySelected).append(" ").append(mHourSelected).append(":").append(mMinuteSelected);
        } else
            sb.append((wv_year.getCurrentItem() + startYear)).append("-")
                    .append((wv_month.getCurrentItem() + 1)).append("-")
                    .append((wv_day.getCurrentItem() + 1)).append(" ")
                    .append(wv_hours.getCurrentItem()).append(":")
                    .append(wv_mins.getCurrentItem());
        return sb.toString();
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
        mResources = view.getContext().getResources();
        wv_year = view.findViewById(R.id.year);
        wv_month = view.findViewById(R.id.month);
        wv_day = view.findViewById(R.id.day);
        wv_hours = view.findViewById(R.id.hour);
        wv_mins = view.findViewById(R.id.min);
        // 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
//        int textSize = 6;
        switch (type) {
            case YEAR_MONTH_DAY:
                wv_hours.setVisibility(View.GONE);
                wv_mins.setVisibility(View.GONE);
                break;
            case HOURS_MINS:
                wv_year.setVisibility(View.GONE);
                wv_month.setVisibility(View.GONE);
                wv_day.setVisibility(View.GONE);
                break;
            case MONTH_DAY_HOUR_MIN:
                wv_year.setVisibility(View.GONE);
                break;
            case YEAR_MONTH:
                wv_day.setVisibility(View.GONE);
                wv_hours.setVisibility(View.GONE);
                wv_mins.setVisibility(View.GONE);
        }
//        wv_day.setTextSize(textSize);
//        wv_month.setTextSize(textSize);
//        wv_year.setTextSize(textSize);
//        wv_hours.setTextSize(textSize);
//        wv_mins.setTextSize(textSize);
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public void setUp() {
        initTimeWithLimit();
    }

    private void initTimeWithLimit() {
        if (mCalendarLimit == null) {
            mCalendarLimit = Calendar.getInstance();
            try {
                mCalendarLimit.setTime(new SimpleDateFormat("yyyy", Locale.CHINA).parse(String.valueOf(startYear)));
            } catch (ParseException e) {
                e.printStackTrace();
                mCalendarLimit.setTime(new Date());
            }
        } else
            startYear = mCalendarLimit.get(Calendar.YEAR);
        mYearSelected = startYear;
        mMonthSelected = mCalendarLimit.get(Calendar.MONTH) + 1;
        mDaySelected = mCalendarLimit.get(Calendar.DAY_OF_MONTH);
        mHourSelected = mCalendarLimit.get(Calendar.HOUR_OF_DAY);
        mMinuteSelected = mCalendarLimit.get(Calendar.MINUTE);

        wv_year.setAdapter(new NumericWheelAdapter(startYear, endYear));
        wv_year.setLabel(mResources.getString(R.string.pickerview_year));
        wv_year.setCurrentItem(0);

        setMonth(startYear, mResources);
        setDay(startYear, mCalendarLimit.get(Calendar.MONTH) + 1, mResources);
        setHour(startYear, mCalendarLimit.get(Calendar.MONTH) + 1, mCalendarLimit.get(Calendar.DAY_OF_MONTH), mResources);
        setMinute(startYear, mCalendarLimit.get(Calendar.MONTH) + 1, mCalendarLimit.get(Calendar.DAY_OF_MONTH), mCalendarLimit.get(Calendar.HOUR_OF_DAY), mResources);

        wv_year.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                mYearSelected = index + startYear;
                setMonth(mYearSelected, mResources);
            }
        });
        wv_month.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                mMonthSelected = index + ((mYearSelected == mCalendarLimit.get(Calendar.YEAR)) ? mCalendarLimit.get(Calendar.MONTH) + 1 : 1);
                setDay(mYearSelected, mMonthSelected, mResources);

            }
        });
        wv_day.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                mDaySelected = index + ((mYearSelected == mCalendarLimit.get(Calendar.YEAR) && mMonthSelected == mCalendarLimit.get(Calendar.MONTH) + 1) ? mCalendarLimit.get(Calendar.DAY_OF_MONTH) : 1);
                setHour(mYearSelected, mMonthSelected, mDaySelected, mResources);
            }
        });
        wv_hours.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                mHourSelected = index + ((mYearSelected == mCalendarLimit.get(Calendar.YEAR) && (mMonthSelected == mCalendarLimit.get(Calendar.MONTH) + 1) && mDaySelected == mCalendarLimit.get(Calendar.DAY_OF_MONTH)) ? mCalendarLimit.get(Calendar.HOUR_OF_DAY) : 0);
                setMinute(mYearSelected, mMonthSelected, mDaySelected, mHourSelected, mResources);
            }
        });

        wv_mins.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                mMinuteSelected = index + ((mYearSelected == mCalendarLimit.get(Calendar.YEAR) && (mMonthSelected == mCalendarLimit.get(Calendar.MONTH) + 1) && mDaySelected == mCalendarLimit.get(Calendar.DAY_OF_MONTH) && mHourSelected == mCalendarLimit.get(Calendar.HOUR_OF_DAY)) ? mCalendarLimit.get(Calendar.MINUTE) : 0);
            }
        });
    }

    private void setMonth(int year, Resources resources) {
        boolean init = (wv_month.getAdapter() == null);
        int itemCount;
        if (year == mCalendarLimit.get(Calendar.YEAR)) {
            wv_month.setAdapter(new NumericWheelAdapter(mCalendarLimit.get(Calendar.MONTH) + 1, 12));
            itemCount = (12 - mCalendarLimit.get(Calendar.MONTH));
        } else {
            itemCount = 12;
            wv_month.setAdapter(new NumericWheelAdapter(1, 12));
        }
        wv_month.setLabel(resources.getString(R.string.pickerview_month));
        wv_month.setCurrentItem(wv_month.getCurrentItem() < itemCount ? wv_month.getCurrentItem() : 0, !init);
    }

    private void setDay(int year, int month, Resources resources) {
        boolean init = (wv_day.getAdapter() == null);
        int itemCount;
        if (month == 2) {
            int dayCount = (isRunYear(year) ? 29 : 28);
            if (year == mCalendarLimit.get(Calendar.YEAR) && month == mCalendarLimit.get(Calendar.MONTH) + 1) {
                wv_day.setAdapter(new NumericWheelAdapter(mCalendarLimit.get(Calendar.DAY_OF_MONTH), dayCount));
                itemCount = (dayCount - mCalendarLimit.get(Calendar.DAY_OF_MONTH) + 1);
            } else {
                wv_day.setAdapter(new NumericWheelAdapter(1, dayCount));
                itemCount = dayCount;
            }
        } else {
            int dayCount = (isMonthBig(month) ? 31 : 30);
            if (year == mCalendarLimit.get(Calendar.YEAR) && month == mCalendarLimit.get(Calendar.MONTH) + 1) {
                wv_day.setAdapter(new NumericWheelAdapter(mCalendarLimit.get(Calendar.DAY_OF_MONTH), dayCount));
                itemCount = (dayCount - mCalendarLimit.get(Calendar.DAY_OF_MONTH) + 1);
            } else {
                wv_day.setAdapter(new NumericWheelAdapter(1, dayCount));
                itemCount = dayCount;
            }
        }
        wv_day.setLabel(resources.getString(R.string.pickerview_day));// 添加文字
        wv_day.setCurrentItem(wv_day.getCurrentItem() < itemCount ? wv_day.getCurrentItem() : 0, !init);
    }

    private void setHour(int year, int month, int day, Resources resources) {
        boolean init = (wv_hours.getAdapter() == null);
        int itemCount;
        if (year == mCalendarLimit.get(Calendar.YEAR) && month == mCalendarLimit.get(Calendar.MONTH) + 1 && day == mCalendarLimit.get(Calendar.DAY_OF_MONTH)) {
            wv_hours.setAdapter(new NumericWheelAdapter(mCalendarLimit.get(Calendar.HOUR_OF_DAY), 23));
            itemCount = 24 - mCalendarLimit.get(Calendar.HOUR_OF_DAY);
        } else {
            wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
            itemCount = 24;
        }
        wv_hours.setLabel(resources.getString(R.string.pickerview_hours));// 添加文字
        wv_hours.setCurrentItem(wv_hours.getCurrentItem() < itemCount ? wv_hours.getCurrentItem() : 0, !init);
    }

    private void setMinute(int year, int month, int day, int hour, Resources resources) {
        boolean init = (wv_mins.getAdapter() == null);
        int itemCount;
        if (year == mCalendarLimit.get(Calendar.YEAR) && month == mCalendarLimit.get(Calendar.MONTH) + 1 && day == mCalendarLimit.get(Calendar.DAY_OF_MONTH) && hour == mCalendarLimit.get(Calendar.HOUR_OF_DAY)) {
            wv_mins.setAdapter(new NumericWheelAdapter(mCalendarLimit.get(Calendar.MINUTE), 59));
            itemCount = 60 - mCalendarLimit.get(Calendar.MINUTE);
        } else {
            wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
            itemCount = 60;
        }
        wv_mins.setLabel(resources.getString(R.string.pickerview_minutes));// 添加文字
        wv_mins.setCurrentItem(wv_mins.getCurrentItem() < itemCount ? wv_mins.getCurrentItem() : 0, !init);
    }

    private boolean isMonthBig(int month) {
        return (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12);
    }

    private boolean isMonthSmall(int month) {
        return (month == 4 || month == 6 || month == 9 || month == 11);
    }

    private boolean isRunYear(int year) {
        return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0);
    }
}
