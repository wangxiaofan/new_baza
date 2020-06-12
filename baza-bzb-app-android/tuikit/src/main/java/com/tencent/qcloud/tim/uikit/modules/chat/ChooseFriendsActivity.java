package com.tencent.qcloud.tim.uikit.modules.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.input.InputLayout;
import com.tencent.qcloud.tim.uikit.utils.ScrollViewWithListView;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ChooseFriendsActivity extends Activity implements View.OnClickListener {

    private String TAG = "ChooseFriendsActivity";
    private ListView friends_lv;
    private RelativeLayout layout_title, layout_search;
    private EditText search_ed;
    protected InputMethodManager mImm;
    private List<TIMGroupMemberInfo> memberInfosAll = new ArrayList<>();
    private List<TIMGroupMemberInfo> memberInfosSearch = new ArrayList<>();
    private ChooseFriendsAdapter adapter;
    private boolean isSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_friends);
        ((TextView) findViewById(R.id.layout_title_text)).setText("选择你要@的人");
        findViewById(R.id.layout_title_right).setVisibility(View.VISIBLE);
        friends_lv = findViewById(R.id.friends_lv);
        layout_title = findViewById(R.id.layout_title);
        layout_search = findViewById(R.id.layout_search);
        search_ed = findViewById(R.id.search_ed);
        findViewById(R.id.layout_title_right).setOnClickListener(this);
        findViewById(R.id.title_search_cancel).setOnClickListener(this);

        //ListView点击监听
        friends_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //回传
                Intent intent = new Intent();
                if (isSearch) {
                    intent.putExtra("NAME", memberInfosSearch.get(i).getUser());
                } else {
                    if (i == 0) {
                        intent.putExtra("NAME", memberInfosAll.get(i).getNameCard());
                    } else {
                        intent.putExtra("NAME", memberInfosAll.get(i).getUser());
                    }
                }

                setResult(1000, intent);
                finish();
            }
        });

        //获取群列表
        TIMGroupManager.getInstance().getGroupMembers(InputLayout.id, new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
            @Override
            public void onError(int i, String s) {
                Log.e("herb", "获取列表数据失败>>");
            }

            @Override
            public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
                Log.e("herb", "获取列表数据成功>>" + timGroupMemberInfos.size());
                TIMGroupMemberInfo info = new TIMGroupMemberInfo();
                info.setNameCard("所有人");
                memberInfosAll.add(info);
                memberInfosAll.addAll(timGroupMemberInfos);
                isSearch = false;
                adapter = new ChooseFriendsAdapter(ChooseFriendsActivity.this, memberInfosAll, false);
                friends_lv.setAdapter(adapter);
            }
        });

        //键盘相关
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        search_ed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    //回车触发搜索
                    searchFun();
                    ChooseFriendsActivity.this.hideSoftInput();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.layout_title_right) {
            //搜索
            layout_title.setVisibility(View.GONE);
            layout_search.setVisibility(View.VISIBLE);
            //进入搜索界面弹出键盘
            search_ed.postDelayed(new Runnable() {
                @Override
                public void run() {
                    search_ed.requestFocus();
                    ChooseFriendsActivity.this.showSoftInput(search_ed);
                }
            }, 500);
            //friends_lv.setVisibility(View.GONE);
        } else if (view.getId() == R.id.title_search_cancel) {
            //取消
            layout_title.setVisibility(View.VISIBLE);
            layout_search.setVisibility(View.GONE);
            hideSoftInput();
            friends_lv.setVisibility(View.VISIBLE);
            isSearch = false;
            friends_lv.setAdapter(new ChooseFriendsAdapter(ChooseFriendsActivity.this, memberInfosAll, false));
        }
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

    private void searchFun() {
        String input = search_ed.getText().toString().trim();
        if (input.equals("")) {
            ToastUtil.toastShortMessage("请输入搜索内容");
            return;
        }

        if (memberInfosSearch != null) {
            memberInfosSearch.clear();
        }

        for (int i = 0; i < memberInfosAll.size(); i++) {
            String name = Configs.nameIdsMap.get(memberInfosAll.get(i).getUser());
            if (name != null && name.contains(input)) {
                memberInfosSearch.add(memberInfosAll.get(i));
            }
        }
        //重置
        friends_lv.setVisibility(View.VISIBLE);
        isSearch = true;
        friends_lv.setAdapter(new ChooseFriendsAdapter(ChooseFriendsActivity.this, memberInfosSearch, true));
    }
}
