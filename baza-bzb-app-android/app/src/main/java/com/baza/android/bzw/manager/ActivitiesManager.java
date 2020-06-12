package com.baza.android.bzw.manager;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.businesscontroller.im.IMConst;
import com.baza.android.bzw.businesscontroller.login.LoginActivity;

import java.util.ArrayList;


/**
 * Created by Vincent.Lei on 2016/12/15.
 * Title : 所有Activity页面管理类
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class ActivitiesManager implements Application.ActivityLifecycleCallbacks {
    private ArrayList<Activity> mActivityArrayList = new ArrayList<>(10);
    private int mResumeActivityCount;
    private boolean mIsOnFinishAllExceptStackTopActivityForSinglePlatform;
    private static ActivitiesManager mInstance = new ActivitiesManager();

    public static ActivitiesManager getInstance() {
        return mInstance;
    }

    private ActivitiesManager() {
    }

    public void init(Application application) {
        application.unregisterActivityLifecycleCallbacks(this);
        application.registerActivityLifecycleCallbacks(this);
    }

    public void finishAllActivitiesButCurrent(Activity current) {
        if (!mActivityArrayList.isEmpty()) {
            Activity activity;
            for (int i = 0, size = mActivityArrayList.size(); i < size; i++) {
                activity = mActivityArrayList.get(i);
                if (activity != current)
                    activity.finish();
            }
        }
    }

    public Activity getTopStackActivity() {
        if (!mActivityArrayList.isEmpty())
            for (int i = mActivityArrayList.size() - 1; i >= 0; i--) {
                if (!mActivityArrayList.get(i).isFinishing())
                    return mActivityArrayList.get(i);
            }
        return null;
    }

    public void reset() {
        mActivityArrayList.clear();
        mIsOnFinishAllExceptStackTopActivityForSinglePlatform = false;
    }

    public Activity finishAllExceptStackTopActivityForSinglePlatform() {
        if (mIsOnFinishAllExceptStackTopActivityForSinglePlatform)
            return null;
        Activity mActivityResumed = getTopStackActivity();
        if (mActivityResumed == null || mActivityResumed.isFinishing())
            return null;
        Activity temp;
        if (!mActivityArrayList.isEmpty()) {
            for (int i = 0, size = mActivityArrayList.size(); i < size; i++) {
                temp = mActivityArrayList.get(i);
                if (temp != mActivityResumed)
                    temp.finish();
            }
        }
        if (mActivityResumed instanceof LoginActivity)
            return null;
        if (mIsOnFinishAllExceptStackTopActivityForSinglePlatform)
            return null;
        mIsOnFinishAllExceptStackTopActivityForSinglePlatform = true;
        return mActivityResumed;
    }

//    public boolean isAllStopped() {
//        return mResumeActivityCount <= 0;
//    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActivityArrayList.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        mResumeActivityCount++;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        mResumeActivityCount--;
        if (mResumeActivityCount <= 0) {
            IMManager.getInstance(BZWApplication.getApplication()).setChattingAccount(null, IMConst.SESSION_TYPE_NONE, false);
            IMManager.getInstance(BZWApplication.getApplication()).setFriendNotifyEnable(true);
        }
        if (activity.isFinishing())
            mActivityArrayList.remove(activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
