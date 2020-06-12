package com.tencent.qcloud.tim.uikit.bean;

import java.io.Serializable;
import java.util.List;

public class IMCheckInnerRequestBean implements Serializable {

    public IMCheckInnerRequestBean(List<String> accountIds, List<String> groupIds) {
        this.accountIds = accountIds;
        this.groupIds = groupIds;
    }

    private List<String> accountIds;
    private List<String> groupIds;

    public List<String> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(List<String> accountIds) {
        this.accountIds = accountIds;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }
}
