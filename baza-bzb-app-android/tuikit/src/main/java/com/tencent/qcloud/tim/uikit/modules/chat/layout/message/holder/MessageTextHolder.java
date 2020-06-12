package com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder;

import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.bean.ElementBean;
import com.tencent.qcloud.tim.uikit.component.face.FaceManager;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.MyWebViewActivity;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * 聊天内容界面
 */
public class MessageTextHolder extends MessageContentHolder {

    private TextView msgBodyText;

    public MessageTextHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getVariableLayout() {
        return R.layout.message_adapter_content_text;
    }

    @Override
    public void initVariableViews() {
        msgBodyText = rootView.findViewById(R.id.msg_body_tv);
    }

    @Override
    public void layoutVariableViews(MessageInfo msg, int position, ElementBean elementBean) {
        if (msg != null) {
            msgBodyText.setVisibility(View.VISIBLE);
            if (msg.getExtra() != null) {
                //处理展示消息
                if (msg.getExtra().toString().contains("@")) {
                    String newStr = msg.getExtra().toString();
                    for (int j = 0; j < Configs.nameIdsList.size(); j++) {
                        if (newStr.contains("@" + Configs.nameIdsList.get(j))) {
                            //取出来，判断前一位是不是@
                            Log.e("herb", "找到名字>>" + Configs.nameIdsList.get(j));
                            Log.e("herb", "准备替换成>>" + String.format("%1$s <font color='#53ABD5'><small>%2$s</small></font>", "", "@" + Configs.nameIdsList.get(j)));
                            newStr = newStr.replace("@" + Configs.nameIdsList.get(j), String.format("%1$s <font color='#53ABD5'><small>%2$s</small></font>", "", "@" + Configs.nameIdsList.get(j)));
                        }
                    }
                    Log.e("herb", "newStr>>" + newStr);
                    FaceManager.handlerEmojiText(msgBodyText, newStr, false);
                } else {
                    FaceManager.handlerEmojiText(msgBodyText, msg.getExtra().toString(), false);
                }
            }
            if (properties.getChatContextFontSize() != 0) {
                msgBodyText.setTextSize(properties.getChatContextFontSize());
            }
            if (msg.isSelf()) {
                if (properties.getRightChatContentFontColor() != 0) {
                    msgBodyText.setTextColor(properties.getRightChatContentFontColor());
                }
            } else {
                if (properties.getLeftChatContentFontColor() != 0) {
                    msgBodyText.setTextColor(properties.getLeftChatContentFontColor());
                }
            }

            msgContentFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("herb", "文本点击>>" + msgBodyText.getText().toString());
                    if (msgBodyText.getText().toString().startsWith("http") ||
                            msgBodyText.getText().toString().startsWith("ftp") ||
                            msgBodyText.getText().toString().startsWith("https") ||
                            msgBodyText.getText().toString().startsWith("file")) {
                        //跳转Webview
                        Intent intent = new Intent(TUIKit.getAppContext(), MyWebViewActivity.class);
                        intent.putExtra("url", msgBodyText.getText().toString().trim());
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        TUIKit.getAppContext().startActivity(intent);
                    }
                }
            });
        }
    }

}
