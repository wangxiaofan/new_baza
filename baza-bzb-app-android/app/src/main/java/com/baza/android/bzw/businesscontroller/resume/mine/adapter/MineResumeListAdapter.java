package com.baza.android.bzw.businesscontroller.resume.mine.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.resume.base.BaseResumesAdapter;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.ResumeDao;
import com.bznet.android.rcbox.R;
import com.slib.utils.DateUtil;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/22.
 * Title：
 * Note：
 */

public class MineResumeListAdapter extends BaseResumesAdapter implements View.OnClickListener {
    private boolean mSeekMode;

    public MineResumeListAdapter(Context context, List<ResumeBean> resumeList, IAdapterEventsListener adapterEventsListener) {
        super(context, resumeList, adapterEventsListener);
    }

    public MineResumeListAdapter(Context context, List<ResumeBean> resumeList, boolean seekMode, IAdapterEventsListener adapterEventsListener) {
        super(context, resumeList, adapterEventsListener);
        mSeekMode = seekMode;
    }

    @Override
    public void initOnGetView(BaseResumeViewHolder viewHolder) {
        viewHolder.view_content.setOnClickListener(this);
        viewHolder.view_sideMenuOne.setOnClickListener(this);
        viewHolder.view_sideMenuTwo.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.talent_mine_adapter_item;
    }

    @Override
    public BaseResumeViewHolder getViewHolder(View convertView) {
        return new BaseResumeViewHolder(convertView);
    }

    @Override
    public void refreshItem(View convertView, int position) {
        BaseResumeViewHolder viewHolder = (BaseResumeViewHolder) convertView.getTag();
        ResumeBean resumeBean = mResumeList.get(position);
        setMainInfo(viewHolder, resumeBean, position);
        setSubInfo(viewHolder, resumeBean, position);
        viewHolder.view_content.setTag(position);
        String source = ResumeDao.getSourceForShow(resumeBean);
        if (source != null) {
            viewHolder.textView_sourceFrom.setVisibility(View.VISIBLE);
            viewHolder.textView_sourceFrom.setText(source);
            viewHolder.textView_title.setMaxEms(CommonConst.MAX_LIST_JOB_EMS_SHORT);
        } else {
            viewHolder.textView_title.setMaxEms(CommonConst.MAX_LIST_JOB_EMS_LONG);
            viewHolder.textView_sourceFrom.setVisibility(View.GONE);
        }

        setSideMenu(viewHolder, resumeBean, position);
        setOperatorView(viewHolder, resumeBean, position);
        setItemReadStatus(viewHolder, resumeBean);
    }

    @Override
    protected void setSubInfo(BaseResumeViewHolder viewHolder, ResumeBean resumeBean, int position) {
        if (!mSeekMode) {
            super.setSubInfo(viewHolder, resumeBean, position);
            return;
        }
        if (viewHolder.textView_subInfo == null)
            return;
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.resume_sub_info_seek_no_owner_value, DateUtil.longMillions2FormatDate(resumeBean.inquiryUpdateTime, DateUtil.SDF_YMD), mResources.getString((resumeBean.currentCompletion >= CommonConst.COMLPETION_HIGH_LEVEL ? R.string.completion_high : (resumeBean.currentCompletion >= CommonConst.COMLPETION_NORMAL_LEVEL ? R.string.completion_normal : R.string.completion_low)))));
        spannableString.setSpan(new ForegroundColorSpan(mColorCompletionNormal), 21, 22, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.textView_subInfo.setText(spannableString);
    }

    @Override
    protected boolean isIgnoreOwnersCountInSubInfo() {
        return true;
    }

    @Override
    protected void setSideMenu(BaseResumeViewHolder viewHolder, ResumeBean resumeBean, int position) {
        super.setSideMenu(viewHolder, resumeBean, position);
        viewHolder.view_sideMenuOne.setTag(position);
        viewHolder.view_sideMenuOne.setTag(R.id.hold_tag_id_two, viewHolder);
        viewHolder.view_sideMenuTwo.setTag(position);
        viewHolder.view_sideMenuTwo.setTag(R.id.hold_tag_id_two, viewHolder);
    }

    protected void setOperatorView(BaseResumeViewHolder viewHolder, ResumeBean resumeBean, int position) {
        if (resumeBean.isWaitUpdate()) {
            viewHolder.textView_operator.setOnClickListener(this);
            viewHolder.textView_operator.setVisibility(View.VISIBLE);
            viewHolder.textView_operator.setTag(position);
        } else {
            viewHolder.textView_operator.setVisibility(View.GONE);
            viewHolder.textView_operator.setTag(null);
        }
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
