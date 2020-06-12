package com.baza.android.bzw.businesscontroller.friend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baza.android.bzw.bean.friend.DynamicListResultBean;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.businesscontroller.friend.adapter.frienddynamicholder.AddFriendDynamicHolder;
import com.baza.android.bzw.businesscontroller.friend.adapter.frienddynamicholder.BaseDynamicHolder;
import com.baza.android.bzw.businesscontroller.friend.adapter.frienddynamicholder.DownLoadPCDynamicHolder;
import com.baza.android.bzw.businesscontroller.friend.adapter.frienddynamicholder.ShareRequestDynamicHolder;
import com.baza.android.bzw.businesscontroller.friend.adapter.frienddynamicholder.SyncResumeDynamicHolder;
import com.bznet.android.rcbox.R;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/9/26.
 * Title：
 * Note：
 */

public class FriendDynamicAdapter extends BaseBZWAdapter {
    private static final int TYPE_COUNT = 4;
    private static final int TYPE_DOWNLOAD_PC = 0;
    private static final int TYPE_SYNC_RESUME = 1;
    private static final int TYPE_SHARE_REQUEST = 2;
    private static final int TYPE_ADD_FRIEND = 3;
    private Context mContext;
    private List<DynamicListResultBean.DynamicBean> mDynamicList;
    private boolean mHideDepart;

    public FriendDynamicAdapter(Context mContext, List<DynamicListResultBean.DynamicBean> mDynamicList, IAdapterEventsListener adapterEventsListener) {
        this(mContext, false, mDynamicList, adapterEventsListener);
    }

    public FriendDynamicAdapter(Context mContext, boolean mHideDepart, List<DynamicListResultBean.DynamicBean> mDynamicList, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = mContext;
        this.mHideDepart = mHideDepart;
        this.mDynamicList = mDynamicList;
    }

    @Override
    public int getCount() {
        return (mDynamicList == null ? 0 : mDynamicList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseDynamicHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_for_dynamic_base, null);
            holder = getHolder(position, convertView);
            convertView.setTag(holder);
        } else
            holder = (BaseDynamicHolder) convertView.getTag();
        holder.setData(position, mDynamicList.get(position));
        if (mHideDepart)
            holder.view_depart.setVisibility(View.GONE);
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        DynamicListResultBean.DynamicBean dynamicBean = mDynamicList.get(position);
        int type = TYPE_DOWNLOAD_PC;
        switch (dynamicBean.sceneId) {
            case DynamicListResultBean.DynamicBean.DYNAMIC_TYPE_SYNC_RESUME:
                type = TYPE_SYNC_RESUME;
                break;
            case DynamicListResultBean.DynamicBean.DYNAMIC_TYPE_SHARE_REQUEST:
                type = TYPE_SHARE_REQUEST;
                break;
            case DynamicListResultBean.DynamicBean.DYNAMIC_TYPE_ADD_FRIEND:
                type = TYPE_ADD_FRIEND;
                break;
        }
        return type;
    }

    private BaseDynamicHolder getHolder(int position, View convertView) {
        BaseDynamicHolder holder = null;
        switch (getItemViewType(position)) {
            case TYPE_DOWNLOAD_PC:
                holder = new DownLoadPCDynamicHolder(mContext, convertView, mIAdapterEventsListener);
                break;
            case TYPE_SYNC_RESUME:
                holder = new SyncResumeDynamicHolder(mContext, convertView, mIAdapterEventsListener);
                break;
            case TYPE_SHARE_REQUEST:
                holder = new ShareRequestDynamicHolder(mContext, convertView, mIAdapterEventsListener);
                break;
            case TYPE_ADD_FRIEND:
                holder = new AddFriendDynamicHolder(mContext, convertView, mIAdapterEventsListener);
                break;
        }
        return holder;
    }
}
