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

public class IMSearchJLAdapter extends BaseAdapter {

    private Context context;
    private List<IMSearchBean.DataBeanX.MessageInfoPageBean.DataBean> beanList;
    public boolean isMore;//默认两条，isMore为true展示全部

    public IMSearchJLAdapter(Context context, List<IMSearchBean.DataBeanX.MessageInfoPageBean.DataBean> beanList, boolean isMore) {
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

        if (beanList.get(i).getType() == 0) {
            //单聊
            Glide.with(context).load(URLConst.URL_ICON + beanList.get(i).getId()).circleCrop().into(viewHolder.adapter_search_img);
            //GlideEngine.loadCornerImage(viewHolder.adapter_search_img, URLConst.URL_ICON + beanList.get(i).getId(), null, 100);
        } else {
            //群聊
            Glide.with(context).load(URLConst.URL_ICON_GROUP + beanList.get(i).getId()).circleCrop().into(viewHolder.adapter_search_img);
            //GlideEngine.loadCornerImage(viewHolder.adapter_search_img, URLConst.URL_ICON_GROUP + beanList.get(i).getId(), null, 100);
        }
        viewHolder.adapter_search_name.setText(beanList.get(i).getName());
        viewHolder.adapter_search_last.setText(beanList.get(i).getCount() + "条相关的聊天记录");

        return view;
    }

    class ViewHolder {
        private ImageView adapter_search_img;
        private TextView adapter_search_name, adapter_search_last;
    }
}
