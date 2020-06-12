package com.baza.android.bzw.businesscontroller.company;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeDetailBean;
import com.baza.android.bzw.bean.resumeelement.RemarkBean;
import com.baza.android.bzw.businesscontroller.company.fragment.RecommendListFragment;
import com.baza.android.bzw.businesscontroller.company.fragment.RemarkListFragment;
import com.baza.android.bzw.businesscontroller.company.fragment.ResumeListFragment;
import com.baza.android.bzw.businesscontroller.email.BindEmailActivity;
import com.baza.android.bzw.businesscontroller.email.EmailShareActivity;
import com.baza.android.bzw.businesscontroller.find.updateengine.ResumeUpdateCardActivity;
import com.baza.android.bzw.businesscontroller.message.IMAddGroupChatActivity;
import com.baza.android.bzw.businesscontroller.message.IMAddGroupChatSearchActivity;
import com.baza.android.bzw.businesscontroller.message.IMResumeBean;
import com.baza.android.bzw.businesscontroller.resume.create.CreateResumeActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.DetailBottomMenuUI;
import com.baza.android.bzw.businesscontroller.resume.detail.RecommendUI;
import com.baza.android.bzw.businesscontroller.resume.detail.presenter.ResumeDetailPresenter;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.manager.AddressManager;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.baza.android.bzw.widget.LineBreakLayout;
import com.baza.android.bzw.widget.ListPopupWindow;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.ViewPagerForScrollView;
import com.baza.android.bzw.widget.dialog.AddRemarkDialog;
import com.bznet.android.rcbox.R;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.slib.utils.AppUtil;
import com.slib.utils.DateUtil;
import com.slib.utils.DialogUtil;
import com.slib.utils.ScreenUtil;
import com.slib.utils.ToastUtil;
import com.slib.utils.string.StringUtil;
import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CompanyDetailActivity extends BaseActivity implements View.OnClickListener, IResumeDetailView {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ibtn_left_click)
    ImageButton ibtnLeftClick;
    @BindView(R.id.ibtn_right_click)
    ImageButton ibtnRightClick;
    @BindView(R.id.view_title_bar)
    FrameLayout viewTitleBar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_major)
    TextView tvMajor;
    @BindView(R.id.tv_user_info)
    TextView tvUserInfo;
    @BindView(R.id.tv_user_address_info)
    TextView tvUserAddressInfo;
    @BindView(R.id.tv_company_info)
    TextView tvCompanyInfo;
    @BindView(R.id.tv_college_info)
    TextView tvCollegeInfo;
    @BindView(R.id.tv_update_and_integrity_info)
    TextView tvUpdateAndIntegrityInfo;
    @BindView(R.id.stl_main)
    SlidingTabLayout stlMain;
    @BindView(R.id.vp_main)
    ViewPagerForScrollView vpMain;
    @BindView(R.id.tv_check_phone)
    TextView tv_check_phone;
    @BindView(R.id.tv_contact_phone)
    TextView tv_contact_phone;
    @BindView(R.id.tv_contact_email)
    TextView tv_contact_email;
    @BindView(R.id.tv_check_email)
    TextView tv_check_email;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.iv_flag)
    ImageView iv_flag;
    @BindView(R.id.view_resume_ele_labels)
    LineBreakLayout lineBreakLayout_labels;

    private ArrayList<Fragment> mFragments;
    private ResumeDetailPresenter mPresenter;
    private ListPopupWindow listPopupWindow_moreOperation;
    private StringBuilder mStringBuilder = new StringBuilder();
    private int mColorCompletionNormal;
    private DetailBottomMenuUI mDetailBottomMenuUI;
    private ResumeListFragment resumeListFragment;
    private RemarkListFragment remarkListFragment;
    private RecommendListFragment recommendListFragment;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_company_detail;
    }

    @Override
    protected String getPageTitle() {
        return "企业人才库详情";
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        mPresenter = new ResumeDetailPresenter(this, getIntent());
        //底部菜单
        mDetailBottomMenuUI = new DetailBottomMenuUI(this, mPresenter);
        ibtnRightClick.setImageResource(R.drawable.icon_more_black);
        mFragments = new ArrayList<>();
        resumeListFragment = new ResumeListFragment(this, mPresenter);
        remarkListFragment = new RemarkListFragment(this, mPresenter);
        recommendListFragment = new RecommendListFragment(this, mPresenter);
        mFragments.add(resumeListFragment);
        mFragments.add(remarkListFragment);
        mFragments.add(recommendListFragment);
        stlMain.setViewPager(vpMain, new String[]{"简历", "寻访记录", "推荐"}, this, mFragments);
        vpMain.setOffscreenPageLimit(3);
        stlMain.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                ResumeBean bean = mPresenter.getCurrentResumeData();
                if (position == 1) {
                    mPresenter.getResumeDetailLogger().sendClickInquiryRecordsLog(CompanyDetailActivity.this, bean.candidateId,
                            bean.firmId, (bean.owner != null && bean.owner.unionId != null) ? bean.owner.unionId : "");
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        mColorCompletionNormal = mResources.getColor(R.color.text_color_orange_FF7700);

        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                loadingView.startLoading(null);
                mPresenter.loadInitData();
            }
        });
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    public static void launch(Activity activity, IResumeDetailView.IntentParam intentParam) {
        if (intentParam == null)
            return;
        Intent intent = new Intent(activity, CompanyDetailActivity.class);
        intent.putExtra("intentParam", intentParam);
        activity.startActivity(intent);
    }

    @OnClick({R.id.ibtn_left_click, R.id.ibtn_right_click, R.id.tv_check_phone, R.id.tv_check_email, R.id.tv_contact_phone, R.id.tv_contact_email})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;

            case R.id.ibtn_right_click:
                //更多编辑菜单
                showMoreEditMenu();
                break;

            case R.id.tv_check_phone:
                ResumeDetailBean resumeBean = mPresenter.getCurrentResumeData();
                mPresenter.checkContact(1, resumeBean.candidateId);
                mPresenter.getResumeDetailLogger().sendViewMobileLog(this, resumeBean.candidateId, resumeBean.firmId, resumeBean.ownerId);
                break;

            case R.id.tv_check_email:
                ResumeDetailBean resumeBean1 = mPresenter.getCurrentResumeData();
                mPresenter.checkContact(2, resumeBean1.candidateId);
                mPresenter.getResumeDetailLogger().sendViewEmailLog(this, resumeBean1.candidateId, resumeBean1.firmId, resumeBean1.ownerId);
                break;
            case R.id.tv_contact_phone:
                clickPhoneNumber();
                break;
            case R.id.tv_contact_email:
                clickEmail();
                break;
        }
    }

    @Override
    public void updateViewForCurrentMode() {

    }

    @Override
    public void updateViewForUpdateHistory() {

    }

    @Override
    public void callShowSpecialToastMsg(int type, String msg, int msgId) {
        switch (type) {
            case ResumeDetailPresenter.SELF_TOAST_RECORD:
                View view_hint = getLayoutInflater().inflate(R.layout.toast_record_hint, null);
                ((TextView) view_hint.findViewById(R.id.tv_text_hint)).setText(msg == null ? mResources.getString(msgId) : msg);
                ToastUtil.selfToast(mApplication, view_hint);
                break;
            case ResumeDetailPresenter.SELF_TOAST_COLLECTION:
                ImageView imageView = new ImageView(mApplication);
                imageView.setImageResource((R.string.un_collection_success == msgId ? R.drawable.image_uncollection : R.drawable.image_collection));
                ToastUtil.selfToast(mApplication, imageView);
                break;
            case ResumeDetailPresenter.TOAST_ADD_OR_REMOVE_TRACKINGLIST:
                ToastUtil.showToast(mApplication, msg);
                break;
        }
    }

    @Override
    public void callFinishPlayVoice() {
        remarkListFragment.callFinishPlayVoice();
    }

    @Override
    public void callUpdateRemarkViews(int targetPosition) {
        remarkListFragment.updateData(targetPosition);
        stlMain.getTitleView(1).setText("寻访记录(" + mPresenter.getRemarkListData().size() + ")");
    }

    @Override
    public void callShowAddRemarkView() {
//        AddTextRemarkActivity.launch(CompanyDetailActivity.this, mPresenter.getCurrentResumeData(), null, RequestCodeConst.INT_REQUEST_NEW_ADD_REMARK, true);
        AddRemarkDialog addRemarkDialog = new AddRemarkDialog(this);
        addRemarkDialog.setData(mPresenter.getCurrentResumeData(), null);
        addRemarkDialog.setRemarkListener(new AddRemarkDialog.updateRemarkListener() {
            @Override
            public void updateRemark(RemarkBean remarkBean) {
                mPresenter.addOrUpdateRemark(remarkBean);
            }
        });
        addRemarkDialog.show();
    }

    @Override
    public void callEmailShare(boolean hasBindEmail, boolean isShareContact, boolean isShareRemark) {
        if (!hasBindEmail)
            BindEmailActivity.launch(this);
        else
            EmailShareActivity.launch(CompanyDetailActivity.this, mPresenter.getCurrentResumeData().candidateId, isShareContact, isShareRemark, mPresenter.getCurrentResumeData().isHasOriginalFile == ResumeDetailBean.ORIGINAL_FILE_EXIST);
    }

    @Override
    public void callCancelLoadingView(boolean success, int errorCode, String errorMsg) {
        if (!loadingView.isShownVisibility())
            return;
        if (success)
            loadingView.finishLoading();
        else
            loadingView.loadingFailed(errorCode, errorMsg);
    }

    @Override
    public void callShowLoadingView() {
        if (loadingView.isShownVisibility())
            return;
        loadingView.startLoading(null);
    }

    @Override
    public void callUpdateCollectionStatus() {
        mDetailBottomMenuUI.updateCollectionInfo();
    }

    @Override
    public void callUpdateResumeMainInfo() {
        ResumeDetailBean candidateDetailBean = mPresenter.getCurrentResumeData();
        setLabels(lineBreakLayout_labels, candidateDetailBean);
        mPresenter.getResumeDetailLogger().sendAddTagLog(this, candidateDetailBean.candidateId, candidateDetailBean.firmId,
                candidateDetailBean.ownerId, candidateDetailBean.tagBindingList);
    }

    @Override
    public void updateToTargetResume() {
        ResumeUpdateCardActivity.launch(this, mPresenter.getCurrentResumeData().candidateId, RequestCodeConst.INT_REQUEST_UPDATE_RESUME_BY_ENGINE);
    }

    @Override
    public void callUpdateDurationChangedView(long curPosition) {
        remarkListFragment.updateDurationChangedView(curPosition);
    }

    @Override
    public void callShowRecommendView() {
        new RecommendUI(this, new RecommendUI.IRecommendSetListener() {
            @Override
            public void onRecommendSet(String content, Date date) {
                mPresenter.addRecommend(content, date);
            }
        }).show();
    }

    @Override
    public void callUpdateMobileOrEmailValidView() {
        ResumeDetailBean resumeDetailBean = mPresenter.getCurrentResumeData();
        if (!resumeDetailBean.isMobileValid() && !TextUtils.isEmpty(resumeDetailBean.mobile) && resumeDetailBean.mobileStatus != 2) {
            SpannableString spannableString = new SpannableString(resumeDetailBean.mobile);
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_contact_phone.setText(spannableString);
        } else
            setPhoneNum(resumeDetailBean);

        if (!resumeDetailBean.isEmailValid() && !TextUtils.isEmpty(resumeDetailBean.email) && resumeDetailBean.emailStatus != 2) {
            SpannableString spannableString = new SpannableString(resumeDetailBean.email);
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_contact_email.setText(spannableString);
        } else
            setEmailNum(resumeDetailBean);
    }

    @Override
    public void callUpdateMobileOrEmailNum(int type, String content) {
        if (StringUtil.isEmpty(content)) {
            content = "未知";
        } else {
            if (type == 1) {
                mPresenter.getCurrentResumeData().mobile = content;
                mPresenter.getCurrentResumeData().mobileStatus = 3;
            } else if (type == 2) {
                mPresenter.getCurrentResumeData().email = content;
                mPresenter.getCurrentResumeData().emailStatus = 3;
            }
        }
        if (type == 1) {
            tv_check_phone.setVisibility(View.GONE);
            tv_contact_phone.setVisibility(View.VISIBLE);
            tv_contact_phone.setText(content);
        } else if (type == 2) {
            tv_check_email.setVisibility(View.GONE);
            tv_contact_email.setVisibility(View.VISIBLE);
            tv_contact_email.setText(content);
        }
    }

    @Override
    public void callUpdateHandleResumeDataViews() {
        ResumeDetailBean candidateDetailBean = mPresenter.getCurrentResumeData();
        if (candidateDetailBean.inquiryList.size() > 0) {
            stlMain.getTitleView(1).setText("寻访记录(" + mPresenter.getRemarkListData().size() + ")");
        }
        stlMain.getTitleView(2).setText("推荐(" + candidateDetailBean.recommendTotal + ")");
        tvTitle.setText(mResources.getString(R.string.candidate_detail_title, candidateDetailBean != null ? candidateDetailBean.realName : null));
        setResumeElement(candidateDetailBean);
        resumeListFragment.setData();
        recommendListFragment.setData();
        mDetailBottomMenuUI.setData();
        callUpdateMobileOrEmailValidView();
        if (mPresenter.isAddRemark()) {
            stlMain.setCurrentTab(1);
        }
        if (candidateDetailBean.isJobHunting) {
            iv_flag.setVisibility(View.VISIBLE);
        } else {
            iv_flag.setVisibility(View.GONE);
        }
    }

    /**
     * 右上角更多编辑菜单
     */
    private void showMoreEditMenu() {
        String[] moreEditMenu = mPresenter.getCompanyMoreEditMenu();
        if (moreEditMenu == null)
            return;
        if (listPopupWindow_moreOperation == null)
            listPopupWindow_moreOperation = new ListPopupWindow(this, null, new ListPopupWindow.IMenuClickListener() {
                @Override
                public void onMenuClick(int position) {
                    ResumeDetailBean resumeBean = mPresenter.getCurrentResumeData();
                    switch (position) {
                        case 0:
                            if (resumeBean.trackingListStatus == 0) {
                                mPresenter.addOrRemoveTrackingList(0, resumeBean.candidateId);
                                mPresenter.getResumeDetailLogger().sendAddTrackingListLog(CompanyDetailActivity.this, resumeBean.candidateId, resumeBean.firmId, resumeBean.ownerId);
                            } else if (resumeBean.trackingListStatus == 1) {
                                mPresenter.addOrRemoveTrackingList(1, resumeBean.candidateId);
                                mPresenter.getResumeDetailLogger().sendRemoveTrackingListLog(CompanyDetailActivity.this, resumeBean.candidateId, resumeBean.firmId, resumeBean.ownerId);
                            }
                            break;
                        case 1:
                            CreateResumeActivity.launch(CompanyDetailActivity.this, resumeBean, true, mPresenter.getmIntentParam().pageCode);
                            break;
                        case 2:
                            //分享
                            /**
                             *       resumeId: "",  //候选人简历id
                             *       candidateName: "",  //候选人姓名
                             *       title: "",  //候选人当前职位
                             *       company: "",  //候选人当前公司
                             *       cityName: "",  //所在城市
                             *       yearOfExperience: 0,  //工作经验
                             *       degree: 0,  //学历，
                             *       school: ""  //毕业院校
                             */
                            String school = "";
                            if (TextUtils.isEmpty(resumeBean.school)) {
                                school = "学校信息暂无";
                            } else {
                                school = resumeBean.school.length() > 8 ? resumeBean.school.substring(0, 8) + "..." : resumeBean.school;
                            }
                            IMResumeBean bean = new IMResumeBean("candidateShare", new IMResumeBean.Data(resumeBean.owner.userName, resumeBean.realName,
                                    resumeBean.title, resumeBean.company,
                                    AddressManager.getInstance().getCityNameByCode(resumeBean.location),
                                    resumeBean.yearExpr, resumeBean.degree, school)
                            );
                            String jsonBean = new Gson().toJson(bean);
                            Log.e("herb", "简历组装>>" + jsonBean);
                            Intent intent = new Intent(CompanyDetailActivity.this, IMAddGroupChatActivity.class);
                            intent.putExtra("TYPE", "3");
                            intent.putExtra("DETAILS", jsonBean);
                            startActivity(intent);
                            break;
                    }
                }
            });
        listPopupWindow_moreOperation.updateMenus(moreEditMenu);
        listPopupWindow_moreOperation.show(ibtnRightClick);
    }

    private void setResumeElement(ResumeDetailBean resumeDetailBean) {
        tvName.setText(resumeDetailBean.realName);
        tvMajor.setText(mResources.getString(R.string.job_title, (TextUtils.isEmpty(resumeDetailBean.title) ? mResources.getString(R.string.title_unknown) : resumeDetailBean.title)));
        tvUserInfo.setText(getShownSecondMainInfo(resumeDetailBean));
        if (TextUtils.isEmpty(resumeDetailBean.company))
            tvCompanyInfo.setText("公司信息暂无");
        else {
            tvCompanyInfo.setText(resumeDetailBean.company);
        }
        if (resumeDetailBean.huKou == 0)
            tvUserAddressInfo.setVisibility(View.GONE);
        else {
            tvUserAddressInfo.setText(mResources.getString(R.string.huKou_value, AddressManager.getInstance().getCityNameByCode(resumeDetailBean.huKou)));
            tvUserAddressInfo.setVisibility(View.VISIBLE);
        }
        String schoolInfo = getSchoolInfo(resumeDetailBean);
        if (schoolInfo == null)
            tvCollegeInfo.setText("学校信息暂无");
        else {
            tvCollegeInfo.setText(schoolInfo);
        }

        String subInfo = mResources.getString(R.string.resume_sub_info__no_owner_value, DateUtil.longMillions2FormatDate(resumeDetailBean.sourceUpdateTime, DateUtil.SDF_YMD), resumeDetailBean.currentCompletion + "");
        SpannableString spannableString = new SpannableString(subInfo);
        spannableString.setSpan(new ForegroundColorSpan(mColorCompletionNormal), 5, 16, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(mColorCompletionNormal), spannableString.length() - (resumeDetailBean.currentCompletion + "").length(), spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvUpdateAndIntegrityInfo.setText(spannableString);
        setLabels(lineBreakLayout_labels, resumeDetailBean);
        setPhoneNum(resumeDetailBean);
        setEmailNum(resumeDetailBean);
    }

    /**
     * 根据手机号状态设置手机号显示样式
     *
     * @param resumeDetailBean
     */
    private void setPhoneNum(ResumeDetailBean resumeDetailBean) {
        if (resumeDetailBean.mobileStatus == 1) {
            tv_check_phone.setVisibility(View.VISIBLE);
            tv_contact_phone.setVisibility(View.GONE);
        } else if (resumeDetailBean.mobileStatus == 2) {
            tv_check_phone.setVisibility(View.GONE);
            tv_contact_phone.setVisibility(View.VISIBLE);
            tv_contact_phone.setText("上锁保护中");
        } else if (resumeDetailBean.mobileStatus == 3) {
            tv_check_phone.setVisibility(View.GONE);
            tv_contact_phone.setVisibility(View.VISIBLE);
            tv_contact_phone.setText(StringUtil.isEmpty(resumeDetailBean.mobile) ? "未知" : resumeDetailBean.mobile);
        }
    }

    /**
     * 根据邮箱状态设置邮箱显示样式
     *
     * @param resumeDetailBean
     */
    private void setEmailNum(ResumeDetailBean resumeDetailBean) {
        if (resumeDetailBean.emailStatus == 1) {
            tv_check_email.setVisibility(View.VISIBLE);
            tv_contact_email.setVisibility(View.GONE);
        } else if (resumeDetailBean.emailStatus == 2) {
            tv_check_email.setVisibility(View.GONE);
            tv_contact_email.setVisibility(View.VISIBLE);
            tv_contact_email.setText("上锁保护中");
        } else if (resumeDetailBean.emailStatus == 3) {
            tv_check_email.setVisibility(View.GONE);
            tv_contact_email.setVisibility(View.VISIBLE);
            tv_contact_email.setText(StringUtil.isEmpty(resumeDetailBean.email) ? "未知" : resumeDetailBean.email);
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
        if (Long.parseLong(resumeBean.birthday) > 0) {
            try {
                mStringBuilder.append(DateUtil.getAge(DateUtil.parse(DateUtil.longMillions2FormatDate(Long.parseLong(resumeBean.birthday), DateUtil.SDF_YMD), DateUtil.SDF_YMD))).append("岁/");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            textView_label = (TextView) (hasCache ? lineBreakLayout_labels.getChildAt(index) : LayoutInflater.from(this).inflate(R.layout.item_label, null));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestCodeConst.INT_REQUEST_UPDATE_RESUME_BY_ENGINE:
                if (resultCode == Activity.RESULT_OK)
                    mPresenter.highLightUpdatedByEngineContent();
                break;
            case RequestCodeConst.INT_REQUEST_NEW_ADD_REMARK:
                if (resultCode == Activity.RESULT_OK && data != null)
                    mPresenter.addOrUpdateRemark((RemarkBean) data.getSerializableExtra("remark"));
                break;
        }
    }

    private String[] mPhoneMenuItem;
    private String[] mPhoneMenuItemInvalid;
    private String[] mEmailMenuItem;
    private String[] mEmailMenuItemInvalid;
    private List<String> mMenuItemList;
    private int mClickType;
    private static final int CLICK_TYPE_PHONE = 0;
    private static final int CLICK_TYPE_EMAIL = 1;
    private androidx.appcompat.widget.ListPopupWindow mListPopEmailAndPhone;
    private ArrayAdapter<String> mMenuAdapter;

    /**
     * 点击手机号弹出打电话、发送、复制对话框
     */
    private void clickPhoneNumber() {
        ResumeDetailBean resumeDetailBean = mPresenter.getCurrentResumeData();
        if (resumeDetailBean == null || resumeDetailBean.mobileStatus == 2)
            return;
        if (!resumeDetailBean.isMobileValid() && mPhoneMenuItemInvalid == null) {
            mPhoneMenuItemInvalid = mResources.getStringArray(R.array.long_click_phone_number_invalid);
        } else if (mPhoneMenuItem == null)
            mPhoneMenuItem = mResources.getStringArray(R.array.long_click_phone_number_valid);

        showPhoneOrEmailMenu(tv_contact_phone, resumeDetailBean.isMobileValid() ? mPhoneMenuItem : mPhoneMenuItemInvalid);
    }

    /**
     * 点击邮件弹出发送复制对话框
     */
    private void clickEmail() {
        ResumeDetailBean resumeDetailBean = mPresenter.getCurrentResumeData();
        if (resumeDetailBean == null || !AppUtil.checkEmail(resumeDetailBean.email) || resumeDetailBean.emailStatus == 2)
            return;
        if (!resumeDetailBean.isEmailValid() && mEmailMenuItemInvalid == null)
            mEmailMenuItemInvalid = mResources.getStringArray(R.array.long_click_email_invalid);
        else if (mEmailMenuItem == null)
            mEmailMenuItem = mResources.getStringArray(R.array.long_click_email_valid);
        showPhoneOrEmailMenu(tv_contact_email, resumeDetailBean.isEmailValid() ? mEmailMenuItem : mEmailMenuItemInvalid);
    }

    private void showPhoneOrEmailMenu(View anchorView, String[] items) {
        if (mMenuItemList == null)
            mMenuItemList = new ArrayList<>(items.length);
        else
            mMenuItemList.clear();
        Collections.addAll(mMenuItemList, items);
        mClickType = (anchorView == tv_contact_phone ? CLICK_TYPE_PHONE : CLICK_TYPE_EMAIL);
        if (mListPopEmailAndPhone == null) {
            mListPopEmailAndPhone = new androidx.appcompat.widget.ListPopupWindow(this);
            mMenuAdapter = new ArrayAdapter<>(this, R.layout.list_pop_defaylt_menu_item, mMenuItemList);
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
                                AppUtil.copyToClipboard(CompanyDetailActivity.this, mPresenter.getCurrentResumeData().mobile);
                            break;
                        case 3:
                            AppUtil.copyToClipboard(CompanyDetailActivity.this, mPresenter.getCurrentResumeData().mobile);
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
                            AppUtil.copyToClipboard(CompanyDetailActivity.this, mPresenter.getCurrentResumeData().email);
                        break;
                    case 2:
                        AppUtil.copyToClipboard(CompanyDetailActivity.this, mPresenter.getCurrentResumeData().email);
                        break;
                }
            }
        });
        mMenuAdapter.notifyDataSetChanged();
        mListPopEmailAndPhone.show();
    }

    private void markMobileOrEmailValid(final boolean mobileValid) {
        Resources resources = getResources();
        DialogUtil.doubleButtonShow(this, null, resources.getString(R.string.valid_mobile_or_email, resources.getString(mobileValid ? R.string.mobile : R.string.email)), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.markValid(mobileValid);
            }
        }, null);
    }

    @Override
    protected void onActivityDeadForApp() {
        if (mDetailBottomMenuUI != null)
            mDetailBottomMenuUI.destroy();
        if (mPresenter != null)
            mPresenter.onDestroy();
    }
}
