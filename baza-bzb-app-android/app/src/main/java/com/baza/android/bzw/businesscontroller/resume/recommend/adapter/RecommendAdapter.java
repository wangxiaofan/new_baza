package com.baza.android.bzw.businesscontroller.resume.recommend.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.base.BZWDataAdapter;
import com.baza.android.bzw.base.BaseAdapterViewHolder;
import com.baza.android.bzw.bean.recommend.RecommendBean;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.bznet.android.rcbox.R;
import com.slib.utils.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by Vincent.Lei on 2019/8/15.
 * Title：
 * Note：
 */
public class RecommendAdapter extends BZWDataAdapter<RecommendBean> implements View.OnClickListener {
    private String mCurrentDateStr;
    private int mColorDateToday, mColorDateNormal;
    private int mColorComplete, mColorDelay;

    public RecommendAdapter(Context context, List<RecommendBean> dataList, IAdapterEventsListener adapterEventsListener) {
        super(context, dataList, adapterEventsListener);
        this.mResources = context.getResources();
        this.mCurrentDateStr = DateUtil.longMillions2FormatDate(new Date().getTime(), DateUtil.SDF_RECOMMEND).substring(0, 10);
        this.mColorDateToday = mResources.getColor(R.color.text_color_black_4E5968);
        this.mColorDateNormal = mResources.getColor(R.color.text_color_gray_a9adb3);
        this.mColorComplete = mResources.getColor(R.color.text_color_blue_53ABD5);
        this.mColorDelay = mResources.getColor(R.color.color_red_FF6564);
    }

    @Override
    protected View getConvertView(int position) {
        return LayoutInflater.from(mContext).inflate(R.layout.recommend_adapter_list_item, null);
    }

    @Override
    protected BaseAdapterViewHolder getViewHolder(int position, View convertView) {
        ViewHolder viewHolder = new ViewHolder(convertView);
        viewHolder.textView_complete.setOnClickListener(this);
        viewHolder.textView_delete.setOnClickListener(this);
        viewHolder.convertView.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    protected void setUpItemView(BaseAdapterViewHolder bvh, int position) {
        ViewHolder viewHolder = (ViewHolder) bvh;
        RecommendBean recommend = mDataList.get(position);
        viewHolder.textView_name.setText(recommend.realName);
        if (TextUtils.isEmpty(recommend.remindTime)) {
            viewHolder.textView_date.setText(null);
        } else if (recommend.remindTime.startsWith(mCurrentDateStr)) {
            viewHolder.textView_date.setText(mResources.getString(R.string.recommend_date_today, recommend.remindTime.substring(10, 16)));
            viewHolder.textView_date.setTextColor(mColorDateToday);
        } else {
            viewHolder.textView_date.setText(mResources.getString(R.string.recommend_date_show, recommend.remindTime.substring(5, 16)));
            viewHolder.textView_date.setTextColor(mColorDateNormal);
        }

        viewHolder.textView_status.setVisibility(recommend.status == RecommendBean.STATE_NORMAL ? View.GONE : View.VISIBLE);
        if (recommend.status != RecommendBean.STATE_NORMAL) {
            viewHolder.textView_status.setText(recommend.status == RecommendBean.STATE_COMPLETE ? R.string.recommend_state_complete : R.string.recommend_state_delay);
            viewHolder.textView_status.setTextColor(recommend.status == RecommendBean.STATE_COMPLETE ? mColorComplete : mColorDelay);
        }
        if (recommend.status == RecommendBean.STATE_COMPLETE && !TextUtils.isEmpty(recommend.content)) {
            SpannableString spannableString = new SpannableString(recommend.content);
            spannableString.setSpan(new StrikethroughSpan(), 0, recommend.content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.textView_content.setText(spannableString);
        } else
            viewHolder.textView_content.setText(recommend.content);
        viewHolder.textView_complete.setVisibility(recommend.status == RecommendBean.STATE_COMPLETE ? View.GONE : View.VISIBLE);

        viewHolder.textView_complete.setTag(position);
        viewHolder.textView_delete.setTag(position);
        viewHolder.convertView.setTag(R.id.hold_tag_id_one, position);
        viewHolder.convertView.setBackgroundResource(position == mDataList.size() - 1 ? R.drawable.selector_background_click_default : R.drawable.selector_background_click_default_with_bottom_line);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_complete:
                int position = (int) v.getTag();
                sendAdapterEventToHost(AdapterEventIdConst.EVENT_ID_RECOMMEND_COMPLETE, position, null, mDataList.get(position));
                break;
            case R.id.tv_delete:
                position = (int) v.getTag();
                sendAdapterEventToHost(AdapterEventIdConst.EVENT_ID_RECOMMEND_DELETE, position, null, mDataList.get(position));
                break;
            default:
                position = (int) v.getTag(R.id.hold_tag_id_one);
                sendAdapterEventToHost(AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL, position, null, mDataList.get(position));
                break;
        }
    }

    private static class ViewHolder extends BaseAdapterViewHolder {
        TextView textView_name;
        TextView textView_date;
        TextView textView_status;
        TextView textView_content;
        TextView textView_complete;
        TextView textView_delete;

        ViewHolder(View convertView) {
            super(convertView);
            textView_name = convertView.findViewById(R.id.tv_name);
            textView_date = convertView.findViewById(R.id.tv_time);
            textView_status = convertView.findViewById(R.id.tv_status);
            textView_content = convertView.findViewById(R.id.tv_content);
            textView_complete = convertView.findViewById(R.id.tv_complete);
            textView_delete = convertView.findViewById(R.id.tv_delete);
        }
    }
}
