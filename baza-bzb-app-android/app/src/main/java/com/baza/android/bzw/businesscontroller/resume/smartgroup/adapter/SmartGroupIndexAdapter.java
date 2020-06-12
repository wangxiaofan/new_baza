package com.baza.android.bzw.businesscontroller.resume.smartgroup.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.smartgroup.GroupIndexResultBean;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.slib.utils.AppUtil;
import com.slib.utils.DateUtil;
import com.bznet.android.rcbox.R;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/11/27.
 * Title：
 * Note：
 */
public class SmartGroupIndexAdapter extends BaseBZWAdapter implements View.OnClickListener {
    private Context mContext;
    private Resources mResources;
    private SparseArray<Drawable> drawableSparseArray;
    private List<GroupIndexResultBean.GroupIndexBean> mDataList;

    public SmartGroupIndexAdapter(Context context, List<GroupIndexResultBean.GroupIndexBean> dataList, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = context;
        this.mDataList = dataList;
        mResources = mContext.getResources();
        initDrawables();
    }

    private void initDrawables() {
        drawableSparseArray = new SparseArray<>(6);
        drawableSparseArray.put(CommonConst.SmartGroupType.GROUP_TYPE_TIME, AppUtil.drawableInit(R.drawable.smart_group_ic_time, mResources));
        drawableSparseArray.put(CommonConst.SmartGroupType.GROUP_TYPE_COMPANY, AppUtil.drawableInit(R.drawable.smart_group_ic_company, mResources));
        drawableSparseArray.put(CommonConst.SmartGroupType.GROUP_TYPE_DEGREE, AppUtil.drawableInit(R.drawable.smart_group_ic_degree, mResources));
        drawableSparseArray.put(CommonConst.SmartGroupType.GROUP_TYPE_TITLE, AppUtil.drawableInit(R.drawable.smart_group_ic_title, mResources));
        drawableSparseArray.put(CommonConst.SmartGroupType.GROUP_TYPE_WORK_EXPERIENCE, AppUtil.drawableInit(R.drawable.smart_group_ic_work_experoence, mResources));
        drawableSparseArray.put(CommonConst.SmartGroupType.GROUP_TYPE_CUSTOMER, AppUtil.drawableInit(R.drawable.smart_group_ic_customer, mResources));
    }

    @Override
    public void onDestroy() {
        if (drawableSparseArray != null) {
            for (int i = 0; i < drawableSparseArray.size(); i++) {
                drawableSparseArray.valueAt(i).setCallback(null);
            }
            drawableSparseArray.clear();
            drawableSparseArray = null;
        }

    }

    @Override
    public int getCount() {
        return (mDataList != null ? mDataList.size() : 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.smart_group_index_adapter_item, null);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setOnClickListener(this);
            convertView.setTag(viewHolder);
        }
        refreshItem(convertView, position);

        return convertView;
    }

    public void refreshItem(View convertView, int position) {
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        GroupIndexResultBean.GroupIndexBean groupIndexBean = mDataList.get(position);
        viewHolder.textView_title.setText(groupIndexBean.groupName);
        viewHolder.textView_title.setCompoundDrawables(drawableSparseArray.get(groupIndexBean.groupType), null, null, null);
        if (groupIndexBean.secondLevelGroupCount > 0) {
            viewHolder.textView_time.setText(DateUtil.longMillions2FormatDate(groupIndexBean.groupTime, DateUtil.DEFAULT_API_FORMAT));
            viewHolder.textView_time.setVisibility(View.VISIBLE);
        } else
            viewHolder.textView_time.setVisibility(View.GONE);
        if (groupIndexBean.groupType == CommonConst.SmartGroupType.GROUP_TYPE_CUSTOMER) {
            viewHolder.textView_des.setText(groupIndexBean.secondLevelGroupCount > 0 ? mResources.getString(R.string.customer_smart_group_normal, FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(groupIndexBean.secondLevelGroupCount, false, true), FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(groupIndexBean.resumeCount, false, true)) : mResources.getString(R.string.customer_smart_group_none));
        } else
            viewHolder.textView_des.setText(mResources.getString(R.string.smart_group_index_des, FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(groupIndexBean.secondLevelGroupCount, false, true), FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(groupIndexBean.resumeCount, false, true)));

        convertView.setTag(R.id.hold_tag_id_one, position);
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag(R.id.hold_tag_id_one);
        sendAdapterEventToHost(AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL, position, null, mDataList.get(position));
    }

    private static class ViewHolder {
        View convertView;
        TextView textView_title;
        TextView textView_time;
        TextView textView_des;

        public ViewHolder(View convertView) {
            this.convertView = convertView;
            textView_title = convertView.findViewById(R.id.tv_title);
            textView_time = convertView.findViewById(R.id.tv_time);
            textView_des = convertView.findViewById(R.id.tv_des);
        }
    }
}
