package com.baza.android.bzw.businesscontroller.resume.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.manager.AddressManager;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.SwipeMenuLayout;
import com.slib.utils.AppUtil;
import com.slib.utils.DateUtil;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/8/14.
 * Title：
 * Note：
 */
@SuppressLint("StringFormatMatches")
public abstract class BaseResumesAdapter extends BaseBZWAdapter {
    protected Context mContext;
    protected Resources mResources;
    protected List<ResumeBean> mResumeList;
    protected int mColorCompletionNormal;
    protected int mColorWhite;
    protected int mColorRead;
    protected int mColorUnReadName;
    protected int mColorUnReadTitle;
    protected int mCollectedBackground;
    protected int mUnCollectedBackground;
    protected int mColorTextNormal;
    protected Drawable mDrawableCollection;
    protected Drawable mDrawableUnCollection;
    private StringBuilder mStringBuilder = new StringBuilder();

    public BaseResumesAdapter(Context context, List<ResumeBean> resumeList, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = context;
        this.mResumeList = resumeList;
        this.mResources = context.getResources();
        this.mColorWhite = mResources.getColor(android.R.color.white);
        this.mColorRead = mResources.getColor(R.color.text_color_grey_94A1A5);
        this.mColorUnReadName = mResources.getColor(R.color.text_color_blue_0D315C);
        this.mColorUnReadTitle = mResources.getColor(R.color.text_color_black_4E5968);
        this.mColorCompletionNormal = mResources.getColor(R.color.text_color_orange_FF7700);
        this.mCollectedBackground = mResources.getColor(R.color.text_color_blue_53ABD5);
        this.mColorTextNormal = mResources.getColor(R.color.text_color_grey_94A1A5);
        this.mUnCollectedBackground = mResources.getColor(R.color.background_2053ABD5);

        mDrawableUnCollection = AppUtil.drawableInit(R.drawable.icon_collection_full_high_light, mResources);
        mDrawableCollection = AppUtil.drawableInit(R.drawable.icon_collection, mResources);
    }

    @Override
    public void onDestroy() {
        AppUtil.nonCallBackDrawable(mDrawableCollection);
        AppUtil.nonCallBackDrawable(mDrawableUnCollection);
    }

