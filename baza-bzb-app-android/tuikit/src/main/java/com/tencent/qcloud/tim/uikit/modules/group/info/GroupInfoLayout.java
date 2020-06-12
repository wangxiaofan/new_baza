package com.tencent.qcloud.tim.uikit.modules.group.info;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMOfflinePushSettings;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.bean.IMGetNameRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetNameResponseBean;
import com.tencent.qcloud.tim.uikit.component.LineControllerView;
import com.tencent.qcloud.tim.uikit.component.SelectionActivity;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.component.dialog.TUIKitDialog;
import com.tencent.qcloud.tim.uikit.modules.chat.GroupChatManagerKit;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationProvider;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.tencent.qcloud.tim.uikit.modules.conversation.interfaces.IConversationAdapter;
import com.tencent.qcloud.tim.uikit.modules.group.interfaces.IGroupMemberLayout;
import com.tencent.qcloud.tim.uikit.modules.group.member.GroupMemberInfo;
import com.tencent.qcloud.tim.uikit.modules.group.member.IGroupMemberRouter;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;
import com.tencent.qcloud.tim.uikit.utils.TUIKitLog;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.tencent.qcloud.tim.uikit.utils.dialog.MaterialDialog;
import com.tencent.qcloud.tim.uikit.utils.retrofit.RetrofitHelper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GroupInfoLayout extends LinearLayout implements IGroupMemberLayout, View.OnClickListener {

    private static final String TAG = GroupInfoLayout.class.getSimpleName();
    private TitleBarLayout mTitleBar;
    private TextView layout_title_text;
    private LineControllerView mMemberView;
    private GroupInfoAdapter mMemberAdapter;
    private IGroupMemberRouter mMemberPreviewListener;
    private LineControllerView mGroupTypeView;
    private LineControllerView mGroupIDView;
    private LineControllerView mGroupNameView;
    private LineControllerView mGroupIcon;
    private LineControllerView mGroupNotice;
    private LineControllerView mNickView;
    private LineControllerView mJoinTypeView;
    private LineControllerView mTopSwitchView;
    private LineControllerView chat_to_disturb_switch;//免打扰
    private Button mDissolveBtn;
    private EditText group_name;//弹出框群名称

    private GroupInfo mGroupInfo;
    private GroupInfoPresenter mPresenter;
    private ArrayList<String> mJoinTypes = new ArrayList<>();

    private LinearLayout group_members_more;//更多
    private TextView group_members_more_text;//更多字样

    private MaterialDialog mMaterialDialog;
    private boolean isAll;//是否是全员

    public GroupInfoLayout(Context context) {
        super(context);
        init(context);
    }

    public GroupInfoLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GroupInfoLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public GroupInfoPresenter getmPresenter() {
        return mPresenter;
    }

    public void setmPresenter(GroupInfoPresenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    private void init(Context context) {
        inflate(getContext(), R.layout.group_info_layout, this);
        // 标题
        mTitleBar = findViewById(R.id.group_info_title_bar);
        mTitleBar.getRightGroup().setVisibility(GONE);
        mTitleBar.setTitle(getResources().getString(R.string.group_detail), TitleBarLayout.POSITION.MIDDLE);
        mTitleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) getContext()).finish();
            }
        });
        layout_title_text = findViewById(R.id.layout_title_text);
        findViewById(R.id.layout_title_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) getContext()).finish();
            }
        });
        findViewById(R.id.layout_title_right_text).setVisibility(GONE);

        // 成员标题
        mMemberView = findViewById(R.id.group_member_bar);
        mMemberView.setOnClickListener(this);
        mMemberView.setCanNav(true);
        // 成员列表
        GridView memberList = findViewById(R.id.group_members);
        mMemberAdapter = new GroupInfoAdapter();
        memberList.setAdapter(mMemberAdapter);
        // 群类型，只读
        mGroupTypeView = findViewById(R.id.group_type_bar);
        // 群ID，只读
        mGroupIDView = findViewById(R.id.group_account);
        // 群聊名称
        mGroupNameView = findViewById(R.id.group_name);
        mGroupNameView.setOnClickListener(this);
        mGroupNameView.setCanNav(true);
        // 群头像
        mGroupIcon = findViewById(R.id.group_icon);
        mGroupIcon.setOnClickListener(this);
        mGroupIcon.setCanNav(false);
        // 群公告
        mGroupNotice = findViewById(R.id.group_notice);
        mGroupNotice.setOnClickListener(this);
        mGroupNotice.setCanNav(true);
        // 加群方式
        mJoinTypeView = findViewById(R.id.join_type_bar);
        mJoinTypeView.setOnClickListener(this);
        mJoinTypeView.setCanNav(true);
        mJoinTypes.addAll(Arrays.asList(getResources().getStringArray(R.array.group_join_type)));
        // 群昵称
        mNickView = findViewById(R.id.self_nickname_bar);
        mNickView.setOnClickListener(this);
        mNickView.setCanNav(true);
        // 是否置顶
        mTopSwitchView = findViewById(R.id.chat_to_top_switch);
        mTopSwitchView.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPresenter.setTopConversation(isChecked);
            }
        });
        //是否设置免打扰
        chat_to_disturb_switch = findViewById(R.id.chat_to_disturb_switch);
        chat_to_disturb_switch.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                final TIMGroupManager.ModifyMemberInfoParam param = new TIMGroupManager.ModifyMemberInfoParam(mGroupInfo.getId(), Configs.unionId);
                Log.e("herb", "群消息免打扰设置>>getId>>" + mGroupInfo.getId());
                Log.e("herb", "群消息免打扰设置>>unionId>>" + Configs.unionId);
                if (isChecked) {
                    param.setReceiveMessageOpt(TIMGroupReceiveMessageOpt.ReceiveNotNotify);
                } else {
                    param.setReceiveMessageOpt(TIMGroupReceiveMessageOpt.ReceiveAndNotify);
                }
                Log.e("herb", "群消息免打扰设置为>>" + param.getReceiveMessageOpt());

                GroupChatManagerKit.getInstance().getCurrentChatInfo().setNoDisturb(isChecked);

               /* TIMGroupManager.getInstance().modifyMemberInfo(param, new TIMCallBack() {
                    @Override
                    public void onError(int code, String desc) {
                        Log.e("herb", "群消息免打扰设置失败>>" + code + "=" + desc);
                    }

                    @Override
                    public void onSuccess() {
                        //mGroupInfo.setNoDisturb(isChecked);
                        Log.e("herb", "群消息免打扰设置为02>>" + param.getReceiveMessageOpt());

                    }
                });*/
            }
        });
        // 退群
        mDissolveBtn = findViewById(R.id.group_dissolve_button);
        mDissolveBtn.setOnClickListener(this);

        mPresenter = new GroupInfoPresenter(this);

        //更多
        group_members_more = findViewById(R.id.group_members_more);
        group_members_more_text = findViewById(R.id.group_members_more_text);

        //初始化填写群聊名弹出框
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_group_chat_name, null);
        group_name = contentView.findViewById(R.id.group_name);
        mMaterialDialog = new MaterialDialog(context);
        mMaterialDialog.setCancelable(true);
        mMaterialDialog.setCanceledOnTouchOutside(false);
        mMaterialDialog.setMessageView(contentView);
        mMaterialDialog.buildClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //取消
                mMaterialDialog.dismiss();
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View view) {
                //确定
                int strLength = 0;
                try {
                    strLength = group_name.getText().toString().getBytes("UTF-8").length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.e("herb", "str.length()>>" + strLength);

                if (group_name.getText().toString().trim().equals("")) {
                    ToastUtil.toastShortMessage("请输入群聊名称");
                } else if (strLength > 30) {
                    ToastUtil.toastShortMessage("群名长度超过限制");
                } else {
                    mPresenter.modifyGroupName(group_name.getText().toString());
                    mMaterialDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (mGroupInfo == null) {
            TUIKitLog.e(TAG, "mGroupInfo is NULL");
            return;
        }
        if (v.getId() == R.id.group_member_bar) {
            if (mMemberPreviewListener != null) {
                mMemberPreviewListener.forwardListMember(mGroupInfo);
            }
        } else if (v.getId() == R.id.group_name) {
            //修改群名称
            group_name.setText(mGroupInfo.getGroupName());
            mMaterialDialog.show();
//            Bundle bundle = new Bundle();
//            bundle.putString(TUIKitConstants.Selection.TITLE, getResources().getString(R.string.modify_group_name));
//            bundle.putString(TUIKitConstants.Selection.INIT_CONTENT, mGroupNameView.getContent());
//            bundle.putInt(TUIKitConstants.Selection.LIMIT, 20);
//            SelectionActivity.startTextSelection((Activity) getContext(), bundle, new SelectionActivity.OnResultReturnListener() {
//                @Override
//                public void onReturn(final Object text) {
//                    mPresenter.modifyGroupName(text.toString());
//                    mGroupNameView.setContent(text.toString());
//                }
//            });
        } else if (v.getId() == R.id.group_icon) {
            String groupUrl = String.format("https://picsum.photos/id/%d/200/200", new Random().nextInt(1000));
            TIMGroupManager.ModifyGroupInfoParam param = new TIMGroupManager.ModifyGroupInfoParam(mGroupInfo.getId());
            param.setFaceUrl(groupUrl);
            TIMGroupManager.getInstance().modifyGroupInfo(param, new TIMCallBack() {
                @Override
                public void onError(int code, String desc) {
                    TUIKitLog.e(TAG, "modify group icon failed, code:" + code + "|desc:" + desc);
                    ToastUtil.toastLongMessage("修改群头像失败, code = " + code + ", info = " + desc);
                }

                @Override
                public void onSuccess() {
                    ToastUtil.toastLongMessage("修改群头像成功");
                }
            });

        } else if (v.getId() == R.id.group_notice) {
            Bundle bundle = new Bundle();
            bundle.putString(TUIKitConstants.Selection.TITLE, getResources().getString(R.string.modify_group_notice));
            bundle.putString(TUIKitConstants.Selection.INIT_CONTENT, mGroupNotice.getContent());
            bundle.putInt(TUIKitConstants.Selection.LIMIT, 200);
            SelectionActivity.startTextSelection((Activity) getContext(), bundle, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(final Object text) {
                    mPresenter.modifyGroupNotice(text.toString());
                    mGroupNotice.setContent(text.toString());
                }
            });
        } else if (v.getId() == R.id.self_nickname_bar) {
            Bundle bundle = new Bundle();
            bundle.putString(TUIKitConstants.Selection.TITLE, getResources().getString(R.string.modify_nick_name_in_goup));
            bundle.putString(TUIKitConstants.Selection.INIT_CONTENT, mNickView.getContent());
            bundle.putInt(TUIKitConstants.Selection.LIMIT, 20);
            SelectionActivity.startTextSelection((Activity) getContext(), bundle, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(final Object text) {
                    mPresenter.modifyMyGroupNickname(text.toString());
                    mNickView.setContent(text.toString());
                }
            });
        } else if (v.getId() == R.id.join_type_bar) {
            if (mGroupTypeView.getContent().equals("聊天室")) {
                ToastUtil.toastLongMessage("加入聊天室为自动审批，暂不支持修改");
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString(TUIKitConstants.Selection.TITLE, getResources().getString(R.string.group_join_type));
            bundle.putStringArrayList(TUIKitConstants.Selection.LIST, mJoinTypes);
            bundle.putInt(TUIKitConstants.Selection.DEFAULT_SELECT_ITEM_INDEX, mGroupInfo.getJoinType());
            SelectionActivity.startListSelection((Activity) getContext(), bundle, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(final Object text) {
                    mPresenter.modifyGroupInfo((Integer) text, TUIKitConstants.Group.MODIFY_GROUP_JOIN_TYPE);
                    mJoinTypeView.setContent(mJoinTypes.get((Integer) text));

                }
            });
        } else if (v.getId() == R.id.group_dissolve_button) {
            if (mGroupInfo.isOwner() && !mGroupInfo.getGroupType().equals("Private")) {
                new TUIKitDialog(getContext())
                        .builder()
                        .setCancelable(true)
                        .setCancelOutside(true)
                        .setTitle("您确认解散该群?")
                        .setDialogWidth(0.75f)
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPresenter.deleteGroup();
                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();
            } else {
                new TUIKitDialog(getContext())
                        .builder()
                        .setCancelable(true)
                        .setCancelOutside(true)
                        .setTitle("确定退出群聊吗？")
                        .setDialogWidth(0.75f)
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPresenter.quitGroup();
                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();
            }
        }
    }

    public void setGroupId(String groupId) {
        mPresenter.loadGroupInfo(groupId, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                setGroupInfo((GroupInfo) data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    private void setGroupInfo(final GroupInfo info) {
        if (info == null) {
            return;
        }
        layout_title_text.setText(info.getGroupName() + "(" + info.getMemberCount() + ")");//设置标题
        mGroupNameView.setContent(info.getGroupName());
        mGroupIDView.setContent(info.getId());
        mGroupNotice.setContent(info.getNotice());
        mMemberView.setContent(info.getMemberCount() + "人");

        //在此更新群成员的昵称
        Log.e("herb", "获取用户名称>>开始");
        List<IMGetNameRequestBean.DataListBean> dataList = new ArrayList<>();
        for (int i = 0; i < info.getMemberDetails().size(); i++) {
            dataList.add(new IMGetNameRequestBean.DataListBean(info.getMemberDetails().get(i).getAccount(), 0));
        }
        IMGetNameRequestBean bean = new IMGetNameRequestBean(dataList);
        RetrofitHelper.getInstance().getServer().getIMName(URLConst.getIMName, bean).enqueue(new Callback<IMGetNameResponseBean>() {
            @Override
            public void onResponse(Call<IMGetNameResponseBean> call, Response<IMGetNameResponseBean> response) {
                IMGetNameResponseBean resultEntity = response.body();
                //Log.e("herb", "获取用户名称结果>>" + resultEntity.toString());
                if (resultEntity != null) {
                    for (int i = 0; i < info.getMemberDetails().size(); i++) {
                        for (int j = 0; j < resultEntity.getData().size(); j++) {
                            if (resultEntity.getData().get(j).getAccountId().equals(info.getMemberDetails().get(i).getAccount())) {
                                //添加昵称
                                info.getMemberDetails().get(i).setUserName(resultEntity.getData().get(j).getAccountUserName());
                            }
                        }
                    }
                }
                //排序
                for (int i = 0; i < info.getMemberDetails().size(); i++) {
                    if (info.getMemberDetails().get(i).getMemberType() == 400) {
                        GroupMemberInfo memberInfo = info.getMemberDetails().get(i);
                        //Log.e("herb", i + ">>找到群主>>" + info.getMemberDetails().get(i).toString());
                        info.getMemberDetails().remove(i);
                        info.getMemberDetails().add(0, memberInfo);
                        break;
                    }
                }

                //继续下面的流程
                mGroupInfo = info;
                mMemberAdapter.setDataSource(info, false, isAll);

                //所有成员无增删＼修改群名＼退群的功能
                if (isAll) {
                    mGroupNameView.setVisibility(GONE);
                    mDissolveBtn.setVisibility(GONE);
                }

                mGroupTypeView.setContent(convertGroupText(info.getGroupType()));
                mJoinTypeView.setContent(mJoinTypes.get(info.getJoinType()));
                mNickView.setContent(mPresenter.getNickName());
                mTopSwitchView.setChecked(mGroupInfo.isTopChat());
//
//                TIMManager.getInstance().getOfflinePushSettings(new TIMValueCallBack<TIMOfflinePushSettings>() {
//                    @Override
//                    public void onError(int i, String s) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(TIMOfflinePushSettings timOfflinePushSettings) {
//                        Log.e("herb", "获取01>>" + timOfflinePushSettings.toString());
//
//                    }
//                });
//                List<String> userIDs = new ArrayList<>();
//                userIDs.add(Configs.unionId);
//                TIMGroupManager.getInstance().getGroupMembersInfo(mGroupInfo.getId(), userIDs, new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
//                    @Override
//                    public void onError(int i, String s) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
//                        for (int i = 0; i < timGroupMemberInfos.size(); i++) {
//                            Log.e("herb", "获取>>" + timGroupMemberInfos.get(i).toString());
//                        }
//                    }
//                });
//
                TIMMessage timMessage;
//                timMessage.getRecvFlag().equals()

//                TIMGroupManager.ModifyMemberInfoParam param = new TIMGroupManager.ModifyMemberInfoParam(mGroupInfo.getId(), Configs.unionId);
//                TIMGroupReceiveMessageOpt opt = param.getReceiveMessageOpt();
//                Log.e("herb", "群消息免打扰获取>>getId>>" + mGroupInfo.getId());
//                Log.e("herb", "群消息免打扰获取>>unionId>>" + Configs.unionId);
//                Log.e("herb", "群消息免打扰获取>>" + opt);
//
//                TIMGroupManager.getInstance().modifyMemberInfo(param, new TIMCallBack() {
//                    @Override
//                    public void onError(int code, String desc) {
//                        Log.e("herb", "群消息免打扰设置失败>>" + code + "=" + desc);
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        //mGroupInfo.setNoDisturb(isChecked);
//                        Log.e("herb", "群消息免打扰设置为02>>" + param.getReceiveMessageOpt());
//
//                    }
//                });

//                if (opt == TIMGroupReceiveMessageOpt.ReceiveNotNotify) {
//                    chat_to_disturb_switch.setChecked(true);
//                } else {
//                    chat_to_disturb_switch.setChecked(false);
//                }

                mDissolveBtn.setText(R.string.dissolve);
                if (mGroupInfo.isOwner()) {
                    //mGroupNotice.setVisibility(VISIBLE);
                    //mJoinTypeView.setVisibility(VISIBLE);
                    if (mGroupInfo.getGroupType().equals("Private")) {
                        mDissolveBtn.setText(R.string.exit_group);
                    }
                } else {
                    //mGroupNotice.setVisibility(GONE);
                    //mJoinTypeView.setVisibility(GONE);
                    mDissolveBtn.setText(R.string.exit_group);
                }

                //展示更多按钮
                if (mGroupInfo.getMemberCount() > 14) {
                    group_members_more.setVisibility(VISIBLE);
                    group_members_more_text.setText("查看全部(" + mGroupInfo.getMemberCount() + ")");
                } else {
                    group_members_more.setVisibility(GONE);
                }
                group_members_more.setOnClickListener(new

                                                              OnClickListener() {
                                                                  @Override
                                                                  public void onClick(View view) {
                                                                      //展示全部
                                                                      group_members_more.setVisibility(GONE);
                                                                      mMemberAdapter.setDataSource(mGroupInfo, true, isAll);
                                                                  }
                                                              });
            }

            @Override
            public void onFailure(Call<IMGetNameResponseBean> call, Throwable t) {

            }
        });
    }

    private String convertGroupText(String groupType) {
        String groupText = "";
        if (TextUtils.isEmpty(groupType)) {
            return groupText;
        }
        if (TextUtils.equals(groupType, TUIKitConstants.GroupType.TYPE_PRIVATE)) {
            groupText = "讨论组";
        } else if (TextUtils.equals(groupType, TUIKitConstants.GroupType.TYPE_PUBLIC)) {
            groupText = "公开群";
        } else if (TextUtils.equals(groupType, TUIKitConstants.GroupType.TYPE_CHAT_ROOM)) {
            groupText = "聊天室";
        }
        return groupText;
    }

    public void onGroupInfoModified(Object value, int type) {
        switch (type) {
            case TUIKitConstants.Group.MODIFY_GROUP_NAME:
                ToastUtil.toastLongMessage(getResources().getString(R.string.modify_group_name_success));
                mGroupNameView.setContent(value.toString());
                break;
            case TUIKitConstants.Group.MODIFY_GROUP_NOTICE:
                mGroupNotice.setContent(value.toString());
                ToastUtil.toastLongMessage(getResources().getString(R.string.modify_group_notice_success));
                break;
            case TUIKitConstants.Group.MODIFY_GROUP_JOIN_TYPE:
                mJoinTypeView.setContent(mJoinTypes.get((Integer) value));
                break;
            case TUIKitConstants.Group.MODIFY_MEMBER_NAME:
                ToastUtil.toastLongMessage(getResources().getString(R.string.modify_nickname_success));
                mNickView.setContent(value.toString());
                break;
        }
    }

    public void setRouter(IGroupMemberRouter listener) {
        mMemberPreviewListener = listener;
        mMemberAdapter.setManagerCallBack(listener);
    }

    @Override
    public void setDataSource(GroupInfo dataSource) {

    }

    public void setIsAll(boolean b) {
        isAll = b;
    }

    @Override
    public TitleBarLayout getTitleBar() {
        return mTitleBar;
    }

    @Override
    public void setParentLayout(Object parent) {

    }

}
