package com.baza.android.bzw.businesscontroller.find.updateengine.presenter;

import android.os.Bundle;
import android.text.TextUtils;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.resume.ResumeUpdatedContentResultBean;
import com.baza.android.bzw.bean.resumeelement.EducationBean;
import com.baza.android.bzw.bean.resumeelement.EducationUnion;
import com.baza.android.bzw.bean.resumeelement.IntentionBean;
import com.baza.android.bzw.bean.resumeelement.IntentionUnion;
import com.baza.android.bzw.bean.resumeelement.ProjectExperienceBean;
import com.baza.android.bzw.bean.resumeelement.ProjectUnion;
import com.baza.android.bzw.bean.resumeelement.WorkExperienceBean;
import com.baza.android.bzw.bean.resumeelement.WorkExperienceUnion;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IUpdateContentCardView;
import com.baza.android.bzw.businesscontroller.resume.detail.ResumeTextActivity;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.dao.ResumeUpdateDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.AddressManager;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/11/7.
 * Title：
 * Note：
 */

public class UpdateContentCardPresenter extends BasePresenter {
    private static final int ALREADY_CALCULATE_INTENTION = 1;
    private static final int ALREADY_CALCULATE_WORK_EXPERIENCE = 1 << 1;
    private static final int ALREADY_CALCULATE_EDUCATION = 1 << 2;
    private static final int ALREADY_CALCULATE_MAIN_INFO = 1 << 3;
    private static final int ALREADY_CALCULATE_PROJECT = 1 << 4;

    private IUpdateContentCardView mUpdateContentCardView;
    private IntentionUnion mIntentionUnion;
    private List<WorkExperienceUnion> mWorkExperienceUnions;
    private List<ProjectUnion> mProjectExperienceUnions;
    private List<EducationUnion> mEducationUnions;
    private ArrayList<Integer> mListEnableUpdate;
    private String mResumeId;
    private ResumeUpdatedContentResultBean.Data mEnableUpdateContent;
    private boolean mJustShowEnableUpdateContentMode;
    private boolean mHasInit;
    private boolean mHasLoadData;
    private int mCalculateStatus;
    private boolean mHasUpdateContent;

    public UpdateContentCardPresenter(IUpdateContentCardView updateContentCardView, Bundle bundle) {
        this.mUpdateContentCardView = updateContentCardView;
        if (bundle != null) {
            this.mResumeId = bundle.getString("candidateId");
            this.mJustShowEnableUpdateContentMode = bundle.getBoolean("justShowEnableContent");

        }
    }

    @Override
    public void initialize() {
        if (!mHasInit) {
            loadResumeUpdateContent();
            mHasInit = true;
        }
    }

    public boolean isJustShowEnableUpdateContentMode() {
        return mJustShowEnableUpdateContentMode;
    }

    public void setJustShowEnableUpdateContentMode(boolean justShowEnableUpdateContent) {
        mJustShowEnableUpdateContentMode = justShowEnableUpdateContent;
        setContent();
    }

    public ResumeUpdatedContentResultBean.Data getEnableUpdateContentData() {
        return mEnableUpdateContent;
    }

    public boolean isAlreadyLoadData() {
        return mHasLoadData;
    }

