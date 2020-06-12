package com.tencent.qcloud.tim.uikit.bean;

import java.util.List;

public class IMSendManySystemMessageRequestBean {
    @Override
    public String toString() {
        return "IMSendManySystemMessageRequestBean{" +
                "msgContent='" + msgContent + '\'' +
                ", toAccountIds=" + toAccountIds +
                '}';
    }

    public IMSendManySystemMessageRequestBean(String msgContent, List<String> toAccountIds) {
        this.msgContent = msgContent;
        this.toAccountIds = toAccountIds;
    }

    /**
     * msgContent : string
     * toAccountIds : ["string"]
     */

    private String msgContent;
    private List<String> toAccountIds;

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public List<String> getToAccountIds() {
        return toAccountIds;
    }

    public void setToAccountIds(List<String> toAccountIds) {
        this.toAccountIds = toAccountIds;
    }
}
