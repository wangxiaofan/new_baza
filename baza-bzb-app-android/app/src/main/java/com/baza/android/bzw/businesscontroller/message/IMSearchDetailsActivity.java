package com.baza.android.bzw.businesscontroller.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bznet.android.rcbox.R;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.bean.IMSearchBean;
import com.tencent.qcloud.tim.uikit.bean.IMSearchDetailsBean;
import com.tencent.qcloud.tim.uikit.bean.IMSearchDetailsRequestBean;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.input.InputLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.tencent.qcloud.tim.uikit.utils.retrofit.RetrofitHelper;
import com.tencent.qcloud.tim.uikit.utils.retrofit.WaitingView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 搜索聊天记录详情页面
 */
public class IMSearchDetailsActivity extends Activity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    //标题
    private ImageView layout_title_back;
    private TextView layout_title_text;

    private PullToRefreshListView search_details_lv;
    private ListView search_details_lv_lv;
    private IMSearchDetailsAdapter imSearchAdapter;

    private IMSearchBean.DataBeanX.InnerMemberInfosBean innerMemberInfosBean;//公司同事
    private IMSearchBean.DataBeanX.GroupInfosBean groupInfosBean;//群聊
    private IMSearchBean.DataBeanX.MessageInfoPageBean.DataBean dataBean;//聊天记录
    private IMSearchBean.DataBeanX.OuterMemberInfosBean outerMemberInfosBean;//外部公司
    private String key = "";//查询关键字
    private List<IMSearchDetailsBean.DataBeanX.DataBean> mData = new ArrayList<>();

    //获取出相关
    private int currPage = 1;//当前页

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_search_details);
        layout_title_back = findViewById(R.id.layout_title_back);
        layout_title_text = findViewById(R.id.layout_title_text);
        search_details_lv = findViewById(R.id.search_details_lv);
        layout_title_back.setOnClickListener(this);

        //初始化RecycleView
        search_details_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        search_details_lv.setOnRefreshListener(this);
        search_details_lv_lv = search_details_lv.getRefreshableView();

        search_details_lv_lv.setOnItemClickListener((adapterView, view, i, l) -> {
            Log.e("herb", "点击了某一条>>" + mData.get(i - 1).toString());
            if (mData.get(i - 1).getType() == 0) {
                //单聊
                if (mData.get(i - 1).getFromAccountId().equals(Configs.unionId)) {
                    //自己
                    startConversation(TIMConversationType.C2C, mData.get(i - 1).getFromAccountId(), mData.get(i - 1).getUserName(), mData.get(i - 1).getMsgSeq(), mData.get(i - 1).getMsgTime());
                } else {
                    //他人
                    startConversation(TIMConversationType.C2C, mData.get(i - 1).getToAccountId(), mData.get(i - 1).getUserName(), mData.get(i - 1).getMsgSeq(), mData.get(i - 1).getMsgTime());
                }
            } else {
                //群组
                startConversation(TIMConversationType.Group, mData.get(i - 1).getGroupId(), dataBean.getName(), mData.get(i - 1).getMsgSeq(), mData.get(i - 1).getMsgTime());
            }
        });

        //获取数据
        dataBean = (IMSearchBean.DataBeanX.MessageInfoPageBean.DataBean) getIntent().getSerializableExtra("Bean");
        key = dataBean.getKey();
        layout_title_text.setText("聊天记录-" + dataBean.getName());
        //获取数据
        getDetails(false, null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_title_back:
                //返回
                finish();
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        getDetails(true, refreshView);
    }

    //查询数据
    public void getDetails(boolean isLoadMore, PullToRefreshBase refreshView) {

        if (isLoadMore) {
            currPage++;
        }

        List<String> stringList = new ArrayList<>();
        stringList.add(IMSearchActivity.searchText);
        IMSearchDetailsRequestBean bean = new IMSearchDetailsRequestBean(key, currPage, 10, stringList);
        //Log.e("herb", "查询详情页传参>>" + bean.toString());
        WaitingView.getInstance().showWaitingView(this);
        RetrofitHelper.getInstance().getServer().searchDetails(URLConst.searchDetails, bean).enqueue(new Callback<IMSearchDetailsBean>() {
            @Override
            public void onResponse(Call<IMSearchDetailsBean> call, Response<IMSearchDetailsBean> response) {
                WaitingView.getInstance().closeWaitingView();
                IMSearchDetailsBean resultEntity = response.body();
                if (resultEntity == null || resultEntity.getData() == null || resultEntity.getData().getCount() == 0 || resultEntity.getData().getData() == null || resultEntity.getData().getData().size() == 0) {
                    Log.e("herb", "查询详情页数据长度>>null");
                    if (isLoadMore && refreshView != null) {
                        refreshView.onRefreshComplete();
                    }
                    return;
                }

                Log.e("herb", "查询详情页数据内容>>" + resultEntity.getData().getData().toString());
                mData.addAll(resultEntity.getData().getData());
                //初始化RecycleView
                imSearchAdapter = new IMSearchDetailsAdapter(IMSearchDetailsActivity.this, mData);
                search_details_lv_lv.setAdapter(imSearchAdapter);

                if (isLoadMore && refreshView != null) {
                    refreshView.onRefreshComplete();
                }
            }

            @Override
            public void onFailure(Call<IMSearchDetailsBean> call, Throwable t) {
                WaitingView.getInstance().closeWaitingView();
                if (isLoadMore && refreshView != null) {
                    runOnUiThread(() -> refreshView.onRefreshComplete());
                }
            }
        });
    }

    /**
     * 跳转到聊天详情页面
     */
    public void startConversation(TIMConversationType type, String id, String title, int msgSeq, long msgTime) {

        InputLayout.type = type;
        InputLayout.id = id;

        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(type);//个人对话还是群组对话
        chatInfo.setId(id);//对话ID
        chatInfo.setChatName(title);//对话标题

        Intent intent = new Intent(this, IMChatActivity.class);
        intent.putExtra(Constants.CHAT_INFO, chatInfo);
        intent.putExtra("msgSeq", msgSeq);
        intent.putExtra("msgTime", msgTime);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
