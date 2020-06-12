package com.baza.android.bzw.businesscontroller.company.presenter;

import android.os.Handler;
import android.os.Looper;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeSearchBean;
import com.baza.android.bzw.bean.searchfilterbean.CompanySearchFilterInfoBean;
import com.baza.android.bzw.businesscontroller.company.viewinterface.ICompanyTalentView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.CollectionDao;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

public class CompanyTalentPresenter extends BasePresenter {

    private ICompanyTalentView iCompanyTalentView;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private List<ResumeBean> mResumeList = new ArrayList<>();
    private ResumeDao.CompanySearchParam mSearchParam;
    public static final int SELF_TOAST_COLLECTION = 2;
    private CompanySearchFilterInfoBean mSearchFilterInfo;

    public CompanyTalentPresenter(ICompanyTalentView iCompanyTalentView) {
        this.iCompanyTalentView = iCompanyTalentView;
        mSearchFilterInfo = new CompanySearchFilterInfoBean();
        mSearchFilterInfo.mandatorySort = CommonConst.COMPANY_RESUME_SEARCH_SORT_ORDER_UPDATE_TIME;
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

    public List<ResumeBean> getDataList() {
        return mResumeList;
    }

    public void loadMyResumeList(final boolean refresh) {
        if (mSearchParam == null)
            mSearchParam = new ResumeDao.CompanySearchParam();
        mSearchParam.offset(refresh ? 0 : mResumeList.size())
                .pageSize(CommonConst.DEFAULT_PAGE_SIZE)
                .searchType(CommonConst.INT_SEARCH_TYPE_COMPANY)
                .searchFilter(mSearchFilterInfo)
                .build();
        ResumeDao.doCompanySearch(mSearchParam, new IDefaultRequestReplyListener<ResumeSearchBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeSearchBean resumeSearchBean, int errorCode, String errorMsg) {
                iCompanyTalentView.callCancelLoadingView(success, errorCode, errorMsg);
                iCompanyTalentView.callCancelProgress();
                if (success) {
                    if (refresh)
                        mResumeList.clear();
                    if (resumeSearchBean != null) {
//                        mTotalCount = resumeSearchBean.totalCount;
//                        mJobHunterCount = resumeSearchBean.jobHuntingCount;
                        if (resumeSearchBean.recordList != null)
                            mResumeList.addAll(resumeSearchBean.recordList);
                        iCompanyTalentView.callUpdateLoadAllDataView(resumeSearchBean.recordList == null || resumeSearchBean.recordList.size() < CommonConst.DEFAULT_PAGE_SIZE);
//                        iCompanyTalentView.callUpdateSearchCountView(mTotalCount, mJobHunterCount);
                    }
                    iCompanyTalentView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    iCompanyTalentView.callSetNoResultHintView(mResumeList.isEmpty());
                }
            }
        });
    }

    public void collection(int position, ResumeBean mResumeDetailBean) {
        iCompanyTalentView.callShowProgress(null, true);
        CollectionDao.doOrCancelCollection(mResumeDetailBean, new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(boolean success, String s, int errorCode, String errorMsg) {
                iCompanyTalentView.callCancelProgress();
                if (success) {
                    mResumeDetailBean.collectStatus = (mResumeDetailBean.collectStatus == CommonConst.COLLECTION_NO ? CommonConst.COLLECTION_YES : CommonConst.COLLECTION_NO);
                    iCompanyTalentView.callShowSpecialToastMsg(SELF_TOAST_COLLECTION, null, (mResumeDetailBean.collectStatus == CommonConst.COLLECTION_NO ? R.string.un_collection_success : R.string.collection_success));
                    iCompanyTalentView.callRefreshListItems(position);
                    return;
                }
                iCompanyTalentView.callShowToastMessage(errorMsg, 0);
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
                    int targetPosition = ResumeDao.findTargetResumePosition(resumeBean, mResumeList);
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
