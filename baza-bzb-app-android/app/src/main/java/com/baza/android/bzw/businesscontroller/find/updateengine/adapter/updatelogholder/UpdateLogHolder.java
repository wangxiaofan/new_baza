package com.baza.android.bzw.businesscontroller.find.updateengine.adapter.updatelogholder;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.bean.resume.ResumeUpdateLogResultBean;
import com.baza.android.bzw.bean.resumeelement.IntentionBean;
import com.baza.android.bzw.bean.resumeelement.IntentionUnion;
import com.baza.android.bzw.bean.resumeelement.ProjectExperienceBean;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.AddressManager;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.slib.utils.DateUtil;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/8/29.
 * Title：
 * Note：
 */

public class UpdateLogHolder extends BaseLogHolder implements View.OnClickListener {
    @BindView(R.id.tv_log_title)
    TextView textView_logType;
    @BindView(R.id.tv_pick_up)
    TextView textView_packUp;
    @BindView(R.id.tv_main_info_title)
    TextView textView_mainInfoTitle;
    @BindView(R.id.ll_main_info_container)
    LinearLayout linearLayout_mainInfoContainer;
    @BindView(R.id.tv_education_title)
    TextView textView_educationTitle;
    @BindView(R.id.ll_education_container)
    LinearLayout linearLayout_educationContainer;
    @BindView(R.id.tv_work_title)
    TextView textView_workExperienceTitle;
    @BindView(R.id.ll_main_work_container)
    LinearLayout linearLayout_workExperienceContainer;
    @BindView(R.id.tv_self_evaluation_title)
    TextView textView_selfEvaluationTitle;
    @BindView(R.id.tv_self_evaluation)
    TextView textView_selfEvaluationContent;
    @BindView(R.id.tv_self_intentions_title)
    TextView textView_intentionsTitle;
    @BindView(R.id.ll_selfIntentions_container)
    LinearLayout linearLayout_intentionsContainer;
    @BindView(R.id.iv_pack_up)
    ImageView imageView_line;
    @BindView(R.id.tv_project_title)
    TextView textView_projectTitle;
    @BindView(R.id.ll_project_container)
    LinearLayout linearLayout_projectContainer;
    private int mMainInfoIndex;
    private int mEducationIndex;
    private int mWorkExperienceIndex;

    public UpdateLogHolder(Context context, View convertView, ILogAdapterListener listener) {
        super(context, convertView, listener);
    }

    @Override
    int getItemLayoutId() {
        return R.layout.layout_update_log_update;
    }

    @Override
    void initContentView() {
        ButterKnife.bind(this, view_contentRoot);
        textView_packUp.setOnClickListener(this);
    }

    @Override
    void setHolderData() {
        ResumeUpdateLogResultBean.LogResumeContent logResumeContent = mLogData.getLogResumeContent();
        if (logResumeContent == null)
            return;
        textView_logType.setText(getLogTypeMsg(mLogData.sceneId));
        boolean isUnPackUp = mListener.isCurrentUnPackUp(String.valueOf(mLogData.id));
        view_contentRoot.setBackgroundResource((isUnPackUp ? R.drawable.shape_background_update_log_normal : R.drawable.shape_background_update_log_pick_up));
        textView_packUp.setCompoundDrawables(null, null, (isUnPackUp ? mListener.getDrawableUnPackUp() : mListener.getDrawablePackUp()), null);
        imageView_line.setVisibility((isUnPackUp ? View.VISIBLE : View.GONE));
        if (!isUnPackUp) {
            textView_mainInfoTitle.setVisibility(View.GONE);
            linearLayout_mainInfoContainer.setVisibility(View.GONE);
            linearLayout_educationContainer.setVisibility(View.GONE);
            textView_educationTitle.setVisibility(View.GONE);
            textView_workExperienceTitle.setVisibility(View.GONE);
            linearLayout_workExperienceContainer.setVisibility(View.GONE);
            textView_selfEvaluationTitle.setVisibility(View.GONE);
            textView_selfEvaluationContent.setVisibility(View.GONE);
            textView_intentionsTitle.setVisibility(View.GONE);
            linearLayout_intentionsContainer.setVisibility(View.GONE);
            textView_projectTitle.setVisibility(View.GONE);
            linearLayout_projectContainer.setVisibility(View.GONE);
            return;
        }
        setMainInfoViews(logResumeContent);
        setEducationViews(logResumeContent);
        setWorkExperienceViews(logResumeContent);
        setProjectViews(logResumeContent);
        if (!TextUtils.isEmpty(logResumeContent.selfEvaluation)) {
            textView_selfEvaluationTitle.setVisibility(View.VISIBLE);
            textView_selfEvaluationContent.setText(logResumeContent.selfEvaluation);
            textView_selfEvaluationContent.setVisibility(View.VISIBLE);
        } else {
            textView_selfEvaluationTitle.setVisibility(View.GONE);
            textView_selfEvaluationContent.setVisibility(View.GONE);
        }
        setIntentionViews(logResumeContent);
    }

