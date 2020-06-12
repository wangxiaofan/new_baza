package com.tencent.qcloud.tim.uikit.modules.conversation.base;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConversationInfo implements Serializable, Comparable<ConversationInfo> {
    @Override
    public String toString() {
        return "ConversationInfo{" +
                "type=" + type +
                ", unRead=" + unRead +
                ", conversationId='" + conversationId + '\'' +
                ", id='" + id + '\'' +
                ", iconUrlList=" + iconUrlList +
                ", title='" + title + '\'' +
                ", icon=" + icon +
                ", isGroup=" + isGroup +
                ", top=" + top +
                ", lastMessageTime=" + lastMessageTime +
                ", lastMessage=" + lastMessage +
                ", isInner=" + isInner +
                ", isCompanyResult=" + isCompanyResult +
                ", nameBean=" + nameBean +
                '}';
    }

    public static final int TYPE_COMMON = 1;
    public static final int TYPE_CUSTOM = 2;
    /**
     * 会话类型，自定义会话or普通会话
     */
    private int type;

    /**
     * 消息未读数
     */
    private int unRead;
    /**
     * 会话ID
     */
    private String conversationId;
    /**
     * 会话标识，C2C为对方用户ID，群聊为群组ID
     */
    private String id;
    /**
     * 会话头像url
     */
    private List<Object> iconUrlList = new ArrayList<>();

    public List<Object> getIconUrlList() {
        return iconUrlList;
    }

    public void setIconUrlList(List<Object> iconUrlList) {
        this.iconUrlList = iconUrlList;
    }

    /**
     * 会话标题
     */
    private String title;

    /**
     * 会话头像
     */
    private Bitmap icon;
    /**
     * 是否为群会话
     */
    private boolean isGroup;
    /**
     * 是否为置顶会话
     */
    private boolean top;
    /**
     * 最后一条消息时间
     */
    private long lastMessageTime;
    /**
     * 最后一条消息，MessageInfo对象
     */
    private MessageInfo lastMessage;

    private boolean isInner;//是否是内部成员（后期添加）
    private boolean isCompanyResult;//公司群检测结果（后期添加）
    private String state;//是否在线（后期添加）Online
    private NameBean nameBean;//名字列表（后期添加）

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public NameBean getNameBean() {
        return nameBean;
    }

    public void setNameBean(NameBean nameBean) {
        this.nameBean = nameBean;
    }

    public boolean isInner() {
        return isInner;
    }

    public void setInner(boolean inner) {
        isInner = inner;
    }

    public boolean isCompanyResult() {
        return isCompanyResult;
    }

    public void setCompanyResult(boolean companyResult) {
        isCompanyResult = companyResult;
    }

    public ConversationInfo() {

    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUnRead() {
        return unRead;
    }

    public void setUnRead(int unRead) {
        this.unRead = unRead;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

    /**
     * 获得最后一条消息的时间，单位是秒
     */
    public long getLastMessageTime() {
        return lastMessageTime;
    }

    /**
     * 设置最后一条消息的时间，单位是秒
     *
     * @param lastMessageTime
     */
    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public MessageInfo getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageInfo lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public int compareTo(@NonNull ConversationInfo other) {
        return this.lastMessageTime > other.lastMessageTime ? -1 : 1;
    }

    public static class NameBean {
        private String accountId;
        private String accountUserName;
        private String company;
        private String firmId;
        private String title;

        public NameBean(String accountId, String accountUserName, String company, String firmId, String title) {
            this.accountId = accountId;
            this.accountUserName = accountUserName;
            this.company = company;
            this.firmId = firmId;
            this.title = title;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "accountId='" + accountId + '\'' +
                    ", accountUserName='" + accountUserName + '\'' +
                    ", company='" + company + '\'' +
                    ", firmId='" + firmId + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getAccountUserName() {
            return accountUserName;
        }

        public void setAccountUserName(String accountUserName) {
            this.accountUserName = accountUserName;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getFirmId() {
            return firmId;
        }

        public void setFirmId(String firmId) {
            this.firmId = firmId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
