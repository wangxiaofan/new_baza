package com.baza.android.bzw.businesscontroller.message;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bznet.android.rcbox.R;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.bean.IMGetTeamMembersResponseBean;

import java.util.List;


public class IMAddGroupChatAdapterSecond extends BaseAdapter {

    private IMAddGroupChatActivity activity;
    private List<IMGetTeamMembersResponseBean.DataBean> beanList;
    private IMAddGroupChatAdapter imAddGroupChatAdapter;

    public IMAddGroupChatAdapterSecond(IMAddGroupChatActivity activity, List<IMGetTeamMembersResponseBean.DataBean> beanList, IMAddGroupChatAdapter imAddGroupChatAdapter) {
        this.activity = activity;
        this.beanList = beanList;
        this.imAddGroupChatAdapter = imAddGroupChatAdapter;
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
            view = LayoutInflater.from(activity).inflate(R.layout.adapter_add_group_chat_second, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.group_chat_second_name = view.findViewById(R.id.group_chat_second_name);
            viewHolder.group_chat_second_team = view.findViewById(R.id.group_chat_second_team);
            viewHolder.group_chat_second_iv = view.findViewById(R.id.group_chat_second_iv);
            viewHolder.group_chat_second_icon = view.findViewById(R.id.group_chat_second_icon);
            viewHolder.group_chat_second_rel = view.findViewById(R.id.group_chat_second_rel);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //邀请群聊进来的需要判断是否含有已经是群成员的，如果已经是，默认勾选且不可取消
        Log.e("herb", "getUnionId()>>" + beanList.get(i).getUnionId());
        if (IMAddGroupChatActivity.mGroupInfo != null && IMAddGroupChatActivity.mGroupInfo.getMemberDetails() != null && IMAddGroupChatActivity.mGroupInfo.getMemberDetails().size() > 0) {
            for (int j = 0; j < IMAddGroupChatActivity.mGroupInfo.getMemberDetails().size(); j++) {
                Log.e("herb", "getAccount>>" + IMAddGroupChatActivity.mGroupInfo.getMemberDetails().get(j).getAccount());

                if (beanList.get(i).getUnionId().equals(IMAddGroupChatActivity.mGroupInfo.getMemberDetails().get(j).getAccount())) {
                    beanList.get(i).setChecked(true);
                }
            }
        }

        Glide.with(activity).load(URLConst.URL_ICON + beanList.get(i).getUnionId()).circleCrop().into(viewHolder.group_chat_second_icon);
        //Glide.with(this).load(UserInfo.getInstance().getUserInformationEntity().getUser().getHeadImgUrl()).bitmapTransform(new CropCircleTransformation(this)).into(details_icon);
        //GlideEngine.loadCornerImage(viewHolder.group_chat_second_icon, URLConst.URL_ICON + beanList.get(i).getUnionId(), null, 100);
        viewHolder.group_chat_second_name.setText(beanList.get(i).getRealName() + "@" + beanList.get(i).getNickname());
        viewHolder.group_chat_second_team.setText(beanList.get(i).getTeamId());

        //是否选中
        if (beanList.get(i).isChecked() || beanList.get(i).getUnionId().equals(Configs.unionId)) {
            viewHolder.group_chat_second_iv.setImageResource(R.drawable.btn_kuangxuan_sel);
        } else {
            viewHolder.group_chat_second_iv.setImageResource(R.drawable.btn_kuangxuan_nor);
        }

        viewHolder.group_chat_second_rel.setOnClickListener(view1 -> {

            if (beanList.get(i).getUnionId().equals(Configs.unionId)) {
                //自己
                return;
            }

            //更新状态，刷新界面
            beanList.get(i).setChecked(!beanList.get(i).isChecked());
            if (beanList.get(i).isChecked()) {
                activity.updateChooseNum(true);
                viewHolder.group_chat_second_iv.setImageResource(R.drawable.btn_kuangxuan_sel);
            } else {
                activity.updateChooseNum(false);
                viewHolder.group_chat_second_iv.setImageResource(R.drawable.btn_kuangxuan_nor);
            }
            //更新其他数据
            imAddGroupChatAdapter.updateListSelect(beanList.get(i).getUnionId(), beanList.get(i).isChecked());

        });

        return view;
    }

    class ViewHolder {
        private TextView group_chat_second_name, group_chat_second_team;
        private ImageView group_chat_second_iv, group_chat_second_icon;
        private RelativeLayout group_chat_second_rel;
    }
}