    /**
     * 注意mIndex下标
     */
    private void setMainInfoViews(ResumeUpdateLogResultBean.LogResumeContent logResumeContent) {
        mMainInfoIndex = 0;
        //真实姓名
        if (!TextUtils.isEmpty(logResumeContent.realName))
            setMainInfoItemValue(mResources.getString(R.string.update_item_name, logResumeContent.realName));
        //手机
        if (!TextUtils.isEmpty(logResumeContent.mobile))
            setMainInfoItemValue(mResources.getString(R.string.update_item_cellphone, logResumeContent.mobile));
        //职位
        if (!TextUtils.isEmpty(logResumeContent.title))
            setMainInfoItemValue(mResources.getString(R.string.update_item_job, logResumeContent.title));
        //邮箱
        if (!TextUtils.isEmpty(logResumeContent.email))
            setMainInfoItemValue(mResources.getString(R.string.update_item_email, logResumeContent.email));
        //学历
        if (logResumeContent.degree > 0)
            setMainInfoItemValue(mResources.getString(R.string.update_item_degree, FriendlyShowInfoManager.getInstance().getDegree(logResumeContent.degree)));
        //性别
        if (logResumeContent.gender > -1)
            setMainInfoItemValue(mResources.getString(R.string.update_item_sex, mResources.getString((logResumeContent.gender == CommonConst.SEX_MALE ? R.string.male : (logResumeContent.gender == CommonConst.SEX_FEMALE ? R.string.female : R.string.message_unknown)))));
        //工作年限
        if (logResumeContent.yearOfExperience > 0)
            setMainInfoItemValue(mResources.getString(R.string.update_item_work_year, String.valueOf(logResumeContent.yearOfExperience)));
        //所在公司
        if (!TextUtils.isEmpty(logResumeContent.company))
            setMainInfoItemValue(mResources.getString(R.string.update_item_company, logResumeContent.company));
        //所在地区
        if (logResumeContent.locationId > 0)
            setMainInfoItemValue(mResources.getString(R.string.update_item_city, AddressManager.getInstance().getCityNameByCode(logResumeContent.locationId)));
        //毕业院校
        if (!TextUtils.isEmpty(logResumeContent.school))
            setMainInfoItemValue(mResources.getString(R.string.update_item_school, logResumeContent.school));
        //专业
        if (!TextUtils.isEmpty(logResumeContent.major))
            setMainInfoItemValue(mResources.getString(R.string.update_item_major, logResumeContent.major));
        int childCountEnableIndex = linearLayout_mainInfoContainer.getChildCount() - 1;
        if (mMainInfoIndex <= childCountEnableIndex) {
            for (int i = mMainInfoIndex; i <= childCountEnableIndex; i++) {
                linearLayout_mainInfoContainer.getChildAt(i).setVisibility(View.GONE);
            }
        }
        textView_mainInfoTitle.setVisibility((mMainInfoIndex > 0) ? View.VISIBLE : View.GONE);
        linearLayout_mainInfoContainer.setVisibility((mMainInfoIndex > 0) ? View.VISIBLE : View.GONE);
    }

    private void setMainInfoItemValue(CharSequence charSequence) {
        TextView textView;
        if (mMainInfoIndex < linearLayout_mainInfoContainer.getChildCount())
            textView = (TextView) linearLayout_mainInfoContainer.getChildAt(mMainInfoIndex);
        else {
            LogUtil.d("create MainInfoShowView");
            textView = new TextView(mContext);
            textView.setTextColor(mResources.getColor(R.color.text_color_gray_a9adb3));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            linearLayout_mainInfoContainer.addView(textView);
        }
        textView.setText(charSequence);
        textView.setVisibility(View.VISIBLE);
        mMainInfoIndex++;
    }

