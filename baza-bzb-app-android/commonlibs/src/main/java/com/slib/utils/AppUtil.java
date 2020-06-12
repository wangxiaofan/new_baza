package com.slib.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import com.google.android.material.tabs.TabLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AppUtil {
    public static final String EMAIL_PATTEN = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    public static final String DOMAIN_NAME_PATTEN = "^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$";

    public static boolean isNetworkAvailable(Context context) {
        boolean wifiConnected;
        boolean mobileConnected;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
        @SuppressLint("MissingPermission") NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
//        }else{
//
//        }
        return (wifiConnected | mobileConnected);
    }

    public static String objectToJson(Object object) {

        return JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
    }

    public static <T> T jsonToObject(String json, Class<T> cls) {
        try {
            return JSON.parseObject(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static String getDeviceId(Context context) {
//        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        String deviceId = manager.getDeviceId();
//        if (null == deviceId) {
//            deviceId = "";
//        }
//        return deviceId;
//    }
//
//    public static int getVersion(Context context) {
//        int version = -1;
//        try {
//            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
//        } catch (NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return version;
//    }
//
//    public static String getVersionName(Context context) {
//        String versionName = null;
//        try {
//            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
//        } catch (NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return versionName;
//    }

//    public static int sp2px(Context context, float spValue) {
//        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
//        return (int) (spValue * fontScale + 0.5f);
//    }
//
//    public static int dp2px(Context context, float dpValue) {
//        final float density = context.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * density + 0.5f);
//    }
//
//    public static int getDeviceScreenWidth(Context context) {
//        return context.getResources().getDisplayMetrics().widthPixels;
//    }
//
//    public static int getDeviceScreenHeight(Context context) {
//        return context.getResources().getDisplayMetrics().heightPixels;
//    }

//    public static void resetViewSize(final View view, int width, int height) {
//        LayoutParams lp = view.getLayoutParams();
//        lp.width = width;
//        lp.height = height;
//        view.setLayoutParams(lp);
//    }
//
//    public static void resetViewSizeByImageSize(final RatingBar mRatingBar, Resources mResources, int imageId) {
//        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
//        bmpFactoryOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(mResources, imageId, bmpFactoryOptions);
//        int width = mRatingBar.getLayoutParams().width;
//        int height = bmpFactoryOptions.outHeight;
//        LayoutParams lp = mRatingBar.getLayoutParams();
//        lp.width = width;
//        lp.height = height;
//        mRatingBar.setLayoutParams(lp);
//    }

    //    public static void showSoftInput(View view) {
//        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.showSoftInput(view, 0);
//        }
//    }
//
//    public static void hideSoftInput(View view, Context context) {
//        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED);
//        }
//    }
//
    public static String formatFeeFloat(float oil) {
        DecimalFormat fnum = new DecimalFormat("##0.00");
        float dd = Float.parseFloat(fnum.format(oil));
        float n = oil - dd;
        if (n >= 0.01) {
            dd = dd + 0.01f;
        }
        return fnum.format(dd);
    }

    public static String formatFeeDouble(double oil) {
        DecimalFormat fnum = new DecimalFormat("##0.00");
//        double dd = Double.parseDouble(fnum.format(oil));
//        double n = oil - dd;
//        if (n >= 0.01) {
//            dd = dd + 0.01;
//        }
//        return fnum.format(dd);
        return fnum.format(oil);
    }

    public static boolean checkPhone(String str) {
        if (str == null || str.length() != 11) {
            return false;
        }
        String checkRules = "1+[3456789]+[0-9]{9}";
        Pattern regex = Pattern.compile(checkRules);
        Matcher matcher = regex.matcher(str);
        boolean flag = matcher.matches();
        return flag;
    }

//    public static final void pickPhotos(Activity activity, int requestCode, int maxChoseCount, ArrayList<String> oldData) {
//        PickPhotosActivity.launch(activity, requestCode, maxChoseCount, oldData);
//    }

//    public static final String getSinglePhotoPath(Intent data) {
//        if (data != null) {
//            return data.getStringExtra("url");
//        }
//        return null;
//    }

//    public static final ArrayList<String> getMulityPhotoPaths(Intent data) {
//        if (data != null) {
//            return (ArrayList<String>) data.getSerializableExtra("urlList");
//        }
//        return null;
//    }


    /**
     * 验证身份证
     */
//    public static final boolean checkIsIdCard(String idcard) {
//
//        if (TextUtils.isEmpty(idcard) || (idcard.length() != 15 && idcard.length() != 18)) {
//            return false;
//        }
//        if (idcard.matches("^[1-9]\\d{icon_collect}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$") || idcard.matches("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$")) {
//            return true;
//        }
//        return false;
//    }
    public static final int[] getIntegerIdList(Context context, int resId) {
        TypedArray ta = context.getResources().obtainTypedArray(resId);
        int[] ids = new int[ta.length()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = ta.getResourceId(i, 0);
        }
        ta.recycle();
        return ids;
    }


    public static final String getProcessName(Context context) {
        String processName = null;

        // ActivityManager
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;
//                    Log.d("RCBox", "processName = " + processName);
//                    Log.d("RCBox", "pid = " + info.pid);
                    break;
                }
            }
            return processName;
        }
    }


    public static void makeCall(Activity activity, String number) {
        if (activity == null || number == null)
            return;
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            ToastUtil.showToast(activity, "您的设备不具备打电话功能!");
        }
    }

    public static void sendSmMessage(Activity activity, String number, String defaultMessage) {
        if (activity == null)
            return;
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + (number == null ? "" : number)));
        if (defaultMessage != null)
            intent.putExtra("sms_body", defaultMessage);
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            ToastUtil.showToast(activity, "您的设置不具备发送短信功能!");
        }

    }

    public static void sendEmail(Activity activity, String email) {
        String[] receiver = new String[]{email};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, receiver);
        try {
            activity.startActivity(Intent.createChooser(emailIntent, "邮件发送"));
        } catch (Exception e) {
            ToastUtil.showToast(activity, "您的设置不具备发送邮件功能!");
        }
    }


    public static void copyToClipboard(Context context, String text) {
        ClipboardManager myClipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
    }

    public static boolean checkEmail(String email) {
        return pattenCheck(email, EMAIL_PATTEN);
    }

    public static boolean checkDomainName(String domainName) {
        return pattenCheck(domainName, DOMAIN_NAME_PATTEN);
    }

    public static boolean pattenCheck(String str, String patten) {
        if (TextUtils.isEmpty(str))
            return false;
        return str.matches(patten);
    }

