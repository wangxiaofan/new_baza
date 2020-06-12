package com.tencent.qcloud.tim.uikit.modules.group.info;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMGroupMemberRoleType;
import com.tencent.imsdk.TIMManager;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tim.uikit.modules.chat.GroupChatManagerKit;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.group.member.GroupMemberInfo;
import com.tencent.qcloud.tim.uikit.modules.group.member.IGroupMemberRouter;
import com.tencent.qcloud.tim.uikit.utils.BackgroundTasks;
import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;


public class GroupInfoAdapter extends BaseAdapter {

    private static final int ADD_TYPE = -100;
    private static final int DEL_TYPE = -101;
    private static final int OWNER_PRIVATE_MAX_LIMIT = 10;  //讨论组,owner可以添加成员和删除成员，
    private static final int OWNER_PUBLIC_MAX_LIMIT = 11;   //公开群,owner不可以添加成员，但是可以删除成员
    private static final int OWNER_CHATROOM_MAX_LIMIT = 11; //聊天室,owner不可以添加成员，但是可以删除成员
    private static final int NORMAL_PRIVATE_MAX_LIMIT = 11; //讨论组,普通人可以添加成员
    private static final int NORMAL_PUBLIC_MAX_LIMIT = 12;  //公开群,普通人没有权限添加成员和删除成员
    private static final int NORMAL_CHATROOM_MAX_LIMIT = 12; //聊天室,普通人没有权限添加成员和删除成员

    private List<GroupMemberInfo> mGroupMembers = new ArrayList<>();
    private IGroupMemberRouter mTailListener;
    private GroupInfo mGroupInfo;
    private boolean isDelMode = false;//是否是删除模式

    public void setManagerCallBack(IGroupMemberRouter listener) {
        mTailListener = listener;
    }

    @Override
    public int getCount() {
        return mGroupMembers.size();
    }

