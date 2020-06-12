package com.baza.android.bzw.businesscontroller.find.updateengine.presenter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.ResumeUpdateTransformParamBean;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeDetailBean;
import com.baza.android.bzw.bean.resume.ResumeSearchBean;
import com.baza.android.bzw.bean.resume.ResumeUpdatedContentResultBean;
import com.baza.android.bzw.bean.searchfilterbean.SearchFilterInfoBean;
import com.baza.android.bzw.bean.updateengine.SuggestEnableUpdateTipResultBean;
import com.baza.android.bzw.bean.updateengine.UpdateResumeWrapperBean;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IResumeUpdateCardView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.ResumeUpdateDao;
import com.baza.android.bzw.events.IDefaultEventsSubscriber;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.log.logger.ResumeUpdateLogger;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/11/7.
 * Title：
 * Note：
 */

public class ResumeUpdateCardPresenter extends BasePresenter {
    private static final int READY_TO_LOAD_COUNT = 10;
    private IResumeUpdateCardView mResumeUpdateCardView;
    private List<ResumeBean> mEnableUpdateList = new ArrayList<>();
    private SearchFilterInfoBean mSearchFilterInfo;
    private SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean mSuggestLabelFilter;
    private SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean mSuggestEnableUpdateTitleFilter;
    private HashSet<String> mHasUpdated = new HashSet<>();
    private ResumeUpdateLogger mUpdateLogger = new ResumeUpdateLogger();
    private String mOnUpdatingResumeId;
    private String mSingleUpdateResumeId;
    private boolean mJustShowEnableUpdateContent = true;
    private boolean mOnLoadingData;
    private int mTotalCount;
    private int mInitPosition;

    public ResumeUpdateCardPresenter(IResumeUpdateCardView resumeUpdateCardView, Intent intent) {
        this.mResumeUpdateCardView = resumeUpdateCardView;
        mSingleUpdateResumeId = intent.getStringExtra("singleUpdateResumeId");
        ResumeUpdateTransformParamBean mResumeUpdateTransformPara = (ResumeUpdateTransformParamBean) BZWApplication.getApplication().getCachedTransformData(CommonConst.STR_TRANSFORM_UPDATE_RESUME_PARAM);
        if (mResumeUpdateTransformPara != null) {
            mInitPosition = mResumeUpdateTransformPara.position;
            mTotalCount = mResumeUpdateTransformPara.totalCount;
            if (mResumeUpdateTransformPara.enableUpdateList != null) {
                mEnableUpdateList.addAll(mResumeUpdateTransformPara.enableUpdateList);
            }
            mSearchFilterInfo = mResumeUpdateTransformPara.searchFilterInfoBean;
            mSuggestLabelFilter = mResumeUpdateTransformPara.suggestLabelFilter;
            mSuggestEnableUpdateTitleFilter = mResumeUpdateTransformPara.suggestEnableUpdateTitleFilter;
        }
    }

    @Override
    public void initialize() {
        if (!TextUtils.isEmpty(mSingleUpdateResumeId))
            mResumeUpdateCardView.callSetUpUpdateSingleResumeView();
    }

    public int getAllUpdateCount() {
        return (TextUtils.isEmpty(mSingleUpdateResumeId) ? mEnableUpdateList.size() : 1);
    }

    public String getTargetUpdateResumeId(int position) {
        return (TextUtils.isEmpty(mSingleUpdateResumeId) ? mEnableUpdateList.get(position).candidateId : mSingleUpdateResumeId);
    }

//    public List<ResumeBean> getEnableUpdateList() {
//        return mEnableUpdateList;
//    }

    public int getInitPosition() {
        return mInitPosition;
    }

    public boolean isJustShowEnableUpdateContent() {
        return mJustShowEnableUpdateContent;
    }

    public void setJustShowEnableUpdateContent(boolean mIsJustShowEnableUpdateContent) {
        this.mJustShowEnableUpdateContent = mIsJustShowEnableUpdateContent;
    }

    public boolean enableScanNext(int currentIndex) {
        if ((currentIndex + 1) < mEnableUpdateList.size()) {
            onLoadMore(currentIndex);
            return true;
        }
        return false;
    }

    public boolean onLoadMore(int currentIndex) {
        if (!TextUtils.isEmpty(mSingleUpdateResumeId))
            return false;
        if (mEnableUpdateList.size() >= mTotalCount && mTotalCount != 0)
            return false;
        if ((mEnableUpdateList.size() - currentIndex - 1) <= READY_TO_LOAD_COUNT) {
            loadData();
            return true;
        }
        return false;
    }

    private boolean isCurrentHasUpdated(String resumeId) {
        return mHasUpdated.contains(resumeId);
    }

