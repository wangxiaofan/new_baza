package com.baza.android.bzw.businesscontroller.resume.smartgroup.presenter;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeSearchBean;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.viewinterface.ISFGroupDetailView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.dao.SmartGroupDao;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Vincent.Lei on 2018/8/31.
 * Title：
 * Note：
 */
public class SFGroupDetailPresenter extends BasePresenter {
    private ISFGroupDetailView mGroupDetailView;
    private List<ResumeBean> mDataList = new ArrayList<>();
    private int mOffset;
    private int mTotalCount;
    private int mJobHuntingCount;
    private String mLastKeyword;
    private String mGroupId;
    private int mSearchId;
    private HashMap<String, ResumeBean> mSelectedResumes = new HashMap<>();
    private boolean mJobHunterTagSet;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ResumeDao.SearchParam mSearchParam;

    public SFGroupDetailPresenter(ISFGroupDetailView groupDetailView, String groupId) {
        this.mGroupDetailView = groupDetailView;
        this.mGroupId = groupId;
    }

    @Override
    public void initialize() {
        subscribeEvents(true);
        loadResumesInGroup(false, true);
    }

    @Override
    public void onDestroy() {
        subscribeEvents(false);
        mHandler.removeCallbacksAndMessages(null);
    }

    public List<ResumeBean> getDataList() {
        return mDataList;
    }

    public HashMap<String, ResumeBean> getSelectedResumes() {
        return mSelectedResumes;
    }

    public void prepareToEdit(boolean edit) {
        if (edit)
            mSelectedResumes.clear();
    }

    public String getLastKeyword() {
        return mLastKeyword;
    }

    public void onKeywordChanged(String keyword) {
        mLastKeyword = keyword;
        loadResumesInGroup(true, true);
//        if (!TextUtils.isEmpty(mLastKeyword)) {
//            NameListSearchFilterBean searchFilterBean = new NameListSearchFilterBean();
//            searchFilterBean.keyword = mLastKeyword;
//            NameListDao.saveSearchFilterToLocalDb(UserInfoManager.getInstance().getUserInfo().unionId, searchFilterBean);
//        }
    }

    public void onOnlySearchJobFinderSet(boolean enable) {
        mJobHunterTagSet = enable;
        loadResumesInGroup(true, true);
    }

    public void delayToLoadResumesInGroup(final boolean showLoadView, final boolean refresh) {
        if (showLoadView)
            mGroupDetailView.callShowLoadingView(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadResumesInGroup(showLoadView, refresh);
            }
        }, 5000);
    }

    public void loadResumesInGroup(boolean showLoadView, final boolean refresh) {
        if (showLoadView)
            mGroupDetailView.callShowLoadingView(null);
        mSearchId++;
        final int requestId = mSearchId;
        mOffset = (refresh ? 0 : mDataList.size());
        if (mSearchParam == null)
            mSearchParam = new ResumeDao.SearchParam();
        mSearchParam.offset(mOffset).pageSize(CommonConst.DEFAULT_PAGE_SIZE).isJobHunting(mJobHunterTagSet).isSearchMineResume(true).isReturnJobHuntingCount(true).build();
        HashMap<String, String> param = mSearchParam.getKeyMap();
        param.put("bzwGroupId", mGroupId);
        if (!TextUtils.isEmpty(mLastKeyword))
            param.put("keywords", mLastKeyword);

        ResumeDao.doSearch(mSearchParam, new IDefaultRequestReplyListener<ResumeSearchBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeSearchBean resumeSearchBean, int errorCode, String errorMsg) {
                if (requestId != mSearchId)
                    return;
                mGroupDetailView.callCancelLoadingView(success, errorCode, errorMsg);
                if (mOffset == 0) {
                    mDataList.clear();
                    mSelectedResumes.clear();
                }
                if (success && resumeSearchBean != null) {
                    if (resumeSearchBean.recordList != null)
                        mDataList.addAll(resumeSearchBean.recordList);
                    mJobHuntingCount = resumeSearchBean.jobHuntingCount;
                    mTotalCount = resumeSearchBean.totalCount;
                    mGroupDetailView.callUpdateLoadAllDataView(mDataList.size() >= resumeSearchBean.totalCount);
                }
                mGroupDetailView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                mGroupDetailView.callUpdateLoadCountView(mTotalCount, mJobHuntingCount);
            }
        });

    }

    public void removeAllResume() {
        removeResumesOfGroup(null);
    }

    public void removeResume(ResumeBean resumeBean) {
        removeResumesOfGroup(resumeBean);
    }

    private void removeResumesOfGroup(final ResumeBean resumeBean) {
        List<String> list = new ArrayList<>(resumeBean != null ? 1 : mSelectedResumes.size());
        if (resumeBean != null)
            list.add(resumeBean.candidateId);
        else {
            Iterator<String> iterator = mSelectedResumes.keySet().iterator();
            while (iterator.hasNext())
                list.add(iterator.next());
        }
        if (list.isEmpty()) {
            mGroupDetailView.callShowToastMessage(null, R.string.please_select_resume_to_remove);
            return;
        }
        mGroupDetailView.callShowProgress(null, true);
        SmartGroupDao.removeResumesFromGroup(mGroupId, list, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mGroupDetailView.callCancelProgress();
                if (success) {
                    mGroupDetailView.callShowToastMessage(null, R.string.remove_success);
                    if (resumeBean != null) {
                        mTotalCount--;
                        if (resumeBean.isJobHunting && mJobHuntingCount > 0)
                            mJobHuntingCount--;
                        mDataList.remove(resumeBean);
                    } else {
                        Iterator<Map.Entry<String, ResumeBean>> iterator = mSelectedResumes.entrySet().iterator();
                        ResumeBean resumeBeanMap;
                        while (iterator.hasNext()) {
                            resumeBeanMap = iterator.next().getValue();
                            mTotalCount--;
                            if (resumeBeanMap.isJobHunting && mJobHuntingCount > 0)
                                mJobHuntingCount--;
                            mDataList.remove(resumeBeanMap);
                        }
                    }
                    mSelectedResumes.clear();
                    mGroupDetailView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    mGroupDetailView.callUpdateLoadCountView(mTotalCount, mJobHuntingCount);
                    mGroupDetailView.callUpdateSelectedCountView(mSelectedResumes.size());
                    if (mDataList.isEmpty())
                        delayToLoadResumesInGroup(true, true);
                    return;
                }
                mGroupDetailView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void selectedAll() {
        ResumeBean resumeBean;
        for (int i = 0, size = mDataList.size(); i < size; i++) {
            resumeBean = mDataList.get(i);
            mSelectedResumes.put(resumeBean.candidateId, resumeBean);
        }
        mGroupDetailView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
        mGroupDetailView.callUpdateSelectedCountView(mDataList.size());
    }

    private void subscribeEvents(boolean isSubscribe) {
        if (isSubscribe) {
            UIEventsObservable.getInstance().subscribeEvent(IResumeEventsSubscriber.class, this, new IResumeEventsSubscriber() {
                @Override
                public boolean isFilterByTag(Object sendTag) {
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
                    if (mSelectedResumes != null && mSelectedResumes.size() > 0)
                        mSelectedResumes.remove(resumeBean.candidateId);
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
}
