package com.tencent.qcloud.tim.uikit.bean;

import java.io.Serializable;
import java.util.List;

public class IMGetNameRequestBean implements Serializable {

    public IMGetNameRequestBean(List<DataListBean> dataList) {
        this.dataList = dataList;
    }

    private List<DataListBean> dataList;

    public List<DataListBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataListBean> dataList) {
        this.dataList = dataList;
    }

    public static class DataListBean {
        public DataListBean(String accountId, int type) {
            this.accountId = accountId;
            this.type = type;
        }

        /**
         * accountId : string
         * type : 0
         */


        private String accountId;
        private int type;

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
