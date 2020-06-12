package com.baza.android.bzw.businesscontroller.friend.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.constant.CommonConst;
import com.slib.utils.LoadImageUtil;
import com.slib.utils.ToastUtil;
import com.bznet.android.rcbox.R;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2018/1/11.
 * Title：
 * Note：
 */

public class FriendSelectedAdapter extends BaseBZWAdapter implements View.OnClickListener {
    private static final int MAX_CHOSE_COUNT = 10;
    private Context mContext;
    private List<FriendListResultBean.FriendBean> mFriendList;
    private HashSet<String> mSelectedSet = new HashSet<>();
    private HashSet<String> mSelectedSetTemp = new HashSet<>();

    public FriendSelectedAdapter(Context context, List<FriendListResultBean.FriendBean> friendList, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = context;
        this.mFriendList = friendList;
    }

    @Override
    public int getCount() {
        return (mFriendList == null ? 0 : mFriendList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_for_friend_selected, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
            viewHolder.imageView_selected.setOnClickListener(this);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        FriendListResultBean.FriendBean friendBean = mFriendList.get(position);
        String nameShow = (!TextUtils.isEmpty(friendBean.nickName) ? friendBean.nickName : (!TextUtils.isEmpty(friendBean.trueName) ? friendBean.trueName : CommonConst.STR_DEFAULT_USER_NAME_SX));
        viewHolder.textView_name.setText(nameShow);
        if (!TextUtils.isEmpty(friendBean.avatar) || nameShow.equals(CommonConst.STR_DEFAULT_USER_NAME_SX)) {
            viewHolder.textView_avatar.setVisibility(View.GONE);
            LoadImageUtil.loadImage(friendBean.avatar, R.drawable.avatar_def, viewHolder.imageView_avatar);
            viewHolder.imageView_avatar.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageView_avatar.setVisibility(View.GONE);
            viewHolder.textView_avatar.setText(nameShow.substring(0, 1));
            viewHolder.textView_avatar.setVisibility(View.VISIBLE);
        }
        viewHolder.textView_titleAndCompany.setText(friendBean.company + " " + friendBean.title);
        viewHolder.imageView_selected.setImageResource((mSelectedSet.contains(friendBean.unionId) ? R.drawable.icon_agreement_checked : R.drawable.icon_agreement_unchecked));
        viewHolder.imageView_selected.setTag(position);
        return convertView;
    }

    public void refresh() {
        if (mFriendList != null && !mFriendList.isEmpty()) {
            mSelectedSetTemp.clear();
            mSelectedSetTemp.addAll(mSelectedSet);
            mSelectedSet.clear();
            FriendListResultBean.FriendBean friendBean;
            for (int i = 0, size = mFriendList.size(); i < size; i++) {
                friendBean = mFriendList.get(i);
                if (mSelectedSetTemp.contains(friendBean.unionId))
                    mSelectedSet.add(friendBean.unionId);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_selected:
                int position = (int) v.getTag();
                FriendListResultBean.FriendBean friendBean = mFriendList.get(position);
                if (friendBean.unionId != null) {
                    ImageView imageView = (ImageView) v;
                    if (mSelectedSet.remove(friendBean.unionId))
                        imageView.setImageResource(R.drawable.icon_agreement_unchecked);
                    else if (mSelectedSet.size() < MAX_CHOSE_COUNT) {
                        mSelectedSet.add(friendBean.unionId);
                        imageView.setImageResource(R.drawable.icon_agreement_checked);
                    } else {
                        ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.chose_friend_limit, MAX_CHOSE_COUNT));
                    }
                }
                break;
        }
    }

    public HashSet<String> getSelectedFriendIds() {
        return mSelectedSet;
    }

    static class ViewHolder {
        @BindView(R.id.iv_selected)
        ImageView imageView_selected;
        @BindView(R.id.tv_name)
        TextView textView_name;
        @BindView(R.id.tv_company_and_title)
        TextView textView_titleAndCompany;
        @BindView(R.id.tv_avatar)
        TextView textView_avatar;
        @BindView(R.id.civ_avatar)
        ImageView imageView_avatar;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
