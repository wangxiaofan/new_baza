package com.baza.android.bzw.businesscontroller.resume.recommend.presenter;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.recommend.RecommendBean;
import com.baza.android.bzw.businesscontroller.resume.recommend.viewinterface.IRecommendView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.RecommendDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.logger.RecommendLogger;
import com.bznet.android.rcbox.R;
import com.slib.utils.AppUtil;
import com.slib.utils.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent.Lei on 2019/8/15.
 * Title：
 * Note：
 */
public class RecommendPresenter extends BasePresenter {
    private IRecommendView mRecommendView;
    private List<RecommendBean> mOriginList = new ArrayList<>();
    private List<RecommendBean> mDataList = new ArrayList<>();
    private int mCurrentYear, mCurrentMonth;
    private int requestId;
    private int mDelayCount;
    private int mCompleteCount;
    private Integer mSelectedStated;
    private RecommendLogger mRecommendLogger = new RecommendLogger();
    private String mDateYMDToday;
    private Comparator<RecommendBean> comparator = new Comparator<RecommendBean>() {
        @Override
        public int compare(RecommendBean o1, RecommendBean o2) {
            int result = o2.getStateCompareValue(mDateYMDToday) - o1.getStateCompareValue(mDateYMDToday);
            return result == 0 ? (int) (o2.getTimeMsec() - o1.getTimeMsec()) : result;
        }
    };

    public RecommendPresenter(IRecommendView recommendView, int currentYear, int currentMonth) {
        this.mRecommendView = recommendView;
        this.mCurrentYear = currentYear;
        this.mCurrentMonth = currentMonth;
        this.mDateYMDToday = DateUtil.longMillions2FormatDate(new Date().getTime(), DateUtil.SDF_RECOMMEND);
    }

    @Override
    public void initialize() {
        loadRecommendList();
    }

    public List<RecommendBean> getDataList() {
        return mDataList;
    }

    public void setDate(int currentYear, int currentMonth) {
        this.mCurrentYear = currentYear;
        this.mCurrentMonth = currentMonth;
    }

    public void loadRecommendList() {
        requestId++;
        final int currentRequestId = requestId;
        String startRemindTime = mCurrentYear + "-" + (mCurrentMonth >= 10 ? mCurrentMonth : ("0" + mCurrentMonth)) + "-01 00:00:00";
        String endRemindTime = mCurrentYear + "-" + (mCurrentMonth >= 10 ? mCurrentMonth : ("0" + mCurrentMonth)) + "-" + AppUtil.getDayCountOfMonth(mCurrentYear, mCurrentMonth) + " 23:59:59";
        RecommendDao.loadRecommendList(startRemindTime, endRemindTime, new IDefaultRequestReplyListener<List<RecommendBean>>() {
            @Override
            public void onRequestReply(boolean success, List<RecommendBean> recommendBeans, int errorCode, String errorMsg) {
                if (currentRequestId != requestId)
                    return;
                mRecommendView.callCancelLoadingView(success, errorCode, errorMsg);
                mOriginList.clear();
                if (success && recommendBeans != null)
                    mOriginList.addAll(recommendBeans);
                selectAndFillDataListByState();
                if (!mDataList.isEmpty())
                    Collections.sort(mDataList, comparator);
                calculateStateCount();
                mRecommendView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
            }
        });
    }

    private void calculateStateCount() {
        mDelayCount = 0;
        mCompleteCount = 0;
        RecommendBean recommend;
        for (int i = 0, size = mOriginList.size(); i < size; i++) {
            recommend = mOriginList.get(i);
            if (recommend.status == RecommendBean.STATE_DELAY)
                mDelayCount++;
            else if (recommend.status == RecommendBean.STATE_COMPLETE)
                mCompleteCount++;
        }
        mRecommendView.updateStateCountView(mDelayCount, mCompleteCount);
    }

    public void setRecommendComplete(final RecommendBean recommend) {
        mRecommendView.callShowProgress(null, true);
        RecommendDao.setRecommendComplete(recommend.id, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mRecommendView.callCancelProgress();
                if (success) {
                    if (recommend.status == RecommendBean.STATE_DELAY)
                        mDelayCount--;
                    recommend.status = RecommendBean.STATE_COMPLETE;
                    Collections.sort(mDataList, comparator);
                    mRecommendView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    mRecommendView.callShowToastMessage(null, R.string.recommend_complete_set_success);
                    mCompleteCount++;
                    mRecommendView.updateStateCountView(mDelayCount, mCompleteCount);
                    mRecommendLogger.sendCompleteRecommend(mRecommendView.callGetBindActivity(), recommend.resumeId);
                    return;
                }
                mRecommendView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void deleteRecommend(final RecommendBean recommend) {
        mRecommendView.callShowProgress(null, true);
        RecommendDao.deleteRecommend(recommend.resumeId, recommend.id, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mRecommendView.callCancelProgress();
                if (success) {
                    mRecommendView.callShowToastMessage(null, R.string.delete_success);
                    mDataList.remove(recommend);
                    mOriginList.remove(recommend);
                    mRecommendView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    if (recommend.status == RecommendBean.STATE_COMPLETE)
                        mCompleteCount--;
                    if (recommend.status == RecommendBean.STATE_DELAY)
                        mDelayCount--;
                    mRecommendView.updateStateCountView(mDelayCount, mCompleteCount);
                    mRecommendLogger.sendDeleteRecommend(mRecommendView.callGetBindActivity(), recommend.resumeId);
                    return;
                }
                mRecommendView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void selectRecommendByState(int state) {
        if (state != RecommendBean.STATE_COMPLETE && state != RecommendBean.STATE_DELAY && state != RecommendBean.STATE_NORMAL)
            return;
        mSelectedStated = state;
        selectAndFillDataListByState();
        Collections.sort(mDataList, comparator);
        mRecommendView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
    }

    public void clearSelectRecommendState() {
        mSelectedStated = null;
        selectAndFillDataListByState();
        Collections.sort(mDataList, comparator);
        mRecommendView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
    }

    private void selectAndFillDataListByState() {
        mDataList.clear();
        if (mSelectedStated != null) {
            int state = mSelectedStated;
            RecommendBean recommendBean;
            for (int i = 0, size = mOriginList.size(); i < size; i++) {
                recommendBean = mOriginList.get(i);
                if (state == recommendBean.status)
                    mDataList.add(recommendBean);
            }
        } else
            mDataList.addAll(mOriginList);
    }
}
