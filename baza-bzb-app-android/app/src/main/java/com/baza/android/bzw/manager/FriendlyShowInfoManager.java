package com.baza.android.bzw.manager;

import android.content.res.Resources;
import android.text.TextUtils;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.constant.CommonConst;
import com.bznet.android.rcbox.R;
import com.slib.utils.AppUtil;
import com.slib.utils.DateUtil;

import java.util.HashMap;

/**
 * Created by Vincent.Lei on 2017/9/27.
 * Title：
 * Note：
 */

public class FriendlyShowInfoManager {
    private Resources mResources;
    private String[] mDegreeArray;
    private HashMap<String, String> mLanguageIdNames;
    private static FriendlyShowInfoManager mInstance;

    private FriendlyShowInfoManager() {
        this.mResources = BZWApplication.getApplication().getResources();
    }

    public static FriendlyShowInfoManager getInstance() {
        if (mInstance == null) {
            synchronized (FriendlyShowInfoManager.class) {
                if (mInstance == null)
                    mInstance = new FriendlyShowInfoManager();
            }
        }
        return mInstance;
    }

    /**
     * 获取学历
     */
    public String getDegree(int degree) {
        //1--高中及以下  senior schoolName; 2--大专  associate; 3--本科  bachelor; 4--硕士  master; 5--MBA(工商管理硕士) ; 6--博士  doctor
        if (mDegreeArray == null)
            mDegreeArray = mResources.getStringArray(R.array.degree_level);
        int degreePosition = degree - 1;
        return (degreePosition >= mDegreeArray.length || degreePosition < 0 ? "未知" : mDegreeArray[degreePosition]);
    }

    public String getGender(int gender) {
        return mResources.getString(gender == CommonConst.SEX_UNKNOWN ? R.string.keep_secret : (gender == CommonConst.SEX_MALE ? R.string.male : R.string.female));
    }

    public String getMarriage(int marriage) {
        return mResources.getString(marriage == CommonConst.Marriage.UN_KNOW ? R.string.message_unknown : (marriage == CommonConst.Marriage.SINGLE ? R.string.marriage_single : (marriage == CommonConst.Marriage.MARRIAGE ? R.string.marriage_marriage : R.string.keep_secret)));
    }

    public boolean isDegreeEnable(int degree) {
        //1--高中及以下  senior schoolName; 2--大专  associate; 3--本科  bachelor; 4--硕士  master; 5--MBA(工商管理硕士) ; 6--博士  doctor
        return degree >= 1 && degree <= 6;
    }

    public String getWorkExperienceFormattedTime(long startDate, long endDate) {
        StringBuilder stringBuilder = new StringBuilder();
        String temp;
        if (startDate == CommonConst.TIME_NO_GET)
            //时间未知
            stringBuilder.append("未知");
        else if (startDate == CommonConst.TIME_UNTIL_NOW)
            //至今
            stringBuilder.append("至今");
        else {
            temp = DateUtil.longMillions2FormatDate(startDate, DateUtil.SDF_YMD_B);
            if (temp != null) {
                if (temp.startsWith("1900") || temp.startsWith("1970") || temp.startsWith("0001"))
                    temp = "未知";
                else if (temp.startsWith("9999"))
                    temp = "至今";
                stringBuilder.append(temp);
            }
        }
        stringBuilder.append("-");
        if (endDate == CommonConst.TIME_NO_GET)
            //时间未知
            stringBuilder.append("未知");
        else if (endDate == CommonConst.TIME_UNTIL_NOW || endDate <= startDate)
            //至今
            stringBuilder.append("至今");
        else {
            temp = DateUtil.longMillions2FormatDate(endDate, DateUtil.SDF_YMD_B);
            if (temp != null) {
                if (temp.startsWith("1900") || temp.startsWith("1970") || temp.startsWith("0001"))
                    temp = "未知";
                else if (temp.startsWith("9999"))
                    temp = "至今";
                stringBuilder.append(temp);
            }
        }
        return stringBuilder.toString();
    }

    public String getDynamicShowTime(long time) {
        if (DateUtil.isInSameYear(time, System.currentTimeMillis()))
            return DateUtil.longMillions2FormatDate(time, DateUtil.SDF_MD_HM);
        return DateUtil.longMillions2FormatDate(time, DateUtil.SDF_YMD_HM);
    }

    public String getFriendResumeCountValue(int value, boolean useWob, boolean useTob) {
        if (useWob && value > CommonConst.MAX_COUNT_VALUE_LEVEL) {
            return mResources.getString(R.string.friend_count_value, String.valueOf(value / CommonConst.MAX_COUNT_VALUE_LEVEL));
        }
        if (useTob)
            return AppUtil.formatTob(value);
        return String.valueOf(value);
    }

    public String formatTBDTime_YM(String time) {
        if (TextUtils.isEmpty(time) || time.length() < 10 || "0001-01-01 00:00:00".equals(time))
            return "未知";
        return time.substring(0, 4) + "." + time.substring(5, 7);
    }

    public boolean isLanguageEnable(int languageId) {
        return (languageId > 0 && languageId <= 9);
    }

    public String getLanguageNameById(int languageId) {
        if (languageId < 0 || languageId > 9)
            return "未知";
        if (mLanguageIdNames == null) {
            mLanguageIdNames = new HashMap<>();
            mLanguageIdNames.put("0", "未指定");
            mLanguageIdNames.put("1", "英语");
            mLanguageIdNames.put("2", "俄语");
            mLanguageIdNames.put("3", "德语");
            mLanguageIdNames.put("4", "法语");
            mLanguageIdNames.put("5", "西班牙语");
            mLanguageIdNames.put("6", "意大利语");
            mLanguageIdNames.put("7", "阿拉伯语");
            mLanguageIdNames.put("8", "朝鲜语");
            mLanguageIdNames.put("9", "日语");
        }

        return mLanguageIdNames.get(String.valueOf(languageId));
    }

    public String getLanguageLevelById(int languageId) {
        return (languageId == 4 ? "母语" : (languageId == 3 ? "流利" : (languageId == 2 ? "良好" : (languageId == 1 ? "一般" : "未知"))));
    }

    public String getSkillLevelById(int level) {
        return (level == 4 ? "精通" : (level == 3 ? "良好" : (level == 2 ? "熟练" : (level == 1 ? "一般" : "未知"))));
    }

    public int getSkillLevelScoreById(int level) {
        return (level == 4 ? 100 : (level == 3 ? 75 : (level == 2 ? 50 : (level == 1 ? 25 : 0))));
    }
}
