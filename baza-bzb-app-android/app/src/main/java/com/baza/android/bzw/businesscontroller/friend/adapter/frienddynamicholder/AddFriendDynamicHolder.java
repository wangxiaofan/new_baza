package com.baza.android.bzw.businesscontroller.friend.adapter.frienddynamicholder;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.bean.friend.DynamicListResultBean;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/9/27.
 * Title：
 * Note：
 */

public class AddFriendDynamicHolder extends BaseDynamicHolder {
    private TextView textView_info;

    public AddFriendDynamicHolder(Context mContext, View contentView, BaseBZWAdapter.IAdapterEventsListener mAdapterEventsListener) {
        super(mContext, contentView, mAdapterEventsListener);
    }

    @Override
    protected View getTypeItemView() {
        textView_info = new TextView(mContext);
        textView_info.setTextColor(mResources.getColor(R.color.text_color_black_4E5968));
        textView_info.setTextSize(TypedValue.COMPLEX_UNIT_SP, ScreenUtil.px2dip(mResources.getDimension(R.dimen.text_size_10)));
        textView_info.setOnClickListener(this);
        return textView_info;
    }


    @Override
    protected void initTypeItemView() {
    }

    @Override
    public void setData(int position, DynamicListResultBean.DynamicBean data) {
        super.setData(position, data);
        DynamicListResultBean.AddFriendDynamicInfo addFriendDynamicInfo = mDynamicBean.getAddFriendDynamic();
        if (addFriendDynamicInfo == null)
            return;
        String name = (TextUtils.isEmpty(addFriendDynamicInfo.nickName) ? addFriendDynamicInfo.trueName : addFriendDynamicInfo.nickName);
        if (TextUtils.isEmpty(name))
            name = CommonConst.STR_DEFAULT_USER_NAME_SX;
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.dynamic_add_friend_info, name, (TextUtils.isEmpty(addFriendDynamicInfo.title) ? mResources.getString(R.string.message_unknown) : addFriendDynamicInfo.title)));
//        spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_orange_F98F33)), 1, 1 + name.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                sendAdapterEvent(AdapterEventIdConst.ADAPTER_EVENT_CLICK_ADDED_FRIEND, mPosition, null, mDynamicBean);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(mResources.getColor(R.color.text_color_blue_53ABD5));
                ds.setUnderlineText(false);
                ds.clearShadowLayer();
            }
        }, 1, 1 + name.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_grey_94A1A5)), 1 + name.length(), spannableString.length() - 5, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_info.setText(spannableString);
        textView_info.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
