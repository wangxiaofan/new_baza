package com.tencent.qcloud.tim.uikit.bean;

import java.io.Serializable;
import java.util.List;

public class IMCheckOnlineRequestBean implements Serializable {

    public IMCheckOnlineRequestBean(List<String> accountIds) {
        this.accountIds = accountIds;
    }

    private List<String> accountIds;

    public List<String> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(List<String> accountIds) {
        this.accountIds = accountIds;
    }
}
