package com.baza.android.bzw.businesscontroller.resume.smartgroup.presenter;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.smartgroup.GroupIndexResultBean;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.viewinterface.ISmartGroupIndexView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.SmartGroupDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2018/11/27.
 * Title：
 * Note：
 */
public class SmartGroupIndexPresenter extends BasePresenter {
    private ISmartGroupIndexView mSmartGroupIndexView;
    private List<GroupIndexResultBean.GroupIndexBean> mDataList = new ArrayList<>();
    private boolean mIsOnLoading;

    public SmartGroupIndexPresenter(ISmartGroupIndexView smartGroupIndexView) {
        this.mSmartGroupIndexView = smartGroupIndexView;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void onResume() {
        loadGroupCollectInfo();
    }

    public List<GroupIndexResultBean.GroupIndexBean> getDataList() {
        return mDataList;
    }

    public void loadGroupCollectInfo() {
        if (mIsOnLoading)
            return;
        mIsOnLoading = true;
        SmartGroupDao.loadGroupCollectInfo(new IDefaultRequestReplyListener<GroupIndexResultBean>() {
            @Override
            public void onRequestReply(boolean success, GroupIndexResultBean groupIndexResultBean, int errorCode, String errorMsg) {
                mIsOnLoading = false;
                mSmartGroupIndexView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success && groupIndexResultBean != null && groupIndexResultBean.recordList != null) {
                    int sumResumeCount = 0;
                    for (int i = 0, size = groupIndexResultBean.recordList.size(); i < size; i++) {
                        sumResumeCount += groupIndexResultBean.recordList.get(i).resumeCount;
                    }
                    mDataList.clear();
                    if (sumResumeCount > 0)
                        mDataList.addAll(groupIndexResultBean.recordList);
                    mSmartGroupIndexView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                }
            }
        });
    }
}
