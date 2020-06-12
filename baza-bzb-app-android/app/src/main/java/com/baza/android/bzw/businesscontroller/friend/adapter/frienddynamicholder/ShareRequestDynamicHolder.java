package com.baza.android.bzw.businesscontroller.friend.adapter.frienddynamicholder;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.bean.friend.DynamicListResultBean;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/9/27.
 * Title：
 * Note：
 */

public class ShareRequestDynamicHolder extends BaseDynamicHolder {
    TextView textView_title;
    TextView textView_resumeInfo;

    public ShareRequestDynamicHolder(Context mContext, View contentView, BaseBZWAdapter.IAdapterEventsListener mAdapterEventsListener) {
        super(mContext, contentView, mAdapterEventsListener);
    }

    @Override
    protected View getTypeItemView() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_dynamic_type_share_request, null);
    }


    @Override
    protected void initTypeItemView() {
        textView_title = view_typeItem.findViewById(R.id.tv_title);
        textView_resumeInfo = view_typeItem.findViewById(R.id.tv_resume_info);
        textView_resumeInfo.setOnClickListener(this);
    }

    @Override
    public void setData(int position, DynamicListResultBean.DynamicBean data) {
        super.setData(position, data);
        DynamicListResultBean.ShareRequestDynamicInfo shareRequestDynamicInfo = mDynamicBean.getShareRequestDynamic();
        if (shareRequestDynamicInfo == null)
            return;
        textView_resumeInfo.setText(shareRequestDynamicInfo.realName + "-" + (TextUtils.isEmpty(shareRequestDynamicInfo.title) ? mResources.getString(R.string.message_unknown) : shareRequestDynamicInfo.title));
        String name = (TextUtils.isEmpty(shareRequestDynamicInfo.nickName) ? shareRequestDynamicInfo.trueName : shareRequestDynamicInfo.nickName);
        if (name == null)
            name = CommonConst.STR_DEFAULT_USER_NAME_SX;
        if (shareRequestDynamicInfo.type == CommonConst.REQUEST_SHARE_STATUS_WAIT)
            textView_title.setText(mResources.getString(R.string.dynamic_share_request_info_normal, name));
        else {
            SpannableString spannableString = new SpannableString(mResources.getString((shareRequestDynamicInfo.type == CommonConst.REQUEST_SHARE_STATUS_AGREE ? R.string.dynamic_share_request_info_agree : R.string.dynamic_share_request_info_reject), name));
            spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_blue_53ABD5)), 0, 3, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView_title.setText(spannableString);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_resume_info:
                sendAdapterEvent(AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL, mPosition, null, mDynamicBean);
                break;
        }
    }
}
