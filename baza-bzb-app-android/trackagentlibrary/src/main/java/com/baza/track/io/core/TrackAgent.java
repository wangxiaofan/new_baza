package com.baza.track.io.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.baza.track.io.BaZaIo;
import com.baza.track.io.bean.TrackEventBean;
import com.baza.track.io.constant.TrackConst;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by Vincent.Lei on 2018/4/17.
 * Title：
 * Note：
 */
public class TrackAgent {
    private TrackAgent() {
    }

    private static TrackAgent mTrackAgent = new TrackAgent();
    private TrackConfig mConfig = new TrackConfig();
    private PageCache mPageCache = new PageCache();
    private Application mApplication;
    private WeakReference<Object> mCurrentPageRef;
    private WeakReference<Object> mLastPageRef;
    private Handler mHandler = new Handler(Looper.myLooper());
    private HeartBeatTask mHeartBeatTask = new HeartBeatTask();
    private Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks;
    private static final long HEART_BEAT_TIME = 60 * 1000;
    private int mResumeActivityCount;

    public static TrackAgent getInstance() {
        return mTrackAgent;
    }

    public void init(Application application, String processName, TrackConfig.SetClient setClient) {
        mApplication = application;
        TrackConst.Db.DB_PROCESS = processName;
        mConfig.init(mApplication, setClient);
        listenActivityLiftStyle();
        UploadService.getInstance().upload(mApplication);
    }

    public TrackConfig.SetClient getSetClient() {
        return mConfig.getSetClient();
    }

    public void refreshSetClient(TrackConfig.SetClient setClient) {
        mConfig.setSetClient(setClient);
    }

    public void setPageName(Object o, String pageName) {
        mPageCache.setPageName(o, pageName);
    }

    public void setPageCode(Object o, String pageCode) {
        mPageCache.setPageCode(o, pageCode);
    }

    public void onPageResume(Object o) {
        if (shouldFragmentPageEventIgnore(o))
            return;
        pageResumeEvent(o);
    }

    public void onPagePause(Object o) {
        if (shouldFragmentPageEventIgnore(o))
            return;
//        mPageCache.onPagePause(o);
        makePageViewLog(o, true, "PageClose");
    }

    private void pageResumeEvent(Object o) {
        mPageCache.onPageResume(o);
        mLastPageRef = mCurrentPageRef;
        mCurrentPageRef = new WeakReference<>(o);
        makePageViewLog(o, false, "PageOpen");
        heartBeatPageLog();
    }

    public void onFragmentSpecialVisibleChanged(Class fromClass, Object pageObj, boolean hide) {
        if (pageObj instanceof androidx.lifecycle.ReportFragment)
            return;
        if (fromClass == BaZaIo.class) {
            if (hide)
                makePageViewLog(pageObj, true, "PageClose");
            else
                pageResumeEvent(pageObj);
        }
    }

    public void onPageDestroy(Object o) {
        mPageCache.onPageDestroy(o);
    }


    private void listenActivityLiftStyle() {
        if (mActivityLifecycleCallbacks != null)
            mApplication.unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        if (mActivityLifecycleCallbacks == null)
            mActivityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {
                    mResumeActivityCount++;
                    onPageResume(activity);
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    onPagePause(activity);
                }

                @Override
                public void onActivityStopped(Activity activity) {
                    mResumeActivityCount--;
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    onPageDestroy(activity);
                }
            };
        mApplication.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    public void makeClickEvent(View view) {
        if (view == null)
            return;
        JSONObject jsonObject;
        try {
            jsonObject = mConfig.getClickEventCommonJsonObject();
            Object pageObj = BaZaIo.getFragmentCache(view);
            if (pageObj == null) {
                Context context = view.getContext();
                if (context instanceof Activity)
                    pageObj = context;
                else if ((mCurrentPageRef != null && mCurrentPageRef.get() != null))
                    pageObj = mCurrentPageRef.get();
            }

            if (pageObj != null) {
                jsonObject.put(TrackConst.PAGE_VIEW_ID, mPageCache.getPageViewId(pageObj));
                jsonObject.put(TrackConst.TITLE, mPageCache.getPageName(pageObj));
                jsonObject.put(TrackConst.PAGE_CODE, mPageCache.getPageCode(pageObj));
            }
            String path = PathUtil.getViewPath(view);
            jsonObject.put(TrackConst.EVENT_CODE, Util.getStringMD5(path));

            JSONObject eventData = new JSONObject();
            eventData.put("viewPath", path);
            eventData.put("viewClass", view.getClass().getName());
            if (view instanceof TextView) {
                String textInfo = ((TextView) view).getText().toString();
                if (textInfo.length() > 20)
                    textInfo = textInfo.substring(0, 20);
                eventData.put("viewText", textInfo);
            }

            jsonObject.put(TrackConst.EVENT_DATA, eventData.toString());
        } catch (JSONException e) {
            jsonObject = null;
            e.printStackTrace();
        }
        if (jsonObject != null) {
            TrackEventBean eventData = new TrackEventBean(TrackConst.EventType.EVENT_TYPE_CLICK, jsonObject.toString());
            DbIO.getInstance(mApplication).saveEvent(eventData);
            TrackLog.d(eventData.value);
        }

    }

