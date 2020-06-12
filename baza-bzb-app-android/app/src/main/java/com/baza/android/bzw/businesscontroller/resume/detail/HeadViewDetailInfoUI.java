package com.baza.android.bzw.businesscontroller.resume.detail;

import android.content.res.Resources;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baza.android.bzw.bean.resume.ResumeDetailBean;
import com.baza.android.bzw.bean.resumeelement.CertificationBean;
import com.baza.android.bzw.bean.resumeelement.EducationBean;
import com.baza.android.bzw.bean.resumeelement.IntentionBean;
import com.baza.android.bzw.bean.resumeelement.IntentionUnion;
import com.baza.android.bzw.bean.resumeelement.LanguageBean;
import com.baza.android.bzw.bean.resumeelement.ProjectExperienceBean;
import com.baza.android.bzw.bean.resumeelement.SkillBean;
import com.baza.android.bzw.bean.resumeelement.TrainingBean;
import com.baza.android.bzw.bean.resumeelement.WorkExperienceBean;
import com.baza.android.bzw.businesscontroller.resume.detail.presenter.ResumeDetailPresenter;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.dao.ResumeCompareDao;
import com.baza.android.bzw.manager.AddressManager;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.baza.android.bzw.widget.FoldTextView;
import com.bznet.android.rcbox.R;
import com.slib.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/5/24.
 * Title：
 * Note：
 */

public class HeadViewDetailInfoUI implements View.OnClickListener {
    private View mHeadView;
    @BindView(R.id.tv_self_des)
    TextView textView_selfDes;
    @BindView(R.id.tv_self_des_old)
    TextView textView_selfDesOld;
    @BindView(R.id.lly_education_container)
    LinearLayout linearLayout_educationContainer;
    @BindView(R.id.lly_work_experience_container)
    LinearLayout linearLayout_workExperienceContainer;
    @BindView(R.id.tv_intentions_title)
    TextView textView_intentionsTitle;
    @BindView(R.id.ll_intentions_container)
    LinearLayout linearLayout_intentionsContainer;
    @BindView(R.id.tv_project_title)
    TextView textView_projectTitle;
    @BindView(R.id.lly_project_experience_container)
    LinearLayout linearLayout_projectContainer;
    @BindView(R.id.tv_self_des_title)
    TextView textView_selfDesTitle;
    @BindView(R.id.tv_evaluation_type_title_current)
    TextView textView_evaluationCurrentTitle;
    @BindView(R.id.tv_evaluation_type_title_before)
    TextView textView_evaluationBeforeTitle;
    @BindView(R.id.ll_self_des_container)
    View textView_evaluationContainer;
    @BindView(R.id.tv_education_title)
    TextView textView_EducationTitle;
    @BindView(R.id.tv_work_experience_title)
    TextView textView_workExperienceTitle;
    @BindView(R.id.tv_self_des_new_tag)
    TextView view_newTagSelfDes;
    @BindView(R.id.tv_intention_new_tag)
    View view_newTagIntention;
    @BindView(R.id.view_line_remark_depart)
    View view_lineRemarkDepart;
    @BindView(R.id.tv_certification_title)
    TextView textView_certificationTitle;
    @BindView(R.id.lly_certification_container)
    LinearLayout linearLayout_certificationContainer;
    @BindView(R.id.tv_language_title)
    TextView textView_languageTitle;
    @BindView(R.id.lly_language_container)
    LinearLayout linearLayout_languageContainer;
    @BindView(R.id.tv_skill_title)
    TextView textView_skillTitle;
    @BindView(R.id.lly_skill_container)
    LinearLayout linearLayout_skillContainer;
    @BindView(R.id.tv_training_title)
    TextView textView_trainingTitle;
    @BindView(R.id.lly_training_container)
    LinearLayout linearLayout_trainingContainer;
    @BindView(R.id.view_line_self_des)
    View view_lineSelfDes;
    @BindView(R.id.view_line_intention)
    View view_lineIntention;
    @BindView(R.id.view_line_education)
    View view_lineEducation;
    @BindView(R.id.view_line_work_experience)
    View view_lineWorkExperience;
    @BindView(R.id.view_line_project_experience)
    View view_lineProjectExperience;
    @BindView(R.id.view_line_training)
    View view_lineTraining;
    @BindView(R.id.view_line_skill)
    View view_lineSkill;
    @BindView(R.id.view_line_language)
    View view_lineLanguage;

    private LayoutInflater mLayoutInflater;
    private IResumeDetailView mResumeDetailView;
    private ResumeDetailPresenter mPresenter;
    private Resources mResources;
    private int defaultMargin;
    private int mColorNormalContentGray;
    private int mColorNormalContentBlack;
    private int mColorBlue;
    private int mColorContentPrevious;
    private boolean isOpen = false;
    private ResumeDetailBean resumeDetailBean;

    public HeadViewDetailInfoUI(IResumeDetailView mResumeDetailView, ResumeDetailPresenter mPresenter) {
        this.mResumeDetailView = mResumeDetailView;
        this.mPresenter = mPresenter;
        mLayoutInflater = mResumeDetailView.callGetBindActivity().getLayoutInflater();
        init();
    }

    private void init() {
        mHeadView = mResumeDetailView.callGetBindActivity().getLayoutInflater().inflate(R.layout.head_view_candidate_detail_info, null);
        mResources = mResumeDetailView.callGetResources();
        mColorNormalContentGray = mResources.getColor(R.color.text_color_black_4E5968);
        mColorNormalContentBlack = mResources.getColor(R.color.text_color_blue_0D315C);
        mColorBlue = mResources.getColor(R.color.text_color_blue_53ABD5);
        mColorContentPrevious = mResources.getColor(R.color.text_color_grey_94A1A5);
        defaultMargin = (int) mResources.getDimension(R.dimen.dp_10);
        ButterKnife.bind(this, mHeadView);
    }

    public View getHeadView() {
        return mHeadView;
    }

    public void setData() {
        resumeDetailBean = mPresenter.getCurrentResumeData();
        if (resumeDetailBean == null)
            return;
        view_lineRemarkDepart.setVisibility(View.VISIBLE);
        setSelfDes(resumeDetailBean);
        setEducation(resumeDetailBean);
        setWorkExperience(resumeDetailBean, isOpen);
        setIntentions(resumeDetailBean);
        setProjectExperience(resumeDetailBean);
        setTraining(resumeDetailBean);
        setSkills(resumeDetailBean);
        setCertification(resumeDetailBean);
        setLanguage(resumeDetailBean);
    }

