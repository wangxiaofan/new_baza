package com.baza.android.bzw.businesscontroller.floating.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.TimeLineListBean;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

public class TimeLineListAdapter extends BaseBZWAdapter {

    Context mContext;

    List<TimeLineListBean> timeLineList = new ArrayList<>();

    StringBuilder recommendBuilder = new StringBuilder();

    public TimeLineListAdapter(Context context, List<TimeLineListBean> list, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = context;
        this.timeLineList = list;
    }

    @Override
    public int getCount() {
        return timeLineList == null ? 0 : timeLineList.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_list_recommend_time_line, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        TimeLineListBean bean = timeLineList.get(i);
        holder.tv_step.setText(bean.getProcessTypeText());
        holder.tv_name.setText(bean.getRecommenderName());

        holder.tv_time.setText(bean.getCreatedString());

        holder.tv_content.setText(getContent(bean));
        if (bean.getProcessType() == 3 || bean.getProcessType() == 11) {
            holder.tv_step.setBackgroundResource(R.drawable.bg_red_circle);
            holder.tv_step.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            if (i == 0) {
                holder.tv_step.setBackgroundResource(R.drawable.bg_blue_circle);
                holder.tv_step.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                holder.tv_step.setBackgroundResource(R.drawable.bg_white_circle);
                holder.tv_step.setTextColor(Color.parseColor("#53ABD5"));
            }
        }
        return view;
    }

    class ViewHolder {
        TextView tv_step;
        TextView tv_name;
        TextView tv_time;
        TextView tv_content;

        public ViewHolder(View itemView) {
            tv_step = itemView.findViewById(R.id.tv_step);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }

    private String getContent(TimeLineListBean bean) {
        if (recommendBuilder.length() > 0) {
            recommendBuilder.delete(0, recommendBuilder.length());
        }

        if (bean.getProcessType() == 1) {
            recommendBuilder.append("推荐了候选人").append("\n");
            recommendBuilder.append(bean.getExtensionProperties().getRecommendReason());
        } else if (bean.getProcessType() == 2 || bean.getProcessType() == 4) {
            recommendBuilder.append("接受了候选人");
        } else if (bean.getProcessType() == 3) {
            recommendBuilder.append("拒绝了候选人").append("\n");
            if (bean.getExtensionProperties().getRejectedReason() != null) {
                recommendBuilder.append(bean.getExtensionProperties().getRejectedReason()).append("\n");
            }
            if (bean.getExtensionProperties().getRejectedDetail() != null) {
                recommendBuilder.append(bean.getExtensionProperties().getRejectedDetail());
            }
        } else if (bean.getProcessType() == 5) {
            recommendBuilder.append("将候选人推进到面试阶段");
        } else if (bean.getProcessType() == 6) {
            recommendBuilder.append("安排").append(bean.getExtensionProperties().getInterviewType()).append("\n");
            recommendBuilder.append("面试时间：").append(bean.getExtensionProperties().getInterviewTime()).append("\n");
            recommendBuilder.append(bean.getExtensionProperties().getInterviewAddress());
        } else if (bean.getProcessType() == 7) {
            recommendBuilder.append("取消").append(bean.getExtensionProperties().getInterviewType()).append("\n");
            recommendBuilder.append("面试时间：").append(bean.getExtensionProperties().getInterviewTime()).append("\n");
            recommendBuilder.append(bean.getExtensionProperties().getInterviewAddress());
        } else if (bean.getProcessType() == 8) {
            recommendBuilder.append("反馈了").append(bean.getExtensionProperties().getInterviewType()).append("结果")
                    .append(bean.getExtensionProperties().getResult()).append("\n");
            recommendBuilder.append(bean.getExtensionProperties().getEvaluation());
        } else if (bean.getProcessType() == 9) {
            recommendBuilder.append("将候选人推进到Offer阶段").append("\n");
            recommendBuilder.append("薪酬为：").append(bean.getOfferMonthSalary() + "K*").append(bean.getOfferMonths()).append("\n");
            recommendBuilder.append("入职时间：").append(bean.getOfferHiredDate()).append("\n");
            recommendBuilder.append("备注为：").append(bean.getOfferRemarks());
        } else if (bean.getProcessType() == 10) {
            recommendBuilder.append("将候选人推进到入职阶段").append("\n");
            recommendBuilder.append("入职时间：").append(bean.getOnboardDate()).append("\n");
            recommendBuilder.append("保证期：").append(bean.getGuaranteeDays() / 30 + "个月");
        } else if (bean.getProcessType() == 11) {
            recommendBuilder.append("淘汰了候选人").append("\n");
            recommendBuilder.append(bean.getExtensionProperties().getObsoletedReason()).append("\n");
            recommendBuilder.append(bean.getExtensionProperties().getObsoletedDetail());
        } else if (bean.getProcessType() == 17) {
            recommendBuilder.append("将候选人设置为离职状态");
        } else if (bean.getProcessType() == 18) {
            recommendBuilder.append("筛选通过了候选人");
        } else if (bean.getProcessType() == 20) {
            recommendBuilder.append(bean.getExtensionProperties().getAmount() + "元 打赏了").append(bean.getExtensionProperties().getRecommenderRealName())
                    .append("@@").append(bean.getExtensionProperties().getRecommenderNickname()).append("\n");
            recommendBuilder.append(bean.getExtensionProperties().getRemark());
        } else if (bean.getProcessType() == 26) {
            recommendBuilder.append("恢复了候选人").append("\n");
            recommendBuilder.append("候选人当前状态为").append(bean.getExtensionProperties().getLastStatusName());
        }

        return recommendBuilder.toString();
    }
}
