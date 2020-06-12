package com.baza.android.bzw.businesscontroller.email.presenter;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.email.EmailAttachmentBean;
import com.baza.android.bzw.bean.resume.ResumeUpdateLogResultBean;
import com.baza.android.bzw.businesscontroller.email.SendEmailSuccessActivity;
import com.baza.android.bzw.businesscontroller.email.viewinterface.ISendEmailView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.dao.EmailDao;
import com.baza.android.bzw.dao.ResumeUpdateDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/8/1.
 * Title：
 * Note：
 */

public class SendEmailPresenter extends BasePresenter {
    private ISendEmailView mSendEmailView;
    private String mAssigner;
    private List<EmailAttachmentBean> mEmailAttachmentList = new ArrayList<>(5);
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private String[] mEditMenuJustDelete;
    private String[] mEditMenuOther;
    private String mLastPath;
    private String mResumeId;

    public SendEmailPresenter(ISendEmailView sendEmailView, Intent intent) {
        this.mSendEmailView = sendEmailView;
        this.mAssigner = intent.getStringExtra("assigner");
        this.mResumeId = intent.getStringExtra("candidateId");
    }

    @Override
    public void initialize() {
        mSendEmailView.callSetPreviousAssigner((AppUtil.checkEmail(mAssigner) ? mAssigner : null));
    }

    public List<EmailAttachmentBean> getEmailAttachmentList() {
        return mEmailAttachmentList;
    }

    public boolean isEnableToAddAttachment() {
        return mEmailAttachmentList.size() < CommonConst.MAX_EMAIL_ATTACHMENT_COUNT;
    }

