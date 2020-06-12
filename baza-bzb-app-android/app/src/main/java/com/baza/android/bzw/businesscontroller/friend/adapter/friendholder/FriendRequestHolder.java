package com.baza.android.bzw.businesscontroller.friend.adapter.friendholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/10/19.
 * Title：
 * Note：
 */

public class FriendRequestHolder extends BaseFriendHolder implements View.OnLongClickListener {
    TextView textView_title;
    TextView textView_msg;

    public FriendRequestHolder(View convertView, Context context, BaseBZWAdapter.IAdapterEventsListener adapterEventsListener) {
        super(convertView, context, adapterEventsListener);
        textView_title = convertView.findViewById(R.id.tv_title);
        textView_msg = convertView.findViewById(R.id.tv_msg);
        textView_process.setOnClickListener(this);
        convertView.setOnLongClickListener(this);
    }


    @Override
    public void refreshData(FriendListResultBean.FriendBean friendBean, int position) {
        super.refreshData(friendBean, position);
        textView_title.setText(mResources.getString(R.string.job_title, (TextUtils.isEmpty(friendBean.title) ? mResources.getString(R.string.job_message_unknown) : friendBean.title)));
        boolean isNormal = (friendBean.requestStatus == FriendListResultBean.FriendBean.TYPE_NORMAL);
        textView_process.setText((isNormal ? R.string.friend_request_accept : R.string.has_add));
        textView_process.setTextColor(mResources.getColor((isNormal ? android.R.color.white : R.color.text_color_grey_94A1A5)));
        textView_process.setBackgroundResource((isNormal ? R.drawable.resume_request_share_btn_bg : R.drawable.bg_city_item_normal));
        if (!TextUtils.isEmpty(friendBean.message)) {
            textView_msg.setText(friendBean.message);
            textView_msg.setVisibility(View.VISIBLE);
        } else
            textView_msg.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_process:
                if (mIAdapterEventsListener != null && (mFriendBean.requestStatus == FriendListResultBean.FriendBean.TYPE_NORMAL))
                    mIAdapterEventsListener.onAdapterEventsArrival(AdapterEventIdConst.ADAPTER_EVENT_AGREE_FRIEND, mPosition, null, mFriendBean);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mIAdapterEventsListener != null)
            mIAdapterEventsListener.onAdapterEventsArrival(AdapterEventIdConst.ADAPTER_EVENT_DELETE_FRIEND_REQUEST, mPosition, null, mFriendBean);
        return true;
    }
}
