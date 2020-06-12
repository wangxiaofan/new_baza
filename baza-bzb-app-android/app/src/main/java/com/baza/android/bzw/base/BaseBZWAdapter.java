package com.baza.android.bzw.base;

import android.view.View;
import android.widget.BaseAdapter;

/**
 * Created by LW on 2016/5/23.
 * Title : 列表适配器父类（如果可能所有列表适配器继承该类）
 * Note :
 */
public abstract class BaseBZWAdapter extends BaseAdapter {

    protected IAdapterEventsListener mIAdapterEventsListener;

    public BaseBZWAdapter(IAdapterEventsListener adapterEventsListener) {
        this.mIAdapterEventsListener = adapterEventsListener;
    }

    public void setAdapterEventsListener(IAdapterEventsListener mIAdapterEventsListener) {
        this.mIAdapterEventsListener = mIAdapterEventsListener;
    }

    public interface IAdapterEventsListener {
        void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void sendAdapterEventToHost(int adapterEventId, int position, View v, Object input) {
        if (mIAdapterEventsListener != null)
            mIAdapterEventsListener.onAdapterEventsArrival(adapterEventId, position, v, input);
    }

    public void onDestroy() {
    }

}
