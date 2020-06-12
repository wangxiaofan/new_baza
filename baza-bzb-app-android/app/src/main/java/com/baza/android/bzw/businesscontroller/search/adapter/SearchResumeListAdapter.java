package com.baza.android.bzw.businesscontroller.search.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.resume.base.BaseResumesAdapter;
import com.baza.android.bzw.businesscontroller.resume.mine.adapter.MineResumeListAdapter;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.bznet.android.rcbox.R;
import com.slib.utils.DateUtil;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/22.
 * Title：
 * Note：
 */

public class SearchResumeListAdapter extends MineResumeListAdapter implements CompoundButton.OnCheckedChangeListener {
    private boolean mSortByCreateTime;
    private int mSearchType;
    private HashSet<String> mSelectedIdForGroupAdd;


    public SearchResumeListAdapter(Context context, int searchType, boolean sortByCreateTime, List<ResumeBean> resumeList, HashSet<String> selectedIdForGroupAdd, IAdapterEventsListener adapterEventsListener) {
        super(context, resumeList, adapterEventsListener);
        this.mSortByCreateTime = sortByCreateTime;
        this.mSearchType = searchType;
        this.mSelectedIdForGroupAdd = selectedIdForGroupAdd;

    }

    public SearchResumeListAdapter(Context context, int searchType, boolean sortByCreateTime, List<ResumeBean> resumeList, IAdapterEventsListener adapterEventsListener) {
        this(context, searchType, sortByCreateTime, resumeList, null, adapterEventsListener);
    }

    @Override
    public int getLayoutId() {
        return mSearchType == CommonConst.INT_SEARCH_TYPE_GROUP_ADD ? R.layout.talent_mine_add_to_group_adapter_item : R.layout.talent_mine_adapter_item;
    }

    @Override
    public void initOnGetView(BaseResumeViewHolder viewHolder) {
        viewHolder.view_content.setOnClickListener(this);
        viewHolder.view_sideMenuOne.setOnClickListener(this);
        viewHolder.view_sideMenuTwo.setOnClickListener(this);
        viewHolder.textView_operator.setOnClickListener(this);
        viewHolder.textView_sourceFrom.setVisibility(View.GONE);
    }

    public void refresh(boolean sortByCreateTime) {
        mSortByCreateTime = sortByCreateTime;
        notifyDataSetChanged();
    }

    @Override
    public BaseResumeViewHolder getViewHolder(View convertView) {
        return new ViewHolder(convertView);
    }

    @Override
    protected void setSubInfo(BaseResumeViewHolder viewHolder, ResumeBean resumeBean, int position) {
        SpannableString spannableString = new SpannableString(mResources.getString(mSortByCreateTime ? R.string.resume_create_time_no_owner_extra : R.string.resume_sub_info__no_owner_value, DateUtil.longMillions2FormatDate(mSortByCreateTime ? resumeBean.createTime : resumeBean.sourceUpdateTime, DateUtil.SDF_YMD), mResources.getString((resumeBean.currentCompletion >= CommonConst.COMLPETION_HIGH_LEVEL ? R.string.completion_high : (resumeBean.currentCompletion >= CommonConst.COMLPETION_NORMAL_LEVEL ? R.string.completion_normal : R.string.completion_low)))));
        spannableString.setSpan(new ForegroundColorSpan(mColorCompletionNormal), 21, 22, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.textView_subInfo.setText(spannableString);
    }


    @Override
    protected void setOperatorView(BaseResumeViewHolder viewHolder, ResumeBean resumeBean, int position) {
        super.setOperatorView(viewHolder, resumeBean, position);

        ViewHolder holder = (ViewHolder) viewHolder;
        if (holder.checkBox_selected != null && mSelectedIdForGroupAdd != null) {
            holder.checkBox_selected.setOnCheckedChangeListener(null);
            holder.checkBox_selected.setChecked(mSelectedIdForGroupAdd.contains(resumeBean.candidateId));
            holder.checkBox_selected.setOnCheckedChangeListener(this);
            holder.checkBox_selected.setTag(resumeBean);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_resume_ele_operator:
                if (v.getTag() == null)
                    return;
                int position = (int) v.getTag();
                ResumeBean resumeBean = mResumeList.get(position);
                sendAdapterEventToHost(AdapterEventIdConst.RESUME_LIST_SINGLE__UPDATE, position, null, resumeBean);
                break;
            case R.id.view_resume_ele_content:
                position = (int) v.getTag();
                sendAdapterEventToHost(AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL, position, null, mResumeList.get(position));
                break;
            case R.id.view_resume_ele_side_menu_one:
                BaseResumeViewHolder viewHolder = (BaseResumeViewHolder) v.getTag(R.id.hold_tag_id_two);
                viewHolder.swipeMenuLayout.smoothClose();
                position = (int) v.getTag();
                sendAdapterEventToHost(AdapterEventIdConst.RESUME_ADAPTER_EVENT_COLLECTION, position, null, mResumeList.get(position));
                break;
            case R.id.view_resume_ele_side_menu_two:
                viewHolder = (BaseResumeViewHolder) v.getTag(R.id.hold_tag_id_two);
                viewHolder.swipeMenuLayout.smoothClose();
                position = (int) v.getTag();
                sendAdapterEventToHost(AdapterEventIdConst.RESUME_ADAPTER_EVENT_ADD_REMARK, position, null, mResumeList.get(position));
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ResumeBean resumeBean = (ResumeBean) buttonView.getTag();
        if (isChecked)
            mSelectedIdForGroupAdd.add(resumeBean.candidateId);
        else
            mSelectedIdForGroupAdd.remove(resumeBean.candidateId);
        sendAdapterEventToHost(AdapterEventIdConst.ADAPTER_EVENT_ADD_RESUMNE_TO_GROUP, 0, null, mSelectedIdForGroupAdd.size());
    }

    public static class ViewHolder extends BaseResumesAdapter.BaseResumeViewHolder {
        CheckBox checkBox_selected;

        public ViewHolder(View convertView) {
            super(convertView);
            checkBox_selected = convertView.findViewById(R.id.cb_to_group);
        }
    }
}
