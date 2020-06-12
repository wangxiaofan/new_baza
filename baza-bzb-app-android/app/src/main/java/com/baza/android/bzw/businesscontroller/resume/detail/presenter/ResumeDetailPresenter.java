package com.baza.android.bzw.businesscontroller.resume.detail.presenter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.common.OSSFileUploadResultBean;
import com.baza.android.bzw.bean.email.AddresseeBean;
import com.baza.android.bzw.bean.email.BindEmailResultBean;
import com.baza.android.bzw.bean.resume.AddOrRemoveTrackingListResultBean;
import com.baza.android.bzw.bean.resume.ContactResultBean;
import com.baza.android.bzw.bean.resume.ResumeAttachment;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeDetailBean;
import com.baza.android.bzw.bean.resume.ResumeDetailResultBean;
import com.baza.android.bzw.bean.resume.ResumeOnLineResultBean;
import com.baza.android.bzw.bean.resume.ResumeUpdateLogResultBean;
import com.baza.android.bzw.bean.resume.ResumeUpdatedHistoryResultBean;
import com.baza.android.bzw.bean.resumeelement.RemarkBean;
import com.baza.android.bzw.bean.updateengine.UpdateResumeWrapperBean;
import com.baza.android.bzw.businesscontroller.audio.tools.AudioPlayerTool;
import com.baza.android.bzw.businesscontroller.audio.tools.RecorderTool;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.businesscontroller.email.BindEmailActivity;
import com.baza.android.bzw.businesscontroller.email.SendEmailActivity;
import com.baza.android.bzw.businesscontroller.resume.base.presenter.ResumeBasePresenter;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.AttachmentRemarkDao;
import com.baza.android.bzw.dao.CollectionDao;
import com.baza.android.bzw.dao.EmailDao;
import com.baza.android.bzw.dao.RecommendDao;
import com.baza.android.bzw.dao.ResumeCompareDao;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.dao.ResumeUpdateDao;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.logger.RecommendLogger;
import com.baza.android.bzw.log.logger.ResumeDetailLogger;
import com.baza.android.bzw.log.logger.ResumeLogger;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.slib.utils.AppUtil;
import com.slib.utils.DateUtil;
import com.slib.utils.DialogUtil;
import com.slib.utils.string.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Vincent.Lei on 2017/5/24.
 * Title：
 * Note：
 */

public class ResumeDetailPresenter extends ResumeBasePresenter implements AttachmentRemarkDao.IRemarkAudioLoadListener {
    public static final int SELF_TOAST_RECORD = 1;
    public static final int SELF_TOAST_COLLECTION = 2;
    public static final int TOAST_ADD_OR_REMOVE_TRACKINGLIST = 3;

    private IResumeDetailView mResumeDetailView;
    private Resources mResources;
    private String[] mMoreEditMenuNormal;
    private ArrayList<RemarkBean> mRemarkList = new ArrayList<>();
    private ResumeDetailBean mResumeDetailBean;
    private Object mFilterTag = new Object();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ResumeCompareDao mResumeCompareDao;
    private IResumeDetailView.IntentParam mIntentParam;
    private ResumeLogger mResumeLogger = new ResumeLogger();
    private RecommendLogger mRecommendLogger = new RecommendLogger();
    private ResumeDetailLogger resumeDetailLogger = new ResumeDetailLogger();
    private boolean isAddRemark = false;

    public ResumeDetailPresenter(IResumeDetailView mResumeDetailView, Intent intent) {
        this.mResumeDetailView = mResumeDetailView;
        this.mResources = mResumeDetailView.callGetResources();
        this.mIntentParam = (IResumeDetailView.IntentParam) intent.getSerializableExtra("intentParam");
        resumeDetailLogger.setPageCode(mResumeDetailView.callGetBindActivity(), mIntentParam.pageCode);
    }

