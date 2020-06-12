package com.baza.android.bzw.businesscontroller.message.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.message.ExtraMessageBean;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.businesscontroller.im.IMConst;
import com.baza.android.bzw.businesscontroller.im.IMRecentContact;
import com.baza.android.bzw.businesscontroller.im.IMUserInfoProvider;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.manager.IMManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.slib.utils.DateUtil;
import com.slib.utils.LoadImageUtil;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/6/1.
 * Title：
 * Note：
 */

public class ConversationListAdapter extends BaseBZWAdapter implements View.OnClickListener, View.OnLongClickListener {
    private Context mContext;
    private List<IMRecentContact> mRecentContactList;
    private IMUserInfoProvider mUserInfoProvider;
    private Resources mResources;
    private String mMyNameDefault;

    public ConversationListAdapter(Context mContext, List<IMRecentContact> mRecentContactList, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = mContext;
        this.mResources = mContext.getResources();
        this.mRecentContactList = mRecentContactList;
        this.mUserInfoProvider = IMUserInfoProvider.getInstance(mContext);
        mMyNameDefault = mResources.getString(R.string.my_name_default);
    }

    @Override
    public int getCount() {
//        return mRecentContactList == null ? 0 : mRecentContactList.size();
        //屏蔽消息里面的聊天列表
        return 0 ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_conversition, null);
            IMViewHolder viewHolder = new IMViewHolder(convertView);
            convertView.setTag(viewHolder);
            convertView.setOnClickListener(this);
            convertView.setOnLongClickListener(this);
        }
        refreshTargetView(convertView, position);
        return convertView;
    }

    public void refreshTargetView(View convertView, int position) {
        if (convertView == null)
            return;
        IMViewHolder viewHolder = (IMViewHolder) convertView.getTag();
        if (viewHolder == null)
            return;
        IMRecentContact imRecentContact = mRecentContactList.get(position);
        viewHolder.textView_time.setText(DateUtil.formatMsgFriendlyTime(imRecentContact.getTime()));
        if (imRecentContact.getUnreadCount() > 0) {
            viewHolder.textView_unReadCount.setText(String.valueOf(imRecentContact.getUnreadCount()));
            viewHolder.textView_unReadCount.setVisibility(View.VISIBLE);
        } else
            viewHolder.textView_unReadCount.setVisibility(View.GONE);

        switch (imRecentContact.getStatus()) {
            case IMConst.IM_MESSAGE_STATUS_FAILED:
                viewHolder.imageView_status.setImageResource(R.drawable.nim_g_ic_failed_small);
                viewHolder.imageView_status.setVisibility(View.VISIBLE);
                break;
            case IMConst.IM_MESSAGE_STATUS_SENDING:
                viewHolder.imageView_status.setImageResource(R.drawable.nim_recent_contact_ic_sending);
                viewHolder.imageView_status.setVisibility(View.VISIBLE);
                break;
            default:
                viewHolder.imageView_status.setVisibility(View.GONE);
                break;
        }


        String name = null;
        UserInfoBean userInfoBean = mUserInfoProvider.getBZWUserInfo(imRecentContact.getContactId());
        if (userInfoBean != null)
            name = (TextUtils.isEmpty(userInfoBean.nickName) ? userInfoBean.trueName : userInfoBean.nickName);
        if (TextUtils.isEmpty(name))
            name = CommonConst.STR_DEFAULT_USER_NAME_CONVERSATION;
        viewHolder.textView_name.setText(name);
        if ((userInfoBean != null && !TextUtils.isEmpty(userInfoBean.avatar)) || name.equals(CommonConst.STR_DEFAULT_USER_NAME_CONVERSATION)) {
            LoadImageUtil.loadImage((userInfoBean != null ? userInfoBean.avatar : null), R.drawable.avatar_def, viewHolder.imageView_avatar);
            viewHolder.imageView_avatar.setVisibility(View.VISIBLE);
            viewHolder.textView_avatar.setVisibility(View.GONE);
        } else {
            viewHolder.imageView_avatar.setVisibility(View.GONE);
            viewHolder.textView_avatar.setText(name.substring(0, 1));
            viewHolder.textView_avatar.setVisibility(View.VISIBLE);
        }

        ExtraMessageBean extraMessageBean = imRecentContact.getExtraMessage();
        if (extraMessageBean == null)
            viewHolder.textView_message.setText(IMManager.getInstance(mContext).getCheckedTextMessage(imRecentContact.getContent()));
        else {
            viewHolder.textView_message.setText(getExtraMessageTitle(mResources.getString(R.string.other_side), extraMessageBean, imRecentContact.getFromAccount().equals(UserInfoManager.getInstance().getUserInfo().neteaseId)));
        }
        viewHolder.textView_message.setTag(position);
    }

    private String getExtraMessageTitle(String name, ExtraMessageBean extraMessageBean, boolean isMine) {
        String title;
        switch (extraMessageBean.type) {
            case ExtraMessageBean.TYPE_FRIEND_AGREE_NOTIFY:
                title = extraMessageBean.content;
                break;
            default:
                title = (isMine ? mResources.getString(R.string.msg_unknown_from_you) : mResources.getString(R.string.msg_unknown_from_other));
                break;
        }

        return title;
    }

    @Override
    public void onClick(View v) {
        IMViewHolder viewHolder = (IMViewHolder) v.getTag();
        int position = (int) (viewHolder.textView_message).getTag();
        sendAdapterEventToHost(AdapterEventIdConst.CONVERSATION_ADAPTER_EVENT_TO_CHAT, position, null, mRecentContactList.get(position));
    }

    @Override
    public boolean onLongClick(View v) {
        IMViewHolder viewHolder = (IMViewHolder) v.getTag();
        int position = (int) (viewHolder.textView_message).getTag();
        sendAdapterEventToHost(AdapterEventIdConst.CONVERSATION_ADAPTER_EVENT_ITEM_LONG_CLICK, position, v, mRecentContactList.get(position));
        return true;
    }


    static class IMViewHolder {
        TextView textView_avatar;
        TextView textView_unReadCount;
        ImageView imageView_status;
        ImageView imageView_avatar;
        TextView textView_name;
        TextView textView_message;
        TextView textView_time;

        IMViewHolder(View convertView) {
            textView_avatar = convertView.findViewById(R.id.tv_avatar);
            textView_unReadCount = convertView.findViewById(R.id.tv_un_read_count);
            imageView_status = convertView.findViewById(R.id.iv_img_msg_status);
            imageView_avatar = convertView.findViewById(R.id.civ_avatar);
            textView_name = convertView.findViewById(R.id.tv_user_name);
            textView_message = convertView.findViewById(R.id.tv_message);
            textView_time = convertView.findViewById(R.id.tv_time);
        }
    }
}
