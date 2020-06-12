package com.baza.android.bzw.businesscontroller.friend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.businesscontroller.friend.adapter.friendholder.BaseFriendHolder;
import com.baza.android.bzw.businesscontroller.friend.adapter.friendholder.FriendRequestHolder;
import com.baza.android.bzw.businesscontroller.friend.adapter.friendholder.NearlyPersonHolder;
import com.baza.android.bzw.businesscontroller.friend.adapter.friendholder.NormalFriendHolder;
import com.bznet.android.rcbox.R;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/9/21.
 * Title：
 * Note：
 */

public class FriendAdapter extends BaseBZWAdapter {
    public static final int TYPE_FRIEND_LIST = 1;
    public static final int TYPE_ADD_FRIEND = 2;
    public static final int TYPE_FRIEND_REQUIRE = 3;
    public static final int TYPE_LOCAL_SEARCH_AND_ADD_CLOUD = 4;
    public static final int TYPE_NEARLY_FRIEND = 5;

    private static final int ITEM_COUNT = 4;
    private static final int ITEM_TYPE_NORMAL = 0;
    private static final int ITEM_TYPE_CLOUD_LOCAL_DEPART = 1;
    private static final int ITEM_TYPE_NEARLY_PERSON = 2;
    private static final int ITEM_TYPE_FRIEND_REQUEST = 3;


    private Context mContext;
    private List<FriendListResultBean.FriendBean> mFriendList;
    private int mType;

//    public FriendAdapter(Context mContext, List<FriendListResultBean.FriendBean> mFriendList, IAdapterEventsListener adapterEventsListener) {
//        this(mContext, mFriendList, TYPE_FRIEND_LIST, adapterEventsListener);
//    }

    public FriendAdapter(Context mContext, List<FriendListResultBean.FriendBean> mFriendList, int type, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = mContext;
        this.mFriendList = mFriendList;
        this.mType = type;
    }

    @Override
    public int getCount() {
        return (mFriendList == null ? 0 : mFriendList.size());
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (mType == TYPE_FRIEND_REQUIRE)
            return ITEM_TYPE_FRIEND_REQUEST;
        if (mType == TYPE_NEARLY_FRIEND)
            return ITEM_TYPE_NEARLY_PERSON;
        if (mType == TYPE_LOCAL_SEARCH_AND_ADD_CLOUD && mFriendList.get(position) == null)
            return ITEM_TYPE_CLOUD_LOCAL_DEPART;
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseFriendHolder baseFriendHolder = null;
        if (convertView == null) {
            switch (getItemViewType(position)) {
                case ITEM_TYPE_NORMAL:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.child_item_for_friend_adapter, null);
                    baseFriendHolder = new NormalFriendHolder(convertView, mContext, mType, mIAdapterEventsListener);
                    convertView.setTag(baseFriendHolder);
                    break;
                case ITEM_TYPE_CLOUD_LOCAL_DEPART:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_local_cloud_friend_depart, null);
                    break;
                case ITEM_TYPE_FRIEND_REQUEST:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.child_item_for_friend_request, null);
                    baseFriendHolder = new FriendRequestHolder(convertView, mContext, mIAdapterEventsListener);
                    convertView.setTag(baseFriendHolder);
                    break;
                case ITEM_TYPE_NEARLY_PERSON:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.child_item_for_nearly_person, null);
                    baseFriendHolder = new NearlyPersonHolder(convertView, mContext, mIAdapterEventsListener);
                    convertView.setTag(baseFriendHolder);
                    break;
            }

        } else
            baseFriendHolder = (BaseFriendHolder) convertView.getTag();

        if (baseFriendHolder != null)
            baseFriendHolder.refreshData(mFriendList.get(position), position);
        return convertView;
    }


}
