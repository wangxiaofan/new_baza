package com.baza.android.bzw.businesscontroller.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bznet.android.rcbox.R;
import com.slib.utils.ToastUtil;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.bean.IMGetTeamMembersResponseBean;
import com.tencent.qcloud.tim.uikit.modules.chat.GroupChatManagerKit;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.input.InputLayout;
import com.tencent.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.tencent.qcloud.tim.uikit.modules.group.member.GroupMemberInfo;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import baza.dialog.simpledialog.MaterialDialog;

public class IMAddGroupChatChooseActivity extends Activity {

    private ListView add_group_choose;
    private TextView layout_title_text, layout_title_right_text;
    private MaterialDialog mMaterialDialog;
    private ArrayList<GroupMemberInfo> mMembers = new ArrayList<>();//群聊成员

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_choose);
        add_group_choose = findViewById(R.id.add_group_choose);
        layout_title_text = findViewById(R.id.layout_title_text);
        layout_title_right_text = findViewById(R.id.layout_title_right_text);

        //人数
        layout_title_text.setText("已选择" + IMAddGroupChatActivity.chooseNum + "人");

        //初始化填写群聊名弹出框
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_group_chat_name, null);
        EditText group_name = contentView.findViewById(R.id.group_name);
        mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setCancelable(true);
        mMaterialDialog.setCanceledOnTouchOutside(false);
        mMaterialDialog.setMessageView(contentView);
        mMaterialDialog.buildClickListener(view -> {
            //取消
            mMaterialDialog.dismiss();
        }, view -> {
            //确定
            int strLength = 0;
            try {
                strLength = group_name.getText().toString().getBytes("UTF-8").length;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.e("herb", "str.length()>>" + strLength);

            if (group_name.getText().toString().trim().equals("")) {
                ToastUtil.showToast(IMAddGroupChatChooseActivity.this, "请输入群聊名称");
            } else if (strLength > 30) {
                ToastUtil.showToast(IMAddGroupChatChooseActivity.this, "群名长度超过限制");
            } else {
                mMaterialDialog.dismiss();
                IMAddGroupChatChooseActivity.this.createGroup(group_name.getText().toString());
            }
        });

        findViewById(R.id.layout_title_back).setOnClickListener(view -> {
            //返回
            finish();
        });


        layout_title_right_text.setOnClickListener(view -> {
            //确定
            if (IMAddGroupChatActivity.chooseNum < 2) {
                ToastUtil.showToast(this, "至少选择2位联系人");
                return;
            }
            mMaterialDialog.show();
        });

        //设置数据
        List<IMGetTeamMembersResponseBean.DataBean> dataBeanList = new ArrayList<>();
        for (int i = 0; i < IMAddGroupChatActivity.beanList.size(); i++) {
            if (IMAddGroupChatActivity.beanList.get(i) != null && IMAddGroupChatActivity.beanList.get(i).getBean() != null && IMAddGroupChatActivity.beanList.get(i).getBean().getData() != null && IMAddGroupChatActivity.beanList.get(i).getBean().getData().size() > 0) {
                for (int j = 0; j < IMAddGroupChatActivity.beanList.get(i).getBean().getData().size(); j++) {
                    if (IMAddGroupChatActivity.beanList.get(i).getBean().getData().get(j).isChecked()) {
                        boolean isContain = false;
                        for (int k = 0; k < dataBeanList.size(); k++) {
                            if (IMAddGroupChatActivity.beanList.get(i).getBean().getData().get(j).getUnionId().equals(dataBeanList.get(k).getUnionId())) {
                                isContain = true;
                            }
                        }
                        if (!isContain) {
                            dataBeanList.add(IMAddGroupChatActivity.beanList.get(i).getBean().getData().get(j));
                        }
                    }
                }
            }
        }

        add_group_choose.setAdapter(new IMAddGroupChatAdapterSecondChoose(this, dataBeanList));
    }

    //更新其他列表的选项
    public static void updateListSelect(String id, boolean isSelect) {
        for (int i = 0; i < IMAddGroupChatActivity.beanList.size(); i++) {
            if (IMAddGroupChatActivity.beanList.get(i) != null && IMAddGroupChatActivity.beanList.get(i).getBean() != null && IMAddGroupChatActivity.beanList.get(i).getBean().getData() != null && IMAddGroupChatActivity.beanList.get(i).getBean().getData().size() > 0) {
                for (int j = 0; j < IMAddGroupChatActivity.beanList.get(i).getBean().getData().size(); j++) {
                    if (IMAddGroupChatActivity.beanList.get(i).getBean().getData().get(j).getUnionId().equals(id)) {
                        IMAddGroupChatActivity.beanList.get(i).getBean().getData().get(j).setChecked(isSelect);
                    }
                }
            }
        }
    }

    //更新选择数目
    public void updateChooseNum(boolean isAdd) {
        if (isAdd) {
            IMAddGroupChatActivity.chooseNum++;
        } else if (IMAddGroupChatActivity.chooseNum > 0) {
            IMAddGroupChatActivity.chooseNum--;
        }

        layout_title_text.setText("已选择：" + IMAddGroupChatActivity.chooseNum + "人");
        if (IMAddGroupChatActivity.chooseNum >= 2) {
            layout_title_right_text.setTextColor(getResources().getColor(R.color.choose_text_coolor_d));
            layout_title_right_text.setEnabled(true);
        } else {
            layout_title_right_text.setTextColor(getResources().getColor(R.color.choose_text_coolor_u));
            layout_title_right_text.setEnabled(false);
        }
    }

    //调用腾讯SDK创建群聊
    public void createGroup(String groupName) {
        //准备成员数据
        GroupMemberInfo memberInfo = new GroupMemberInfo();
        memberInfo.setAccount(Configs.unionId);
        memberInfo.setIconUrl(URLConst.URL_ICON + Configs.unionId);
        mMembers.add(0, memberInfo);

        for (int i = 0; i < IMAddGroupChatActivity.beanList.size(); i++) {
            if (IMAddGroupChatActivity.beanList.get(i) != null && IMAddGroupChatActivity.beanList.get(i).getBean() != null && IMAddGroupChatActivity.beanList.get(i).getBean().getData() != null && IMAddGroupChatActivity.beanList.get(i).getBean().getData().size() > 0) {
                for (int j = 0; j < IMAddGroupChatActivity.beanList.get(i).getBean().getData().size(); j++) {
                    if (IMAddGroupChatActivity.beanList.get(i).getBean().getData().get(j).isChecked()) {

                        boolean isContain = false;
                        for (int k = 0; k < mMembers.size(); k++) {
                            if (mMembers.get(k).getAccount().equals(IMAddGroupChatActivity.beanList.get(i).getBean().getData().get(j).getUnionId())) {
                                isContain = true;
                                break;
                            }
                        }
                        if (!isContain) {
                            memberInfo = new GroupMemberInfo();
                            memberInfo.setAccount(IMAddGroupChatActivity.beanList.get(i).getBean().getData().get(j).getUnionId());
                            memberInfo.setIconUrl(URLConst.URL_ICON + IMAddGroupChatActivity.beanList.get(i).getBean().getData().get(j).getUnionId());
                            mMembers.add(memberInfo);
                        }
                    }
                }
            }
        }

        final GroupInfo groupInfo = new GroupInfo();
        groupInfo.setChatName(groupName);
        groupInfo.setGroupName(groupName);
        groupInfo.setMemberDetails(mMembers);
        groupInfo.setGroupType("Private");
        groupInfo.setJoinType(-1);

        //创建群聊
        String groupId = GroupChatManagerKit.generateGroupId();
        GroupChatManagerKit.createGroupChat(groupInfo, groupId, URLConst.URL_ICON_GROUP + groupId, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                ChatInfo chatInfo = new ChatInfo();
                chatInfo.setType(TIMConversationType.Group);
                chatInfo.setId(data.toString());
                chatInfo.setChatName(groupInfo.getGroupName());

                InputLayout.type = TIMConversationType.Group;
                InputLayout.id = data.toString();

                Intent intent = new Intent(IMAddGroupChatChooseActivity.this, IMChatActivity.class);
                intent.putExtra(Constants.CHAT_INFO, chatInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                IMAddGroupChatActivity.imAddGroupChatActivity.finish();
                IMAddGroupChatActivity.imAddGroupChatActivity = null;
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                com.tencent.qcloud.tim.uikit.utils.ToastUtil.toastLongMessage("createGroupChat fail:" + errCode + "=" + errMsg);
            }
        });

    }
}