    @Override
    public int getCount() {
        return mResumeList == null ? 0 : mResumeList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(getLayoutId(), null);
            BaseResumeViewHolder viewHolder = getViewHolder(convertView);
            convertView.setTag(viewHolder);
            initOnGetView(viewHolder);
        }
        refreshItem(convertView, position);
        return convertView;
    }

    public abstract int getLayoutId();

    public abstract void initOnGetView(BaseResumeViewHolder viewHolder);

    public abstract BaseResumeViewHolder getViewHolder(View convertView);

    public void refreshItem(View convertView, int position) {
        ResumeBean resumeBean = mResumeList.get(position);
        BaseResumeViewHolder viewHolder = (BaseResumeViewHolder) convertView.getTag();
        setMainInfo(viewHolder, resumeBean, position);
        setSubInfo(viewHolder, resumeBean, position);
        setSideMenu(viewHolder, resumeBean, position);

        setItemReadStatus(viewHolder, resumeBean);
    }

    protected void setSideMenu(BaseResumeViewHolder viewHolder, ResumeBean resumeBean, int position) {
        if (viewHolder.view_sideMenuOne != null) {
            viewHolder.textView_sideMenuOneText.setText((resumeBean.collectStatus == CommonConst.COLLECTION_NO ? R.string.collection : R.string.un_collection));
            viewHolder.textView_sideMenuOneText.setCompoundDrawables(null, (resumeBean.collectStatus == CommonConst.COLLECTION_NO ? mDrawableUnCollection : mDrawableCollection), null, null);
            viewHolder.textView_sideMenuOneText.setTextColor((resumeBean.collectStatus == CommonConst.COLLECTION_NO ? mCollectedBackground : mColorWhite));
            viewHolder.view_sideMenuOne.setBackgroundColor((resumeBean.collectStatus == CommonConst.COLLECTION_NO ? mUnCollectedBackground : mCollectedBackground));
            if (viewHolder.view_sideMenuDepart != null)
                viewHolder.view_sideMenuDepart.setVisibility((resumeBean.collectStatus == CommonConst.COLLECTION_NO ? View.VISIBLE : View.GONE));
        }

    }

    protected void setMainInfo(BaseResumeViewHolder viewHolder, ResumeBean resumeBean, int position) {
        if (viewHolder.imageView_tagJobHunter != null)
            viewHolder.imageView_tagJobHunter.setVisibility((resumeBean.isJobHunting ? View.VISIBLE : View.GONE));
        viewHolder.textView_realName.setText(resumeBean.realName);
        viewHolder.textView_title.setText(mResources.getString(R.string.job_title, (TextUtils.isEmpty(resumeBean.title) ? mResources.getString(R.string.title_unknown) : resumeBean.title)));
        viewHolder.textView_secondMainInfo.setText(getShownSecondMainInfo(resumeBean));
        viewHolder.textView_companyInfo.setText(TextUtils.isEmpty(resumeBean.company) ? mResources.getString(R.string.company_info_unknown) : resumeBean.company);
    }

    protected void setSubInfo(BaseResumeViewHolder viewHolder, ResumeBean resumeBean, int position) {
        if (viewHolder.textView_subInfo == null)
            return;
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.resume_sub_info__no_owner_value, DateUtil.longMillions2FormatDate(resumeBean.sourceUpdateTime, DateUtil.SDF_YMD), mResources.getString((resumeBean.currentCompletion >= CommonConst.COMLPETION_HIGH_LEVEL ? R.string.completion_high : (resumeBean.currentCompletion >= CommonConst.COMLPETION_NORMAL_LEVEL ? R.string.completion_normal : R.string.completion_low)))));
        spannableString.setSpan(new ForegroundColorSpan(mColorCompletionNormal), 21, 22, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.textView_subInfo.setText(spannableString);
    }

    protected String getShownSecondMainInfo(ResumeBean resumeBean) {
        if (mStringBuilder.length() > 0)
            mStringBuilder.delete(0, mStringBuilder.length());
        if (AddressManager.getInstance().isCityCodeEnable(resumeBean.location))
            mStringBuilder.append(AddressManager.getInstance().getCityNameByCode(resumeBean.location)).append("/");
        if (FriendlyShowInfoManager.getInstance().isDegreeEnable(resumeBean.degree))
            mStringBuilder.append(FriendlyShowInfoManager.getInstance().getDegree(resumeBean.degree)).append("/");
        if (resumeBean.yearExpr > 0)
            mStringBuilder.append(mResources.getString(R.string.work_year_value, resumeBean.yearExpr)).append("/");
        if (resumeBean.gender == CommonConst.SEX_FEMALE || resumeBean.gender == CommonConst.SEX_MALE)
            mStringBuilder.append(mResources.getString(resumeBean.gender == CommonConst.SEX_FEMALE ? R.string.female : R.string.male)).append("/");
        if (mStringBuilder.length() > 0) {
            mStringBuilder.deleteCharAt(mStringBuilder.length() - 1);
            return mStringBuilder.toString();
        }
        return mResources.getString(R.string.common_info_unknown);
    }

    protected boolean isIgnoreOwnersCountInSubInfo() {
        return false;
    }

    protected void setItemReadStatus(BaseResumeViewHolder viewHolder, ResumeBean resumeBean) {
        boolean hasRead = (UserInfoManager.getInstance().isResumeReadByCurrentUser(resumeBean.candidateId));
        viewHolder.textView_realName.setTextColor(hasRead ? mColorRead : mColorUnReadName);
        viewHolder.textView_title.setTextColor(hasRead ? mColorRead : mColorUnReadTitle);
    }

    public static class BaseResumeViewHolder {
        public TextView textView_realName;
        public TextView textView_title;
        public TextView textView_secondMainInfo;
        public TextView textView_companyInfo;
        public TextView textView_subInfo;
        public TextView textView_operator;
        public TextView textView_sourceFrom;
        public ImageView imageView_tagJobHunter;
        //        public LineBreakLayout lineBreakLayout_labels;
        public ViewGroup view_sideMenuOne;
        public ViewGroup view_sideMenuTwo;
        public TextView textView_sideMenuOneText;
        public TextView textView_sideMenuTwoText;
        public View view_content;
        public View view_sideMenuDepart;
        public SwipeMenuLayout swipeMenuLayout;

        public BaseResumeViewHolder(View convertView) {
            swipeMenuLayout = convertView.findViewById(R.id.swipe_layout);
            imageView_tagJobHunter = convertView.findViewById(R.id.view_resume_ele_hunter_job);
            textView_realName = convertView.findViewById(R.id.view_resume_ele_real_name);
            textView_title = convertView.findViewById(R.id.view_resume_ele_title);
            textView_secondMainInfo = convertView.findViewById(R.id.view_resume_ele_second_main_info);
            textView_companyInfo = convertView.findViewById(R.id.view_resume_ele_company_info);
            textView_subInfo = convertView.findViewById(R.id.view_resume_ele_sub_info);
            textView_operator = convertView.findViewById(R.id.view_resume_ele_operator);
            textView_sourceFrom = convertView.findViewById(R.id.view_resume_ele_source_from);
//            lineBreakLayout_labels = convertView.findViewById(R.id.view_resume_ele_labels);
            view_sideMenuOne = convertView.findViewById(R.id.view_resume_ele_side_menu_one);
            view_sideMenuTwo = convertView.findViewById(R.id.view_resume_ele_side_menu_two);
            textView_sideMenuOneText = convertView.findViewById(R.id.view_resume_ele_side_menu_one_text);
            textView_sideMenuTwoText = convertView.findViewById(R.id.view_resume_ele_side_menu_two_text);
            view_content = convertView.findViewById(R.id.view_resume_ele_content);
            view_sideMenuDepart = convertView.findViewById(R.id.view_menu_depart);
        }
    }
}
