package com.baza.android.bzw.businesscontroller.friend.adapter.friendholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.friend.FriendInfoResultBean;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.businesscontroller.friend.adapter.FriendAdapter;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/10/19.
 * Title：
 * Note：
 */

public class NormalFriendHolder extends BaseFriendHolder {
    TextView textView_title;
    TextView textView_company;
    private int mType;

    public NormalFriendHolder(View convertView, Context context, int type, BaseBZWAdapter.IAdapterEventsListener adapterEventsListener) {
        super(convertView, context, adapterEventsListener);
        this.mType = type;
        textView_title = convertView.findViewById(R.id.tv_title);
        textView_company = convertView.findViewById(R.id.tv_company);
        textView_process.setOnClickListener(this);
    }

    @Override
    public void refreshData(FriendListResultBean.FriendBean friendBean, int position) {
        super.refreshData(friendBean, position);
        textView_company.setText(TextUtils.isEmpty(friendBean.company) ? mResources.getString(R.string.company_message_unknown) : friendBean.company);
        textView_title.setText(mResources.getString(R.string.job_title, (TextUtils.isEmpty(friendBean.title) ? mResources.getString(R.string.job_message_unknown) : friendBean.title)));
        textView_process.setText((mType == FriendAdapter.TYPE_FRIEND_LIST || friendBean.isFriend == FriendInfoResultBean.FriendInfoBean.FRIEND_YES) ? R.string.send_msg : R.string.add_friend);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_process:
                if (mIAdapterEventsListener != null)
                    mIAdapterEventsListener.onAdapterEventsArrival((mType == FriendAdapter.TYPE_FRIEND_LIST || mFriendBean.isFriend == FriendInfoResultBean.FriendInfoBean.FRIEND_YES ? AdapterEventIdConst.ADAPTER_EVENT_FRIEND_SEND_MSG : AdapterEventIdConst.ADAPTER_EVENT_ADD_FRIEND), mPosition, null, mFriendBean);
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
