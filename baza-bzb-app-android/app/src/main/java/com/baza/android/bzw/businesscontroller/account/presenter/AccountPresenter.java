package com.baza.android.bzw.businesscontroller.account.presenter;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.exchange.BenefitResultBean;
import com.baza.android.bzw.bean.user.ExtraCountBean;
import com.baza.android.bzw.bean.user.GrowResultBean;
import com.baza.android.bzw.businesscontroller.account.AccountExperienceActivity;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IAccountView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.dao.AccountDao;
import com.baza.android.bzw.events.IDefaultEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.manager.UserInfoManager;

/**
 * Created by Vincent.Lei on 2017/5/26.
 * Title：
 * Note：
 */

public class AccountPresenter extends BasePresenter {
    private static final int TAG_ON_GET_GRADE_INFO = 1;
    private static final int TAG_ON_GET_EXTENSION = 1 << 1;
    private static final int TAG_ON_GET_BENEFIT = 1 << 2;
    private IAccountView mAccountView;
    private boolean mHidden;
    private int mRequestTag;

    public AccountPresenter(IAccountView mAccountView) {
        this.mAccountView = mAccountView;
    }

    @Override
    public void initialize() {
        mAccountView.callUpdateMainInfo();
        subscribeEvents(true);
    }

    @Override
    public void onResume() {
        onHiddenChanged(mHidden);
    }

    public void onHiddenChanged(boolean hidden) {
        this.mHidden = hidden;
        if (!mHidden) {
            loadGradeAndExperience(false);
            loadExtensionCountInfo();
            loadUserBenefit();
        }
    }

    @Override
    public void onDestroy() {
        subscribeEvents(false);
    }

    public void loadGradeAndExperience(final boolean callForGrowPage) {
        if (!callForGrowPage) {
            if ((mRequestTag & TAG_ON_GET_GRADE_INFO) != 0)
                return;
            mRequestTag |= TAG_ON_GET_GRADE_INFO;
        }
        if (callForGrowPage)
            mAccountView.callShowProgress(null, true);
        AccountDao.getGradeAndExperience(UserInfoManager.getInstance().getRsaUnionId(), new IDefaultRequestReplyListener<GrowResultBean>() {
            @Override
            public void onRequestReply(boolean success, GrowResultBean growResultBean, int errorCode, String errorMsg) {
                if (callForGrowPage)
                    mAccountView.callCancelProgress();
                mRequestTag &= ~TAG_ON_GET_GRADE_INFO;
                if (success) {
                    UserInfoManager.getInstance().setGrowInfo(growResultBean.data);
                    mAccountView.callUpdateGrowInfo();
                    if (callForGrowPage)
                        AccountExperienceActivity.launch(mAccountView.callGetBindActivity());
                }
            }
        });
    }

    private void loadExtensionCountInfo() {
        if ((mRequestTag & TAG_ON_GET_EXTENSION) != 0)
            return;
        mRequestTag |= TAG_ON_GET_EXTENSION;
        AccountDao.getExtensionCountInfo(new IDefaultRequestReplyListener<ExtraCountBean>() {
            @Override
            public void onRequestReply(boolean success, ExtraCountBean extraCountBean, int errorCode, String errorMsg) {
                mRequestTag &= ~TAG_ON_GET_EXTENSION;
                if (success && extraCountBean != null)
                    mAccountView.callUpdateCountViews(extraCountBean);
            }
        });
    }

    private void subscribeEvents(boolean register) {
        if (register) {
            UIEventsObservable.getInstance().subscribeEvent(IDefaultEventsSubscriber.class, this, new IDefaultEventsSubscriber() {
                @Override
                public boolean killEvent(String action, Object data) {
                    if (ActionConst.ACTION_EVENT_ACCOUNT_INFO_CHANGED.equals(action))
                        mAccountView.callUpdateMainInfo();
                    return false;
                }
            });

        } else {
            UIEventsObservable.getInstance().stopSubscribeEvent(IDefaultEventsSubscriber.class, this);
        }
    }

    private void loadUserBenefit() {
        if ((mRequestTag & TAG_ON_GET_BENEFIT) != 0)
            return;
        mRequestTag |= TAG_ON_GET_BENEFIT;
        AccountDao.loadUserBenefit(new IDefaultRequestReplyListener<BenefitResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, BenefitResultBean.Data data, int errorCode, String errorMsg) {
                mRequestTag &= ~TAG_ON_GET_BENEFIT;
                if (success && data != null) {
                    mAccountView.callUpdateBenefitView(data);
                }
            }
        });
    }
}
