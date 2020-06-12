package com.baza.android.bzw.bean.push;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.baza.android.bzw.bean.message.BaseConversation;

/**
 * Created by Vincent.Lei on 2017/6/6.
 * Title：
 * Note：
 */

public class PushBean implements BaseConversation {
    public static final int MESSAGE_TYPE_UPDATE_RESUME_RECOMMEND = 7;
    public static final int TYPE_READ = 1;
    public static final int TYPE_UN_READ = 0;

    public static final String ACTION_EMIAL_SYNC = "SyncMailResume";

    public long triggerTime;
    public String title;
    public String subTitle;
    public String content;
    public int messageType;
    public int isView;

    public int needShow;//活动是否需要弹框提示
    protected PushExtraBean pushExtraBean;

    @Override
    public long getTime() {
        return triggerTime;
    }

    public static class PushExtraBean {
        public static final int RETURN_BUTTON_YES = 1;
        public String id;
        public String linkName;
        public String url;
        public String icon;//新活动提醒图标地址
        public int returnButtonType;//是否需要添加返回标题栏
        //旧版本字段
        public String link;
    }

    public PushExtraBean getExtraMsg() {
        if (pushExtraBean == null && !TextUtils.isEmpty(content)) {
            try {
                pushExtraBean = JSON.parseObject(content, PushExtraBean.class);
            } catch (Exception e) {
                //ignore
            }
        }
        return pushExtraBean;
    }

    public boolean isClickEnable() {
        PushExtraBean pushExtraBean = getExtraMsg();
        if (pushExtraBean != null) {
            return (!TextUtils.isEmpty(pushExtraBean.url)) || (!TextUtils.isEmpty(pushExtraBean.link));
        }
        return (messageType == MESSAGE_TYPE_UPDATE_RESUME_RECOMMEND);
    }
}
