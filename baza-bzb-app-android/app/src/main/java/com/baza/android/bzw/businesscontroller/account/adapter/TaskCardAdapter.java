package com.baza.android.bzw.businesscontroller.account.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.taskcard.TaskBean;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vincent.Lei on 2018/11/29.
 * Title：
 * Note：
 */
public class TaskCardAdapter extends BaseBZWAdapter implements View.OnClickListener {
    private Context mContext;
    private List<TaskBean> mDataList;
    private Resources mResources;
    private int mColorOperateNormal, mColorOperateDone, mColorHighLight;
    private Pattern mPattern;
    private int mTextSizeHighLight;
    private int mPaddingTB;

    public TaskCardAdapter(Context context, List<TaskBean> dataList, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = context;
        this.mDataList = dataList;
        this.mResources = mContext.getResources();
        this.mColorOperateNormal = mResources.getColor(R.color.text_color_blue_53ABD5);
        this.mColorOperateDone = mResources.getColor(R.color.text_color_grey_9E9E9E);
        this.mColorHighLight = mResources.getColor(R.color.text_color_orange_FF7700);
        this.mPattern = Pattern.compile("(\\+{0,1}[0-9].[0-9])|(\\+{0,1}[0-9]+)");
        this.mTextSizeHighLight = ScreenUtil.px2dip(mResources.getDimension(R.dimen.text_size_12));
        this.mPaddingTB = (int) mResources.getDimension(R.dimen.dp_10);
    }

    @Override
    public int getCount() {
        return (mDataList == null ? 0 : mDataList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.account_task_card_adapter_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
            viewHolder.button_operate.setOnClickListener(this);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        TaskBean taskBean = mDataList.get(position);
        if (position == 0 || taskBean.taskType != mDataList.get(position - 1).taskType) {
            viewHolder.textView_groupTitle.setText(mResources.getString(taskBean.taskType == CommonConst.MerculetTaskType.NORMAL ? R.string.merculet_task_normal : (taskBean.taskType == CommonConst.MerculetTaskType.SPECIAL ? R.string.merculet_task_special : R.string.merculet_task_unknow)));
            viewHolder.textView_groupTitle.setVisibility(View.VISIBLE);
        } else
            viewHolder.textView_groupTitle.setVisibility(View.GONE);
        viewHolder.textView_taskTitle.setText(taskBean.eventTitle);
        viewHolder.button_operate.setText(taskBean.eventStatus == TaskBean.STATUS_UNENABLE ? mResources.getString(R.string.merculet_task_status_not_start) : (taskBean.eventStatus == TaskBean.STATUS_NORMAL ? taskBean.notYetButtonName : taskBean.alreadyButtonName));
        viewHolder.button_operate.setBackgroundResource(taskBean.eventStatus == TaskBean.STATUS_NORMAL ? R.drawable.account_task_card_operate_enable : R.drawable.account_task_card_operate_unenable);
        viewHolder.button_operate.setTextColor(taskBean.eventStatus == TaskBean.STATUS_NORMAL ? mColorOperateNormal : mColorOperateDone);
        String subTittle = (taskBean.eventStatus == TaskBean.STATUS_DONE ? taskBean.finishedSubTitle : taskBean.eventSubTitle);
        SpannableString spannableString = new SpannableString(subTittle);
        if (subTittle != null) {
            Matcher matcher = mPattern.matcher(subTittle);
            while (matcher.find()) {
                spannableString.setSpan(new ForegroundColorSpan(mColorHighLight), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new AbsoluteSizeSpan(mTextSizeHighLight, true), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        viewHolder.textView_taskDes.setText(spannableString);
        if ((position == mDataList.size() - 1) || (position + 1 < mDataList.size() && taskBean.taskType != mDataList.get(position + 1).taskType))
            viewHolder.convertView.setBackgroundDrawable(null);
        else
            viewHolder.convertView.setBackgroundResource(R.drawable.background_default_with_bottom_line);
        viewHolder.convertView.setPadding(0, mPaddingTB, 0, mPaddingTB);
        viewHolder.button_operate.setTag(position);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_operate:
                int position = (int) v.getTag();
                TaskBean taskBean = mDataList.get(position);
                if (taskBean.eventStatus != TaskBean.STATUS_NORMAL)
                    return;
                sendAdapterEventToHost(AdapterEventIdConst.ADAPTER_EVENT_TASK_OPERATE, position, null, taskBean);
                break;
        }
    }


    private static class ViewHolder {
        View convertView;
        TextView textView_groupTitle;
        TextView textView_taskTitle;
        TextView textView_taskDes;
        Button button_operate;

        public ViewHolder(View convertView) {
            this.convertView = convertView;
            textView_groupTitle = convertView.findViewById(R.id.tv_group_title);
            textView_taskTitle = convertView.findViewById(R.id.tv_task_title);
            textView_taskDes = convertView.findViewById(R.id.tv_task_des);
            button_operate = convertView.findViewById(R.id.btn_operate);
        }
    }
}
