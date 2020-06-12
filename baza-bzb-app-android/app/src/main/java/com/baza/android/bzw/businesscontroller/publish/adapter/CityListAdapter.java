package com.baza.android.bzw.businesscontroller.publish.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.common.LocalAreaBean;
import com.baza.android.bzw.bean.searchfilterbean.FilterCityBean;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.widget.LineBreakLayout;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/23.
 * Title：
 * Note：
 */

public class CityListAdapter extends BaseBZWAdapter implements View.OnClickListener {
    private Context mContext;
    private List<FilterCityBean> mList;
    private int mGroupSelected, mIndexSelectedInGroup;
    private int mItemWidth, mItemHeight;
    private int mTextColorChecked, mTextColorNormal;
    private int mTextSize;

    public CityListAdapter(Context mContext, List<FilterCityBean> mList, int mGroupSelected, int mIndexSelectedInGroup, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = mContext;
        this.mList = mList;
        this.mGroupSelected = mGroupSelected;
        this.mIndexSelectedInGroup = mIndexSelectedInGroup;
        Resources resources = mContext.getResources();
        this.mItemWidth = (int) ((ScreenUtil.screenWidth - resources.getDimension(R.dimen.dp_60) - 4) / 4);
        this.mItemHeight = (int) resources.getDimension(R.dimen.dp_25);
        this.mTextSize = ScreenUtil.px2dip(resources.getDimension(R.dimen.text_size_11));
        mTextColorChecked = resources.getColor(android.R.color.white);
        mTextColorNormal = resources.getColor(R.color.text_color_black_4E5968);
    }


    @Override
    public int getCount() {
        return (mList == null ? 0 : mList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_filter_city, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        FilterCityBean filterCityBean = mList.get(position);
        viewHolder.textView_title.setText(filterCityBean.title);
        setCityItems(viewHolder.lineBreakLayout_cityContainer, filterCityBean.cities, position);
        return convertView;
    }

    public void resetGroupAndIndex(int group, int indexSelectedInGroup) {
        if (group != mGroupSelected || indexSelectedInGroup != mIndexSelectedInGroup) {
            this.mGroupSelected = group;
            this.mIndexSelectedInGroup = indexSelectedInGroup;
            notifyDataSetChanged();
            LogUtil.d("-----resetGroupAndIndex------");
        }
    }

    private void setCityItems(LineBreakLayout lineBreakLayout, List<LocalAreaBean> cities, int position) {
        if (cities == null || cities.isEmpty()) {
            lineBreakLayout.setVisibility(View.GONE);
            return;
        }
        int currentCount = lineBreakLayout.getChildCount();
        int needShowCount = cities.size();
        boolean hasCache;
        TextView textView;
        boolean groupChecked = (position == mGroupSelected);
        boolean itemChecked;
        for (int i = 0; i < needShowCount; i++) {
            itemChecked = mIndexSelectedInGroup == i;
            hasCache = (i < currentCount);
            textView = (hasCache ? (TextView) lineBreakLayout.getChildAt(i) : createItem());
            textView.setText(cities.get(i).name);
            textView.setBackgroundResource(((groupChecked && itemChecked) ? R.drawable.shape_search_filter_checked : R.drawable.shape_search_filter_normal));
            textView.setTextColor(((groupChecked && itemChecked) ? mTextColorChecked : mTextColorNormal));
            textView.setTag(R.id.hold_tag_id_one, position);
            textView.setTag(R.id.hold_tag_id_two, i);
            textView.setPadding(5, 0, 5, 0);
            textView.setOnClickListener(this);
            textView.setVisibility(View.VISIBLE);
            if (!hasCache)
                lineBreakLayout.addView(textView, new ViewGroup.LayoutParams(mItemWidth, mItemHeight));
        }

        if (currentCount > needShowCount) {
            for (; needShowCount < currentCount; needShowCount++) {
                lineBreakLayout.getChildAt(needShowCount).setVisibility(View.GONE);
            }
        }
        lineBreakLayout.setVisibility(View.VISIBLE);
    }

    private TextView createItem() {
        TextView textView = new TextView(mContext);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        return textView;
    }

    @Override
    public void onClick(View v) {
        mGroupSelected = (int) v.getTag(R.id.hold_tag_id_one);
        mIndexSelectedInGroup = (int) v.getTag(R.id.hold_tag_id_two);
        notifyDataSetChanged();
        if (mGroupSelected < mList.size()) {
            FilterCityBean filterCityBean = mList.get(mGroupSelected);
            if (filterCityBean.cities != null && mIndexSelectedInGroup < filterCityBean.cities.size()) {
                sendAdapterEventToHost(0, mGroupSelected, null, filterCityBean.cities.get(mIndexSelectedInGroup));
            }
        }

    }

    static class ViewHolder {
        TextView textView_title;
        LineBreakLayout lineBreakLayout_cityContainer;

        public ViewHolder(View convertView) {
            textView_title = convertView.findViewById(R.id.tv_title);
            lineBreakLayout_cityContainer = convertView.findViewById(R.id.lbl_city_container);
        }
    }
}