    private boolean isCurrentUpdating(String resumeId) {
        return (resumeId != null && mOnUpdatingResumeId != null && resumeId.equals(mOnUpdatingResumeId));
    }

    private void loadData() {
        if (mOnLoadingData)
            return;
        mOnLoadingData = true;
        ResumeUpdateDao.loadResumeEnableUpdateList(ResumeUpdateDao.buildSearchParam(0, false, mEnableUpdateList, mSearchFilterInfo, mSuggestLabelFilter, mSuggestEnableUpdateTitleFilter), new IDefaultRequestReplyListener<ResumeSearchBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeSearchBean data, int errorCode, String errorMsg) {
                mOnLoadingData = false;
                if (success && data != null && data.recordList != null && !data.recordList.isEmpty()) {
                    mEnableUpdateList.addAll(data.recordList);
                    mResumeUpdateCardView.callRefreshList();
                }
            }
        });
    }

    public void prepareToUpdate(int currentItem) {
        String resumeId;
        if (TextUtils.isEmpty(mSingleUpdateResumeId)) {
            resumeId = mEnableUpdateList.get(currentItem).candidateId;
            if (isCurrentHasUpdated(resumeId)) {
                mResumeUpdateCardView.callScanNext();
                return;
            }
            if (isCurrentUpdating(resumeId)) {
                mResumeUpdateCardView.callShowToastMessage(null, R.string.on_updating_resume);
                return;
            }
        } else
            resumeId = mSingleUpdateResumeId;
        UpdateResumeWrapperBean wrapperBean = mResumeUpdateCardView.callGetSelectedUpdateContent();
        if (wrapperBean == null)
            return;
        doUpdate(resumeId, wrapperBean, mResumeUpdateCardView.callGetEnableUpdateContentData());
    }

    private void doUpdate(final String resumeId, final UpdateResumeWrapperBean wrapperBean, final ResumeUpdatedContentResultBean.Data enableUpdateContent) {
        if (wrapperBean == null || !wrapperBean.isEnableUpdate()) {
            mResumeUpdateCardView.callShowToastMessage(null, R.string.please_selected_update_content);
            return;
        }
        wrapperBean.candidateId = resumeId;
        mOnUpdatingResumeId = resumeId;
        mResumeUpdateCardView.callShowProgress(null, true);
        ResumeUpdateDao.updateResumeContent(wrapperBean.candidateId, wrapperBean, new IDefaultRequestReplyListener<ResumeDetailBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeDetailBean candidateDetailBean, int errorCode, String errorMsg) {
                mResumeUpdateCardView.callCancelProgress();
                if (success) {
                    if (enableUpdateContent == null || enableUpdateContent.current == null || enableUpdateContent.target == null)
                        return;
                    mUpdateLogger.sendSingleUpdateLog(mResumeUpdateCardView.callGetBindActivity(), resumeId, enableUpdateContent.current.completion, enableUpdateContent.target.completion);

                    mResumeUpdateCardView.callShowToastMessage(null, R.string.update_success_hint);
                    mHasUpdated.add(resumeId);
                    if (candidateDetailBean != null) {
                        wrapperBean.updateStatus = candidateDetailBean.updateStatus;
                        wrapperBean.currentCompletion = candidateDetailBean.currentCompletion;
                        UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_RESUME_UPDATE_BY_ENGINE, candidateDetailBean, null);
                        UIEventsObservable.getInstance().postEvent(IDefaultEventsSubscriber.class, ActionConst.ACTION_EVENT_UPDATE_AMOUNT_CHANGED, -1, null);
                    }
                    //单份简历更新
                    if (!TextUtils.isEmpty(mSingleUpdateResumeId)) {
                        Activity activity = mResumeUpdateCardView.callGetBindActivity();
                        if (activity == null || activity.isFinishing())
                            return;
                        activity.setResult(Activity.RESULT_OK);
                        BZWApplication.getApplication().cacheTransformData(CommonConst.STR_TRANSFORM_RESUME_UPDATED_CONTENT_KEY, wrapperBean);
                        activity.finish();
                        return;
                    }
                    if (mOnUpdatingResumeId != null && mOnUpdatingResumeId.equals(resumeId)) {
                        mResumeUpdateCardView.callScanNext();
                        mOnUpdatingResumeId = null;
                    }
                    return;
                }
                mOnUpdatingResumeId = null;
                if (errorCode == CustomerRequestAssistHandler.NET_REQUEST_UPDATE_AMOUNT_NOT_ENOUGH_ERROR) {
                    mResumeUpdateCardView.callReachUpdateLimitDialog();
                    return;
                }
                mResumeUpdateCardView.callShowToastMessage(errorMsg, 0);
            }
        });
           }
}
