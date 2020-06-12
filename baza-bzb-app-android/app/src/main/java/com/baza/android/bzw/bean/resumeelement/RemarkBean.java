package com.baza.android.bzw.bean.resumeelement;


import java.io.Serializable;


public class RemarkBean implements Serializable {
    public static final int INQUIRY_CREATE_BY_ME = 1;//文本备注是自己添加的

    public static final int INQUIRY_TYPE_TEXT = 1;
    public static final int INQUIRY_TYPE_PHONE = 2;
    public static final int INQUIRY_TYPE_AUDIO = 3;

    public long userId;
    public String candidateId;
    public String content;
    public long updateTime;
    public String inquiryId;
    public String id;
    public String creatorName;
    public String jobHoppingOccasion;
    public String employerInfo;
    public String expectSalary;
    public int isMyCreate;//备注是否是来自于其他人
    public int inquiryType;
    public Attachment attachment;
    public int isValidInquiry;//是否有效寻访，0-否，1-是
    public String userName;//推荐人姓名
    public String avatar;
    public String defaultAvatar;

    public static class Attachment implements Serializable {
        public int attachmentDuration;
        public String attachmentKey;
        public String attachmentLink;
    }

    public boolean isPhoneRemark() {
        return inquiryType == INQUIRY_TYPE_PHONE;
    }

    public boolean isAudioRemark() {
        return inquiryType == INQUIRY_TYPE_AUDIO;
    }

    public boolean isVoice() {
        return inquiryType == INQUIRY_TYPE_PHONE || inquiryType == INQUIRY_TYPE_AUDIO;
    }

    public int getAudioTimeLength() {
        return attachment != null ? attachment.attachmentDuration : 0;
    }

    public String getAttachmentKey() {
        return attachment != null ? attachment.attachmentKey : null;
    }

    public String getAttachmentLink() {
        return attachment != null ? attachment.attachmentLink : null;
    }

    public void setAttachmentLink(String link) {
        if (attachment != null)
            attachment.attachmentLink = link;
    }

    public boolean isSelfCreated() {
        return isMyCreate == INQUIRY_CREATE_BY_ME;
    }

    public void validContent() {
        if (isVoice() && "未填写".equals(content))
            content = null;
    }
}
