package com.tencent.qcloud.tim.uikit.bean;

import java.io.Serializable;
import java.util.List;

public class IMGetExternalResponseBean implements Serializable {

    /**
     * data : {"current":0,"orders":[{"asc":true,"column":"string"}],"pages":0,"records":[{"firmShortName":"string","nickname":"string","realName":"string","unionId":"string"}],"searchCount":true,"size":0,"total":0}
     * errorCode : 0
     * errorMessage : string
     * succeeded : true
     */

    private DataBean data;
    private int errorCode;
    private String errorMessage;
    private boolean succeeded;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public static class DataBean {
        /**
         * current : 0
         * orders : [{"asc":true,"column":"string"}]
         * pages : 0
         * records : [{"firmShortName":"string","nickname":"string","realName":"string","unionId":"string"}]
         * searchCount : true
         * size : 0
         * total : 0
         */

        private int current;
        private int pages;
        private boolean searchCount;
        private int size;
        private int total;
        private List<OrdersBean> orders;
        private List<RecordsBean> records;

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public boolean isSearchCount() {
            return searchCount;
        }

        public void setSearchCount(boolean searchCount) {
            this.searchCount = searchCount;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<OrdersBean> getOrders() {
            return orders;
        }

        public void setOrders(List<OrdersBean> orders) {
            this.orders = orders;
        }

        public List<RecordsBean> getRecords() {
            return records;
        }

        public void setRecords(List<RecordsBean> records) {
            this.records = records;
        }

        public static class OrdersBean {
            /**
             * asc : true
             * column : string
             */

            private boolean asc;
            private String column;

            public boolean isAsc() {
                return asc;
            }

            public void setAsc(boolean asc) {
                this.asc = asc;
            }

            public String getColumn() {
                return column;
            }

            public void setColumn(String column) {
                this.column = column;
            }
        }

        public static class RecordsBean {
            /**
             * firmShortName : string
             * nickname : string
             * realName : string
             * unionId : string
             */

            private String firmShortName;
            private String nickname;
            private String realName;
            private String unionId;
            private boolean isChecked = false;//是否已经选择

            public boolean isChecked() {
                return isChecked;
            }

            public void setChecked(boolean checked) {
                isChecked = checked;
            }

            public String getFirmShortName() {
                return firmShortName;
            }

            public void setFirmShortName(String firmShortName) {
                this.firmShortName = firmShortName;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getRealName() {
                return realName;
            }

            public void setRealName(String realName) {
                this.realName = realName;
            }

            public String getUnionId() {
                return unionId;
            }

            public void setUnionId(String unionId) {
                this.unionId = unionId;
            }
        }
    }
}
