package com.baza.android.bzw.businesscontroller.account.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.resume.mine.adapter.MineResumeListAdapter;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.bznet.android.rcbox.R;
import com.slib.utils.DateUtil;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/22.
 * Title：
 * Note：
 */

public class ResumeCollectionListAdapter extends MineResumeListAdapter {

    public ResumeCollectionListAdapter(Context context, List<ResumeBean> resumeList, IAdapterEventsListener adapterEventsListener) {
        super(context, resumeList, adapterEventsListener);
    }

    @Override
    public void initOnGetView(BaseResumeViewHolder viewHolder) {
        viewHolder.view_content.setOnClickListener(this);
        viewHolder.textView_operator.setOnClickListener(this);
        viewHolder.view_sideMenuOne.setOnClickListener(this);
        viewHolder.view_sideMenuTwo.setOnClickListener(this);
    }

    @Override
    public void refreshItem(View convertView, int position) {
        BaseResumeViewHolder viewHolder = (BaseResumeViewHolder) convertView.getTag();
        ResumeBean resumeBean = mResumeList.get(position);
        setMainInfo(viewHolder, resumeBean, position);
        setSubInfo(viewHolder, resumeBean, position);

        viewHolder.view_content.setTag(position);
        viewHolder.textView_sourceFrom.setVisibility(View.GONE);
        viewHolder.textView_sideMenuOneText.setText(R.string.un_collection);
        viewHolder.view_sideMenuOne.setTag(position);
        viewHolder.view_sideMenuOne.setTag(R.id.hold_tag_id_two, viewHolder);
        viewHolder.view_sideMenuTwo.setTag(position);
        viewHolder.view_sideMenuTwo.setTag(R.id.hold_tag_id_two, viewHolder);
        if (resumeBean.isWaitUpdate()) {
            viewHolder.textView_operator.setOnClickListener(this);
            viewHolder.textView_operator.setVisibility(View.VISIBLE);
            viewHolder.textView_operator.setTag(position);
        } else {
            viewHolder.textView_operator.setVisibility(View.GONE);
            viewHolder.textView_operator.setTag(null);
        }

        setItemReadStatus(viewHolder, resumeBean);
    }

    @Override
    protected void setSubInfo(BaseResumeViewHolder viewHolder, ResumeBean resumeBean, int position) {
        if (viewHolder.textView_subInfo == null)
            return;
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.resume_sub_info_collection_no_owner_value, DateUtil.longMillions2FormatDate(resumeBean.collectedCreateTime, DateUtil.SDF_YMD), mResources.getString((resumeBean.currentCompletion >= CommonConst.COMLPETION_HIGH_LEVEL ? R.string.completion_high : (resumeBean.currentCompletion >= CommonConst.COMLPETION_NORMAL_LEVEL ? R.string.completion_normal : R.string.completion_low)))));
        spannableString.setSpan(new ForegroundColorSpan(mColorCompletionNormal), 21, 22, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.textView_subInfo.setText(spannableString);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_resume_ele_operator:
                int position = (int) v.getTag();
                sendAdapterEventToHost(AdapterEventIdConst.RESUME_LIST_SINGLE__UPDATE, position, null, mResumeList.get(position));
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
}
