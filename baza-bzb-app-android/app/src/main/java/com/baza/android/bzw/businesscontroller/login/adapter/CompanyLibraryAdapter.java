package com.baza.android.bzw.businesscontroller.login.adapter;

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
import com.baza.android.bzw.log.LogUtil;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/17.
 * Title：
 * Note：
 */

public class CompanyLibraryAdapter extends BaseBZWAdapter implements Filterable {
    private Context mContext;
    private List<String> mCurrentList = new ArrayList<>();
    private int mPadding, mItemWidth, mItemHeight;
    private CompanyFilter mCompanyFilter;

    public CompanyLibraryAdapter(Context mContext, int mItemWidth, int mItemHeight, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = mContext;
        this.mPadding = ScreenUtil.dip2px(10);
        this.mItemWidth = mItemWidth;
        this.mItemHeight = mItemHeight;
    }

    @Override
    public int getCount() {
        return mCurrentList.size();
    }

    public void refresh(List<String> list) {
        mCurrentList.clear();
        if (list != null && !list.isEmpty())
            mCurrentList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return mCurrentList.get(position);
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
        textView.setText(mCurrentList.get(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mCompanyFilter == null)
            mCompanyFilter = new CompanyFilter();
        return mCompanyFilter;
    }

//    public String getCompanyAtPosition(int index) {
//        if (index >= 0 && index < mCurrentList.size())
//            return mCurrentList.get(index);
//        return null;
//    }

    private class CompanyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            LogUtil.d("company filter local");
            FilterResults filterResults = new FilterResults();
            filterResults.count = mCurrentList.size();
            filterResults.values = mCurrentList;
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
