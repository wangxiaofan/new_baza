package com.baza.android.bzw.businesscontroller.company.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.manager.AddressManager;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.baza.android.bzw.widget.CircleImageView;
import com.bznet.android.rcbox.R;
import com.slib.utils.DateUtil;
import com.slib.utils.LoadImageUtil;
import com.slib.utils.string.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/22.
 * Title：
 * Note：
 */

public class CompanyResumeListAdapter extends BaseBZWAdapter implements View.OnClickListener {

    private Context mContext;
    private List<ResumeBean> mResumeList;
    private StringBuilder mStringBuilder = new StringBuilder();
    private StringBuilder companyBuilder = new StringBuilder();
    private StringBuilder collegeBuilder = new StringBuilder();
    private StringBuilder timeBuilder = new StringBuilder();
    private Resources mResources;
    private int mColorRead;
    private int mColorUnReadName;
    private boolean mShowType;
    private boolean mShowCollect;

    public CompanyResumeListAdapter(Context context, List<ResumeBean> resumeList, boolean showType, boolean showCollect, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = context;
        this.mResumeList = resumeList;
        this.mResources = context.getResources();
        this.mColorRead = mResources.getColor(R.color.text_color_grey_94A1A5);
        this.mColorUnReadName = mResources.getColor(R.color.text_color_blue_0D315C);
        this.mShowType = showType;
        this.mShowCollect = showCollect;
    }

