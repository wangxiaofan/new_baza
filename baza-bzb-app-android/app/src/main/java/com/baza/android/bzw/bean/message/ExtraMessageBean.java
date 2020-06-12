package com.baza.android.bzw.bean.message;

import java.io.Serializable;

/**
 * Created by Vincent.Lei on 2017/6/7.
 * Title：
 * Note：
 */

public class ExtraMessageBean implements Serializable {
    public static final int TYPE_FRIEND_AGREE_NOTIFY = 2;

    public int type;
    public String receiverTip;
    public String senderTip;
    public String content;
}
