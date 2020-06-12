package com.baza.android.bzw.businesscontroller.resume.jobfind.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.resume.mine.adapter.MineResumeListAdapter;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.slib.utils.DateUtil;
import com.bznet.android.rcbox.R;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/8/23.
 * Title：
 * Note：
 */
public class JobHunterPredictionAdapter extends MineResumeListAdapter {
    static final int VIEW_TYPE_COUNT = 2;
    static final int TYPE_NORMAL = 0;
    static final int TYPE_TIPS = 1;
    private int mColorAddRecord;
    private int mNowPredictionCount;
    private int mPreviousPredictionCount;

    public JobHunterPredictionAdapter(Context context, List<ResumeBean> resumeList, int nowPredictionCount, int previousPredictionCount, IAdapterEventsListener adapterEventsListener) {
        super(context, resumeList, adapterEventsListener);
        this.mColorAddRecord = mResources.getColor(R.color.text_color_blue_53ABD5);
        this.mNowPredictionCount = nowPredictionCount;
        this.mPreviousPredictionCount = previousPredictionCount;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return mResumeList.get(position) == null ? TYPE_TIPS : TYPE_NORMAL;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (type == TYPE_TIPS) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.job_hunter_prediction_adapter_item_tips, null);
                TipsViewHolder viewHolder = new TipsViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
        } else {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.talent_mine_adapter_item, null);
                BaseResumeViewHolder viewHolder = new BaseResumeViewHolder(convertView);
                convertView.setTag(viewHolder);
                viewHolder.textView_operator.setOnClickListener(this);
                viewHolder.view_content.setOnClickListener(this);
                viewHolder.view_sideMenuOne.setOnClickListener(this);
                viewHolder.view_sideMenuTwo.setVisibility(View.GONE);
                viewHolder.textView_operator.setText(R.string.add_record);
                viewHolder.textView_operator.setTextColor(mColorAddRecord);
                viewHolder.textView_operator.setBackgroundResource(R.drawable.job_hunter_add_record_btn_bg);
            }
        }
        refreshItem(convertView, position);
        return convertView;
    }

    public void refresh(int nowPredictionCount, int previousPredictionCount, View convertView, int position) {
        this.mNowPredictionCount = nowPredictionCount;
        this.mPreviousPredictionCount = previousPredictionCount;
        if (position == CommonConst.LIST_POSITION_NONE)
            notifyDataSetChanged();
        else
            refreshItem(convertView, position);
    }

    @Override
    protected void setSubInfo(BaseResumeViewHolder viewHolder, ResumeBean resumeBean, int position) {
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.job_hunter_previous_sub_info, mResources.getString((resumeBean.currentCompletion >= CommonConst.COMLPETION_HIGH_LEVEL ? R.string.completion_high : (resumeBean.currentCompletion >= CommonConst.COMLPETION_NORMAL_LEVEL ? R.string.completion_normal : R.string.completion_low))), DateUtil.longMillions2FormatDate(resumeBean.jobHuntingExpiryTime, DateUtil.SDF_YMD_HM)));
        spannableString.setSpan(new ForegroundColorSpan(mColorCompletionNormal), 3, 5, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(mColorCompletionNormal), spannableString.length() - 17, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.textView_subInfo.setText(spannableString);
    }

    @Override
    public void refreshItem(View convertView, int position) {
        if (getItemViewType(position) == TYPE_NORMAL)
            super.refreshItem(convertView, position);
        else {
            TipsViewHolder tipsViewHolder = (TipsViewHolder) convertView.getTag();
            if (position == 0) {
                tipsViewHolder.textView_tips.setBackgroundResource(R.drawable.job_hunter_now_prediction_tip_bg);
                SpannableString spannableString = new SpannableString(mResources.getString(mNowPredictionCount == 0 ? R.string.job_hunter_now_previous_count : R.string.job_hunter_now_previous_count_with_refresh, mNowPredictionCount));
                spannableString.setSpan(new ForegroundColorSpan(mColorCompletionNormal), 8, spannableString.length() - (mNowPredictionCount == 0 ? 1 : 6), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (mNowPredictionCount > 0) {
                    spannableString.setSpan(new ForegroundColorSpan(mColorAddRecord), spannableString.length() - 4, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            sendAdapterEventToHost(AdapterEventIdConst.ADAPTER_EVENT_REFRESH_NOW_PREDICTION, 0, null, null);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setColor(mColorAddRecord);
                            ds.setUnderlineText(false);
                            ds.clearShadowLayer();
                        }
                    }, spannableString.length() - 4, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                tipsViewHolder.textView_tips.setMovementMethod(LinkMovementMethod.getInstance());
                tipsViewHolder.textView_tips.setText(spannableString);
            } else {
                tipsViewHolder.textView_tips.setBackgroundResource(R.drawable.job_hunter_previous_prediction_tip_bg);
                tipsViewHolder.textView_tips.setText(mResources.getString(R.string.job_hunter_prediction_previous_count, mPreviousPredictionCount));
            }

        }
    }

    @Override
    protected void setOperatorView(BaseResumeViewHolder viewHolder, ResumeBean resumeBean, int position) {
        viewHolder.textView_operator.setVisibility(View.VISIBLE);
        viewHolder.textView_operator.setTag(position);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.view_resume_ele_operator) {
            int position = (int) v.getTag();
            sendAdapterEventToHost(AdapterEventIdConst.RESUME_ADAPTER_EVENT_ADD_REMARK, position, null, mResumeList.get(position));
            return;
        }
        super.onClick(v);
    }

    private static class TipsViewHolder {
        TextView textView_tips;

        TipsViewHolder(View convertView) {
            textView_tips = convertView.findViewById(R.id.tv_tips);
        }
    }
}
