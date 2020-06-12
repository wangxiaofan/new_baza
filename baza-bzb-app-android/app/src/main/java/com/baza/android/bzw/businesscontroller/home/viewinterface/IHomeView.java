package com.baza.android.bzw.businesscontroller.home.viewinterface;

import com.baza.android.bzw.base.IBaseView;
import com.baza.android.bzw.bean.user.VersionBean;
import com.baza.android.bzw.dao.ActivityPrepareDao;

/**
 * Created by Vincent.Lei on 2017/5/17.
 * Title：
 * Note：
 */

public interface IHomeView extends IBaseView {
    void callShowNewVersionDialog(VersionBean data);

    void callMessageUnReadCountUpdate(int unReadCount);

    boolean callIsCurrentIsMessageView();

    void callShowConfigActivityDialog(ActivityPrepareDao.DialogConfig dialogConfig);

//    void callShowActivityDialog(ActivityStatusResultBean.Data data);

    void callUpdateRecommendFloattingView(String msg);

    void callDismissRecommendFloattingView();

    boolean callIsRecommendFloattingViewShown();
}
