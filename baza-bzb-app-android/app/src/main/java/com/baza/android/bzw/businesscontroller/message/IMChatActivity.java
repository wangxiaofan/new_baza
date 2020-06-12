package com.baza.android.bzw.businesscontroller.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bznet.android.rcbox.R;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.modules.chat.ChatLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.input.InputLayout;

public class IMChatActivity extends Activity {

    private ChatLayout chatLayout;
    private int msgSeq;
    private long msgTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_chat);

        msgSeq = getIntent().getIntExtra("msgSeq", -1);
        msgTime = getIntent().getLongExtra("msgTime", -1);
        if (msgSeq != -1 && msgTime != -1) {
            //定位聊天位置

        }

        // 从布局文件中获取聊天面板
        chatLayout = findViewById(R.id.chat_layout);
        // 单聊面板的默认 UI 和交互初始化
        chatLayout.initDefault();
        // 传入 ChatInfo 的实例，这个实例必须包含必要的聊天信息，一般从调用方传入
        // 构造 mChatInfo 可参考 StartC2CChatActivity.java 的方法 startConversation
        ChatInfo chatInfo = (ChatInfo) getIntent().getExtras().getSerializable(Constants.CHAT_INFO);
        Log.e("herb", "chatInfo全员>>" + chatInfo.isAll());
        chatLayout.setChatInfo(chatInfo);
    }

    @Override
    public void finish() {
        setResult(1000);
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 1000) {
            //选择@后回调界面
            String name = data.getStringExtra("NAME");
            Log.e("herb", "选择@回调>>" + name);
            if (!name.equals("所有人")) {
                name = Configs.nameIdsMap.get(name);
            }
            InputLayout.lastNameList.add(name);
            //输入框展示@name
            if (chatLayout != null) {
                String textSource = String.format("%1$s <font color='#53ABD5'><small>%2$s</small></font>", "", " @" + name + " ");
                Log.e("herb", "新增>>" + textSource);
                String oldText = chatLayout.getInputLayout().getInputText().getText().toString().substring(0, chatLayout.getInputLayout().getInputText().getText().toString().length() - 1);
                Log.e("herb", "旧的>>" + oldText);
                String[] split = oldText.split(" ");
                String newStr = "";
                for (int i = 0; i < split.length; i++) {
                    if (split[i].trim().startsWith("@")) {
                        newStr = newStr + String.format("%1$s <font color='#53ABD5'><small>%2$s</small></font>", "", split[i].trim() + " ");
                    } else {
                        newStr = newStr + split[i];
                    }
                }
                Log.e("herb", "旧的翻新>>" + newStr);
                chatLayout.getInputLayout().getInputText().setText(Html.fromHtml(newStr + textSource));
                Log.e("herb", "最新>>" + chatLayout.getInputLayout().getInputText().getText().toString());
                chatLayout.getInputLayout().getInputText().setSelection(chatLayout.getInputLayout().getInputText().getText().toString().length());

                /*String textSource = String.format("%1$s <font color='#53ABD5'><small>%2$s</small></font>", "", "@" + name + " ");
                Log.e("herb", "新增>>" + textSource);
                String oldText = chatLayout.getInputLayout().getInputText().getText().toString().substring(0, chatLayout.getInputLayout().getInputText().getText().toString().length() - 1);
                Log.e("herb", "旧的>>" + oldText);
                chatLayout.getInputLayout().getInputText().setText(Html.fromHtml(textSource));
                chatLayout.getInputLayout().getInputText().setSelection(chatLayout.getInputLayout().getInputText().getText().toString().length());*/
            }
        }
    }
}