    void updateViewForUpdateHistory() {
        view_lineRemarkDepart.setVisibility(View.GONE);
    }

    /**
     * 设置培训经历
     */
    private void setTraining(ResumeDetailBean resumeDetailBean) {
        List<TrainingBean> trainingList = resumeDetailBean.trainings;
        if (trainingList == null || trainingList.isEmpty()) {
            textView_trainingTitle.setVisibility(View.GONE);
            view_lineTraining.setVisibility(View.GONE);
            linearLayout_trainingContainer.setVisibility(View.GONE);
            return;
        }

        View view_Item, view_trainingItem;
        ViewGroup view_innerItemContainer;
        ImageView imageView_timeLine;
        int childCurrentCount = linearLayout_trainingContainer.getChildCount();
        int needShowCount = trainingList.size();
        boolean hasCache;
        TextView textView;
        TrainingBean trainingBean;
        LinearLayout.MarginLayoutParams lpTrainingItem;
        ViewGroup.LayoutParams lpp_timeLine;
        for (int i = 0; i < needShowCount; i++) {
            trainingBean = trainingList.get(i);
            hasCache = i < childCurrentCount;
            view_Item = (hasCache ? linearLayout_trainingContainer.getChildAt(i) : mLayoutInflater.inflate(R.layout.layout_resume_element_container_default_parent, null));
            view_innerItemContainer = view_Item.findViewById(R.id.view_item_container);
            view_trainingItem = view_innerItemContainer.getChildAt(0);
            if (view_trainingItem == null) {
                view_trainingItem = mLayoutInflater.inflate(R.layout.resume_element_training, null);
                view_innerItemContainer.addView(view_trainingItem);
            }
            textView = view_trainingItem.findViewById(R.id.tv_training_time);
            textView.setText(FriendlyShowInfoManager.getInstance().formatTBDTime_YM(trainingBean.startDate) + "-" + FriendlyShowInfoManager.getInstance().formatTBDTime_YM(trainingBean.endDate));
            textView = view_trainingItem.findViewById(R.id.tv_training_organization);
            textView.setText(trainingBean.organizationName);
            textView = view_trainingItem.findViewById(R.id.tv_training_location_and_certification);
            boolean isCityOk = AddressManager.getInstance().isCityCodeEnable(trainingBean.locationId);
            textView.setText((isCityOk ? AddressManager.getInstance().getCityNameByCode((trainingBean.locationId)) : mResources.getString(R.string.training_location_unknown)) + "/" + (!TextUtils.isEmpty(trainingBean.certifications) ? trainingBean.certifications : mResources.getString(R.string.training_certification_unknown)));
            textView = view_Item.findViewById(R.id.tv_training_course);
            SpannableString spannableString = new SpannableString(mResources.getString(R.string.training_course_value, !TextUtils.isEmpty(trainingBean.courseName) ? trainingBean.courseName : mResources.getString(R.string.training_course_unknown)));
            spannableString.setSpan(new ForegroundColorSpan(mColorNormalContentBlack), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);

            imageView_timeLine = view_Item.findViewById(R.id.iv_time_line);
            lpp_timeLine = imageView_timeLine.getLayoutParams();
            lpTrainingItem = (LinearLayout.MarginLayoutParams) view_trainingItem.getLayoutParams();
            if (lpTrainingItem == null)
                lpTrainingItem = new LinearLayout.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lpTrainingItem.leftMargin = defaultMargin;

            if (i == needShowCount - 1) {
                lpTrainingItem.bottomMargin = 0;
                lpp_timeLine.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                imageView_timeLine.setImageResource(R.drawable.time_line_single);
            } else {
                lpTrainingItem.bottomMargin = defaultMargin;
                lpp_timeLine.height = ViewGroup.LayoutParams.MATCH_PARENT;
                imageView_timeLine.setImageResource(R.drawable.time_line_normal);
            }
            imageView_timeLine.setLayoutParams(lpp_timeLine);
            view_trainingItem.setLayoutParams(lpTrainingItem);
            view_Item.setVisibility(View.VISIBLE);
            if (!hasCache)
                linearLayout_trainingContainer.addView(view_Item);
        }

        if (childCurrentCount > needShowCount) {
            for (; needShowCount < childCurrentCount; needShowCount++)
                linearLayout_trainingContainer.getChildAt(needShowCount).setVisibility(View.GONE);
        }
        textView_trainingTitle.setVisibility(View.VISIBLE);
        view_lineTraining.setVisibility(View.VISIBLE);
        linearLayout_trainingContainer.setVisibility(View.VISIBLE);
    }

    /**
     * 设置技能
     */
    private void setSkills(ResumeDetailBean resumeDetailBean) {
        List<SkillBean> skillList = resumeDetailBean.skills;
        if (skillList == null || skillList.isEmpty()) {
            textView_skillTitle.setVisibility(View.GONE);
            view_lineSkill.setVisibility(View.GONE);
            linearLayout_skillContainer.setVisibility(View.GONE);
            return;
        }
        View view_Item;
        int childCurrentCount = linearLayout_skillContainer.getChildCount();
        int needShowCount = skillList.size();
        boolean hasCache;
        TextView textView;
        ProgressBar progressBar;
        SkillBean skillBean;
        for (int i = 0; i < needShowCount; i++) {
            skillBean = skillList.get(i);
            hasCache = i < childCurrentCount;
            view_Item = (hasCache ? linearLayout_skillContainer.getChildAt(i) : mLayoutInflater.inflate(R.layout.resume_element_skill, null));
            textView = view_Item.findViewById(R.id.tv_skill_name);
            textView.setText(skillBean.skill);
            textView = view_Item.findViewById(R.id.tv_used_year);
            textView.setText(mResources.getString(R.string.work_year_value, String.valueOf(skillBean.userdYears)));
            textView = view_Item.findViewById(R.id.tv_level_name);
            textView.setText(FriendlyShowInfoManager.getInstance().getSkillLevelById(skillBean.level));
            progressBar = view_Item.findViewById(R.id.progressBar);
            progressBar.setProgress(FriendlyShowInfoManager.getInstance().getSkillLevelScoreById(skillBean.level));
            view_Item.setVisibility(View.VISIBLE);
            if (!hasCache)
                linearLayout_skillContainer.addView(view_Item);
        }

        if (childCurrentCount > needShowCount) {
            for (; needShowCount < childCurrentCount; needShowCount++)
                linearLayout_skillContainer.getChildAt(needShowCount).setVisibility(View.GONE);
        }
        textView_skillTitle.setVisibility(View.VISIBLE);
        view_lineSkill.setVisibility(View.VISIBLE);
        linearLayout_skillContainer.setVisibility(View.VISIBLE);
    }

