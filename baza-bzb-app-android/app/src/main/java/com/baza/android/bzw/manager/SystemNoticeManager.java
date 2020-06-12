package com.baza.android.bzw.manager;

import android.content.Context;

import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.bean.user.VerifyStatusBean;
import com.baza.android.bzw.businesscontroller.im.ISystemNoticeObserve;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.events.IDefaultEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;

/**
 * Created by Vincent.Lei on 2018/8/29.
 * Title：
 * Note：
 */
public class SystemNoticeManager implements ISystemNoticeObserve {
    private SystemNoticeManager() {
    }

    private static final SystemNoticeManager mInstance = new SystemNoticeManager();

    public static SystemNoticeManager getInstance() {
        return mInstance;
    }

    public void observeSystemNotice(Context context) {
        IMManager.getInstance(context).registerSystemNoticeObserve(this, true);
    }

    public void stopObserveSystemNotice(Context context) {
        IMManager.getInstance(context).registerSystemNoticeObserve(this, false);
    }

    @Override
    public void onSystemNoticeArrival(int type, Object value) {
        switch (type) {
            case ISystemNoticeObserve.TYPE_USER_VERIFY:
                VerifyStatusBean verifyStatusBean = (VerifyStatusBean) value;
                if (UserInfoManager.getInstance().isUserSignIn()) {
                    UserInfoBean userInfoBean = UserInfoManager.getInstance().getUserInfo();
                    userInfoBean.channelVerifyStatus = verifyStatusBean.channelVerifyStatus;
                    UserInfoManager.getInstance().saveUserInfo(userInfoBean);
                    UIEventsObservable.getInstance().postEvent(IDefaultEventsSubscriber.class, ActionConst.ACTION_EVENT_ACCOUNT_INFO_CHANGED, null, null);
                    UIEventsObservable.getInstance().postEvent(IDefaultEventsSubscriber.class, ActionConst.ACTION_EVENT_USER_VERIFY_CHANGED, verifyStatusBean, null);
                }
                break;
        }
    }
}
