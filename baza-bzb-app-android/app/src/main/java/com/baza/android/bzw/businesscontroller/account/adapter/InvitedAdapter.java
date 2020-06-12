package com.baza.android.bzw.businesscontroller.account.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.user.InviteInfoResultBean;
import com.slib.utils.DateUtil;
import com.bznet.android.rcbox.R;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/12/5.
 * Title：
 * Note：
 */
public class InvitedAdapter extends BaseBZWAdapter {
    private Context mContext;
    private List<InviteInfoResultBean.InviteInfoBean> mDataList;

    public InvitedAdapter(Context context, List<InviteInfoResultBean.InviteInfoBean> dataList, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        return (mDataList == null ? 0 : mDataList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.account_invited_adapter_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        InviteInfoResultBean.InviteInfoBean inviteInfoBean = mDataList.get(position);
        viewHolder.textView_name.setText(inviteInfoBean.inviteeUserName);
        viewHolder.textView_time.setText(DateUtil.longMillions2FormatDate(inviteInfoBean.created, DateUtil.SDF_YMD_HM));
        return convertView;
    }

    private static class ViewHolder {
        TextView textView_name;
        TextView textView_time;

        public ViewHolder(View convertView) {
            textView_name = convertView.findViewById(R.id.tv_invited_name);
            textView_time = convertView.findViewById(R.id.tv_invited_time);
        }
    }
}