    /**
     * 设置语言
     */
    private void setLanguage(ResumeDetailBean resumeDetailBean) {
        List<LanguageBean> languageList = null;
        if (resumeDetailBean.languages != null && !resumeDetailBean.languages.isEmpty()) {
            languageList = new ArrayList<>(resumeDetailBean.languages.size());
            for (int i = 0, size = resumeDetailBean.languages.size(); i < size; i++) {
                if (FriendlyShowInfoManager.getInstance().isLanguageEnable(resumeDetailBean.languages.get(i).languageId))
                    languageList.add(resumeDetailBean.languages.get(i));
            }
        }
        if (languageList == null || languageList.isEmpty()) {
            textView_languageTitle.setVisibility(View.GONE);
            view_lineLanguage.setVisibility(View.GONE);
            linearLayout_languageContainer.setVisibility(View.GONE);
            return;
        }
        View view_Item;
        int childCurrentCount = linearLayout_languageContainer.getChildCount();
        int needShowCount = languageList.size();
        boolean hasCache;
        TextView textView;
        LanguageBean languageBean;
        for (int i = 0; i < needShowCount; i++) {
            languageBean = languageList.get(i);
            hasCache = i < childCurrentCount;
            view_Item = (hasCache ? linearLayout_languageContainer.getChildAt(i) : mLayoutInflater.inflate(R.layout.resume_element_language, null));
            textView = view_Item.findViewById(R.id.tv_language_name);
            textView.setText(FriendlyShowInfoManager.getInstance().getLanguageNameById(languageBean.languageId));
            textView = view_Item.findViewById(R.id.tv_language_level);
            textView.setText(FriendlyShowInfoManager.getInstance().getLanguageLevelById(languageBean.languageLevel));
            view_Item.setVisibility(View.VISIBLE);
            if (!hasCache)
                linearLayout_languageContainer.addView(view_Item);
        }

        if (childCurrentCount > needShowCount) {
            for (; needShowCount < childCurrentCount; needShowCount++)
                linearLayout_languageContainer.getChildAt(needShowCount).setVisibility(View.GONE);
        }
        textView_languageTitle.setVisibility(View.VISIBLE);
        view_lineLanguage.setVisibility(View.VISIBLE);
        linearLayout_languageContainer.setVisibility(View.VISIBLE);
    }

    /**
     * 设置证书
     */
    private void setCertification(ResumeDetailBean resumeDetailBean) {
        List<CertificationBean> certificationList = resumeDetailBean.certifications;
        if (certificationList == null || certificationList.isEmpty()) {
            textView_certificationTitle.setVisibility(View.GONE);
            linearLayout_certificationContainer.setVisibility(View.GONE);
            return;
        }
        View view_Item;
        int childCurrentCount = linearLayout_certificationContainer.getChildCount();
        int needShowCount = certificationList.size();
        boolean hasCache;
        TextView textView;
        CertificationBean certificationBean;
        for (int i = 0; i < needShowCount; i++) {
            certificationBean = certificationList.get(i);
            hasCache = i < childCurrentCount;
            view_Item = (hasCache ? linearLayout_certificationContainer.getChildAt(i) : mLayoutInflater.inflate(R.layout.resume_element_certifiction, null));
            textView = view_Item.findViewById(R.id.tv_certification_name);
            textView.setText(certificationBean.name);
            textView = view_Item.findViewById(R.id.tv_certification_score);
            textView.setText(mResources.getString(R.string.certification_score_value, certificationBean.score > 0 ? String.valueOf(certificationBean.score) : mResources.getString(R.string.message_unknown)));
            textView = view_Item.findViewById(R.id.tv_certification_time);
            textView.setText(mResources.getString(R.string.certification_time_value, FriendlyShowInfoManager.getInstance().formatTBDTime_YM(certificationBean.obtainedDate)));
            view_Item.setVisibility(View.VISIBLE);
            if (!hasCache)
                linearLayout_certificationContainer.addView(view_Item);
        }

        if (childCurrentCount > needShowCount) {
            for (; needShowCount < childCurrentCount; needShowCount++)
                linearLayout_certificationContainer.getChildAt(needShowCount).setVisibility(View.GONE);
        }
        textView_certificationTitle.setVisibility(View.VISIBLE);
        linearLayout_certificationContainer.setVisibility(View.VISIBLE);
    }

