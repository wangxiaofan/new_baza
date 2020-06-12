package com.slib.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    public static final SimpleDateFormat SDF_YMD_HMS = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.CHINA);
    //    public static final SimpleDateFormat SDF_RABBIMQ = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss EEEE", Locale.ENGLISH);
    //    public static final SimpleDateFormat SDF_YMD_HMS_SSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.CHINA);
    public static final SimpleDateFormat SDF_YMD = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);
    public static final SimpleDateFormat SDF_MD_HM = new SimpleDateFormat("MM.dd  HH:mm", Locale.CHINA);
    public static final SimpleDateFormat SDF_HM = new SimpleDateFormat("HH:mm", Locale.CHINA);
    public static final SimpleDateFormat SDF_MD = new SimpleDateFormat("MM.dd", Locale.CHINA);
    public static final SimpleDateFormat SDF_Y = new SimpleDateFormat("yyyy", Locale.CHINA);
    public static final SimpleDateFormat SDF_YMD_HM = new SimpleDateFormat("yyyy.MM.dd  HH:mm", Locale.CHINA);
    public static final SimpleDateFormat SDF_YMD_B = new SimpleDateFormat("yyyy.MM", Locale.CHINA);
    public static final SimpleDateFormat SDF_YMD_H = new SimpleDateFormat("yyyy.MM.dd  HH", Locale.CHINA);
    //    public static final int ONE_DAY_TIME_MILLIONS = 24 * 60 * 60 * 1000;
    public static final SimpleDateFormat DEFAULT_API_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    public static final SimpleDateFormat SDF_RECOMMEND = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    public static final SimpleDateFormat SDF_RECOMMEND_ONE = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);
    public static final SimpleDateFormat SDF_RECOMMEND_TWO = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    private static void setTimeZone() {
        TimeZone tz = TimeZone.getTimeZone("GMT+8");
        TimeZone.setDefault(tz);
    }

    static {
        setTimeZone();
    }

    public static String strMillions2FormatDate(String millionStr, SimpleDateFormat simpleDateFormat) {
        if (millionStr == null)
            return null;
        try {
            long lcc_time = Long.valueOf(millionStr);
            millionStr = simpleDateFormat.format(new Date(lcc_time));
        } catch (Exception e) {
            millionStr = null;
        }
        return millionStr;
    }

    public static String longMillions2FormatDate(long longMillions, SimpleDateFormat simpleDateFormat) {
        String result;
        try {
            result = simpleDateFormat.format(new Date(longMillions));
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    public static String formatMsgFriendlyTime(long timeMillions) {
        String time;
        try {
            Date dateNow = new Date();
            Date needFormat = new Date(timeMillions);

            if (SDF_YMD.format(dateNow).equals(SDF_YMD.format(needFormat)))
                time = SDF_HM.format(needFormat);
            else if (SDF_Y.format(dateNow).equals(SDF_Y.format(new Date(timeMillions))))
                time = SDF_MD.format(needFormat);
            else
                time = SDF_YMD.format(needFormat);

        } catch (Exception e) {
            time = null;
        }
        return time;
    }

    public static boolean isInSameDay(long time1, long time2) {
        try {
            return SDF_YMD.format(new Date(time1)).equals(SDF_YMD.format(new Date(time2)));
        } catch (Exception e) {
            //ignore
        }
        return false;
    }

    public static long parseLongDate(String date, SimpleDateFormat simpleDateFormat) {
        if (TextUtils.isEmpty(date))
            return 0;
        try {
            return simpleDateFormat.parse(date).getTime();
        } catch (Exception e) {
            //ignore
        }
        return 0;
    }

    public static boolean isInSameYear(long time1, long time2) {
        try {
            return SDF_Y.format(new Date(time1)).equals(SDF_Y.format(new Date(time2)));
        } catch (Exception e) {
            //ignore
        }
        return false;
    }

    public static Date parse(String strDate, SimpleDateFormat simpleDateFormat) throws ParseException {
        return simpleDateFormat.parse(strDate);
    }

    public static String dateToString(Date strDate, SimpleDateFormat simpleDateFormat) {
        return simpleDateFormat.format(strDate);
    }

    public static int getAge(Date birthDay) throws Exception {
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) { //出生日期晚于当前时间，无法计算
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);  //当前年份
        int monthNow = cal.get(Calendar.MONTH);  //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //当前日期
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;   //计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;//当前日期在生日之前，年龄减一
            } else {
                age--;//当前月份在生日之前，年龄减一
            }
        }
        return age;
    }

    /**
     * 得到几天前的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }


    public static String formatTimeSelected(int yearCurrent, int monthCurrent, int dayCurrent, String hm) {
        StringBuilder stringBuilder = new StringBuilder();
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

        if (!TextUtils.isEmpty(hm)) {
            stringBuilder.append(" ");
            stringBuilder.append(hm);
        }

        return stringBuilder.toString();
    }
}
