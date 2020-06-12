package com.baza.android.bzw.businesscontroller.find.updateengine.adapter.updatelogholder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.bean.resume.ResumeUpdateLogResultBean;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.slib.utils.DateUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/8/29.
 * Title：
 * Note：
 */

public abstract class BaseLogHolder {
    protected Context mContext;
    protected Resources mResources;
    protected int mPosition;
    protected String mResumeName;
    protected ResumeUpdateLogResultBean.LogData mLogData;
    protected View view_contentRoot;
    protected TextView textView_time;
    protected ImageView imageView_timeLine;
    protected ILogAdapterListener mListener;
    protected BaseBZWAdapter.IAdapterEventsListener mAdapterEventsListener;

    public interface ILogAdapterListener {
        boolean isCurrentUnPackUp(String id);

        void cacheInUnPackUp(String id);

        void removeUnPackUp(String id);

        Drawable getDrawablePackUp();

        Drawable getDrawableUnPackUp();
    }

    public BaseLogHolder(Context context, View convertView, ILogAdapterListener listener) {
        this.mContext = context;
        this.mListener = listener;
        this.mResources = context.getResources();
        textView_time = convertView.findViewById(R.id.tv_log_time);
        imageView_timeLine = convertView.findViewById(R.id.iv_time_line);
        FrameLayout frameLayout = convertView.findViewById(R.id.fl_content);
        view_contentRoot = LayoutInflater.from(context).inflate(getItemLayoutId(), null);
        initContentView();
        frameLayout.addView(view_contentRoot, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setIAdapterEventsListener(BaseBZWAdapter.IAdapterEventsListener mAdapterEventsListener) {
        this.mAdapterEventsListener = mAdapterEventsListener;
    }

    public void setData(int position, int allCount, String candidateName, ResumeUpdateLogResultBean.LogData logData) {
        this.mLogData = logData;
        this.mResumeName = candidateName;
        this.mPosition = position;
        textView_time.setText(DateUtil.longMillions2FormatDate(logData.created, DateUtil.SDF_YMD_HMS));
        ViewGroup.LayoutParams lp = imageView_timeLine.getLayoutParams();
        if (position == allCount - 1) {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            imageView_timeLine.setLayoutParams(lp);
            imageView_timeLine.setImageResource(R.drawable.time_line_single);
        } else {
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            imageView_timeLine.setLayoutParams(lp);
            imageView_timeLine.setImageResource(R.drawable.time_line_normal);
        }
        if (mLogData != null)
            setHolderData();
    }

    abstract int getItemLayoutId();

    abstract void initContentView();

    abstract void setHolderData();

    public CharSequence getLogTypeMsg(int type) {
        CharSequence str = null;
        String name = null;
        if (mLogData != null && mLogData.user != null)
            name = (!TextUtils.isEmpty(mLogData.user.nickName) ? mLogData.user.nickName : mLogData.user.trueName);
        if (name == null)
            name = "TA";
        switch (type) {
            case ResumeUpdateLogResultBean.LogData.TYPE_UPDATE_RESUME:
                str = mResources.getString(R.string.log_type_update, name, mResumeName);
                break;
            case ResumeUpdateLogResultBean.LogData.TYPE_EDIT_RESUME:
                str = mResources.getString(R.string.log_type_edit, name, mResumeName);
                break;
            case ResumeUpdateLogResultBean.LogData.TYPE_COLLECTION_RESUME:
                str = mResources.getString(R.string.log_type_collection, name, mResumeName);
                break;
            case ResumeUpdateLogResultBean.LogData.TYPE_UN_COLLECTION_RESUME:
                str = mResources.getString(R.string.log_type_un_collection, name, mResumeName);
                break;
            case ResumeUpdateLogResultBean.LogData.TYPE_MAKE_CALL:
                str = mResources.getString(R.string.log_type_make_call, name);
                break;
            case ResumeUpdateLogResultBean.LogData.TYPE_SEND_EMAIL:
                str = mResources.getString(R.string.log_type_send_email, name);
                break;
            case ResumeUpdateLogResultBean.LogData.TYPE_ADD_LABEL:
                str = mResources.getString(R.string.log_type_label, name);
                break;
            case ResumeUpdateLogResultBean.LogData.TYPE_TEXT_REMARK:
            case ResumeUpdateLogResultBean.LogData.TYPE_IMAGE_REMARK:
            case ResumeUpdateLogResultBean.LogData.TYPE_VOICE_REMARK:
                str = mResources.getString(R.string.log_type_remark, name);
                break;
        }
        return str;
    }

    protected void sendAdapterEvent(int eventId, int position, View v, Object data) {
        if (mAdapterEventsListener != null)
            mAdapterEventsListener.onAdapterEventsArrival(eventId, position, v, data);
    }
}
