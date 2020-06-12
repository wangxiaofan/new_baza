package com.baza.android.bzw.businesscontroller.message;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bznet.android.rcbox.R;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.bean.IMSearchDetailsBean;
import com.tencent.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tim.uikit.utils.DateTimeUtil;

import java.util.Date;
import java.util.List;

public class IMSearchDetailsAdapter extends BaseAdapter {

    private Context mContext;
    private List<IMSearchDetailsBean.DataBeanX.DataBean> mData;

    public IMSearchDetailsAdapter(Context mContext, List<IMSearchDetailsBean.DataBeanX.DataBean> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_search, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.adapter_search_img = view.findViewById(R.id.adapter_search_img);
            viewHolder.adapter_search_name = view.findViewById(R.id.adapter_search_name);
            viewHolder.adapter_search_last = view.findViewById(R.id.adapter_search_last);
            viewHolder.adapter_search_time = view.findViewById(R.id.adapter_search_time);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Glide.with(mContext).load(URLConst.URL_ICON + mData.get(i).getFromAccountId()).circleCrop().into(viewHolder.adapter_search_img);
        //GlideEngine.loadCornerImage(viewHolder.adapter_search_img, URLConst.URL_ICON + mData.get(i).getFromAccountId(), null, 100);
        viewHolder.adapter_search_name.setText(mData.get(i).getUserName());
        viewHolder.adapter_search_last.setText(mData.get(i).getContent());
        viewHolder.adapter_search_time.setText(DateTimeUtil.getTimeFormatText(new Date(mData.get(i).getMsgTime() * 1000)));

        return view;
    }

    class ViewHolder {
        private ImageView adapter_search_img;
        private TextView adapter_search_name, adapter_search_last, adapter_search_time;
    }
}
