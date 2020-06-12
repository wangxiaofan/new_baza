package com.baza.android.bzw.businesscontroller.im;

import com.baza.android.bzw.bean.message.ExtraMessageBean;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Created by Vincent.Lei on 2017/6/2.
 * Title：
 * Note：
 */

public class BZWIMMessage {
    public IMMessage imMessage;
    private ExtraMessageBean extraMessageBean;

    public BZWIMMessage(IMMessage imMessage) {
        this.imMessage = imMessage;
    }

    public boolean isReceivedMessage() {
        return (imMessage != null && imMessage.getDirect() == MsgDirectionEnum.In);
    }

    public int getSessionType() {
        if (imMessage != null) {
            SessionTypeEnum sessionTypeEnum = imMessage.getSessionType();
            if (sessionTypeEnum == SessionTypeEnum.System)
                return IMConst.SESSION_TYPE_SYSTEM;
            else if (sessionTypeEnum == SessionTypeEnum.Team)
                return IMConst.SESSION_TYPE_TEAM;
        }
        return IMConst.SESSION_TYPE_P2P;
    }

    public String getSessionId() {
        if (imMessage != null)
            return imMessage.getSessionId();
        return null;
    }

    public String getFromAccount() {
        if (imMessage != null)
            return imMessage.getFromAccount();
        return null;
    }


    public int getStatus() {
        int s = IMConst.IM_MESSAGE_STATUS_DEFAULT;
        if (imMessage != null) {
            MsgStatusEnum status = imMessage.getStatus();
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

    public void setSendStatus() {
        if (imMessage != null)
            imMessage.setStatus(MsgStatusEnum.sending);
    }

    public String getUuid() {
        if (imMessage != null)
            return imMessage.getUuid();
        return null;
    }

    public void resetStatusByNewest(BZWIMMessage newest) {
        if (newest != null && newest.imMessage != null && imMessage != null)
            imMessage.setStatus(newest.imMessage.getStatus());
    }

    public void setHasRead() {
        if (imMessage != null) {
            imMessage.setStatus(MsgStatusEnum.read);
        }
    }

    public ExtraMessageBean getExtraMessage() {
        if (extraMessageBean == null && imMessage != null) {
            if (imMessage.getMsgType() == MsgTypeEnum.custom && imMessage.getAttachment() != null) {
                BZWCustomerAttachment attachment = (BZWCustomerAttachment) imMessage.getAttachment();
                extraMessageBean = attachment.extraMessageBean;
                return extraMessageBean;
            }
        }
        return extraMessageBean;
    }
}
