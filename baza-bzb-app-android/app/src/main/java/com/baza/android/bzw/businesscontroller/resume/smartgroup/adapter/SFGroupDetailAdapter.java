package com.baza.android.bzw.businesscontroller.resume.smartgroup.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.resume.mine.adapter.MineResumeListAdapter;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.bznet.android.rcbox.R;
import com.slib.utils.DateUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Vincent.Lei on 2018/8/31.
 * Title：
 * Note：
 */
public class SFGroupDetailAdapter extends MineResumeListAdapter implements CompoundButton.OnCheckedChangeListener {
    private boolean mOnEdit;
    private int mType;
    private boolean mSortByCreateTime;
    private HashMap<String, ResumeBean> mSelectedResumes;

    public SFGroupDetailAdapter(Context context, int type, List<ResumeBean> resumeList, HashMap<String, ResumeBean> selectedResumes, IAdapterEventsListener adapterEventsListener) {
        super(context, resumeList, adapterEventsListener);
        this.mType = type;
        this.mSelectedResumes = selectedResumes;
        this.mSortByCreateTime = (mType == CommonConst.SmartGroupType.GROUP_TYPE_TIME);
    }

    public boolean isOnEdit() {
        return mOnEdit;
    }

    public void onEditModeChanged() {
        this.mOnEdit = !mOnEdit;
        notifyDataSetChanged();
    }

    @Override
    public void initOnGetView(BaseResumeViewHolder viewHolder) {
        viewHolder.view_content.setOnClickListener(this);
        viewHolder.view_sideMenuOne.setOnClickListener(this);
        if (viewHolder.view_sideMenuTwo != null)
            viewHolder.view_sideMenuTwo.setOnClickListener(this);
    }

    @Override
    public BaseResumeViewHolder getViewHolder(View convertView) {
        return new ViewHolder(convertView);
    }

    @Override
    public int getLayoutId() {
        return (mType == CommonConst.SmartGroupType.GROUP_TYPE_CUSTOMER ? R.layout.smart_group_sf_adapter_item : super.getLayoutId());
    }


    @Override
    public void refreshItem(View convertView, int position) {
        super.refreshItem(convertView, position);
        if (mType == CommonConst.SmartGroupType.GROUP_TYPE_CUSTOMER && mSelectedResumes != null) {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.checkBox_selected.setVisibility(mOnEdit ? View.VISIBLE : View.GONE);
            viewHolder.checkBox_selected.setOnCheckedChangeListener(null);
            viewHolder.checkBox_selected.setChecked(mSelectedResumes.containsKey(mResumeList.get(position).candidateId));
            viewHolder.checkBox_selected.setOnCheckedChangeListener(this);
            viewHolder.checkBox_selected.setTag(mResumeList.get(position));
        }

    }

    @Override
    protected void setSubInfo(BaseResumeViewHolder viewHolder, ResumeBean resumeBean, int position) {
        SpannableString spannableString = new SpannableString(mResources.getString(mSortByCreateTime ? R.string.resume_create_time_no_owner_extra : R.string.resume_sub_info__no_owner_value, DateUtil.longMillions2FormatDate(resumeBean.createTime, DateUtil.SDF_YMD), mResources.getString((resumeBean.currentCompletion >= CommonConst.COMLPETION_HIGH_LEVEL ? R.string.completion_high : (resumeBean.currentCompletion >= CommonConst.COMLPETION_NORMAL_LEVEL ? R.string.completion_normal : R.string.completion_low)))));
        spannableString.setSpan(new ForegroundColorSpan(mColorCompletionNormal), 21, 22, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.textView_subInfo.setText(spannableString);
    }

    @Override
    protected void setSideMenu(BaseResumeViewHolder viewHolder, ResumeBean resumeBean, int position) {
        if (mType != CommonConst.SmartGroupType.GROUP_TYPE_CUSTOMER)
            super.setSideMenu(viewHolder, resumeBean, position);
        else {
            viewHolder.view_sideMenuOne.setTag(position);
            viewHolder.view_sideMenuOne.setTag(R.id.hold_tag_id_two, viewHolder);
        }
    }

    @Override
    public void onClick(View v) {
        if (mType == CommonConst.SmartGroupType.GROUP_TYPE_CUSTOMER && v.getId() == R.id.view_resume_ele_side_menu_one) {
            //移除
            ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.hold_tag_id_two);
            viewHolder.swipeMenuLayout.smoothClose();
            int position = (int) v.getTag();
            sendAdapterEventToHost(AdapterEventIdConst.ADAPTER_EVENT_REMOVE_RESUMNE_FROM_GROUP, position, v, mResumeList.get(position));
            return;
        }
        super.onClick(v);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ResumeBean resumeBean = (ResumeBean) buttonView.getTag();
        if (isChecked)
            mSelectedResumes.put(resumeBean.candidateId, resumeBean);
        else
            mSelectedResumes.remove(resumeBean.candidateId);
        sendAdapterEventToHost(AdapterEventIdConst.ADAPTER_EVENT_ADD_RESUMNE_TO_GROUP, 0, null, mSelectedResumes.size());
    }


    private static class ViewHolder extends BaseResumeViewHolder {
        CheckBox checkBox_selected;

        public ViewHolder(View convertView) {
            super(convertView);
            checkBox_selected = convertView.findViewById(R.id.cb_to_group);
        }
    }
}
