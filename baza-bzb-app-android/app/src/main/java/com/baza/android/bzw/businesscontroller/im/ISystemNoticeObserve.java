package com.baza.android.bzw.businesscontroller.im;

/**
 * Created by Vincent.Lei on 2017/9/22.
 * Title：
 * Note：
 */

public interface ISystemNoticeObserve {
//    int TYPE_MEMORANDUM = 1;
    int TYPE_USER_VERIFY = 2;

    void onSystemNoticeArrival(int type, Object value);

}
