package com.tencent.qcloud.tim.uikit.modules.conversation;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.bean.IMCheckInnerRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMCheckInnerResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMCheckOnlineRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMCheckOnlineResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetNameRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetNameResponseBean;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.tencent.qcloud.tim.uikit.modules.conversation.interfaces.IConversationAdapter;
import com.tencent.qcloud.tim.uikit.modules.conversation.interfaces.IConversationLayout;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.utils.DateTimeUtil;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.tencent.qcloud.tim.uikit.utils.retrofit.RetrofitHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationLayout extends RelativeLayout implements IConversationLayout {

    private ConversationListLayout mConversationList;
    private Activity context;
    private ConversationProvider conversationProvider;
    private IConversationAdapter adapter;

    public ConversationLayout(Context context) {
        super(context);
        this.context = (Activity) context;
        init();
    }

    public ConversationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (Activity) context;
        init();
    }

    public ConversationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = (Activity) context;
        init();
    }

    /**
     * 初始化相关UI元素
     */
    private void init() {
        inflate(getContext(), R.layout.conversation_layout, this);
        mConversationList = findViewById(R.id.conversation_list);
    }

    public void initDefault() {

        adapter = new ConversationListAdapter();
        mConversationList.setAdapter(adapter);
        ConversationManagerKit.getInstance().loadConversation(new IUIKitCallBack() {

            @Override
            public void onSuccess(Object data) {
                //请求是否是内部成员
                conversationProvider = (ConversationProvider) data;
                checkInner(conversationProvider, adapter);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.toastLongMessage("加载消息失败");
            }
        });
    }

    //收到新消息更新某一列
    public void updateItem() {
        ConversationManagerKit.getInstance().loadConversation(new IUIKitCallBack() {

            @Override
            public void onSuccess(Object data) {
                conversationProvider = (ConversationProvider) data;
                checkInner(conversationProvider, adapter);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.toastLongMessage("加载消息失败");
            }
        });
    }

    public TitleBarLayout getTitleBar() {
        return null;
    }

    @Override
    public void setParentLayout(Object parent) {

    }

    @Override
    public ConversationListLayout getConversationList() {
        return mConversationList;
    }

    public void addConversationInfo(int position, ConversationInfo info) {
        mConversationList.getAdapter().addItem(position, info);
    }

    public void removeConversationInfo(int position) {
        mConversationList.getAdapter().removeItem(position);
    }

    public void removeConversationInfo(String groupId) {
        for (int i = 0; i < conversationProvider.getDataSource().size(); i++) {
            if (conversationProvider.getDataSource().get(i).getId().equals(groupId)) {
                conversationProvider.getDataSource().remove(i);
                mConversationList.getAdapter().notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void setConversationTop(int position, ConversationInfo conversation) {
        ConversationManagerKit.getInstance().setConversationTop(position, conversation);
    }

    @Override
    public void deleteConversation(int position, ConversationInfo conversation) {
        ConversationManagerKit.getInstance().deleteConversation(position, conversation);
    }

    //查询是否为内部成员
    public void checkInner(final ConversationProvider provider, final IConversationAdapter adapter) {
        Log.e("herb", "腾讯获取列表数据>>" + provider.getDataSource().toString());
        Log.e("herb", "查询是否为内部成员>>开始");

        List<String> accountIds = new ArrayList<>();
        List<String> groupIds = new ArrayList<>();
        for (int i = 0; i < provider.getDataSource().size(); i++) {
            if (provider.getDataSource().get(i).isGroup()) {
                groupIds.add(provider.getDataSource().get(i).getId());
            } else {
                accountIds.add(provider.getDataSource().get(i).getId());
            }
        }
        Log.e("herb", "查询是否为内部成员groupIds>>" + groupIds.size());
        Log.e("herb", "查询是否为内部成员accountIds>>" + accountIds.size());

        IMCheckInnerRequestBean bean = new IMCheckInnerRequestBean(accountIds, groupIds);
        RetrofitHelper.getInstance().getServer().searchInner(URLConst.searchInner, bean).enqueue(new Callback<IMCheckInnerResponseBean>() {
            @Override
            public void onResponse(Call<IMCheckInnerResponseBean> call, Response<IMCheckInnerResponseBean> response) {
                IMCheckInnerResponseBean resultEntity = response.body();
                Log.e("herb", "查询是否为内部成员结果>>" + resultEntity.getData().size());
                if (resultEntity != null && resultEntity.getData() != null && resultEntity.getData().size() > 0) {
                    for (int i = 0; i < provider.getDataSource().size(); i++) {
                        for (int j = 0; j < resultEntity.getData().size(); j++) {
                            if (provider.getDataSource().get(i).getId().equals(resultEntity.getData().get(j).getAccountId())) {
                                //结果塞进去
                                //Log.e("herb", i + ">>isCheckResult>>" + resultEntity.getData().get(j).isCheckResult());
                                //Log.e("herb", i + ">>isCompanyResult>>" + resultEntity.getData().get(j).isCompanyResult());

                                provider.getDataSource().get(i).setInner(resultEntity.getData().get(j).isCheckResult());
                                provider.getDataSource().get(i).setCompanyResult(resultEntity.getData().get(j).isCompanyResult());
                            }
                        }
                    }
                }
                //获取用户名称
                Log.e("herb", "列表长度>>" + provider.getDataSource().size());
                checkOnline(provider, adapter);
            }

            @Override
            public void onFailure(Call<IMCheckInnerResponseBean> call, Throwable t) {

            }
        });
    }

    //针对C2C获取是否在线
    public void checkOnline(final ConversationProvider provider, final IConversationAdapter adapter) {
        Log.e("herb", "检测是否在线>>开始");

        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < provider.getDataSource().size(); i++) {
            if (!provider.getDataSource().get(i).isGroup()) {
                dataList.add(provider.getDataSource().get(i).getId());
            }
        }
        IMCheckOnlineRequestBean bean = new IMCheckOnlineRequestBean(dataList);
        RetrofitHelper.getInstance().getServer().checkOnline(URLConst.checkOnline, bean).enqueue(new Callback<IMCheckOnlineResponseBean>() {
            @Override
            public void onResponse(Call<IMCheckOnlineResponseBean> call, Response<IMCheckOnlineResponseBean> response) {
                IMCheckOnlineResponseBean resultEntity = response.body();
                //Log.e("herb", "检测是否在线结果>>" + resultEntity.toString());
                if (resultEntity != null && resultEntity.getData() != null) {
                    //更新数据
                    for (int i = 0; i < resultEntity.getData().size(); i++) {
                        for (int j = 0; j < provider.getDataSource().size(); j++) {
                            if (resultEntity.getData().get(i).getAccountId().equals(provider.getDataSource().get(j).getId())) {
                                provider.getDataSource().get(j).setState(resultEntity.getData().get(i).getState());
                            }
                        }
                    }
                }
                getName(provider, adapter);
            }

            @Override
            public void onFailure(Call<IMCheckOnlineResponseBean> call, Throwable t) {
                Log.e("herb", "检测是否在线>>失败");
            }
        });
    }

    //获取用户名称
    public void getName(final ConversationProvider provider, final IConversationAdapter adapter) {
        Log.e("herb", "获取用户名称>>开始");

        List<IMGetNameRequestBean.DataListBean> dataList = new ArrayList<>();
        for (int i = 0; i < provider.getDataSource().size(); i++) {
            if (provider.getDataSource().get(i).isGroup()) {
                //群组名称
                dataList.add(new IMGetNameRequestBean.DataListBean(provider.getDataSource().get(i).getId(), 1));
                if (provider.getDataSource().get(i).getLastMessage() != null && provider.getDataSource().get(i).getLastMessage().getFromUser() != null) {
                    //最后一条消息成员名称
                    dataList.add(new IMGetNameRequestBean.DataListBean(provider.getDataSource().get(i).getLastMessage().getFromUser(), 0));
                }
            } else {
                dataList.add(new IMGetNameRequestBean.DataListBean(provider.getDataSource().get(i).getId(), 0));
            }
        }
        IMGetNameRequestBean bean = new IMGetNameRequestBean(dataList);
        RetrofitHelper.getInstance().getServer().getIMName(URLConst.getIMName, bean).enqueue(new Callback<IMGetNameResponseBean>() {
            @Override
            public void onResponse(Call<IMGetNameResponseBean> call, Response<IMGetNameResponseBean> response) {
                IMGetNameResponseBean resultEntity = response.body();
                //Log.e("herb", "获取用户名称结果>>" + resultEntity.toString());
                if (resultEntity != null) {

                    for (int i = 0; i < provider.getDataSource().size(); i++) {

                        Log.e("herb", "provider>>" + provider.getDataSource().get(i).toString());

                        MessageInfo lastMsg = provider.getDataSource().get(i).getLastMessage();
                        if (lastMsg != null && lastMsg.getExtra() != null) {
                            String last = lastMsg.getExtra().toString();
                            Log.e("herb", "获取最后一条消息>>" + last);
                            for (int j = 0; j < resultEntity.getData().size(); j++) {
                                if (lastMsg.getFromUser() != null && lastMsg.getFromUser().equals(resultEntity.getData().get(j).getAccountId())) {
                                    //包含的话就替换
                                    if (last.contains(resultEntity.getData().get(j).getAccountId())) {
                                        lastMsg.setExtra(last.replace(resultEntity.getData().get(j).getAccountId(), resultEntity.getData().get(j).getAccountUserName()));
                                    }
                                    lastMsg.setFromUser(resultEntity.getData().get(j).getAccountUserName());

                                    provider.getDataSource().get(i).setNameBean(new ConversationInfo.NameBean(resultEntity.getData().get(j).getAccountId(), resultEntity.getData().get(j).getAccountUserName(), resultEntity.getData().get(j).getCompany(), resultEntity.getData().get(j).getFirmId(), resultEntity.getData().get(j).getTitle()));

                                    if (resultEntity.getData().get(j).getAccountId().equals(Configs.unionId)) {
                                        Configs.unionName = resultEntity.getData().get(j).getAccountUserName();
                                        Log.e("herb", "遍历到自己的名字>>" + Configs.unionName);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                adapter.setDataProvider(provider);
            }

            @Override
            public void onFailure(Call<IMGetNameResponseBean> call, Throwable t) {

            }
        });
    }
}
