package com.baza.android.bzw.businesscontroller.resume.detail;

import android.content.res.Resources;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.ListPopupWindow;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeDetailBean;
import com.baza.android.bzw.businesscontroller.resume.detail.presenter.ResumeDetailPresenter;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.ResumeCompareDao;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.manager.AddressManager;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.baza.android.bzw.widget.LineBreakLayout;
import com.bznet.android.rcbox.R;
import com.slib.utils.AppUtil;
import com.slib.utils.DateUtil;
import com.slib.utils.DialogUtil;
import com.slib.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/24.
 * Title：
 * Note：
 */

public class HeadViewMainInfoUI implements View.OnClickListener, View.OnLongClickListener {
    private static final int CLICK_TYPE_PHONE = 0;
    private static final int CLICK_TYPE_EMAIL = 1;
    private TextView textView_phone;
    private TextView textView_email;
    private View view_addRemarkTitle;
    private ResumeMainInfoElement mResumeMainInfoElement;
    private ResumeMainInfoElement mResumeMainInfoElementOld;

    private View view_root;
    private Resources mResources;
    private IResumeDetailView mResumeDetailView;
    private ResumeDetailPresenter mPresenter;
    private int mColorCompletionNormal;
    private StringBuilder mStringBuilder = new StringBuilder();
    private ListPopupWindow mListPopEmailAndPhone;
    private String[] mPhoneMenuItem;
    private String[] mPhoneMenuItemInvalid;
    private String[] mEmailMenuItem;
    private String[] mEmailMenuItemInvalid;
    private List<String> mMenuItemList;
    private ArrayAdapter<String> mMenuAdapter;
    private int mClickType;

    public HeadViewMainInfoUI(IResumeDetailView mResumeDetailView, ResumeDetailPresenter mPresenter) {
        this.mResumeDetailView = mResumeDetailView;
        this.mPresenter = mPresenter;
        this.mResources = mResumeDetailView.callGetResources();
        this.mColorCompletionNormal = mResources.getColor(R.color.text_color_orange_FF7700);
        setUpViews();
    }

    public View getHeadView() {
        return view_root;
    }

    public void destroy() {
    }

    private void setUpViews() {
        if (view_root == null) {
            view_root = mResumeDetailView.callGetBindActivity().getLayoutInflater().inflate(R.layout.head_view_resume_main_info_mine, null);
            mResumeMainInfoElement = new ResumeMainInfoElement(view_root.findViewById(R.id.view_resume_ele_content));
            view_addRemarkTitle = view_root.findViewById(R.id.fl_add_remark);
            textView_phone = view_root.findViewById(R.id.tv_contact_phone);
            textView_email = view_root.findViewById(R.id.tv_contact_email);
            textView_phone.setTextColor(mColorCompletionNormal);
            textView_email.setTextColor(mColorCompletionNormal);
            textView_phone.setOnClickListener(this);
            textView_phone.setOnLongClickListener(this);
            textView_email.setOnClickListener(this);
            textView_email.setOnLongClickListener(this);
            view_root.findViewById(R.id.btn_add_remark).setOnClickListener(this);
        }
    }

    public void setData() {
        //判断更新引擎高亮
        ResumeCompareDao compareDao = mPresenter.getResumeCompareDao();
        ResumeDetailBean resumeDetailBean = mPresenter.getCurrentResumeData();
        if (resumeDetailBean == null)
            return;
        view_addRemarkTitle.setVisibility(View.VISIBLE);
        setResumeElement(resumeDetailBean, mResumeMainInfoElement);
        mResumeMainInfoElement.textView_updateTag.setVisibility(View.GONE);
        if (compareDao != null && compareDao.isTargetMainInfoChanged(ResumeCompareDao.CHANGE_REAL_NAME | ResumeCompareDao.CHANGE_TITLE | ResumeCompareDao.CHANGE_LOCATION | ResumeCompareDao.CHANGE_YEAR_EXPERIENCE | ResumeCompareDao.CHANGE_GENDER | ResumeCompareDao.CHANGE_BIRTHDAY
                | ResumeCompareDao.CHANGE_MARRIAGE | ResumeCompareDao.CHANGE_HUKOU | ResumeCompareDao.CHANGE_COMPANY | ResumeCompareDao.CHANGE_MAJOR | ResumeCompareDao.CHANGE_SCHOOL | ResumeCompareDao.CHANGE_DEGREE)) {
            if (mResumeMainInfoElementOld == null) {
                ViewStub viewStub = view_root.findViewById(R.id.viewStub_resume_main_info_old);
                mResumeMainInfoElementOld = new ResumeMainInfoElement(viewStub.inflate());
                mResumeMainInfoElementOld.removeNoUseElement();
            }
            setResumeElement(compareDao.getOldSMainInfoCache(), mResumeMainInfoElementOld);
            mResumeMainInfoElementOld.view_root.setVisibility(View.VISIBLE);
            mResumeMainInfoElement.textView_updateTag.setVisibility(View.VISIBLE);
        } else if (mResumeMainInfoElementOld != null)
            mResumeMainInfoElementOld.view_root.setVisibility(View.GONE);

        updateMobileOrEmailValidView();
    }

