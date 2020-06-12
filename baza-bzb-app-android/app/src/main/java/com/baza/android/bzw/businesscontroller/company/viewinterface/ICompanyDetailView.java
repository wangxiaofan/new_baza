package com.baza.android.bzw.businesscontroller.company.viewinterface;

import com.baza.android.bzw.businesscontroller.resume.base.viewinterface.IResumeBaseView;

import java.io.Serializable;

/**
 * Created by Vincent.Lei on 2017/5/24.
 * Title：
 * Note：
 */

public interface ICompanyDetailView extends IResumeBaseView {

    class IntentParam implements Serializable {
        public String resumeId;
        public String updateHistoryId;
        public boolean isAddRemarkMode;

        public IntentParam(String resumeId) {
            this.resumeId = resumeId;
        }

        public IntentParam resumeId(String resumeId) {
            this.resumeId = resumeId;
            return this;
        }

        public IntentParam updateHistoryId(String updateHistoryId) {
            this.updateHistoryId = updateHistoryId;
            return this;
        }

        public IntentParam isAddRemarkMode(boolean isAddRemarkMode) {
            this.isAddRemarkMode = isAddRemarkMode;
            return this;
        }
    }

    void updateViewForCurrentMode();

    void updateViewForUpdateHistory();

    void callShowSpecialToastMsg(int type, String msg, int msgId);

    void callFinishPlayVoice();

    void callUpdateRemarkViews(int targetPosition);

    void callShowAddRemarkView();

    void callEmailShare(boolean hasBindEmail, final boolean isShareContact, final boolean isShareRemark);

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callShowLoadingView();

    void callUpdateCollectionStatus();

    void callUpdateResumeMainInfo();

    void updateToTargetResume();

    void callUpdateDurationChangedView(long curPosition);

    void callShowRecommendView();

    void callUpdateMobileOrEmailValidView();
}
