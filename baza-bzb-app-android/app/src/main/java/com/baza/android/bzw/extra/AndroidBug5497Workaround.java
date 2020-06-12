package com.baza.android.bzw.extra;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by LW on 2016/8/5.
 * Title :
 * Note :
 */
public class AndroidBug5497Workaround {

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    public static void assistActivity(Activity activity, int statusBarH) {
        new AndroidBug5497Workaround(activity, statusBarH);
    }

    private int statusBarH, navigationBarHeight;
    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private Resources mResources;

    private AndroidBug5497Workaround(Activity activity, int statusBarH) {
        this.statusBarH = statusBarH;
        this.mResources = activity.getResources();
        this.navigationBarHeight = getNavigationBarHeight();
//        LogUtil.d("getNavigationBarHeight = " + navigationBarHeight);
//        LogUtil.d("statusBarH = " + statusBarH);
        FrameLayout content = activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
//            LogUtil.d("heightDifference = " + heightDifference);
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference + statusBarH;
            } else {
                // keyboard probably just became hidden
                //(heightDifference == (statusBarH + navigationBarHeight) ? navigationBarHeight : 0) 适配部分设备底部虚拟键盘显示隐藏
                frameLayoutParams.height = usableHeightSansKeyboard - (heightDifference == (statusBarH + navigationBarHeight) ? navigationBarHeight : 0);
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

    private int getNavigationBarHeight() {
        int resourceId = mResources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0)
            return mResources.getDimensionPixelSize(resourceId);
        return 0;
    }

//    public static boolean checkDeviceHasNavigationBar(Context activity) {
//
//        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
////        boolean hasMenuKey = ViewConfiguration.get(activity).hasPermanentMenuKey();
//        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
//
//        if (!hasBackKey) {
//            // 做任何你需要做的,这个设备有一个导航栏
//            return true;
//        }
//        return false;
//    }
}
