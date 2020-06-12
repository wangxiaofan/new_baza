package com.baza.android.bzw.businesscontroller.im;

import com.alibaba.fastjson.JSON;
import com.baza.android.bzw.bean.message.ExtraMessageBean;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

/**
 * Created by Vincent.Lei on 2017/6/27.
 * Title：
 * Note：
 */

public class BZWCustomAttachParser implements MsgAttachmentParser {
    @Override
    public MsgAttachment parse(String json) {
        ExtraMessageBean extraMessageBean = null;
//        LogUtil.d(json);
        try {
            extraMessageBean = JSON.parseObject(json, ExtraMessageBean.class);
        } catch (Exception e) {
        }
        return new BZWCustomerAttachment(extraMessageBean);
    }
}