    public void loadResumeUpdateContent() {
        ResumeUpdateDao.loadResumeUpdateContent(mResumeId, new IDefaultRequestReplyListener<ResumeUpdatedContentResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, ResumeUpdatedContentResultBean.Data data, int errorCode, String errorMsg) {
                mUpdateContentCardView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success && data != null) {
                    mEnableUpdateContent = data;
                    mHasLoadData = true;
                    setContent();
                }
            }
        });
    }

    private void setContent() {
        if (!mHasLoadData)
            return;
        if (mEnableUpdateContent == null || mEnableUpdateContent.current == null || mEnableUpdateContent.target == null) {
            mUpdateContentCardView.callSetSubmitUpdateView(mHasUpdateContent);
            mUpdateContentCardView.callSetOnUpdatingView();
            return;
        }
        mHasUpdateContent = (mHasUpdateContent || !TextUtils.isEmpty(mEnableUpdateContent.target.selfEvaluation) && !mEnableUpdateContent.target.selfEvaluation.equals(mEnableUpdateContent.current.selfEvaluation));
        if (mListEnableUpdate == null && (mCalculateStatus & ALREADY_CALCULATE_MAIN_INFO) == 0) {
            mListEnableUpdate = calculateUpdatedMainInfo();
            mHasUpdateContent = (mHasUpdateContent || !mListEnableUpdate.isEmpty());
            mCalculateStatus |= ALREADY_CALCULATE_MAIN_INFO;
        }
        if (mIntentionUnion == null && (mCalculateStatus & ALREADY_CALCULATE_INTENTION) == 0) {
            mIntentionUnion = calculateIntentionUpdate();
            mHasUpdateContent = (mHasUpdateContent || mIntentionUnion != null);
            mCalculateStatus |= ALREADY_CALCULATE_INTENTION;
        }
        if (mEducationUnions == null && (mCalculateStatus & ALREADY_CALCULATE_EDUCATION) == 0) {
            mEducationUnions = calculateEducationUpdate();
            mCalculateStatus |= ALREADY_CALCULATE_EDUCATION;
        }
        if (mWorkExperienceUnions == null && (mCalculateStatus & ALREADY_CALCULATE_WORK_EXPERIENCE) == 0) {
            mWorkExperienceUnions = calculateWorkExperienceUpdate();
            mCalculateStatus |= ALREADY_CALCULATE_WORK_EXPERIENCE;
        }
        if (mProjectExperienceUnions == null && (mCalculateStatus & ALREADY_CALCULATE_PROJECT) == 0) {
            mProjectExperienceUnions = calculateProjectUpdate();
            mCalculateStatus |= ALREADY_CALCULATE_PROJECT;
        }

        mUpdateContentCardView.callSetSubmitUpdateView(mHasUpdateContent);
        if (!mHasUpdateContent) {
            mUpdateContentCardView.callSetOnUpdatingView();
            return;
        }
        mUpdateContentCardView.callSetTopView(mListEnableUpdate);
        mUpdateContentCardView.callSetSelfEvaluationView();
        mUpdateContentCardView.callSetIntentionsView(mIntentionUnion);
        mUpdateContentCardView.callSetEducationView(mEducationUnions);
        mUpdateContentCardView.callSetWorkExperienceView(mWorkExperienceUnions);
        mUpdateContentCardView.callSetProjectExperienceView(mProjectExperienceUnions);
        mUpdateContentCardView.callSetResumeTextView();
    }

    private List<WorkExperienceUnion> calculateWorkExperienceUpdate() {
        int currentCount = (mEnableUpdateContent.current.workList == null ? 0 : mEnableUpdateContent.current.workList.size());
        List<WorkExperienceBean> temp = null;
        if (mEnableUpdateContent.target.workList != null && !mEnableUpdateContent.target.workList.isEmpty()) {
            temp = new ArrayList<>(mEnableUpdateContent.target.workList.size());
            temp.addAll(mEnableUpdateContent.target.workList);
        }
        if (currentCount == 0 && temp == null)
            return null;
        List<WorkExperienceUnion> unionList;
        if (currentCount == 0) {
            //全部是新增
            mHasUpdateContent = true;
            unionList = new ArrayList<>(temp.size());
            for (int i = 0, size = temp.size(); i < size; i++) {
                unionList.add(new WorkExperienceUnion(null, temp.get(i)));
            }
            return unionList;
        }
        if (temp == null) {
            //全部是不可更新的
            unionList = new ArrayList<>(currentCount);
            for (int i = 0, size = mEnableUpdateContent.current.workList.size(); i < size; i++) {
                unionList.add(new WorkExperienceUnion(mEnableUpdateContent.current.workList.get(i), null));
            }
            return unionList;
        }
        int targetCount;
        int indexTarget;
        WorkExperienceBean workExperienceCurrent;
        WorkExperienceBean workExperienceTarget;
        unionList = new ArrayList<>();
        for (int i = 0; i < currentCount; i++) {
            workExperienceCurrent = mEnableUpdateContent.current.workList.get(i);
            if (TextUtils.isEmpty(workExperienceCurrent.id))
                continue;
            for (indexTarget = 0, targetCount = temp.size(); indexTarget < targetCount; indexTarget++) {
                workExperienceTarget = temp.get(indexTarget);
                if (workExperienceCurrent.id.equals(workExperienceTarget.id)) {
                    mHasUpdateContent = true;
                    unionList.add(new WorkExperienceUnion(workExperienceCurrent, workExperienceTarget));
                    temp.remove(indexTarget);
                    workExperienceCurrent = null;
                    break;
                }
            }
            if (workExperienceCurrent != null)
                unionList.add(new WorkExperienceUnion(workExperienceCurrent, null));
        }
        if (!temp.isEmpty()) {
            mHasUpdateContent = true;
            for (int i = 0, size = temp.size(); i < size; i++) {
                unionList.add(new WorkExperienceUnion(null, temp.get(i)));
            }
        }
        return unionList;
    }

    private List<EducationUnion> calculateEducationUpdate() {
        int currentCount = (mEnableUpdateContent.current.eduList == null ? 0 : mEnableUpdateContent.current.eduList.size());
        List<EducationBean> temp = null;
        if (mEnableUpdateContent.target.eduList != null && !mEnableUpdateContent.target.eduList.isEmpty()) {
            temp = new ArrayList<>(mEnableUpdateContent.target.eduList.size());
            temp.addAll(mEnableUpdateContent.target.eduList);
        }
        if (currentCount == 0 && temp == null)
            return null;
        List<EducationUnion> unionList;
        if (currentCount == 0) {
            //全部是新增
            mHasUpdateContent = true;
            unionList = new ArrayList<>(temp.size());
            for (int i = 0, size = temp.size(); i < size; i++) {
                unionList.add(new EducationUnion(null, temp.get(i)));
            }
            return unionList;
        }
        if (temp == null) {
            //没有可更新的
            unionList = new ArrayList<>(currentCount);
            for (int i = 0, size = mEnableUpdateContent.current.eduList.size(); i < size; i++) {
                unionList.add(new EducationUnion(mEnableUpdateContent.current.eduList.get(i), null));
            }
            return unionList;
        }
        int targetCount;
        int indexTarget;
        EducationBean educationCurrent;
        EducationBean educationTarget;
        unionList = new ArrayList<>();
        for (int i = 0; i < currentCount; i++) {
            educationCurrent = mEnableUpdateContent.current.eduList.get(i);
            if (TextUtils.isEmpty(educationCurrent.id))
                continue;
            for (indexTarget = 0, targetCount = temp.size(); indexTarget < targetCount; indexTarget++) {
                educationTarget = temp.get(indexTarget);
                if (educationCurrent.id.equals(educationTarget.id)) {
                    mHasUpdateContent = true;
                    unionList.add(new EducationUnion(educationCurrent, educationTarget));
                    temp.remove(indexTarget);
                    educationCurrent = null;
                    break;
                }
            }
            if (educationCurrent != null)
                unionList.add(new EducationUnion(educationCurrent, null));
        }
        if (!temp.isEmpty()) {
            mHasUpdateContent = true;
            for (int i = 0, size = temp.size(); i < size; i++) {
                unionList.add(new EducationUnion(null, temp.get(i)));
            }
        }
        return unionList;
    }


    private List<ProjectUnion> calculateProjectUpdate() {
        int currentCount = (mEnableUpdateContent.current.projectExperienceList == null ? 0 : mEnableUpdateContent.current.projectExperienceList.size());
        List<ProjectExperienceBean> temp = null;
        if (mEnableUpdateContent.target.projectExperienceList != null && !mEnableUpdateContent.target.projectExperienceList.isEmpty()) {
            temp = new ArrayList<>(mEnableUpdateContent.target.projectExperienceList.size());
            temp.addAll(mEnableUpdateContent.target.projectExperienceList);
        }
        if (currentCount == 0 && temp == null)
            return null;
        List<ProjectUnion> unionList;
        if (currentCount == 0) {
            //全部是新增
            mHasUpdateContent = true;
            unionList = new ArrayList<>(temp.size());
            for (int i = 0, size = temp.size(); i < size; i++) {
                unionList.add(new ProjectUnion(null, temp.get(i)));
            }
            return unionList;
        }
        if (temp == null) {
            //全部是不可更新的
            unionList = new ArrayList<>(currentCount);
            for (int i = 0, size = mEnableUpdateContent.current.projectExperienceList.size(); i < size; i++) {
                unionList.add(new ProjectUnion(mEnableUpdateContent.current.projectExperienceList.get(i), null));
            }
            return unionList;
        }
        int targetCount;
        int indexTarget;
        ProjectExperienceBean projectExperienceCurrent;
        ProjectExperienceBean projectExperienceTarget;
        unionList = new ArrayList<>();
        for (int i = 0; i < currentCount; i++) {
            projectExperienceCurrent = mEnableUpdateContent.current.projectExperienceList.get(i);
            if (TextUtils.isEmpty(projectExperienceCurrent.id))
                continue;
            for (indexTarget = 0, targetCount = temp.size(); indexTarget < targetCount; indexTarget++) {
                projectExperienceTarget = temp.get(indexTarget);
                if (projectExperienceCurrent.id.equals(projectExperienceTarget.id)) {
                    mHasUpdateContent = true;
                    unionList.add(new ProjectUnion(projectExperienceCurrent, projectExperienceTarget));
                    temp.remove(indexTarget);
                    projectExperienceCurrent = null;
                    break;
                }
            }
            if (projectExperienceCurrent != null)
                unionList.add(new ProjectUnion(projectExperienceCurrent, null));
        }
        if (!temp.isEmpty()) {
            mHasUpdateContent = true;
            for (int i = 0, size = temp.size(); i < size; i++) {
                unionList.add(new ProjectUnion(null, temp.get(i)));
            }
        }
        return unionList;
    }


    private IntentionUnion calculateIntentionUpdate() {
        if (mEnableUpdateContent.target.intentions == null || mEnableUpdateContent.target.intentions.isEmpty())
            return null;
        IntentionUnion intentionUnion = new IntentionUnion();
        HashSet<String> jobSet = new HashSet<>();
        HashSet<String> citySet = new HashSet<>();
        if (mEnableUpdateContent.current.intentions != null && !mEnableUpdateContent.current.intentions.isEmpty())
            mergeIntentionValue(mEnableUpdateContent.current.intentions, intentionUnion, jobSet, citySet);
        mergeIntentionValue(mEnableUpdateContent.target.intentions, intentionUnion, jobSet, citySet);
        StringBuilder sb = new StringBuilder();
        if (!jobSet.isEmpty()) {
            Iterator<String> iterator = jobSet.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next()).append("、");
            }
            if (sb.length() > 0)
                sb.deleteCharAt(sb.length() - 1);
            intentionUnion.title = sb.toString();
        }
        if (sb.length() > 0)
            sb.delete(0, sb.length());
        if (!citySet.isEmpty()) {
            Iterator<String> iterator = citySet.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next()).append("、");
            }
            if (sb.length() > 0)
                sb.deleteCharAt(sb.length() - 1);
            intentionUnion.city = sb.toString();
        }
        return intentionUnion;
    }

    private void mergeIntentionValue(List<IntentionBean> intentions, IntentionUnion intentionUnion, HashSet<String> jobSet, HashSet<String> citySet) {
        IntentionBean bean;
        for (int i = 0, size = intentions.size(); i < size; i++) {
            bean = mEnableUpdateContent.target.intentions.get(i);
            if (!TextUtils.isEmpty(bean.title))
                jobSet.add(bean.title);
            if (bean.locationId > 0)
                citySet.add(AddressManager.getInstance().getCityNameByCode(bean.locationId));
            if (bean.maxSalary > intentionUnion.maxSalary) {
                intentionUnion.maxSalary = bean.maxSalary;
                intentionUnion.minSalary = bean.minSalary;
            }
        }
    }

    private ArrayList<Integer> calculateUpdatedMainInfo() {
        ArrayList<Integer> listEnableUpdate = new ArrayList<>(10);
        if (!TextUtils.isEmpty(mEnableUpdateContent.target.realName) && !mEnableUpdateContent.target.realName.equals(mEnableUpdateContent.current.realName))
            listEnableUpdate.add(R.id.set_item_id_name);
        if (!TextUtils.isEmpty(mEnableUpdateContent.target.mobile) && !mEnableUpdateContent.target.mobile.equals(mEnableUpdateContent.current.mobile))
            listEnableUpdate.add(R.id.set_item_id_contact);
        if (!TextUtils.isEmpty(mEnableUpdateContent.target.title) && !mEnableUpdateContent.target.title.equals(mEnableUpdateContent.current.title))
            listEnableUpdate.add(R.id.set_item_id_job);
        if (!TextUtils.isEmpty(mEnableUpdateContent.target.email) && !mEnableUpdateContent.target.email.equals(mEnableUpdateContent.current.email))
            listEnableUpdate.add(R.id.set_item_id_email);
        if (!TextUtils.isEmpty(mEnableUpdateContent.target.company) && !mEnableUpdateContent.target.company.equals(mEnableUpdateContent.current.company))
            listEnableUpdate.add(R.id.set_item_id_company);
        if (!TextUtils.isEmpty(mEnableUpdateContent.target.school) && !mEnableUpdateContent.target.school.equals(mEnableUpdateContent.current.school))
            listEnableUpdate.add(R.id.set_item_id_school);
        if (!TextUtils.isEmpty(mEnableUpdateContent.target.major) && !mEnableUpdateContent.target.major.equals(mEnableUpdateContent.current.major))
            listEnableUpdate.add(R.id.set_item_id_major);
        if (mEnableUpdateContent.target.location > 0 && mEnableUpdateContent.target.location != mEnableUpdateContent.current.location)
            listEnableUpdate.add(R.id.set_item_id_city);
        if (mEnableUpdateContent.target.degree > 0 && mEnableUpdateContent.target.degree != mEnableUpdateContent.current.degree)
            listEnableUpdate.add(R.id.set_item_id_degree);
        if (mEnableUpdateContent.target.gender > 0 && mEnableUpdateContent.target.gender != mEnableUpdateContent.current.gender)
            listEnableUpdate.add(R.id.set_item_id_sex);
        if (mEnableUpdateContent.target.huKou > 0 && mEnableUpdateContent.target.huKou != mEnableUpdateContent.current.huKou)
            listEnableUpdate.add(R.id.set_item_id_hukou);
        if (mEnableUpdateContent.target.marriage > 0 && mEnableUpdateContent.target.marriage != mEnableUpdateContent.current.marriage)
            listEnableUpdate.add(R.id.set_item_id_marriage);
        if (Long.parseLong(mEnableUpdateContent.target.birthday) > 0 && !mEnableUpdateContent.target.birthday.equals(mEnableUpdateContent.current.birthday))
            listEnableUpdate.add(R.id.set_item_id_birthday);
        if (mEnableUpdateContent.target.yearExpr > 0 && mEnableUpdateContent.target.yearExpr != mEnableUpdateContent.current.yearExpr)
            listEnableUpdate.add(R.id.set_item_id_workYear);
        return listEnableUpdate;
    }

    protected BZWApplication getApplication() {
        return mUpdateContentCardView.callGetApplication();
    }

    public void watchResumeText() {
        if (mEnableUpdateContent == null || mEnableUpdateContent.current == null)
            return;
        if (TextUtils.isEmpty(mEnableUpdateContent.content)) {
            mUpdateContentCardView.callShowToastMessage(null, R.string.get_resume_text_failed);
            return;
        }
        if (!mEnableUpdateContent.extraHasLineFeed) {
            LogUtil.d("line feed resume text");
            mEnableUpdateContent.content = ResumeDao.lineFeedResumeText(mEnableUpdateContent.content);
            mEnableUpdateContent.extraHasLineFeed = true;
        }
        BZWApplication.getApplication().cacheTransformData(CommonConst.STR_TRANSFORM_RESUME_TEXT, mEnableUpdateContent.content);
        ResumeTextActivity.launch(mUpdateContentCardView.callGetBindActivity(), getApplication().getResources().getString(R.string.candidate_detail_title, mEnableUpdateContent.current.realName), mEnableUpdateContent.fileName, mEnableUpdateContent.fileUrl);
    }
}
