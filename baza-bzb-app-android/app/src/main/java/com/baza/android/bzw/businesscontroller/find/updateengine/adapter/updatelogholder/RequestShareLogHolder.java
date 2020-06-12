package com.baza.android.bzw.businesscontroller.find.updateengine.adapter.updatelogholder;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/8/30.
 * Title：
 * Note：
 */

public class RequestShareLogHolder extends BaseLogHolder {
    TextView textView_tips;

    public RequestShareLogHolder(Context context, View convertView, ILogAdapterListener listener) {
        super(context, convertView, listener);
    }

    @Override
    int getItemLayoutId() {
        return R.layout.layout_update_log_tips;
    }

    @Override
    void initContentView() {
        textView_tips = view_contentRoot.findViewById(R.id.tv_tips);
    }

    @Override
    void setHolderData() {
        String name = null;
        if (mLogData != null && mLogData.user != null)
            name = (!TextUtils.isEmpty(mLogData.user.nickName) ? mLogData.user.nickName : mLogData.user.trueName);
        if (name == null)
            name = "TA";
        UserInfoBean userInfoBean = UserInfoManager.getInstance().getUserInfo();
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.log_type_ask_get, name, (TextUtils.isEmpty(userInfoBean.nickName) ? userInfoBean.trueName : userInfoBean.nickName)));
        spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_blue_53ABD5)), name.length() + 4, spannableString.length() - 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_tips.setText(spannableString);
    }
}
