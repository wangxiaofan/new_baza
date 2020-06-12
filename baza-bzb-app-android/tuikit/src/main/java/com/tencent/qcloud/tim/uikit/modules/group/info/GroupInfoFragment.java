package com.tencent.qcloud.tim.uikit.modules.group.info;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.base.BaseFragment;
import com.tencent.qcloud.tim.uikit.modules.group.member.GroupMemberDeleteFragment;
import com.tencent.qcloud.tim.uikit.modules.group.member.GroupMemberInviteFragment;
import com.tencent.qcloud.tim.uikit.modules.group.member.GroupMemberManagerFragment;
import com.tencent.qcloud.tim.uikit.modules.group.member.IGroupMemberRouter;
import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;

import java.util.ArrayList;


@SuppressLint("ValidFragment")
public class GroupInfoFragment extends BaseFragment {

    private View mBaseView;
    private GroupInfoLayout mGroupInfoLayout;
    private boolean isAll;

    public GroupInfoFragment(boolean isAll) {
        this.isAll = isAll;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.group_info_fragment, container, false);
        initView();
        return mBaseView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 1000) {
            //添加成员回传
            ArrayList<String> memberIds = new ArrayList<>();
            memberIds.addAll(data.getStringArrayListExtra("LIST"));
            Log.e("herb", "添加条数>>" + memberIds);
            //更新界面
            getActivity().finish();
        }
    }

    private void initView() {
        mGroupInfoLayout = mBaseView.findViewById(R.id.group_info_layout);
        mGroupInfoLayout.setIsAll(isAll);
        mGroupInfoLayout.setGroupId(getArguments().getString(TUIKitConstants.Group.GROUP_ID));
        mGroupInfoLayout.setRouter(new IGroupMemberRouter() {
            @Override
            public void forwardListMember(GroupInfo info) {
                GroupMemberManagerFragment fragment = new GroupMemberManagerFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(TUIKitConstants.Group.GROUP_INFO, info);
                fragment.setArguments(bundle);
                forward(fragment, false);
            }

            @Override
            public void forwardAddMember(GroupInfo info) {
//                GroupMemberInviteFragment fragment = new GroupMemberInviteFragment();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(TUIKitConstants.Group.GROUP_INFO, info);
//                fragment.setArguments(bundle);
//                forward(fragment, false);
                //跳转搜索页 -> 邀请成功后群成员要更新数据
                String url = "scheme://baza/IMAddGroupChatActivity?text=add";//这个就是刚刚前面在AndroidManManifest中设置的，?后面是需要传去的参数，但是不要太长
                //host和path
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.putExtra(TUIKitConstants.Group.GROUP_INFO, info);
                startActivityForResult(intent, 1000);
                //getActivity().finish();
            }

            @Override
            public void forwardDeleteMember(GroupInfo info) {
                GroupMemberDeleteFragment fragment = new GroupMemberDeleteFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(TUIKitConstants.Group.GROUP_INFO, info);
                fragment.setArguments(bundle);
                forward(fragment, false);
            }
        });
    }
}
