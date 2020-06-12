package com.baza.android.bzw.businesscontroller.audio.tools;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.constant.CommonConst;
import com.netease.nimlib.sdk.media.record.AudioRecorder;
import com.netease.nimlib.sdk.media.record.IAudioRecordCallback;
import com.netease.nimlib.sdk.media.record.RecordType;

import java.io.File;

/**
 * Created by Vincent.Lei on 2017/7/7.
 * Title：
 * Note：
 */

public class RecorderTool {
    public interface IBZWRecorderListener {
        void onRecordReady();

        void onRecordStart(File audioFile);

        void onRecordSuccess(File audioFile, long audioLength);

        void onRecordFail();

        void onRecordCancel();

        void onRecordReachedMaxTime(int maxTime);
    }

    private static RecorderTool mInstance = new RecorderTool();
    private AudioRecorder mAudioRecorder;
    private IBZWRecorderListener mListener;

    private RecorderTool() {
    }

    public static RecorderTool getInstance() {
        return mInstance;
    }

    private void initRecord() {
        mAudioRecorder = new AudioRecorder(BZWApplication.getApplication(), RecordType.AAC, CommonConst.MAX_VOICE_TIME_SECONDS, new IAudioRecordCallback() {
            @Override
            public void onRecordReady() {
                if (mListener != null)
                    mListener.onRecordReady();
            }

            @Override
            public void onRecordStart(File audioFile, RecordType recordType) {
                if (mListener != null)
                    mListener.onRecordStart(audioFile);
            }

            @Override
            public void onRecordSuccess(File audioFile, long audioLength, RecordType recordType) {
                if (mListener != null) {
                    if (audioLength >= 1000)
                        mListener.onRecordSuccess(audioFile, audioLength);
                    else
                        mListener.onRecordFail();
                }
                mListener = null;
            }

            @Override
            public void onRecordFail() {
                if (mListener != null)
                    mListener.onRecordFail();
                mListener = null;
            }

            @Override
            public void onRecordCancel() {
                if (mListener != null)
                    mListener.onRecordCancel();
                mListener = null;
            }

            @Override
            public void onRecordReachedMaxTime(int maxTime) {
                if (mListener != null)
                    mListener.onRecordReachedMaxTime(maxTime);
                mAudioRecorder.handleEndRecord(true, maxTime);
            }
        });
    }

    public void startRecord(IBZWRecorderListener listener) {
        if (listener == null)
            return;
        if (mAudioRecorder == null)
            initRecord();
        this.mListener = listener;
        mAudioRecorder.startRecord();
    }

    public void stopRecord(boolean cancel) {
        if (mAudioRecorder != null)
            mAudioRecorder.completeRecord(cancel);
    }

    public void destroyRecord() {
        if (mAudioRecorder != null) {
            try {
                mAudioRecorder.destroyAudioRecorder();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mListener = null;
        mAudioRecorder = null;
    }
}
