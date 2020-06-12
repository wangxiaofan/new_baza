package com.baza.android.bzw.businesscontroller.resume.smartgroup.presenter;

import android.text.TextUtils;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeSearchBean;
import com.baza.android.bzw.bean.smartgroup.GroupTimeSelectorResultBean;
import com.baza.android.bzw.bean.smartgroup.SmartGroupFoldersResultBean;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.viewinterface.ICommonGroupDetailView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.CollectionDao;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.dao.SmartGroupDao;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.logger.ResumeLogger;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Vincent.Lei on 2018/9/3.
 * Title：
 * Note：
 */
public class CommonGroupDetailPresenter extends BasePresenter {
    private ICommonGroupDetailView mGroupDetailView;
    private int mType;
    private String mLastKeyword;
    private List<ResumeBean> mDataList = new ArrayList<>();
    private int mSearchId, mOffset, mJobHuntingCount, mTotalCount;
    private boolean mJobHunterTagSet;
    private Object mFilterTag = new Object();
    private SmartGroupFoldersResultBean.SmartGroupFolderBean mSmartGroupFolderBean;
    private TimeSelector mTimeSelector;
    private String mTimeStart, mTimeEnd;
    private ResumeLogger mSearchLogger = new ResumeLogger();
    private ResumeDao.SearchParam mSearchParam;

    public CommonGroupDetailPresenter(ICommonGroupDetailView groupDetailView, int type, SmartGroupFoldersResultBean.SmartGroupFolderBean smartGroupFolderBean) {
        this.mGroupDetailView = groupDetailView;
        this.mType = type;
        this.mSmartGroupFolderBean = smartGroupFolderBean;
    }

    @Override
    public void initialize() {
        subscribeEvents(true);
        loadInitData(false, true);
    }

    @Override
    public void onDestroy() {
        subscribeEvents(false);
    }

    public TimeSelector getTimeSelector() {
        return mTimeSelector;
    }

    public List<ResumeBean> getDataList() {
        return mDataList;
    }

    public void onKeywordChanged(String keyword) {
        mLastKeyword = keyword;
        loadResumesInGroup(true, true);
    }

    public String getLastKeyword() {
        return mLastKeyword;
    }

    public void onOnlySearchJobFinderSet(boolean enable) {
        mJobHunterTagSet = enable;
        loadResumesInGroup(true, true);
    }

    public void loadInitData(boolean showLoadView, final boolean refresh) {
        if (mType == CommonConst.SmartGroupType.GROUP_TYPE_TIME) {
            loadTimeSelector(showLoadView, refresh);
            return;
        }
        loadResumesInGroup(showLoadView, refresh);
    }

    private void loadTimeSelector(final boolean showLoadView, final boolean refresh) {
        if (showLoadView)
            mGroupDetailView.callShowLoadingView(null);
        final String year = (mSmartGroupFolderBean.groupName != null && mSmartGroupFolderBean.groupName.length() > 4 ? mSmartGroupFolderBean.groupName.substring(0, 4) : null);
        if (year == null) {
            mGroupDetailView.callCancelLoadingView(false, 0, null);
            return;
        }
        SmartGroupDao.loadTimeSelectorFormGroup(year, new IDefaultRequestReplyListener<GroupTimeSelectorResultBean>() {
            @Override
            public void onRequestReply(boolean success, GroupTimeSelectorResultBean groupTimeSelectorResultBean, int errorCode, String errorMsg) {
                if (!success || groupTimeSelectorResultBean == null || groupTimeSelectorResultBean.data == null || groupTimeSelectorResultBean.data.length == 0) {
                    mGroupDetailView.callCancelLoadingView(success, errorCode, errorMsg);
                    return;
                }
                HashMap<String, HashSet<String>> monthDay = new HashMap<>();
                HashSet<String> daySet;
                String[] array;
                for (int i = 0; i < groupTimeSelectorResultBean.data.length; i++) {
                    array = groupTimeSelectorResultBean.data[i].split("-");
                    daySet = monthDay.get(array[1]);
                    if (daySet == null)
                        daySet = new HashSet<>();
                    daySet.add(array[2]);
                    monthDay.put(array[1], daySet);
                }
                mTimeSelector = new TimeSelector();
                mTimeSelector.addYear(year);
                mTimeSelector.addMonthDay(monthDay);
                loadResumesInGroup(showLoadView, refresh);
            }
        });
    }