    public void refreshItem(View convertView, int position) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        ResumeBean resumeBean = mResumeList.get(position);
        setData(holder, resumeBean);
    }

    @Override
    public int getCount() {
        return mResumeList == null ? 0 : mResumeList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResumeBean resumeBean = mResumeList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_company_resume_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.resume_layout.setTag(position);
        holder.iv_collection.setTag(position);
        holder.resume_layout.setOnClickListener(this);
        holder.iv_collection.setOnClickListener(this);

        refreshItem(convertView, position);
        return convertView;
    }

    private void setData(ViewHolder holder, ResumeBean resumeBean) {
        holder.tv_name.setText(resumeBean.realName);
        holder.tv_integrity.setText("" + resumeBean.currentCompletion);
        holder.tv_update_time.setText(DateUtil.longMillions2FormatDate(resumeBean.sourceUpdateTime, DateUtil.SDF_YMD) + "更新");
        holder.tv_user_info.setText(getShownSecondMainInfo(resumeBean));
        holder.tv_company_info.setText(getCompanyInfo(resumeBean));
        holder.tv_position.setText(getTimeInfo(resumeBean));
        holder.tv_college_info.setText(getCollegeInfo(resumeBean));

        if (resumeBean.isJobHunting) {
            holder.iv_flag.setVisibility(View.VISIBLE);
        } else {
            holder.iv_flag.setVisibility(View.GONE);
        }

        if (resumeBean.owner != null && resumeBean.owner.avatar != null && !StringUtil.isEmpty(resumeBean.owner.avatar)) {
            LoadImageUtil.loadBase64Image(resumeBean.owner.avatar, R.drawable.avatar_def, holder.user_head);
        } else if (resumeBean.owner != null && resumeBean.owner.defaultAvatar != null && !StringUtil.isEmpty(resumeBean.owner.defaultAvatar)) {
            LoadImageUtil.loadBase64Image("", R.drawable.avatar_def, holder.user_head);
        } else {
            holder.user_head.setBackgroundResource(R.drawable.avatar_def);
        }

        if (resumeBean.owner != null && resumeBean.owner.userName != null) {
            holder.tv_user_name.setText(resumeBean.owner.userName);
        } else {
            holder.tv_user_name.setText("");
        }

        if (mShowCollect) {
            holder.iv_collection.setVisibility(View.VISIBLE);
            if (resumeBean.collectStatus == 0) {
                holder.iv_collection.setImageResource(R.drawable.btn_shoucang_nor);
            } else {
                holder.iv_collection.setImageResource(R.drawable.btn_shoucang_sel);
            }
        } else {
            holder.iv_collection.setVisibility(View.GONE);
        }

        if (mShowType) {
            holder.tv_resumen_type.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(resumeBean.firmId)) {
                holder.tv_resumen_type.setText("私");
                holder.tv_resumen_type.setBackgroundResource(R.drawable.bg_resume_type_mine);
            } else {
                holder.tv_resumen_type.setText("企");
                holder.tv_resumen_type.setBackgroundResource(R.drawable.bg_resume_type_company);
            }
        } else {
            holder.tv_resumen_type.setVisibility(View.GONE);
        }

        boolean hasRead = (UserInfoManager.getInstance().isResumeReadByCurrentUser(resumeBean.candidateId));
        holder.tv_name.setTextColor(hasRead ? mColorRead : mColorUnReadName);
        if (hasRead || resumeBean.scanStatus == 1) {
            holder.tv_read_status.setText("已浏览");
        } else {
            holder.tv_read_status.setText("");
        }
    }

    class ViewHolder {
        LinearLayout resume_layout;
        TextView tv_name;
        TextView tv_integrity;
        TextView tv_update_time;
        TextView tv_read_status;
        TextView tv_user_info;
        TextView tv_company_info;
        TextView tv_position;
        TextView tv_college_info;
        ImageView iv_flag;
        ImageView iv_collection;
        CircleImageView user_head;
        TextView tv_user_name;
        TextView tv_resumen_type;

        public ViewHolder(View itemView) {
            resume_layout = itemView.findViewById(R.id.resume_layout);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_integrity = itemView.findViewById(R.id.tv_integrity);
            tv_update_time = itemView.findViewById(R.id.tv_update_time);
            tv_read_status = itemView.findViewById(R.id.tv_read_status);
            tv_user_info = itemView.findViewById(R.id.tv_user_info);
            tv_company_info = itemView.findViewById(R.id.tv_company_info);
            tv_position = itemView.findViewById(R.id.tv_position);
            tv_college_info = itemView.findViewById(R.id.tv_college_info);
            iv_flag = itemView.findViewById(R.id.iv_flag);
            iv_collection = itemView.findViewById(R.id.iv_collection);
            user_head = itemView.findViewById(R.id.user_head);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_resumen_type = itemView.findViewById(R.id.tv_resumen_type);
        }
    }

    /**
     * 组装个人信息
     *
     * @param resumeBean
     * @return
     */
    private String getShownSecondMainInfo(ResumeBean resumeBean) {
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
        if (resumeBean.birthday != null) {
            try {
                String[] birthday = resumeBean.birthday.split("-");
                int age = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date())) - Integer.parseInt(birthday[0]);
                mStringBuilder.append(age).append("岁/");
            } catch (Exception e) {
            }
        }
        if (mStringBuilder.length() > 0) {
            mStringBuilder.deleteCharAt(mStringBuilder.length() - 1);
            return mStringBuilder.toString();
        }
        return mResources.getString(R.string.common_info_unknown);
    }

    /**
     * 组装公司信息
     *
     * @param resumeBean
     * @return
     */
    private String getCompanyInfo(ResumeBean resumeBean) {
        if (companyBuilder.length() > 0) {
            companyBuilder.delete(0, companyBuilder.length());
        }
        if (TextUtils.isEmpty(resumeBean.company)) {
            companyBuilder.append("公司信息暂无");
        } else {
            companyBuilder.append(resumeBean.company.length() > 8 ? resumeBean.company.substring(0, 8) + "..." : resumeBean.company);
        }
        companyBuilder.append("/");
        if (TextUtils.isEmpty(resumeBean.title)) {
            companyBuilder.append("职位信息暂无");
        } else {
            companyBuilder.append(resumeBean.title.length() > 8 ? resumeBean.title.substring(0, 8) + "..." : resumeBean.title);
        }
        return companyBuilder.toString();
    }

    /**
     * 组装学校信息
     *
     * @param resumeBean
     * @return
     */
    private String getCollegeInfo(ResumeBean resumeBean) {
        if (collegeBuilder.length() > 0) {
            collegeBuilder.delete(0, collegeBuilder.length());
        }
        if (TextUtils.isEmpty(resumeBean.school)) {
            collegeBuilder.append("学校信息暂无");
        } else {
            collegeBuilder.append(resumeBean.school.length() > 8 ? resumeBean.school.substring(0, 8) + "..." : resumeBean.school);
        }
        collegeBuilder.append("/");
        if (TextUtils.isEmpty(resumeBean.major)) {
            collegeBuilder.append("专业信息暂无");
        } else {
            collegeBuilder.append(resumeBean.major.length() > 8 ? resumeBean.major.substring(0, 8) + "..." : resumeBean.major);
        }
        return collegeBuilder.toString();
    }

    /**
     * 组装时间
     *
     * @param resumeBean
     * @return
     */
    private String getTimeInfo(ResumeBean resumeBean) {
        if (timeBuilder.length() > 0) {
            timeBuilder.delete(0, timeBuilder.length());
        }

        //垃圾服务端
        if (resumeBean.workStartDate.startsWith("0001")) {
            timeBuilder.append("未知");
        } else if (!TextUtils.isEmpty(resumeBean.workStartDate)) {
            timeBuilder.append(resumeBean.workStartDate.length() > 7 ? resumeBean.workStartDate.substring(0, 7).replace("-", ".") : resumeBean.workStartDate.replace("-", "."));
        } else {
            timeBuilder.append("未知");
        }

        timeBuilder.append("-");
        if (resumeBean.workEndDate.startsWith("0001")) {
            timeBuilder.append("未知");
        } else if (TextUtils.isEmpty(resumeBean.workEndDate) || resumeBean.workEndDate.startsWith("9999")) {
            timeBuilder.append("至今");
        } else {
            timeBuilder.append(resumeBean.workEndDate.length() > 7 ? resumeBean.workEndDate.substring(0, 7).replace("-", ".") : resumeBean.workEndDate.replace("-", "."));
        }
        return timeBuilder.toString();
    }

    @Override
    public void onClick(View v) {
        int position;
        switch (v.getId()) {
            case R.id.resume_layout:
                position = (int) v.getTag();
                sendAdapterEventToHost(AdapterEventIdConst.EVENT_ID_COMPANY_ITEM_CLICK, position, null, mResumeList.get(position));
                break;
            case R.id.iv_collection:
                position = (int) v.getTag();
                sendAdapterEventToHost(AdapterEventIdConst.EVENT_ID_COMPANY_ITEM_COLLECTION, position, null, mResumeList.get(position));
                break;
        }
    }
}
