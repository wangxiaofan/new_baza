package com.baza.android.bzw.bean.user;

import com.slib.storage.database.annotation.ColumnAnnotation;
import com.slib.storage.database.annotation.TableAnnotation;

import java.io.Serializable;

/**
 * Created by Vincent.Lei on 2017/5/15.
 * Title：
 * Note：
 */
@TableAnnotation(tableName = "userInfoTable2")
public class UserInfoBean implements Serializable {
    @ColumnAnnotation(columnName = "avatar")
    public String avatar;
    @ColumnAnnotation(columnName = "email")
    public String email;
    @ColumnAnnotation(columnName = "gender", columnType = "INTEGER")
    public int gender;
    @ColumnAnnotation(columnName = "userId", columnType = "INTEGER", primaryKey = 1)
    public long userId;
    @ColumnAnnotation(columnName = "mobile")
    public String mobile;
    @ColumnAnnotation(columnName = "nickName")
    public String nickName;
    @ColumnAnnotation(columnName = "trueName")
    public String trueName;
    @ColumnAnnotation(columnName = "company")
    public String company;
    @ColumnAnnotation(columnName = "title")
    public String title;
    @ColumnAnnotation(columnName = "location")
    public String location;
    @ColumnAnnotation(columnName = "openId")
    public String openId;
    @ColumnAnnotation(columnName = "neteaseId")
    public String neteaseId;
    @ColumnAnnotation(columnName = "unionId")
    public String unionId;
    @ColumnAnnotation(columnName = "userName")
    public String userName;
    public String neteaseToken;
    public String bbsUserName;
    public int channelVerifyStatus;
    public String firmId;//企业id
    public int isCfUser;//是否CF用户，0-否，1-是
    public int isFirmUser;// 是否猎企用户，0-否，1-是
    public String defaultAvatar;
//    public int identity;
//    public String industry;

    public static class ExtraInfo {
        //        public int setStep;
        public int resumeSetStatus;
        public boolean isCFUser;
    }

}
