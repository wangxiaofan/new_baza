package com.baza.android.bzw.businesscontroller.tracking.presenter;

import android.os.Handler;
import android.os.Looper;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.TrackingListBean;
import com.baza.android.bzw.bean.resume.TrackingSearchResultBean;
import com.baza.android.bzw.businesscontroller.tracking.viewinterface.ITrackingView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.dao.TrackingResumeDao;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;

import java.util.ArrayList;
import java.util.List;

public class TrackingPresenter extends BasePresenter {

    private ITrackingView iCompanyTalentView;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private List<TrackingListBean> mResumeList = new ArrayList<>();
    private TrackingResumeDao.SearchParam mSearchParam;
    public static final int SELF_TOAST_COLLECTION = 2;

    public TrackingPresenter(ITrackingView iCompanyTalentView) {
        this.iCompanyTalentView = iCompanyTalentView;
    }

    @Override
    public void initialize() {
        subscriberResumeEvents(true);
        loadMyResumeList(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriberResumeEvents(false);
    }

    public List<TrackingListBean> getDataList() {
        return mResumeList;
    }

    public void loadMyResumeList(final boolean refresh) {
        if (mSearchParam == null)
            mSearchParam = new TrackingResumeDao.SearchParam();
        mSearchParam.offset(refresh ? 0 : mResumeList.size()).pageSize(CommonConst.DEFAULT_PAGE_SIZE).build();
        TrackingResumeDao.doTrackingSearch(mSearchParam, new IDefaultRequestReplyListener<TrackingSearchResultBean>() {
            @Override
            public void onRequestReply(boolean success, TrackingSearchResultBean resumeSearchBean, int errorCode, String errorMsg) {
                iCompanyTalentView.callCancelLoadingView(success, errorCode, errorMsg);
                iCompanyTalentView.callCancelProgress();
                if (success) {
                    if (refresh)
                        mResumeList.clear();
                    if (resumeSearchBean != null && resumeSearchBean.data != null && resumeSearchBean.data.getData() != null && resumeSearchBean.data.getData().size() > 0) {
                        if (resumeSearchBean.data.getData().get(0).getTrackingListData() != null) {
                            mResumeList.addAll(resumeSearchBean.data.getData().get(0).getTrackingListData());
                            iCompanyTalentView.callUpdateLoadAllDataView(resumeSearchBean.data.getData().get(0).getTrackingListData() == null || resumeSearchBean.data.getData().get(0).getTrackingListData().size() < CommonConst.DEFAULT_PAGE_SIZE);
                        }
                    }
                    iCompanyTalentView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    iCompanyTalentView.callSetNoResultHintView(mResumeList.isEmpty());
                }
            }
        });
    }

    private void subscriberResumeEvents(boolean isRegister) {
        if (isRegister) {
            UIEventsObservable.getInstance().subscribeEvent(IResumeEventsSubscriber.class, this, new IResumeEventsSubscriber() {
                @Override
                public boolean onResumeModifiedObserver(ResumeBean resumeBean, Object extra) {
                    if (resumeBean == null)
                        return false;
                    //通知简历被修改了
                    int targetPosition = TrackingResumeDao.findTargetResumePosition(resumeBean, mResumeList);
                    if (targetPosition == CommonConst.LIST_POSITION_NONE)
                        return false;
                    mResumeList.get(targetPosition).refreshModifyInfo(resumeBean);
                    //只更新改变的Item
                    iCompanyTalentView.callRefreshListItems(targetPosition);
                    return false;
                }
            });
        } else {
            UIEventsObservable.getInstance().stopSubscribeEvent(IResumeEventsSubscriber.class, this);
        }
    }
}
