package com.baza.android.bzw.businesscontroller.email.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baza.android.bzw.bean.email.ListSyncEmailBean;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.log.LogUtil;
import com.slib.utils.DateUtil;
import com.bznet.android.rcbox.R;

import java.util.List;


/**
 * Created by Vincent.Lei on 2017/4/18.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class EmailSyncedAdapter extends BaseBZWAdapter implements View.OnLongClickListener, View.OnClickListener {
    public static final int EVENT_ID_LONG_CLICK = 1;
    public static final int EVENT_ID_SYNC_NOW = 2;
    public static final int EVENT_ID_RESET_ACCOUNT = 3;
    private Context mContext;
    private Resources mResources;
    private List<ListSyncEmailBean> mEmailList;
    private int mMenuHeight;
    private View view_unfolded;
    private ValueAnimator mValueAnimatorExpand, mValueAnimatorFolder;
    private Drawable mDrawableError;
    private int mColorNormal, mColorDeleted;

    public EmailSyncedAdapter(Context mContext, List<ListSyncEmailBean> mEmailList, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = mContext;
        this.mEmailList = mEmailList;
        this.mResources = mContext.getResources();
        this.mMenuHeight = (int) mResources.getDimension(R.dimen.dp_40);
        this.mDrawableError = mResources.getDrawable(R.drawable.icon_error);
        this.mDrawableError.setBounds(0, 0, mDrawableError.getIntrinsicWidth(), mDrawableError.getIntrinsicHeight());
        this.mColorNormal = mResources.getColor(R.color.text_color_blue_53ABD5);
        this.mColorDeleted = mResources.getColor(R.color.color_red_FF6564);
    }

    @Override
    public int getCount() {
        return mEmailList == null ? 0 : mEmailList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_for_email_synced_list, null);
            convertView.setOnLongClickListener(this);
            convertView.setOnClickListener(this);
            ViewHolder viewHolder = new ViewHolder(convertView);
            viewHolder.textView_operate.setOnClickListener(this);
            convertView.setTag(viewHolder);
        }
        refreshTargetItemView(position, convertView, false);

        return convertView;
    }

    public void refreshTargetItemView(int position, View convertView, boolean mayHideUseAnimation) {
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        ListSyncEmailBean addresseeBean = mEmailList.get(position);
        viewHolder.textView_email.setText(addresseeBean.account);
        viewHolder.textView_email.setCompoundDrawables((addresseeBean.status == ListSyncEmailBean.STATUS_ERROR ? mDrawableError : null), null, null, null);
        viewHolder.textView_count.setText(String.valueOf(addresseeBean.resumeCount));
        viewHolder.textView_lastUpdateTime.setText((addresseeBean.status == ListSyncEmailBean.STATUS_SYNCING ? mResources.getString(R.string.syncing) : (addresseeBean.status == ListSyncEmailBean.STATUS_ERROR ? mResources.getString(R.string.an_error_on_sync) : mResources.getString(R.string.last_sync_time, DateUtil.longMillions2FormatDate(addresseeBean.updateTime, DateUtil.SDF_YMD_HMS)))));
        if (ListSyncEmailBean.STATUS_SYNCING == addresseeBean.status)
            viewHolder.textView_operate.setVisibility(View.GONE);
        else {
            viewHolder.textView_operate.setText((addresseeBean.status == ListSyncEmailBean.STATUS_COMPLETE ? R.string.sync_now : R.string.reset_email_account));
            viewHolder.textView_operate.setTextColor((addresseeBean.status == ListSyncEmailBean.STATUS_COMPLETE ? mColorNormal : mColorDeleted));
            viewHolder.textView_operate.setVisibility(View.VISIBLE);
        }
        viewHolder.textView_email.setTag(position);
        viewHolder.textView_operate.setTag(position);
        if (mayHideUseAnimation)
            expandMenu(viewHolder.view_extraMenu);
        else
            viewHolder.view_extraMenu.setVisibility(View.GONE);

    }

    @Override
    public boolean onLongClick(View v) {
        if (mIAdapterEventsListener != null) {
            ViewHolder viewHolder = (ViewHolder) v.getTag();
            mIAdapterEventsListener.onAdapterEventsArrival(EVENT_ID_LONG_CLICK, (int) viewHolder.textView_email.getTag(), v, null);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_operate:
                if (mIAdapterEventsListener != null) {
                    int position = (int) v.getTag();
                    ListSyncEmailBean addresseeBean = mEmailList.get(position);
                    mIAdapterEventsListener.onAdapterEventsArrival((addresseeBean.status == ListSyncEmailBean.STATUS_COMPLETE ? EVENT_ID_SYNC_NOW : EVENT_ID_RESET_ACCOUNT), position, v, addresseeBean);
                }
                break;
            default:
                ViewHolder viewHolder = (ViewHolder) v.getTag();
                expandMenu(viewHolder.view_extraMenu);
                break;
        }

    }

    private void expandMenu(final View menuView) {
        if (mValueAnimatorExpand != null && mValueAnimatorExpand.isRunning())
            return;
        if (mValueAnimatorFolder != null && mValueAnimatorFolder.isRunning())
            return;
        LogUtil.d("start expandMenu");
        if (menuView.getVisibility() == View.GONE) {
            final View view_previousExpand = view_unfolded;
            this.view_unfolded = menuView;
            //展开菜单
            if (mValueAnimatorExpand == null) {
                mValueAnimatorExpand = ValueAnimator.ofInt(0, mMenuHeight);
                mValueAnimatorExpand.setDuration(200);
            }
            mValueAnimatorExpand.removeAllUpdateListeners();
            mValueAnimatorExpand.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ViewGroup.LayoutParams lp = menuView.getLayoutParams();
                    lp.height = (int) valueAnimator.getAnimatedValue();
                    menuView.setLayoutParams(lp);
                    if (menuView.getVisibility() != View.VISIBLE)
                        menuView.setVisibility(View.VISIBLE);
                    //隐藏已经打开的
                    if (view_previousExpand != null && view_previousExpand != menuView && view_previousExpand.getVisibility() == View.VISIBLE) {
                        ViewGroup.LayoutParams lpPrevious = view_previousExpand.getLayoutParams();
                        lpPrevious.height = mMenuHeight - lp.height;
                        view_previousExpand.setLayoutParams(lpPrevious);
                        if (lpPrevious.height == 0)
                            view_previousExpand.setVisibility(View.GONE);
                    }
                }
            });
            mValueAnimatorExpand.start();
            return;
        }
        //隐藏菜单
        if (mValueAnimatorFolder == null) {
            mValueAnimatorFolder = ValueAnimator.ofInt(mMenuHeight, 0);
            mValueAnimatorFolder.setDuration(200);
        }
        this.view_unfolded = null;
        mValueAnimatorFolder.removeAllUpdateListeners();
        mValueAnimatorFolder.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams lp = menuView.getLayoutParams();
                lp.height = (int) valueAnimator.getAnimatedValue();
                menuView.setLayoutParams(lp);
                if (lp.height == 0)
                    menuView.setVisibility(View.GONE);
            }
        });
        mValueAnimatorFolder.start();
    }

    public void destroy() {
        if (mValueAnimatorExpand != null && mValueAnimatorExpand.isRunning())
            mValueAnimatorExpand.cancel();
        mValueAnimatorExpand = null;
        if (mValueAnimatorFolder != null && mValueAnimatorFolder.isRunning())
            mValueAnimatorFolder.cancel();
        mValueAnimatorFolder = null;
    }

    private static class ViewHolder {
        TextView textView_email, textView_count;
        View view_extraMenu;
        TextView textView_lastUpdateTime, textView_operate;

        public ViewHolder(View convertView) {
            this.textView_email = convertView.findViewById(R.id.tv_email);
            this.textView_count = convertView.findViewById(R.id.tv_count);
            this.view_extraMenu = convertView.findViewById(R.id.ll_extra_menu);
            this.textView_lastUpdateTime = convertView.findViewById(R.id.tv_lastSyncTime);
            this.textView_operate = convertView.findViewById(R.id.tv_operate);
        }
    }
}
