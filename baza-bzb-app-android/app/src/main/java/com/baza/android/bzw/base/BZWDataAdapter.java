package com.baza.android.bzw.base;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.slib.utils.AppUtil;

import java.util.List;

/**
 * Created by Vincent.Lei on 2019/8/21.
 * Title：
 * Note：
 */
public abstract class BZWDataAdapter<T> extends BaseBZWAdapter {
    protected Context mContext;
    protected List<T> mDataList;
    protected Resources mResources;

    public BZWDataAdapter(Context context, List<T> dataList, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = context;
        this.mDataList = dataList;
        this.mResources = context.getResources();
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = getConvertView(position);
            convertView.setTag(getViewHolder(position, convertView));
        }
        setUpItemView((BaseAdapterViewHolder) convertView.getTag(), position);
        return convertView;
    }

    protected abstract View getConvertView(int position);

    protected abstract BaseAdapterViewHolder getViewHolder(int position, View convertView);

    protected abstract void setUpItemView(BaseAdapterViewHolder bvh, int position);

    public void refreshTargetItemView(ListView bindListView, int position) {
        if (position < 0) {
            notifyDataSetChanged();
            return;
        }
        View convertView = AppUtil.getTargetVisibleView(position, bindListView);
        if (convertView != null)
            setUpItemView((BaseAdapterViewHolder) convertView.getTag(), position);
    }
}
