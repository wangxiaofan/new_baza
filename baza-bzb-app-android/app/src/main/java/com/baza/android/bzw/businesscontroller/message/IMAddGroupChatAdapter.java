package com.baza.android.bzw.businesscontroller.message;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bznet.android.rcbox.R;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.bean.IMGetTeamsResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMSearchBean;
import com.tencent.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;

import java.util.List;

public class IMAddGroupChatAdapter extends BaseAdapter {

    private IMAddGroupChatActivity activity;
    private List<IMGetTeamsResponseBean.DataBean> beanList;

    public IMAddGroupChatAdapter(IMAddGroupChatActivity activity, List<IMGetTeamsResponseBean.DataBean> beanList) {
        this.activity = activity;
        this.beanList = beanList;
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
            view = LayoutInflater.from(activity).inflate(R.layout.adapter_add_group_chat, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.group_chat_rel = view.findViewById(R.id.group_chat_rel);
            viewHolder.group_chat_tv = view.findViewById(R.id.group_chat_tv);
            viewHolder.group_chat_iv = view.findViewById(R.id.group_chat_iv);
            viewHolder.group_chat_lv = view.findViewById(R.id.group_chat_lv);
            viewHolder.group_chat_lv_min = view.findViewById(R.id.group_chat_lv_min);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //文字+右侧箭头
        if (beanList.get(i) != null && beanList.get(i).getMembersCount() == 0) {
            //箭头不显示
            viewHolder.group_chat_tv.setText(beanList.get(i).getTeamName() + "(0)");
            viewHolder.group_chat_iv.setVisibility(View.GONE);
        } else {
            //箭头显示
            viewHolder.group_chat_tv.setText(beanList.get(i).getTeamName() + "(" + beanList.get(i).getMembersCount() + ")");
            viewHolder.group_chat_iv.setVisibility(View.VISIBLE);
            if (beanList.get(i).getBean() != null && beanList.get(i).getBean().getData() != null) {
                viewHolder.group_chat_lv.setAdapter(new IMAddGroupChatAdapterSecond(activity, beanList.get(i).getBean().getData(), this));
            }
        }

        //箭头展开
        if (beanList.get(i).isOpen()) {
            //展开
            viewHolder.group_chat_iv.setImageResource(R.drawable.btn_shouqi);
            viewHolder.group_chat_lv.setVisibility(View.VISIBLE);
        } else {
            //关闭
            viewHolder.group_chat_iv.setImageResource(R.drawable.btn_zhankai);
            viewHolder.group_chat_lv.setVisibility(View.GONE);
        }

        //箭头监听
        viewHolder.group_chat_rel.setOnClickListener(view1 -> {
            if (viewHolder.group_chat_iv.getVisibility() == View.GONE) {
                return;
            }
            beanList.get(i).setOpen(!beanList.get(i).isOpen());
            if (beanList.get(i).isOpen()) {
                //展开
                viewHolder.group_chat_iv.setImageResource(R.drawable.btn_shouqi);
                viewHolder.group_chat_lv.setVisibility(View.VISIBLE);
            } else {
                //关闭
                viewHolder.group_chat_iv.setImageResource(R.drawable.btn_zhankai);
                viewHolder.group_chat_lv.setVisibility(View.GONE);
            }
        });

        if (beanList.get(i).getChildTeams() != null && beanList.get(i).getChildTeams().size() > 0) {
            boolean hasChild = false;
            for (int j = 0; j < beanList.get(i).getChildTeams().size(); j++) {
                if (beanList.get(i).getChildTeams().get(j) != null &&
                        (beanList.get(i).getChildTeams().get(j).getMembersCount() > 0 ||
                                (beanList.get(i).getChildTeams().get(j).getChildTeams() != null &&
                                        beanList.get(i).getChildTeams().get(j).getChildTeams().size() > 0))) {
                    hasChild = true;
                    break;
                }
            }
            if (hasChild) {
                viewHolder.group_chat_lv_min.setVisibility(View.VISIBLE);
                viewHolder.group_chat_lv_min.setAdapter(new IMAddGroupChatAdapter(activity, beanList.get(i).getChildTeams()));
            }
        } else {
            viewHolder.group_chat_lv_min.setVisibility(View.GONE);
        }

        return view;
    }

    class ViewHolder {
        private RelativeLayout group_chat_rel;
        private TextView group_chat_tv;
        private ImageView group_chat_iv;
        private ListView group_chat_lv, group_chat_lv_min;
    }

    //更新其他列表的选项
    public void updateListSelect(String id, boolean isSelect) {
        for (int i = 0; i < beanList.size(); i++) {
            if (beanList.get(i) != null && beanList.get(i).getBean() != null && beanList.get(i).getBean().getData() != null && beanList.get(i).getBean().getData().size() > 0) {
                for (int j = 0; j < beanList.get(i).getBean().getData().size(); j++) {
                    if (beanList.get(i).getBean().getData().get(j).getUnionId().equals(id)) {
                        beanList.get(i).getBean().getData().get(j).setChecked(isSelect);
                    }
                }
            }
        }
        //更新数据
        notifyDataSetChanged();
    }
}
