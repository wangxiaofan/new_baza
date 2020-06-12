package com.baza.android.bzw.businesscontroller.im;

import com.baza.android.bzw.bean.message.BaseConversation;
import com.baza.android.bzw.bean.message.ExtraMessageBean;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;

/**
 * Created by Vincent.Lei on 2017/6/1.
 * Title：
 * Note：
 */

public class IMRecentContact implements BaseConversation {
    public RecentContact nimRecentContact;
    public int type;
    private ExtraMessageBean mExtraMessageBean;

    public IMRecentContact(RecentContact nimRecentContact) {
        this.nimRecentContact = nimRecentContact;
    }

    public long getTime() {
        if (nimRecentContact != null)
            return nimRecentContact.getTime();
        return 0;
    }

    public int getUnreadCount() {
        if (nimRecentContact != null)
            return nimRecentContact.getUnreadCount();
        return 0;
    }

    public String getFromAccount() {
        if (nimRecentContact != null)
            return nimRecentContact.getFromAccount();
        return "";
    }

    public String getContent() {
        if (nimRecentContact != null)
            return nimRecentContact.getContent();
        return "";
    }

    public ExtraMessageBean getExtraMessage() {
        if (nimRecentContact != null && nimRecentContact.getMsgType() == MsgTypeEnum.custom && nimRecentContact.getAttachment() != null) {
            mExtraMessageBean = ((BZWCustomerAttachment) nimRecentContact.getAttachment()).extraMessageBean;
            return mExtraMessageBean;
        }
        return null;
    }

    public String getContactId() {
        if (nimRecentContact != null)
            return nimRecentContact.getContactId();
        return null;
    }

    public int getSessionType() {
        if (nimRecentContact != null) {
            SessionTypeEnum sessionTypeEnum = nimRecentContact.getSessionType();
            if (sessionTypeEnum == SessionTypeEnum.System)
                return IMConst.SESSION_TYPE_SYSTEM;
            else if (sessionTypeEnum == SessionTypeEnum.Team)
                return IMConst.SESSION_TYPE_TEAM;
        }
        return IMConst.SESSION_TYPE_P2P;
    }


    public int getStatus() {
        int s = IMConst.IM_MESSAGE_STATUS_DEFAULT;
        if (nimRecentContact != null) {
            MsgStatusEnum status = nimRecentContact.getMsgStatus();
            switch (status) {
                case fail:
                    s = IMConst.IM_MESSAGE_STATUS_FAILED;
                    break;
                case sending:
                    s = IMConst.IM_MESSAGE_STATUS_SENDING;
                    break;
            }
        }
        return s;
    }

    public String getUuid() {
        if (nimRecentContact != null)
            return nimRecentContact.getRecentMessageId();
        return null;
    }

    public void resetStatusByIMMessage(BZWIMMessage bzwimMessage) {
        if (nimRecentContact != null && bzwimMessage.imMessage != null)
            nimRecentContact.setMsgStatus(bzwimMessage.imMessage.getStatus());
    }
}
