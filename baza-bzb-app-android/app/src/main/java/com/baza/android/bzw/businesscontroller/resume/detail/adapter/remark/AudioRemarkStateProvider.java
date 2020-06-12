package com.baza.android.bzw.businesscontroller.resume.detail.adapter.remark;

import android.os.Looper;

import com.baza.android.bzw.bean.resumeelement.RemarkBean;

/**
 * Created by Vincent.Lei on 2019/8/13.
 * Title：
 * Note：
 */
public class AudioRemarkStateProvider {
    public RemarkBean remarkOnPlaying;
    public int playingDuration;
    public StringBuilder stringBuilder;
    public boolean isPaused;
    public static final int MINUTE_SECONDS = 60;

    public boolean isCurrentVoiceIsPlaying(RemarkBean data) {
        return remarkOnPlaying != null && remarkOnPlaying.inquiryId != null && data != null && remarkOnPlaying.inquiryId.equals(data.inquiryId);
    }

    public boolean hasPlayingAudio() {
        return remarkOnPlaying != null;
    }

    public void clear() {
        remarkOnPlaying = null;
        playingDuration = 0;
        isPaused = false;
    }

    public int getProgress(RemarkBean currentData) {
        if (!isCurrentVoiceIsPlaying(currentData))
            return 0;
        return getProgress();
    }

    public int getProgress() {
        return (int) (playingDuration * 100 * 1.0f / remarkOnPlaying.getAudioTimeLength());
    }

    public String getShowDurationMsg(RemarkBean data) {
        if (stringBuilder == null || Looper.myLooper() != Looper.getMainLooper())
            stringBuilder = new StringBuilder();
        if (stringBuilder.length() > 0)
            stringBuilder.delete(0, stringBuilder.length());
        buildTime(stringBuilder, isCurrentVoiceIsPlaying(data) ? playingDuration : 0);
        stringBuilder.append("/");
        buildTime(stringBuilder, data.getAudioTimeLength());
        return stringBuilder.toString();
    }

    public String getShowDurationMsg() {
        return getShowDurationMsg(remarkOnPlaying);
    }

    public void buildTime(StringBuilder stringBuilder, int duration) {
        if (duration == 0) {
            stringBuilder.append("00:00");
            return;
        }
        int minute = (duration / MINUTE_SECONDS);
        if (minute >= 10)
            stringBuilder.append(minute);
        else
            stringBuilder.append("0").append(minute);
        stringBuilder.append(":");
        int second = (duration - minute * MINUTE_SECONDS);
        if (second >= 10)
            stringBuilder.append(second);
        else
            stringBuilder.append("0").append(second);
    }
}
