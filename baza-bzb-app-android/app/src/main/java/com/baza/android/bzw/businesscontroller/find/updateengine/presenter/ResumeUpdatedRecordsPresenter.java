package com.baza.android.bzw.businesscontroller.find.updateengine.presenter;

import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeUpdateListResultBean;
import com.baza.android.bzw.dao.ResumeUpdateDao;
import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IResumeUpdatedRecordsView;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/9/11.
 * Title：
 * Note：
 */

public class ResumeUpdatedRecordsPresenter extends BasePresenter {
    private IResumeUpdatedRecordsView mAlreadyUpdatedView;
    private List<ResumeBean> mList = new ArrayList<>();
    private int mPageNo;

    public ResumeUpdatedRecordsPresenter(IResumeUpdatedRecordsView alreadyUpdatedView) {
        this.mAlreadyUpdatedView = alreadyUpdatedView;
    }

    @Override
    public void initialize() {
        getAlreadyUpdatedList(true);
    }

    public List<ResumeBean> getData() {
        return mList;
    }

    public void getAlreadyUpdatedList(boolean refresh) {
        if (refresh)
            mPageNo = 1;
        ResumeUpdateDao.loadAlreadyUpdatedList(mPageNo, new IDefaultRequestReplyListener<ResumeUpdateListResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, ResumeUpdateListResultBean.Data data, int errorCode, String errorMsg) {
                mAlreadyUpdatedView.callCancelLoadingView(success, errorCode, errorMsg);
                if (mPageNo == 1)
                    mList.clear();
                if (success && data != null) {
                    if (data.list != null && !data.list.isEmpty())
                        mList.addAll(data.list);
                    mAlreadyUpdatedView.callUpdateLoadAllDataView((mList.size() >= data.total));
                    mAlreadyUpdatedView.callUpdateAllCountView(data.total);
                    mPageNo++;
                }
                mAlreadyUpdatedView.callRefreshListItems();
                if (!success)
                    mAlreadyUpdatedView.callShowToastMessage(errorMsg, 0);
            }
        });
    }
}
