package com.baza.android.bzw.businesscontroller.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bznet.android.rcbox.R;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.bean.IMSearchBean;
import com.tencent.qcloud.tim.uikit.bean.IMSearchRequestBean;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.input.InputLayout;
import com.tencent.qcloud.tim.uikit.utils.retrofit.RetrofitHelper;
import com.tencent.qcloud.tim.uikit.utils.retrofit.WaitingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IMSearchActivity extends Activity implements View.OnClickListener {

    private EditText search_ed;
    //公司同事
    private LinearLayout search_lin_ts, search_lin_ts_more;
    private TextView search_lin_ts_more_text;
    private ScrollViewWithListView search_lv_ts;
    private IMSearchAdapter imSearchAdapter;
    private List<IMSearchBean.DataBeanX.InnerMemberInfosBean> beanList = new ArrayList<>();
    //群聊
    private LinearLayout search_lin_ql, search_lin_ql_more;
    private TextView search_lin_ql_more_text;
    private ScrollViewWithListView search_lv_ql;
    private IMSearchQLAdapter imSearchQLAdapter;
    private List<IMSearchBean.DataBeanX.GroupInfosBean> beanListQL = new ArrayList<>();
    //聊天记录
    private LinearLayout search_lin_jl, search_lin_jl_more;
    private TextView search_lin_jl_more_text;
    private ScrollViewWithListView search_lv_jl;
    private IMSearchJLAdapter imSearchJLAdapter;
    private List<IMSearchBean.DataBeanX.MessageInfoPageBean.DataBean> beanListJL = new ArrayList<>();
    //外部公司
    private LinearLayout search_lin_wb, search_lin_wb_more;
    private TextView search_lin_wb_more_text;
    private ScrollViewWithListView search_lv_wb;
    private IMSearchWBAdapter imSearchWBAdapter;
    private List<IMSearchBean.DataBeanX.OuterMemberInfosBean> beanListWB = new ArrayList<>();

    protected InputMethodManager mImm;
    private boolean isShow = false;//是否展示列表
    public static String searchText;//搜索内容

    private TextView tv_null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_search);
        tv_null = findViewById(R.id.tv_null);
        search_ed = findViewById(R.id.search_ed);
        search_lv_ts = findViewById(R.id.search_lv_ts);
        search_lv_ql = findViewById(R.id.search_lv_ql);
        search_lv_jl = findViewById(R.id.search_lv_jl);
        search_lv_wb = findViewById(R.id.search_lv_wb);
        search_lin_ts = findViewById(R.id.search_lin_ts);
        search_lin_ql = findViewById(R.id.search_lin_ql);
        search_lin_jl = findViewById(R.id.search_lin_jl);
        search_lin_wb = findViewById(R.id.search_lin_wb);
        search_lin_ts_more = findViewById(R.id.search_lin_ts_more);
        search_lin_ql_more = findViewById(R.id.search_lin_ql_more);
        search_lin_jl_more = findViewById(R.id.search_lin_jl_more);
        search_lin_wb_more = findViewById(R.id.search_lin_wb_more);
        search_lin_ts_more_text = findViewById(R.id.search_lin_ts_more_text);
        search_lin_ql_more_text = findViewById(R.id.search_lin_ql_more_text);
        search_lin_jl_more_text = findViewById(R.id.search_lin_jl_more_text);
        search_lin_wb_more_text = findViewById(R.id.search_lin_wb_more_text);
        search_lin_ts.setVisibility(View.GONE);
        search_lin_ql.setVisibility(View.GONE);
        search_lin_jl.setVisibility(View.GONE);
        search_lin_wb.setVisibility(View.GONE);
        search_lin_ts_more.setVisibility(View.GONE);
        search_lin_ql_more.setVisibility(View.GONE);
        search_lin_jl_more.setVisibility(View.GONE);
        search_lin_wb_more.setVisibility(View.GONE);

        search_lin_ts_more.setOnClickListener(this);
        search_lin_ql_more.setOnClickListener(this);
        search_lin_jl_more.setOnClickListener(this);
        search_lin_wb_more.setOnClickListener(this);
        findViewById(R.id.search_back).setOnClickListener(this);

        //公司同事
        imSearchAdapter = new IMSearchAdapter(this, beanList, false);
        search_lv_ts.setAdapter(imSearchAdapter);
        search_lv_ts.setOnItemClickListener((adapterView, view, i, l) -> {
            Log.e("herb", "点击>>" + beanList.get(i));
            startConversation(TIMConversationType.C2C, beanList.get(i).getUnionId(), beanList.get(i).getUserName(), beanList.get(i).getCompany(), beanList.get(i).getTitle(), false);
        });
        //群聊
        imSearchQLAdapter = new IMSearchQLAdapter(this, beanListQL, false);
        search_lv_ql.setAdapter(imSearchQLAdapter);
        search_lv_ql.setOnItemClickListener((adapterView, view, i, l) -> {
            startConversation(TIMConversationType.Group, beanListQL.get(i).getGroupId(), beanListQL.get(i).getGroupName(), "", "", false);
        });
        //聊天记录
        imSearchJLAdapter = new IMSearchJLAdapter(this, beanListJL, false);
        search_lv_jl.setAdapter(imSearchJLAdapter);
        search_lv_jl.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(IMSearchActivity.this, IMSearchDetailsActivity.class);
            intent.putExtra("Bean", beanListJL.get(i));
            startActivity(intent);
        });
        //外部公司
        imSearchWBAdapter = new IMSearchWBAdapter(this, beanListWB, false);
        search_lv_wb.setAdapter(imSearchWBAdapter);
        search_lv_wb.setOnItemClickListener((adapterView, view, i, l) -> {
            startConversation(TIMConversationType.C2C, beanListWB.get(i).getUnionId(), beanListWB.get(i).getUserName(), beanListWB.get(i).getCompany(), beanListWB.get(i).getTitle(), false);
        });

        //键盘相关
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //进入搜索界面弹出键盘
        search_ed.postDelayed(() -> {
            search_ed.requestFocus();
            showSoftInput(search_ed);
        }, 500);
        //添加搜索监听
        search_ed.addTextChangedListener(textWatcher);
        search_ed.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                //回车触发搜索
                searchFun();
                hideSoftInput();
            }
            return false;
        });
    }

    /**
     * 跳转到聊天详情页面
     */
    public void startConversation(TIMConversationType type, String id, String title, String company, String post, boolean isAll) {
        InputLayout.type = type;
        InputLayout.id = id;

        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(type);//个人对话还是群组对话
        chatInfo.setId(id);//对话ID
        chatInfo.setChatName(title);//对话标题
        chatInfo.setCompany(company);
        chatInfo.setPost(post);
        chatInfo.setAll(isAll);

        Intent intent = new Intent(this, IMChatActivity.class);
        intent.putExtra(Constants.CHAT_INFO, chatInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftInput();
    }

    //隐藏输入框
    public void hideSoftInput() {
        try {
            if (mImm != null && mImm.isActive() && getCurrentFocus() != null
                    && getCurrentFocus().getWindowToken() != null)
                mImm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        0);
        } catch (Exception e) {
            //ignore
        }
    }

    public void showSoftInput(View view) {
        try {
            if (mImm != null && mImm.isActive() && getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null)
                mImm.showSoftInput(view, 0);
        } catch (Exception e) {
            //ignore
        }
    }

    //输入监听
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            //input = editable.toString().trim();
            //Log.e("herb", "输入内容>>" + input);
            //searchFun();
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_back:
                //返回
                finish();
                break;
            case R.id.search_lin_ts_more:
                //公司同事
                imSearchAdapter.isMore = true;
                imSearchAdapter.notifyDataSetChanged();
                search_lin_ts_more.setVisibility(View.GONE);
                break;
            case R.id.search_lin_ql_more:
                //群聊
                imSearchQLAdapter.isMore = true;
                imSearchQLAdapter.notifyDataSetChanged();
                search_lin_ql_more.setVisibility(View.GONE);
                break;
            case R.id.search_lin_jl_more:
                //聊天记录
                imSearchJLAdapter.isMore = true;
                imSearchJLAdapter.notifyDataSetChanged();
                search_lin_jl_more.setVisibility(View.GONE);
                break;
            case R.id.search_lin_wb_more:
                //外部公司
                imSearchWBAdapter.isMore = true;
                imSearchWBAdapter.notifyDataSetChanged();
                search_lin_wb_more.setVisibility(View.GONE);
                break;
        }
    }

    //搜索接口
    public void searchFun() {
        searchText = search_ed.getText().toString().trim();
        if (searchText.equals("")) {
            isShow = false;
            search_lin_ts.setVisibility(View.GONE);
            search_lin_ql.setVisibility(View.GONE);
            search_lin_jl.setVisibility(View.GONE);
            search_lin_wb.setVisibility(View.GONE);
            search_lin_ts_more.setVisibility(View.GONE);
            search_lin_ql_more.setVisibility(View.GONE);
            search_lin_jl_more.setVisibility(View.GONE);
            search_lin_wb_more.setVisibility(View.GONE);
            return;
        } else {
            isShow = true;
        }

        List<String> stringList = new ArrayList<>();
        stringList.add(searchText);
        IMSearchRequestBean bean = new IMSearchRequestBean(1, 10, 0, stringList);

        WaitingView.getInstance().showWaitingView(this);
        RetrofitHelper.getInstance().getServer().search(URLConst.search, bean).enqueue(new Callback<IMSearchBean>() {
            @Override
            public void onResponse(Call<IMSearchBean> call, Response<IMSearchBean> response) {
                WaitingView.getInstance().closeWaitingView();
                IMSearchBean resultEntity = response.body();
                if (resultEntity != null && resultEntity.getData() != null &&
                        resultEntity.getData().getInnerMemberInfos().size() == 0 &&
                        resultEntity.getData().getGroupInfos().size() == 0 &&
                        resultEntity.getData().getMessageInfoPage().getData().size() == 0 &&
                        resultEntity.getData().getOuterMemberInfos().size() == 0) {
                    tv_null.setVisibility(View.VISIBLE);
                    return;
                }

                Log.e("herb", "搜索请求返回结果>>" + resultEntity.toString());
                Log.e("herb", "公司同事>>" + resultEntity.getData().getInnerMemberInfos().size());
                Log.e("herb", "群聊>>" + resultEntity.getData().getGroupInfos().size());
                Log.e("herb", "聊天记录>>" + resultEntity.getData().getMessageInfoPage().getData().size());
                Log.e("herb", "外部公司>>" + resultEntity.getData().getOuterMemberInfos().size());

                tv_null.setVisibility(View.GONE);
                if (!isShow) {
                    search_lin_ts.setVisibility(View.GONE);
                    search_lin_ql.setVisibility(View.GONE);
                    search_lin_jl.setVisibility(View.GONE);
                    search_lin_wb.setVisibility(View.GONE);
                    search_lin_ts_more.setVisibility(View.GONE);
                    search_lin_ql_more.setVisibility(View.GONE);
                    search_lin_jl_more.setVisibility(View.GONE);
                    search_lin_wb_more.setVisibility(View.GONE);
                    return;
                }

                //公司同事
                if (resultEntity.getData().getInnerMemberInfos() != null && resultEntity.getData().getInnerMemberInfos().size() > 0) {
                    search_lin_ts.setVisibility(View.VISIBLE);

                    //是否显示更多
                    if (resultEntity.getData().getInnerMemberInfos().size() > 2) {
                        search_lin_ts_more.setVisibility(View.VISIBLE);
                        search_lin_ts_more_text.setText("查看全部(" + (resultEntity.getData().getInnerMemberInfos().size() - 2) + ")");
                    } else {
                        search_lin_ts_more.setVisibility(View.GONE);
                    }

                    if (beanList != null) {
                        beanList.clear();
                    }
                    beanList.addAll(resultEntity.getData().getInnerMemberInfos());
                    imSearchAdapter.notifyDataSetChanged();
                } else {
                    search_lin_ts.setVisibility(View.GONE);
                }

                //群聊
                if (resultEntity.getData().getGroupInfos() != null && resultEntity.getData().getGroupInfos().size() > 0) {
                    search_lin_ql.setVisibility(View.VISIBLE);

                    //是否显示更多
                    if (resultEntity.getData().getGroupInfos().size() > 2) {
                        search_lin_ql_more.setVisibility(View.VISIBLE);
                        search_lin_ql_more_text.setText("查看全部(" + (resultEntity.getData().getGroupInfos().size() - 2) + ")");
                    } else {
                        search_lin_ql_more.setVisibility(View.GONE);
                    }

                    if (beanListQL != null) {
                        beanListQL.clear();
                    }
                    beanListQL.addAll(resultEntity.getData().getGroupInfos());
                    imSearchQLAdapter.notifyDataSetChanged();
                } else {
                    search_lin_ql.setVisibility(View.GONE);
                }

                //聊天记录
                if (resultEntity.getData().getMessageInfoPage() != null && resultEntity.getData().getMessageInfoPage().getData() != null && resultEntity.getData().getMessageInfoPage().getData().size() > 0) {
                    search_lin_jl.setVisibility(View.VISIBLE);

                    //是否显示更多
                    if (resultEntity.getData().getMessageInfoPage().getData().size() > 2) {
                        search_lin_jl_more.setVisibility(View.VISIBLE);
                        search_lin_jl_more_text.setText("查看全部(" + (resultEntity.getData().getMessageInfoPage().getData().size() - 2) + ")");
                    } else {
                        search_lin_jl_more.setVisibility(View.GONE);
                    }

                    if (beanListJL != null) {
                        beanListJL.clear();
                    }
                    beanListJL.addAll(resultEntity.getData().getMessageInfoPage().getData());
                    imSearchJLAdapter.notifyDataSetChanged();
                } else {
                    search_lin_jl.setVisibility(View.GONE);
                }

                //外部公司
                if (resultEntity.getData().getOuterMemberInfos() != null && resultEntity.getData().getOuterMemberInfos().size() > 0) {
                    search_lin_wb.setVisibility(View.VISIBLE);

                    //是否显示更多
                    if (resultEntity.getData().getOuterMemberInfos().size() > 2) {
                        search_lin_wb_more.setVisibility(View.VISIBLE);
                        search_lin_wb_more_text.setText("查看全部(" + (resultEntity.getData().getOuterMemberInfos().size() - 2) + ")");
                    } else {
                        search_lin_wb_more.setVisibility(View.GONE);
                    }

                    if (beanListWB != null) {
                        beanListWB.clear();
                    }
                    beanListWB.addAll(resultEntity.getData().getOuterMemberInfos());
                    imSearchWBAdapter.notifyDataSetChanged();
                } else {
                    search_lin_wb.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<IMSearchBean> call, Throwable t) {
                WaitingView.getInstance().closeWaitingView();
            }
        });
    }
}