    /**
     * 设置项目经验
     */
    private void setProjectExperience(ResumeDetailBean resumeDetailBean) {
        List<ProjectExperienceBean> projectExperiences = resumeDetailBean.projectExperienceList;
        if (projectExperiences == null || projectExperiences.isEmpty()) {
            textView_projectTitle.setVisibility(View.GONE);
            view_lineProjectExperience.setVisibility(View.GONE);
            linearLayout_projectContainer.setVisibility(View.GONE);
            return;
        }
        int childCurrentCount = linearLayout_projectContainer.getChildCount();
        int needShowCount = projectExperiences.size();
        ProjectExperienceBean projectExperienceBean, projectExperienceBeforeUpdate;
        View view_Item, viewValueCurrent, viewValueBefore;
        ViewGroup view_innerItemContainer;
        ImageView imageView_timeLine;
        ResumeCompareDao compareDao = mPresenter.getResumeCompareDao();
        boolean isHasUpdatedContent;
        boolean hasCache, isNewAdd, isCreateCurrentItem, isCreateBeforeItem;
        LinearLayout.MarginLayoutParams lpCurrentParent, lpBeforeParent;
        ViewGroup.LayoutParams lpp_timeLine;

        for (int i = 0; i < needShowCount; i++) {
            isCreateCurrentItem = false;
            isCreateBeforeItem = false;
            lpBeforeParent = null;
            projectExperienceBean = projectExperiences.get(i);
            projectExperienceBeforeUpdate = (compareDao != null ? (ProjectExperienceBean) compareDao.getOldValue(projectExperienceBean) : null);
            isHasUpdatedContent = (compareDao != null && !projectExperienceBean.equals(projectExperienceBeforeUpdate));
            isNewAdd = (isHasUpdatedContent && projectExperienceBeforeUpdate == null);
            hasCache = i < childCurrentCount;

            //从已有缓存里面拿，避免重复实例化控件  加快UI速度
            view_Item = (hasCache ? linearLayout_projectContainer.getChildAt(i) : mLayoutInflater.inflate(R.layout.layout_resume_element_container_default_parent, null));
            view_innerItemContainer = view_Item.findViewById(R.id.view_item_container);
            viewValueCurrent = view_innerItemContainer.findViewById(R.id.id_current_item);
            if (viewValueCurrent == null) {
                viewValueCurrent = mLayoutInflater.inflate(R.layout.resume_element_project, null);
                viewValueCurrent.setId(R.id.id_current_item);
                isCreateCurrentItem = true;
            }
            setProjectInnerItem(viewValueCurrent, projectExperienceBean, isHasUpdatedContent, isNewAdd, false);

            viewValueBefore = view_innerItemContainer.findViewById(R.id.id_before_item);
            if ((!isHasUpdatedContent || isNewAdd) && viewValueBefore != null)
                viewValueBefore.setVisibility(View.GONE);
            if (isHasUpdatedContent && !isNewAdd) {
                if (viewValueBefore == null) {
                    viewValueBefore = mLayoutInflater.inflate(R.layout.resume_element_project, null);
                    viewValueBefore.setId(R.id.id_before_item);
                    isCreateBeforeItem = true;
                }
                viewValueBefore.setVisibility(View.VISIBLE);
                setProjectInnerItem(viewValueBefore, projectExperienceBeforeUpdate, true, false, true);
            }


            imageView_timeLine = view_Item.findViewById(R.id.iv_time_line);
            lpp_timeLine = imageView_timeLine.getLayoutParams();

            lpCurrentParent = (LinearLayout.LayoutParams) viewValueCurrent.getLayoutParams();
            if (lpCurrentParent == null) {
                lpCurrentParent = new LinearLayout.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lpCurrentParent.leftMargin = defaultMargin;
            }

            if (viewValueBefore != null) {
                lpBeforeParent = (LinearLayout.LayoutParams) viewValueBefore.getLayoutParams();
                if (lpBeforeParent == null)
                    lpBeforeParent = new LinearLayout.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lpBeforeParent.leftMargin = defaultMargin;
                lpBeforeParent.topMargin = defaultMargin / 2;
            }


            if (i == needShowCount - 1) {
                lpCurrentParent.bottomMargin = 0;
                if (lpBeforeParent != null)
                    lpBeforeParent.bottomMargin = 0;
                lpp_timeLine.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                imageView_timeLine.setImageResource(R.drawable.time_line_single);
            } else {
                lpCurrentParent.bottomMargin = defaultMargin;
                if (lpBeforeParent != null)
                    lpBeforeParent.bottomMargin = defaultMargin;
                lpp_timeLine.height = ViewGroup.LayoutParams.MATCH_PARENT;
                imageView_timeLine.setImageResource(R.drawable.time_line_normal);
            }
            imageView_timeLine.setLayoutParams(lpp_timeLine);
            viewValueCurrent.setLayoutParams(lpCurrentParent);
            if (viewValueBefore != null)
                viewValueBefore.setLayoutParams(lpBeforeParent);

            if (isCreateCurrentItem)
                view_innerItemContainer.addView(viewValueCurrent, lpCurrentParent);
            if (isCreateBeforeItem)
                view_innerItemContainer.addView(viewValueBefore, lpBeforeParent);
            view_Item.setVisibility(View.VISIBLE);
            if (!hasCache)
                linearLayout_projectContainer.addView(view_Item);
        }

        //多余的缓存控件要隐藏
        if (childCurrentCount > needShowCount) {
            for (; needShowCount < childCurrentCount; needShowCount++)
                linearLayout_projectContainer.getChildAt(needShowCount).setVisibility(View.GONE);
        }
        textView_projectTitle.setVisibility(View.VISIBLE);
        view_lineProjectExperience.setVisibility(View.VISIBLE);
        linearLayout_projectContainer.setVisibility(View.VISIBLE);
    }

    private void setProjectInnerItem(View viewElement, ProjectExperienceBean projectExperienceBean, boolean isHasUpdatedContent, boolean isNewAdd, boolean isOldData) {
        TextView textView = viewElement.findViewById(R.id.tv_change_type_title);
        if (!isHasUpdatedContent || isNewAdd)
            textView.setVisibility(View.GONE);
        else {
            textView.setText(isOldData ? R.string.update_item_previous : R.string.update_item_to_2);
            textView.setTextColor(isOldData ? mColorContentPrevious : mColorBlue);
            textView.setVisibility(View.VISIBLE);
        }

        textView = viewElement.findViewById(R.id.tv_project_time);
        textView.setText(FriendlyShowInfoManager.getInstance().getWorkExperienceFormattedTime(projectExperienceBean.startDate, projectExperienceBean.endDate));
        textView.setTextColor((!isHasUpdatedContent || isNewAdd || !isOldData) ? mColorNormalContentGray : mColorContentPrevious);

        textView = viewElement.findViewById(R.id.tv_project_name);
        textView.setText(projectExperienceBean.projectName);
        textView.setTextColor((!isHasUpdatedContent || isNewAdd || !isOldData) ? mColorNormalContentBlack : mColorContentPrevious);

        textView = viewElement.findViewById(R.id.tv_project_role);
        textView.setText(mResources.getString(R.string.project_role_value, projectExperienceBean.projectRole));
        textView.setTextColor((!isHasUpdatedContent || isNewAdd || !isOldData) ? mColorNormalContentBlack : mColorContentPrevious);

        textView = viewElement.findViewById(R.id.tv_project_content);
        String content = mResources.getString(R.string.project_content_value, projectExperienceBean.projectDescription);
        if (!isHasUpdatedContent || isNewAdd || !isOldData) {
            textView.setTextColor(mColorNormalContentBlack);
            SpannableString spannableString = new SpannableString(content);
            spannableString.setSpan(new ForegroundColorSpan(mColorNormalContentBlack), 5, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);
        } else {
            textView.setTextColor(mColorContentPrevious);
            textView.setText(content);
        }

        textView = viewElement.findViewById(R.id.tv_project_responsibility);
        content = mResources.getString(R.string.project_responsibility_value, projectExperienceBean.responsibility);
        if (!isHasUpdatedContent || isNewAdd || !isOldData) {
            textView.setTextColor(mColorNormalContentBlack);
            SpannableString spannableString = new SpannableString(content);
            spannableString.setSpan(new ForegroundColorSpan(mColorNormalContentGray), 3, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);
        } else {
            textView.setTextColor(mColorContentPrevious);
            textView.setText(content);
        }

        textView = viewElement.findViewById(R.id.tv_new_tag);
        if (isNewAdd) {
            textView.setText(R.string.new_en);
            textView.setBackgroundResource(R.drawable.list_exchange_function_hint);
            textView.setVisibility(View.VISIBLE);
        } else if (isHasUpdatedContent && !isOldData) {
            textView.setText(R.string.update);
            textView.setBackgroundResource(R.drawable.updated_content_tag_bg);
            textView.setVisibility(View.VISIBLE);
        } else
            textView.setVisibility(View.GONE);
    }

