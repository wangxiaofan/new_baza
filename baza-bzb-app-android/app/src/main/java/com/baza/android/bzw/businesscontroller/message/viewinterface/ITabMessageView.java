package com.baza.android.bzw.businesscontroller.message.viewinterface;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/6/1.
 * Title：
 * Note：
 */

public interface ITabMessageView extends IBaseView {
    void callCancelInnerProgress();

    void callShowInnerProgress(int msg);

    void callRefreshTargetRecentView(int targetPosition);

    void callOnLineStatusChanged(int statusCode);

    void callUpdateFriendRequestView(int requestCount);

    void callUpdateNoticeUnreadCountView(int systemMsgCount, int processCount);

    void callUpdateRecommendCountView(int unCompleteCount);
}
