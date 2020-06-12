package com.baza.android.bzw.log;

import android.app.Activity;
import android.app.Application;
import androidx.fragment.app.Fragment;

import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.track.io.core.TrackAgent;
import com.baza.track.io.core.TrackConfig;

/**
 * Created by Vincent.Lei on 2017/5/17.
 * Title：
 * Note：
 */

public class ReportAgent {
    public static void initScenarioType(Application application, String processName) {
        TrackAgent.getInstance().init(application, processName, new TrackConfig.SetClient().projectCode("bzbox").productCode("bzbox-android"));
    }

    public static void setUserInfo(UserInfoBean userInfo) {
        TrackConfig.SetClient setClient = TrackAgent.getInstance().getSetClient();
        setClient.projectCode("bzbox").productCode("bzbox-android").bui(userInfo != null ? userInfo.unionId : null);
        TrackAgent.getInstance().refreshSetClient(setClient);
    }

    public static void clearUserInfo() {
        TrackConfig.SetClient setClient = TrackAgent.getInstance().getSetClient();
        setClient.projectCode("bzbox").productCode("bzbox-android").bui(null);
        TrackAgent.getInstance().refreshSetClient(setClient);
    }

    public static void setPageCode(Object o, String pageCode) {
        TrackAgent.getInstance().setPageCode(o, pageCode);
    }

    public static void onCreate(Activity activity, String splashScreen) {
    }

    public static void onCreate(Fragment fragment, String splashScreen) {
    }

    public static void onResume(Activity activity, String splashScreen) {
        TrackAgent.getInstance().setPageName(activity, splashScreen);
    }

    public static void onResume(Fragment fragment, String splashScreen) {
        TrackAgent.getInstance().setPageName(fragment, splashScreen);
    }

    public static void onPause(Activity activity, String splashScreen) {
    }

    public static void onPause(Fragment fragment, String splashScreen) {
    }

    public static void onDestroy(Activity activity) {
    }

    public static void onDestroy(Fragment fragment) {
    }

    public static void sendEventLog(Object page, String pageCode, String eventCode, String eventData) {
        TrackAgent.getInstance().makeEventLog(page, pageCode, eventCode, eventData);
    }

    public static void sendEventLog(Object page, String eventCode, String eventData) {
        TrackAgent.getInstance().makeEventLog(page, eventCode, eventData);
    }
}