    /**
     * 设置候选人工作经历
     */

    private void setWorkExperience(ResumeDetailBean data, boolean isOpen) {
        if (data.workList == null || data.workList.isEmpty()) {
            textView_workExperienceTitle.setVisibility(View.GONE);
            view_lineWorkExperience.setVisibility(View.GONE);
            linearLayout_workExperienceContainer.setVisibility(View.GONE);
            return;
        }
        int childCurrentCount = linearLayout_workExperienceContainer.getChildCount();
        int needShowCount = data.workList.size();
        View view_Item, viewValueCurrent, viewValueBefore;
        ViewGroup view_innerItemContainer;
        ImageView imageView_timeLine;
        ResumeCompareDao compareDao = mPresenter.getResumeCompareDao();
        WorkExperienceBean workExperienceBean, workExperienceBeforeUpdate;
        boolean isHasUpdatedContent;
        boolean hasCache, isNewAdd, isCreateCurrentItem, isCreateBeforeItem;
        LinearLayout.MarginLayoutParams lpCurrentParent, lpBeforeParent;
        ViewGroup.LayoutParams lpp_timeLine;
        for (int i = 0; i < needShowCount; i++) {
            isCreateCurrentItem = false;
            isCreateBeforeItem = false;
            lpBeforeParent = null;
            workExperienceBean = data.workList.get(i);
            workExperienceBeforeUpdate = (compareDao != null ? (WorkExperienceBean) compareDao.getOldValue(workExperienceBean) : null);
            isHasUpdatedContent = (compareDao != null && !workExperienceBean.equals(workExperienceBeforeUpdate));
            isNewAdd = (isHasUpdatedContent && workExperienceBeforeUpdate == null);
            hasCache = i < childCurrentCount;
            //从已有缓存里面拿，避免重复实例化控件  加快UI速度
            view_Item = (hasCache ? linearLayout_workExperienceContainer.getChildAt(i) : mLayoutInflater.inflate(R.layout.layout_resume_element_container_default_parent, null));
            view_innerItemContainer = view_Item.findViewById(R.id.view_item_container);
            viewValueCurrent = view_innerItemContainer.findViewById(R.id.id_current_item);

            if (viewValueCurrent == null) {
                viewValueCurrent = mLayoutInflater.inflate(R.layout.resume_element_work_experience, null);
                viewValueCurrent.setId(R.id.id_current_item);
                isCreateCurrentItem = true;
            }
            setWorkExperienceInnerItem(viewValueCurrent, workExperienceBean, isHasUpdatedContent, isNewAdd, false, isOpen);

            viewValueBefore = view_innerItemContainer.findViewById(R.id.id_before_item);
            if ((!isHasUpdatedContent || isNewAdd) && viewValueBefore != null)
                viewValueBefore.setVisibility(View.GONE);
            if (isHasUpdatedContent && !isNewAdd) {
                if (viewValueBefore == null) {
                    viewValueBefore = mLayoutInflater.inflate(R.layout.resume_element_work_experience, null);
                    viewValueBefore.setId(R.id.id_before_item);
                    isCreateBeforeItem = true;
                }
                viewValueBefore.setVisibility(View.VISIBLE);
                setWorkExperienceInnerItem(viewValueBefore, workExperienceBeforeUpdate, true, false, true, isOpen);
            }

            imageView_timeLine = view_Item.findViewById(R.id.iv_time_line);
            lpp_timeLine = imageView_timeLine.getLayoutParams();

            lpCurrentParent = (LinearLayout.LayoutParams) viewValueCurrent.getLayoutParams();
            if (lpCurrentParent == null) {
                lpCurrentParent = new LinearLayout.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lpCurrentParent.leftMargin = defaultMargin;
            }

            if (viewValueBefore != null) {
                lpBeforeParent = (LinearLayout.LayoutParams) viewValueBefore.getLayoutParams();
                if (lpBeforeParent == null)
                    lpBeforeParent = new LinearLayout.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lpBeforeParent.leftMargin = defaultMargin;
                lpBeforeParent.topMargin = defaultMargin / 2;
            }


            if (i == needShowCount - 1) {
                lpCurrentParent.bottomMargin = 0;
                if (lpBeforeParent != null)
                    lpBeforeParent.bottomMargin = 0;
                lpp_timeLine.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                imageView_timeLine.setImageResource(R.drawable.time_line_single);
            } else {
                lpCurrentParent.bottomMargin = defaultMargin;
                if (lpBeforeParent != null)
                    lpBeforeParent.bottomMargin = defaultMargin;
                lpp_timeLine.height = ViewGroup.LayoutParams.MATCH_PARENT;
                imageView_timeLine.setImageResource(R.drawable.time_line_normal);
            }
            imageView_timeLine.setLayoutParams(lpp_timeLine);
            viewValueCurrent.setLayoutParams(lpCurrentParent);
            if (viewValueBefore != null)
                viewValueBefore.setLayoutParams(lpBeforeParent);

            if (isCreateCurrentItem)
                view_innerItemContainer.addView(viewValueCurrent, lpCurrentParent);
            if (isCreateBeforeItem)
                view_innerItemContainer.addView(viewValueBefore, lpBeforeParent);
            view_Item.setVisibility(View.VISIBLE);
            if (!hasCache)
                linearLayout_workExperienceContainer.addView(view_Item);
        }
        //多余的缓存控件要隐藏
        if (childCurrentCount > needShowCount) {
            for (; needShowCount < childCurrentCount; needShowCount++)
                linearLayout_workExperienceContainer.getChildAt(needShowCount).setVisibility(View.GONE);
        }

        if (!isOpen) {
            int textSize = ScreenUtil.px2dip(mResources.getDimension(R.dimen.text_size_12));
            TextView textView = new TextView(mResumeDetailView.callGetApplication());
            textView.setTextColor(Color.parseColor("#53ABD5"));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            textView.setText("展开");
            textView.setGravity(Gravity.CENTER);
            textView.setOnClickListener(this);
            linearLayout_workExperienceContainer.addView(textView);
        }

        view_lineWorkExperience.setVisibility(View.VISIBLE);
        textView_workExperienceTitle.setVisibility(View.VISIBLE);
        linearLayout_workExperienceContainer.setVisibility(View.VISIBLE);
    }

