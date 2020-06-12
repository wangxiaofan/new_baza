package com.baza.android.bzw.businesscontroller.message.viewinterface;

import android.widget.TextView;

import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/6/2.
 * Title：
 * Note：
 */

public interface IChatView extends IBaseView {
    void callRefreshMessageView(int targetPosition, int selection, int lastCount, boolean isNewMessageArrival);

    void callOnLineStatusChanged(int statusCode);

    void callSetTitle(String title);

    void callFinishPlayVoice();

    void callUpdateIsFriendView(boolean isFriend);

    void callShowAudioRecordView(TextView touchView);

    void callSetMessageListToBottom();
}
