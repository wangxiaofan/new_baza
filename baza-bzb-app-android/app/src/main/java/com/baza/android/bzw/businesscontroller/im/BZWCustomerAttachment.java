package com.baza.android.bzw.businesscontroller.im;

import com.baza.android.bzw.bean.message.ExtraMessageBean;
import com.slib.utils.AppUtil;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

/**
 * Created by Vincent.Lei on 2017/6/27.
 * Title：
 * Note：
 */

public class BZWCustomerAttachment implements MsgAttachment {
    public ExtraMessageBean extraMessageBean;

    public BZWCustomerAttachment(ExtraMessageBean extraMessageBean) {
        this.extraMessageBean = extraMessageBean;
    }

    @Override
    public String toJson(boolean send) {
        return (extraMessageBean == null ? null : AppUtil.objectToJson(extraMessageBean));
    }
}