    private void makePageViewLog(Object page, boolean onPause, String pageData) {
        JSONObject jsonObject;
        try {
            jsonObject = mConfig.getPageEventCommonJsonObject();
            if (!TextUtils.isEmpty(pageData))
                jsonObject.put(TrackConst.PAGE_DATA, pageData);
            jsonObject.put(TrackConst.TITLE, mPageCache.getPageName(page));
            jsonObject.put(TrackConst.PAGE_CODE, mPageCache.getPageCode(page));
            jsonObject.put(TrackConst.PAGE_VIEW_ID, mPageCache.getPageViewId(page));
            if (!onPause && mLastPageRef != null && mLastPageRef.get() != null && mCurrentPageRef != null && mCurrentPageRef.get() != null && mCurrentPageRef.get() != mLastPageRef.get()) {
                jsonObject.put(TrackConst.PREVIOUS_PAGE_CODE, mPageCache.getPageCode(mLastPageRef.get()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        if (jsonObject != null) {
            TrackEventBean trackEventBean = new TrackEventBean(TrackConst.EventType.EVENT_TYPE_PAGE_REVIEW, jsonObject.toString());
            DbIO.getInstance(mApplication).saveEvent(trackEventBean);
            TrackLog.d(trackEventBean.value);
        }
    }

    public void makeEventLog(Object page, String pageCode, String eventCode, String eventData) {
        if (TextUtils.isEmpty(eventCode))
            return;
        JSONObject jsonObject;
        try {
            jsonObject = mConfig.getClickEventCommonJsonObject();
            jsonObject.put(TrackConst.TITLE, mPageCache.getPageName(page));
            jsonObject.put(TrackConst.PAGE_CODE, mPageCache.getPageCode(page));
            jsonObject.put(TrackConst.PAGE_VIEW_ID, mPageCache.getPageViewId(page));
            jsonObject.put(TrackConst.EVENT_CODE, eventCode);
            if (!TextUtils.isEmpty(pageCode))
                jsonObject.put(TrackConst.PAGE_CODE, pageCode);
            if (!TextUtils.isEmpty(eventData))
                jsonObject.put(TrackConst.EVENT_DATA, eventData);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        if (jsonObject != null) {
            TrackEventBean trackEventBean = new TrackEventBean(TrackConst.EventType.EVENT_TYPE_CLICK, jsonObject.toString());
            DbIO.getInstance(mApplication).saveEvent(trackEventBean);
            TrackLog.d(trackEventBean.value);
        }
    }

    public void makeEventLog(Object page, String eventCode, String eventData) {
        makeEventLog(page, null, eventCode, eventData);
    }

//    private Object getFragmentShowInActivity(Activity activity) {
//        if (activity instanceof android.support.v4.app.FragmentActivity) {
//            android.support.v4.app.FragmentActivity sf = (android.support.v4.app.FragmentActivity) activity;
//            List<Fragment> list = sf.getSupportFragmentManager().getFragments();
//            if (list != null)
//                for (android.support.v4.app.Fragment f : list) {
//                    if (f.isVisible())
//                        return f;
//                }
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            List<android.app.Fragment> list = activity.getFragmentManager().getFragments();
//            if (list != null)
//                for (android.app.Fragment f : list) {
//                    if (f.isVisible())
//                        return f;
//                }
//        }
//        return null;
//    }

    private boolean shouldFragmentPageEventIgnore(Object pageObj) {
        if (pageObj instanceof androidx.lifecycle.ReportFragment)
            return true;
        if (pageObj instanceof Fragment && ((Fragment) pageObj).isHidden())
            return true;
        return pageObj instanceof android.app.Fragment && ((android.app.Fragment) pageObj).isHidden();
    }

    private class HeartBeatTask implements Runnable {
        private WeakReference<Object> mHeartBeatPageRef;

        void refreshTask(WeakReference<Object> heartBeatPageRef) {
            mHeartBeatPageRef = heartBeatPageRef;
        }

        @Override
        public void run() {
            if (mResumeActivityCount <= 0)
                return;
            if (mHeartBeatPageRef == null || mHeartBeatPageRef != mCurrentPageRef || mCurrentPageRef.get() == null)
                return;
            makePageViewLog(mCurrentPageRef.get(), false, null);
            mHandler.postDelayed(mHeartBeatTask, HEART_BEAT_TIME);
        }
    }

    private void heartBeatPageLog() {
        mHandler.removeCallbacksAndMessages(null);
        mHeartBeatTask.refreshTask(mCurrentPageRef);
        mHandler.postDelayed(mHeartBeatTask, HEART_BEAT_TIME);
    }
}
