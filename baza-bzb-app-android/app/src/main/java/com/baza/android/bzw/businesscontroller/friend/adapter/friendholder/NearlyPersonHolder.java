package com.baza.android.bzw.businesscontroller.friend.adapter.friendholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.friend.FriendInfoResultBean;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.bean.friend.ListNearlyResultBean;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.manager.AddressManager;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/10/19.
 * Title：
 * Note：
 */

public class NearlyPersonHolder extends BaseFriendHolder {
    private static final int KM = 1000;
    TextView textView_msg;
    TextView textView_distance;

    public NearlyPersonHolder(View convertView, Context context, BaseBZWAdapter.IAdapterEventsListener adapterEventsListener) {
        super(convertView, context, adapterEventsListener);
        textView_msg = convertView.findViewById(R.id.tv_msg);
        textView_distance = convertView.findViewById(R.id.tv_distance);
        textView_process.setOnClickListener(this);
    }

    @Override
    public void refreshData(FriendListResultBean.FriendBean friendBean, int position) {
        super.refreshData(friendBean, position);
        ListNearlyResultBean.NearlyPersonBean nearlyPersonBean = (ListNearlyResultBean.NearlyPersonBean) friendBean;
        textView_msg.setText(AddressManager.getInstance().getCityNameByCode(friendBean.location) + "  " + (TextUtils.isEmpty(friendBean.company) ? mResources.getString(R.string.company_message_unknown) : friendBean.company));
        textView_process.setText((friendBean.isFriend != FriendInfoResultBean.FriendInfoBean.FRIEND_YES) ? R.string.add_friend : R.string.send_msg);
        boolean kmShow = (nearlyPersonBean.distance >= KM);
        textView_distance.setText(mResources.getString((kmShow ? R.string.distance_show_km : R.string.distance_show_m), (kmShow ? AppUtil.formatFeeFloat(nearlyPersonBean.distance * 1.0f / KM) : nearlyPersonBean.distance)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_process:
                if (mIAdapterEventsListener != null)
                    mIAdapterEventsListener.onAdapterEventsArrival((mFriendBean.isFriend == FriendInfoResultBean.FriendInfoBean.FRIEND_YES ? AdapterEventIdConst.ADAPTER_EVENT_FRIEND_SEND_MSG : AdapterEventIdConst.ADAPTER_EVENT_ADD_FRIEND), mPosition, null, mFriendBean);
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
