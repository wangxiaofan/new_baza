package com.baza.android.bzw.businesscontroller.resume.detail.adapter.remark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.bznet.android.rcbox.R;

public class WuxiaoListAdapter extends BaseBZWAdapter {

    private Context mContext;

    private String[] contents;

    private int position = -1;

    public WuxiaoListAdapter(Context context, String[] data, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = context;
        this.contents = data;
    }

    @Override
    public int getCount() {
        return contents.length;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_list_wuxiao_remark, null);
            holder.tv_content = view.findViewById(R.id.tv_content);
            holder.iv_select = view.findViewById(R.id.iv_select);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_content.setText(contents[i]);

        if (i == position) {
            holder.iv_select.setVisibility(View.VISIBLE);
        } else {
            holder.iv_select.setVisibility(View.GONE);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAdapterEventToHost(AdapterEventIdConst.WUXIAO_REMARK_CLICK, i, null, contents[i]);
            }
        });
        return view;
    }

    public void setPosition(int i) {
        this.position = i;
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView tv_content;
        ImageView iv_select;
    }
}
