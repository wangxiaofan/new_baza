package com.baza.android.bzw.businesscontroller.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bznet.android.rcbox.R;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.bean.IMSearchBean;
import com.tencent.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;

import java.util.List;

public class IMSearchAdapter extends BaseAdapter {

    private Context context;
    private List<IMSearchBean.DataBeanX.InnerMemberInfosBean> beanList;
    public boolean isMore;//默认两条，isMore为true展示全部

    public IMSearchAdapter(Context context, List<IMSearchBean.DataBeanX.InnerMemberInfosBean> beanList, boolean isMore) {
        this.context = context;
        this.beanList = beanList;
        this.isMore = isMore;
    }

    @Override
    public int getCount() {
        if (isMore) {
            return beanList.size();
        } else {
            if (beanList.size() > 2) {
                return 2;
            } else {
                return beanList.size();
            }
        }
    }

    @Override
    public Object getItem(int i) {
        return beanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_search, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.adapter_search_img = view.findViewById(R.id.adapter_search_img);
            viewHolder.adapter_search_name = view.findViewById(R.id.adapter_search_name);
            viewHolder.adapter_search_last = view.findViewById(R.id.adapter_search_last);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Glide.with(context).load(URLConst.URL_ICON + beanList.get(i).getUnionId()).circleCrop().into(viewHolder.adapter_search_img);
        //GlideEngine.loadCornerImage(viewHolder.adapter_search_img, URLConst.URL_ICON + beanList.get(i).getUnionId(), null, 100);
        viewHolder.adapter_search_name.setText(beanList.get(i).getUserName());
        viewHolder.adapter_search_last.setText(beanList.get(i).getCompany() + " | " + beanList.get(i).getTitle());

        return view;
    }

    class ViewHolder {
        private ImageView adapter_search_img;
        private TextView adapter_search_name, adapter_search_last;
    }
}
