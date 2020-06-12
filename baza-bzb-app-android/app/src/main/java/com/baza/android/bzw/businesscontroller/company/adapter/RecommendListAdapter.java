package com.baza.android.bzw.businesscontroller.company.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.ResumeRecommend;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.widget.CircleImageView;
import com.bznet.android.rcbox.R;
import com.slib.utils.LoadImageUtil;
import com.slib.utils.string.StringUtil;

import java.util.List;

public class RecommendListAdapter extends BaseBZWAdapter implements View.OnClickListener {

    private Context mContext;

    private List<ResumeRecommend> mData;

    public RecommendListAdapter(Context context, List<ResumeRecommend> beans, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = context;
        this.mData = beans;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_recommend, null);
            holder.llContent = convertView.findViewById(R.id.ll_content);
            holder.user_pic = convertView.findViewById(R.id.user_pic);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_time = convertView.findViewById(R.id.tv_time);
            holder.tv_status = convertView.findViewById(R.id.tv_status);
            holder.tv_company = convertView.findViewById(R.id.tv_company);
            holder.tv_title = convertView.findViewById(R.id.tv_title);
            holder.tv_location = convertView.findViewById(R.id.tv_location);
            holder.tv_salary = convertView.findViewById(R.id.tv_salary);
            holder.tv_commission = convertView.findViewById(R.id.tv_commission);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ResumeRecommend bean = mData.get(position);

        if (!StringUtil.isEmpty(bean.avatarData)) {
            LoadImageUtil.loadBase64Image(bean.avatarData, R.drawable.avatar_def, holder.user_pic);
        } else {
            holder.user_pic.setBackgroundResource(R.drawable.avatar_def);
        }

        if (bean.userName.contains("@")) {
            String[] str = bean.userName.split("@");
            String textSource = String.format("%1$s <font color='#94A1A5'><small>%2$s</small></font>", str[0], "@" + str[1]);
            holder.tv_name.setText(Html.fromHtml(textSource));
        } else {
            holder.tv_name.setText(bean.userName);
        }

        holder.tv_time.setText(bean.recommendDate + "推荐");
        setStatus(holder.tv_status, bean.recommendStatus);
        holder.tv_company.setText("[" + bean.company + "]");
        holder.tv_title.setText(bean.title);
        holder.tv_location.setText("[" + bean.locationName + "]");
        holder.tv_salary.setText("月薪：" + bean.salary);
        holder.tv_commission.setText("佣金：" + bean.commission);
        holder.llContent.setTag(position);
        holder.llContent.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        sendAdapterEventToHost(AdapterEventIdConst.EVENT_ID_RECOMMEND_CLICK, position, null, mData.get(position));
    }

    class ViewHolder {
        LinearLayout llContent;
        CircleImageView user_pic;
        TextView tv_name;
        TextView tv_time;
        TextView tv_status;
        TextView tv_company;
        TextView tv_title;
        TextView tv_location;
        TextView tv_salary;
        TextView tv_commission;
    }

    private void setStatus(TextView textView, String status) {
        switch (status) {
            case "Unfiltered":
                textView.setText("待反馈");
                break;
            case "Unprocessed":
                textView.setText("未处理");
                break;
            case "Accepted":
                textView.setText("已接受");
                break;
            case "Rejected":
                textView.setText("已拒绝");
                break;
            case "Interview":
                textView.setText("面试待安排");
                break;
            case "Arranged":
                textView.setText("已安排");
                break;
            case "AwaitEvaluation":
                textView.setText("待评估");
                break;
            case "Feedbacked":
                textView.setText("已反馈");
                break;
            case "Offer":
                textView.setText("Offer");
                break;
            case "Onboard":
                textView.setText("已入职");
                break;
            case "Dimission":
                textView.setText("已离职");
                break;
            case "Obsoleted":
                textView.setText("已淘汰");
                break;
            default:
                textView.setText("待反馈");
                break;
        }
    }
}
