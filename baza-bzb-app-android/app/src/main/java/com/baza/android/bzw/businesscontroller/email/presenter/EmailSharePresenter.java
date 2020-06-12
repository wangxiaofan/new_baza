package com.baza.android.bzw.businesscontroller.email.presenter;

import android.content.Intent;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.email.AddresseeBean;
import com.baza.android.bzw.businesscontroller.email.viewinterface.IEmailShareView;
import com.baza.android.bzw.dao.EmailDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.bznet.android.rcbox.R;
import com.slib.storage.database.listener.IDBReplyListener;
import com.slib.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/31.
 * Title：
 * Note：
 */

public class EmailSharePresenter extends BasePresenter {
    private IEmailShareView mEmailShareView;
    private boolean mIsShareContact, mIsShareRemark;
    private String mResumeId;
    private int mMaxAddresseeCount = 10;
    private int mMaxPreviousCount = 10;
    private boolean mHasOriginalFile;
    private ArrayList<AddresseeBean> mAddresseeList = new ArrayList<>();
    private List<AddresseeBean> mPreviousList;

    public EmailSharePresenter(IEmailShareView mEmailShareView, Intent intent) {
        this.mEmailShareView = mEmailShareView;
        this.mIsShareContact = intent.getBooleanExtra("isShareContact", false);
        this.mIsShareRemark = intent.getBooleanExtra("isShareRemark", false);
        this.mResumeId = intent.getStringExtra("candidateId");
        this.mHasOriginalFile = intent.getBooleanExtra("hasOriginalFile", true);
    }

    @Override
    public void initialize() {
        if (!mIsShareContact || !mHasOriginalFile) {
            //隐藏联系人或者没有附件,那么简历附件是不能分享的
            mEmailShareView.callHideAttachmentShareSelection();
        }
        readLocalEmail();
    }

    @Override
    public void onDestroy() {
        //保存输入过的邮箱
        EmailDao.saveToLocal(mAddresseeList);
    }

    public ArrayList<AddresseeBean> getAddresseeList() {
        return mAddresseeList;
    }

    public List<AddresseeBean> getPreviousList() {
        return mPreviousList;
    }

    private void readLocalEmail() {
        EmailDao.readLocalEmails(new IDBReplyListener<List<AddresseeBean>>() {
            @Override
            public void onDBReply(List<AddresseeBean> list) {
                if (list != null) {
                    mPreviousList = list;
                    mEmailShareView.callSetPreviousAddresseeView(mMaxPreviousCount);
                    mEmailShareView.callSetAddresseeHint();
                }
            }
        });
    }

    public void deleteTargetAssignedAddressee(int position) {
        if (position >= 0 && position < mAddresseeList.size()) {
            mAddresseeList.remove(position);
            mEmailShareView.callResetAddresseeView(mAddresseeList.size() < mMaxAddresseeCount);
        }
    }

    public boolean addNewAddressee(String addressee, AddresseeBean addresseeBean) {
        if (addresseeBean != null)
            addressee = addresseeBean.email;
        if (mAddresseeList.size() >= mMaxAddresseeCount) {
            mEmailShareView.callShowToastMessage(mEmailShareView.callGetResources().getString(R.string.email_share_addressee_count_limit, String.valueOf(mMaxAddresseeCount)), 0);
            return false;
        }
        if (!AppUtil.checkEmail(addressee)) {
            mEmailShareView.callShowToastMessage(null, R.string.input_valuable_addressee);
            return false;
        }
        boolean couldAdd = true;
        for (int i = 0, size = mAddresseeList.size(); i < size; i++) {
            if (addressee.equals(mAddresseeList.get(i).email)) {
                couldAdd = false;
                break;
            }
        }
        if (!couldAdd) {
            mEmailShareView.callShowToastMessage(null, R.string.addressee_has_add);
            return false;
        }
        if (addresseeBean == null) {
            addresseeBean = new AddresseeBean();
            addresseeBean.email = addressee;
            addresseeBean.createTime = System.currentTimeMillis();
        }
        mAddresseeList.add(addresseeBean);
        mEmailShareView.callResetAddresseeView(mAddresseeList.size() < mMaxAddresseeCount);
        return true;
    }

    public void sendEmails(final boolean isShareStand, final boolean isShareOriginal, String des) {
        if (mAddresseeList.isEmpty()) {
            mEmailShareView.callShowToastMessage(null, R.string.addressee_not_set);
            return;
        }
        if (!isShareStand && !isShareOriginal) {
            mEmailShareView.callShowToastMessage(null, R.string.please_set_email_share_type);
            return;
        }
        mEmailShareView.callShowProgress(null, true);
        EmailDao.emailShare(mResumeId, mAddresseeList, des, isShareStand, isShareOriginal, mIsShareContact, mIsShareRemark, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mEmailShareView.callCancelProgress();
                if (success) {
                    mEmailShareView.callShowToastMessage(null, R.string.share_success);
                    mEmailShareView.callFinished();
                    return;
                }
                mEmailShareView.callShowToastMessage(errorMsg, 0);
            }
        });
    }
}