    public void addEmailAttachment(String filePath) {
        if (filePath == null) {
            mLastPath = null;
            return;
        }
        final EmailAttachmentBean emailAttachmentBean = EmailAttachmentBean.createLocalAttachment(filePath);
        if (emailAttachmentBean.isEmailAttachmentExist()) {
            mLastPath = emailAttachmentBean.getFile().getParent();
            if (emailAttachmentBean.fileSize > CommonConst.MAX_EMAIL_ATTACHMENT_SIZE) {
                mSendEmailView.callShowToastMessage(null, R.string.email_attachment_is_too_big);
                return;
            }
            if (isAttachmentHasAdd(emailAttachmentBean)) {
                mSendEmailView.callShowToastMessage(null, R.string.email_attachment_has_add);
                return;
            }
            mEmailAttachmentList.add(emailAttachmentBean);
            emailAttachmentBean.status = EmailAttachmentBean.STATUS_UPLOADING;
            mSendEmailView.callUpdateAttachmentView(-1);
            mSendEmailView.callUpdateAttachmentCountView(mEmailAttachmentList.size());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    uploadAttachment(emailAttachmentBean);
                }
            });
        } else
            mSendEmailView.callShowToastMessage(null, R.string.attachment_is_not_exist_is_delete);
    }

    public String getLastPath() {
        return mLastPath;
    }

    public void reUploadAttachment(int position, final EmailAttachmentBean emailAttachmentBean) {
        emailAttachmentBean.status = EmailAttachmentBean.STATUS_UPLOADING;
        mSendEmailView.callUpdateAttachmentView(position);
        uploadAttachment(emailAttachmentBean);
    }

    private void uploadAttachment(final EmailAttachmentBean emailAttachmentBean) {
        if (!emailAttachmentBean.isEmailAttachmentExist()) {
            mEmailAttachmentList.remove(emailAttachmentBean);
            mSendEmailView.callUpdateAttachmentView(-1);
            mSendEmailView.callShowToastMessage(null, R.string.attachment_is_not_exist_is_delete);
            return;
        }
        EmailDao.uploadEmailAttachment(emailAttachmentBean.getFile(), new IDefaultRequestReplyListener<EmailAttachmentBean>() {
            @Override
            public void onRequestReply(boolean success, EmailAttachmentBean upload, int errorCode, String errorMsg) {
                int targetPosition = -1;
                for (int i = 0, size = mEmailAttachmentList.size(); i < size; i++) {
                    if (mEmailAttachmentList.get(i) == emailAttachmentBean) {
                        targetPosition = i;
                        break;
                    }
                }
                if (targetPosition == -1)
                    return;
                if (success)
                    emailAttachmentBean.updateNewInfo(upload);
                emailAttachmentBean.status = (success ? EmailAttachmentBean.STATUS_SUCCESS_UPLOAD : EmailAttachmentBean.STATUS_FAILED_UPLOAD);
                mSendEmailView.callUpdateAttachmentView(targetPosition);
            }
        });
    }

    public void deleteTargetAttachment(EmailAttachmentBean e) {
        mEmailAttachmentList.remove(e);
        mSendEmailView.callUpdateAttachmentView(-1);
        mSendEmailView.callUpdateAttachmentCountView(mEmailAttachmentList.size());
    }

    public void doSubmit() {
        mAssigner = mSendEmailView.callGetEmailAssigner();
        if (!AppUtil.checkEmail(mAssigner)) {
            mSendEmailView.callShowToastMessage(null, R.string.input_email_hint);
            return;
        }
        String emailTitle = mSendEmailView.callGetEmailTitle();
        if (TextUtils.isEmpty(emailTitle)) {
            mSendEmailView.callShowToastMessage(null, R.string.input_email_title);
            return;
        }
        String emailContent = mSendEmailView.callGetEmailContent();
        if (TextUtils.isEmpty(emailContent)) {
            mSendEmailView.callShowToastMessage(null, R.string.input_content);
            return;
        }
        if (hasAttachmentOnUploading()) {
            mSendEmailView.callShowToastMessage(null, R.string.email_attachment_on_uploading);
            return;
        }
        mSendEmailView.callShowProgress(null, true);
        EmailDao.sendEmailToTargetResume(mAssigner, emailTitle, emailContent, mEmailAttachmentList, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mSendEmailView.callCancelProgress();
                if (success) {
                    SendEmailSuccessActivity.launch(mSendEmailView.callGetBindActivity(), RequestCodeConst.INT_REQUEST_SEND_EMAIL_SUCCESS);
                    ResumeUpdateDao.sendUpdateLog(ResumeUpdateLogResultBean.LogData.TYPE_SEND_EMAIL, mResumeId, null);
                    return;
                }
                mSendEmailView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public String[] getEditMenu(EmailAttachmentBean attachment) {
        if (attachment.status == EmailAttachmentBean.STATUS_FAILED_UPLOAD) {
            if (mEditMenuOther == null) {
                mEditMenuOther = new String[2];
                mEditMenuOther[0] = mSendEmailView.callGetResources().getString(R.string.delete);
                mEditMenuOther[1] = mSendEmailView.callGetResources().getString(R.string.re_uploading);
            }
            return mEditMenuOther;
        } else {
            if (mEditMenuJustDelete == null) {
                mEditMenuJustDelete = new String[1];
                mEditMenuJustDelete[0] = mSendEmailView.callGetResources().getString(R.string.delete);
            }
            return mEditMenuJustDelete;
        }
    }

    private boolean hasAttachmentOnUploading() {
        if (!mEmailAttachmentList.isEmpty()) {
            for (int i = 0, size = mEmailAttachmentList.size(); i < size; i++) {
                if (mEmailAttachmentList.get(i).status == EmailAttachmentBean.STATUS_UPLOADING) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean shouldHintSendEmail() {
        mAssigner = mSendEmailView.callGetEmailAssigner();
        String emailTitle = mSendEmailView.callGetEmailTitle();
        String emailContent = mSendEmailView.callGetEmailContent();
        return (!TextUtils.isEmpty(mAssigner) || !TextUtils.isEmpty(emailTitle) || !TextUtils.isEmpty(emailContent) || hasAttachmentOnUploading());
    }

    private boolean isAttachmentHasAdd(EmailAttachmentBean emailAttachmentBean) {
        if (mEmailAttachmentList.isEmpty())
            return false;
        for (int i = 0, size = mEmailAttachmentList.size(); i < size; i++) {
            if (emailAttachmentBean.filePath != null && emailAttachmentBean.filePath.equals(mEmailAttachmentList.get(i).filePath))
                return true;
        }
        return false;
    }

    public void clearForAnother() {
        mEmailAttachmentList.clear();
        mSendEmailView.callResetAllViews();
    }
}
