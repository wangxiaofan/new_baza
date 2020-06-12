package com.baza.android.bzw.businesscontroller.search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/5/23.
 * Title：
 * Note：
 */

public class SearchHistoryAdapter extends BaseBZWAdapter implements View.OnClickListener {
//    public static final int EVENT_DELETE_HISTORY = 19080;
//    public static final int EVENT_CLICK_HISTORY = 19081;

    public interface ISearchHistoryDataProvider {
        int getCount();

        String getItemText(int position);

        void onItemClick(int position);

        void onItemDelete(int position);
    }

    private Context mContext;
    private ISearchHistoryDataProvider mProvider;

    public SearchHistoryAdapter(Context mContext, ISearchHistoryDataProvider provider, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = mContext;
        this.mProvider = provider;
    }

    @Override
    public int getCount() {
        return mProvider.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_history_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
            viewHolder.imageView_delete.setOnClickListener(this);
            convertView.setOnClickListener(this);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.textView_history.setText(mProvider.getItemText(position));
        viewHolder.imageView_delete.setTag(position);
        return convertView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_del_history:
                mProvider.onItemDelete((int) v.getTag());
                break;
            default:
                ViewHolder viewHolder = (ViewHolder) v.getTag();
                int position = (int) viewHolder.imageView_delete.getTag();
                mProvider.onItemClick(position);
                break;
        }
    }

    static class ViewHolder {
        TextView textView_history;
        ImageView imageView_delete;

        public ViewHolder(View convertView) {
            textView_history = convertView.findViewById(R.id.tv_item_value);
            imageView_delete = convertView.findViewById(R.id.iv_del_history);
        }
    }
}
