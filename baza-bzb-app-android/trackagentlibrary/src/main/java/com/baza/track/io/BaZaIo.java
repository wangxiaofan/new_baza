package com.baza.track.io;

import android.content.DialogInterface;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;


import androidx.fragment.app.Fragment;

import com.baza.track.io.core.TrackAgent;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BaZaIo {
    private static HashMap<Integer, Pair<Integer, String>> mAliveFragMap = new HashMap<>();
    private static HashMap<Integer, WeakReference<Object>> mAliveWeakFragCache = new HashMap<>();

    public static HashMap<Integer, Pair<Integer, String>> getAliveFragMap() {
        return mAliveFragMap;
    }

    public static void onClick(View view) {
        TrackAgent.getInstance().makeClickEvent(view);
    }

    public static void onClick(Object object, DialogInterface dialogInterface, int which) {
    }

    public static void onItemClick(Object object, AdapterView parent, View view, int position, long id) {
        TrackAgent.getInstance().makeClickEvent(view);
    }

    public static void onItemSelected(Object object, AdapterView parent, View view, int position, long id) {
        onItemClick(object, parent, view, position, id);
    }

    public static void onGroupClick(Object thisObject, ExpandableListView parent, View v, int groupPosition, long id) {
    }

    public static void onChildClick(Object thisObject, ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

    }

    public static void onStopTrackingTouch(Object thisObj, SeekBar seekBar) {

    }

    public static void onRatingChanged(Object thisObj, RatingBar ratingBar, float rating, boolean fromUser) {

    }

    public static void onCheckedChanged(Object object, RadioGroup radioGroup, int checkedId) {
        if (radioGroup != null)
            TrackAgent.getInstance().makeClickEvent(radioGroup.findViewById(checkedId));
    }

    public static void onCheckedChanged(Object object, CompoundButton button, boolean isChecked) {
        TrackAgent.getInstance().makeClickEvent(button);
    }

    public static void onFragmentResume(Object obj) {
        addAliveFragment(obj);
        TrackAgent.getInstance().onPageResume(obj);
    }

    public static void onFragmentPause(Object obj) {
        removeAliveFragment(obj);
        TrackAgent.getInstance().onPagePause(obj);
    }

    public static void onFragmentDestroy(Object obj) {
        TrackAgent.getInstance().onPageDestroy(obj);
    }

    private static boolean checkFragment(Fragment paramFragment) {
        return true;
    }

    private static boolean checkFragment(android.app.Fragment paramFragment) {
        return true;
    }

    public static void setFragmentUserVisibleHint(Object obj, boolean isUserVisibleHint) {
        if (isUserVisibleHint)
            addAliveFragment(obj);
        else
            removeAliveFragment(obj);
        TrackAgent.getInstance().onFragmentSpecialVisibleChanged(BaZaIo.class, obj, isUserVisibleHint);
    }

    public static void onFragmentHiddenChanged(Object fragment, boolean hidden) {
        setFragmentUserVisibleHint(fragment, !hidden);
    }

    private static void addAliveFragment(Object obj) {
        View view = null;
        if (obj instanceof android.app.Fragment) {
            view = ((android.app.Fragment) obj).getView();
        } else if (obj instanceof Fragment) {
            view = ((Fragment) obj).getView();
        }
        if (null != view) {
            int viewCode = view.hashCode();
            mAliveFragMap.put(obj.hashCode(), new Pair<>(viewCode, obj.getClass().getName()));
            mAliveWeakFragCache.put(viewCode, new WeakReference<Object>(obj));
        }
    }

    private static void removeAliveFragment(Object obj) {
        if (null != obj) {
            mAliveFragMap.remove(obj.hashCode());
            View view = null;
            if (obj instanceof android.app.Fragment) {
                view = ((android.app.Fragment) obj).getView();
            } else if (obj instanceof Fragment) {
                view = ((Fragment) obj).getView();
            }
            if (null != view)
                mAliveWeakFragCache.remove(view.hashCode());
        }
    }

    public static String getFragmentName(View targetView) {
        if (targetView == null)
            return null;
        Iterator<Map.Entry<Integer, Pair<Integer, String>>> iterator = mAliveFragMap.entrySet().iterator();
        Pair<Integer, String> pair;
        int viewCode;
        ViewParent parent;
        while (iterator.hasNext()) {
            pair = iterator.next().getValue();
            viewCode = pair.first;
            if (viewCode == targetView.hashCode())
                return pair.second;
            parent = targetView.getParent();
            while (parent != null && parent instanceof ViewGroup) {
                if (viewCode == parent.hashCode())
                    return pair.second;
                parent = parent.getParent();
            }
        }
        return null;
    }

    public static Object getFragmentCache(View targetView) {
        if (targetView == null)
            return null;
        int viewCode = targetView.hashCode();
        if (mAliveWeakFragCache.containsKey(viewCode))
            return mAliveWeakFragCache.get(viewCode).get();
        WeakReference<Object> ref;
        ViewParent parent = targetView.getParent();
        while (parent != null && parent instanceof ViewGroup) {
            ref = mAliveWeakFragCache.get(parent.hashCode());
            if (ref != null)
                return ref.get();
            parent = parent.getParent();
        }
        return null;
    }
}
