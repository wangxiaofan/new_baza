package com.baza.android.bzw.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.bean.push.PushBean;
import com.baza.android.bzw.businesscontroller.account.AddEmailSyncAccountActivity;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.businesscontroller.find.updateengine.ResumeEnableUpdateListActivity;
import com.baza.android.bzw.businesscontroller.message.SystemMessageActivity;
import com.baza.android.bzw.businesscontroller.message.viewinterface.ISystemView;
import com.baza.android.bzw.businesscontroller.publish.LauncherActivity;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.constant.SharedPreferenceConst;
import com.baza.android.bzw.dao.AccountDao;
import com.baza.android.bzw.log.LogUtil;
import com.baza.jpush.IPushListener;
import com.baza.jpush.Pusher;
import com.slib.storage.sharedpreference.SharedPreferenceManager;

import cn.jpush.android.api.NotificationMessage;

/**
 * Created by Vincent.Lei on 2017/6/9.
 * Title：
 * Note：
 */

public class PushManager implements IPushListener {
    private static PushManager mPushHelper = new PushManager();

    private PushManager() {
    }

    public static PushManager getInstance() {
        return mPushHelper;
    }

    public void startPush(Context context) {
        Pusher.startPush(context, this);
    }

    public void stopPush(Context context) {
        Pusher.stopPush(context);
    }

    @Override
    public final void onRegister(Context context, String jPushId) {
        if (TextUtils.isEmpty(jPushId))
            return;
        LogUtil.d("JPushId", jPushId);
        SharedPreferenceManager.saveString(SharedPreferenceConst.SP_JPUSH_ID, jPushId);
        bindUserToPush(0, jPushId);
    }

    @Override
    public final void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {

    }

    @Override
    public final void onNotifyMessageOpened(Context context, String extras) {
        PushBean pushBean = null;
        try {
            pushBean = JSON.parseObject(extras, PushBean.class);
        } catch (Exception e) {
            //ignore
        }
        if (pushBean == null)
            return;
        BZWApplication bzwApplication = BZWApplication.getApplication();
        if (bzwApplication == null)
            return;
        pushBean.title = extras;
        if (!UserInfoManager.getInstance().isAppOpened()) {
            //打开应用
            bzwApplication.cacheTransformData(CommonConst.STR_TRANSFORM_PUSH_KEY, pushBean);
            Intent intent = new Intent(context, LauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        }
        Activity activity = ActivitiesManager.getInstance().getTopStackActivity();
        if (activity != null)
            openPushMessage(activity, pushBean, true);
    }

    /**
     * 将推送ID和用户绑定
     *
     * @param userId 用户ID
     */
    private void bindUserToPush(long userId, String pushId) {
        if (userId <= 0)
            userId = UserInfoManager.getInstance().getUserInfo().userId;
        if (TextUtils.isEmpty(pushId))
            pushId = SharedPreferenceManager.getString(SharedPreferenceConst.SP_JPUSH_ID);
        if (userId <= 0 || pushId == null)
            return;
        LogUtil.d("bind user and push userID = " + userId + " pushId = " + pushId);
        AccountDao.bindPushId(pushId);
    }

    public void openPushMessage(Activity activity, PushBean pushBean, boolean openPushList) {
        if (pushBean == null) {
            SystemMessageActivity.launch(activity, ISystemView.MSG_SYSTEM);
            return;
        }
        if (pushBean.messageType == PushBean.MESSAGE_TYPE_UPDATE_RESUME_RECOMMEND) {
            ResumeEnableUpdateListActivity.launch(activity);
            return;
        }
        PushBean.PushExtraBean extraMsg = pushBean.getExtraMsg();
        if (extraMsg == null || (TextUtils.isEmpty(extraMsg.url) && TextUtils.isEmpty(extraMsg.link))) {
            SystemMessageActivity.launch(activity, ISystemView.MSG_SYSTEM);
            return;
        }
        if (!TextUtils.isEmpty(extraMsg.url)) {
            if (extraMsg.url.startsWith("http")) {
                RemoteBrowserActivity.launch(activity, null, false, extraMsg.url);
                return;
            } else if (extraMsg.url.equals(PushBean.ACTION_EMIAL_SYNC)) {
                AddEmailSyncAccountActivity.launch(activity, RequestCodeConst.INT_REQUEST_ADD_SYNC_EMAIL_ACCOUNT);
                return;
            }
            return;
        }
        //兼容旧版本
        if (!TextUtils.isEmpty(extraMsg.link)) {
            RemoteBrowserActivity.launch(activity, null, (extraMsg.returnButtonType == PushBean.PushExtraBean.RETURN_BUTTON_YES), extraMsg.link);
            return;
        }
        if (openPushList)
            SystemMessageActivity.launch(activity, ISystemView.MSG_BAZA_HELPER);
    }
}
