package com.baza.android.bzw.bean.resume;

import com.baza.android.bzw.bean.BaseHttpResultBean;

import java.util.List;

public class FirmMembersResultBean extends BaseHttpResultBean {

    public List<FirmMembersBean> data;

    public class FirmMembersBean {

        /**
         * nickName : l15814498877_WWWWWWWWWWWWs
         * realName : 七七
         * unionId : a843030d-8d63-4838-ba8e-65ff1b652215
         * userId : a7a50230-3111-4e71-ba68-773a9a9b04db
         */

        private String nickName;
        private String realName;
        private String unionId;
        private String userId;

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
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

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
