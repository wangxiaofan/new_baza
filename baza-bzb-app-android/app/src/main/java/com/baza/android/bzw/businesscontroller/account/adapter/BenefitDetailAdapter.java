package com.baza.android.bzw.businesscontroller.account.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.exchange.BenefitDetailResultBean;
import com.slib.utils.DateUtil;
import com.bznet.android.rcbox.R;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/9/4.
 * Title：
 * Note：
 */
public class BenefitDetailAdapter extends BaseBZWAdapter {
    private Context mContext;
    private Resources mResources;
    private List<BenefitDetailResultBean.BenefitRecord> mDataList;
    private boolean mIsGetType;
    private int mColorHight;

    public BenefitDetailAdapter(Context context, List<BenefitDetailResultBean.BenefitRecord> dataList, boolean isGetType, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = context;
        this.mResources = context.getResources();
        this.mDataList = dataList;
        this.mIsGetType = isGetType;
        this.mColorHight = mResources.getColor(R.color.text_color_orange_FF7700);
    }

    @Override
    public int getCount() {
        return (mDataList == null ? 0 : mDataList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.accout_benefit_detail_adapter_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        BenefitDetailResultBean.BenefitRecord benefitRecord = mDataList.get(position);
        if (mIsGetType) {
            viewHolder.textView_title.setText(benefitRecord.title);
            viewHolder.textView_title.setVisibility(View.VISIBLE);
        }
        viewHolder.textView_content.setText(benefitRecord.content);
        SpannableString spannableString = new SpannableString(mResources.getString(mIsGetType ? R.string.create_time_value : R.string.exchange_time_value, DateUtil.longMillions2FormatDate(benefitRecord.createTime, DateUtil.SDF_YMD_HM)));
        spannableString.setSpan(new ForegroundColorSpan(mColorHight), 4, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.textView_timeLeft.setText(spannableString);
        viewHolder.textView_timeRight.setVisibility(mIsGetType ? View.GONE : View.VISIBLE);
        if (viewHolder.textView_timeRight.getVisibility() == View.VISIBLE) {
            spannableString = new SpannableString(mResources.getString(R.string.invalid_time_value, DateUtil.longMillions2FormatDate(benefitRecord.invalidTime, DateUtil.SDF_YMD_HM)));
            spannableString.setSpan(new ForegroundColorSpan(mColorHight), 4, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.textView_timeRight.setText(spannableString);
        }
        return convertView;
    }

    public static class ViewHolder {
        TextView textView_title;
        TextView textView_content;
        TextView textView_timeLeft;
        TextView textView_timeRight;

        public ViewHolder(View convertView) {
            textView_title = convertView.findViewById(R.id.tv_title);
            textView_content = convertView.findViewById(R.id.tv_content);
            textView_timeLeft = convertView.findViewById(R.id.tv_time_1);
            textView_timeRight = convertView.findViewById(R.id.tv_time_2);
        }
    }
}