    private void setWorkExperienceInnerItem(View viewElement, WorkExperienceBean workExperienceBean, boolean isHasUpdatedContent,
                                            boolean isNewAdd, boolean isOldData, boolean isOpen) {
        TextView textView = viewElement.findViewById(R.id.tv_change_type_title);
        if (!isHasUpdatedContent || isNewAdd)
            textView.setVisibility(View.GONE);
        else {
            textView.setText(isOldData ? R.string.update_item_previous : R.string.update_item_to_2);
            textView.setTextColor(isOldData ? mColorContentPrevious : mColorBlue);
            textView.setVisibility(View.VISIBLE);
        }
        textView = viewElement.findViewById(R.id.tv_work_time);
        textView.setText(FriendlyShowInfoManager.getInstance().getWorkExperienceFormattedTime(workExperienceBean.startDate, workExperienceBean.endDate));
        textView.setTextColor((!isHasUpdatedContent || isNewAdd || !isOldData) ? mColorNormalContentGray : mColorContentPrevious);

        textView = viewElement.findViewById(R.id.tv_company);
        textView.setText(workExperienceBean.companyName);
        textView.setTextColor((!isHasUpdatedContent || isNewAdd || !isOldData) ? mColorNormalContentBlack : mColorContentPrevious);

        textView = viewElement.findViewById(R.id.tv_job_title);
        textView.setTextColor((!isHasUpdatedContent || isNewAdd || !isOldData) ? mColorNormalContentBlack : mColorContentPrevious);
        if (isOpen) {
            textView.setText(getTitleINfo(workExperienceBean));
        } else {
            textView.setText(workExperienceBean.title);
        }

        textView = viewElement.findViewById(R.id.tv_report_to);
        textView.setText(mResources.getString(R.string.report_to_with_value, workExperienceBean.reportTo));
        textView.setTextColor((!isHasUpdatedContent || isNewAdd || !isOldData) ? mColorNormalContentBlack : mColorContentPrevious);
        textView.setVisibility(isOpen ? View.VISIBLE : View.GONE);

        textView = viewElement.findViewById(R.id.tv_subordinate_count);
        textView.setText(mResources.getString(R.string.subordinateCount_value, String.valueOf(workExperienceBean.subordinateCount)));
        textView.setTextColor((!isHasUpdatedContent || isNewAdd || !isOldData) ? mColorNormalContentBlack : mColorContentPrevious);
        textView.setVisibility(isOpen ? View.VISIBLE : View.GONE);

//        textView = viewElement.findViewById(R.id.tv_salary);
//        textView.setText(mResources.getString(R.string.salary_with_value, String.valueOf(workExperienceBean.salary)));
//        textView.setTextColor((!isHasUpdatedContent || isNewAdd || !isOldData) ? mColorNormalContentBlack : mColorContentPrevious);

        textView = viewElement.findViewById(R.id.tv_job_content);
        String content = mResources.getString(R.string.main_job_with_value, workExperienceBean.responsibility);
        if (!isHasUpdatedContent || isNewAdd || !isOldData) {
            textView.setTextColor(mColorNormalContentBlack);
            SpannableString spannableString = new SpannableString(content);
            spannableString.setSpan(new ForegroundColorSpan(mColorNormalContentGray), 5, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);
        } else {
            textView.setTextColor(mColorContentPrevious);
            textView.setText(content);
        }
        textView.setVisibility(isOpen ? View.VISIBLE : View.GONE);

        textView = viewElement.findViewById(R.id.tv_new_tag);
        if (isNewAdd) {
            textView.setText(R.string.new_en);
            textView.setBackgroundResource(R.drawable.list_exchange_function_hint);
            textView.setVisibility(isOpen ? View.VISIBLE : View.GONE);
        } else if (isHasUpdatedContent && !isOldData) {
            textView.setText(R.string.update);
            textView.setBackgroundResource(R.drawable.updated_content_tag_bg);
            textView.setVisibility(isOpen ? View.VISIBLE : View.GONE);
        } else
            textView.setVisibility(View.GONE);
    }

    private String getTitleINfo(WorkExperienceBean workExperienceBean) {
        StringBuilder builder = new StringBuilder();
        builder.append(workExperienceBean.title).append("/");
        if (workExperienceBean.location != 0) {
            builder.append(AddressManager.getInstance().getCityNameByCode(workExperienceBean.location));
            builder.append("/");
        }
        builder.append(workExperienceBean.salary + "K");
        return builder.toString();
    }

    /**
     * 设置候选人评价
     */
    private void setSelfDes(ResumeDetailBean data) {
        data.selfEvaluation = ResumeDetailPresenter.removeLineFeedSymbolAtEnd(data.selfEvaluation);
        if (TextUtils.isEmpty(data.selfEvaluation)) {
            textView_selfDesTitle.setVisibility(View.GONE);
            textView_evaluationContainer.setVisibility(View.GONE);
            view_lineSelfDes.setVisibility(View.GONE);
            view_newTagSelfDes.setVisibility(View.GONE);
            return;
        }
        textView_selfDes.setText(data.selfEvaluation);
        ResumeCompareDao compareDao = mPresenter.getResumeCompareDao();
        boolean isChanged = (compareDao != null && compareDao.isTargetMainInfoChanged(ResumeCompareDao.CHANGE_SELF_EVALUATION));
        boolean isNewAdd = (compareDao != null && compareDao.isTargetMainInfoAdd(ResumeCompareDao.CHANGE_SELF_EVALUATION));
        if (isChanged) {
            view_newTagSelfDes.setVisibility(View.VISIBLE);
            view_newTagSelfDes.setBackgroundResource(isNewAdd ? R.drawable.list_exchange_function_hint : R.drawable.updated_content_tag_bg);
            view_newTagSelfDes.setText(isNewAdd ? R.string.new_en : R.string.update);
            textView_evaluationCurrentTitle.setVisibility(isNewAdd ? View.GONE : View.VISIBLE);
            textView_evaluationBeforeTitle.setVisibility(isNewAdd ? View.GONE : View.VISIBLE);
            textView_selfDesOld.setVisibility(isNewAdd ? View.GONE : View.VISIBLE);
            if (!isNewAdd)
                textView_selfDesOld.setText(compareDao.getOldSMainInfoCache().selfEvaluation);
        } else {
            textView_evaluationCurrentTitle.setVisibility(View.GONE);
            textView_evaluationBeforeTitle.setVisibility(View.GONE);
            textView_selfDesOld.setVisibility(View.GONE);
            view_newTagSelfDes.setVisibility(View.GONE);
        }
        textView_evaluationContainer.setVisibility(View.VISIBLE);
        textView_selfDesTitle.setVisibility(View.VISIBLE);
        view_lineSelfDes.setVisibility(View.VISIBLE);
        ((FoldTextView) textView_selfDes).setFoldLine(3);
    }

