package com.baza.android.bzw.businesscontroller.resume.detail;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.baza.android.bzw.widget.LineBreakLayout;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.bznet.android.rcbox.R;
import com.slib.utils.AppUtil;
import com.slib.utils.DateUtil;
import com.slib.utils.KeyBoardHelper;
import com.slib.utils.ToastUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent.Lei on 2019/8/14.
 * Title：
 * Note：
 */
public class RecommendUI extends BottomSheetDialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextWatcher {
    EditText editText_recommendContent;
    LineBreakLayout lbl_defaultContentTag;
    TextView textView_time;
    StringBuilder stringBuilder;
    Date dateSelected;
    InputMethodManager mImm;
    boolean isKeyBoardOpen;
    IRecommendSetListener recommendSetListener;
    private String[] mDefaultLabels;


    public interface IRecommendSetListener {
        void onRecommendSet(String content, Date date);
    }

    public RecommendUI(@NonNull Context context, IRecommendSetListener recommendSetListener) {
        super(context);
        this.recommendSetListener = recommendSetListener;
        init(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && isKeyBoardOpen) {
            if (mImm != null && mImm.isActive() && getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                mImm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void init(final Context context) {
        mImm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View rootView = LayoutInflater.from(context).inflate(R.layout.resume_recommend_ui, null);
        editText_recommendContent = rootView.findViewById(R.id.et_recommend_content);
        editText_recommendContent.addTextChangedListener(this);
        lbl_defaultContentTag = rootView.findViewById(R.id.lbl_default_content_tag);
        textView_time = rootView.findViewById(R.id.tv_time);
        textView_time.setOnClickListener(this);
        initDefaultDate();
        textView_time.setText(DateUtil.longMillions2FormatDate(dateSelected.getTime(), DateUtil.SDF_RECOMMEND));
        addDefaultContentTag(context);
        rootView.findViewById(R.id.tv_save).setOnClickListener(this);
        setContentView(rootView);
        KeyBoardHelper.addKeyBoardOpenOrClosedListener((Activity) context, new KeyBoardHelper.IKeyBoardListener() {
            @Override
            public void onKeyBoardOpen() {
                isKeyBoardOpen = true;
            }

            @Override
            public void onKeyBoardClosed() {
                isKeyBoardOpen = false;
            }
        });

    }

    private void initDefaultDate() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = new Date(new Date().getTime() + 3600000L);
        calendar.setTime(currentDate);
        int yearCurrent = calendar.get(Calendar.YEAR);
        int monthCurrent = calendar.get(Calendar.MONTH) + 1;
        int dayCurrent = calendar.get(Calendar.DAY_OF_MONTH);
        int hourCurrent = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteCurrent = calendar.get(Calendar.MINUTE);
        minuteCurrent = (minuteCurrent / 15 + (minuteCurrent % 15 > 0 ? 1 : 0)) * 15;
        if (minuteCurrent == 60) {
            minuteCurrent = 0;
            hourCurrent++;
            if (hourCurrent == 24) {
                hourCurrent = 0;
                dayCurrent++;
            }
        }
        formatTimeSelected(yearCurrent, monthCurrent, dayCurrent, hourCurrent, minuteCurrent, null);

    }

    private void formatTimeSelected(int yearCurrent, int monthCurrent, int dayCurrent, int hourCurrent, int minuteCurrent, String hm) {
        if (stringBuilder == null)
            stringBuilder = new StringBuilder();
        if (stringBuilder.length() > 0)
            stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append(yearCurrent).append("-");
        if (monthCurrent >= 10)
            stringBuilder.append(monthCurrent);
        else
            stringBuilder.append("0").append(monthCurrent);
        stringBuilder.append("-");
        if (dayCurrent >= 10)
            stringBuilder.append(dayCurrent);
        else
            stringBuilder.append("0").append(dayCurrent);
        stringBuilder.append(" ");
        if (!TextUtils.isEmpty(hm)) {
            stringBuilder.append(hm);
        } else {
            if (hourCurrent >= 10)
                stringBuilder.append(hourCurrent);
            else
                stringBuilder.append("0").append(hourCurrent);
            stringBuilder.append(":");
            if (minuteCurrent >= 10)
                stringBuilder.append(minuteCurrent);
            else
                stringBuilder.append("0").append(minuteCurrent);
        }

        String dateStr = stringBuilder.toString();
        try {
            dateSelected = DateUtil.SDF_RECOMMEND.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s.toString().trim())) {
            int childCount = lbl_defaultContentTag.getChildCount();
            CheckBox checkBox;
            for (int i = 0; i < childCount; i++) {
                checkBox = (CheckBox) lbl_defaultContentTag.getChildAt(i);
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(false);
                checkBox.setOnCheckedChangeListener(this);
            }
        }
    }

    private void addDefaultContentTag(Context context) {
        mDefaultLabels = context.getResources().getStringArray(R.array.default_recommend_content_tag);
        int itemHeight = (int) context.getResources().getDimension(R.dimen.dp_30);
        CheckBox checkBox;
        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        for (int i = 0; i < mDefaultLabels.length; i++) {
            checkBox = (CheckBox) mLayoutInflater.inflate(R.layout.layout_more_filter_item, null);
            checkBox.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, itemHeight));
            checkBox.setText(mDefaultLabels[i]);
            checkBox.setOnCheckedChangeListener(this);
            lbl_defaultContentTag.addView(checkBox);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_save:
                String content = editText_recommendContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showToast(getContext(), R.string.recommend_content_not_empty);
                    return;
                }
                dismiss();
                recommendSetListener.onRecommendSet(content, dateSelected);
                break;
            case R.id.tv_time:
                new RecommendDateSetDialog(v.getContext(), dateSelected, new RecommendDateSetDialog.ITimeSelectedListener() {
                    @Override
                    public void onTimeSelected(int year, int month, int day, String hm) {

                        formatTimeSelected(year, month, day, 0, 0, hm);
                        textView_time.setText(DateUtil.longMillions2FormatDate(dateSelected.getTime(), DateUtil.SDF_RECOMMEND));
                    }
                }).show();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String content = editText_recommendContent.getText().toString();
        if (isChecked) {
            if (stringBuilder == null)
                stringBuilder = new StringBuilder();
            if (stringBuilder.length() > 0)
                stringBuilder.delete(0, stringBuilder.length());
            if (!TextUtils.isEmpty(content)) {
                stringBuilder.append(content);
                for (int i = 0; i < mDefaultLabels.length; i++) {
                    if (content.endsWith(mDefaultLabels[i])) {
                        stringBuilder.append("；");
                        break;
                    }
                }
            }
            stringBuilder.append(buttonView.getText().toString());
            content = stringBuilder.toString();
            editText_recommendContent.setText(content);
        } else {
            String str = buttonView.getText().toString();
            content = content.replaceAll(str + "；", "");
            content = content.replaceAll(str, "");
            editText_recommendContent.setText(content);
        }
        if (content.length() > 0)
            editText_recommendContent.setSelection(content.length());

    }


    static class RecommendDateSetDialog extends BottomSheetDialog implements View.OnClickListener {
        public interface ITimeSelectedListener {
            void onTimeSelected(int year, int month, int day, String hm);
        }

        WheelView wheelView_year;
        WheelView wheelView_month;
        WheelView wheelView_day;
        WheelView wheelView_hourMinute;
        ArrayList<Integer> yearList = new ArrayList<>(10);
        ArrayList<Integer> monthList = new ArrayList<>(12);
        ArrayList<Integer> dayList = new ArrayList<>(31);
        ArrayList<String> hmList = new ArrayList<>(24 * 3);
        ArrayList<String> hourMinuteOrigin = new ArrayList<>(24 * 3);
        int yearCurrent, monthCurrent, dayCurrent, hourCurrent, minuteCurrent;
        int yearSelected, monthSelected, daySelected, hourSelected, minuteSelected;
        ITimeSelectedListener timeSelectedListener;

        public RecommendDateSetDialog(@NonNull Context context, Date target, ITimeSelectedListener timeSelectedListener) {
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
            hourSelected = hourCurrent = calendar.get(Calendar.HOUR_OF_DAY);
            minuteSelected = minuteCurrent = calendar.get(Calendar.MINUTE);
            minuteCurrent = (minuteCurrent / 15 + (minuteCurrent % 15 > 0 ? 1 : 0)) * 15;
            if (minuteCurrent == 60) {
                minuteCurrent = 0;
                hourCurrent++;
                if (hourCurrent == 24) {
                    hourCurrent = 0;
                    dayCurrent++;
                }
            }

            for (int i = 0; i < 10; i++) {
                yearList.add(yearCurrent + i);
            }
            if (target != null) {
                calendar.setTime(target);
                yearSelected = calendar.get(Calendar.YEAR);
                monthSelected = calendar.get(Calendar.MONTH) + 1;
                daySelected = calendar.get(Calendar.DAY_OF_MONTH);
                hourSelected = calendar.get(Calendar.HOUR_OF_DAY);
                minuteSelected = calendar.get(Calendar.MINUTE);
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j <= 3; j++) {
                    if (stringBuilder.length() > 0)
                        stringBuilder.delete(0, stringBuilder.length());
                    if (i >= 10)
                        stringBuilder.append(i);
                    else
                        stringBuilder.append("0").append(i);
                    stringBuilder.append(":");
                    if (j == 0) {
                        stringBuilder.append("00");
                    } else
                        stringBuilder.append(j * 15);
                    hourMinuteOrigin.add(stringBuilder.toString());
                }
            }

            setMonths(yearSelected);
            setDays(yearSelected, monthSelected);
            setHourAndMinute(yearSelected, monthSelected, daySelected);
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

        private void setHourAndMinute(int year, int month, int day) {
            hmList.clear();
            if (year == yearCurrent && month == monthCurrent && day == dayCurrent) {
                int index = findIndexInHMOrigin(hourCurrent, minuteCurrent, hourMinuteOrigin);
                for (int i = index, size = hourMinuteOrigin.size(); i < size; i++) {
                    hmList.add(hourMinuteOrigin.get(i));
                }
            } else
                hmList.addAll(hourMinuteOrigin);
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
                    BottomSheetBehavior behavior = BottomSheetBehavior.from(RecommendDateSetDialog.this.findViewById(R.id.design_bottom_sheet));
                    //此处设置表示禁止BottomSheetBehavior的执行
                    behavior.setHideable(false);
                }
            });
            wheelView_year = rootView.findViewById(R.id.options1);
            wheelView_month = rootView.findViewById(R.id.options2);
            wheelView_day = rootView.findViewById(R.id.options3);
            wheelView_hourMinute = rootView.findViewById(R.id.options4);
            rootView.findViewById(R.id.tv_save).setOnClickListener(this);
            rootView.findViewById(R.id.tv_cancel).setOnClickListener(this);
            wheelView_year.setAdapter(new ArrayWheelAdapter(yearList));
            wheelView_month.setAdapter(new ArrayWheelAdapter(monthList));
            wheelView_day.setAdapter(new ArrayWheelAdapter(dayList));
            wheelView_hourMinute.setAdapter(new ArrayWheelAdapter(hmList));
            wheelView_year.setCurrentItem(yearSelected - yearList.get(0));
            wheelView_month.setCurrentItem(monthSelected - monthList.get(0));
            wheelView_day.setCurrentItem(daySelected - dayList.get(0));
            wheelView_hourMinute.setCurrentItem(findIndexInHMOrigin(hourSelected, minuteSelected, hmList));
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
                    setHourAndMinute(yearSelected, monthSelected, daySelected);
                    wheelView_hourMinute.setAdapter(new ArrayWheelAdapter(hmList));
                    wheelView_hourMinute.setCurrentItem(0);
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
                    setHourAndMinute(yearSelected, monthSelected, daySelected);
                    wheelView_hourMinute.setAdapter(new ArrayWheelAdapter(hmList));
                    wheelView_hourMinute.setCurrentItem(0);
                }
            });
            wheelView_day.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    daySelected = dayList.get(index);
                    setHourAndMinute(yearSelected, monthSelected, daySelected);
                    wheelView_hourMinute.setAdapter(new ArrayWheelAdapter(hmList));
                    wheelView_hourMinute.setCurrentItem(0);
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
                    int hmSelected = wheelView_hourMinute.getCurrentItem();
                    if (hmSelected >= hmList.size())
                        return;
                    dismiss();
                    if (timeSelectedListener != null)
                        timeSelectedListener.onTimeSelected(yearSelected, monthSelected, dayList.get(daySelected), hmList.get(hmSelected));
                    break;
            }
        }
    }
}
