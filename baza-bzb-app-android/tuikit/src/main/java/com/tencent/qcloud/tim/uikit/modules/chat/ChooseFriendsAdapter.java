package com.tencent.qcloud.tim.uikit.modules.chat;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.bean.IMSearchBean;
import com.tencent.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;

import java.util.List;

public class ChooseFriendsAdapter extends BaseAdapter {

    private Context context;
    private List<TIMGroupMemberInfo> beanList;
    private boolean isSearch;

    public ChooseFriendsAdapter(Context context, List<TIMGroupMemberInfo> beanList, boolean isSearch) {
        this.context = context;
        this.beanList = beanList;
        this.isSearch = isSearch;
    }

    @Override
    public int getCount() {
        return beanList.size();
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
            view = LayoutInflater.from(context).inflate(R.layout.adapter_choose_friends, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.adapter_choose_friends_im = view.findViewById(R.id.adapter_choose_friends_im);
            viewHolder.adapter_choose_friends_tv = view.findViewById(R.id.adapter_choose_friends_tv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (isSearch) {
            Glide.with(context).load(URLConst.URL_ICON + beanList.get(i).getUser()).circleCrop().into(viewHolder.adapter_choose_friends_im);
            //GlideEngine.loadCornerImage(viewHolder.adapter_choose_friends_im, URLConst.URL_ICON + beanList.get(i).getUser(), null, 100);
            viewHolder.adapter_choose_friends_tv.setText(Configs.nameIdsMap.get(beanList.get(i).getUser()));
        } else {
            if (i > 0) {
                Glide.with(context).load(URLConst.URL_ICON + beanList.get(i).getUser()).circleCrop().into(viewHolder.adapter_choose_friends_im);
                //GlideEngine.loadCornerImage(viewHolder.adapter_choose_friends_im, URLConst.URL_ICON + beanList.get(i).getUser(), null, 100);
                viewHolder.adapter_choose_friends_tv.setText(Configs.nameIdsMap.get(beanList.get(i).getUser()));
            } else {
                viewHolder.adapter_choose_friends_im.setImageResource(R.drawable.pic_suoyouren);
                viewHolder.adapter_choose_friends_tv.setText(beanList.get(i).getNameCard() + "(" + beanList.size() + ")");
            }
        }

        return view;
    }

    class ViewHolder {
        private ImageView adapter_choose_friends_im;
        private TextView adapter_choose_friends_tv;
    }
}
