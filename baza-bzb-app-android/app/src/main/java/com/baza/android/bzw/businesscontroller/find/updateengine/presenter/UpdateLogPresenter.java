package com.baza.android.bzw.businesscontroller.find.updateengine.presenter;

import android.content.Intent;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.resume.ResumeUpdateLogResultBean;
import com.baza.android.bzw.businesscontroller.audio.tools.AudioPlayerTool;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IUpdateLogView;
import com.baza.android.bzw.dao.ResumeUpdateDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.bznet.android.rcbox.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/8/29.
 * Title：
 * Note：
 */

public class UpdateLogPresenter extends BasePresenter {
    private IUpdateLogView mUpdateLogView;
    private String mOwnerName;
    private String mResumeId;
    private List<ResumeUpdateLogResultBean.LogData> mDataList = new ArrayList<>();
//    private List<ResumeUpdateLogResultBean.LogData> mLocalList;
//    private List<ResumeUpdateLogResultBean.LogData> mTempLocalList;
//    private List<ResumeUpdateLogResultBean.LogData> mTempList;
    private int mCurrentPage;
//    private boolean hasAppendLocalLog;

    public UpdateLogPresenter(IUpdateLogView updateLogView, Intent intent) {
        this.mUpdateLogView = updateLogView;
        this.mResumeId = intent.getStringExtra("candidateId");
        this.mOwnerName = intent.getStringExtra("ownerName");
    }

    @Override
    public void initialize() {
        mUpdateLogView.callSetTitle(mUpdateLogView.callGetResources().getString(R.string.owner_log, mOwnerName));
        getLogs(true);

    }

    @Override
    public void onDestroy() {
        releaseRecordResource();
    }

    public List<ResumeUpdateLogResultBean.LogData> getLogDataList() {
        return mDataList;
    }

    public String getResumeUserName() {
        return mOwnerName;
    }

    public void getLogs(final boolean refresh) {
        if (refresh)
            mCurrentPage = 1;
        ResumeUpdateDao.loadUpdateLogs(mCurrentPage, mResumeId, new IDefaultRequestReplyListener<ResumeUpdateLogResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, ResumeUpdateLogResultBean.Data data, int errorCode, String errorMsg) {
                mUpdateLogView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success) {
                    if (refresh)
                        mDataList.clear();
                    if (data != null) {
                        if (data.data != null && !data.data.isEmpty())
                            mDataList.addAll(data.data);
                        mUpdateLogView.callSetLoadMoreEnable((mDataList.size() < data.count));
                        mCurrentPage++;
                    }
//                    if (!hasAppendLocalLog)
//                        readLocalLog();
//                    else
//                        appendLocalLog(refresh);
                    mUpdateLogView.callRefreshListView();

                }
            }
        });
    }

//    private void readLocalLog() {
//        hasAppendLocalLog = true;
//        AttachmentRemarkDao.readResumeLocalRemark(mResumeId, new IDBReplyListener<List<RemarkBean>>() {
//            @Override
//            public void onDBReply(List<RemarkBean> list) {
//                if (list != null && !list.isEmpty()) {
//                    mLocalList = new ArrayList<>(list.size());
//                    ResumeUpdateLogResultBean.LogData logData;
//                    RemarkBean remarkBean;
//                    for (int i = 0, size = list.size(); i < size; i++) {
//                        remarkBean = list.get(i);
//                        logData = new ResumeUpdateLogResultBean.LogData();
//                        logData.created = remarkBean.updateTime;
//                        logData.user = UserInfoManager.getInstance().getUserInfo();
//                        logData.sceneId = (remarkBean.isPhoneRemark() ? ResumeUpdateLogResultBean.LogData.TYPE_VOICE_REMARK : ResumeUpdateLogResultBean.LogData.TYPE_IMAGE_REMARK);
//                        logData.setRemarkBean(remarkBean);
//                        mLocalList.add(logData);
//                    }
//                    appendLocalLog(true);
//                    mUpdateLogView.callRefreshListView();
//                }
//            }
//        });
//    }

//    private void appendLocalLog(boolean refresh) {
//        if (!hasAppendLocalLog || mLocalList == null || mLocalList.isEmpty())
//            return;
//        Comparator<ResumeUpdateLogResultBean.LogData> comparator = new Comparator<ResumeUpdateLogResultBean.LogData>() {
//            @Override
//            public int compare(ResumeUpdateLogResultBean.LogData o1, ResumeUpdateLogResultBean.LogData o2) {
//                return (int) (o2.created - o1.created);
//            }
//        };
//        mDataList.addAll(mLocalList);
//        Collections.sort(mDataList, comparator);
//    }

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
                mUpdateLogView.callFinishPlayVoice();
            }

            @Override
            public void onInterrupt() {
//                mResumeDetailView.callFinishPlayVoice();
            }

            @Override
            public void onError(String error) {
                mUpdateLogView.callFinishPlayVoice();
            }

            @Override
            public void onDurationChanged(long curPosition) {

            }
        });
    }

    /**
     * 停止播放
     */
    public void stopPlayRecord() {
        AudioPlayerTool.getInstance().stop();
    }

    private void releaseRecordResource() {
        AudioPlayerTool.getInstance().stop();
    }
}
