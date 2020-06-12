package com.baza.android.bzw.businesscontroller.message.presenter;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.push.PushBean;
import com.baza.android.bzw.bean.push.PushListResultBean;
import com.baza.android.bzw.businesscontroller.message.viewinterface.ISystemView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.PushDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/6/21.
 * Title：
 * Note：
 */

public class SystemMessagePresenter extends BasePresenter {
    private ISystemView mSystemView;
    private List<PushBean> mMessageList = new ArrayList<>();
    private int mType;
    private int mOffset;

    public SystemMessagePresenter(ISystemView mSystemView, int type) {
        this.mSystemView = mSystemView;
        this.mType = type;
    }

    @Override
    public void initialize() {
        loadInitData(true);
        PushDao.markAllNoticeRead(mType == ISystemView.MSG_BAZA_HELPER ? PushDao.BAZA_HELPER : PushDao.SYSTEM_NOTICE, null);
    }


    public List<PushBean> getDataList() {
        return mMessageList;
    }


    public void loadInitData(boolean refresh) {
        if (mType == ISystemView.MSG_BAZA_HELPER)
            loadBazaHelperMsg(refresh);
        else
            loadSystemMsg(refresh);
    }

    private void loadBazaHelperMsg(boolean refresh) {
        if (refresh)
            mOffset = 0;
        PushDao.loadBazaHelperMsg(mOffset, new IDefaultRequestReplyListener<PushListResultBean>() {
            @Override
            public void onRequestReply(boolean success, PushListResultBean pushListResultBean, int errorCode, String errorMsg) {
                mSystemView.callCancelLoadingView(success, errorCode, errorMsg);
                if (mOffset == 0)
                    mMessageList.clear();
                boolean enable = false;
                if (success) {
                    if (pushListResultBean.data != null && !pushListResultBean.data.isEmpty()) {
                        mMessageList.addAll(pushListResultBean.data);
                        enable = (pushListResultBean.data.size() == CommonConst.DEFAULT_PAGE_SIZE);
                        mOffset += pushListResultBean.data.size();
                    }

                }
                mSystemView.callUpdateLoadMoreView(enable);
                mSystemView.callRefreshMessageViews(CommonConst.LIST_POSITION_NONE);
            }
        });
    }

    private void loadSystemMsg(boolean refresh) {
        if (refresh)
            mOffset = 0;
        PushDao.loadSystemMsg(mOffset, new IDefaultRequestReplyListener<PushListResultBean>() {
            @Override
            public void onRequestReply(boolean success, PushListResultBean pushListResultBean, int errorCode, String errorMsg) {
                mSystemView.callCancelLoadingView(success, errorCode, errorMsg);
                if (mOffset == 0)
                    mMessageList.clear();
                boolean enable = false;
                if (success) {
                    if (pushListResultBean.data != null && !pushListResultBean.data.isEmpty()) {
                        enable = (pushListResultBean.data.size() == CommonConst.DEFAULT_PAGE_SIZE);
                        mMessageList.addAll(pushListResultBean.data);
                        mOffset += pushListResultBean.data.size();
                    }

                }
                mSystemView.callUpdateLoadMoreView(enable);
                mSystemView.callRefreshMessageViews(CommonConst.LIST_POSITION_NONE);
            }
        });
    }
}