    private void setProjectViews(ResumeUpdateLogResultBean.LogResumeContent logResumeContent) {
        List<ProjectExperienceBean> allProjectExperienceList = logResumeContent.getAllProjectExperienceList();
        if (allProjectExperienceList == null || allProjectExperienceList.isEmpty()) {
            textView_projectTitle.setVisibility(View.GONE);
            linearLayout_projectContainer.setVisibility(View.GONE);
            return;
        }
        int childCurrentCount = linearLayout_projectContainer.getChildCount();
        int needShowCount = allProjectExperienceList.size();
        int bottomMargin = ScreenUtil.dip2px(5);
        View view_projectItem;
        TextView textView;
        ImageView imageView;
        ProjectExperienceBean projectExperienceBean;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        boolean hasCache;
        int color = mResources.getColor(R.color.text_color_gray_a9adb3);
        for (int i = 0; i < needShowCount; i++) {
            hasCache = (i < childCurrentCount);
            //从已有缓存里面拿，避免重复实例化控件  加快UI速度
            view_projectItem = (hasCache ? linearLayout_projectContainer.getChildAt(i) : layoutInflater.inflate(R.layout.layout_project_experience_item, null));
            View linearLayout_main_item = view_projectItem.findViewById(R.id.line_mainItem);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) linearLayout_main_item.getLayoutParams();
            if (lp == null)
                lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = bottomMargin;
            linearLayout_main_item.setLayoutParams(lp);
            //更新数据
            projectExperienceBean = allProjectExperienceList.get(i);
            textView = linearLayout_main_item.findViewById(R.id.tv_project_content);
            setNormalTextStyle(textView, color, projectExperienceBean.projectDescription);
            imageView = view_projectItem.findViewById(R.id.iv_time_line);
            ViewGroup.LayoutParams lpp = imageView.getLayoutParams();
            if (i == needShowCount - 1) {
                lpp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                imageView.setLayoutParams(lpp);
                imageView.setImageResource(R.drawable.time_line_single);
            } else {
                lpp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                imageView.setLayoutParams(lpp);
                imageView.setImageResource(R.drawable.time_line_normal);
            }
            view_projectItem.setVisibility(View.VISIBLE);
            if (!hasCache)
                linearLayout_projectContainer.addView(view_projectItem);
        }
        //多余的缓存控件要隐藏
        if (childCurrentCount > needShowCount) {
            for (; needShowCount < childCurrentCount; needShowCount++)
                linearLayout_projectContainer.getChildAt(needShowCount).setVisibility(View.GONE);
        }
        textView_projectTitle.setVisibility(View.VISIBLE);
        linearLayout_projectContainer.setVisibility(View.VISIBLE);
    }

    private void setEducationViews(ResumeUpdateLogResultBean.LogResumeContent logResumeContent) {
        mEducationIndex = 0;
        List<ResumeUpdateLogResultBean.LogEducation> educations = logResumeContent.getAllEducationList();
        if (educations == null || educations.isEmpty()) {
            linearLayout_educationContainer.setVisibility(View.GONE);
            textView_educationTitle.setVisibility(View.GONE);
            return;
        }
        int childCurrentCount = linearLayout_educationContainer.getChildCount();
        int needShowCount = educations.size();
        int bottomMargin = ScreenUtil.dip2px(5);
        View view_education_item;
        TextView textView;
        ImageView imageView;
        ResumeUpdateLogResultBean.LogEducation educationBean;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        boolean hasCache;
        int color = mResources.getColor(R.color.text_color_gray_a9adb3);
        for (; mEducationIndex < needShowCount; mEducationIndex++) {
            hasCache = (mEducationIndex < childCurrentCount);
            //从已有缓存里面拿，避免重复实例化控件  加快UI速度
            view_education_item = (hasCache ? linearLayout_educationContainer.getChildAt(mEducationIndex) : layoutInflater.inflate(R.layout.layout_education_item, null));
            View linearLayout_main_item = view_education_item.findViewById(R.id.ll_main_item);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) linearLayout_main_item.getLayoutParams();
            if (lp == null)
                lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = bottomMargin;
            linearLayout_main_item.setLayoutParams(lp);
            //更新数据
            educationBean = educations.get(mEducationIndex);
            textView = linearLayout_main_item.findViewById(R.id.tv_learn_time);
            setNormalTextStyle(textView, color, FriendlyShowInfoManager.getInstance().getWorkExperienceFormattedTime(DateUtil.parseLongDate(educationBean.startDate, DateUtil.SDF_YMD_HMS), DateUtil.parseLongDate(educationBean.endDate, DateUtil.SDF_YMD_HMS)));
            textView = linearLayout_main_item.findViewById(R.id.tv_school);
            setNormalTextStyle(textView, color, educationBean.schoolName);
            //1--高中及以下  senior schoolName; 2--大专  associate; 3--本科  bachelor; 4--硕士  master; 5--MBA(工商管理硕士) ; 6--博士  doctor
            textView = linearLayout_main_item.findViewById(R.id.tv_degree_and_major);
            setNormalTextStyle(textView, color, FriendlyShowInfoManager.getInstance().getDegree(educationBean.degree) + " " + educationBean.majorName);
            imageView = view_education_item.findViewById(R.id.iv_time_line);
            ViewGroup.LayoutParams lpp = imageView.getLayoutParams();
            if (mEducationIndex == needShowCount - 1) {
                lpp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                imageView.setLayoutParams(lpp);
                imageView.setImageResource(R.drawable.time_line_single);
            } else {
                lpp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                imageView.setLayoutParams(lpp);
                imageView.setImageResource(R.drawable.time_line_normal);
            }
            view_education_item.setVisibility(View.VISIBLE);
            if (!hasCache) {
                LogUtil.d("create Education log view");
                linearLayout_educationContainer.addView(view_education_item);
            }
        }
        //多余的缓存控件要隐藏
        if (childCurrentCount > needShowCount) {
            for (; needShowCount < childCurrentCount; needShowCount++)
                linearLayout_educationContainer.getChildAt(needShowCount).setVisibility(View.GONE);
        }
        textView_educationTitle.setVisibility(View.VISIBLE);
        linearLayout_educationContainer.setVisibility(View.VISIBLE);
    }

    private void setNormalTextStyle(TextView textView, int color, CharSequence text) {
        textView.setTextColor(color);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setText(text);
    }

    private void setWorkExperienceViews(ResumeUpdateLogResultBean.LogResumeContent logResumeContent) {
        mWorkExperienceIndex = 0;
        List<ResumeUpdateLogResultBean.LogWorkExperience> workExperiences = logResumeContent.getAllWorkExperienceList();
        if (workExperiences == null || workExperiences.isEmpty()) {
            textView_workExperienceTitle.setVisibility(View.GONE);
            linearLayout_workExperienceContainer.setVisibility(View.GONE);
            return;
        }
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        int childCurrentCount = linearLayout_workExperienceContainer.getChildCount();
        int needShowCount = workExperiences.size();
        int bottomMargin = ScreenUtil.dip2px(5);
        View view_experience_item;
        TextView textView;
        ImageView imageView;
        boolean hasCache;
        int color = mResources.getColor(R.color.text_color_gray_a9adb3);
        ResumeUpdateLogResultBean.LogWorkExperience workExperienceBean;
        for (; mWorkExperienceIndex < needShowCount; mWorkExperienceIndex++) {
            hasCache = (mWorkExperienceIndex < childCurrentCount);
            //从已有缓存里面拿，避免重复实例化控件  加快UI速度
            view_experience_item = (hasCache ? linearLayout_workExperienceContainer.getChildAt(mWorkExperienceIndex) : layoutInflater.inflate(R.layout.layout_work_experience_item, null));
            View linearLayout_main_item = view_experience_item.findViewById(R.id.line_mainItem);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) linearLayout_main_item.getLayoutParams();
            if (lp == null)
                lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = bottomMargin;
            linearLayout_main_item.setLayoutParams(lp);
            //更新数据
            workExperienceBean = workExperiences.get(mWorkExperienceIndex);
            textView = view_experience_item.findViewById(R.id.tv_work_time);
            setNormalTextStyle(textView, color, FriendlyShowInfoManager.getInstance().getWorkExperienceFormattedTime(DateUtil.parseLongDate(workExperienceBean.startDate, DateUtil.SDF_YMD_HMS), DateUtil.parseLongDate(workExperienceBean.endDate, DateUtil.SDF_YMD_HMS)));
            textView = view_experience_item.findViewById(R.id.tv_company);
            setNormalTextStyle(textView, color, workExperienceBean.companyName);
            textView = view_experience_item.findViewById(R.id.tv_job_title);
            setNormalTextStyle(textView, color, workExperienceBean.title);
            textView = view_experience_item.findViewById(R.id.tv_report_to);
            setNormalTextStyle(textView, color, mResources.getString(R.string.report_to_with_value, workExperienceBean.reportTo));
            textView = view_experience_item.findViewById(R.id.tv_subordinate_count);
            setNormalTextStyle(textView, color, mResources.getString(R.string.subordinateCount_value, String.valueOf(workExperienceBean.subordinateCount)));
            textView = view_experience_item.findViewById(R.id.tv_salary);
            setNormalTextStyle(textView, color, mResources.getString(R.string.salary_with_value, String.valueOf(workExperienceBean.salary)));
            textView = view_experience_item.findViewById(R.id.tv_job_content);
            setNormalTextStyle(textView, color, mResources.getString(R.string.main_job_with_value, workExperienceBean.responsibility));

            imageView = view_experience_item.findViewById(R.id.iv_time_line);
            ViewGroup.LayoutParams lpp = imageView.getLayoutParams();
            if (mWorkExperienceIndex == needShowCount - 1) {
                lpp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                imageView.setLayoutParams(lpp);
                imageView.setImageResource(R.drawable.time_line_single);
            } else {
                lpp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                imageView.setLayoutParams(lpp);
                imageView.setImageResource(R.drawable.time_line_normal);
            }
            view_experience_item.setVisibility(View.VISIBLE);
            if (!hasCache) {
                LogUtil.d("create WorkExperience log view");
                linearLayout_workExperienceContainer.addView(view_experience_item);
            }
        }
        //多余的缓存控件要隐藏
        if (childCurrentCount > needShowCount) {
            for (; needShowCount < childCurrentCount; needShowCount++)
                linearLayout_workExperienceContainer.getChildAt(needShowCount).setVisibility(View.GONE);
        }
        textView_workExperienceTitle.setVisibility(View.VISIBLE);
        linearLayout_workExperienceContainer.setVisibility(View.VISIBLE);
    }

    private void setIntentionViews(ResumeUpdateLogResultBean.LogResumeContent logResumeContent) {
        List<IntentionBean> intentions = logResumeContent.getAllIntentionList();
        if (intentions == null || intentions.isEmpty()) {
            textView_intentionsTitle.setVisibility(View.GONE);
            linearLayout_intentionsContainer.setVisibility(View.GONE);
            return;
        }
        IntentionUnion intentionUnion = logResumeContent.getIntentionUnion();
        if (intentionUnion == null) {
            intentionUnion = calculateIntentionUpdate(intentions);
            logResumeContent.setIntentionUnion(intentionUnion);
        }

        int index = 0;
        TextView textView;
        if (intentionUnion.title != null) {
            textView = getIntentionShowView(index);
            textView.setText(mResources.getString(R.string.intentions_job, intentionUnion.title));
            textView.setVisibility(View.VISIBLE);
            index++;
        }
        if (intentionUnion.city != null) {
            textView = getIntentionShowView(index);
            textView.setText(mResources.getString(R.string.intentions_city, intentionUnion.city));
            textView.setVisibility(View.VISIBLE);
            index++;
        }
        if (intentionUnion.maxSalary > 0) {
            textView = getIntentionShowView(index);
            textView.setText(mResources.getString(R.string.intentions_salary, intentionUnion.minSalary + "-" + intentionUnion.maxSalary));
            textView.setVisibility(View.VISIBLE);
            index++;
        }
        int childCountEnableIndex = linearLayout_intentionsContainer.getChildCount() - 1;
        if (index <= childCountEnableIndex) {
            for (; index <= childCountEnableIndex; index++) {
                linearLayout_intentionsContainer.getChildAt(index).setVisibility(View.GONE);
            }
        }

        textView_intentionsTitle.setVisibility(View.VISIBLE);
        linearLayout_intentionsContainer.setVisibility(View.VISIBLE);
    }

    private TextView getIntentionShowView(int index) {
        if (index < linearLayout_intentionsContainer.getChildCount())
            return (TextView) linearLayout_intentionsContainer.getChildAt(index);
        LogUtil.d("create IntentionShowView");
        TextView textView = new TextView(mContext);
        textView.setTextColor(mResources.getColor(R.color.text_color_gray_a9adb3));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pick_up:
                boolean isUnPickUp = mListener.isCurrentUnPackUp(String.valueOf(mLogData.id));
                if (isUnPickUp)
                    mListener.removeUnPackUp(String.valueOf(mLogData.id));
                else
                    mListener.cacheInUnPackUp(String.valueOf(mLogData.id));
                setHolderData();
                break;
        }
    }
}
