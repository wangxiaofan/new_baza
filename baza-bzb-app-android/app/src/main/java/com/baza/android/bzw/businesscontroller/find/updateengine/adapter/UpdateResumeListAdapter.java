package com.baza.android.bzw.businesscontroller.find.updateengine.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.resume.mine.adapter.MineResumeListAdapter;
import com.slib.utils.DateUtil;
import com.bznet.android.rcbox.R;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/8/23.
 * Title：
 * Note：
 */

public class UpdateResumeListAdapter extends MineResumeListAdapter {
    private boolean mEnableUpdateType;

    public UpdateResumeListAdapter(Context context, List<ResumeBean> resumeList, boolean enableUpdateType, IAdapterEventsListener adapterEventsListener) {
        super(context, resumeList, adapterEventsListener);
        this.mEnableUpdateType = enableUpdateType;
    }

    public UpdateResumeListAdapter(Context context, List<ResumeBean> resumeList, IAdapterEventsListener adapterEventsListener) {
        this(context, resumeList, false, adapterEventsListener);
    }

    @Override
    public void initOnGetView(BaseResumeViewHolder viewHolder) {
        viewHolder.view_content.setOnClickListener(this);
        viewHolder.swipeMenuLayout.setSwipeEnable(false);
    }

    @Override
    protected void setSubInfo(BaseResumeViewHolder viewHolder, ResumeBean resumeBean, int position) {
        if (mEnableUpdateType) {
            SpannableString spannableString = new SpannableString(mResources.getString(R.string.resume_update_sub_info, DateUtil.longMillions2FormatDate(resumeBean.sourceUpdateTime, DateUtil.SDF_YMD), String.valueOf((int)resumeBean.currentCompletion), String.valueOf((int)resumeBean.targetCompletion)));
            spannableString.setSpan(new ForegroundColorSpan(mColorCompletionNormal), spannableString.length() - 7, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.textView_subInfo.setText(spannableString);
        } else {
            SpannableString spannableString = new SpannableString(mResources.getString(R.string.resume_update_sub_info, DateUtil.longMillions2FormatDate(resumeBean.sourceUpdateTime, DateUtil.SDF_YMD_HM), String.valueOf((int)resumeBean.currentCompletion), String.valueOf((int)resumeBean.targetCompletion)));
            spannableString.setSpan(new ForegroundColorSpan(mColorCompletionNormal), 4, 22, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(mColorCompletionNormal), spannableString.length() - 7, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.textView_subInfo.setText(spannableString);
        }

    }

    @Override
    protected void setOperatorView(BaseResumeViewHolder viewHolder, ResumeBean resumeBean, int position) {
        if (!mEnableUpdateType) {
            viewHolder.textView_operator.setVisibility(View.GONE);
        } else {
            if (resumeBean.isUpdating()) {
                viewHolder.textView_operator.setOnClickListener(null);
                viewHolder.textView_operator.setText(R.string.updating);
                viewHolder.textView_operator.setTextColor(mColorTextNormal);
                viewHolder.textView_operator.setBackgroundDrawable(null);
                viewHolder.textView_operator.setVisibility(View.VISIBLE);
            } else if (resumeBean.isWaitUpdate()) {
                viewHolder.textView_operator.setOnClickListener(this);
                viewHolder.textView_operator.setTag(position);
                viewHolder.textView_operator.setText(R.string.update);
                viewHolder.textView_operator.setTextColor(mColorWhite);
                viewHolder.textView_operator.setBackgroundResource(R.drawable.name_list_update_btn_bg);
                viewHolder.textView_operator.setVisibility(View.VISIBLE);
            } else
                viewHolder.textView_operator.setVisibility(View.GONE);

        }

    }
}
