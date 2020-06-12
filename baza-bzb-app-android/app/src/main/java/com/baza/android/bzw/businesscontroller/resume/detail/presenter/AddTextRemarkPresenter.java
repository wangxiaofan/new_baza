package com.baza.android.bzw.businesscontroller.resume.detail.presenter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.resume.CompanyLibInfoOfResumeBean;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resumeelement.RemarkBean;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IAddTextRemarkView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2019/2/11.
 * Title：
 * Note：
 */
public class AddTextRemarkPresenter extends BasePresenter {
    private IAddTextRemarkView mAddTextRemarkView;
    private ResumeBean mResumeBean;
    private RemarkBean oldRemarkBean;

    public AddTextRemarkPresenter(IAddTextRemarkView addTextRemarkView, ResumeBean resumeBean, RemarkBean oldRemarkBean) {
        this.mAddTextRemarkView = addTextRemarkView;
        this.mResumeBean = resumeBean;
        this.oldRemarkBean = oldRemarkBean;
    }

    @Override
    public void initialize() {
        loadCompanyLibInfoOfResume();
    }

    public void submit() {
        final String remark = mAddTextRemarkView.callGetTextRemark();
        final int flag = mAddTextRemarkView.callGetFlag();
        String content = mAddTextRemarkView.callGetContent();
        if (flag == 1) {
            if (TextUtils.isEmpty(remark) || remark.matches("\\s*|t|r|n")) {
                mAddTextRemarkView.callShowToastMessage(null, R.string.no_fill_remark_msg);
                return;
            }
        } else {
            if (TextUtils.isEmpty(content)) {
                mAddTextRemarkView.callShowToastMessage(null, R.string.no_fill_remark_msg);
                return;
            }
        }
        String companyTemp = mAddTextRemarkView.callGetCompany();
        String jobTemp = mAddTextRemarkView.callGetJob();
        final String jobHoping = mAddTextRemarkView.callGetJobHoping();
        final String hirerDes = mAddTextRemarkView.callGetHirerDes();
        final String expectSalary = mAddTextRemarkView.callGetExpectSalary();

        final String company = (TextUtils.isEmpty(companyTemp) || TextUtils.equals(companyTemp, mResumeBean.company)) ? null : companyTemp;
        final String job = (TextUtils.isEmpty(jobTemp) || TextUtils.equals(jobTemp, mResumeBean.title)) ? null : jobTemp;
        mAddTextRemarkView.callShowProgress(null, true);
        ResumeDao.addOrEditRemark(mResumeBean.candidateId, (oldRemarkBean != null ? oldRemarkBean.inquiryId : null), flag == 0 ? content : remark, jobHoping, hirerDes, expectSalary, company, job, flag, new IDefaultRequestReplyListener<RemarkBean>() {
            @Override
            public void onRequestReply(boolean success, RemarkBean remarkBean, int errorCode, String errorMsg) {
                mAddTextRemarkView.callCancelProgress();
                if (success) {
                    if (company != null || job != null) {
                        if (company != null)
                            mResumeBean.company = company;
                        if (job != null)
                            mResumeBean.title = job;
                        UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_MODIFY_RESUME, mResumeBean, null);
                    }
                    if (oldRemarkBean != null) {
                        oldRemarkBean.content = remark;
                        oldRemarkBean.jobHoppingOccasion = jobHoping;
                        oldRemarkBean.employerInfo = hirerDes;
                        oldRemarkBean.expectSalary = expectSalary;
                        remarkBean = oldRemarkBean;
                    } else if (remarkBean != null) {
                        //新增一条文字备注
                        remarkBean.inquiryId = remarkBean.id;
                        remarkBean.updateTime = System.currentTimeMillis();
                        remarkBean.userId = UserInfoManager.getInstance().getUserInfo().userId;
                        remarkBean.isMyCreate = RemarkBean.INQUIRY_CREATE_BY_ME;
                    }
//                    if (mAddTextRemarkView.callGetBindActivity() != null) {
//                        Intent intent = new Intent();
//                        intent.putExtra("remark", remarkBean);
//                        mAddTextRemarkView.callGetBindActivity().setResult(Activity.RESULT_OK, intent);
//                        mAddTextRemarkView.callGetBindActivity().finish();
//                    } else {
                        mAddTextRemarkView.callUpdateRemark(remarkBean);
//                    }
                    return;
                }
                mAddTextRemarkView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    private void loadCompanyLibInfoOfResume() {
        ResumeDao.loadCompanyLibInfoOfResume(mResumeBean.candidateId, new IDefaultRequestReplyListener<CompanyLibInfoOfResumeBean>() {
            @Override
            public void onRequestReply(boolean success, CompanyLibInfoOfResumeBean companyLibInfoOfResumeBean, int errorCode, String errorMsg) {
                if (success && companyLibInfoOfResumeBean != null && companyLibInfoOfResumeBean.canUpdateTitleAndCompany)
                    mAddTextRemarkView.callShowMoreRemarkSelectionView();
            }
        });
    }
}
