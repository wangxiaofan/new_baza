package com.baza.android.bzw.businesscontroller.account.presenter;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.resume.ResumeClassifyResultBean;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IResumeClassifyView;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2018/8/24.
 * Title：
 * Note：
 */
public class ResumeClassifyPresenter extends BasePresenter {
    private IResumeClassifyView mResumeClassifyView;
    private int mType;
    private int mOffset;
    private List<ResumeClassifyResultBean.ResumeClassifyBean> mDataList = new ArrayList<>();
    private String mSourcePath = "";


    public ResumeClassifyPresenter(IResumeClassifyView resumeClassifyView, int type) {
        this.mResumeClassifyView = resumeClassifyView;
        this.mType = type;
    }

    @Override
    public void initialize() {
        loadClassifyList(true, false);
    }

    public List<ResumeClassifyResultBean.ResumeClassifyBean> getDataList() {
        return mDataList;
    }

    public void setSourcePath(String sourcePath) {
        if (mSourcePath != null && mSourcePath.equals(sourcePath))
            return;
        this.mSourcePath = sourcePath;
        loadClassifyList(true, true);
    }

    public void loadClassifyList(boolean refresh, boolean showLoadingView) {
        mOffset = (refresh ? 0 : mDataList.size());
        if (showLoadingView)
            mResumeClassifyView.callShowLoadingView(null);
        ResumeDao.loadResumeClassifyList(mOffset, 15, mType, mSourcePath, new IDefaultRequestReplyListener<ResumeClassifyResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, ResumeClassifyResultBean.Data data, int errorCode, String errorMsg) {
                mResumeClassifyView.callCancelLoadingView(success, errorCode, errorMsg);
                if (mOffset == 0)
                    mDataList.clear();
                if (success && data != null) {
                    if (data.recordList != null)
                        mDataList.addAll(data.recordList);
                    mResumeClassifyView.callUpdateLoadAllDataView(mDataList.size() >= data.totalCount);
                    mResumeClassifyView.callUpdateAllCountView(data.attach);
                }
                mResumeClassifyView.callRefreshListItems();
            }
        });
    }
}
