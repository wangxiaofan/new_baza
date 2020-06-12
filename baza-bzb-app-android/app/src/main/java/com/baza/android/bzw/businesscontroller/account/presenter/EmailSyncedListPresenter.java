package com.baza.android.bzw.businesscontroller.account.presenter;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.email.ListSyncEmailBean;
import com.baza.android.bzw.bean.email.ListSyncEmailResultBean;
import com.baza.android.bzw.businesscontroller.account.AddEmailSyncAccountActivity;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IEmailSyncedListView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.dao.EmailDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/27.
 * Title：
 * Note：
 */

public class EmailSyncedListPresenter extends BasePresenter {
    private IEmailSyncedListView mEmailSyncedListView;
    private List<ListSyncEmailBean> mEmailList = new ArrayList<>();
    private String[] mMenuItem;

    public EmailSyncedListPresenter(IEmailSyncedListView mEmailSyncedListView) {
        this.mEmailSyncedListView = mEmailSyncedListView;
    }

    @Override
    public void initialize() {
        loadSyncEmailList();
    }

    public List<ListSyncEmailBean> getEmailList() {
        return mEmailList;
    }

    public String[] getMenuItem() {
        if (mMenuItem == null) {
            mMenuItem = new String[1];
            mMenuItem[0] = mEmailSyncedListView.callGetResources().getString(R.string.delete);
        }
        return mMenuItem;
    }

    public void deleteEmail(int position) {
        final ListSyncEmailBean listSyncEmailBean = mEmailList.get(position);
        mEmailSyncedListView.callShowProgress(null, true);
        EmailDao.deleteSyncedEmail(listSyncEmailBean.account, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mEmailSyncedListView.callCancelProgress();
                if (success) {
                    mEmailList.remove(listSyncEmailBean);
                    mEmailSyncedListView.callRefreshEmailListView(-1);
                    mEmailSyncedListView.callShowToastMessage(null, R.string.delete_success);
                    return;
                }
                mEmailSyncedListView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void startSyncEmailResume(final ListSyncEmailBean listSyncEmailBean) {
        mEmailSyncedListView.callShowProgress(null, true);
        EmailDao.startSyncEmailResume(listSyncEmailBean.account, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mEmailSyncedListView.callCancelProgress();
                if (success || errorCode == CustomerRequestAssistHandler.NET_REQUEST_EMAIL_PASSWORD_ERROR || errorCode == CustomerRequestAssistHandler.NET_REQUEST_EMAIL_MSG_ERROR) {
                    listSyncEmailBean.status = (success ? ListSyncEmailBean.STATUS_SYNCING : ListSyncEmailBean.STATUS_ERROR);
                    int targetPosition = -1;
                    for (int i = 0, size = mEmailList.size(); i < size; i++) {
                        if (listSyncEmailBean.account.equals(mEmailList.get(i).account)) {
                            targetPosition = i;
                            break;
                        }
                    }
                    mEmailSyncedListView.callRefreshEmailListView(targetPosition);
                    if (!success)
                        mEmailSyncedListView.callShowToastMessage(errorMsg, 0);
                    return;
                }
                mEmailSyncedListView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void loadSyncEmailList() {
        EmailDao.getListSyncEmail(new IDefaultRequestReplyListener<ListSyncEmailResultBean>() {
            @Override
            public void onRequestReply(boolean success, ListSyncEmailResultBean listSyncEmailRequestBean, int errorCode, String errorMsg) {
                mEmailSyncedListView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success) {
                    mEmailList.clear();
                    if (listSyncEmailRequestBean.data != null && !listSyncEmailRequestBean.data.isEmpty())
                        mEmailList.addAll(listSyncEmailRequestBean.data);
                    mEmailSyncedListView.callRefreshEmailListView(CommonConst.LIST_POSITION_NONE);
                    if (mEmailList.isEmpty())
                        AddEmailSyncAccountActivity.launch(mEmailSyncedListView.callGetBindActivity(), RequestCodeConst.INT_REQUEST_ADD_SYNC_EMAIL_ACCOUNT, null);
                }
            }
        });
    }
}
