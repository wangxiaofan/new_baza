package com.slib.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;


public class ScreenUtil {
    public static int screenWidth;
    public static int screenHeight;
    public static float density;
    public static float scaleDensity;
    public static float xdpi;
    public static float ydpi;
    public static int densityDpi;
    public static boolean isXXXHDpi;

    public static int dip2px(float dipValue) {
        return (int) (dipValue * density + 0.5f);
    }

    public static int px2dip(float pxValue) {
        return (int) (pxValue / density + 0.5f);
    }

    public static void init(Context context) {
        if (null == context) {
            return;
        }
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        density = dm.density;
        scaleDensity = dm.scaledDensity;
        xdpi = dm.xdpi;
        ydpi = dm.ydpi;
        densityDpi = dm.densityDpi;
        isXXXHDpi = (px2dip(screenWidth) >= 360);
    }


//    public static int getStatusBarHeight(Context context) {
//        Class<?> c = null;
//        Object obj = null;
//        Field field = null;
//        int x = 0, sbar = 0;
//        try {
//            c = Class.forName("com.android.internal.R$dimen");
//            obj = c.newInstance();
//            field = c.getField("status_bar_height");
//            x = Integer.parseInt(field.get(obj).toString());
//            sbar = context.getResources().getDimensionPixelSize(x);
//        } catch (Exception E) {
//            E.printStackTrace();
//        }
//        return sbar;
//
//    }

    public static int getNavBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

//    public static int getImageMaxEdge() {
//        return (int) (165.0 / 320.0 * screenWidth);
//    }
//
//    public static int getImageMinEdge() {
//        return (int) (76.0 / 320.0 * screenWidth);
//    }
}