    public void onTimeSelected(String year, String month, String day, boolean load) {
        if (month == null) {
            mTimeStart = year + "-01-01 00:00:00";
            mTimeEnd = String.valueOf(Integer.parseInt(year) + 1) + "-01-01 00:00:00";
        } else if (day == null) {
            mTimeStart = year + "-" + month + "-01 00:00:00";
            int nextMonth = Integer.parseInt(month) + 1;
            if (nextMonth > 12) {
                nextMonth = 1;
                year = String.valueOf(Integer.parseInt(year) + 1);
            }
            mTimeEnd = year + "-" + (nextMonth >= 10 ? String.valueOf(nextMonth) : ("0" + nextMonth)) + "-01 00:00:00";
        } else {
            mTimeStart = year + "-" + month + "-" + day + " 00:00:00";
            mTimeEnd = year + "-" + month + "-" + day + " 23:59:59";
        }
        if (load)
            loadResumesInGroup(true, true);
    }

    public void loadResumesInGroup(boolean showLoadView, final boolean refresh) {
        if (showLoadView)
            mGroupDetailView.callShowLoadingView(null);
        mSearchId++;
        final int requestId = mSearchId;
        mOffset = (refresh ? 0 : mDataList.size());
        if (mSearchParam == null)
            mSearchParam = new ResumeDao.SearchParam();
        mSearchParam.isSearchMineResume(true).offset(mOffset).pageSize(CommonConst.DEFAULT_PAGE_SIZE).isJobHunting(mJobHunterTagSet).isReturnJobHuntingCount(true).build();
        HashMap<String, String> param = mSearchParam.getKeyMap();
        if (!TextUtils.isEmpty(mLastKeyword))
            param.put("keywords", mLastKeyword);
        switch (mType) {
            case CommonConst.SmartGroupType.GROUP_TYPE_DEGREE:
                param.put("degree", mSmartGroupFolderBean.key);
                break;
            case CommonConst.SmartGroupType.GROUP_TYPE_WORK_EXPERIENCE:
                param.put("yearOfExperienceRegion", mSmartGroupFolderBean.key);
                break;
            case CommonConst.SmartGroupType.GROUP_TYPE_TIME:
                param.put("storageDateStart", mTimeStart);
                param.put("storageDateEnd", mTimeEnd);
                break;
            case CommonConst.SmartGroupType.GROUP_TYPE_TITLE:
                param.put("standardTitle", mSmartGroupFolderBean.groupName);
                break;
            case CommonConst.SmartGroupType.GROUP_TYPE_COMPANY:
                param.put("standardCompany", mSmartGroupFolderBean.groupName);
                break;
        }

        ResumeDao.doSearch(mSearchParam, new IDefaultRequestReplyListener<ResumeSearchBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeSearchBean resumeSearchBean, int errorCode, String errorMsg) {
                if (requestId != mSearchId)
                    return;
                mGroupDetailView.callCancelLoadingView(success, errorCode, errorMsg);
                if (mOffset == 0)
                    mDataList.clear();
                if (success && resumeSearchBean != null) {
                    if (resumeSearchBean.recordList != null)
                        mDataList.addAll(resumeSearchBean.recordList);
                    mTotalCount = resumeSearchBean.totalCount;
                    mJobHuntingCount = resumeSearchBean.jobHuntingCount;
                    mGroupDetailView.callUpdateLoadAllDataView(mDataList.size() >= resumeSearchBean.totalCount);
                    if (mOffset == 0) {
                        mSearchLogger.sendDoSearchLog(mGroupDetailView, mSearchParam.getKeyMap(), resumeSearchBean.totalCount, resumeSearchBean.recordList);
                    }
                }
                mGroupDetailView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                mGroupDetailView.callUpdateLoadCountView(mTotalCount, mJobHuntingCount);
            }
        });

    }

    public void collection(int position) {
        if (position < 0 || position >= mDataList.size())
            return;
        mGroupDetailView.callShowProgress(null, true);
        final ResumeBean resumeBean = mDataList.get(position);
        CollectionDao.doOrCancelCollection(resumeBean, new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(boolean success, String s, int errorCode, String errorMsg) {
                mGroupDetailView.callCancelProgress();
                if (success) {
                    resumeBean.collectStatus = (resumeBean.collectStatus == CommonConst.COLLECTION_NO ? CommonConst.COLLECTION_YES : CommonConst.COLLECTION_NO);
                    mGroupDetailView.callShowToastMessage(null, (resumeBean.collectStatus == CommonConst.COLLECTION_NO ? R.string.un_collection_success : R.string.collection_success));
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mDataList);
                    if (targetPosition != CommonConst.LIST_POSITION_NONE)
                        mGroupDetailView.callRefreshListItems(targetPosition);
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_COLLECTION_RESUME_CHANGED, resumeBean, mFilterTag);
                    return;
                }
                mGroupDetailView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    private void subscribeEvents(boolean isSubscribe) {
        if (isSubscribe) {
            UIEventsObservable.getInstance().subscribeEvent(IResumeEventsSubscriber.class, this, new IResumeEventsSubscriber() {
                @Override
                public boolean isFilterByTag(Object sendTag) {
                    return mFilterTag == sendTag;
                }

                @Override
                public boolean onResumeCollectionStatusChangedObserver(ResumeBean resumeBean, Object extra) {
                    if (resumeBean == null)
                        return false;
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mDataList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mDataList.get(targetPosition).collectStatus = resumeBean.collectStatus;
                    //只更新改变的Item
                    mGroupDetailView.callRefreshListItems(targetPosition);
                    return false;
                }

                @Override
                public boolean onResumeModifiedObserver(ResumeBean resumeBean, Object extra) {
                    if (resumeBean == null)
                        return false;
                    //通知简历被修改了
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mDataList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mDataList.get(targetPosition).refreshModifyInfo(resumeBean);
                    //只更新改变的Item
                    mGroupDetailView.callRefreshListItems(targetPosition);
                    return false;
                }

                @Override
                public boolean onResumeDeletedObserver(ResumeBean data, Object extra) {
                    //简历被删除
                    int targetPosition = ResumeDao.findTargetResumePosition(data, mDataList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    ResumeBean resumeBean = mDataList.remove(targetPosition);
                    //CommonConst.LIST_POSITION_NONE刷新整个列表
                    mGroupDetailView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    mTotalCount--;
                    if (resumeBean.isJobHunting && mJobHuntingCount > 0)
                        mJobHuntingCount--;
                    mGroupDetailView.callUpdateLoadCountView(mTotalCount, mJobHuntingCount);
                    return false;
                }

                @Override
                public boolean onResumeUpdateByEngine(ResumeBean data, Object extra) {
                    if (data == null)
                        return false;
                    //通知简历被修改了
                    int targetPosition = ResumeDao.findTargetResumePosition(data, mDataList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mDataList.get(targetPosition).refreshModifyInfo(data);
                    mDataList.get(targetPosition).updateStatus = ResumeBean.UpdateState.STATE_UPDATE_DONE;
                    //只更新改变的Item
                    mGroupDetailView.callRefreshListItems(targetPosition);
                    return false;
                }

            });
        } else {
            UIEventsObservable.getInstance().stopSubscribeEvent(IResumeEventsSubscriber.class, this);
        }
    }

    public static class TimeSelector {
        public ArrayList<String> yearList = new ArrayList<>(1);
        public ArrayList<String> monthList;
        public HashMap<String, ArrayList<String>> monthDaySet = new HashMap<>();

        private void addYear(String year) {
            yearList.add(year);
        }

        private void addMonthDay(HashMap<String, HashSet<String>> monthDay) {
            Iterator<Map.Entry<String, HashSet<String>>> iterator = monthDay.entrySet().iterator();
            monthList = new ArrayList<>(monthDay.size() + 1);
            monthList.add("全部");
            monthDaySet.put("全部", new ArrayList<String>(1) {
                {
                    add("全部");
                }
            });
            Map.Entry<String, HashSet<String>> setEntry;
            HashSet<String> daySet;
            ArrayList<String> dayList;
            while (iterator.hasNext()) {
                setEntry = iterator.next();
                monthList.add(setEntry.getKey());
                daySet = setEntry.getValue();
                dayList = new ArrayList<>(daySet.size() + 1);
                dayList.add("全部");
                monthDaySet.put(setEntry.getKey(), dayList);
                Iterator<String> ite = daySet.iterator();
                while (ite.hasNext())
                    dayList.add(ite.next());
                Collections.sort(dayList);
            }
            Collections.sort(monthList);
        }
    }
}
