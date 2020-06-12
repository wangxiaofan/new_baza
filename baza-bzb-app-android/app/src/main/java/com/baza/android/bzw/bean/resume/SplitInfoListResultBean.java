package com.baza.android.bzw.bean.resume;

import com.baza.android.bzw.bean.BaseHttpResultBean;

import java.util.List;

public class SplitInfoListResultBean extends BaseHttpResultBean {

    public List<SplitInfoBean> data;

    public static class SplitInfoBean {

        /**
         * calculationSettingId : f10e50d0-3775-11ea-9e1d-b8f66a753531
         * canManuallyModify : true
         * canModifyDefaultUser : false
         * canModifyPercentValue : true
         * defaultUserType : 3
         * isMulti : false
         * name : BD
         * splitItems : [{"id":"","realName":"猎头管理1","recommendationProportion":0,"userId":"a6ca0302-d1c2-42dd-a17b-00685baaabd5","value":15}]
         * type : 1
         */

        private String calculationSettingId;
        private boolean canManuallyModify;
        private boolean canModifyDefaultUser;
        private boolean canModifyPercentValue;
        private int defaultUserType;
        private boolean isMulti;
        private String name;
        private int type;
        private List<SplitItemsBean> splitItems;

        public String getCalculationSettingId() {
            return calculationSettingId;
        }

        public void setCalculationSettingId(String calculationSettingId) {
            this.calculationSettingId = calculationSettingId;
        }

        public boolean isCanManuallyModify() {
            return canManuallyModify;
        }

        public void setCanManuallyModify(boolean canManuallyModify) {
            this.canManuallyModify = canManuallyModify;
        }

        public boolean isCanModifyDefaultUser() {
            return canModifyDefaultUser;
        }

        public void setCanModifyDefaultUser(boolean canModifyDefaultUser) {
            this.canModifyDefaultUser = canModifyDefaultUser;
        }

        public boolean isCanModifyPercentValue() {
            return canModifyPercentValue;
        }

        public void setCanModifyPercentValue(boolean canModifyPercentValue) {
            this.canModifyPercentValue = canModifyPercentValue;
        }

        public int getDefaultUserType() {
            return defaultUserType;
        }

        public void setDefaultUserType(int defaultUserType) {
            this.defaultUserType = defaultUserType;
        }

        public boolean isIsMulti() {
            return isMulti;
        }

        public void setIsMulti(boolean isMulti) {
            this.isMulti = isMulti;
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

        public List<SplitItemsBean> getSplitItems() {
            return splitItems;
        }

        public void setSplitItems(List<SplitItemsBean> splitItems) {
            this.splitItems = splitItems;
        }

        public static class SplitItemsBean {
            /**
             * id :
             * realName : 猎头管理1
             * recommendationProportion : 0
             * userId : a6ca0302-d1c2-42dd-a17b-00685baaabd5
             * value : 15
             */

            private String id;
            private String realName;
            private int recommendationProportion;
            private String userId;
            private int value;
            private boolean isNewAdd;

            public boolean isNewAdd() {
                return isNewAdd;
            }

            public void setNewAdd(boolean newAdd) {
                isNewAdd = newAdd;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getRealName() {
                return realName;
            }

            public void setRealName(String realName) {
                this.realName = realName;
            }

            public int getRecommendationProportion() {
                return recommendationProportion;
            }

            public void setRecommendationProportion(int recommendationProportion) {
                this.recommendationProportion = recommendationProportion;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public int getValue() {
                return value;
            }

            public void setValue(int value) {
                this.value = value;
            }
        }
    }
}
