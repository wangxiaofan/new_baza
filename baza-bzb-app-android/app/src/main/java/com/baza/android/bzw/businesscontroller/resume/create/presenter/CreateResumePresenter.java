package com.baza.android.bzw.businesscontroller.resume.create.presenter;

import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.resume.CreateResumeResultBean;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.resume.create.viewinterface.ICreateResumeView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.logger.ResumeDetailLogger;
import com.bznet.android.rcbox.R;
import com.slib.utils.AppUtil;

import java.util.HashMap;

/**
 * Created by Vincent.Lei on 2017/5/31.
 * Title：
 * Note：
 */

public class CreateResumePresenter extends BasePresenter {
    private ICreateResumeView mCreateResumeView;
    private ResumeBean mResumeBeanBeEdit, mResumeNewDataHolder;
    private String[] mOptionsSex, mOptionsDegree, mOptionsWorkYears;
    private HashMap<String, String> mParam;
    private boolean isFirm;
    private ResumeDetailLogger resumeDetailLogger = new ResumeDetailLogger();

    public CreateResumePresenter(ICreateResumeView mCreateResumeView, Intent intent) {
        this.mCreateResumeView = mCreateResumeView;
        this.mResumeBeanBeEdit = (ResumeBean) intent.getSerializableExtra("candidateBeEdited");
        this.isFirm = intent.getBooleanExtra("isFirm", false);
        resumeDetailLogger.setPageCode(mCreateResumeView.callGetBindActivity(), intent.getStringExtra("pageCode"));
    }

    @Override
    public void initialize() {
        Resources mResources = mCreateResumeView.callGetResources();
        mOptionsSex = mResources.getStringArray(R.array.new_candidate_sex_options);
        mOptionsDegree = mResources.getStringArray(R.array.degree_level);
        mOptionsWorkYears = new String[22];
        for (int i = 0; i < 21; i++)
            mOptionsWorkYears[i] = String.valueOf(i);
        mOptionsWorkYears[21] = mResources.getString(R.string.more_than_twenty_years);
        mResumeNewDataHolder = new ResumeBean();
        mResumeNewDataHolder.copyFromOld(mResumeBeanBeEdit);
        mCreateResumeView.callSetUpItemViews();

        mCreateResumeView.callSetMode(mResumeBeanBeEdit != null);
    }

    public ResumeBean getBeEditedData() {
        return mResumeBeanBeEdit;
    }

    public void setDegree(int degree) {
        mResumeNewDataHolder.degree = degree;
    }

    public void setSex(int sex) {
        mResumeNewDataHolder.gender = sex;
    }

    public void setCity(int provinceCode, int cityCode) {
        mResumeNewDataHolder.location = cityCode;
//        mResumeNewDataHolder.province = provinceCode;
    }

    public void setWorkYears(int years) {
        mResumeNewDataHolder.yearExpr = years;
    }

    public String[] getOptionsSex() {
        return mOptionsSex;
    }

    public String[] getOptionsDegree() {
        return mOptionsDegree;
    }

    public String[] getOptionsWorkYears() {
        return mOptionsWorkYears;
    }

    /**
     * 提取界面上输入的数据
     */
    private void scanningInput() {
        mResumeNewDataHolder.realName = mCreateResumeView.callGetName();
        mResumeNewDataHolder.mobile = mCreateResumeView.callGetMobile();
        mResumeNewDataHolder.email = mCreateResumeView.callGetEmail();
        mResumeNewDataHolder.company = mCreateResumeView.callGetCompany();
        mResumeNewDataHolder.title = mCreateResumeView.callGetTitle();
    }

