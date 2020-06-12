package com.baza.android.bzw.businesscontroller.im;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/6/1.
 * Title：
 * Note：
 */

public interface IRecentContactMessageObserve {
    void onRecentContactMessageEvent(List<IMRecentContact> recentContactList);
}
