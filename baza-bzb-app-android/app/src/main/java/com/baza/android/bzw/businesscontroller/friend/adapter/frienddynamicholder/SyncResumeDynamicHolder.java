package com.baza.android.bzw.businesscontroller.friend.adapter.frienddynamicholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.friend.DynamicListResultBean;
import com.baza.android.bzw.dao.ResumeDao;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/9/27.
 * Title：
 * Note：
 */

public class SyncResumeDynamicHolder extends BaseDynamicHolder {
    TextView textView_source;
    TextView textView_resumeCount;

    public SyncResumeDynamicHolder(Context mContext, View contentView, BaseBZWAdapter.IAdapterEventsListener mAdapterEventsListener) {
        super(mContext, contentView, mAdapterEventsListener);
    }

    @Override
    protected View getTypeItemView() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_dynamic_type_sync_resume, null);
    }


    @Override
    protected void initTypeItemView() {
        textView_source = view_typeItem.findViewById(R.id.tv_source);
        textView_resumeCount = view_typeItem.findViewById(R.id.tv_resume_count);
    }

    @Override
    public void setData(int position, DynamicListResultBean.DynamicBean data) {
        super.setData(position, data);
        DynamicListResultBean.SyncResumeDynamicInfo syncResumeInfo = mDynamicBean.getSyncResumeDynamic();
        if (syncResumeInfo == null)
            return;
        textView_source.setText(mResources.getString(R.string.dynamic_sync_resume_source, ResumeDao.getSourceForShow(syncResumeInfo.source)));
        textView_resumeCount.setText(mResources.getString(R.string.dynamic_sync_resume_count, AppUtil.formatTob(syncResumeInfo.count)));
    }
}
