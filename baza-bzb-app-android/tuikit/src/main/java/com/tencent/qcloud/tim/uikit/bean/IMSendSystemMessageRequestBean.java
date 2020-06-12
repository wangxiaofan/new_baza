package com.tencent.qcloud.tim.uikit.bean;

public class IMSendSystemMessageRequestBean {

    String msgContent;// 自定义通知内容json格式，包含通知类型和数据 ,
    String toAccountId;// 接收者，用于接收系统通知，必传

    @Override
    public String toString() {
        return "IMSendSystemMessageRequestBean{" +
                "msgContent='" + msgContent + '\'' +
                ", toAccountId='" + toAccountId + '\'' +
                '}';
    }

    public IMSendSystemMessageRequestBean(String msgContent, String toAccountId) {
        this.msgContent = msgContent;
        this.toAccountId = toAccountId;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(String toAccountId) {
        this.toAccountId = toAccountId;
    }
}
