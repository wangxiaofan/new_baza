package com.baza.android.bzw.businesscontroller.account.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/6/7.
 * Title：
 * Note：
 */
public class EmailInputHintAdapter extends BaseBZWAdapter implements Filterable {
    private Context mContext;
    private List<String> mEmailHintList;
    private EmailHintFilter mEmailHintFilter;
    private int mPadding;
    private int mItemWidth, mItemHeight;

    public EmailInputHintAdapter(Context context, int itemWidth, int itemHeight, List<String> emailHintList, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        mContext = context;
        mItemWidth = itemWidth;
        mItemHeight = itemHeight;
        mEmailHintList = emailHintList;
        mPadding = ScreenUtil.dip2px(10);
    }

    @Override
    public int getCount() {
        return (mEmailHintList == null ? 0 : mEmailHintList.size());
    }

    @Override
    public Object getItem(int position) {
        return mEmailHintList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = new TextView(mContext);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setTextColor(mContext.getResources().getColor(R.color.text_color_black_666666));
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            textView.setPadding(mPadding, 0, mPadding, 0);
            textView.setLayoutParams(new AbsListView.LayoutParams(mItemWidth, mItemHeight));
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            convertView = textView;
        }
        textView = (TextView) convertView;
        textView.setText(mEmailHintList.get(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mEmailHintFilter == null)
            mEmailHintFilter = new EmailHintFilter();
        return mEmailHintFilter;
    }

    private class EmailHintFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            filterResults.values = mEmailHintList;
            filterResults.count = (mEmailHintList == null ? 0 : mEmailHintList.size());
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results == null)
                return;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else
                notifyDataSetInvalidated();
        }
    }
}