//    public static boolean isChineseChar(String str) {
//        if (TextUtils.isEmpty(str))
//            return false;
//        boolean temp = false;
//        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
//        Matcher m = p.matcher(str);
//        if (m.find()) {
//            temp = true;
//        }
//        return temp;
//    }

    public static int getChineseCharCount(String str) {
        if (TextUtils.isEmpty(str))
            return 0;
        int count = 0;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        while (m.find())
            ++count;
        return count;
    }

    public static String getApplicationMetaData(Context context, String dataName) {
        String msg = "";
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            msg = appInfo.metaData.getString(dataName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }

    /**
     * 半角转全角
     *
     * @return 全角字符串.
     */
//    public static String toSBC(String input) {
//        if (TextUtils.isEmpty(input))
//            return null;
//        char c[] = input.toCharArray();
//        for (int i = 0; i < c.length; i++) {
//            if (c[i] == ' ') {
//                c[i] = '\u3000';
//            } else if (c[i] < '\177') {
//                c[i] = (char) (c[i] + 65248);
//
//            }
//        }
//        return new String(c);
//    }
    public static View getTargetVisibleView(int targetPosition, ListView listView) {
        if (targetPosition >= (listView.getFirstVisiblePosition() - listView.getHeaderViewsCount()) && targetPosition <= listView.getLastVisiblePosition() - listView.getHeaderViewsCount()) {
            return listView.getChildAt(targetPosition - listView.getFirstVisiblePosition() + listView.getHeaderViewsCount());
        }
        return null;
    }

    public static String formatTob(int num) {
        if (num >= 1000) {
            String temp = String.valueOf(num);
            int length = temp.length();
            int index;
            switch (length % 3) {
                case 1:
                    index = 1;
                    break;
                case 2:
                    index = 2;
                    break;
                default:
                    index = 0;
                    break;
            }
            StringBuilder stringBuilder = new StringBuilder();
            if (index > 0)
                stringBuilder.append(temp, 0, index).append(",");
            while (index < length) {
                stringBuilder.append(temp, index, index + 3);
                if (index + 3 < length)
                    stringBuilder.append(",");
                index += 3;
            }
            return stringBuilder.toString();
        }
        return String.valueOf(num);
    }

    public static String getPackageName(Context context) {
        return context.getApplicationInfo().packageName;
    }

    /**
     * URL转换为链接
     *
     * @param urlText
     * @return String
     * @author Boyer
     */
    public static List<LinkData> urlToLink(String urlText) {
        // 匹配的条件选项为结束为空格(半角和全角)、换行符、字符串的结尾或者遇到其他格式的文本
        // 以http...或www开头
        // 中间为任意内容，惰性匹配
        // 结束条件
        if (TextUtils.isEmpty(urlText))
            return null;
        String regexp = "(((http|ftp|https|file)://)|((?<!((http|ftp|https|file)://))www\\.))" + ".*?" + "(?=(&nbsp;|\\s|　|<br />|$|[<>]))";
        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(urlText);
        List<LinkData> list = null;
        LinkData linkData;
        while (matcher.find()) {
            if (list == null)
                list = new ArrayList<>();
            linkData = new LinkData();
            linkData.start = matcher.start();
            linkData.end = matcher.end();
            linkData.url = matcher.group();
            list.add(linkData);
        }
        return list;
    }

    public static class LinkData {
        public String url;
        public int start;
        public int end;
    }

    /**
     * 初始化一个Drawable
     */
    public static Drawable drawableInit(int drawableId, Resources resources) {
        Drawable drawable = resources.getDrawable(drawableId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return drawable;
    }

    /**
     * 置空一个Drawable所有引用
     */
    public static void nonCallBackDrawable(Drawable drawable) {
        if (drawable != null)
            drawable.setCallback(null);
    }

    public static String getUserAgent(Context context) {
        String userAgent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        return userAgent;
    }

    public static void setTabLayoutIndicatorMargin(Context context, TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout ll_tab = null;
        try {
            ll_tab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) (context.getResources().getDisplayMetrics().density * leftDip);
        int right = (int) (context.getResources().getDisplayMetrics().density * rightDip);

        for (int i = 0; i < ll_tab.getChildCount(); i++) {
            View child = ll_tab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }

    public static void modifyEditTextCursorDrawable(EditText editText, int drawable) {
        try {//修改光标的颜色（反射）
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(editText, drawable);
        } catch (Exception ignored) {
            //ignore
        }
    }

    public static int getFitScreenHeight(Context context) {
        int height = context.getResources().getDisplayMetrics().heightPixels;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && context instanceof Activity) {
            DisplayMetrics mRealDisplayMetrics = new DisplayMetrics();
            Window window = ((Activity) (context)).getWindow();
            View decorView = window.getDecorView();
            View contentView = decorView.findViewById(android.R.id.content);
            Rect rect = new Rect();
            decorView.getWindowVisibleDisplayFrame(rect);
            //虚拟导航栏隐藏
            Display d = ((Activity) (context)).getWindowManager().getDefaultDisplay();
            d.getRealMetrics(mRealDisplayMetrics);
            if (contentView.getBottom() == decorView.getBottom())
                height = mRealDisplayMetrics.heightPixels;
        }
        return height;
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean isRunNian(int year) {
        return (year % 4 == 0 && year % 100 != 0 || year % 400 == 0);
    }

    public static int getDayCountOfMonth(int year, int month) {
        if (month == 2) {
            return isRunNian(year) ? 29 : 28;
        }
        return (month == 4 || month == 6 || month == 9 || month == 11) ? 30 : 31;
    }
}