    private void setResumeElement(ResumeDetailBean resumeDetailBean, ResumeMainInfoElement mainInfoElement) {
        if (mainInfoElement.imageView_tagJobHunter != null)
            mainInfoElement.imageView_tagJobHunter.setVisibility((resumeDetailBean.isJobHunting ? View.VISIBLE : View.GONE));
        mainInfoElement.textView_realName.setText(resumeDetailBean.realName);
        mainInfoElement.textView_title.setText(mResources.getString(R.string.job_title, (TextUtils.isEmpty(resumeDetailBean.title) ? mResources.getString(R.string.title_unknown) : resumeDetailBean.title)));
        mainInfoElement.textView_secondMainInfo.setText(getShownSecondMainInfo(resumeDetailBean));
        if (TextUtils.isEmpty(resumeDetailBean.company))
            mainInfoElement.textView_companyInfo.setVisibility(View.GONE);
        else {
            mainInfoElement.textView_companyInfo.setText(resumeDetailBean.company);
            mainInfoElement.textView_huKouInfo.setVisibility(View.VISIBLE);
        }
        if (resumeDetailBean.huKou == 0)
            mainInfoElement.textView_huKouInfo.setVisibility(View.GONE);
        else {
            mainInfoElement.textView_huKouInfo.setText(mResources.getString(R.string.huKou_value, AddressManager.getInstance().getCityNameByCode(resumeDetailBean.huKou)));
            mainInfoElement.textView_huKouInfo.setVisibility(View.VISIBLE);
        }
        String schoolInfo = getSchoolInfo(resumeDetailBean);
        if (schoolInfo == null)
            mainInfoElement.textView_schoolInfo.setVisibility(View.GONE);
        else {
            mainInfoElement.textView_schoolInfo.setText(schoolInfo);
            mainInfoElement.textView_schoolInfo.setVisibility(View.VISIBLE);
        }
        String source = ResumeDao.getSourceForShow(resumeDetailBean);
        if (source != null) {
            mainInfoElement.textView_sourceFrom.setVisibility(View.VISIBLE);
            mainInfoElement.textView_sourceFrom.setText(source);
            mainInfoElement.textView_title.setMaxEms(CommonConst.MAX_LIST_JOB_EMS_SHORT);
        } else {
            mainInfoElement.textView_title.setMaxEms(CommonConst.MAX_LIST_JOB_EMS_LONG);
            mainInfoElement.textView_sourceFrom.setVisibility(View.GONE);
        }

        if (mainInfoElement.lineBreakLayout_labels != null)
            setLabels(mainInfoElement.lineBreakLayout_labels, resumeDetailBean);
        boolean isOldData = (mResumeMainInfoElementOld != null && mainInfoElement == mResumeMainInfoElementOld);

        String subInfo = mResources.getString(R.string.resume_sub_info__no_owner_value, DateUtil.longMillions2FormatDate(resumeDetailBean.sourceUpdateTime, DateUtil.SDF_YMD), mResources.getString((resumeDetailBean.currentCompletion >= CommonConst.COMLPETION_HIGH_LEVEL ? R.string.completion_high : (resumeDetailBean.currentCompletion >= CommonConst.COMLPETION_NORMAL_LEVEL ? R.string.completion_normal : R.string.completion_low))));
        if (isOldData) {
            mainInfoElement.textView_subInfo.setText(subInfo);
        } else {
            SpannableString spannableString = new SpannableString(subInfo);
            spannableString.setSpan(new ForegroundColorSpan(mColorCompletionNormal), 5, 16, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(mColorCompletionNormal), spannableString.length() - 1, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            mainInfoElement.textView_subInfo.setText(spannableString);
        }

        if (isOldData) {
            int color = mResources.getColor(R.color.text_color_grey_94A1A5);
            mainInfoElement.textView_changeTypeTitle.setVisibility(View.VISIBLE);
            mainInfoElement.textView_realName.setTextColor(color);
            mainInfoElement.textView_title.setTextColor(color);
            mainInfoElement.textView_sourceFrom.setTextColor(color);
            mainInfoElement.textView_subInfo.setTextColor(color);
            mainInfoElement.textView_secondMainInfo.setTextColor(color);
            mainInfoElement.textView_companyInfo.setTextColor(color);
            mainInfoElement.textView_schoolInfo.setTextColor(color);
            mainInfoElement.textView_huKouInfo.setTextColor(color);
            mainInfoElement.textView_sourceFrom.setBackgroundResource(R.drawable.resume_text_bg_source_from_old);

            ConstraintLayout.MarginLayoutParams cml = (ConstraintLayout.MarginLayoutParams) mainInfoElement.textView_realName.getLayoutParams();
            cml.topMargin = 0;
            mainInfoElement.textView_realName.setLayoutParams(cml);
        }
    }

    private String getShownSecondMainInfo(ResumeBean resumeBean) {
        if (mStringBuilder.length() > 0)
            mStringBuilder.delete(0, mStringBuilder.length());
        if (AddressManager.getInstance().isCityCodeEnable(resumeBean.location))
            mStringBuilder.append(AddressManager.getInstance().getCityNameByCode(resumeBean.location)).append("/");
        if (resumeBean.yearExpr > 0)
            mStringBuilder.append(mResources.getString(R.string.work_year_value, String.valueOf(resumeBean.yearExpr))).append("/");
        if (resumeBean.gender == CommonConst.SEX_FEMALE || resumeBean.gender == CommonConst.SEX_MALE)
            mStringBuilder.append(mResources.getString(resumeBean.gender == CommonConst.SEX_FEMALE ? R.string.female : R.string.male)).append("/");
        if (Long.parseLong(resumeBean.birthday) > 0)
            mStringBuilder.append(DateUtil.longMillions2FormatDate(Long.parseLong(resumeBean.birthday), DateUtil.SDF_YMD)).append("/");
        if (resumeBean.marriage == CommonConst.Marriage.SINGLE || resumeBean.marriage == CommonConst.Marriage.MARRIAGE || resumeBean.marriage == CommonConst.Marriage.SECRET)
            mStringBuilder.append(FriendlyShowInfoManager.getInstance().getMarriage(resumeBean.marriage)).append("/");
        if (mStringBuilder.length() > 0) {
            mStringBuilder.deleteCharAt(mStringBuilder.length() - 1);
            return mStringBuilder.toString();
        }
        return mResources.getString(R.string.common_info_unknown);
    }

    private String getSchoolInfo(ResumeBean resumeBean) {
        if (mStringBuilder.length() > 0)
            mStringBuilder.delete(0, mStringBuilder.length());
        if (!TextUtils.isEmpty(resumeBean.school))
            mStringBuilder.append(resumeBean.school).append("/");
        if (!TextUtils.isEmpty(resumeBean.major))
            mStringBuilder.append(resumeBean.major).append("/");
        if (FriendlyShowInfoManager.getInstance().isDegreeEnable(resumeBean.degree))
            mStringBuilder.append(FriendlyShowInfoManager.getInstance().getDegree(resumeBean.degree)).append("/");
        if (mStringBuilder.length() > 0) {
            mStringBuilder.deleteCharAt(mStringBuilder.length() - 1);
            return mStringBuilder.toString();
        }
        return null;
    }

    /**
     * 设置标签
     */
    private void setLabels(LineBreakLayout lineBreakLayout_labels, ResumeDetailBean candidate) {
        mPresenter.getResumeDetailLogger().sendAddTagLog(mResumeDetailView.callGetBindActivity(), candidate.candidateId, candidate.firmId,
                candidate.ownerId, candidate.tagBindingList);
        List<Label> tags = candidate.tagBindingList;
        int needShowCount = (tags == null ? 0 : tags.size());
        if (needShowCount == 0) {
            lineBreakLayout_labels.setVisibility(View.GONE);
            return;
        }
        int itemHeight = ScreenUtil.dip2px(20);
        int currentCount = lineBreakLayout_labels.getChildCount();
        boolean hasCache;
        TextView textView_label;
        Label label;
        for (int index = 0; index < needShowCount; index++) {
            label = tags.get(index);
            hasCache = index < currentCount;
            textView_label = (TextView) (hasCache ? lineBreakLayout_labels.getChildAt(index) : LayoutInflater.from(mResumeDetailView.callGetBindActivity()).inflate(R.layout.item_label, null));
            textView_label.setText(label.tag);
            if (!hasCache)
                lineBreakLayout_labels.addView(textView_label, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, itemHeight));
            textView_label.setVisibility(View.VISIBLE);
        }
        if (needShowCount < currentCount) {
            for (; needShowCount < currentCount; needShowCount++) {
                lineBreakLayout_labels.getChildAt(needShowCount).setVisibility(View.GONE);
            }
        }
        lineBreakLayout_labels.setVisibility(View.VISIBLE);
    }

