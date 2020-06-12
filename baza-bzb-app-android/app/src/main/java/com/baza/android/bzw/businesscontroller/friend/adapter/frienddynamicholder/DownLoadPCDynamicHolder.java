package com.baza.android.bzw.businesscontroller.friend.adapter.frienddynamicholder;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.friend.DynamicListResultBean;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/9/27.
 * Title：
 * Note：
 */

public class DownLoadPCDynamicHolder extends BaseDynamicHolder {
    private TextView textView_info;

    public DownLoadPCDynamicHolder(Context mContext, View contentView, BaseBZWAdapter.IAdapterEventsListener mAdapterEventsListener) {
        super(mContext, contentView, mAdapterEventsListener);
    }

    @Override
    protected View getTypeItemView() {
        textView_info = new TextView(mContext);
        textView_info.setTextColor(mResources.getColor(R.color.text_color_blue_0D315C));
        textView_info.setTextSize(TypedValue.COMPLEX_UNIT_SP, ScreenUtil.px2dip(mResources.getDimension(R.dimen.text_size_10)));
        textView_info.setOnClickListener(this);
        textView_info.setId(R.id.dynamic_id_click_one);
        return textView_info;
    }


    @Override
    protected void initTypeItemView() {
    }

    @Override
    public void setData(int position, DynamicListResultBean.DynamicBean data) {
        super.setData(position, data);
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.dynamic_info_download_pc));
        spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_blue_53ABD5)), spannableString.length() - 4, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_info.setText(spannableString);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.dynamic_id_click_one:
                sendAdapterEvent(AdapterEventIdConst.ADAPTER_EVENT_DYNAMIC_CLICK_PC_INFO, mPosition, null, null);
                break;
        }
    }
}