    /**
     * 设置候选人教育经历
     */
    private void setEducation(ResumeDetailBean data) {
        if (data.eduList == null || data.eduList.isEmpty()) {
            textView_EducationTitle.setVisibility(View.GONE);
            view_lineEducation.setVisibility(View.GONE);
            linearLayout_educationContainer.setVisibility(View.GONE);
            return;
        }
        int childCurrentCount = linearLayout_educationContainer.getChildCount();
        int needShowCount = data.eduList.size();
        View view_Item, viewValueCurrent, viewValueBefore;
        ViewGroup view_innerItemContainer;
        ImageView imageView_timeLine;
        ResumeCompareDao compareDao = mPresenter.getResumeCompareDao();
        EducationBean educationBean, educationBeforeUpdate;
        boolean isHasUpdatedContent;
        boolean hasCache, isNewAdd, isCreateCurrentItem, isCreateBeforeItem;
        LinearLayout.MarginLayoutParams lpCurrentParent, lpBeforeParent;
        ViewGroup.LayoutParams lpp_timeLine;

        for (int i = 0; i < needShowCount; i++) {
            isCreateCurrentItem = false;
            isCreateBeforeItem = false;
            lpBeforeParent = null;
            educationBean = data.eduList.get(i);
            educationBeforeUpdate = (compareDao != null ? (EducationBean) compareDao.getOldValue(educationBean) : null);
            isHasUpdatedContent = (compareDao != null && !educationBean.equals(educationBeforeUpdate));
            isNewAdd = (isHasUpdatedContent && educationBeforeUpdate == null);
            hasCache = i < childCurrentCount;

            //从已有缓存里面拿，避免重复实例化控件  加快UI速度
            view_Item = (hasCache ? linearLayout_educationContainer.getChildAt(i) : mLayoutInflater.inflate(R.layout.layout_resume_element_container_default_parent, null));
            view_innerItemContainer = view_Item.findViewById(R.id.view_item_container);
            viewValueCurrent = view_innerItemContainer.findViewById(R.id.id_current_item);
            if (viewValueCurrent == null) {
                viewValueCurrent = mLayoutInflater.inflate(R.layout.resume_element_education, null);
                viewValueCurrent.setId(R.id.id_current_item);
                isCreateCurrentItem = true;
            }
            setEducationInnerItem(viewValueCurrent, educationBean, isHasUpdatedContent, isNewAdd, false);

            viewValueBefore = view_innerItemContainer.findViewById(R.id.id_before_item);
            if ((!isHasUpdatedContent || isNewAdd) && viewValueBefore != null)
                viewValueBefore.setVisibility(View.GONE);
            if (isHasUpdatedContent && !isNewAdd) {
                if (viewValueBefore == null) {
                    viewValueBefore = mLayoutInflater.inflate(R.layout.resume_element_education, null);
                    viewValueBefore.setId(R.id.id_before_item);
                    isCreateBeforeItem = true;
                }
                viewValueBefore.setVisibility(View.VISIBLE);
                setEducationInnerItem(viewValueBefore, educationBeforeUpdate, true, false, true);
            }

            imageView_timeLine = view_Item.findViewById(R.id.iv_time_line);
            lpp_timeLine = imageView_timeLine.getLayoutParams();

            lpCurrentParent = (LinearLayout.LayoutParams) viewValueCurrent.getLayoutParams();
            if (lpCurrentParent == null) {
                lpCurrentParent = new LinearLayout.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lpCurrentParent.leftMargin = defaultMargin;
            }

            if (viewValueBefore != null) {
                lpBeforeParent = (LinearLayout.LayoutParams) viewValueBefore.getLayoutParams();
                if (lpBeforeParent == null)
                    lpBeforeParent = new LinearLayout.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lpBeforeParent.leftMargin = defaultMargin;
                lpBeforeParent.topMargin = defaultMargin / 2;
            }


            if (i == needShowCount - 1) {
                lpCurrentParent.bottomMargin = 0;
                if (lpBeforeParent != null)
                    lpBeforeParent.bottomMargin = 0;
                lpp_timeLine.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                imageView_timeLine.setImageResource(R.drawable.time_line_single);
            } else {
                lpCurrentParent.bottomMargin = defaultMargin;
                if (lpBeforeParent != null)
                    lpBeforeParent.bottomMargin = defaultMargin;
                lpp_timeLine.height = ViewGroup.LayoutParams.MATCH_PARENT;
                imageView_timeLine.setImageResource(R.drawable.time_line_normal);
            }
            imageView_timeLine.setLayoutParams(lpp_timeLine);
            viewValueCurrent.setLayoutParams(lpCurrentParent);
            if (viewValueBefore != null)
                viewValueBefore.setLayoutParams(lpBeforeParent);

            if (isCreateCurrentItem)
                view_innerItemContainer.addView(viewValueCurrent, lpCurrentParent);
            if (isCreateBeforeItem)
                view_innerItemContainer.addView(viewValueBefore, lpBeforeParent);
            view_Item.setVisibility(View.VISIBLE);
            if (!hasCache)
                linearLayout_educationContainer.addView(view_Item);
        }

        //多余的缓存控件要隐藏
        if (childCurrentCount > needShowCount) {
            for (; needShowCount < childCurrentCount; needShowCount++)
                linearLayout_educationContainer.getChildAt(needShowCount).setVisibility(View.GONE);
        }
        view_lineEducation.setVisibility(View.VISIBLE);
        textView_EducationTitle.setVisibility(View.VISIBLE);
        linearLayout_educationContainer.setVisibility(View.VISIBLE);
    }