    /**
     * 正常保存
     */
    public void save() {
        scanningInput();
        if (TextUtils.isEmpty(mResumeNewDataHolder.realName) || TextUtils.isEmpty(mResumeNewDataHolder.mobile)) {
            mCreateResumeView.callShowToastMessage(null, R.string.create_new_candidate_name_or_phone_not_right);
            return;
        }

        if (mResumeBeanBeEdit.mobileStatus == 3) {
            if (mResumeNewDataHolder.mobile == null || mResumeNewDataHolder.mobile.length() < 3) {
                mCreateResumeView.callShowToastMessage(null, R.string.input_username_correctly);
                return;
            }
        }
        if (mResumeBeanBeEdit.emailStatus == 3) {
            if (!TextUtils.isEmpty(mResumeNewDataHolder.email) && !AppUtil.checkEmail(mResumeNewDataHolder.email)) {
                mCreateResumeView.callShowToastMessage(null, R.string.email_not_correctly);
                return;
            }
        }
        //拼接参数
        if (mParam == null)
            mParam = new HashMap<>();
        else
            mParam.clear();
        mParam.put("realName", mResumeNewDataHolder.realName);
        mParam.put("gender", String.valueOf(mResumeNewDataHolder.gender));
//        if (mResumeNewDataHolder.province > 0)
//            mParam.put("province", String.valueOf(mResumeNewDataHolder.province));
        if (mResumeNewDataHolder.location > 0)
            mParam.put("location", String.valueOf(mResumeNewDataHolder.location));
        if (mResumeNewDataHolder.degree > 0)
            mParam.put("degree", String.valueOf(mResumeNewDataHolder.degree));
        if (mResumeNewDataHolder.yearExpr > -1)
            mParam.put("yearExpr", String.valueOf(mResumeNewDataHolder.yearExpr));
        if (mResumeNewDataHolder.title != null && mResumeNewDataHolder.title.length() > 0)
            mParam.put("title", mResumeNewDataHolder.title);
        if (mResumeNewDataHolder.company != null && mResumeNewDataHolder.company.length() > 0)
            mParam.put("company", mResumeNewDataHolder.company);

        //未查看联系方式的不上传
        if (mResumeBeanBeEdit.emailStatus == 3 && mResumeNewDataHolder.email != null && mResumeNewDataHolder.email.length() > 0)
            mParam.put("email", mResumeNewDataHolder.email);
        if (mResumeBeanBeEdit.mobileStatus == 3 && mResumeNewDataHolder.mobile != null && mResumeNewDataHolder.mobile.length() > 0)
            mParam.put("mobile", mResumeNewDataHolder.mobile);

        //mResumeBeEdited 不为空表示为修改简历候选人  为空表示新建
        if (mResumeBeanBeEdit != null)
            mParam.put("candidateId", mResumeBeanBeEdit.candidateId);
        mParam.put("isFirm", String.valueOf(isFirm));
        realSave();
        resumeDetailLogger.sendEditFirmResumeLog(mCreateResumeView.callGetBindActivity(), mResumeBeanBeEdit.candidateId,
                mResumeBeanBeEdit.firmId, mResumeBeanBeEdit.owner.unionId, mParam);
    }

    /**
     * 提交要保存的
     */
    private void realSave() {
        mCreateResumeView.callShowProgress(null, true);
        ResumeDao.createOrUpdateResume(mParam, (mResumeBeanBeEdit != null), new IDefaultRequestReplyListener<CreateResumeResultBean>() {
            @Override
            public void onRequestReply(boolean success, CreateResumeResultBean createResumeResultBean, int errorCode, String errorMsg) {
                mCreateResumeView.callCancelProgress();
                if (success) {
                    boolean isCreated = (mResumeBeanBeEdit == null);
                    mCreateResumeView.callShowToastMessage(null, isCreated ? R.string.candidate_create_success : R.string.candidate_update_success);
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, (isCreated ? ActionConst.ACTION_EVENT_CREATED_RESUME : ActionConst.ACTION_EVENT_MODIFY_RESUME), (isCreated ? null : mResumeNewDataHolder), null);
                    mCreateResumeView.callGetBindActivity().finish();
                    return;
                }
                mCreateResumeView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public boolean shouldRemindSave() {
        scanningInput();
        if (mResumeBeanBeEdit == null) {
            if (!TextUtils.isEmpty(mResumeNewDataHolder.realName) || !TextUtils.isEmpty(mResumeNewDataHolder.mobile) || !TextUtils.isEmpty(mResumeNewDataHolder.company) || !TextUtils.isEmpty(mResumeNewDataHolder.title) || !TextUtils.isEmpty(mResumeNewDataHolder.email) || mResumeNewDataHolder.location > 0 || mResumeNewDataHolder.yearExpr > -1)
                return true;
            //自建简历还需检测性别和学历
            if (mResumeNewDataHolder.degree != 0)
                return true;
        }

        if (mResumeBeanBeEdit != null) {
            if (mResumeNewDataHolder.location != mResumeBeanBeEdit.location)
                return true;
            if (mResumeNewDataHolder.yearExpr != mResumeBeanBeEdit.yearExpr)
                return true;
            if (mResumeNewDataHolder.realName != null && mResumeNewDataHolder.realName.length() > 0 && !mResumeNewDataHolder.realName.equals(mResumeBeanBeEdit.realName))
                return true;
            if (mResumeNewDataHolder.mobile != null && mResumeNewDataHolder.mobile.length() > 0 && !mResumeNewDataHolder.mobile.equals(mResumeBeanBeEdit.mobile))
                return true;
            if (mResumeNewDataHolder.company != null && mResumeNewDataHolder.company.length() > 0 && !mResumeNewDataHolder.company.equals(mResumeBeanBeEdit.company))
                return true;
            if (mResumeNewDataHolder.title != null && mResumeNewDataHolder.title.length() > 0 && !mResumeNewDataHolder.title.equals(mResumeBeanBeEdit.title))
                return true;
            if (mResumeNewDataHolder.email != null && mResumeNewDataHolder.email.length() > 0 && !mResumeNewDataHolder.email.equals(mResumeBeanBeEdit.email))
                return true;
            //自建简历还需检测性别和学历
            if (mResumeNewDataHolder.degree != mResumeBeanBeEdit.degree)
                return true;
            return mResumeNewDataHolder.gender != mResumeBeanBeEdit.gender;
        }
        return false;
    }

    public boolean isFirm() {
        return isFirm;
    }
}
