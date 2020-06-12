package com.baza.android.bzw.businesscontroller.account.presenter;

import android.text.TextUtils;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IFeedBackView;
import com.baza.android.bzw.dao.AccountDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2018/1/12.
 * Title：
 * Note：
 */

public class FeedBackPresenter extends BasePresenter {
    private IFeedBackView mFeedBackView;

    public FeedBackPresenter(IFeedBackView feedBackView) {
        this.mFeedBackView = feedBackView;
    }

    @Override
    public void initialize() {

    }

    public void submit() {
        String content = mFeedBackView.callGetContent();
        if (TextUtils.isEmpty(content)) {
            mFeedBackView.callShowToastMessage(null, R.string.input_feed_back);
            return;
        }
        mFeedBackView.callShowProgress(null, true);
        AccountDao.feedBack(content, new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(boolean success, String s, int errorCode, String errorMsg) {
                mFeedBackView.callCancelProgress();
                if (success) {
                    mFeedBackView.callShowToastMessage(null, R.string.feed_back_success);
                    mFeedBackView.callGetBindActivity().finish();
                    return;
                }
                mFeedBackView.callShowToastMessage(errorMsg, 0);
            }
        });
    }
}
