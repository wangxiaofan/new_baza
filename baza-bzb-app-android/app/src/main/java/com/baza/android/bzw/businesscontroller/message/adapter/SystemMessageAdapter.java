package com.baza.android.bzw.businesscontroller.message.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.push.PushBean;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.slib.utils.AppUtil;
import com.slib.utils.DateUtil;
import com.bznet.android.rcbox.R;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/6/21.
 * Title：
 * Note：
 */

public class SystemMessageAdapter extends BaseBZWAdapter implements View.OnClickListener {
    private Context mContext;
    private List<PushBean> mMessageList;
    private Resources mResources;

    public SystemMessageAdapter(Context mContext, List<PushBean> mMessageList, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = mContext;
        this.mResources = mContext.getResources();
        this.mMessageList = mMessageList;
    }

    @Override
    public int getCount() {
        return mMessageList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_system_message, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
            viewHolder.textView_btn.setOnClickListener(this);

        } else
            viewHolder = (ViewHolder) convertView.getTag();
        PushBean pushBean = mMessageList.get(position);
        viewHolder.textView_Title.setText(pushBean.title);
        viewHolder.textView_time.setText(DateUtil.longMillions2FormatDate(pushBean.triggerTime, DateUtil.SDF_YMD_HM));
        if(TextUtils.isEmpty(pushBean.subTitle)){
            viewHolder.textView_subTitle.setVisibility(View.GONE);
        }else{
            SpannableString spannableString = new SpannableString(pushBean.subTitle != null ? pushBean.subTitle : "");
            if (mergedLinkClick(spannableString, pushBean.subTitle))
                viewHolder.textView_subTitle.setMovementMethod(LinkMovementMethod.getInstance());
            viewHolder.textView_subTitle.setText(spannableString);
            viewHolder.textView_subTitle.setVisibility(View.VISIBLE);
        }

        boolean clickAble = pushBean.isClickEnable();
        viewHolder.textView_btn.setVisibility(clickAble ? View.VISIBLE : View.GONE);
        viewHolder.view_line.setVisibility(clickAble ? View.VISIBLE : View.GONE);
        viewHolder.textView_btn.setTag(position);
        return convertView;
    }

    private boolean mergedLinkClick(SpannableString spannableString, String sourceStr) {
        List<AppUtil.LinkData> list = AppUtil.urlToLink(sourceStr);
        if (list != null && !list.isEmpty()) {
            AppUtil.LinkData linkData;
            for (int i = 0, size = list.size(); i < size; i++) {
                linkData = list.get(i);
                spannableString.setSpan(new SystemMessageClickableSpan(linkData, mResources, this), linkData.start, linkData.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        sendAdapterEventToHost(AdapterEventIdConst.SYSTEM_MESSAGE_ADAPTER_EVENT_OPEN, position, null, mMessageList.get(position));
    }

    static class ViewHolder {
        TextView textView_time;
        TextView textView_subTitle;
        TextView textView_Title;
        TextView textView_btn;
        View view_line;

        public ViewHolder(View convertView) {
            textView_time = convertView.findViewById(R.id.tv_chat_depart_time);
            textView_subTitle = convertView.findViewById(R.id.tv_sub_title);
            textView_Title = convertView.findViewById(R.id.tv_title);
            textView_btn = convertView.findViewById(R.id.tv_process);
            view_line = convertView.findViewById(R.id.view_line);
        }
    }

    private static class SystemMessageClickableSpan extends ClickableSpan {
        private AppUtil.LinkData mLinkData;
        private Resources mResources;
        private BaseBZWAdapter mAdapter;

        SystemMessageClickableSpan(AppUtil.LinkData mLinkData, Resources mResources, BaseBZWAdapter mAdapter) {
            this.mLinkData = mLinkData;
            this.mResources = mResources;
            this.mAdapter = mAdapter;
        }

        @Override
        public void onClick(View widget) {
            mAdapter.sendAdapterEventToHost(AdapterEventIdConst.ADAPTER_EVENT_CLICK_LINK_TEXT, -1, null, mLinkData.url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
//            super.updateDrawState(ds);
            ds.setColor(mResources.getColor(R.color.text_color_blue_53ABD5));
            ds.setUnderlineText(false);
            ds.clearShadowLayer();
        }
    }
}
