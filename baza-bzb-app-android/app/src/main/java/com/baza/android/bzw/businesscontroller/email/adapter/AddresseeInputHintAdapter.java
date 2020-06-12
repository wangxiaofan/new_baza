package com.baza.android.bzw.businesscontroller.email.adapter;

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

import com.baza.android.bzw.bean.email.AddresseeBean;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vincent.Lei on 2017/4/17.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class AddresseeInputHintAdapter extends BaseBZWAdapter implements Filterable {
    private Context mContext;
    private List<AddresseeBean> mPreviousList = new ArrayList<>();
    private List<AddresseeBean> mTempList = new ArrayList<>();
    private List<AddresseeBean> mOriginalList;
    private AddresseeFilter mAddresseeFilter;
    private int mItemWidth, mItemHeight, mPadding;

    public AddresseeInputHintAdapter(Context mContext, List<AddresseeBean> original, int width, int height, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = mContext;
        if (original != null && !original.isEmpty()) {
            this.mOriginalList = original;
            this.mPreviousList = new ArrayList<>(original.size());
            this.mPreviousList.addAll(original);
        } else
            this.mPreviousList = new ArrayList<>();
        this.mItemHeight = height;
        this.mItemWidth = width;
        this.mPadding = ScreenUtil.dip2px(5);
    }

    @Override
    public int getCount() {
        return mPreviousList.size();
    }

    public String getStrData(int position) {
        return mPreviousList.get(position).email;
    }

    @Override
    public Object getItem(int position) {
        return mPreviousList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = new TextView(mContext);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setTextColor(mContext.getResources().getColor(R.color.text_color_black_666666));
            textView.setGravity(Gravity.LEFT);
            textView.setPadding(mPadding, 0, mPadding, 0);
            textView.setLayoutParams(new AbsListView.LayoutParams(mItemWidth, mItemHeight));
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            convertView = textView;
        }
        textView = (TextView) convertView;
        textView.setText(mPreviousList.get(position).email);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mAddresseeFilter == null)
            mAddresseeFilter = new AddresseeFilter();
        return mAddresseeFilter;
    }

    private class AddresseeFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint == null || mOriginalList == null || mOriginalList.isEmpty())
                return null;
            mTempList.clear();
            String str = constraint.toString().toLowerCase();
            AddresseeBean addressBean;
            for (int i = 0, size = mOriginalList.size(); i < size; i++) {
                addressBean = mOriginalList.get(i);
                if (addressBean.email != null && addressBean.email.toLowerCase().contains(str))
                    mTempList.add(addressBean);
            }
            FilterResults filterResults = new FilterResults();
            filterResults.count = mTempList.size();
            filterResults.values = mTempList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results == null)
                return;
            if (results.count > 0) {
                mPreviousList.clear();
                mPreviousList.addAll((ArrayList<AddresseeBean>) results.values);
                notifyDataSetChanged();
            } else
                notifyDataSetInvalidated();
        }
    }
}
