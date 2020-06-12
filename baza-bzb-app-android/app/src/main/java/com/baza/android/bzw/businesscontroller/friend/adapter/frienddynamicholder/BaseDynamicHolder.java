package com.baza.android.bzw.businesscontroller.friend.adapter.frienddynamicholder;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.friend.DynamicListResultBean;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.slib.utils.LoadImageUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/9/27.
 * Title：
 * Note：
 */

public abstract class BaseDynamicHolder implements View.OnClickListener {
    protected Context mContext;
    protected Resources mResources;
    protected BaseBZWAdapter.IAdapterEventsListener mAdapterEventsListener;
    protected TextView textView_friendName;
    protected TextView textView_time;
    protected TextView textView_avatar;
    protected ImageView imageView_avatar;
    protected View view_avatar;
    public View view_depart;
    protected View view_typeItem;
    protected int mPosition;
    protected DynamicListResultBean.DynamicBean mDynamicBean;

    public BaseDynamicHolder(Context mContext, View contentView, BaseBZWAdapter.IAdapterEventsListener mAdapterEventsListener) {
        this.mContext = mContext;
        this.mResources = mContext.getResources();
        this.mAdapterEventsListener = mAdapterEventsListener;
        initView(contentView);
    }

    private void initView(View contentView) {
        view_depart = contentView.findViewById(R.id.view_depart_line);
        textView_friendName = contentView.findViewById(R.id.tv_friend_name);
        textView_time = contentView.findViewById(R.id.tv_time);
        textView_avatar = contentView.findViewById(R.id.tv_avatar);
        imageView_avatar = contentView.findViewById(R.id.civ_avatar);
        view_avatar = contentView.findViewById(R.id.fl_avatar);
        FrameLayout frameLayout = contentView.findViewById(R.id.fl_container);
        view_typeItem = getTypeItemView();
        frameLayout.addView(view_typeItem);
        initTypeItemView();
        view_avatar.setOnClickListener(this);
        textView_friendName.setOnClickListener(this);
        textView_time.setOnClickListener(this);
    }

    public void setData(int position, DynamicListResultBean.DynamicBean data) {
        this.mPosition = position;
        this.mDynamicBean = data;
        String nameShow = (!TextUtils.isEmpty(mDynamicBean.nickName) ? mDynamicBean.nickName : (!TextUtils.isEmpty(mDynamicBean.trueName) ? mDynamicBean.trueName : CommonConst.STR_DEFAULT_USER_NAME_SX));
        textView_friendName.setText(nameShow);
        textView_time.setText( FriendlyShowInfoManager.getInstance().getDynamicShowTime(mDynamicBean.updated));
        if (!TextUtils.isEmpty(mDynamicBean.avatar) || nameShow.equals(CommonConst.STR_DEFAULT_USER_NAME_SX)) {
            textView_avatar.setVisibility(View.GONE);
            LoadImageUtil.loadImage(mDynamicBean.avatar, R.drawable.avatar_def, imageView_avatar);
            imageView_avatar.setVisibility(View.VISIBLE);
        } else {
            imageView_avatar.setVisibility(View.GONE);
            textView_avatar.setText(nameShow.substring(0, 1));
            textView_avatar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_avatar:
            case R.id.tv_friend_name:
            case R.id.tv_time:
                sendAdapterEvent(AdapterEventIdConst.ADAPTER_EVENT_CLICK_TO_FRIEND_HOME, mPosition, null, mDynamicBean);
                break;
        }
    }

    protected abstract View getTypeItemView();

    protected abstract void initTypeItemView();

    public void sendAdapterEvent(int adapterEventId, int position, View v, Object input) {
        if (mAdapterEventsListener != null)
            mAdapterEventsListener.onAdapterEventsArrival(adapterEventId, position, v, input);
    }
}
