package com.tencent.qcloud.tim.uikit.bean;

import java.io.Serializable;
import java.util.List;

public class IMSearchBean implements Serializable {


    /**
     * data : {"groupInfos":[{"companyCheck":false,"groupId":"string","groupName":"string","innerCheck":false,"memberCount":0}],"innerMemberInfos":[{"company":"string","title":"string","unionId":"string","userName":"string"}],"messageInfoPage":{"actualPageSize":0,"count":0,"currentPage":0,"data":[{"count":0,"id":"string","key":"string","name":"string","type":0}],"hasNext":true,"hasPrev":true,"offset":0,"pageCount":0,"pageSize":0,"topCount":0},"outerMemberInfos":[{"company":"string","title":"string","unionId":"string","userName":"string"}]}
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
        /**
         * groupInfos : [{"companyCheck":false,"groupId":"string","groupName":"string","innerCheck":false,"memberCount":0}]
         * innerMemberInfos : [{"company":"string","title":"string","unionId":"string","userName":"string"}]
         * messageInfoPage : {"actualPageSize":0,"count":0,"currentPage":0,"data":[{"count":0,"id":"string","key":"string","name":"string","type":0}],"hasNext":true,"hasPrev":true,"offset":0,"pageCount":0,"pageSize":0,"topCount":0}
         * outerMemberInfos : [{"company":"string","title":"string","unionId":"string","userName":"string"}]
         */

        private MessageInfoPageBean messageInfoPage;
        private List<GroupInfosBean> groupInfos;
        private List<InnerMemberInfosBean> innerMemberInfos;
        private List<OuterMemberInfosBean> outerMemberInfos;

        public MessageInfoPageBean getMessageInfoPage() {
            return messageInfoPage;
        }

        public void setMessageInfoPage(MessageInfoPageBean messageInfoPage) {
            this.messageInfoPage = messageInfoPage;
        }

        public List<GroupInfosBean> getGroupInfos() {
            return groupInfos;
        }

        public void setGroupInfos(List<GroupInfosBean> groupInfos) {
            this.groupInfos = groupInfos;
        }

        public List<InnerMemberInfosBean> getInnerMemberInfos() {
            return innerMemberInfos;
        }

        public void setInnerMemberInfos(List<InnerMemberInfosBean> innerMemberInfos) {
            this.innerMemberInfos = innerMemberInfos;
        }

        public List<OuterMemberInfosBean> getOuterMemberInfos() {
            return outerMemberInfos;
        }

        public void setOuterMemberInfos(List<OuterMemberInfosBean> outerMemberInfos) {
            this.outerMemberInfos = outerMemberInfos;
        }

        public static class MessageInfoPageBean {
            /**
             * actualPageSize : 0
             * count : 0
             * currentPage : 0
             * data : [{"count":0,"id":"string","key":"string","name":"string","type":0}]
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

            public static class DataBean implements Serializable{
                /**
                 * count : 0
                 * id : string
                 * key : string
                 * name : string
                 * type : 0
                 */

                private int count;
                private String id;
                private String key;
                private String name;
                private int type;

                public int getCount() {
                    return count;
                }

                public void setCount(int count) {
                    this.count = count;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getKey() {
                    return key;
                }

                public void setKey(String key) {
                    this.key = key;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }
            }
        }

        public static class GroupInfosBean implements Serializable{
            /**
             * companyCheck : false
             * groupId : string
             * groupName : string
             * innerCheck : false
             * memberCount : 0
             */

            private boolean companyCheck;
            private String groupId;
            private String groupName;
            private boolean innerCheck;
            private int memberCount;

            public boolean isCompanyCheck() {
                return companyCheck;
            }

            public void setCompanyCheck(boolean companyCheck) {
                this.companyCheck = companyCheck;
            }

            public String getGroupId() {
                return groupId;
            }

            public void setGroupId(String groupId) {
                this.groupId = groupId;
            }

            public String getGroupName() {
                return groupName;
            }

            public void setGroupName(String groupName) {
                this.groupName = groupName;
            }

            public boolean isInnerCheck() {
                return innerCheck;
            }

            public void setInnerCheck(boolean innerCheck) {
                this.innerCheck = innerCheck;
            }

            public int getMemberCount() {
                return memberCount;
            }

            public void setMemberCount(int memberCount) {
                this.memberCount = memberCount;
            }
        }

        public static class InnerMemberInfosBean implements Serializable {
            /**
             * company : string
             * title : string
             * unionId : string
             * userName : string
             */

            private String company;
            private String title;
            private String unionId;
            private String userName;

            public String getCompany() {
                return company;
            }

            public void setCompany(String company) {
                this.company = company;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getUnionId() {
                return unionId;
            }

            public void setUnionId(String unionId) {
                this.unionId = unionId;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }
        }

        public static class OuterMemberInfosBean implements Serializable{
            /**
             * company : string
             * title : string
             * unionId : string
             * userName : string
             */

            private String company;
            private String title;
            private String unionId;
            private String userName;

            public String getCompany() {
                return company;
            }

            public void setCompany(String company) {
                this.company = company;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getUnionId() {
                return unionId;
            }

            public void setUnionId(String unionId) {
                this.unionId = unionId;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }
        }
    }

    @Override
    public String toString() {
        return "IMSearchBean{" +
                "data=" + data +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", succeeded=" + succeeded +
                '}';
    }
}
