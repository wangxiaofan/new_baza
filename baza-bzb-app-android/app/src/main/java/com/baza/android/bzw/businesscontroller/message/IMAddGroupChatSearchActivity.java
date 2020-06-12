package com.baza.android.bzw.businesscontroller.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bznet.android.rcbox.R;
import com.slib.utils.ToastUtil;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageOfflinePushSettings;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.conversation.ConversationManager;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.bean.IMCheckInnerRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetExternalRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetExternalResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetTeamMembersResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetTeamsResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMSearchBean;
import com.tencent.qcloud.tim.uikit.bean.IMSearchRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMSendManySystemMessageRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMSendManySystemMessageResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMSendSystemMessageRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMSendSystemMessageResponseBean;
import com.tencent.qcloud.tim.uikit.modules.chat.GroupChatManagerKit;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.input.InputLayout;
import com.tencent.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.tencent.qcloud.tim.uikit.modules.group.info.GroupInfoProvider;
import com.tencent.qcloud.tim.uikit.modules.group.member.GroupMemberInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;
import com.tencent.qcloud.tim.uikit.utils.retrofit.RetrofitHelper;
import com.tencent.qcloud.tim.uikit.utils.retrofit.WaitingView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import baza.dialog.simpledialog.MaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IMAddGroupChatSearchActivity extends Activity implements View.OnClickListener {

    private String TAG = "IMAddGroupChatSearchActivity";
    protected InputMethodManager mImm;
    private EditText search_ed;
    private String searchText;//输入文字
    private ListView layout_group_chat_lv_n, layout_group_chat_lv_w;
    private TextView group_chat_search_lin_n_tv, group_chat_search_lin_w_tv;
    private View group_chat_search_lin_n_v, group_chat_search_lin_w_v;
    private LinearLayout group_chat_search_details;
    private RelativeLayout group_chat_search_bottom;
    private Button add_group_bottom_btn;

    private List<IMGetTeamMembersResponseBean.DataBean> dataBeans = new ArrayList<>();//全部成员数据
    private List<IMGetTeamMembersResponseBean.DataBean> dataBeansN = new ArrayList<>();//搜索到的内部成员数据
    private List<IMGetExternalResponseBean.DataBean.RecordsBean> dataBeansW = new ArrayList<>();//搜索到的外部成员数据

    private IMAddGroupChatSearchAdapterN imSearchAdapterN;
    private IMAddGroupChatSearchAdapterW imSearchAdapterW;

    //选择的人数
    private int chooseNum;
    private TextView add_group_bottom_tv;
    private ImageView add_group_bottom_iv;
    private MaterialDialog mMaterialDialog;
    private ArrayList<GroupMemberInfo> mMembers = new ArrayList<>();//群聊成员

    public int fromWhat;//1.创建群聊 | 2.添加成员
    private GroupInfo mGroupInfo;
    public static List<IMGetTeamMembersResponseBean.DataBean> checkMemberList = new ArrayList<>();//已选成员列表

    private TextView tv_null;
    private String details = "";//简历详情

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_chat_search);
        search_ed = findViewById(R.id.search_ed);
        layout_group_chat_lv_n = findViewById(R.id.layout_group_chat_lv_n);
        layout_group_chat_lv_w = findViewById(R.id.layout_group_chat_lv_w);
        findViewById(R.id.group_chat_search_lin_n).setOnClickListener(this);
        findViewById(R.id.group_chat_search_lin_w).setOnClickListener(this);
        findViewById(R.id.search_back).setOnClickListener(this);
        add_group_bottom_btn = findViewById(R.id.add_group_bottom_btn);
        add_group_bottom_btn.setOnClickListener(this);
        group_chat_search_lin_n_tv = findViewById(R.id.group_chat_search_lin_n_tv);
        group_chat_search_lin_w_tv = findViewById(R.id.group_chat_search_lin_w_tv);
        group_chat_search_lin_n_v = findViewById(R.id.group_chat_search_lin_n_v);
        group_chat_search_lin_w_v = findViewById(R.id.group_chat_search_lin_w_v);
        add_group_bottom_tv = findViewById(R.id.add_group_bottom_tv);
        add_group_bottom_iv = findViewById(R.id.add_group_bottom_iv);
        add_group_bottom_tv.setOnClickListener(this);
        add_group_bottom_iv.setOnClickListener(this);
        group_chat_search_details = findViewById(R.id.group_chat_search_details);
        group_chat_search_bottom = findViewById(R.id.group_chat_search_bottom);
        group_chat_search_details.setVisibility(View.GONE);
        group_chat_search_bottom.setVisibility(View.GONE);
        tv_null = findViewById(R.id.tv_null);

        //获取已选择成员
        for (int i = 0; i < IMAddGroupChatActivity.beanList.size(); i++) {
            if (IMAddGroupChatActivity.beanList != null && IMAddGroupChatActivity.beanList.get(i) != null && IMAddGroupChatActivity.beanList.get(i).getBean() != null && IMAddGroupChatActivity.beanList.get(i).getBean().getData() != null && IMAddGroupChatActivity.beanList.get(i).getBean().getData().size() > 0) {
                for (int j = 0; j < IMAddGroupChatActivity.beanList.get(i).getBean().getData().size(); j++) {
                    if (IMAddGroupChatActivity.beanList.get(i).getBean().getData().get(j).isChecked()) {
                        boolean isContain = false;
                        for (int k = 0; k < checkMemberList.size(); k++) {
                            if (checkMemberList.get(k).getUnionId().equals(IMAddGroupChatActivity.beanList.get(i).getBean().getData().get(j).getUnionId())) {
                                isContain = true;
                                break;
                            }
                        }
                        if (!isContain) {
                            checkMemberList.add(IMAddGroupChatActivity.beanList.get(i).getBean().getData().get(j));
                        }
                    }
                }
            }
        }

        //已选择
        chooseNum = checkMemberList.size();
        chooseNum++;//加上自己
        add_group_bottom_tv.setText("已选择：" + chooseNum + "人");
        if (chooseNum >= 2) {
            add_group_bottom_btn.setBackgroundResource(R.drawable.add_group_chat_sure_bg);
            add_group_bottom_btn.setEnabled(true);
        } else {
            add_group_bottom_btn.setBackgroundResource(R.drawable.add_group_chat_sure_bg_grey);
            add_group_bottom_btn.setEnabled(false);
        }

        //获取全部成员
        getAllTeamMembers();

        //键盘相关
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //进入搜索界面弹出键盘
        search_ed.postDelayed(() -> {
            search_ed.requestFocus();
            showSoftInput(search_ed);
        }, 500);
        search_ed.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                //回车触发搜索，获取外部成员
                searchText = search_ed.getText().toString().trim();
                if (searchText.equals("")) {
                    ToastUtil.showToast(IMAddGroupChatSearchActivity.this, "请输入搜索内容");
                    return true;
                }
                getExternalMembers();
                hideSoftInput();
            }
            return false;
        });

        //公司同事
        imSearchAdapterN = new IMAddGroupChatSearchAdapterN(this, dataBeansN);
        layout_group_chat_lv_n.setAdapter(imSearchAdapterN);

        //外部公司
        imSearchAdapterW = new IMAddGroupChatSearchAdapterW(this, dataBeansW);
        layout_group_chat_lv_w.setAdapter(imSearchAdapterW);

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
                ToastUtil.showToast(IMAddGroupChatSearchActivity.this, "请输入群聊名称");
            } else if (strLength > 30) {
                ToastUtil.showToast(IMAddGroupChatSearchActivity.this, "群名长度超过限制");
            } else {
                mMaterialDialog.dismiss();
                createGroup(group_name.getText().toString());
            }
        });

        //获取上层传参
        if (getIntent() != null) {
            details = getIntent().getStringExtra("DETAILS");
            fromWhat = getIntent().getIntExtra("fromWhat", 1);
            mGroupInfo = (GroupInfo) getIntent().getSerializableExtra(TUIKitConstants.Group.GROUP_INFO);
        }
    }

    //弹出键盘
    public void showSoftInput(View view) {
        try {
            if (mImm != null && mImm.isActive() && getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null)
                mImm.showSoftInput(view, 0);
        } catch (Exception e) {
            //ignore
        }
    }

    //隐藏键盘
    public void hideSoftInput() {
        try {
            if (mImm != null && mImm.isActive() && getCurrentFocus() != null
                    && getCurrentFocus().getWindowToken() != null)
                mImm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        0);
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_back:
                //返回
                hideSoftInput();
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
                    if (chooseNum < 1) {
                        ToastUtil.showToast(this, "至少选择1位联系人");
                        return;
                    }
                    addGroup();
                } else if (fromWhat == 3) {
                    //分享
                    if (chooseNum < 1) {
                        ToastUtil.showToast(this, "至少选择1位联系人");
                        return;
                    }
                    shareResume();
                }
                break;
            case R.id.group_chat_search_lin_n:
                //内部
                group_chat_search_lin_n_tv.setTextColor(getResources().getColor(R.color.choose_text_color));
                group_chat_search_lin_w_tv.setTextColor(getResources().getColor(R.color.choose_text_color_no));
                group_chat_search_lin_n_v.setVisibility(View.VISIBLE);
                group_chat_search_lin_w_v.setVisibility(View.INVISIBLE);
                layout_group_chat_lv_n.setVisibility(View.VISIBLE);
                layout_group_chat_lv_w.setVisibility(View.GONE);
                if (dataBeansN == null || dataBeansN.size() == 0) {
                    tv_null.setVisibility(View.VISIBLE);
                } else {
                    tv_null.setVisibility(View.GONE);
                }
                break;
            case R.id.group_chat_search_lin_w:
                //外部
                group_chat_search_lin_n_tv.setTextColor(getResources().getColor(R.color.choose_text_color_no));
                group_chat_search_lin_w_tv.setTextColor(getResources().getColor(R.color.choose_text_color));
                group_chat_search_lin_n_v.setVisibility(View.INVISIBLE);
                group_chat_search_lin_w_v.setVisibility(View.VISIBLE);
                layout_group_chat_lv_n.setVisibility(View.GONE);
                layout_group_chat_lv_w.setVisibility(View.VISIBLE);
                if (dataBeansW == null || dataBeansW.size() == 0) {
                    tv_null.setVisibility(View.VISIBLE);
                } else {
                    tv_null.setVisibility(View.GONE);
                }
                break;
        }
    }

    //分享简历
    private void shareResume() {

        List<String> idList = new ArrayList<>();

        for (int i = 0; i < dataBeansN.size(); i++) {
            idList.add(dataBeansN.get(i).getUnionId());
        }
        for (int i = 0; i < dataBeansW.size(); i++) {
            idList.add(dataBeansW.get(i).getUnionId());
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
                        ToastUtil.showToast(IMAddGroupChatSearchActivity.this, "分享失败");
                    }


                    @Override
                    public void onSuccess(TIMMessage timMessage) {
                        Log.e("herb", "分享成功>>" + timMessage.toString());
                        ToastUtil.showToast(IMAddGroupChatSearchActivity.this, "分享成功");
                        finish();
                    }
                });
            }

            /*IMSendSystemMessageRequestBean bean = new IMSendSystemMessageRequestBean(details, idList.get(0));
            Log.e(TAG, "单人分享简历>>" + bean.toString());
            RetrofitHelper.getInstance().getServer().sendSystemMessage(URLConst.sendSystemMessage, bean).enqueue(new Callback<IMSendSystemMessageResponseBean>() {
                @Override
                public void onResponse(Call<IMSendSystemMessageResponseBean> call, Response<IMSendSystemMessageResponseBean> response) {
                    WaitingView.getInstance().closeWaitingView();
                    Log.e("herb", "分享成功>>" + response.body().toString());
                    if (response != null && response.body() != null && response.body().isSucceeded()) {
                        ToastUtil.showToast(IMAddGroupChatSearchActivity.this, "分享成功");
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<IMSendSystemMessageResponseBean> call, Throwable t) {
                    WaitingView.getInstance().closeWaitingView();
                    Log.e("herb", "分享失败>>" + call.toString());
                    ToastUtil.showToast(IMAddGroupChatSearchActivity.this, "分享失败");
                }
            });
        } else if (idList != null) {
            IMSendManySystemMessageRequestBean bean = new IMSendManySystemMessageRequestBean(details, idList);
            Log.e(TAG, "多人分享简历>>" + bean.toString());
            RetrofitHelper.getInstance().getServer().sendManySystemMessage(URLConst.sendManySystemMessage, bean).enqueue(new Callback<IMSendManySystemMessageResponseBean>() {
                @Override
                public void onResponse(Call<IMSendManySystemMessageResponseBean> call, Response<IMSendManySystemMessageResponseBean> response) {
                    WaitingView.getInstance().closeWaitingView();
                    Log.e("herb", "分享成功>>" + response.body().toString());
                    if (response != null && response.body() != null && response.body().getData() != null) {
                        if (response != null && response.body() != null && response.body().isSucceeded()) {
                            ToastUtil.showToast(IMAddGroupChatSearchActivity.this, "分享成功");
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<IMSendManySystemMessageResponseBean> call, Throwable t) {
                    WaitingView.getInstance().closeWaitingView();
                    Log.e("herb", "分享失败>>" + call.toString());
                    ToastUtil.showToast(IMAddGroupChatSearchActivity.this, "分享失败");
                }
            });*/
    }

    //获取全部成员接口
    public void getAllTeamMembers() {
        Log.e("herb", "获取全部成员接口>>");
        Map<String, String> params = new HashMap<>();
        params.put("teamId", "0");
        RetrofitHelper.getInstance().getServer().getTeamMembers(URLConst.getTeamMembers, params).enqueue(new Callback<IMGetTeamMembersResponseBean>() {
            @Override
            public void onResponse(Call<IMGetTeamMembersResponseBean> call, Response<IMGetTeamMembersResponseBean> response) {
                Log.e("herb", "获取全部成员接口成功>>" + response.body().getData().size());
                if (dataBeans != null) {
                    dataBeans.clear();
                }
                dataBeans.addAll(response.body().getData());
            }

            @Override
            public void onFailure(Call<IMGetTeamMembersResponseBean> call, Throwable t) {
                Log.e("herb", "获取全部成员接口失败>>" + call.toString());
            }
        });
    }

    //获取外部成员接口
    public void getExternalMembers() {
        WaitingView.getInstance().showWaitingView(this);
        Log.e("herb", "获取外部成员接口>>searchText>>" + searchText);
        IMGetExternalRequestBean bean = new IMGetExternalRequestBean(searchText, 0, 20);
        RetrofitHelper.getInstance().getServer().getExternalMembers(URLConst.getExternalMembers, bean).enqueue(new Callback<IMGetExternalResponseBean>() {
            @Override
            public void onResponse(Call<IMGetExternalResponseBean> call, Response<IMGetExternalResponseBean> response) {
                WaitingView.getInstance().closeWaitingView();
                group_chat_search_details.setVisibility(View.VISIBLE);
                group_chat_search_bottom.setVisibility(View.VISIBLE);
                Log.e("herb", "获取外部成员接口成功>>" + response.body().getData().getRecords().size());
                if (dataBeansW != null) {
                    dataBeansW.clear();
                }
                if (dataBeansN != null) {
                    dataBeansN.clear();
                    dataBeansN.addAll(dataBeans);
                }
                dataBeansW.addAll(response.body().getData().getRecords());
                //过滤内部成员数据
                if (dataBeans == null) {
                    return;
                }

                List<IMGetTeamMembersResponseBean.DataBean> positionList = new ArrayList<>();
                for (int i = 0; i < dataBeansN.size(); i++) {
                    if (dataBeansN.get(i) != null && dataBeansN.get(i).getNickname() != null) {
                        //此成员在搜索范围
                        if (dataBeansW == null) {
                            return;
                        }
                        for (int j = 0; j < dataBeansW.size(); j++) {
                            if (dataBeansW.get(j) == null) {
                                return;
                            }
                            if (dataBeansN.get(i).getUnionId().equals(dataBeansW.get(j).getUnionId())) {
                                //移除 -> 记录位置，整体移除
                                positionList.add(dataBeansN.get(i));
                                return;
                            }
                        }
                    }
                }
                for (int i = 0; i < positionList.size(); i++) {
                    dataBeansN.remove(positionList.get(i));
                }

                Log.e("herb", "内部成员总长度>>" + dataBeansN.size());
                Log.e("herb", "外部搜索到的成员长度>>" + dataBeansW.size());

                List<IMGetTeamMembersResponseBean.DataBean> searchList = new ArrayList<>();
                for (int i = 0; i < dataBeansN.size(); i++) {
                    Log.e("herb", i + ">>dataBeansN.get(i).getRealName()>>" + dataBeansN.get(i).getRealName());
                    Log.e("herb", i + ">>dataBeansN.get(i).getNickname()>>" + dataBeansN.get(i).getNickname());

                    if (dataBeansN.get(i).getNickname().contains(searchText) ||
                            dataBeansN.get(i).getNickname().equals(searchText) ||
                            dataBeansN.get(i).getRealName().contains(searchText) ||
                            dataBeansN.get(i).getRealName().equals(searchText)) {
                        searchList.add(dataBeansN.get(i));
                    }
                }

                Log.e("herb", "内部搜索到的成员长度>>" + searchList.size());
                dataBeansN.clear();
                dataBeansN.addAll(searchList);

                for (int i = 0; i < checkMemberList.size(); i++) {
                    for (int j = 0; j < dataBeansN.size(); j++) {
                        if (checkMemberList.get(i).getUnionId().equals(dataBeansN.get(j).getUnionId())) {
                            dataBeansN.get(j).setChecked(true);
                        }
                    }
                    for (int j = 0; j < dataBeansW.size(); j++) {
                        if (checkMemberList.get(i).getUnionId().equals(dataBeansW.get(j).getUnionId())) {
                            dataBeansW.get(j).setChecked(true);
                        }
                    }
                }

                //更新适配器
                imSearchAdapterN.notifyDataSetChanged();
                imSearchAdapterW.notifyDataSetChanged();
                if (dataBeansN == null || dataBeansN.size() == 0) {
                    tv_null.setVisibility(View.VISIBLE);
                } else {
                    tv_null.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<IMGetExternalResponseBean> call, Throwable t) {
                WaitingView.getInstance().closeWaitingView();
                Log.e("herb", "获取外部成员接口失败>>" + call.toString());
            }
        });
    }

    //更新内选择数目
    public void updateChooseNum(boolean isAdd) {
        if (isAdd) {
            chooseNum++;
            IMAddGroupChatActivity.chooseNum++;
        } else if (chooseNum > 0) {
            chooseNum--;
            IMAddGroupChatActivity.chooseNum--;
        }
        add_group_bottom_tv.setText("已选择：" + chooseNum + "人");
        if (chooseNum >= 2) {
            add_group_bottom_btn.setBackgroundResource(R.drawable.add_group_chat_sure_bg);
            add_group_bottom_btn.setEnabled(true);
        } else {
            add_group_bottom_btn.setBackgroundResource(R.drawable.add_group_chat_sure_bg_grey);
            add_group_bottom_btn.setEnabled(false);
        }
    }

    //调用腾讯SDK创建群聊
    public void createGroup(String groupName) {
        //准备成员数据
        GroupMemberInfo memberInfo = new GroupMemberInfo();
        memberInfo.setAccount(TIMManager.getInstance().getLoginUser());
        mMembers.add(0, memberInfo);

        for (int i = 0; i < dataBeansN.size(); i++) {
            memberInfo = new GroupMemberInfo();
            memberInfo.setAccount(dataBeansN.get(i).getUnionId());
            mMembers.add(memberInfo);
        }

        for (int i = 0; i < dataBeansW.size(); i++) {
            memberInfo = new GroupMemberInfo();
            memberInfo.setAccount(dataBeansW.get(i).getUnionId());
            mMembers.add(memberInfo);
        }

        final GroupInfo groupInfo = new GroupInfo();
        groupInfo.setChatName(groupName);
        groupInfo.setGroupName(groupName);
        groupInfo.setMemberDetails(mMembers);
        groupInfo.setGroupType("Private");
        groupInfo.setJoinType(-1);

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

                Intent intent = new Intent(IMAddGroupChatSearchActivity.this, IMChatActivity.class);
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
        List<String> memberIds = new ArrayList<>();

        for (int i = 0; i < dataBeansN.size(); i++) {
            memberIds.add(dataBeansN.get(i).getUnionId());
        }

        for (int i = 0; i < dataBeansW.size(); i++) {
            memberIds.add(dataBeansW.get(i).getUnionId());
        }

        //添加成员
        GroupInfoProvider provider = new GroupInfoProvider();
        provider.loadGroupInfo(mGroupInfo);
        provider.inviteGroupMembers(memberIds, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                if (data instanceof String) {
                    com.tencent.qcloud.tim.uikit.utils.ToastUtil.toastLongMessage(data.toString());
                } else {
                    com.tencent.qcloud.tim.uikit.utils.ToastUtil.toastLongMessage("邀请成员成功");
                }
                finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                com.tencent.qcloud.tim.uikit.utils.ToastUtil.toastLongMessage("邀请成员失败:" + errCode + "=" + errMsg);
            }
        });
    }
}
