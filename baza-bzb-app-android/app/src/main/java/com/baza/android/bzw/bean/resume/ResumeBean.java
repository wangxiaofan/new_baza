package com.baza.android.bzw.bean.resume;

import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.constant.CommonConst;

import java.io.Serializable;
import java.util.ArrayList;

public class ResumeBean implements Serializable {
    public interface UpdateState {
        //0-待更新；1-更新中；2-更新完成
        int STATE_WAIT_UPDATE = 0;
        int STATE_UPDATING = 1;
        int STATE_UPDATE_DONE = 2;
    }

    public String candidateId;
    public String realName;
    public String mobile;
    public String email;
    public String title;
    public String company;
    public String major;
    public int gender;
    public String school;
    public int yearExpr;
    public int location;
    public int degree;
    public String id;//更新记录的id
    public long sourceUpdateTime;
    //    public long createTime;
    public String fromUser;
    public float currentCompletion;//评分
    public float targetCompletion;//r0评分
    //1--高中及以下  senior schoolName; 2--大专  associate; 3--本科  bachelor; 4--硕士  master; 5--MBA(工商管理硕士) ; 6--博士  doctor
    public int collectStatus;
    public int type; //1-简历上传候选人 2-新建候选人
    public String source;  //来源
    //    public String openId;
    public String talentId;
    public String unionId;
    public UserInfoBean owner;
    public ArrayList<Label> tagBindingList;//标签
    //    public String[] otherIds;//该人才其他简历的ID
    public int shareStatus;
    public boolean isJobHunting;//是否在找工作
    public long jobHuntingExpiryTime;
    public long createTime;
    public long collectedCreateTime;
    public long inquiryUpdateTime;
    public int huKou;
    public int marriage;
    public String birthday = CommonConst.TIME_NO_GET_BIRTHDAY;
    public int updateStatus;
    //手机号状态，1-未查看，2-锁定，3-已查看； 如果为3， mobile字段会有值，而不会是 "******"
    public int mobileStatus;
    //邮箱状态，1-未查看，2-锁定，3-已查看； 如果为3， email字段会有值，而不会是 "******"
    public int emailStatus;
    public int recommendTotal;//推荐记录总条数
    public int trackingListStatus;//trackingList状态，0-未加入，1-已加入
    public String workEndDate;//最近工作结束时间 9999开头或者为空 说明是至今
    public String workStartDate;//最近工作开始时间
    public int scanStatus;//浏览状态，0-未浏览，1-已浏览
    public long updateTime;//更新时间
    public String firmId;//企业id，为空 - 个人库简历，不为空 - 企业库简历
    public String latestOperateType; //最近操作类型
    public boolean waitUpdate;

    public boolean isWaitUpdate() {
        return (updateStatus == UpdateState.STATE_WAIT_UPDATE);
    }

    public boolean isUpdating() {
        return (updateStatus == UpdateState.STATE_UPDATING);
    }

    public void copyFromOld(ResumeBean resumeBean) {
        if (resumeBean == null) {
            gender = 0;
            degree = 0;
            location = 0;
            yearExpr = -1;
            huKou = 0;
            marriage = 0;
            birthday = CommonConst.TIME_NO_GET_BIRTHDAY;
            return;
        }
        candidateId = resumeBean.candidateId;
        realName = resumeBean.realName;
        mobile = resumeBean.mobile;
        email = resumeBean.email;
        title = resumeBean.title;
        company = resumeBean.company;
        major = resumeBean.major;
        gender = resumeBean.gender;
        school = resumeBean.school;
        yearExpr = resumeBean.yearExpr;
        location = resumeBean.location;
        degree = resumeBean.degree;

        currentCompletion = resumeBean.currentCompletion;
        targetCompletion = resumeBean.targetCompletion;
//        openId = resumeBean.openId;
        unionId = resumeBean.unionId;
//        otherIds = resumeBean.otherIds;
        shareStatus = resumeBean.shareStatus;
        sourceUpdateTime = resumeBean.sourceUpdateTime;
        owner = resumeBean.owner;
        talentId = resumeBean.talentId;
        source = resumeBean.source;
        fromUser = resumeBean.fromUser;
        collectStatus = resumeBean.collectStatus;
        type = resumeBean.type; //1-简历上传候选人 2-新建候选人
        tagBindingList = resumeBean.tagBindingList;
        updateStatus = resumeBean.updateStatus;
        huKou = resumeBean.huKou;
        marriage = resumeBean.marriage;
        birthday = resumeBean.birthday;
        collectedCreateTime = resumeBean.collectedCreateTime;
        inquiryUpdateTime = resumeBean.inquiryUpdateTime;
    }

    public void refreshModifyInfo(ResumeBean resumeBean) {
        if (resumeBean == null) {
            return;
        }
        realName = resumeBean.realName;
        gender = resumeBean.gender;
        mobile = resumeBean.mobile;
        email = resumeBean.email;
        title = resumeBean.title;
        company = resumeBean.company;
        yearExpr = resumeBean.yearExpr;
        location = resumeBean.location;
        degree = resumeBean.degree;
        currentCompletion = resumeBean.currentCompletion;
    }
}
