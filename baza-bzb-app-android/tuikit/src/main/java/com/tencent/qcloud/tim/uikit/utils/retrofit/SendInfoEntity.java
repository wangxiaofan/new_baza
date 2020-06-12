package com.tencent.qcloud.tim.uikit.utils.retrofit;

import java.io.Serializable;

public class SendInfoEntity implements Serializable {


    String resp_msg;//":"认证录入成功",
    int resp_code;//":200
    Datas datas;

    @Override
    public String toString() {
        return "SendInfoEntity{" +
                "resp_msg='" + resp_msg + '\'' +
                ", resp_code=" + resp_code +
                ", datas=" + datas +
                '}';
    }

    public String getResp_msg() {
        return resp_msg;
    }

    public void setResp_msg(String resp_msg) {
        this.resp_msg = resp_msg;
    }

    public int getResp_code() {
        return resp_code;
    }

    public void setResp_code(int resp_code) {
        this.resp_code = resp_code;
    }

    public Datas getDatas() {
        return datas;
    }

    public void setDatas(Datas datas) {
        this.datas = datas;
    }

    public class Datas {
        String profilePhoto;

        public String getProfilePhoto() {
            return profilePhoto;
        }

        public void setProfilePhoto(String profilePhoto) {
            this.profilePhoto = profilePhoto;
        }

        @Override
        public String toString() {
            return "Datas{" +
                    "profilePhoto='" + profilePhoto + '\'' +
                    '}';
        }
    }
}
