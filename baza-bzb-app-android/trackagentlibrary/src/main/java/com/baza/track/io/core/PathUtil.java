package com.baza.track.io.core;
import android.app.Fragment;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.baza.track.io.BaZaIo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangdan on 2018/3/5.
 */

public class PathUtil {
    private static final int NO_POSITION = -1;

    private static String getResIdName(Context context, View view) {
        final int viewId = view.getId();
        if (View.NO_ID == viewId) {
            return null;
        } else {
            return ResourceReader.Ids.getInstance(context).nameForId(viewId);
        }
    }

    public static String getViewPath(View view) {
        HashMap<Integer, Pair<Integer, String>> aliveFragments = BaZaIo.getAliveFragMap();
        StringBuilder builder = new StringBuilder();
        StringBuilder element = new StringBuilder();
        ViewParent parent = view.getParent();
        View child = view;
        while (parent != null && parent instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) parent;
            // 根据parent推算[index]部分
            String validIndexSegment;
            if (group instanceof AdapterView) {
                validIndexSegment = buildAdapterViewItemIndex(child, group);
            } else if (ReflectorUtil.isInstanceOfV7RecyclerView(group)) {
                validIndexSegment = buildRecyclerViewItemIndex(child, group);
            } else if (ReflectorUtil.isInstanceOfV4ViewPager(group)) {
                validIndexSegment = buildViewPagerItemIndex(aliveFragments, child, (ViewPager) group);
            } else {
                int index = getChildIndex(group, child);
                String indexStr = (index == NO_POSITION ? "-" : String.valueOf(index));
                validIndexSegment = "[" + indexStr + "]";
            }

            String elementFrag = buildFragmentSegment(aliveFragments, child, validIndexSegment);
            if (TextUtils.isEmpty(elementFrag)) {
                element.delete(0, element.length());
//                element.append("/").append(child.getClass().getSimpleName()).append(validIndexSegment);
                element.append("/").append(child.getClass().getSimpleName());
                String childDistinctId = getResIdName(group.getContext().getApplicationContext(), child);
                if (childDistinctId != null)
                    element.append("#").append(childDistinctId);
                builder.insert(0, element.toString());
                if ("android:content".equals(childDistinctId))
                    break;
            } else
                builder.insert(0, elementFrag);
            child = group;
            parent = group.getParent();
        }
        return builder.insert(0, getMainWindowType()).toString();
    }

    private static String buildViewPagerItemIndex(HashMap<Integer, Pair<Integer, String>> aliveFragments, View child, ViewPager group) {
        int index = NO_POSITION;
        // ViewPager
        ViewPager _group = group;
        try {
            if (!ReflectorUtil.isV4ViewPagerCached) {
                ReflectorUtil.cacheV4ViewPager();// reflects fields and caches the result
            }
            if (ReflectorUtil.isV4ViewPagerCached) {
                List items = (List) ReflectorUtil.fieldmItems.get(_group);
                int position = _group.getCurrentItem();
                for (int i = 0; items != null && i < items.size(); i++) {
                    Object item = items.get(i);
                    int itemPosition = (int) ReflectorUtil.fieldPosition.get(item);
                    if (itemPosition == position) {
                        Object currPagerObject = ReflectorUtil.fieldObject.get(item);
                        boolean isViewFromObject = _group.getAdapter().isViewFromObject(child, currPagerObject);
                        if (isViewFromObject) {
                            index = position;
                            if (currPagerObject instanceof Fragment || ReflectorUtil.isInstanceOfV4Fragment(currPagerObject)) {
                                int viewCode = (currPagerObject instanceof Fragment) ? ((Fragment) currPagerObject).getView().hashCode() : ((androidx.fragment.app.Fragment) currPagerObject).getView().hashCode();
                                aliveFragments.put(currPagerObject.hashCode(), new Pair<Integer, String>(viewCode, currPagerObject.getClass().getSimpleName()));
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //ignore
        }
        if (index == NO_POSITION) {
            index = getChildIndex(group, child);
        }
        return "[" + index + "]";
    }

    private static String buildRecyclerViewItemIndex(View child, ViewGroup group) {
        int index = getChildPositionForRecyclerView(child, group);
        return "[" + index + "]";
    }

    private static String buildAdapterViewItemIndex(View child, ViewGroup group) {
        int index = ((AdapterView) group).getPositionForView(child);
        if (group instanceof ExpandableListView) {
            String exListIndicator;
            ExpandableListView _group = (ExpandableListView) group;
            long l = _group.getExpandableListPosition(index);
            int groupIndex;
            if (ExpandableListView.getPackedPositionType(l) == ExpandableListView.PACKED_POSITION_TYPE_NULL) {
                if (index < _group.getHeaderViewsCount()) {
                    exListIndicator = "[header:" + index + "]";// header
                } else {
                    groupIndex = index - (_group.getCount() - _group.getFooterViewsCount());
                    exListIndicator = "[footer:" + groupIndex + "]";// footer
                }
            } else {
                groupIndex = ExpandableListView.getPackedPositionGroup(l);
                int childIndex = ExpandableListView.getPackedPositionChild(l);
                if (childIndex != -1) {
                    exListIndicator = "[group:" + groupIndex + ",child:" + childIndex + "]";// group/child
                } else {
                    exListIndicator = "[group:" + groupIndex + "]";// group
                }
            }
            return exListIndicator;
        }

        return "[" + index + "]";
    }

    private static String buildFragmentSegment(HashMap<Integer, Pair<Integer, String>> aliveFragments, View child, String validIndexSegment) {
        // deal with Fragment
        StringBuilder element = new StringBuilder();
        if (aliveFragments != null) {
            Iterator<Map.Entry<Integer, Pair<Integer, String>>> iterator = aliveFragments.entrySet().iterator();
            Map.Entry<Integer, Pair<Integer, String>> entry;
            Pair<Integer, String> pair;
            int viewCode;
            String fragName;
            while (iterator.hasNext()) {
                entry = iterator.next();
                pair = entry.getValue();
                viewCode = pair.first;
                fragName = pair.second;
                if (viewCode == child.hashCode()) {
                    element.append("/")
                            .append(fragName);
//                            .append(validIndexSegment);
                    break;
                }
            }
        }
        return element.toString();
    }

    private static int getChildPositionForRecyclerView(View child, ViewGroup group) {
        if ((ReflectorUtil.isV7RecyclerViewLoaded) && ((group instanceof RecyclerView))) {
            RecyclerView localRecyclerView = (RecyclerView) group;
            if (ReflectorUtil.hasChildAdapterPosition) {
                return localRecyclerView.getChildAdapterPosition(child);
            }
            return localRecyclerView.getChildPosition(child);
        }
        if ((ReflectorUtil.isV7RecyclerViewCached) && (group.getClass().equals(ReflectorUtil.sClassRecyclerView))) {
            try {
                return ((Integer) ReflectorUtil.methodItemPosition.invoke(group, new Object[]{child})).intValue();
            } catch (Exception e) {
                //ignore
            }
        }
        return NO_POSITION;
    }

    private static int getChildIndex(ViewGroup parent, View child) {
        if (parent == null) {
            return NO_POSITION;
        }
        final String childIdName = getResIdName(parent.getContext().getApplicationContext(), child);
        if (!TextUtils.isEmpty(childIdName))
            return 0;
        String childClassName = child.getClass().getName();
        int index = 0;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View brother = parent.getChildAt(i);
            if (!hasClassName(brother, childClassName))
                continue;
            String brotherIdName = getResIdName(parent.getContext().getApplicationContext(), brother);
            if (null != childIdName && !childIdName.equals(brotherIdName))
                continue;
            if (brother == child)
                return index;
            index++;
        }

        return NO_POSITION;
    }

    private static boolean hasClassName(Object o, String className) {
        Class<?> klass = o.getClass();
        while (klass.getCanonicalName() != null) {
            if (klass.getCanonicalName().equals(className)) {
                return true;
            }

            if (klass == Object.class) {
                break;
            }

            klass = klass.getSuperclass();
        }
        return false;
    }

    private static String getMainWindowType() {
        return "/RootView";
    }

    public static boolean match(String currViewPath, String viewPath) {
        if (TextUtils.isEmpty(currViewPath) || TextUtils.isEmpty(viewPath)) {
            return false;
        }
        try {
            Pattern pattern = Pattern.compile(viewPath);
            Matcher matcher = pattern.matcher(currViewPath);
            return matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
