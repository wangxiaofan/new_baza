package com.baza.android.bzw.businesscontroller.im;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/6/2.
 * Title：
 * Note：
 */

public interface IIMMessageObserve {
    void onNewIMMessageArrival(List<BZWIMMessage> list);
}