    @Override
    public GroupMemberInfo getItem(int i) {
        return mGroupMembers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        MyViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(TUIKit.getAppContext()).inflate(R.layout.group_member_adpater, viewGroup, false);
            holder = new MyViewHolder();
            holder.memberIcon = view.findViewById(R.id.group_member_icon);
            holder.memberName = view.findViewById(R.id.group_member_name);
            holder.group_member_del = view.findViewById(R.id.group_member_del);
            view.setTag(holder);
        } else {
            holder = (MyViewHolder) view.getTag();
        }
        final GroupMemberInfo info = getItem(i);
        if (info.getMemberType() == ADD_TYPE) {
            holder.memberIcon.setImageResource(R.drawable.btn_add);
            holder.memberName.setText("添加");
            //holder.memberIcon.setBackgroundResource(R.drawable.bottom_action_border);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTailListener != null) {
                        mTailListener.forwardAddMember(mGroupInfo);
                    }
                }
            });
        } else if (info.getMemberType() == DEL_TYPE) {
            if (isDelMode) {
                holder.memberIcon.setImageResource(R.drawable.btn_wancheng);
                holder.memberName.setText("完成");
            } else {
                holder.memberIcon.setImageResource(R.drawable.btn_shanchu);
                holder.memberName.setText("删除");
            }

            //holder.memberIcon.setBackgroundResource(R.drawable.bottom_action_border);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isDelMode = !isDelMode;
                    notifyDataSetChanged();
//                    if (mTailListener != null) {
//                        mTailListener.forwardDeleteMember(mGroupInfo);
//                    }
                }
            });
        } else {
            //其他
            //if (!TextUtils.isEmpty(info.getIconUrl())) {
            Glide.with(TUIKit.getAppContext()).load(URLConst.URL_ICON + info.getAccount()).circleCrop().into(holder.memberIcon);
            //GlideEngine.loadCornerImage(holder.memberIcon, URLConst.URL_ICON + info.getAccount(), null, 90);
            //}
            if (!TextUtils.isEmpty(info.getUserName())) {
                holder.memberName.setText(info.getUserName());
            } else {
                holder.memberName.setText(info.getAccount());
            }
            view.setOnClickListener(null);
            holder.memberIcon.setBackground(null);
            if (isDelMode && i != 2) {
                holder.group_member_del.setVisibility(View.VISIBLE);
            } else {
                holder.group_member_del.setVisibility(View.GONE);
            }

            //点击头像跳转到单聊
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (info == null || info.getAccount() == null) {
                        return;
                    }

                    if (info.getAccount().equals(Configs.unionId)) {
                        Log.e("herb", "点击自己不跳转>>");
                    } else {
                        Log.e("herb", "点击>>" + info.getUserName());
                        //跳转单聊
                        String url = "scheme://baza/IMChatActivity";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        ChatInfo chatInfo = new ChatInfo();
                        chatInfo.setType(TIMConversationType.C2C);//个人对话还是群组对话
                        chatInfo.setId(info.getAccount());//对话ID
                        chatInfo.setChatName(info.getUserName());//对话标题
                        //chatInfo.setCompany(info.);
                        //chatInfo.setPost(post);
                        intent.putExtra(Configs.CHAT_INFO, chatInfo);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        TUIKit.getAppContext().startActivity(intent);
                    }
                }
            });
        }
        holder.group_member_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除
                GroupInfoProvider provider = new GroupInfoProvider();
                provider.loadGroupInfo(mGroupInfo);
                List<GroupMemberInfo> groupMembers = new ArrayList<>();
                Log.e("herb", "删除群成员第几个>>" + (i - 2));
                groupMembers.add(mGroupInfo.getMemberDetails().get(i - 2));
                provider.removeGroupMembers(groupMembers, new IUIKitCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.toastLongMessage("删除成员成功");
                        mGroupMembers.remove(i);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        ToastUtil.toastLongMessage("删除成员失败:" + errCode + "=" + errMsg);
                    }
                });
            }
        });

        return view;
    }

    public void setDataSource(GroupInfo info, boolean isMore, boolean isAll) {
        mGroupInfo = info;
        mGroupMembers.clear();

        Log.e("herb", "chatInfo全员>>setDataSource>>" + isAll);

        //添加删除按钮
        if (!isAll) {
            if (TextUtils.equals(info.getGroupType(), TUIKitConstants.GroupType.TYPE_PRIVATE)) {
                // 公开群/聊天室 只有APP管理员可以邀请他人入群
                GroupMemberInfo add = new GroupMemberInfo();
                add.setMemberType(ADD_TYPE);
                mGroupMembers.add(add);
            }

            GroupMemberInfo self = null;
            for (int i = 0; i < mGroupMembers.size(); i++) {
                GroupMemberInfo memberInfo = mGroupMembers.get(i);
                if (TextUtils.equals(memberInfo.getAccount(), TIMManager.getInstance().getLoginUser())) {
                    self = memberInfo;
                    break;
                }
            }

            if (info.isOwner() || (self != null && self.getMemberType() == TIMGroupMemberRoleType.ROLE_TYPE_ADMIN)) {
                GroupMemberInfo del = new GroupMemberInfo();
                del.setMemberType(DEL_TYPE);
                mGroupMembers.add(del);
            }
        }

        //其他成员
        List<GroupMemberInfo> members = info.getMemberDetails();
        if (members != null) {
            int shootMemberCount = 0;
            if (TextUtils.equals(info.getGroupType(), TUIKitConstants.GroupType.TYPE_PRIVATE)) {
                if (info.isOwner()) {
                    shootMemberCount = members.size() > OWNER_PRIVATE_MAX_LIMIT ? OWNER_PRIVATE_MAX_LIMIT : members.size();
                } else {
                    shootMemberCount = members.size() > NORMAL_PRIVATE_MAX_LIMIT ? NORMAL_PRIVATE_MAX_LIMIT : members.size();
                }
            } else if (TextUtils.equals(info.getGroupType(), TUIKitConstants.GroupType.TYPE_PUBLIC)) {
                if (info.isOwner()) {
                    shootMemberCount = members.size() > OWNER_PUBLIC_MAX_LIMIT ? OWNER_PUBLIC_MAX_LIMIT : members.size();
                } else {
                    shootMemberCount = members.size() > NORMAL_PUBLIC_MAX_LIMIT ? NORMAL_PUBLIC_MAX_LIMIT : members.size();
                }
            } else if (TextUtils.equals(info.getGroupType(), TUIKitConstants.GroupType.TYPE_CHAT_ROOM)) {
                if (info.isOwner()) {
                    shootMemberCount = members.size() > OWNER_CHATROOM_MAX_LIMIT ? OWNER_CHATROOM_MAX_LIMIT : members.size();
                } else {
                    shootMemberCount = members.size() > NORMAL_CHATROOM_MAX_LIMIT ? NORMAL_CHATROOM_MAX_LIMIT : members.size();
                }
            }
            if (isMore) {
                for (int i = 0; i < shootMemberCount; i++) {
                    mGroupMembers.add(members.get(i));
                }
            } else {
                for (int i = 0; i < shootMemberCount; i++) {
                    if (mGroupMembers.size() < 14) {
                        mGroupMembers.add(members.get(i));
                    }
                }
            }

            BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    private class MyViewHolder {
        private ImageView memberIcon, group_member_del;
        private TextView memberName;
    }
}
