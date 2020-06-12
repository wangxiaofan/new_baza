package com.baza.android.bzw.manager;

import com.baza.android.bzw.bean.recommend.RecommendBean;
import com.baza.android.bzw.dao.RecommendDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent.Lei on 2019/8/16.
 * Title：
 * Note：
 */
public class RecommendManager {
    private static final RecommendManager mInstance = new RecommendManager();
    private Calendar mCalendar = Calendar.getInstance();
    private final ArrayList<IRecommendListener> mListeners = new ArrayList<>(5);
    private List<RecommendBean> recommendList = new ArrayList<>(16);

    private RecommendManager() {
    }

    public interface IRecommendListener {
        void onUnCompletedRecommendOfTodayGet(List<RecommendBean> recommendList);
    }

    public static RecommendManager getInstance() {
        return mInstance;
    }

    public void registerListener(IRecommendListener listener) {
        if (listener == null)
            return;
        if (!mListeners.contains(listener))
            mListeners.add(listener);
        if (!recommendList.isEmpty())
            listener.onUnCompletedRecommendOfTodayGet(recommendList);
    }

    public void unRegisterListener(IRecommendListener listener) {
        if (listener == null)
            return;
        mListeners.remove(listener);
    }

    public void refresh() {
        loadRecommendDataOfToday();
    }

    private void loadRecommendDataOfToday() {
        mCalendar.setTime(new Date());
        int currentYear = mCalendar.get(Calendar.YEAR);
        int currentMonth = mCalendar.get(Calendar.MONTH) + 1;
        int currentDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        final String dateYMD = currentYear + "-" + (currentMonth >= 10 ? currentMonth : ("0" + currentMonth)) + "-" + (currentDay >= 10 ? currentDay : ("0" + currentDay));
        String startRemindTime = dateYMD + " 00:00:00";
        String endRemindTime = dateYMD + " 23:59:59";
        RecommendDao.loadRecommendList(startRemindTime, endRemindTime, new IDefaultRequestReplyListener<List<RecommendBean>>() {
            @Override
            public void onRequestReply(boolean success, List<RecommendBean> recommendBeans, int errorCode, String errorMsg) {
                if (success && recommendBeans != null && !recommendBeans.isEmpty()) {
                    RecommendBean recommend;
                    recommendList.clear();
                    for (int i = 0, size = recommendBeans.size(); i < size; i++) {
                        recommend = recommendBeans.get(i);
                        if (recommend.status == RecommendBean.STATE_NORMAL)
                            recommendList.add(recommend);
                    }
                    if (!recommendList.isEmpty()) {
                        for (int i = 0, size = mListeners.size(); i < size; i++) {
                            mListeners.get(i).onUnCompletedRecommendOfTodayGet(recommendList);
                        }
                    }
                }
            }
        });
    }
}
