package com.baza.android.bzw.businesscontroller.friend.adapter.friendholder;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.slib.utils.LoadImageUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/10/19.
 * Title：
 * Note：
 */

public class BaseFriendHolder implements View.OnClickListener {
    protected BaseBZWAdapter.IAdapterEventsListener mIAdapterEventsListener;
    protected Context mContext;
    protected Resources mResources;
    protected TextView textView_avatar;
    protected ImageView imageView_avatar;
    protected TextView textView_process;
    protected TextView textView_name;
    protected int mPosition;
    protected FriendListResultBean.FriendBean mFriendBean;

    public BaseFriendHolder(View convertView, Context context, BaseBZWAdapter.IAdapterEventsListener adapterEventsListener) {
        this.mContext = context;
        this.mResources = context.getResources();
        this.mIAdapterEventsListener = adapterEventsListener;
        textView_name = convertView.findViewById(R.id.tv_name);
        textView_process = convertView.findViewById(R.id.tv_process);
        textView_avatar = convertView.findViewById(R.id.tv_avatar);
        imageView_avatar = convertView.findViewById(R.id.civ_avatar);
        convertView.setOnClickListener(this);
    }

    public void refreshData(FriendListResultBean.FriendBean friendBean, int position) {
        this.mPosition = position;
        this.mFriendBean = friendBean;
        String nameShow = (!TextUtils.isEmpty(friendBean.nickName) ? friendBean.nickName : (!TextUtils.isEmpty(friendBean.trueName) ? friendBean.trueName : CommonConst.STR_DEFAULT_USER_NAME_SX));
        textView_name.setText(nameShow);
        if (!TextUtils.isEmpty(friendBean.avatar) || nameShow.equals(CommonConst.STR_DEFAULT_USER_NAME_SX)) {
            textView_avatar.setVisibility(View.GONE);
            LoadImageUtil.loadImage(friendBean.avatar, R.drawable.avatar_def, imageView_avatar);
            imageView_avatar.setVisibility(View.VISIBLE);
        } else {
            imageView_avatar.setVisibility(View.GONE);
            textView_avatar.setText(nameShow.substring(0, 1));
            textView_avatar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (mIAdapterEventsListener != null)
            mIAdapterEventsListener.onAdapterEventsArrival(AdapterEventIdConst.ADAPTER_EVENT_FRIEND_HOME, mPosition, null, mFriendBean);
    }
}
