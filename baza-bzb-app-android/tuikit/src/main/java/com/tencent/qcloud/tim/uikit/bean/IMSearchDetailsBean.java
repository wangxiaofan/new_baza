package com.tencent.qcloud.tim.uikit.bean;

import java.io.Serializable;
import java.util.List;

public class IMSearchDetailsBean implements Serializable {

    @Override
    public String toString() {
        return "IMSearchDetailsBean{" +
                "data=" + data +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", succeeded=" + succeeded +
                '}';
    }

    /**
     * data : {"actualPageSize":0,"count":0,"currentPage":0,"data":[{"content":"string","fromAccountId":"string","groupId":"string","msgSeq":0,"msgTime":0,"toAccountId":"string","type":0,"userName":"string"}],"hasNext":true,"hasPrev":true,"offset":0,"pageCount":0,"pageSize":0,"topCount":0}
     * errorCode : 0
     * errorMessage : string
     * succeeded : true
     */


    private DataBeanX data;
    private int errorCode;
    private String errorMessage;
    private boolean succeeded;

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
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

    public static class DataBeanX {
        @Override
        public String toString() {
            return "DataBeanX{" +
                    "actualPageSize=" + actualPageSize +
                    ", count=" + count +
                    ", currentPage=" + currentPage +
                    ", hasNext=" + hasNext +
                    ", hasPrev=" + hasPrev +
                    ", offset=" + offset +
                    ", pageCount=" + pageCount +
                    ", pageSize=" + pageSize +
                    ", topCount=" + topCount +
                    ", data=" + data +
                    '}';
        }

        /**
         * actualPageSize : 0
         * count : 0
         * currentPage : 0
         * data : [{"content":"string","fromAccountId":"string","groupId":"string","msgSeq":0,"msgTime":0,"toAccountId":"string","type":0,"userName":"string"}]
         * hasNext : true
         * hasPrev : true
         * offset : 0
         * pageCount : 0
         * pageSize : 0
         * topCount : 0
         */


        private int actualPageSize;
        private int count;
        private int currentPage;
        private boolean hasNext;
        private boolean hasPrev;
        private int offset;
        private int pageCount;
        private int pageSize;
        private int topCount;
        private List<DataBean> data;

        public int getActualPageSize() {
            return actualPageSize;
        }

        public void setActualPageSize(int actualPageSize) {
            this.actualPageSize = actualPageSize;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }

        public boolean isHasPrev() {
            return hasPrev;
        }

        public void setHasPrev(boolean hasPrev) {
            this.hasPrev = hasPrev;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getPageCount() {
            return pageCount;
        }

        public void setPageCount(int pageCount) {
            this.pageCount = pageCount;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTopCount() {
            return topCount;
        }

        public void setTopCount(int topCount) {
            this.topCount = topCount;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            @Override
            public String toString() {
                return "DataBean{" +
                        "content='" + content + '\'' +
                        ", fromAccountId='" + fromAccountId + '\'' +
                        ", groupId='" + groupId + '\'' +
                        ", msgSeq=" + msgSeq +
                        ", msgTime=" + msgTime +
                        ", toAccountId='" + toAccountId + '\'' +
                        ", type=" + type +
                        ", userName='" + userName + '\'' +
                        '}';
            }

            /**
             * content : string
             * fromAccountId : string
             * groupId : string
             * msgSeq : 0
             * msgTime : 0
             * toAccountId : string
             * type : 0
             * userName : string
             */

            private String content;
            private String fromAccountId;
            private String groupId;
            private int msgSeq;
            private long msgTime;
            private String toAccountId;
            private int type;
            private String userName;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
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

            public int getMsgSeq() {
                return msgSeq;
            }

            public void setMsgSeq(int msgSeq) {
                this.msgSeq = msgSeq;
            }

            public long getMsgTime() {
                return msgTime;
            }

            public void setMsgTime(long msgTime) {
                this.msgTime = msgTime;
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

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }
        }
    }
}
