package com.baza.android.bzw.businesscontroller.publish.presenter;

import android.content.Intent;
import android.text.TextUtils;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.resumeelement.ResumeImportResultBean;
import com.baza.android.bzw.businesscontroller.publish.viewinterface.IResumeImportView;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeDetailActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;

import java.io.File;

/**
 * Created by Vincent.Lei on 2017/6/26.
 * Title：
 * Note：
 */

public class ResumeImportPresenter extends BasePresenter {
    private IResumeImportView mResumeImportView;
    private String mSourceAttachment;

    public ResumeImportPresenter(IResumeImportView mResumeImportView, Intent intent) {
        this.mResumeImportView = mResumeImportView;
        mSourceAttachment = intent.getStringExtra("path");
    }

    @Override
    public void initialize() {
        if (TextUtils.isEmpty(mSourceAttachment)) {
            mResumeImportView.callShowToastMessage(null, R.string.attachment_for_resume_is_not_exist);
            return;
        }
        File file = new File(mSourceAttachment);
        if (!file.exists() || file.length() < 0) {
            mResumeImportView.callShowToastMessage(null, R.string.attachment_for_resume_is_not_exist);
            return;
        }
        String userName = UserInfoManager.getInstance().getUserInfo().nickName;
        if (TextUtils.isEmpty(userName))
            userName = UserInfoManager.getInstance().getUserInfo().trueName;
        if (userName == null)
            return;
        mResumeImportView.callUpdateImportHintView(mSourceAttachment, userName);
    }

    public void onNewIntent(Intent intent) {
        mSourceAttachment = intent.getStringExtra("path");
        initialize();
    }

    public void uploadResumeAttachment() {
        File file = new File(mSourceAttachment);
        if (!file.exists() || file.length() < 0) {
            mResumeImportView.callShowToastMessage(null, R.string.attachment_for_resume_is_not_exist);
            return;
        }
        if (file.length() > 20 * 1024 * 1024) {
            mResumeImportView.callShowToastMessage(null, R.string.attachment_for_resume_is_too_big);
            return;
        }
        mResumeImportView.callSetOnImportView(true);
        ResumeDao.doImportResume(file, new IDefaultRequestReplyListener<ResumeImportResultBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeImportResultBean resumeImportResultBean, int errorCode, String errorMsg) {
                if (success) {
                    mResumeImportView.callShowToastMessage(null, R.string.resume_import_success);
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_RESUME_IMPORT, null, null);
                    if (resumeImportResultBean.data != null) {
                        ResumeDetailActivity.launch(mResumeImportView.callGetBindActivity(), new IResumeDetailView.IntentParam(resumeImportResultBean.data));
                    }
                    mResumeImportView.callFinish();
                    return;
                }
                mResumeImportView.callSetOnImportView(false);
                mResumeImportView.callShowToastMessage(errorMsg, 0);
            }
        });
    }
}