    void updateViewForUpdateHistory() {
        view_addRemarkTitle.setVisibility(View.GONE);
        textView_phone.setEnabled(false);
        textView_email.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_contact_phone:
                //点击手机号
                clickPhoneNumber();
                break;
            case R.id.tv_contact_email:
                //点击邮箱
                clickEmail();
                break;
            case R.id.tv_update_to_target:
                mResumeDetailView.updateToTargetResume();
                break;
            case R.id.btn_add_remark:
                mResumeDetailView.callShowAddRemarkView();
                break;
        }
    }

    /**
     * 点击手机号弹出打电话、发送、复制对话框
     */
    private void clickPhoneNumber() {
        ResumeDetailBean resumeDetailBean = mPresenter.getCurrentResumeData();
        if (resumeDetailBean == null)
            return;
        if (!resumeDetailBean.isMobileValid() && mPhoneMenuItemInvalid == null) {
            mPhoneMenuItemInvalid = mResources.getStringArray(R.array.long_click_phone_number_invalid);
        } else if (mPhoneMenuItem == null)
            mPhoneMenuItem = mResources.getStringArray(R.array.long_click_phone_number_valid);

        showPhoneOrEmailMenu(textView_phone, resumeDetailBean.isMobileValid() ? mPhoneMenuItem : mPhoneMenuItemInvalid);
    }

    /**
     * 点击邮件弹出发送复制对话框
     */
    private void clickEmail() {
        ResumeDetailBean resumeDetailBean = mPresenter.getCurrentResumeData();
        if (resumeDetailBean == null || !AppUtil.checkEmail(resumeDetailBean.email))
            return;
        if (!resumeDetailBean.isEmailValid() && mEmailMenuItemInvalid == null)
            mEmailMenuItemInvalid = mResources.getStringArray(R.array.long_click_email_invalid);
        else if (mEmailMenuItem == null)
            mEmailMenuItem = mResources.getStringArray(R.array.long_click_email_valid);
        showPhoneOrEmailMenu(textView_email, resumeDetailBean.isEmailValid() ? mEmailMenuItem : mEmailMenuItemInvalid);
    }

    private void showPhoneOrEmailMenu(View anchorView, String[] items) {
        if (mMenuItemList == null)
            mMenuItemList = new ArrayList<>(items.length);
        else
            mMenuItemList.clear();
        Collections.addAll(mMenuItemList, items);
        mClickType = (anchorView == textView_phone ? CLICK_TYPE_PHONE : CLICK_TYPE_EMAIL);
        if (mListPopEmailAndPhone == null) {
            mListPopEmailAndPhone = new ListPopupWindow(mResumeDetailView.callGetBindActivity());
            mMenuAdapter = new ArrayAdapter<>(mResumeDetailView.callGetBindActivity(), R.layout.list_pop_defaylt_menu_item, mMenuItemList);
            mListPopEmailAndPhone.setAdapter(mMenuAdapter);
            mListPopEmailAndPhone.setWidth(ScreenUtil.screenWidth / 3);
            mListPopEmailAndPhone.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            mListPopEmailAndPhone.setModal(true);
        }

        mListPopEmailAndPhone.setAnchorView(anchorView);
        mListPopEmailAndPhone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListPopEmailAndPhone.dismiss();
                if (mClickType == CLICK_TYPE_PHONE) {
                    switch (position) {
                        case 0:
                            mPresenter.startCall();
                            break;
                        case 1:
                            mPresenter.sendSms(null);
                            break;
                        case 2:
                            if (mPresenter.getCurrentResumeData().isMobileValid())
                                markMobileOrEmailValid(true);
                            else
                                AppUtil.copyToClipboard(mResumeDetailView.callGetBindActivity(), mPresenter.getCurrentResumeData().mobile);
                            break;
                        case 3:
                            AppUtil.copyToClipboard(mResumeDetailView.callGetBindActivity(), mPresenter.getCurrentResumeData().mobile);
                            break;
                    }
                    return;
                }
                switch (position) {
                    case 0:
                        mPresenter.sendEmail();
                        break;
                    case 1:
                        if (mPresenter.getCurrentResumeData().isEmailValid())
                            markMobileOrEmailValid(false);
                        else
                            AppUtil.copyToClipboard(mResumeDetailView.callGetBindActivity(), mPresenter.getCurrentResumeData().email);
                        break;
                    case 2:
                        AppUtil.copyToClipboard(mResumeDetailView.callGetBindActivity(), mPresenter.getCurrentResumeData().email);
                        break;
                }
            }
        });
        mMenuAdapter.notifyDataSetChanged();
        mListPopEmailAndPhone.show();
    }

    private void markMobileOrEmailValid(final boolean mobileValid) {
        Resources resources = mResumeDetailView.callGetResources();
        DialogUtil.doubleButtonShow(mResumeDetailView.callGetBindActivity(), null, resources.getString(R.string.valid_mobile_or_email, resources.getString(mobileValid ? R.string.mobile : R.string.email)), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.markValid(mobileValid);
            }
        }, null);
    }

    void updateMobileOrEmailValidView() {
        ResumeDetailBean resumeDetailBean = mPresenter.getCurrentResumeData();
        if (!resumeDetailBean.isMobileValid() && !TextUtils.isEmpty(resumeDetailBean.mobile)) {
            SpannableString spannableString = new SpannableString(resumeDetailBean.mobile);
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView_phone.setText(spannableString);
        } else
            textView_phone.setText(resumeDetailBean.mobile);

        if (!resumeDetailBean.isEmailValid() && !TextUtils.isEmpty(resumeDetailBean.email)) {
            SpannableString spannableString = new SpannableString(resumeDetailBean.email);
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView_email.setText(spannableString);
        } else
            textView_email.setText(resumeDetailBean.email);
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.tv_contact_phone:
                //点击手机号
                clickPhoneNumber();
                break;
            case R.id.tv_contact_email:
                //点击邮箱
                clickEmail();
                break;
        }
        return true;
    }

    private static class ResumeMainInfoElement {
        View view_root;
        TextView textView_changeTypeTitle;
        TextView textView_realName;
        TextView textView_title;
        TextView textView_secondMainInfo;
        TextView textView_companyInfo;
        TextView textView_schoolInfo;
        TextView textView_subInfo;
        TextView textView_huKouInfo;
        TextView textView_sourceFrom;
        TextView textView_updateTag;
        ImageView imageView_tagJobHunter;
        LineBreakLayout lineBreakLayout_labels;

        ResumeMainInfoElement(View root) {
            this.view_root = root;
            textView_changeTypeTitle = view_root.findViewById(R.id.tv_change_type_title);
            textView_updateTag = view_root.findViewById(R.id.tv_new_tag);
            imageView_tagJobHunter = view_root.findViewById(R.id.view_resume_ele_hunter_job);
            textView_realName = view_root.findViewById(R.id.view_resume_ele_real_name);
            textView_title = view_root.findViewById(R.id.view_resume_ele_title);
            textView_secondMainInfo = view_root.findViewById(R.id.view_resume_ele_second_main_info);
            textView_companyInfo = view_root.findViewById(R.id.view_resume_ele_company_info);
            textView_schoolInfo = view_root.findViewById(R.id.view_resume_ele_school_info);
            textView_huKouInfo = view_root.findViewById(R.id.view_resume_ele_hukou_info);
            textView_subInfo = view_root.findViewById(R.id.view_resume_ele_sub_info);
            textView_sourceFrom = view_root.findViewById(R.id.view_resume_ele_source_from);
            lineBreakLayout_labels = view_root.findViewById(R.id.view_resume_ele_labels);
        }

        void removeNoUseElement() {
            ((ViewGroup) view_root).removeView(imageView_tagJobHunter);
            ((ViewGroup) view_root).removeView(lineBreakLayout_labels);
        }
    }
}
