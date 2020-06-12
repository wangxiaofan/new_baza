package com.baza.android.bzw.businesscontroller.account.presenter;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IAccountExperienceView;

/**
 * Created by Vincent.Lei on 2017/5/31.
 * Title：
 * Note：
 */

public class AccountExperiencePresenter extends BasePresenter {
    private IAccountExperienceView mAccountExperienceView;

    public AccountExperiencePresenter(IAccountExperienceView mAccountExperienceView) {
        this.mAccountExperienceView = mAccountExperienceView;
    }

    @Override
    public void initialize() {
        mAccountExperienceView.callSetMessage();
    }
}
