package com.baza.android.bzw.businesscontroller.account.presenter;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.exchange.BenefitResultBean;
import com.baza.android.bzw.bean.exchange.GoodListResultBean;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IRightCenterView;
import com.baza.android.bzw.dao.AccountDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.logger.RightLogger;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2018/9/4.
 * Title：
 * Note：
 */
public class RightCenterPresenter extends BasePresenter {
    private IRightCenterView mRightCenterView;
    private BenefitResultBean.Data mBenefit;
    private List<GoodListResultBean.Good> mGoodList = new ArrayList<>();
    private RightLogger mRightLogger = new RightLogger();

    public RightCenterPresenter(IRightCenterView rightCenterView) {
        this.mRightCenterView = rightCenterView;
    }

    @Override
    public void initialize() {
        loadUserBenefit();
//        loadInviteCodeActivityStatus();
    }

    public BenefitResultBean.Data getBenefitBaseInfo() {
        return mBenefit;
    }

    public List<GoodListResultBean.Good> getGoodList() {
        return mGoodList;
    }

    public void loadUserBenefit() {
        AccountDao.loadUserBenefit(new IDefaultRequestReplyListener<BenefitResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, BenefitResultBean.Data data, int errorCode, String errorMsg) {
                if (!success || data == null) {
                    mRightCenterView.callCancelLoadingView(false, errorCode, errorMsg);
                    return;
                }
                mBenefit = data;
                mRightCenterView.callUpdateBenefitView();
                loadGoodList();
            }
        });
    }

//    private void loadInviteCodeActivityStatus() {
//        OperationalDao.loadStatusOfTargetActivity(OperationalDao.ACTIVITY_ID_BAZA_GLASS, new IDefaultRequestReplyListener<ActivityStatusResultBean.Data>() {
//            @Override
//            public void onRequestReply(boolean success, ActivityStatusResultBean.Data data, int errorCode, String errorMsg) {
//                if (success && data != null && data.status == ActivityStatusResultBean.Data.STATUS_OK)
//                    mRightCenterView.callUpdateInvitedCodeEnableView();
//            }
//        });
//    }

    private void loadGoodList() {
        AccountDao.loadGoodList(new IDefaultRequestReplyListener<GoodListResultBean>() {
            @Override
            public void onRequestReply(boolean success, GoodListResultBean goodListResultBean, int errorCode, String errorMsg) {
                mRightCenterView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success && goodListResultBean != null && goodListResultBean.data != null) {
                    mGoodList.addAll(goodListResultBean.data);
                    mRightCenterView.callUpdateGoodsView();
                }
            }
        });
    }

    public void exchange(final GoodListResultBean.Good good, final int exchangeTime, final int glassCount) {
        mRightCenterView.callShowProgress(null, true);
        AccountDao.exchangeGoodList(good, exchangeTime, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mRightCenterView.callCancelProgress();
                if (success) {
                    mBenefit.quantity -= glassCount;
                    mRightCenterView.callUpdateBenefitView();
                    mRightCenterView.callShowToastMessage(null, R.string.exchange_success);
                    mRightLogger.sendExchangeLog(mRightCenterView, good, exchangeTime);
                    return;
                }
                mRightCenterView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void inviteCodeExchange(String code) {
        mRightCenterView.callShowProgress(null, true);
        AccountDao.inviteCodeExchange(code, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mRightCenterView.callCancelProgress();
                if (success) {
                    mRightCenterView.callShowToastMessage(errorMsg, R.string.exchange_success);
                    return;
                }
                mRightCenterView.callShowToastMessage(errorMsg, 0);
            }
        });
    }
}
