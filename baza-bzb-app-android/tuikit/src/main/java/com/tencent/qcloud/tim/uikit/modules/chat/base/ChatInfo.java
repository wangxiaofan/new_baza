package com.tencent.qcloud.tim.uikit.modules.chat.base;

import com.tencent.imsdk.TIMConversationType;

import java.io.Serializable;

/**
 * 聊天信息基本类
 */
public class ChatInfo implements Serializable {

    private String chatName;
    private TIMConversationType type = TIMConversationType.C2C;
    private String id;
    private boolean isTopChat;
    private boolean isNoDisturb;

    private String company;//公司
    private String post;//后期添加
    private boolean isAll;//是否是全员

    public boolean isAll() {
        return isAll;
    }

    public void setAll(boolean all) {
        isAll = all;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public boolean isNoDisturb() {
        return isNoDisturb;
    }

    public void setNoDisturb(boolean noDisturb) {
        isNoDisturb = noDisturb;
    }

    public ChatInfo() {

    }

    /**
     * 获取聊天的标题，单聊一般为对方名称，群聊为群名字
     *
     * @return
     */
    public String getChatName() {
        return chatName;
    }

    /**
     * 设置聊天的标题，单聊一般为对方名称，群聊为群名字
     *
     * @param chatName
     */
    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    /**
     * 获取聊天类型，C2C为单聊，Group为群聊
     *
     * @return
     */
    public TIMConversationType getType() {
        return type;
    }

    /**
     * 设置聊天类型，C2C为单聊，Group为群聊
     *
     * @param type
     */
    public void setType(TIMConversationType type) {
        this.type = type;
    }

    /**
     * 获取聊天唯一标识
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * 设置聊天唯一标识
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 是否为置顶的会话
     *
     * @return
     */
    public boolean isTopChat() {
        return isTopChat;
    }

    /**
     * 设置会话是否置顶
     *
     * @param topChat
     */
    public void setTopChat(boolean topChat) {
        isTopChat = topChat;
    }

}
