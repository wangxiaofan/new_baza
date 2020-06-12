package com.baza.android.bzw.businesscontroller.message.adapter.chatHolder;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.bean.message.ExtraMessageBean;
import com.baza.android.bzw.businesscontroller.im.BZWIMMessage;
import com.baza.android.bzw.businesscontroller.im.IMConst;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.manager.IMManager;
import com.baza.android.bzw.widget.emotion.MoonUtil;
import com.slib.utils.AppUtil;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Vincent.Lei on 2017/6/2.
 * Title：
 * Note：
 */

public class TextHolder extends ChatViewHolder {
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_FRIEND_TIPS = 1;
    public static final int TYPE_CUSTOMER_UN_KNOWN = 2;
    private TextView textView;
    private int type;

    public TextHolder(Context context, View convertView, IChatExtraMsgProvider mChatExtraMsgProvider) {
        this(context, convertView, mChatExtraMsgProvider, TYPE_DEFAULT);
    }

    public TextHolder(Context context, View convertView, IChatExtraMsgProvider mChatExtraMsgProvider, int type) {
        super(context, convertView, mChatExtraMsgProvider);
        this.type = type;
    }

    @Override
    public int getItemTypeViewId() {
        return R.layout.chat_item_text;
    }

    @Override
    public void init(View viewContentView) {
        textView = (TextView) viewContentView;
    }


    @Override
    public void refresh(BZWIMMessage bzwimMessage, int position) {
        if (type == TYPE_CUSTOMER_UN_KNOWN) {
            textView.setText(R.string.unkonw_im);
        } else if (type == TYPE_FRIEND_TIPS) {
            ExtraMessageBean extraMessageBean = bzwimMessage.getExtraMessage();
            if (extraMessageBean == null)
                return;
            linearLayout_mainItem.setGravity(Gravity.CENTER);
            view_avatarLeft.setVisibility(View.GONE);
            view_avatarRight.setVisibility(View.GONE);
            textView.setBackgroundResource(R.drawable.shape_background_sys_msg_depart_time);
            textView.setText(extraMessageBean.content);
            textView.setTextColor(mResources.getColor(R.color.text_color_black_4E5968));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, ScreenUtil.px2dip(mResources.getDimension(R.dimen.text_size_10)));
            return;
        }
        super.refresh(bzwimMessage, position);
    }

    @Override
    public void refreshView(BZWIMMessage bzwimMessage, int position) {
        linearLayout_content.setBackgroundResource((bzwimMessage.isReceivedMessage() ? R.drawable.image_chat_bg_left_c : R.drawable.image_chat_bg_right_c));
        if (TextUtils.isEmpty(bzwimMessage.imMessage.getContent())) {
            Map<String, Object> remoteExtension = bzwimMessage.imMessage.getRemoteExtension();
            if (remoteExtension != null && remoteExtension.containsKey(IMConst.DEFAULT_CONTENT))
                textView.setText(remoteExtension.get(IMConst.DEFAULT_CONTENT).toString());
            else
                textView.setText(R.string.unkonw_im);
            return;
        }
        if (IMManager.getInstance(mContext).isTextMessageIllegal(bzwimMessage.imMessage.getContent())) {
            textView.setText(IMManager.getInstance(mContext).getDefaultShowTextForIllegal());
            return;
        }
        List<AppUtil.LinkData> list = AppUtil.urlToLink(bzwimMessage.imMessage.getContent());
        if (list != null && !list.isEmpty()) {
            SpannableString spannableString = new SpannableString(bzwimMessage.imMessage.getContent());
            for (int i = 0, size = list.size(); i < size; i++) {
                final AppUtil.LinkData linkData = list.get(i);
                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        mChatExtraMsgProvider.getAdapterEventsListener().onAdapterEventsArrival(AdapterEventIdConst.ADAPTER_EVENT_CLICK_LINK_TEXT, -1, null, linkData.url);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(mResources.getColor(R.color.text_color_blue_53ABD5));
                        ds.setUnderlineText(false);
                        ds.clearShadowLayer();
                    }
                }, linkData.start, linkData.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            MoonUtil.makeSpannableStringEmotion(textView.getContext(), spannableString);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(spannableString);
        } else {
            textView.setText(MoonUtil.makeStringEmotion(textView.getContext(), bzwimMessage.imMessage.getContent()));
        }

    }

    @Override
    public boolean isUseDefaultBubble() {
        return false;
    }
}
