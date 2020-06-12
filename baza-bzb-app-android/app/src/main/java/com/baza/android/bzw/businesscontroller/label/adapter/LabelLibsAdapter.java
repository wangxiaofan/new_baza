package com.baza.android.bzw.businesscontroller.label.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.SwipeMenuLayout;

import java.util.List;


/**
 * Created by Vincent.Lei on 2017/3/17.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class LabelLibsAdapter extends BaseBZWAdapter implements View.OnClickListener {
    private Context mContext;
    private Resources mResources;
    private List<Label> labelList;

    public LabelLibsAdapter(Context mContext, List<Label> labelList, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = mContext;
        this.labelList = labelList;
        this.mResources = mContext.getResources();
    }

    @Override
    public int getCount() {
        return labelList != null ? labelList.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_for_marklibs, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
            viewHolder.textView_delete.setOnClickListener(this);
            viewHolder.view_content.setOnClickListener(this);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        Label label = labelList.get(position);
        viewHolder.textView_markContent.setText(label.tag);
        viewHolder.textView_markCount.setText(mResources.getString(R.string.resume_in_label, String.valueOf(label.count)));
        viewHolder.textView_delete.setTag(position);
        viewHolder.view_content.setTag(position);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        SwipeMenuLayout swipeMenuLayout = SwipeMenuLayout.getViewCache();
        if (swipeMenuLayout != null)
            swipeMenuLayout.smoothClose();
        if (mIAdapterEventsListener != null) {
            switch (v.getId()) {
                case R.id.tv_delete:
                    int position = (int) v.getTag();
                    sendAdapterEventToHost(AdapterEventIdConst.ADAPTER_EVENT_LABEL_DEL_TARGET, position, v, labelList.get(position));
                    break;
                default:
                    position = (int) v.getTag();
                    sendAdapterEventToHost(AdapterEventIdConst.ADAPTER_EVENT_LABEL_DO_SEARCH, position, v, labelList.get(position));
                    break;
            }
        }
    }


    private static class ViewHolder {
        TextView textView_markContent;
        TextView textView_markCount;
        TextView textView_delete;
        SwipeMenuLayout swipeMenuLayout;
        View view_content;

        public ViewHolder(View convertView) {
            view_content = convertView.findViewById(R.id.view_content);
            swipeMenuLayout = convertView.findViewById(R.id.swipe_layout);
            textView_markContent = convertView.findViewById(R.id.tv_label_content);
            textView_markCount = convertView.findViewById(R.id.tv_label_count);
            textView_delete = convertView.findViewById(R.id.tv_delete);
        }
    }
}
