package com.baza.android.bzw.businesscontroller.find.updateengine.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baza.android.bzw.bean.resume.ResumeUpdateLogResultBean;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.businesscontroller.find.updateengine.adapter.updatelogholder.BaseLogHolder;
import com.baza.android.bzw.businesscontroller.find.updateengine.adapter.updatelogholder.LabelLogHolder;
import com.baza.android.bzw.businesscontroller.find.updateengine.adapter.updatelogholder.RequestShareLogHolder;
import com.baza.android.bzw.businesscontroller.find.updateengine.adapter.updatelogholder.TextRemarkLogHolder;
import com.baza.android.bzw.businesscontroller.find.updateengine.adapter.updatelogholder.TipsLogHolder;
import com.baza.android.bzw.businesscontroller.find.updateengine.adapter.updatelogholder.UpdateLogHolder;
import com.baza.android.bzw.businesscontroller.find.updateengine.adapter.updatelogholder.VoiceLogHolder;
import com.bznet.android.rcbox.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/8/29.
 * Title：
 * Note：
 */

public class UpdateLogAdapter extends BaseBZWAdapter implements BaseLogHolder.ILogAdapterListener {
    private static final int TYPE_COUNT = 7;
    private static final int TYPE_UPDATE = 0;
    private static final int TYPE_LABEL = 1;
    private static final int TYPE_NORMAL_TIPS = 2;
    private static final int TYPE_REQUEST_SHARE = 3;
    private static final int TYPE_TEXT_REMARK = 4;
    private static final int TYPE_IMAGE_REMARK = 5;
    private static final int TYPE_VOICE_REMARK = 6;
    private Context mContext;
    private String mResumeName;
    private List<ResumeUpdateLogResultBean.LogData> mDataList;
    private HashMap<String, String> mUnPickUpMap = new HashMap<>();
    private Drawable mDrawablePickUp;
    private Drawable mDrawableUnPickUp;

    public UpdateLogAdapter(Context mContext, List<ResumeUpdateLogResultBean.LogData> dataList, String candidateName, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = mContext;
        this.mDataList = dataList;
        this.mResumeName = candidateName;
        Resources resources = mContext.getResources();
        mDrawablePickUp = resources.getDrawable(R.drawable.arrow_down_small);
        mDrawablePickUp.setBounds(0, 0, mDrawablePickUp.getIntrinsicWidth(), mDrawablePickUp.getIntrinsicHeight());
        mDrawableUnPickUp = resources.getDrawable(R.drawable.arrow_up_small);
        mDrawableUnPickUp.setBounds(0, 0, mDrawableUnPickUp.getIntrinsicWidth(), mDrawableUnPickUp.getIntrinsicHeight());
    }

    @Override
    public int getCount() {
        return (mDataList == null ? 0 : mDataList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_for_update_log_base, null);
            convertView.setTag(getHolder(convertView, position));
        }
        refreshData(position, convertView);
        return convertView;
    }

    public void refreshData(int position, View convertView) {
        BaseLogHolder baseLogHolder = (BaseLogHolder) convertView.getTag();
        baseLogHolder.setData(position, mDataList.size(), mResumeName, mDataList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        ResumeUpdateLogResultBean.LogData logData = mDataList.get(position);
        if (logData.sceneId == ResumeUpdateLogResultBean.LogData.TYPE_UPDATE_RESUME || logData.sceneId == ResumeUpdateLogResultBean.LogData.TYPE_EDIT_RESUME)
            return TYPE_UPDATE;
        if (logData.sceneId == ResumeUpdateLogResultBean.LogData.TYPE_ADD_LABEL)
            return TYPE_LABEL;
        if (logData.sceneId == ResumeUpdateLogResultBean.LogData.TYPE_ASK_RESUME)
            return TYPE_REQUEST_SHARE;
        if (logData.sceneId == ResumeUpdateLogResultBean.LogData.TYPE_TEXT_REMARK)
            return TYPE_TEXT_REMARK;
        if (logData.sceneId == ResumeUpdateLogResultBean.LogData.TYPE_IMAGE_REMARK)
            return TYPE_IMAGE_REMARK;
        if (logData.sceneId == ResumeUpdateLogResultBean.LogData.TYPE_VOICE_REMARK)
            return TYPE_VOICE_REMARK;
        return TYPE_NORMAL_TIPS;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    private BaseLogHolder getHolder(View convertView, int position) {
        BaseLogHolder baseLogHolder = null;
        switch (getItemViewType(position)) {
            case TYPE_UPDATE:
                baseLogHolder = new UpdateLogHolder(mContext, convertView, this);
                break;
            case TYPE_NORMAL_TIPS:
                baseLogHolder = new TipsLogHolder(mContext, convertView, this);
                break;
            case TYPE_LABEL:
                baseLogHolder = new LabelLogHolder(mContext, convertView, this);
                break;
            case TYPE_REQUEST_SHARE:
                baseLogHolder = new RequestShareLogHolder(mContext, convertView, this);
                break;
            case TYPE_TEXT_REMARK:
                baseLogHolder = new TextRemarkLogHolder(mContext, convertView, this);
                break;
            case TYPE_VOICE_REMARK:
                baseLogHolder = new VoiceLogHolder(mContext, convertView, this);
                break;
        }
        if (baseLogHolder != null)
            baseLogHolder.setIAdapterEventsListener(mIAdapterEventsListener);
        return baseLogHolder;
    }

    @Override
    public boolean isCurrentUnPackUp(String id) {
        return (mUnPickUpMap.get(id) != null);
    }

    @Override
    public void cacheInUnPackUp(String id) {
        mUnPickUpMap.put(id, id);
    }

    @Override
    public void removeUnPackUp(String id) {
        mUnPickUpMap.remove(id);
    }

    @Override
    public Drawable getDrawablePackUp() {
        return mDrawablePickUp;
    }

    @Override
    public Drawable getDrawableUnPackUp() {
        return mDrawableUnPickUp;
    }

    @Override
    public void onDestroy() {
        if (mDrawablePickUp != null)
            mDrawablePickUp.setCallback(null);
        if (mDrawableUnPickUp != null)
            mDrawableUnPickUp.setCallback(null);
        VoiceLogHolder.stopAudioAnimate();
        VoiceLogHolder.onDestroy();
    }

    public void stopAudioAnimate() {
        VoiceLogHolder.stopAudioAnimate();
    }
}
