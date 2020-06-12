package com.baza.android.bzw.businesscontroller.message;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baza.android.bzw.manager.IMManager;
import com.bznet.android.rcbox.R;
import com.slib.utils.ToastUtil;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageOfflinePushSettings;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.conversation.ConversationManager;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.bean.IMGetTeamMembersResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetTeamsResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMSendManySystemMessageRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMSendManySystemMessageResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMSendSystemMessageRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMSendSystemMessageResponseBean;
import com.tencent.qcloud.tim.uikit.modules.chat.GroupChatManagerKit;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatManagerKit;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.input.InputLayout;
import com.tencent.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.tencent.qcloud.tim.uikit.modules.group.info.GroupInfoProvider;
import com.tencent.qcloud.tim.uikit.modules.group.member.GroupMemberInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;
import com.tencent.qcloud.tim.uikit.utils.TUIKitLog;
import com.tencent.qcloud.tim.uikit.utils.retrofit.RetrofitHelper;
import com.tencent.qcloud.tim.uikit.utils.retrofit.WaitingView;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import baza.dialog.simpledialog.MaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IMAddGroupChatActivity extends Activity implements View.OnClickListener {

    private String TAG = IMAddGroupChatActivity.class.getSimpleName();
    private ListView add_group_member_lv;
    private IMAddGroupChatAdapter addGroupChatAdapter;
    public static List<IMGetTeamsResponseBean.DataBean> beanList = new ArrayList<>();

    private TextView add_group_bottom_tv;
    private TextView layout_title_text;
    private ImageView add_group_bottom_iv;
    private Button add_group_bottom_btn;
    private MaterialDialog mMaterialDialog;
    public static int chooseNum = 0;//选择的人数
    private ArrayList<GroupMemberInfo> mMembers = new ArrayList<>();//群聊成员
    public int fromWhat;//1.创建群聊 | 2.添加成员 | 3.简历分享
    public static GroupInfo mGroupInfo;

    public static IMAddGroupChatActivity imAddGroupChatActivity;
    private String details = "";//简历详情

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chooseNum = 0;
        imAddGroupChatActivity = this;
        setContentView(R.layout.activity_add_group_chat);
        findViewById(R.id.layout_title_back).setOnClickListener(this);
        layout_title_text = findViewById(R.id.layout_title_text);
        add_group_member_lv = findViewById(R.id.add_group_member_lv);
        add_group_bottom_tv = findViewById(R.id.add_group_bottom_tv);
        add_group_bottom_iv = findViewById(R.id.add_group_bottom_iv);
        add_group_bottom_btn = findViewById(R.id.add_group_bottom_btn);
        add_group_bottom_tv.setOnClickListener(this);
        add_group_bottom_iv.setOnClickListener(this);
        add_group_bottom_btn.setOnClickListener(this);
        findViewById(R.id.msg_search_lin).setOnClickListener(this);
        ((TextView) findViewById(R.id.msg_search_lin_text)).setText("搜索你的好友");

        //初始化列表
        if (beanList != null) {
            beanList.clear();
        }
        add_group_member_lv.setVisibility(View.GONE);
        addGroupChatAdapter = new IMAddGroupChatAdapter(this, beanList);
        add_group_member_lv.setAdapter(addGroupChatAdapter);

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
            //Log.e("herb", "str.length()>>" + strLength);

            if (group_name.getText().toString().trim().equals("")) {
                ToastUtil.showToast(IMAddGroupChatActivity.this, "请输入群聊名称");
            } else if (strLength > 30) {
                ToastUtil.showToast(IMAddGroupChatActivity.this, "群名长度超过限制");
            } else {
                mMaterialDialog.dismiss();
                IMAddGroupChatActivity.this.createGroup(group_name.getText().toString());
            }
        });

        //获取公司团队
        getTeams();

        //获取添加成员
        if (getIntent() != null) {
            details = getIntent().getStringExtra("DETAILS");
            if (getIntent().getStringExtra("TYPE") != null && getIntent().getStringExtra("TYPE").equals("3")) {
                //分享过来的
                fromWhat = 3;
                layout_title_text.setText("选择联系人");
            } else {
                Uri data = getIntent().getData();
                String getText = "";
                if (data != null) {
                    getText = data.getQueryParameter("text");
                }
                if (data != null && getText.equals("add")) {
                    Log.e("herb", "添加成员过来的>>" + mGroupInfo);
                    //添加成员过来的
                    fromWhat = 2;
                    mGroupInfo = (GroupInfo) getIntent().getSerializableExtra(TUIKitConstants.Group.GROUP_INFO);
                    layout_title_text.setText("选择群成员");
                } else {
                    //创建群聊过来的
                    Log.e("herb", "创建群聊过来的>>");
                    fromWhat = 1;
                    layout_title_text.setText("发起群聊");
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_title_back:
                //返回
                finish();
                break;
            case R.id.add_group_bottom_tv:
            case R.id.add_group_bottom_iv:
                //展开
                if (chooseNum >= 2) {
                    startActivityForResult(new Intent(this, IMAddGroupChatChooseActivity.class), 1000);
                }
                break;
            case R.id.add_group_bottom_btn:
                //确定
                if (fromWhat == 1) {
                    if (chooseNum < 2) {
                        ToastUtil.showToast(this, "至少选择2位联系人");
                        return;
                    }
                    mMaterialDialog.show();
                } else if (fromWhat == 2) {
                    Log.e("herb", "添加群聊，点击确定>>chooseNum>>" + chooseNum);
                    if (chooseNum < 1) {
                        ToastUtil.showToast(this, "至少选择1位联系人");
                        return;
                    }
                    addGroup();
                } else if (fromWhat == 3) {
                    if (chooseNum < 1) {
                        ToastUtil.showToast(this, "至少选择1位联系人");
                        return;
                    }
                    //分享 -> 获取成员信息，跳转分享页面
                    shareResume();
                }
                break;

            case R.id.msg_search_lin:
                //搜索联系人
                Intent intent = new Intent(IMAddGroupChatActivity.this, IMAddGroupChatSearchActivity.class);
                intent.putExtra("fromWhat", fromWhat);
                intent.putExtra("DETAILS", details);
                intent.putExtra(TUIKitConstants.Group.GROUP_INFO, mGroupInfo);
                startActivityForResult(intent, 1000);
                break;
        }
    }

    //分享简历
    private void shareResume() {

        List<String> idList = new ArrayList<>();
        for (int i = 0; i < beanList.size(); i++) {
            if (beanList.get(i) != null && beanList.get(i).getBean() != null && beanList.get(i).getBean().getData() != null && beanList.get(i).getBean().getData().size() > 0) {
                for (int j = 0; j < beanList.get(i).getBean().getData().size(); j++) {
                    if (beanList.get(i).getBean().getData().get(j).isChecked()) {
                        if (!idList.contains(beanList.get(i).getBean().getData().get(j).getUnionId())) {
                            idList.add(beanList.get(i).getBean().getData().get(j).getUnionId());
                        }
                    }
                }
            }
        }

        for (int i = 0; i < idList.size(); i++) {

            IMSendSystemMessageRequestBean bean = new IMSendSystemMessageRequestBean(details, idList.get(i));
            Log.e(TAG, "单人分享简历>>" + bean.toString());
            MessageInfo message = new MessageInfo();
            //组装内容
            TIMMessage timMessage = new TIMMessage();
            timMessage.setSender(Configs.unionId);
            TIMCustomElem timCustomElem = new TIMCustomElem();
            timCustomElem.setExt(details.getBytes());

            timMessage.addElement(timCustomElem);
            message.setElement(timCustomElem);
            //message.setExtra(bean);
            message.setId(idList.get(i));
            message.setTIMMessage(timMessage);

            //离线推送测试代码
            TIMMessageOfflinePushSettings settings = new TIMMessageOfflinePushSettings();
            settings.setExt("test".getBytes());
            TIMMessageOfflinePushSettings.AndroidSettings androidSettings = settings.getAndroidSettings();
            // OPPO必须设置ChannelID才可以收到推送消息，这个channelID需要和控制台一直
            androidSettings.setOPPOChannelID("tuikit");
            message.getTIMMessage().setOfflinePushSettings(settings);

            ConversationManager.getInstance().getConversation(TIMConversationType.C2C, idList.get(i)).sendMessage(message.getTIMMessage(), new TIMValueCallBack<TIMMessage>() {
                @Override
                public void onError(int i, String s) {
                    Log.e("herb", "分享失败>>" + s);
                    ToastUtil.showToast(IMAddGroupChatActivity.this, "分享失败");
                }


                @Override
                public void onSuccess(TIMMessage timMessage) {
                    Log.e("herb", "分享成功>>" + timMessage.toString());
                    ToastUtil.showToast(IMAddGroupChatActivity.this, "分享成功");
                    finish();
                }
            });


//            RetrofitHelper.getInstance().getServer().sendSystemMessage(URLConst.sendSystemMessage, bean).enqueue(new Callback<IMSendSystemMessageResponseBean>() {
//                @Override
//                public void onResponse(Call<IMSendSystemMessageResponseBean> call, Response<IMSendSystemMessageResponseBean> response) {
//                    WaitingView.getInstance().closeWaitingView();
//                    Log.e("herb", "分享成功>>" + response.body().toString());
//                    if (response != null && response.body() != null && response.body().isSucceeded()) {
//                        ToastUtil.showToast(IMAddGroupChatActivity.this, "分享成功");
//                        finish();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<IMSendSystemMessageResponseBean> call, Throwable t) {
//                    WaitingView.getInstance().closeWaitingView();
//                    Log.e("herb", "分享失败>>" + call.toString());
//                    ToastUtil.showToast(IMAddGroupChatActivity.this, "分享失败");
//                }
//            });
//        } else if (idList != null) {
//            Log.e(TAG, "多人分享简历");
//            IMSendManySystemMessageRequestBean bean = new IMSendManySystemMessageRequestBean(details, idList);
//            RetrofitHelper.getInstance().getServer().sendManySystemMessage(URLConst.sendManySystemMessage, bean).enqueue(new Callback<IMSendManySystemMessageResponseBean>() {
//                @Override
//                public void onResponse(Call<IMSendManySystemMessageResponseBean> call, Response<IMSendManySystemMessageResponseBean> response) {
//                    WaitingView.getInstance().closeWaitingView();
//                    Log.e("herb", "分享成功>>" + response.body().toString());
//                    if (response != null && response.body() != null && response.body().getData() != null) {
//                        if (response != null && response.body() != null && response.body().isSucceeded()) {
//                            ToastUtil.showToast(IMAddGroupChatActivity.this, "分享成功");
//                            finish();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<IMSendManySystemMessageResponseBean> call, Throwable t) {
//                    WaitingView.getInstance().closeWaitingView();
//                    Log.e("herb", "分享失败>>" + call.toString());
//                    ToastUtil.showToast(IMAddGroupChatActivity.this, "分享失败");
//                }
//            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            //更新数据
            Log.e("herb", "更新数据");
            addGroupChatAdapter.notifyDataSetChanged();
            add_group_bottom_tv.setText("已选择：" + chooseNum + "人");
            if (fromWhat == 1) {
                //创建群聊
                if (chooseNum >= 2) {
                    add_group_bottom_btn.setBackgroundResource(R.drawable.add_group_chat_sure_bg);
                    add_group_bottom_btn.setEnabled(true);
                } else {
                    add_group_bottom_btn.setBackgroundResource(R.drawable.add_group_chat_sure_bg_grey);
                    add_group_bottom_btn.setEnabled(false);
                }
            } else if (chooseNum == 2 || fromWhat == 3) {
                //添加成员
                if (chooseNum >= 1) {
                    add_group_bottom_btn.setBackgroundResource(R.drawable.add_group_chat_sure_bg);
                    add_group_bottom_btn.setEnabled(true);
                } else {
                    add_group_bottom_btn.setBackgroundResource(R.drawable.add_group_chat_sure_bg_grey);
                    add_group_bottom_btn.setEnabled(false);
                }
            }
        }
    }

    //获取公司团队接口
    public void getTeams() {
        Log.e("herb", "开始获取公司团队>>");
        WaitingView.getInstance().showWaitingView(this);
        RetrofitHelper.getInstance().getServer().getTeams(URLConst.getTeams).enqueue(new Callback<IMGetTeamsResponseBean>() {
            @Override
            public void onResponse(Call<IMGetTeamsResponseBean> call, Response<IMGetTeamsResponseBean> response) {
                WaitingView.getInstance().closeWaitingView();
                Log.e("herb", "获取公司团队成功>>" + response.body().toString());
                if (beanList != null) {
                    beanList.clear();
                }

                if (response.body().getData() != null) {
                    beanList.addAll(response.body().getData().getChildTeams());
                    for (int i = 0; i < beanList.size(); i++) {
                        //获取子成员
                        getTeamMembers(beanList.get(i));
                    }
                }
            }

            @Override
            public void onFailure(Call<IMGetTeamsResponseBean> call, Throwable t) {
                WaitingView.getInstance().closeWaitingView();
                Log.e("herb", "获取公司团队失败>>" + call.toString());
            }
        });
    }

    //获取团队成员接口
    public void getTeamMembers(IMGetTeamsResponseBean.DataBean dataBean) {
        Log.e("herb", "开始获取团队成员>>");
        Map<String, String> params = new HashMap<>();
        params.put("teamId", dataBean.getTeamId());
        RetrofitHelper.getInstance().getServer().getTeamMembers(URLConst.getTeamMembers, params).enqueue(new Callback<IMGetTeamMembersResponseBean>() {
            @Override
            public void onResponse(Call<IMGetTeamMembersResponseBean> call, Response<IMGetTeamMembersResponseBean> response) {
                if (response.body() != null && response.body().getData() != null) {
                    Log.e("herb", dataBean.getTeamName() + ">>" + response.body().getData().size());
                    dataBean.setBean(response.body());
                    for (int i = 0; i < dataBean.getChildTeams().size(); i++) {
                        getTeamMembers(dataBean.getChildTeams().get(i));
                    }
                }

                //更新数据
                add_group_member_lv.setVisibility(View.VISIBLE);
                addGroupChatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<IMGetTeamMembersResponseBean> call, Throwable t) {
                Log.e("herb", "获取团队成员失败>>" + call.toString());
            }
        });
    }

    //更新选择数目
    public void updateChooseNum(boolean isAdd) {
        if (isAdd) {
            chooseNum++;
        } else if (chooseNum > 0) {
            chooseNum--;
        }

        add_group_bottom_tv.setText("已选择：" + chooseNum + "人");
        if (fromWhat == 1) {
            //创建群聊
            if (chooseNum >= 2) {
                add_group_bottom_btn.setBackgroundResource(R.drawable.add_group_chat_sure_bg);
                add_group_bottom_btn.setEnabled(true);
            } else {
                add_group_bottom_btn.setBackgroundResource(R.drawable.add_group_chat_sure_bg_grey);
                add_group_bottom_btn.setEnabled(false);
            }
        } else if (fromWhat == 2 || fromWhat == 3) {
            //添加群员
            if (chooseNum >= 1) {
                add_group_bottom_btn.setBackgroundResource(R.drawable.add_group_chat_sure_bg);
                add_group_bottom_btn.setEnabled(true);
            } else {
                add_group_bottom_btn.setBackgroundResource(R.drawable.add_group_chat_sure_bg_grey);
                add_group_bottom_btn.setEnabled(false);
            }
        }
    }

    //调用腾讯SDK创建群聊
    public void createGroup(String groupName) {
        //准备成员数据
        GroupMemberInfo memberInfo = new GroupMemberInfo();
        memberInfo.setAccount(Configs.unionId);
        memberInfo.setIconUrl(URLConst.URL_ICON + Configs.unionId);
        mMembers.add(0, memberInfo);

        for (int i = 0; i < beanList.size(); i++) {
            if (beanList.get(i) != null && beanList.get(i).getBean() != null && beanList.get(i).getBean().getData() != null && beanList.get(i).getBean().getData().size() > 0) {
                for (int j = 0; j < beanList.get(i).getBean().getData().size(); j++) {
                    if (beanList.get(i).getBean().getData().get(j).isChecked()) {

                        boolean isContain = false;
                        for (int k = 0; k < mMembers.size(); k++) {
                            if (mMembers.get(k).getAccount().equals(beanList.get(i).getBean().getData().get(j).getUnionId())) {
                                isContain = true;
                                break;
                            }
                        }
                        if (!isContain) {
                            memberInfo = new GroupMemberInfo();
                            memberInfo.setAccount(beanList.get(i).getBean().getData().get(j).getUnionId());
                            memberInfo.setIconUrl(URLConst.URL_ICON + beanList.get(i).getBean().getData().get(j).getUnionId());
                            mMembers.add(memberInfo);
                        }
                    }
                }
            }
        }

        Log.e("herb", "群聊人员数量>>" + mMembers.size());
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

                Intent intent = new Intent(IMAddGroupChatActivity.this, IMChatActivity.class);
                intent.putExtra(Constants.CHAT_INFO, chatInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                com.tencent.qcloud.tim.uikit.utils.ToastUtil.toastLongMessage("createGroupChat fail:" + errCode + "=" + errMsg);
            }
        });

    }

    //添加成功
    public void addGroup() {
        //准备成员数据
        ArrayList<String> memberIds = new ArrayList<>();

        for (int i = 0; i < beanList.size(); i++) {
            if (beanList.get(i) != null && beanList.get(i).getBean() != null && beanList.get(i).getBean().getData() != null && beanList.get(i).getBean().getData().size() > 0) {
                for (int j = 0; j < beanList.get(i).getBean().getData().size(); j++) {
                    if (beanList.get(i).getBean().getData().get(j).isChecked()) {
                        if (!memberIds.contains(beanList.get(i).getBean().getData().get(j).getUnionId())) {
                            memberIds.add(beanList.get(i).getBean().getData().get(j).getUnionId());
                        }
                    }
                }
            }
        }

        //添加成员
        GroupInfoProvider provider = new GroupInfoProvider();
        provider.loadGroupInfo(mGroupInfo);
        Log.e("herb", "开始添加群聊>>" + memberIds.size());
        provider.inviteGroupMembers(memberIds, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                Log.e("herb", "添加群聊成功>>");
                if (data instanceof String) {
                    com.tencent.qcloud.tim.uikit.utils.ToastUtil.toastLongMessage(data.toString());
                } else {
                    com.tencent.qcloud.tim.uikit.utils.ToastUtil.toastLongMessage("邀请成员成功");
                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra("LIST", memberIds);
                setResult(1000, intent);
                finish();
                beanList.clear();
                mGroupInfo = null;
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Log.e("herb", "添加群聊失败>>");
                com.tencent.qcloud.tim.uikit.utils.ToastUtil.toastLongMessage("邀请成员失败:" + errCode + "=" + errMsg);
            }
        });
    }

    //更新其他列表的选项
    public static void updateListSelect(String id, boolean isSelect) {
        for (int i = 0; i < beanList.size(); i++) {
            if (beanList.get(i) != null && beanList.get(i).getBean() != null && beanList.get(i).getBean().getData() != null && beanList.get(i).getBean().getData().size() > 0) {
                for (int j = 0; j < beanList.get(i).getBean().getData().size(); j++) {
                    if (beanList.get(i).getBean().getData().get(j).getUnionId().equals(id)) {
                        beanList.get(i).getBean().getData().get(j).setChecked(isSelect);
                    }
                }
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        //清空数据
        beanList.size();
        mGroupInfo = null;
    }
}