    @Override
    public void initialize() {
        subscribeEvents(true);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                loadInitData();
            }
        });
    }

    public ArrayList<RemarkBean> getRemarkListData() {
        return mRemarkList;
    }

    @Override
    public ResumeDetailBean getCurrentResumeData() {
        return mResumeDetailBean;
    }

    @Override
    public ResumeCompareDao getResumeCompareDao() {
        return mResumeCompareDao;
    }

    public IResumeDetailView.IntentParam getmIntentParam() {
        return mIntentParam;
    }

    public String[] getMoreEditMenu() {
        if (mResumeDetailBean == null)
            return null;
        if (mMoreEditMenuNormal == null)
            mMoreEditMenuNormal = mResources.getStringArray(R.array.menu_candidate_edit_more_normal);
        return mMoreEditMenuNormal;
    }

    public ResumeLogger getResumeLogger() {
        return mResumeLogger;
    }

    public ResumeDetailLogger getResumeDetailLogger() {
        return resumeDetailLogger;
    }

    @Override
    public void onDestroy() {
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        AttachmentRemarkDao.unRegisterListeners(this);
        subscribeEvents(false);
        releaseRecordResource();
    }

    public void loadInitData() {
        if (mIntentParam.isCompany) {
            loadCompanyResumeDetail();
        } else {
            if (mIntentParam.updateHistoryId != null)
                loadUpdatedContent();
            else
                loadResumeDetail();
        }
    }

    private void loadUpdatedContent() {
        ResumeUpdateDao.loadResumeUpdatedHistory(mIntentParam.updateHistoryId, new IDefaultRequestReplyListener<ResumeUpdatedHistoryResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, ResumeUpdatedHistoryResultBean.Data data, int errorCode, String errorMsg) {
                mResumeDetailView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success && data != null && data.before != null && data.current != null) {
                    mResumeDetailBean = data.before;
                    //当前查看的自己的简历更新内容,手动重置unionId
                    mResumeDetailBean.unionId = UserInfoManager.getInstance().getUserInfo().unionId;
                    mResumeCompareDao = new ResumeCompareDao();
                    mResumeCompareDao.buildNewContentToCurrentData(mResumeDetailBean, data.current);
                    //更新当前要展示的更新时间
                    mResumeDetailBean.sourceUpdateTime = data.current.sourceUpdateTime;
                    mResumeDetailView.callUpdateHandleResumeDataViews();
                    mResumeDetailView.updateViewForUpdateHistory();
                }
            }
        });
    }

    private void loadResumeDetail() {
        ResumeDao.getResumeDetail(mIntentParam.resumeId, new IDefaultRequestReplyListener<ResumeDetailResultBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeDetailResultBean candidateDetailResultBean, int errorCode, String errorMsg) {
                mResumeDetailView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success && candidateDetailResultBean.data != null) {
                    mResumeDetailBean = candidateDetailResultBean.data;
                    mResumeDetailView.updateViewForCurrentMode();
                    mResumeDetailView.callUpdateHandleResumeDataViews();

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mResumeDetailBean.inquiryList != null && !mResumeDetailBean.inquiryList.isEmpty()) {
                                mRemarkList.clear();
                                mRemarkList.addAll(mResumeDetailBean.inquiryList);
                                for (int i = 0, size = mRemarkList.size(); i < size; i++) {
                                    mRemarkList.get(i).validContent();
                                }
                            }
                            mResumeDetailView.callUpdateRemarkViews(CommonConst.LIST_POSITION_NONE);
                        }
                    }, 500);

                    if (mIntentParam.isAddRemarkMode) {
                        mIntentParam.isAddRemarkMode = false;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mResumeDetailView.callShowAddRemarkView();
                            }
                        });
                    }
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_SHOULD_RESET_SCAN_HISTORY, mResumeDetailBean, mFilterTag);
                    resumeDetailLogger.sendPageOpenLog(mResumeDetailView.callGetBindActivity(), mResumeDetailBean.candidateId, mResumeDetailBean.firmId, mResumeDetailBean.ownerId);
                }

                if (errorCode == CustomerRequestAssistHandler.NET_REQUEST_RESUME_DELETED_ERROR) {
                    ResumeBean resumeBean = new ResumeBean();
                    resumeBean.candidateId = mIntentParam.resumeId;
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_DELETED_RESUME, resumeBean, mFilterTag);
                }
            }
        });
    }

    public void collection() {
        if (mResumeDetailBean.collectStatus == 0) {
            resumeDetailLogger.sendCollectLog(mResumeDetailView.callGetBindActivity(), mResumeDetailBean.candidateId, mResumeDetailBean.firmId, mResumeDetailBean.ownerId);
        } else {
            resumeDetailLogger.sendCancelCollectLog(mResumeDetailView.callGetBindActivity(), mResumeDetailBean.candidateId, mResumeDetailBean.firmId, mResumeDetailBean.ownerId);
        }
        mResumeDetailView.callShowProgress(null, true);
        CollectionDao.doOrCancelCollection(mResumeDetailBean, new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(boolean success, String s, int errorCode, String errorMsg) {
                mResumeDetailView.callCancelProgress();
                if (success) {
                    mResumeDetailBean.collectStatus = (mResumeDetailBean.collectStatus == CommonConst.COLLECTION_NO ? CommonConst.COLLECTION_YES : CommonConst.COLLECTION_NO);
                    mResumeDetailView.callShowSpecialToastMsg(SELF_TOAST_COLLECTION, null, (mResumeDetailBean.collectStatus == CommonConst.COLLECTION_NO ? R.string.un_collection_success : R.string.collection_success));
                    mResumeDetailView.callUpdateCollectionStatus();
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_COLLECTION_RESUME_CHANGED, mResumeDetailBean, mFilterTag);
                    return;
                }
                mResumeDetailView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    /**
     * 删除简历
     */
    public void deleteResume() {
        mResumeDetailView.callShowProgress(null, true);
        ResumeDao.deleteResume(mResumeDetailBean.candidateId, new IDefaultRequestReplyListener<ResumeDetailResultBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeDetailResultBean candidateDetailResultBean, int errorCode, String errorMsg) {
                mResumeDetailView.callCancelProgress();
                if (success) {
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_DELETED_RESUME, mResumeDetailBean, mFilterTag);
                    mResumeDetailView.callGetBindActivity().finish();
                    return;
                }
                mResumeDetailView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public static String removeLineFeedSymbolAtEnd(String source) {
        if (TextUtils.isEmpty(source))
            return null;
        String symbol = "\r\n";
        if (source.endsWith(symbol)) {
            int index;
            do {
                index = source.lastIndexOf(symbol);
                if (index > 0)
                    source = source.substring(0, index);
            }
            while (source.endsWith(symbol));
        }
        return source;
    }


    /**
     * 销毁录音控件(注意 startRecord 和finishRecord请配套使用 页面销毁时请调用releaseRecordResource)
     */
    private void releaseRecordResource() {
        RecorderTool.getInstance().destroyRecord();
        AudioPlayerTool.getInstance().stop();
    }

    /**
     * 播放录音
     */
    public void playVoice(File source, boolean changePlayMode) {
        AudioPlayerTool.getInstance().startPlay(source, changePlayMode, new AudioPlayerTool.IAudioPlayerListener() {
            @Override
            public void onPrepared() {

            }

            @Override
            public void onCompletion() {
                mResumeDetailView.callFinishPlayVoice();
            }

            @Override
            public void onInterrupt() {
//                mResumeDetailView.callFinishPlayVoice();
            }

            @Override
            public void onError(String error) {
                mResumeDetailView.callFinishPlayVoice();
            }

            @Override
            public void onDurationChanged(long curPosition) {
                mResumeDetailView.callUpdateDurationChangedView(curPosition);
            }
        });
    }

    /**
     * 停止播放
     */
    public void stopPlayRecord() {
        AudioPlayerTool.getInstance().stop();
    }

    public void pausePlayRecord() {
        AudioPlayerTool.getInstance().pause();
    }

    public void continuePlayRecord() {
        AudioPlayerTool.getInstance().continuePlaying();
    }

    public void seekPlayRecord(int timeSecond) {
        AudioPlayerTool.getInstance().seek(timeSecond);
    }

    /**
     * 删除备注
     */
    public void deleteRemark(final RemarkBean remarkBean) {
        if (remarkBean == null || remarkBean.inquiryId == null)
            return;
        mResumeDetailView.callShowProgress(null, false);
        ResumeDao.deleteRemark(mResumeDetailBean.candidateId, remarkBean.inquiryId, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mResumeDetailView.callCancelProgress();
                if (success) {
                    mRemarkList.remove(remarkBean);
                    mResumeDetailView.callUpdateRemarkViews(-1);
                    if (mResumeDetailBean != null && mResumeDetailBean.inquiryList != null && !mResumeDetailBean.inquiryList.isEmpty()) {
                        mResumeDetailBean.inquiryList.remove(remarkBean);
                    }
                    if (remarkBean.isVoice()) {
                        AttachmentRemarkDao.deleteRemarkAttachmentInfo(remarkBean);
                        mResumeLogger.sendDeleteAudioRemark(mResumeDetailView.callGetBindActivity(), mResumeDetailBean.candidateId, remarkBean.inquiryId);
                    }
                    return;
                }
                mResumeDetailView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void addOrUpdateRemark(RemarkBean newRemarkBean) {
        newRemarkBean.validContent();
        resumeDetailLogger.sendAddInquiryLog(mResumeDetailView.callGetBindActivity(), mResumeDetailBean.candidateId, mResumeDetailBean.firmId,
                mResumeDetailBean.ownerId, newRemarkBean.content == null ? "" : newRemarkBean.content);
        boolean isNewAdd = true;
        for (int i = 0, size = mRemarkList.size(); i < size; i++) {
            if (TextUtils.equals(newRemarkBean.inquiryId, mRemarkList.get(i).inquiryId)) {
                mRemarkList.set(i, newRemarkBean);
                isNewAdd = false;
                break;
            }
        }
        if (isNewAdd) {
            //添加寻访时直接刷新详情
//            if (mResumeDetailView.callGetBindActivity() instanceof CompanyDetailActivity) {
            mResumeDetailView.callShowLoadingView();
            isAddRemark = true;
            loadInitData();
            return;
//            }
//            mRemarkList.add(0, newRemarkBean);
//            if (mResumeDetailBean.inquiryList == null) {
//                mResumeDetailBean.inquiryList = new ArrayList<>(1);
//                mResumeDetailBean.inquiryList.add(newRemarkBean);
//            } else
//                mResumeDetailBean.inquiryList.add(0, newRemarkBean);
        }
        mResumeDetailView.callUpdateRemarkViews(CommonConst.LIST_POSITION_NONE);
    }

    public void startCall() {
        if (mResumeDetailBean == null)
            return;
        AppUtil.makeCall(mResumeDetailView.callGetBindActivity(), mResumeDetailBean.mobile);
        ResumeUpdateDao.sendUpdateLog(ResumeUpdateLogResultBean.LogData.TYPE_MAKE_CALL, mIntentParam.resumeId, null);
    }


    public void sendSms(String defaultMsg) {
        if (mResumeDetailBean == null)
            return;
        sendSms(mResumeDetailBean.mobile, defaultMsg);
    }

    private void sendSms(String mobile, String defaultMsg) {
        AppUtil.sendSmMessage(mResumeDetailView.callGetBindActivity(), mobile, defaultMsg);
    }

    public void sendEmail() {
        if (mResumeDetailBean == null)
            return;
        checkShareEmailBind(true, false, false);
    }

    private void realSendEmail() {
        Activity activity = mResumeDetailView.callGetBindActivity();
        if (activity == null || activity.isFinishing())
            return;
        SendEmailActivity.launch(activity, mResumeDetailBean.email, mResumeDetailBean.candidateId);
    }

    public void checkShareEmailBind(final boolean isCheckForSendNormalEmail, final boolean isShareContact, final boolean isShareRemark) {
        AddresseeBean bindEmailData = UserInfoManager.getInstance().getBindEmailData();
        if (bindEmailData != null && (bindEmailData.validStatus == AddresseeBean.VALID_OK)) {
            //邮箱验证通过过就不需要每次请求获取验证结果,否则每次都要判断下验证是否通过
            if (isCheckForSendNormalEmail) {
                realSendEmail();
                return;
            }
            mResumeDetailView.callEmailShare(true, isShareContact, isShareRemark);
            return;
        }
        mResumeDetailView.callShowProgress(null, true);
        EmailDao.checkBindEmail(new IDefaultRequestReplyListener<BindEmailResultBean>() {
            @Override
            public void onRequestReply(boolean success, BindEmailResultBean bindEmailResultBean, int errorCode, String errorMsg) {
                mResumeDetailView.callCancelProgress();
                if (success) {
                    UserInfoManager.getInstance().setBindEmailData(bindEmailResultBean.data);
                    if (bindEmailResultBean.data != null && bindEmailResultBean.data.validStatus == AddresseeBean.VALID_OK) {
                        if (isCheckForSendNormalEmail) {
                            realSendEmail();
                            return;
                        }
                        mResumeDetailView.callEmailShare(true, isShareContact, isShareRemark);
                    } else {
                        if (isCheckForSendNormalEmail) {
                            BindEmailActivity.launch(mResumeDetailView.callGetBindActivity(), true);
                            return;
                        }
                        mResumeDetailView.callEmailShare(false, isShareContact, isShareRemark);
                    }

                    return;
                }
                mResumeDetailView.callShowToastMessage(errorMsg, 0);
            }
        });
    }


    private void subscribeEvents(boolean isSubscribe) {
        if (isSubscribe) {
            UIEventsObservable.getInstance().subscribeEvent(IResumeEventsSubscriber.class, this, new IResumeEventsSubscriber() {
                @Override
                public boolean isFilterByTag(Object sendTag) {
                    return mFilterTag == sendTag;
                }

                @Override
                public boolean onResumeCollectionStatusChangedObserver(ResumeBean resumeBean, Object extra) {
                    if (resumeBean == null || mResumeDetailBean == null)
                        return false;
                    mResumeDetailBean.collectStatus = resumeBean.collectStatus;
                    mResumeDetailView.callUpdateCollectionStatus();
                    return false;
                }

                @Override
                public boolean onResumeModifiedObserver(ResumeBean resumeBean, Object extra) {
                    if (resumeBean == null || mResumeDetailBean == null)
                        return false;
                    boolean valid = false;
                    if (!TextUtils.isEmpty(resumeBean.mobile) && !TextUtils.equals(resumeBean.mobile, mResumeDetailBean.mobile)) {
                        valid = true;
                        mResumeDetailBean.markMobileValid(true);
                    }
                    if (!TextUtils.isEmpty(resumeBean.email) && !TextUtils.equals(resumeBean.email, mResumeDetailBean.email)) {
                        valid = true;
                        mResumeDetailBean.markEmailValid(true);
                    }
                    //通知简历被修改了
                    mResumeDetailBean.refreshModifyInfo(resumeBean);
                    mResumeDetailView.callUpdateHandleResumeDataViews();
                    if (valid)
                        mResumeDetailView.callUpdateMobileOrEmailValidView();
                    return false;
                }

                @Override
                public boolean onResumeLabelsChanged(ResumeBean data, Object extra) {
                    if (data == null || mResumeDetailBean == null)
                        return false;
                    if (data.tagBindingList == null || data.tagBindingList.isEmpty())
                        mResumeDetailBean.tagBindingList = null;
                    else {
                        if (mResumeDetailBean.tagBindingList == null)
                            mResumeDetailBean.tagBindingList = new ArrayList<>(data.tagBindingList.size());
                        mResumeDetailBean.tagBindingList.clear();
                        mResumeDetailBean.tagBindingList.addAll(data.tagBindingList);
                    }
                    mResumeDetailView.callUpdateResumeMainInfo();
                    return false;
                }
            });
        } else {
            UIEventsObservable.getInstance().stopSubscribeEvent(IResumeEventsSubscriber.class, this);
        }

    }


    public void scanResumeAttachmentOnLine(final ResumeAttachment attachment) {
        mResumeDetailView.callShowProgress(null, true);
        ResumeDao.loadOnLineAttachmentInfo(mResumeDetailBean.candidateId, attachment.fileId, attachment.sourceChannel, new IDefaultRequestReplyListener<ResumeOnLineResultBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeOnLineResultBean resumeOnLineResultBean, int errorCode, String errorMsg) {
                mResumeDetailView.callCancelProgress();
                if (resumeOnLineResultBean != null && !TextUtils.isEmpty(resumeOnLineResultBean.previewUrl)) {
                    RemoteBrowserActivity.launch(mResumeDetailView.callGetBindActivity(), mResources.getString(R.string.candidate_detail_title, mResumeDetailBean.realName), true, resumeOnLineResultBean.previewUrl);
                    mResumeLogger.sendAttachmentReview(mResumeDetailView.callGetBindActivity(), mResumeDetailBean.candidateId, attachment.fileId);
                }
                if (!success) {
                    mResumeDetailView.callShowToastMessage(errorMsg, 0);
                }
            }
        });
    }

    public void highLightUpdatedByEngineContent() {
        UpdateResumeWrapperBean wrapperBean = (UpdateResumeWrapperBean) mResumeDetailView.callGetApplication().getCachedTransformData(CommonConst.STR_TRANSFORM_RESUME_UPDATED_CONTENT_KEY);
        if (wrapperBean == null || mIntentParam.resumeId == null || !mIntentParam.resumeId.equals(wrapperBean.candidateId))
            return;
        mResumeCompareDao = new ResumeCompareDao();
        mResumeCompareDao.buildNewContentToCurrentData(mResumeDetailBean, wrapperBean);
        mResumeDetailBean.sourceUpdateTime = System.currentTimeMillis();
        mResumeDetailView.callUpdateHandleResumeDataViews();
        mResumeCompareDao = null;
    }

    public void loadRemarkAudioSourceFile(RemarkBean remarkBean) {
        AttachmentRemarkDao.registerListeners(this);
        AttachmentRemarkDao.loadRemarkAudioSourceFile(remarkBean);
        updateAudioAttachmentStatus(remarkBean.inquiryId);
    }

    private void updateAudioAttachmentStatus(String inquiryId) {
        int targetPosition = CommonConst.LIST_POSITION_NONE;
        if (inquiryId != null)
            for (int i = 0; i < mRemarkList.size(); i++) {
                if (inquiryId.equals(mRemarkList.get(i).inquiryId)) {
                    targetPosition = i;
                    break;
                }
            }
        if (targetPosition != CommonConst.LIST_POSITION_NONE)
            mResumeDetailView.callUpdateRemarkViews(targetPosition);
    }

    @Override
    public void onRemarkAudioLoadFailed(String inquiryId) {
        updateAudioAttachmentStatus(inquiryId);
    }

    @Override
    public void onRemarkAudioLoadLoadSuccess(String inquiryId, File audioFile) {
        updateAudioAttachmentStatus(inquiryId);
    }

    /**
     * 开始录音(注意 startRecord 和finishRecord请配套使用 页面销毁时请调用releaseRecordResource)
     */
    public void startRecord() {
        mResumeDetailView.callFinishPlayVoice();
        RecorderTool.getInstance().startRecord(new RecorderTool.IBZWRecorderListener() {
            @Override
            public void onRecordReady() {

            }

            @Override
            public void onRecordStart(File audioFile) {

            }

            @Override
            public void onRecordSuccess(File audioFile, long audioLength) {
                if (audioFile != null && audioFile.exists()) {
                    //存储本次音频备注
                    createAudioAttachment(audioFile, (int) (audioLength / 1000), null);
                }
            }

            @Override
            public void onRecordFail() {
                mResumeDetailView.callShowToastMessage(null, R.string.audio_error_default);
            }

            @Override
            public void onRecordCancel() {
                mResumeDetailView.callShowToastMessage(null, R.string.audio_error_cancelled);
            }

            @Override
            public void onRecordReachedMaxTime(int maxTime) {
                mResumeDetailView.callShowToastMessage(null, R.string.audio_error_time_overflow);
            }
        });
    }

    /**
     * 结束录音(注意 startRecord 和finishRecord请配套使用 页面销毁时请调用releaseRecordResource)
     */
    public void finishRecord() {
        RecorderTool.getInstance().stopRecord(false);
    }

    public void cancelRecord() {
        RecorderTool.getInstance().stopRecord(true);
    }

    private void createAudioAttachment(final File audioFile, final int audioLength, OSSFileUploadResultBean.Data oss) {
        AttachmentRemarkDao.createAudioRemark(mResumeDetailBean.candidateId, audioFile, oss, audioLength, new AttachmentRemarkDao.ICreateAttachmentRemarkListener() {
            @Override
            public void onUploadAttachment() {
                mResumeDetailView.callShowProgress(null, false);
            }

            @Override
            public void onUploadAttachmentResult(boolean success, OSSFileUploadResultBean.Data data, int errorCode, String errorMsg) {
                if (!success) {
                    mResumeDetailView.callCancelProgress();
                    DialogUtil.doubleButtonShow(mResumeDetailView.callGetBindActivity(), 0, R.string.audio_remark_submit_failed, R.string.cancel, R.string.re_submit, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            createAudioAttachment(audioFile, audioLength, null);
                        }
                    }, null);
                }
            }

            @Override
            public void onCreateRemark() {
                mResumeDetailView.callShowProgress(null, false);
            }

            @Override
            public void onCreateRemarkResult(boolean success, int errorCode, String errorMsg, final OSSFileUploadResultBean.Data data, RemarkBean newRemarkBean) {
                mResumeDetailView.callCancelProgress();
                if (success) {
                    newRemarkBean.validContent();
                    mRemarkList.add(0, newRemarkBean);
                    mResumeDetailView.callUpdateRemarkViews(CommonConst.LIST_POSITION_NONE);
                    mResumeLogger.sendAddAudioRemark(mResumeDetailView.callGetBindActivity(), mResumeDetailBean.candidateId);
                    return;
                }
                DialogUtil.doubleButtonShow(mResumeDetailView.callGetBindActivity(), 0, R.string.audio_remark_submit_failed, R.string.cancel, R.string.re_submit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createAudioAttachment(audioFile, audioLength, data);
                    }
                }, null);
            }

        });
    }

    public void addRecommend(String content, Date date) {
        mResumeDetailView.callShowProgress(null, true);
        RecommendDao.addRecommend(mIntentParam.isCompany ? "true" : "false", mResumeDetailBean.candidateId, content, date, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mResumeDetailView.callCancelProgress();
                if (success) {
                    mResumeDetailView.callShowToastMessage(null, R.string.add_recommend_success);
                    mRecommendLogger.sendAddRecommend(mResumeDetailView.callGetBindActivity(), mResumeDetailBean.candidateId);
                    return;
                }
                mResumeDetailView.callShowToastMessage(errorMsg, errorCode);
            }
        });
        resumeDetailLogger.sendAddRemindLog(mResumeDetailView.callGetBindActivity(), mResumeDetailBean.candidateId, mResumeDetailBean.firmId,
                mResumeDetailBean.ownerId, content, DateUtil.dateToString(date, DateUtil.SDF_RECOMMEND));
    }

    public void markValid(final boolean mobileValid) {
        mResumeDetailView.callShowProgress(null, true);
        ResumeDao.markValid(mResumeDetailBean.candidateId, mobileValid ? ResumeDao.VALID_TYPE_MOBILE : ResumeDao.VALID_TYPE_EMAIL, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mResumeDetailView.callCancelProgress();
                if (success) {
                    mResumeDetailView.callShowToastMessage(null, R.string.mark_success);
                    if (mobileValid) {
                        mResumeDetailBean.markMobileValid(false);
                        mResumeLogger.sendMarkMobileInvalid(mResumeDetailView.callGetBindActivity(), mResumeDetailBean.candidateId, mResumeDetailBean.mobile);
                    } else {
                        mResumeDetailBean.markEmailValid(false);
                        mResumeLogger.sendMarkEmailInvalid(mResumeDetailView.callGetBindActivity(), mResumeDetailBean.candidateId, mResumeDetailBean.email);
                    }
                    mResumeDetailView.callUpdateMobileOrEmailValidView();

                    return;
                }
                mResumeDetailView.callShowToastMessage(errorMsg, errorCode);
            }
        });
    }

    //********************企业人才库新加*********************
    public String[] getCompanyMoreEditMenu() {
        if (mResumeDetailBean == null)
            return null;
        if (mResumeDetailBean.trackingListStatus == 0) {
            mMoreEditMenuNormal = mResources.getStringArray(R.array.menu_company_detail_more_add);
        } else if (mResumeDetailBean.trackingListStatus == 1) {
            mMoreEditMenuNormal = mResources.getStringArray(R.array.menu_company_detail_more_remove);
        }
        return mMoreEditMenuNormal;
    }

    private void loadCompanyResumeDetail() {
        ResumeDao.getCompanyResumeDetail(mIntentParam.resumeId, new IDefaultRequestReplyListener<ResumeDetailResultBean>() {
            @Override
            public void onRequestReply(boolean success, ResumeDetailResultBean candidateDetailResultBean, int errorCode, String errorMsg) {
                mResumeDetailView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success && candidateDetailResultBean.data != null) {
                    mResumeDetailBean = candidateDetailResultBean.data;
                    mResumeDetailView.updateViewForCurrentMode();
                    mResumeDetailView.callUpdateHandleResumeDataViews();

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mResumeDetailBean.inquiryList != null && !mResumeDetailBean.inquiryList.isEmpty()) {
                                mRemarkList.clear();
                                for (RemarkBean bean : mResumeDetailBean.inquiryList) {
                                    if (bean.isVoice() && bean.attachment != null && bean.attachment.attachmentDuration < 60 && StringUtil.isEmpty(bean.content)) {
                                        continue;
                                    }

                                    if (bean.isVoice() && bean.attachment != null && bean.attachment.attachmentDuration < 60 && !StringUtil.isEmpty(bean.content)) {
                                        bean.inquiryType = RemarkBean.INQUIRY_TYPE_TEXT;
                                    }

                                    mRemarkList.add(bean);
                                }
                                for (int i = 0, size = mRemarkList.size(); i < size; i++) {
                                    mRemarkList.get(i).validContent();
                                }
                            }
                            mResumeDetailView.callUpdateRemarkViews(CommonConst.LIST_POSITION_NONE);
                        }
                    }, 500);

                    if (mIntentParam.isAddRemarkMode) {
                        mIntentParam.isAddRemarkMode = false;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mResumeDetailView.callShowAddRemarkView();
                            }
                        });
                    }
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_SHOULD_RESET_SCAN_HISTORY, mResumeDetailBean, mFilterTag);
                    resumeDetailLogger.sendPageOpenLog(mResumeDetailView.callGetBindActivity(), mResumeDetailBean.candidateId, mResumeDetailBean.firmId, mResumeDetailBean.ownerId);
                }

                if (errorCode == CustomerRequestAssistHandler.NET_REQUEST_RESUME_DELETED_ERROR) {
                    ResumeBean resumeBean = new ResumeBean();
                    resumeBean.candidateId = mIntentParam.resumeId;
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_DELETED_RESUME, resumeBean, mFilterTag);
                }
            }
        });
    }

    //查看手机或邮箱
    public void checkContact(int type, String id) {
        mResumeDetailView.callShowProgress(null, true);
        ResumeDao.checkContact(type, id, new IDefaultRequestReplyListener<ContactResultBean>() {
            @Override
            public void onRequestReply(boolean success, ContactResultBean contactResultBean, int errorCode, String errorMsg) {
                mResumeDetailView.callCancelProgress();
                if (success && !StringUtil.isEmpty(contactResultBean.data)) {
                    mResumeDetailView.callUpdateMobileOrEmailNum(type, contactResultBean.data);
                }
            }
        });
    }

    //添加到TrackingList / 从TrackingList 移除
    public void addOrRemoveTrackingList(int type, String id) {
        mResumeDetailView.callShowProgress(null, true);
        ResumeDao.addOrRemoveTrackingList(type, id, new IDefaultRequestReplyListener<AddOrRemoveTrackingListResultBean>() {
            @Override
            public void onRequestReply(boolean success, AddOrRemoveTrackingListResultBean contactResultBean, int errorCode, String errorMsg) {
                mResumeDetailView.callCancelProgress();
                if (success) {
                    if (type == 0) {
                        mResumeDetailBean.trackingListStatus = 1;
                        mResumeDetailView.callShowSpecialToastMsg(TOAST_ADD_OR_REMOVE_TRACKINGLIST, "已加入trackinglist", 0);
                    } else if (type == 1) {
                        mResumeDetailBean.trackingListStatus = 0;
                        mResumeDetailView.callShowSpecialToastMsg(TOAST_ADD_OR_REMOVE_TRACKINGLIST, "已移除trackinglist", 0);
                    }
                }
            }
        });
    }

    public boolean isAddRemark() {
        return isAddRemark;
    }
}
