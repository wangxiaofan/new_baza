package com.tencent.qcloud.tim.uikit.bean;

public class IMGetMessageRequestBean {


    public IMGetMessageRequestBean(int count, int flipOverType, String fromAccountId, String groupId, int nextReqMessageId, int nextReqMessageTime, boolean positionFlag, String toAccountId, int type) {
        this.count = count;
        this.flipOverType = flipOverType;
        this.fromAccountId = fromAccountId;
        this.groupId = groupId;
        this.nextReqMessageId = nextReqMessageId;
        this.nextReqMessageTime = nextReqMessageTime;
        this.positionFlag = positionFlag;
        this.toAccountId = toAccountId;
        this.type = type;
    }

    /**
     * count : 10
     * flipOverType : 0
     * fromAccountId : string
     * groupId : string
     * nextReqMessageId : 0
     * nextReqMessageTime : 0
     * positionFlag : false
     * toAccountId : string
     * type : 0
     */

    private int count;
    private int flipOverType;
    private String fromAccountId;
    private String groupId;
    private int nextReqMessageId;
    private int nextReqMessageTime;
    private boolean positionFlag;
    private String toAccountId;
    private int type;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getFlipOverType() {
        return flipOverType;
    }

    public void setFlipOverType(int flipOverType) {
        this.flipOverType = flipOverType;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(String fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getNextReqMessageId() {
        return nextReqMessageId;
    }

    public void setNextReqMessageId(int nextReqMessageId) {
        this.nextReqMessageId = nextReqMessageId;
    }

    public int getNextReqMessageTime() {
        return nextReqMessageTime;
    }

    public void setNextReqMessageTime(int nextReqMessageTime) {
        this.nextReqMessageTime = nextReqMessageTime;
    }

    public boolean isPositionFlag() {
        return positionFlag;
    }

    public void setPositionFlag(boolean positionFlag) {
        this.positionFlag = positionFlag;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(String toAccountId) {
        this.toAccountId = toAccountId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "IMGetMessageRequestBean{" +
                "count=" + count +
                ", flipOverType=" + flipOverType +
                ", fromAccountId='" + fromAccountId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", nextReqMessageId=" + nextReqMessageId +
                ", nextReqMessageTime=" + nextReqMessageTime +
                ", positionFlag=" + positionFlag +
                ", toAccountId='" + toAccountId + '\'' +
                ", type=" + type +
                '}';
    }
}
