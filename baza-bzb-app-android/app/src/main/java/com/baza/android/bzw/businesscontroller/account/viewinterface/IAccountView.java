package com.baza.android.bzw.businesscontroller.account.viewinterface;

import com.baza.android.bzw.base.IBaseView;
import com.baza.android.bzw.bean.exchange.BenefitResultBean;
import com.baza.android.bzw.bean.user.ExtraCountBean;
import com.baza.android.bzw.bean.user.RankBean;

/**
 * Created by Vincent.Lei on 2017/5/26.
 * Title：
 * Note：
 */

public interface IAccountView extends IBaseView {
    void callUpdateMainInfo();

    void callUpdateGrowInfo();

    void callUpdateBenefitView(BenefitResultBean.Data data);

    void callUpdateInterestedTalentView(String typeName);

    void callUpdateCountViews(ExtraCountBean extraCountBean);

//    void callUpdateRankView(RankBean rankBean);
}
