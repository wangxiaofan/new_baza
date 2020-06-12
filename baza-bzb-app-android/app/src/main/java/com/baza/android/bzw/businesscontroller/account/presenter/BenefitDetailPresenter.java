package com.baza.android.bzw.businesscontroller.account.presenter;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.exchange.BenefitDetailResultBean;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IBenefitDetailView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.AccountDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2018/9/4.
 * Title：
 * Note：
 */
public class BenefitDetailPresenter extends BasePresenter {
    private IBenefitDetailView mBenefitDetailView;
    private int mType;
    private int mPageIndex;
    private List<BenefitDetailResultBean.BenefitRecord> mDataList = new ArrayList<>();

    public BenefitDetailPresenter(IBenefitDetailView benefitDetailView, int type) {
        this.mBenefitDetailView = benefitDetailView;
        this.mType = type;
    }

    @Override
    public void initialize() {
        loadBenefitDetail(true);
    }

    public List<BenefitDetailResultBean.BenefitRecord> getDataList() {
        return mDataList;
    }

    public void loadBenefitDetail(boolean refresh) {
        mPageIndex = (refresh ? 1 : mPageIndex + 1);
        AccountDao.loadBenefitRecords(mType, mPageIndex, CommonConst.DEFAULT_PAGE_SIZE, new IDefaultRequestReplyListener<BenefitDetailResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, BenefitDetailResultBean.Data data, int errorCode, String errorMsg) {
                mBenefitDetailView.callCancelLoadingView(success, errorCode, errorMsg);
                if (mPageIndex == 1)
                    mDataList.clear();
                if (success && data != null) {
                    if (data.data != null)
                        mDataList.addAll(data.data);
                    mBenefitDetailView.callUpdateLoadAllDataView(mDataList.size() >= data.count);
                }
                mBenefitDetailView.callRefreshListItems();
            }
        });
    }
}