    private void setEducationInnerItem(View viewElement, EducationBean educationBean, boolean isHasUpdatedContent, boolean isNewAdd, boolean isOldData) {
        TextView textView = viewElement.findViewById(R.id.tv_change_type_title);
        if (!isHasUpdatedContent || isNewAdd)
            textView.setVisibility(View.GONE);
        else {
            textView.setText(isOldData ? R.string.update_item_previous : R.string.update_item_to_2);
            textView.setTextColor(isOldData ? mColorContentPrevious : mColorBlue);
            textView.setVisibility(View.VISIBLE);
        }

        textView = viewElement.findViewById(R.id.tv_learn_time);
        textView.setText(FriendlyShowInfoManager.getInstance().getWorkExperienceFormattedTime(educationBean.startDate, educationBean.endDate));
        textView.setTextColor((!isHasUpdatedContent || isNewAdd || !isOldData) ? mColorNormalContentGray : mColorContentPrevious);

        textView = viewElement.findViewById(R.id.tv_school);
        textView.setText(educationBean.schoolName);
        textView.setTextColor((!isHasUpdatedContent || isNewAdd || !isOldData) ? mColorNormalContentBlack : mColorContentPrevious);

        //1--高中及以下  senior schoolName; 2--大专  associate; 3--本科  bachelor; 4--硕士  master; 5--MBA(工商管理硕士) ; 6--博士  doctor
        textView = viewElement.findViewById(R.id.tv_degree_and_major);
        textView.setText(FriendlyShowInfoManager.getInstance().getDegree(educationBean.degree) + "/" + (TextUtils.isEmpty(educationBean.majorName) ? mResources.getString(R.string.education_major_unknown) : educationBean.majorName));
        textView.setTextColor((!isHasUpdatedContent || isNewAdd || !isOldData) ? mColorNormalContentBlack : mColorContentPrevious);

        textView = viewElement.findViewById(R.id.tv_new_tag);
        if (isNewAdd) {
            textView.setText(R.string.new_en);
            textView.setBackgroundResource(R.drawable.list_exchange_function_hint);
            textView.setVisibility(View.VISIBLE);
        } else if (isHasUpdatedContent && !isOldData) {
            textView.setText(R.string.update);
            textView.setBackgroundResource(R.drawable.updated_content_tag_bg);
            textView.setVisibility(View.VISIBLE);
        } else
            textView.setVisibility(View.GONE);
    }

    public void setIntentions(ResumeDetailBean data) {
        //test data
//        data.intentions = new ArrayList<>(1);
//        IntentionBean intentionBean = new IntentionBean();
//        intentionBean.locationId = 432;
//        intentionBean.minSalary = 10000;
//        intentionBean.maxSalary = 50000;
//        intentionBean.title ="JAVA开发工程师";
//        data.intentions.add(intentionBean);
        if (data.intentions == null || data.intentions.isEmpty()) {
            textView_intentionsTitle.setVisibility(View.GONE);
            view_newTagIntention.setVisibility(View.GONE);
            linearLayout_intentionsContainer.setVisibility(View.GONE);
            view_lineIntention.setVisibility(View.GONE);
            return;
        }
        IntentionUnion intentionUnion = calculateIntentionUpdate(data.intentions);
        int index = 0;
        TextView textView;
        ResumeCompareDao compareDao = mPresenter.getResumeCompareDao();
        boolean isAttributesUpdated = (compareDao != null && compareDao.isTargetMainInfoChanged(ResumeCompareDao.CHANGE_INTENTION));
        if (intentionUnion.title != null) {
            textView = getIntentionShowView(index);
            textView.setText(mResources.getString(R.string.intentions_job, intentionUnion.title));
            textView.setVisibility(View.VISIBLE);
            index++;
//            textView.setTextColor((isAttributesUpdated ? mColorUpdateContent : mColorNormalContentBlack));
            textView.setTextColor(mColorNormalContentBlack);
        }
        if (intentionUnion.city != null) {
            textView = getIntentionShowView(index);
            textView.setText(mResources.getString(R.string.intentions_city, intentionUnion.city));
            textView.setVisibility(View.VISIBLE);
            textView.setTextColor(mColorNormalContentBlack);
//            textView.setTextColor((isAttributesUpdated ? mColorUpdateContent : mColorNormalContentBlack));
            index++;
        }
        if (intentionUnion.maxSalary > 0) {
            textView = getIntentionShowView(index);
            textView.setText(mResources.getString(R.string.intentions_salary, intentionUnion.minSalary + "K-" + intentionUnion.maxSalary + "K"));
            textView.setVisibility(View.VISIBLE);
//            textView.setTextColor((isAttributesUpdated ? mColorUpdateContent : mColorNormalContentBlack));
            textView.setTextColor(mColorNormalContentBlack);
            index++;
        }
        int childCountEnableIndex = linearLayout_intentionsContainer.getChildCount() - 1;
        if (index <= childCountEnableIndex) {
            for (; index <= childCountEnableIndex; index++) {
                linearLayout_intentionsContainer.getChildAt(index).setVisibility(View.GONE);
            }
        }
        textView_intentionsTitle.setVisibility(View.VISIBLE);
        view_lineIntention.setVisibility(View.VISIBLE);
        view_newTagIntention.setVisibility(isAttributesUpdated ? View.VISIBLE : View.GONE);
        linearLayout_intentionsContainer.setVisibility(View.VISIBLE);
    }

    private TextView getIntentionShowView(int index) {
        if (index < linearLayout_intentionsContainer.getChildCount())
            return (TextView) linearLayout_intentionsContainer.getChildAt(index);
        int padding = (int) mResumeDetailView.callGetResources().getDimension(R.dimen.dp_5);
        TextView textView = new TextView(mResumeDetailView.callGetBindActivity());
        textView.setPadding(0, 0, 0, padding);
        textView.setTextColor(mResources.getColor(R.color.text_color_blue_0D315C));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, ScreenUtil.px2dip(mResumeDetailView.callGetResources().getDimension(R.dimen.text_size_11)));
        linearLayout_intentionsContainer.addView(textView);
        return textView;
    }

    private IntentionUnion calculateIntentionUpdate(List<IntentionBean> intentions) {
        IntentionUnion intentionUnion = new IntentionUnion();
        HashSet<String> jobSet = new HashSet<>();
        HashSet<String> citySet = new HashSet<>();
        IntentionBean bean;
        for (int i = 0, size = intentions.size(); i < size; i++) {
            bean = intentions.get(i);
            if (!TextUtils.isEmpty(bean.title))
                jobSet.add(bean.title);
            if (bean.locationId > 0)
                citySet.add(AddressManager.getInstance().getCityNameByCode(bean.locationId));
            if (bean.maxSalary > intentionUnion.maxSalary) {
                intentionUnion.maxSalary = bean.maxSalary;
                intentionUnion.minSalary = bean.minSalary;
            }
        }
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

    @Override
    public void onClick(View view) {
        view.setVisibility(View.GONE);
        isOpen = true;
        setWorkExperience(resumeDetailBean, isOpen);
    }
}
