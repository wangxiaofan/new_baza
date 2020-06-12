package com.baza.android.bzw.businesscontroller.resume.smartgroup.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.smartgroup.SmartGroupFoldersResultBean;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.SwipeMenuLayout;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/8/29.
 * Title：
 * Note：
 */
public class SmartGroupFolderAdapter extends BaseBZWAdapter implements View.OnClickListener {
    private Context mContext;
    private Resources mResources;
    private int mType;
    private List<SmartGroupFoldersResultBean.SmartGroupFolderBean> mDataList;

    public SmartGroupFolderAdapter(Context context, int type, List<SmartGroupFoldersResultBean.SmartGroupFolderBean> dataList, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mType = type;
        this.mDataList = dataList;
        this.mContext = context;
        this.mResources = mContext.getResources();
    }

    @Override
    public int getCount() {
        return (mDataList == null ? 0 : mDataList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.smart_group_adapter_folder_item, null);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
            viewHolder.view_sideMenuOne.setOnClickListener(this);
            viewHolder.view_sideMenuTwo.setOnClickListener(this);
            viewHolder.view_content.setOnClickListener(this);
        }
        refreshTargetItem(convertView, position);
        return convertView;
    }

    private void refreshTargetItem(View convertView, int position) {
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        SmartGroupFoldersResultBean.SmartGroupFolderBean smartGroupFolderBean = mDataList.get(position);
        viewHolder.swipeMenuLayout.setSwipeEnable(mType == CommonConst.SmartGroupType.GROUP_TYPE_CUSTOMER);
        viewHolder.textView_groupName.setText(smartGroupFolderBean.groupName);
        viewHolder.textView_countInfo.setText(mResources.getString(R.string.smart_group_folder_inner_value, FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(smartGroupFolderBean.resumeCount, false, true)));
        viewHolder.view_sideMenuOne.setTag(R.id.hold_tag_id_one, viewHolder);
        viewHolder.view_sideMenuOne.setTag(R.id.hold_tag_id_two, position);
        viewHolder.view_sideMenuTwo.setTag(R.id.hold_tag_id_one, viewHolder);
        viewHolder.view_sideMenuTwo.setTag(R.id.hold_tag_id_two, position);
        viewHolder.view_content.setTag(position);
    }

    public void refresh(int type, View convertView, int position) {
        mType = type;
        if (convertView == null)
            notifyDataSetChanged();
        else {
            refreshTargetItem(convertView, position);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_resume_ele_side_menu_one:
                ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.hold_tag_id_one);
                viewHolder.swipeMenuLayout.smoothClose();
                int position = (int) v.getTag(R.id.hold_tag_id_two);
                sendAdapterEventToHost(AdapterEventIdConst.ADAPTER_EVENT_MODIFY_SMART_GROUP_NAME, position, null, mDataList.get(position));
                break;
            case R.id.view_resume_ele_side_menu_two:
                viewHolder = (ViewHolder) v.getTag(R.id.hold_tag_id_one);
                viewHolder.swipeMenuLayout.smoothClose();
                position = (int) v.getTag(R.id.hold_tag_id_two);
                sendAdapterEventToHost(AdapterEventIdConst.ADAPTER_EVENT_DELETE_SMART_GROUP, position, null, mDataList.get(position));
                break;
            case R.id.content_view:
                position = (int) v.getTag();
                sendAdapterEventToHost(AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL, position, null, mDataList.get(position));
                break;
        }
    }

    private static class ViewHolder {
        TextView textView_groupName;
        TextView textView_countInfo;
        ViewGroup view_sideMenuOne;
        ViewGroup view_sideMenuTwo;
        SwipeMenuLayout swipeMenuLayout;
        View view_content;

        public ViewHolder(View convertView) {
            view_content = convertView.findViewById(R.id.content_view);
            swipeMenuLayout = convertView.findViewById(R.id.swipe_layout);
            view_sideMenuOne = convertView.findViewById(R.id.view_resume_ele_side_menu_one);
            view_sideMenuTwo = convertView.findViewById(R.id.view_resume_ele_side_menu_two);
            textView_groupName = convertView.findViewById(R.id.tv_group_name);
            textView_countInfo = convertView.findViewById(R.id.tv_count_info);
        }
    }
}
