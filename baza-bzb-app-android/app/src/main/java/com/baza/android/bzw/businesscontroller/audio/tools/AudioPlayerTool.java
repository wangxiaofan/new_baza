package com.baza.android.bzw.businesscontroller.audio.tools;

import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.constant.SharedPreferenceConst;
import com.netease.nimlib.sdk.media.player.OnPlayListener;
import com.slib.storage.sharedpreference.SharedPreferenceManager;

import java.io.File;

/**
 * Created by Vincent.Lei on 2017/7/7.
 * Title：
 * Note：
 */

public class AudioPlayerTool {
    public static final int STREAM_MUSIC = 0;
    public static final int STREAM_VOICE_CALL = 1;

    public interface IAudioPlayerListener {
        void onPrepared();

        void onCompletion();

        void onInterrupt();

        void onError(String error);

        void onDurationChanged(long curPosition);
    }

    private static AudioPlayerTool mInstance = new AudioPlayerTool();
    private ExtendAudioPlayer mAudioPlayer;
    private IAudioPlayerListener mListener;
    private int mCurrentStreamMode;
    private Runnable mPlayingRunnable;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private AudioPlayerTool() {
        mCurrentStreamMode = SharedPreferenceManager.getInt(SharedPreferenceConst.SP_VOICE_PLAY_MODE);
        if (mCurrentStreamMode != STREAM_MUSIC)
            mCurrentStreamMode = STREAM_VOICE_CALL;
    }

    public boolean isStreamMusic() {
        return mCurrentStreamMode == STREAM_MUSIC;
    }

    public static AudioPlayerTool getInstance() {
        return mInstance;
    }

    private void initAudioPlayer() {
        mAudioPlayer = new ExtendAudioPlayer(BZWApplication.getApplication());
        mAudioPlayer.setOnPlayListener(new OnPlayListener() {
            @Override
            public void onPrepared() {
                if (mListener != null)
                    mListener.onPrepared();
            }

            @Override
            public void onCompletion() {
                if (mListener != null)
                    mListener.onCompletion();
                mListener = null;
            }

            @Override
            public void onInterrupt() {
                if (mListener != null)
                    mListener.onInterrupt();
                mListener = null;
            }

            @Override
            public void onError(String error) {
                if (mListener != null)
                    mListener.onError(error);
                mListener = null;
            }

            @Override
            public void onPlaying(long curPosition) {
                if (mListener != null)
                    mListener.onDurationChanged(curPosition);
            }
        });
    }

    public void startPlay(File fileSource, boolean resetMode, IAudioPlayerListener listener) {
        mHandler.removeCallbacksAndMessages(null);
        if (mAudioPlayer == null)
            initAudioPlayer();
        if (mAudioPlayer.isPlaying())
            mAudioPlayer.stop();
        this.mListener = listener;
        if (resetMode) {
            mCurrentStreamMode = (mCurrentStreamMode == STREAM_MUSIC ? STREAM_VOICE_CALL : STREAM_MUSIC);
            SharedPreferenceManager.saveInt(SharedPreferenceConst.SP_VOICE_PLAY_MODE, mCurrentStreamMode);
        }
        mAudioPlayer.setDataSource(fileSource.getAbsolutePath());
        if (mPlayingRunnable == null)
            mPlayingRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mAudioPlayer != null)
                        mAudioPlayer.start((mCurrentStreamMode == STREAM_MUSIC ? AudioManager.STREAM_MUSIC : AudioManager.STREAM_VOICE_CALL));
                }
            };
        mHandler.postDelayed(mPlayingRunnable, 100);
    }

    public void stop() {
        mHandler.removeCallbacksAndMessages(null);
        if (mAudioPlayer != null)
            mAudioPlayer.stop();
        mListener = null;
    }

    public void pause() {
        if (mAudioPlayer != null)
            mAudioPlayer.pause();
    }

    public void continuePlaying() {
        if (mAudioPlayer != null)
            mAudioPlayer.continuePlaying();
    }

    public void seek(int timeSecond) {
        if (mAudioPlayer != null)
            mAudioPlayer.seekTo(timeSecond * 1000);
    }
}